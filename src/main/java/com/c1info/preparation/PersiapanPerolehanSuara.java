package com.c1info.preparation;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import boofcv.io.image.UtilImageIO;

import com.c1info.Util;

public class PersiapanPerolehanSuara {

	private String inputDirectory;
	private String outputDirectory;
	private String pattern;
	private double percGridX = 0.10d;
	private double percGridY = 0.05d;
	private double posX = 8.03d;
	private double posY = 3.4d;
	private double countGridX = 1.7d;
	private double countGridY = 5.8d;
	
	public void execute() throws IOException
	{
		List<Path> paths = Util.searchForFiles(inputDirectory, pattern);
		for (Path path:paths)
		{			
			String filename = path.toFile().getName();
			String filenameWoExt = filename.substring(0,filename.length()-4);				
			Path file = Paths.get(outputDirectory + File.separator + filenameWoExt);
			cropImage(path,file);			
		}		
	}
	
	private void cropImage(Path file, Path cropFile) throws IOException {
		BufferedImage image = UtilImageIO.loadImage(file.toFile().getAbsolutePath());
		AreaPerolehanSuara area = new AreaPerolehanSuara(image, percGridX, percGridY, posX, posY, countGridX, countGridY);
		Rectangle rect = new Rectangle(area.x, area.y, area.width, area.height);
		BufferedImage crop = image.getSubimage(rect.x, rect.y, rect.width, rect.height);
		Util.saveImage(crop, "jpg", cropFile.toFile().getAbsolutePath());
	}		
	
	public String getInputDirectory() {
		return inputDirectory;
	}

	public void setInputDirectory(String inputDirectory) {
		this.inputDirectory = inputDirectory;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public double getPercGridX() {
		return percGridX;
	}

	public void setPercGridX(double percGridX) {
		this.percGridX = percGridX;
	}

	public double getPercGridY() {
		return percGridY;
	}

	public void setPercGridY(double percGridY) {
		this.percGridY = percGridY;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public double getCountGridX() {
		return countGridX;
	}

	public void setCountGridX(double countGridX) {
		this.countGridX = countGridX;
	}

	public double getCountGridY() {
		return countGridY;
	}

	public void setCountGridY(double countGridY) {
		this.countGridY = countGridY;
	}

}
