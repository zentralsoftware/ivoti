package org.c1info;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

import com.c1info.Util;
import com.c1info.io.Find.Finder;

public class VisualBinaryTest {

	@Test
	public void convertToBinary() throws IOException
	{
		Path startingDir = Paths.get("src/test/resources/crop");
		String pattern = "*04_6*.jpg";
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);
		List<Path> foundFiles = finder.getFoundFiles();
		System.err.println(foundFiles);
		for (Path foundFile:foundFiles)
		{
			String filename = foundFile.toFile().getName();
			String filenameWoExt = filename.substring(0,filename.length()-4);				
			Path destination = Paths.get("src/test/resources/visualBinary/" + filenameWoExt);
			convertAndSave(foundFile, destination, 2);
		}
	}
	
	public void convertAndSave(Path file, Path destination, int scale) throws IOException
	{
		// load and convert the image into a usable format
		BufferedImage image = UtilImageIO.loadImage(file.toFile().getAbsolutePath());
		
		RescaleOp rescaleOp = new RescaleOp(1.1f, 15, null);
		rescaleOp.filter(image, image);
		
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
		
		// display the results
		BufferedImage visualBinary = VisualizeBinaryData.renderBinary(filtered, null);		
		BufferedImage after = new BufferedImage(scale*visualBinary.getWidth(), scale*visualBinary.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		AffineTransformOp scaleOp = 
		   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(visualBinary, after);		
		
		Util.saveImage(after, "jpg", destination.toFile().getAbsolutePath());		
	}
	
	@Test
	public void contrastTest() throws IOException
	{
		BufferedImage image = UtilImageIO.loadImage("src/test/resources/scan/000000400104.jpg");
		RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);
		rescaleOp.filter(image, image);
		GrayScaleConverterTest.saveImage(image, "jpg", "contrast");
	}
	
}
