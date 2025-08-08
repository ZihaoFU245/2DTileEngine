from PIL import Image


def resize_to_16x16(input_path, output_path):
    img = Image.open(input_path)
    img = img.resize((16, 16), Image.Resampling.LANCZOS)
    img.save(output_path)


if __name__ == "__main__":
    in_p = "ghostRaw.png"
    o_p = "ghost.png"
    resize_to_16x16(in_p, o_p)
