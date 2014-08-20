package org.c1info;

import georegression.struct.line.LineParametric2D_F32;
import georegression.struct.line.LineSegment2D_F32;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

import boofcv.abst.feature.detect.line.DetectLineHoughFoot;
import boofcv.abst.feature.detect.line.DetectLineSegmentsGridRansac;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.line.ConfigHoughFoot;
import boofcv.factory.feature.detect.line.FactoryDetectLineAlgs;
import boofcv.gui.feature.ImageLinePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSingleBand;

public class ExampleLineDetection {

	// adjusts edge threshold for identifying pixels belonging to a line
	private static final float edgeThreshold = 25;
	// adjust the maximum number of found lines in the image
	private static final int maxLines = 10;

	/**
	 * Detects lines inside the image using different types of Hough detectors
	 *
	 * @param image Input image.
	 * @param imageType Type of image processed by line detector.
	 * @param derivType Type of image derivative.
	 */
	public static<T extends ImageSingleBand, D extends ImageSingleBand>
			void detectLines( BufferedImage image , 
							  Class<T> imageType ,
							  Class<D> derivType )
	{
		// convert the line into a single band image
		T input = ConvertBufferedImage.convertFromSingle(image, null, imageType );

		// Comment/uncomment to try a different type of line detector
		//		DetectLineHoughPolar<T,D> detector = FactoryDetectLineAlgs.houghPolar(
		//				new ConfigHoughPolar(3, 30, 2, Math.PI / 180,edgeThreshold, maxLines), imageType, derivType);
				DetectLineHoughFoot<T,D> detector = FactoryDetectLineAlgs.houghFoot(
						new ConfigHoughFoot(3, 8, 5, edgeThreshold,maxLines), imageType, derivType);
		//		DetectLineHoughFootSubimage<T,D> detector = FactoryDetectLineAlgs.houghFootSub(
		//		new ConfigHoughFootSubimage(3, 8, 5, edgeThreshold,maxLines, 2, 2), imageType, derivType);

		List<LineParametric2D_F32> found = detector.detect(input);
		
		// display the results
		ImageLinePanel gui = new ImageLinePanel();
		gui.setBackground(image);
		gui.setLines(found);
		gui.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));

		ShowImages.showWindow(gui,"Found Lines");
	}
	
	public static void main( String args[] ) {
		BufferedImage input = UtilImageIO.loadImage("visualBinary.jpg");

		//detectLines(input,ImageUInt8.class,ImageSInt16.class);

		// line segment detection is still under development and only works for F32 images right now
		detectLineSegments(input, ImageFloat32.class, ImageFloat32.class);
	}	

	/**
	 * Detects segments inside the image
	 *
	 * @param image Input image.
	 * @param imageType Type of image processed by line detector.
	 * @param derivType Type of image derivative.
	 */
	public static<T extends ImageSingleBand, D extends ImageSingleBand>
	void detectLineSegments( BufferedImage image ,
							 Class<T> imageType ,
							 Class<D> derivType )
	{
		// convert the line into a single band image
		T input = ConvertBufferedImage.convertFromSingle(image, null, imageType );

		// Comment/uncomment to try a different type of line detector
		DetectLineSegmentsGridRansac<T,D> detector = FactoryDetectLineAlgs.lineRansac(40, 30, 2.36, true, imageType, derivType);

		List<LineSegment2D_F32> found = detector.detect(input);
		
		// display the results
		ImageLinePanel gui = new ImageLinePanel();
		gui.setBackground(image);
		gui.setLineSegments(found);
		gui.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));

		ShowImages.showWindow(gui,"Found Line Segments");
	}

	
}
