import os
import ffmpeg


def convert_wmv_to_mp4(directory):
    for filename in os.listdir(directory):
        if filename.endswith('.wmv'):
            base = os.path.splitext(filename)[0]
            input_file = os.path.join(directory, filename)
            output_file = os.path.join(directory, f"{base}.mp4")

            try:
                ffmpeg.input(input_file).output(output_file).run()
                print(f"Successfully converted {input_file} to {output_file}")

                os.remove(input_file)
                print(f"Removed original file {input_file}")
            except ffmpeg.Error as e:
                print(f"Error converting {input_file}: {e}")
            except Exception as e:
                print(f"Unexpected error: {e}")


if __name__ == "__main__":
    directory = "./Visualization_Of_Questions"
    convert_wmv_to_mp4(directory)
