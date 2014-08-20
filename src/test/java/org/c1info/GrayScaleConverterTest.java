package org.c1info;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import boofcv.alg.filter.blur.BlurImageOps;
import boofcv.alg.misc.GPixelMath;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

public class GrayScaleConverterTest {

	/**
	 * There is no real perfect way that everyone agrees on for converting color images into gray scale
	 * images.  Two examples of how to convert a MultiSpectral image into a gray scale image are shown 
	 * in this example.
	 * @throws IOException 
	 */
	public static void convertToGray( BufferedImage input ) throws IOException {
		// convert the BufferedImage into a MultiSpectral
		MultiSpectral<ImageUInt8> image = ConvertBufferedImage.convertFromMulti(input,null,true,ImageUInt8.class);

		ImageUInt8 gray = new ImageUInt8( image.width,image.height);

		// creates a gray scale image by averaging intensity value across pixels
		GPixelMath.averageBand(image, gray);
		BufferedImage outputAve = ConvertBufferedImage.convertTo(gray,null);

		// create an output image just from the first band
		BufferedImage outputBand0 = ConvertBufferedImage.convertTo(image.getBand(0),null);

		ShowImages.showWindow(outputAve,"Average");
		ShowImages.showWindow(outputBand0,"Band 0");
		
		saveImage(outputAve, "jpg", "outputAve");
		saveImage(outputBand0, "jpg", "outputBand0");
		
	}
	
	public static void saveImage(BufferedImage im, String type, String name) throws IOException
	{
		File outputfile = new File(name + "." + type);
		ImageIO.write(im, type, outputfile);
	}
	
	public static void main( String args[] ) throws IOException {
		BufferedImage input = UtilImageIO.loadImage("src/test/resources/scan/000000400204.jpg");

		// Uncomment lines below to run each example

//		GrayScaleConverterTest.independent(input);
//		GrayScaleConverterTest.pixelAccess(input);
		GrayScaleConverterTest.convertToGray(input);
	}
	
	/**
	 * Many operations designed to only work on {@link boofcv.struct.image.ImageSingleBand} can be applied
	 * to a MultiSpectral image by feeding in each band one at a time.
	 */	
	public static void independent( BufferedImage input ) {
		// convert the BufferedImage into a MultiSpectral
		MultiSpectral<ImageUInt8> image = ConvertBufferedImage.convertFromMulti(input,null,true,ImageUInt8.class);

		// declare the output blurred image
		MultiSpectral<ImageUInt8> blurred =
				new MultiSpectral<ImageUInt8>(ImageUInt8.class,image.width,image.height,image.getNumBands());

		// Apply Gaussian blur to each band in the image
		for( int i = 0; i < image.getNumBands(); i++ ) {
			// note that the generalized version of BlurImageOps is not being used, but the type
			// specific version.
			BlurImageOps.gaussian(image.getBand(i),blurred.getBand(i),-1,5,null);
		}

		// Declare the BufferedImage manually to ensure that the color bands have the same ordering on input
		// and output
		BufferedImage output = new BufferedImage(image.width,image.height,input.getType());
		ConvertBufferedImage.convertTo(blurred, output,true);
		ShowImages.showWindow(input,"Input");
		ShowImages.showWindow(output,"Ouput");
	}	
	
}
