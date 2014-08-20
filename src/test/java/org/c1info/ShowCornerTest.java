package org.c1info;

import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I16;
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
import java.util.ArrayList;
import java.util.List;

import boofcv.abst.feature.detect.extract.ConfigExtract;
import boofcv.abst.feature.detect.extract.NonMaxSuppression;
import boofcv.abst.feature.detect.intensity.GeneralFeatureIntensity;
import boofcv.alg.feature.detect.interest.EasyGeneralFeatureDetector;
import boofcv.alg.feature.detect.interest.GeneralFeatureDetector;
import boofcv.alg.feature.shapes.ShapeFittingOps;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.LinearContourLabelChang2004;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.filter.derivative.GImageDerivativeOps;
import boofcv.alg.misc.GPixelMath;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.extract.FactoryFeatureExtractor;
import boofcv.factory.feature.detect.intensity.FactoryIntensityPoint;
import boofcv.gui.image.ImageGridPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.ConnectRule;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.QueueCorner;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageSInt32;
import boofcv.struct.image.ImageUInt8;

import com.c1info.io.Find.Finder;

public class ShowCornerTest {

	public static void main(String[] args) throws IOException
	{
		Path startingDir = Paths.get("src/test/resources/visualBinary");
		String pattern = "*04_6*.jpg";
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);
		List<Path> foundFiles = finder.getFoundFiles();
		for (Path foundFile:foundFiles)
		{
			showCornerWithFastAlg(foundFile.toFile().getAbsolutePath());			
		}
	}

	public static void showCornerWithShiTomasi(String file) {
		BufferedImage origImage = UtilImageIO.loadImage(file);
		BufferedImage image = UtilImageIO.loadImage(file);
		ImageUInt8 input = ConvertBufferedImage.convertFromSingle(image, null, ImageUInt8.class);
		ConfigExtract configCorner = new ConfigExtract(20,400,3,true,false,true);
		NonMaxSuppression nonmax = FactoryFeatureExtractor.nonmax(configCorner);
		GeneralFeatureIntensity<ImageUInt8, ImageSInt16> intensity = FactoryIntensityPoint.shiTomasi(2,false,ImageSInt16.class);
		
		GeneralFeatureDetector<ImageUInt8,ImageSInt16> general = new GeneralFeatureDetector<ImageUInt8, ImageSInt16>(intensity,nonmax);		
		EasyGeneralFeatureDetector<ImageUInt8, ImageSInt16> detector = new EasyGeneralFeatureDetector<ImageUInt8,ImageSInt16>(general,ImageUInt8.class,ImageSInt16.class);
		detector.detect(input, null);
		QueueCorner maxs = detector.getMaximums();		
		ImageGridPanel gui = new ImageGridPanel(1,2);
		gui.setImage(0,0,origImage);
		gui.setImage(0,1,image);
		gui.setPreferredSize(new Dimension(2*input.getWidth(), 2*input.getHeight()));
		ShowImages.showWindow(gui,file);
		
		Graphics2D g2 = gui.getImage(0,1).createGraphics();
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(3f));
		System.err.println(maxs.size());
		for (int i=0;i<maxs.size();i++)
		{
			Point2D_I16 p = maxs.get(i);
			g2.drawOval((int)p.x, (int)p.y, 6, 6);
		}
		gui.repaint();
	}
	
	public static void showCornerWithFastAlg(String file) {
		BufferedImage origImage = UtilImageIO.loadImage(file);
		BufferedImage image = UtilImageIO.loadImage(file);
		ImageUInt8 input = ConvertBufferedImage.convertFromSingle(image, null, ImageUInt8.class);
		
		ConfigExtract configCorner = new ConfigExtract(20,400,3,true,false,true);
		NonMaxSuppression nonmax = FactoryFeatureExtractor.nonmax(configCorner);
		GeneralFeatureIntensity<ImageUInt8, ImageSInt16> intensity = FactoryIntensityPoint.shiTomasi(2,false,ImageSInt16.class);
		
		GeneralFeatureDetector<ImageUInt8,ImageSInt16> general = new GeneralFeatureDetector<ImageUInt8, ImageSInt16>(intensity,nonmax);		
		EasyGeneralFeatureDetector<ImageUInt8, ImageSInt16> detector = new EasyGeneralFeatureDetector<ImageUInt8,ImageSInt16>(general,ImageUInt8.class,ImageSInt16.class);
		detector.detect(input, null);
		QueueCorner maxs = detector.getMaximums();		
		ImageGridPanel gui = new ImageGridPanel(1,2);
		gui.setImage(0,0,origImage);
		gui.setImage(0,1,image);
		gui.setPreferredSize(new Dimension(2*input.getWidth(), 2*input.getHeight()));
		ShowImages.showWindow(gui,file);
		
		Graphics2D g2 = gui.getImage(0,1).createGraphics();
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(3f));
		System.err.println(maxs.size());
		for (int i=0;i<maxs.size();i++)
		{
			Point2D_I16 p = maxs.get(i);
			g2.drawOval((int)p.x, (int)p.y, 6, 6);
		}
		gui.repaint();
	}	
	
	public static void showCornerWithContours(String file)
	{
		LinearContourLabelChang2004 findContours = new LinearContourLabelChang2004(ConnectRule.EIGHT);
		BufferedImage origImage = UtilImageIO.loadImage(file);
		BufferedImage image = UtilImageIO.loadImage(file);
		ImageUInt8 input = ConvertBufferedImage.convertFromSingle(image, null, ImageUInt8.class);
		ImageSInt16 edge = new ImageSInt16(input.width,input.height);
		ImageUInt8 binary = new ImageUInt8(input.width,input.height);
		ImageUInt8 filtered = new ImageUInt8(input.width,input.height);
		ImageSInt32 contourOutput = new ImageSInt32(input.width,input.height);

		GImageDerivativeOps.laplace(input,edge);
		GPixelMath.abs(edge,edge);
		
		// use the mean value to threshold the image
		int mean = (int)ImageStatistics.mean(edge)*2;
		
		// create a binary image by thresholding
		ThresholdImageOps.threshold(edge, binary, mean, false);
		
		// reduce noise with some filtering
		BinaryImageOps.removePointNoise(binary, filtered);		
		
		findContours.process(filtered,contourOutput);
		List<Contour> contours = findContours.getContours().toList();
		
		// prepare gui
		ImageGridPanel gui = new ImageGridPanel(1,2);
		gui.setImage(0,0,origImage);
		gui.setImage(0,1,ConvertBufferedImage.convertTo(filtered, null));
		gui.setPreferredSize(new Dimension(2*input.getWidth(), 2*input.getHeight()));
		ShowImages.showWindow(gui,file);		
		Graphics2D g2 = gui.getImage(0,1).createGraphics();
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(2f));
		// draw contour		
		for( Contour contour : contours ) {
			List<Point2D_I32> points = contour.external;
			if( points.size() < 2 )
				continue;
			List<PointIndex_I32> poly = ShapeFittingOps.fitPolygon(points, true, 4, 0.3f, 0);
			for( int i = 1; i < poly.size(); i++ ) {
				PointIndex_I32 a = poly.get(i-1);
				PointIndex_I32 b = poly.get(i);
				g2.drawLine(a.x,a.y,b.x,b.y);
			}
			PointIndex_I32 a = poly.get(poly.size()-1);
			PointIndex_I32 b = poly.get(0);
			g2.drawLine(a.x,a.y,b.x,b.y);
		}
		gui.repaint();
	}
	
	public static List<Point2D_F64> createFrom(QueueCorner maxs)
	{
		List<Point2D_F64> output = new ArrayList<Point2D_F64>();
		for (int i=0;i<maxs.size;i++)
		{
			Point2D_I16 p = maxs.get(i);
			output.add(new Point2D_F64(p.x,p.y));
		}
		return output;
	}

}
