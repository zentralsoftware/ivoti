package org.c1info;

import georegression.metric.UtilAngle;
import georegression.struct.point.Point2D_I32;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import boofcv.alg.feature.shapes.ShapeFittingOps;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.ImageGridPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.ConnectRule;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.image.ImageUInt8;

import com.c1info.io.Find.Finder;

public class ShapeFitContourTest {
	public static void main(String[] args) throws IOException
	{
		Path startingDir = Paths.get("src/test/resources/visualBinary");
		String pattern = "*04_6*.jpg";
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);
		List<Path> foundFiles = finder.getFoundFiles();
		int i=0;
		for (Path foundFile:foundFiles)
		{
			testContour(foundFile.toFile().getAbsolutePath());			
			i++;
			if (i==10)
			{
				break;
			}
		}
	}

	private static void testContour(String absolutePath) {
		BufferedImage image = UtilImageIO.loadImage(absolutePath);
		ImageUInt8 input = ConvertBufferedImage.convertFromSingle(image, null, ImageUInt8.class);
		ImageUInt8 binary = new ImageUInt8(input.width,input.height);
		ImageUInt8 filtered = new ImageUInt8(input.width,input.height);

		// the mean pixel value is often a reasonable threshold when creating a binary image
		double mean = ImageStatistics.mean(input);
		// create a binary image by thresholding
		GThresholdImageOps.threshold(input, binary, mean, true);
		// reduce noise with some filtering
		BinaryImageOps.erode8(binary, 1, filtered);
		BinaryImageOps.dilate8(filtered, 1, binary);
		
		// Find the contour around the shapes
		List<Contour> contours = BinaryImageOps.contour(binary, ConnectRule.EIGHT,null);
		
		BufferedImage visualBinary = ConvertBufferedImage.convertTo(binary, null);
		ImageGridPanel gui = new ImageGridPanel(1,2);
		gui.setImage(0,0,image);
		gui.setImage(0,1,visualBinary);		
		gui.setPreferredSize(new Dimension(2*input.getWidth(), 2*input.getHeight()));
		ShowImages.showWindow(gui,absolutePath);
		
		Graphics2D g1 = gui.getImage(0,1).createGraphics();
		g1.setColor(Color.WHITE);
		g1.setStroke(new BasicStroke(3f));		
		
		int polyAngleTol = 5;
		int polyPixelTol = 2;
		double angleTol = UtilAngle.degreeToRadian(polyAngleTol);
		for( Contour c : contours ) {
			List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external,true,polyPixelTol,angleTol,100);

			g1.setColor(Color.RED);
			VisualizeShapes.drawPolygon(vertexes,true,g1);

			g1.setColor(Color.BLUE);
			for( List<Point2D_I32> internal : c.internal ) {
				vertexes = ShapeFittingOps.fitPolygon(internal,true,polyPixelTol,angleTol,100);
				VisualizeShapes.drawPolygon(vertexes,true,g1);
			}
		}	
		gui.repaint();	
	}
	
	
}
