package org.c1info;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.c1info.Util;

import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;

public class ExampleCannyEdge {

	public static void main(String[] args) throws IOException {
		Path file = Paths.get("src/test/resources/visualBinary/000188700104_600x859.jpg");
		Path destination = Paths.get("src/test/resources/cannyEdge/000188700104_600x859.jpg");
		createCannyEdgeAndSave(file, destination);
	}

	private static void createCannyEdgeAndSave(Path file, Path destination) throws IOException {
		BufferedImage image = UtilImageIO.loadImage(file.toFile().getAbsolutePath());
		 
		ImageUInt8 gray = ConvertBufferedImage.convertFrom(image,(ImageUInt8)null);
		ImageUInt8 edgeImage = new ImageUInt8(gray.width,gray.height);
 
		// Create a canny edge detector which will dynamically compute the threshold based on maximum edge intensity
		// It has also been configured to save the trace as a graph.  This is the graph created while performing
		// hysteresis thresholding.
		CannyEdge<ImageUInt8,ImageSInt16> canny = FactoryEdgeDetectors.canny(2,true, true, ImageUInt8.class, ImageSInt16.class);
 
		// The edge image is actually an optional parameter.  If you don't need it just pass in null
		canny.process(gray,0.1f,0.3f,edgeImage);
  
		// display the results
		BufferedImage visualBinary = VisualizeBinaryData.renderBinary(edgeImage, null); 
		ShowImages.showWindow(visualBinary, "canny");
		Util.saveImage(visualBinary, "jpg", destination.toFile().getAbsolutePath());
	}

}
