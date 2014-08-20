package org.c1info;

import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I16;

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
import boofcv.alg.feature.detect.quadblob.FindBoundingQuadrilateral;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.feature.detect.extract.FactoryFeatureExtractor;
import boofcv.factory.feature.detect.intensity.FactoryIntensityPoint;
import boofcv.gui.image.ImageGridPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.QueueCorner;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;

import com.c1info.io.Find.Finder;

public class FindBoundingQuadrilateralTest {

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
			testBounding(foundFile.toFile().getAbsolutePath());			
			i++;
			if (i==10)
			{
				break;
			}
		}
	}	
	
	public static void testBounding(String file) {
		List<Point2D_F64> points = getPointsWithFastAlg(file);		
		List<Point2D_F64> corners = FindBoundingQuadrilateral.findCorners(points);
		BufferedImage origImage = UtilImageIO.loadImage(file);
		BufferedImage image1 = UtilImageIO.loadImage(file);
		BufferedImage image2 = UtilImageIO.loadImage(file);
		ImageUInt8 input = ConvertBufferedImage.convertFromSingle(image1, null, ImageUInt8.class);
		
		ImageGridPanel gui = new ImageGridPanel(1,3);
		gui.setImage(0,0,origImage);
		gui.setImage(0,1,image1);
		gui.setImage(0,2,image2);
		gui.setPreferredSize(new Dimension(3*input.getWidth(), 3*input.getHeight()));
		ShowImages.showWindow(gui,file);
		
		Graphics2D g1 = gui.getImage(0,1).createGraphics();
		g1.setColor(Color.WHITE);
		g1.setStroke(new BasicStroke(3f));
		System.err.println(points.size());
		for (int i=0;i<points.size();i++)
		{
			Point2D_F64 p = points.get(i);
			g1.drawOval((int)p.x, (int)p.y, 6, 6);
		}		
		
		Graphics2D g2 = gui.getImage(0,2).createGraphics();
		g2.setColor(Color.WHITE);
		g2.setStroke(new BasicStroke(3f));
		System.err.println(corners.size());
		for (int i=0;i<corners.size();i++)
		{
			Point2D_F64 p = corners.get(i);
			g2.drawOval((int)p.x, (int)p.y, 6, 6);
		}
		gui.repaint();		
	}
	
	public static List<Point2D_F64> getPointsWithFastAlg(String file) {
		BufferedImage image = UtilImageIO.loadImage(file);
		ImageUInt8 input = ConvertBufferedImage.convertFromSingle(image, null, ImageUInt8.class);
		
		ConfigExtract configCorner = new ConfigExtract(20,400,3,true,false,true);
		NonMaxSuppression nonmax = FactoryFeatureExtractor.nonmax(configCorner);
		GeneralFeatureIntensity<ImageUInt8, ImageSInt16> intensity = FactoryIntensityPoint.shiTomasi(2,false,ImageSInt16.class);
		
		GeneralFeatureDetector<ImageUInt8,ImageSInt16> general = new GeneralFeatureDetector<ImageUInt8, ImageSInt16>(intensity,nonmax);		
		EasyGeneralFeatureDetector<ImageUInt8, ImageSInt16> detector = new EasyGeneralFeatureDetector<ImageUInt8,ImageSInt16>(general,ImageUInt8.class,ImageSInt16.class);
		detector.detect(input, null);
		QueueCorner maxs = detector.getMaximums();		
		return createFrom(maxs);
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
