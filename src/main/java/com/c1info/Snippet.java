package com.c1info;

import georegression.metric.UtilAngle;

import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.List;

import org.imgscalr.Scalr;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.struct.ConnectRule;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

public class Snippet {

	private List<Bounding> boundings = new ArrayList<Bounding>();
	private ImageUInt8 binary;
	private int pixelValue;
	private int angleValue;
	private double angleTol;
	private BufferedImage original;
	private BufferedImage scaled;
	private BufferedImage bw;
	private BufferedImage visualBinary;
	private int scaleVal;
	private double contrastValue;
	private double brightnessValue;
	
	private Snippet() {};
	
	public static Snippet getInstance(final BufferedImage original, 
									final int scaleVal, 
									final int pixelValue, 
									final int angleValue, 
									final double contrastValue, 
									final double brightnessValue)
	{
		Snippet snippet = new Snippet();
		snippet.original = original;
		snippet.scaleVal = scaleVal;
		snippet.pixelValue = pixelValue;
		snippet.angleValue = angleValue;
		snippet.contrastValue = contrastValue;
		snippet.brightnessValue = brightnessValue;
		snippet.scaled = snippet.scale(original, scaleVal);
		snippet.bw = snippet.convert(snippet.scaled);
		snippet.binary = snippet.getBinary(snippet.bw);
		snippet.angleTol = UtilAngle.degreeToRadian(snippet.angleValue);
		snippet.visualBinary = ConvertBufferedImage.convertTo(snippet.binary, null);
		snippet.init();
		return snippet;
	}
			
	public List<Bounding> getBoundings() {
		return boundings;
	}

	public void setBoundings(List<Bounding> boundings) {
		this.boundings = boundings;
	}

	public ImageUInt8 getBinary() {
		return binary;
	}	
	
	protected void init()
	{
		List<Contour> contours = BinaryImageOps.contour(binary, ConnectRule.EIGHT,null);
		for (Contour contour:contours)
		{
			Bounding bounding = Bounding.getInstance(scaled, contour, this.pixelValue, this.angleTol);
			boundings.add(bounding);
		}
	}
	
	protected BufferedImage scale(BufferedImage orig, int scale)
	{
		if (scale > 1)
		{
			BufferedImage after = Scalr.resize(orig, scale*orig.getWidth(), scale*orig.getHeight(), Scalr.OP_GRAYSCALE);
			return after;
		} else
		{
			return orig;
		}
	}	
	
	protected BufferedImage convert(BufferedImage image)
	{			
		RescaleOp rescaleOp = new RescaleOp((float)contrastValue, (float)brightnessValue, null);
		rescaleOp.filter(image, image);		
		ImageUInt8 binary = getBinary(image);
		BufferedImage visualBinary = VisualizeBinaryData.renderBinary(binary, null);
		return visualBinary;		
	}	
	
	protected ImageUInt8 getBinary(BufferedImage image)
	{					
		// convert into a usable format
		ImageFloat32 input = ConvertBufferedImage.convertFromSingle(image, null, ImageFloat32.class);
		ImageUInt8 binary = new ImageUInt8(input.width,input.height);
		ImageUInt8 filtered = new ImageUInt8(input.width,input.height);

		// the mean pixel value is often a reasonable threshold when creating a binary image
		double mean = ImageStatistics.mean(input);

		// create a binary image by thresholding
		ThresholdImageOps.threshold(input,binary,(float)mean,true);

		// reduce noise with some filtering
		BinaryImageOps.removePointNoise(binary, filtered);		
		return filtered;
		
	}		
	public double getPolyPixelTol() {
		return pixelValue;
	}
	public BufferedImage getOriginal() {
		return original;
	}
	public double getAngleTol() {
		return angleTol;
	}
	public BufferedImage getScaled() {
		return scaled;
	}
	public BufferedImage getBw() {
		return bw;
	}

	public BufferedImage getVisualBinary() {
		return visualBinary;
	}

	public int getScaleVal() {
		return scaleVal;
	}	
}
