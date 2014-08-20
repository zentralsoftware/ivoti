package org.c1info;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.junit.Test;

import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;

import com.c1info.Util;
import com.c1info.io.Find.Finder;

public class CropImageTest {

	@Test
	public void testFinder() throws IOException
	{
		Path startingDir = Paths.get("src/test");
		String pattern = "*04.jpg";
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);
		finder.done();
	}

	@Test
	public void calcImgRatio() throws IOException
	{
		Path startingDir = Paths.get("/Users/fadnan/Documents/workspace-sts-3.4.0.RELEASE/seleniumtest/output/ACEH");
		String pattern = "*04.jpg";
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);
		List<Path> foundFiles = finder.getFoundFiles();
		Map<String, Integer> ratioMap = new HashMap<String, Integer>();
		for (Path file:foundFiles)
		{
			BufferedImage image = UtilImageIO.loadImage(file.toFile().getAbsolutePath());
			int width = image.getWidth();
			int height = image.getHeight();
			String key = width + "x" + height;
			Integer size = ratioMap.get(key);
			if (size != null)
			{
				size = size.intValue() + 1;
			} else
			{
				size = 1;
			}
			System.err.println(key +","+size);
			ratioMap.put(key, size);
		}
			
	}
	
	@Test
	public void showAvgRatio() throws IOException
	{
		Path startingDir = Paths.get("/Users/fadnan/Documents/workspace-sts-3.4.0.RELEASE/seleniumtest/output/ACEH");
		String pattern = "*04.jpg";
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);
		List<Path> foundFiles = finder.getFoundFiles();
		
		float sumRatio = 0f;
		for (Path file:foundFiles)
		{
			BufferedImage image = UtilImageIO.loadImage(file.toFile().getAbsolutePath());
			float ratio = (float)image.getWidth()/image.getHeight();
			sumRatio += ratio;
		}
		float avgRatio = (float)sumRatio/foundFiles.size();
		System.err.println("avg: " + String.format("%5f", avgRatio));
	}

	@Test
	public void testCropMany() throws IOException
	{		
		Path startingDir = Paths.get("/Users/fadnan/Documents/workspace-sts-3.4.0.RELEASE/seleniumtest/output/ACEH");
		String pattern = "*04.jpg";
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);

		List<Path> foundFiles = finder.getFoundFiles();
		for (int i=0;i<10;i++)
		{
			
		}

	}	
	
	@Test
	public void testGetRatioSample() throws IOException
	{		
		Path startingDir = Paths.get("/Users/fadnan/Documents/workspace-sts-3.4.0.RELEASE/seleniumtest/output/ACEH");
		String pattern = "*04.jpg";
		Finder finder = new Finder(pattern);
		Files.walkFileTree(startingDir, finder);
		Set<String> dimSet = new TreeSet<String>();
		List<Path> foundFiles = finder.getFoundFiles();
		for (Path foundFile:foundFiles)
		{			
			BufferedImage image = UtilImageIO.loadImage(foundFile.toFile().getAbsolutePath());
			int width = image.getWidth();
			int height = image.getHeight();
			String key = width + "x" + height;
			if (!dimSet.contains(key))
			{
				dimSet.add(key);
				System.err.println("crop " + foundFile + ", size " + key);
				String filename = foundFile.toFile().getName();
				String filenameWoExt = filename.substring(0,filename.length()-4);				
				Path file = Paths.get("src/test/resources/crop/" + filenameWoExt + "_" + key);
				cropImage(foundFile,file);
			}
		}

	}		
	
	@Test
	public void testCrop() throws IOException
	{		
		Path file = Paths.get("src/test/resources/scan/000000400204.jpg");
		String filename = file.toFile().getName();
		String filenameWoExt = filename.substring(0,filename.length()-4);
		Path output = Paths.get(filenameWoExt);
		cropImage(file, output);
	}

	private void cropImage(Path file, Path cropFile) throws IOException {
		BufferedImage image = UtilImageIO.loadImage(file.toFile().getAbsolutePath());
		final float widthPerc = 0.134f;
		final float heightPerc = 0.05f;
		final float scaleWidth = 1.5f;
		final float scaleHeight = 6.5f;
		int width = image.getWidth();
		int height = image.getHeight();
		System.err.println(width +","+height);
		int posX = width - (new Float(widthPerc*scaleWidth*width)).intValue();
		int posY = (new Float(heightPerc*3*height)).intValue();
		int cropWidth = (int)(widthPerc*scaleWidth*width);
		int cropHeight = (new Float(heightPerc*scaleHeight*height)).intValue();
		System.err.println(cropWidth +","+cropHeight);
		Rectangle rect = new Rectangle(posX, posY, cropWidth, cropHeight);
		BufferedImage crop = cropImage(image, rect);
		Util.saveImage(crop, "jpg", cropFile.toFile().getAbsolutePath());
	}

	private BufferedImage cropImage(BufferedImage src, Rectangle rect) {
		System.err.println(rect.x +","+ rect.y+","+ rect.width+","+ rect.height);
		BufferedImage dest = src.getSubimage(rect.x, rect.y, rect.width, rect.height);
		return dest; 
	}	
	
	@Test
	public void cropArbitraryShape() throws IOException
	{
		BufferedImage input = ImageIO.read(new File("src/test/resources/cropTest/000167400104_637x1050.jpg"));
		BufferedImage out = new BufferedImage(input.getWidth(), input.getHeight(), input.getType());
		out.getGraphics().fillRect(0, 0, out.getWidth(), out.getHeight());
		Polygon p = new Polygon();
		p.addPoint(0, 0);
		p.addPoint(0, input.getHeight());
		p.addPoint(input.getWidth(), input.getHeight());
		p.addPoint(input.getWidth(), 0);
		Graphics2D g2 = out.createGraphics();
		g2.setClip(p);
		g2.drawImage(input, 0, 0, null);
		// convert into a usable format
		ImageFloat32 outFloat32 = ConvertBufferedImage.convertFromSingle(out, null, ImageFloat32.class);
		// the mean pixel value is often a reasonable threshold when creating a binary image
		double mean = ImageStatistics.mean(outFloat32);	
		System.err.println("mean: " + mean);
		Util.saveImage(out, "jpg", "oyeah");
	}
}
