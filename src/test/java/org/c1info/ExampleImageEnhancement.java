package org.c1info;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageUInt8;

public class ExampleImageEnhancement {
	public static void histogram() {
		BufferedImage buffered = UtilImageIO.loadImage("src/test/resources/scan/000000400104.jpg");
		ImageUInt8 gray = ConvertBufferedImage.convertFrom(buffered,(ImageUInt8)null);
		ImageUInt8 adjusted = new ImageUInt8(gray.width, gray.height);
 
		int histogram[] = new int[256];
		int transform[] = new int[256];
 
		ListDisplayPanel panel = new ListDisplayPanel();
 
		ImageStatistics.histogram(gray,histogram);
		EnhanceImageOps.equalize(histogram, transform);
		EnhanceImageOps.applyTransform(gray, transform, adjusted);
		panel.addImage(ConvertBufferedImage.convertTo(adjusted,null),"Global");
 
		EnhanceImageOps.equalizeLocal(gray, 50, adjusted, histogram, transform);
		panel.addImage(ConvertBufferedImage.convertTo(adjusted,null),"Local");
 
		panel.addImage(ConvertBufferedImage.convertTo(gray,null),"Gray");
		
		panel.addImage(buffered,"Original");		
 
		panel.setPreferredSize(new Dimension(gray.width,gray.height));
		ShowImages.showWindow(panel,"Histogram");
	}
 
	/**
	 * When an image is sharpened the intensity of edges are made more extreme while flat regions remain unchanged.
	 */
	public static void sharpen() {
		BufferedImage buffered = UtilImageIO.loadImage("src/test/resources/scan/000000400204.jpg");
		ImageUInt8 gray = ConvertBufferedImage.convertFrom(buffered,(ImageUInt8)null);
		ImageUInt8 adjusted = new ImageUInt8(gray.width, gray.height);
 
 
		ListDisplayPanel panel = new ListDisplayPanel();
 
		EnhanceImageOps.sharpen4(gray, adjusted);
		panel.addImage(ConvertBufferedImage.convertTo(adjusted,null),"Sharpen-4");
 
		EnhanceImageOps.sharpen8(gray, adjusted);
		panel.addImage(ConvertBufferedImage.convertTo(adjusted,null),"Sharpen-8");
 
		panel.addImage(ConvertBufferedImage.convertTo(gray,null),"Original");
 
		panel.setPreferredSize(new Dimension(gray.width,gray.height));
		ShowImages.showWindow(panel,"Sharpen");
	}
 
	public static void main( String args[] )
	{
		histogram();
		//sharpen();
	}
 
}
