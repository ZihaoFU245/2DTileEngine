package Assets.Entities;

import Engine.Graphics.Renderer;
import Engine.Graphics.tileengine.TETile;
import Engine.Input.InputAction;
import Engine.Scene.Entity;
import Engine.Scene.Scene;
import Engine.Utils.Vector2i;
import core.CustomConfig;
import java.awt.Color;
import java.util.*;
import java.util.function.Supplier;

public class Ghost extends Entity {

    private Supplier<Vector2i> targetSupplier = null;
    private Set<Vector2i> walkable = Collections.emptySet();

    private double stepAccumulator = 0.0;
    private static final long STEP_INTERVAL_MS = 250;
    private static final double STEP_INTERVAL_SEC = STEP_INTERVAL_MS / 1000.0;

    private double pathRecalcAccumulator = 0.0;
    private static final double PATH_RECALC_INTERVAL_SEC = 0.20;

    private ArrayDeque<Vector2i> currentPath = new ArrayDeque<>();
    private boolean showPath = false;

    private static final Vector2i[] DIRS = new Vector2i[]{
            new Vector2i(1, 0), new Vector2i(-1, 0),
            new Vector2i(0, 1), new Vector2i(0, -1)
    };

    private final TETile ghostTile = ((CustomConfig) getScene().getConfig()).theme.ghost();

    //About Switcher: Global path display switch, toggled by Player; affects all Ghosts
    private static volatile boolean GLOBAL_SHOW_PATH = false;

    public static void setGlobalShowPath(boolean on) {
        GLOBAL_SHOW_PATH = on;
    }

    public static void toggleGlobalShowPath() {
        GLOBAL_SHOW_PATH = !GLOBAL_SHOW_PATH;
    }

    public static boolean isGlobalShowPath() {
        return GLOBAL_SHOW_PATH;
    }


    /**
     * Constructs a new Entity.
     *
     * @param scene    The scene this entity belongs to.
     * @param position The initial position of the entity.
     */
    public Ghost(Scene scene, Vector2i position) {
        super(scene, position);
        setCollider(new Vector2i(1, 1), false);
    }

    @Override
    public void update(double dt, InputAction ia) {
        // 1) 控制寻路重算频率，避免每帧 BFS
        pathRecalcAccumulator += dt;

        // 2) 目标（玩家）坐标
        Vector2i target = (targetSupplier != null) ? targetSupplier.get() : null;

        // 3) 到了冷却间隔就重算从当前位置到玩家的路径
        if (target != null
                && walkable != null && !walkable.isEmpty()
                && pathRecalcAccumulator >= PATH_RECALC_INTERVAL_SEC) {

            if (!walkable.contains(target) || !walkable.contains(position)) {
                currentPath.clear();
            } else {
                ArrayDeque<Vector2i> newPath = aStarShortestPath(position, target, walkable);
                if (newPath != null) {
                    currentPath = newPath;
                    // 若首元素就是当前位置，弹掉，使队头变成“下一步”
                    if (!currentPath.isEmpty() && currentPath.peekFirst().equals(position)) {
                        currentPath.pollFirst();
                    }
                }
            }
            pathRecalcAccumulator = 0.0;
        }

        // 4) 用 dt 节流（静止时不会积压，因为只有到阈值才走一步）
        stepAccumulator = Math.min(stepAccumulator + dt, STEP_INTERVAL_SEC);

        boolean attemptedMove = false;
        Vector2i prev = position;

        if (stepAccumulator >= STEP_INTERVAL_SEC) {
            Vector2i nextStep = null;

            if (target != null && !currentPath.isEmpty()) {
                nextStep = currentPath.pollFirst();

                // 防御：如果不是相邻格，放弃这条路径
                if (nextStep != null &&
                        Math.abs(nextStep.x() - position.x()) + Math.abs(nextStep.y() - position.y()) != 1) {
                    nextStep = null;
                    currentPath.clear();
                }
            }

            // 路径空了就用一次性贪心走一步，避免卡住
            if (nextStep == null && target != null) {
                nextStep = greedyStepToward(position, target, walkable);
            }

            if (nextStep != null) {
                setPosition(nextStep);   // 碰撞/回退由引擎处理
                attemptedMove = true;
            }

            stepAccumulator = 0.0;       // 每帧最多一步
            getScene().requestRender();
        }
        super.update(dt, ia);

        if (attemptedMove && position.equals(prev)) {
            currentPath.clear();
        }
    }

    @Override
    public void render(Renderer r) {
        Vector2i screen = getScene().getCamera().worldToScreenPoint(position);

        // 1) draw path first, ignore player and ghost objs, prevent covering
        if ((showPath || GLOBAL_SHOW_PATH) && !currentPath.isEmpty()) {
            TETile dot = new TETile('.', Color.RED, Color.BLACK, "path", 110);

            // get the pos of player
            Vector2i target = (targetSupplier != null) ? targetSupplier.get() : null;

            for (Vector2i p : currentPath) {
                // Skip the player's grid: do not cover the Player
                if (p.equals(target)) {
                    continue;
                }
                // Skip the ghost's grid: don't cover the ghost itself
                if (p.equals(position)) {
                    continue;
                }

                Vector2i ps = getScene().getCamera().worldToScreenPoint(p);
                r.drawTile(ps.x(), ps.y(), dot);
            }
        }

        // 2) Draw the ghost body, making sure the ghost is on the path
        r.drawTile(screen.x(), screen.y(), ghostTile);
    }


    public void setWalkable(Set<Vector2i> floorSet) {
        this.walkable = (floorSet != null) ? floorSet : Collections.emptySet();
    }

    public void setTargetSupplier(Supplier<Vector2i> supplier) {
        this.targetSupplier = supplier;
    }

    public void setShowPath(boolean show) {
        this.showPath = show;
    }

    // --- A* Implementation ---
    private ArrayDeque<Vector2i> aStarShortestPath(Vector2i start, Vector2i goal, Set<Vector2i> walkableSet) {
        if (start == null || goal == null) return null;
        if (start.equals(goal)) return new ArrayDeque<>();

        record Node(Vector2i pos, int fScore) {}
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(Node::fScore));
        Map<Vector2i, Integer> g = new HashMap<>();
        Map<Vector2i, Vector2i> parent = new HashMap<>();
        Set<Vector2i> closed = new HashSet<>();

        g.put(start, 0);
        open.add(new Node(start, heuristic(start, goal)));
        parent.put(start, null);

        while (!open.isEmpty()) {
            Vector2i cur = open.poll().pos();
            if (cur.equals(goal)) {
                return reconstruct(parent, goal);
            }
            if (!closed.add(cur)) continue;
            int curG = g.get(cur);

            for (Vector2i d : DIRS) {
                Vector2i nxt = cur.add(d);
                if (!walkableSet.contains(nxt) || closed.contains(nxt)) continue;
                int tentativeG = curG + 1;
                int prevG = g.getOrDefault(nxt, Integer.MAX_VALUE);
                if (tentativeG < prevG) {
                    g.put(nxt, tentativeG);
                    parent.put(nxt, cur);
                    int f = tentativeG + heuristic(nxt, goal);
                    open.add(new Node(nxt, f));
                }
            }
        }
        return null; // no path
    }

    private int heuristic(Vector2i a, Vector2i b) {
        return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y()); // Manhattan
    }

    private ArrayDeque<Vector2i> reconstruct(Map<Vector2i, Vector2i> parent, Vector2i goal) {
        ArrayDeque<Vector2i> path = new ArrayDeque<>();
        Vector2i cur = goal;
        while (cur != null) { path.addFirst(cur); cur = parent.get(cur); }
        return path;
    }

    private Vector2i greedyStepToward(Vector2i from, Vector2i target, Set<Vector2i> walkableSet) {
        int best = Math.abs(from.x() - target.x()) + Math.abs(from.y() - target.y());
        Vector2i bestNext = null;
        for (Vector2i d : DIRS) {
            Vector2i cand = from.add(d);
            if (!walkableSet.contains(cand)) continue;
            int dist = Math.abs(cand.x() - target.x()) + Math.abs(cand.y() - target.y());
            if (dist < best) { best = dist; bestNext = cand; }
        }
        return bestNext;
    }
}
