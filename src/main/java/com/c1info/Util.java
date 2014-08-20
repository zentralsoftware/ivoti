package com.c1info;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Util {

	public static void saveImage(BufferedImage im, String type, String name) throws IOException
	{
		File outputfile = new File(name + "." + type);
		ImageIO.write(im, type, outputfile);
	}

	public static BufferedImage duplicate(final BufferedImage image) {
		if (image == null)
			throw new NullPointerException();

		BufferedImage j = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		j.setData(image.getData());
		return j;
	}	
	
	public static Color getRandomColor()
	{
		int r = (int)(1 + (255)*Math.random());
		int g = (int)(1 + (255)*Math.random());
		int b = (int)(1 + (255)*Math.random());
		return new Color(r,g,b);
	}

}
