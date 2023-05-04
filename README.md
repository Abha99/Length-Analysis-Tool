



# Length Analysis Plugin

The Length Analysis plugin is a tool designed for the analysis of the paths traversed by cells in cellular suspension. It provides information such as the distance between end points (displacement), length, and curvature of the path in pixels. This information can be used to determine the motility of cells.

## How it Works

The plugin works by taking an image of cells in suspension and analyzing the paths that cells traverse over a period of time. It uses image processing techniques to track cells in the image and calculate the distance, length, and curvature of the paths they traverse.

## Installation

To use the Length Analysis plugin, follow these steps:

1. Install Fiji (ImageJ) from the [Fiji website](https://fiji.sc/)
2. Download the Length Analysis plugin [here](https://github.com/Abha99/Length-Analysis-Plugin/Jars)
3. Download the dependencies ([Autothreshold](https://maven.scijava.org/service/local/artifact/maven/redirect?r=releases&g=sc.fiji&a=Auto_Threshold&v=RELEASE&e=jar), [MorpholibJ](https://github.com/ijpb/MorphoLibJ/releases) and [Skeletonize3D](https://imagej.net/plugins/skeletonize3d) Plugins)
3. Copy the `.jar` files to the `plugins` folder in your Fiji installation directory.
4. Restart Fiji
5. The plugin can now be accessed from the Plugins menu.

## Usage

To use the Length Analysis plugin, follow these steps:

1. Open an image of cells in suspension in Fiji.
2. Click on the Length Analysis plugin in the Plugins menu
3. The plugin will calculate the distance, length, and curvature of the paths traversed by the selected cells.
4. The results can be exported as a CSV file for further analysis.

## Contributing

If you'd like to contribute to the Length Analysis plugin, follow these steps:

1. Fork this repository.
2. Create a branch: `git checkout -b your-feature`
3. Make your changes and commit them: `git commit -m 'your-message'`
4. Push to the original branch: `git push origin my-changes`
5. Create a pull request.

