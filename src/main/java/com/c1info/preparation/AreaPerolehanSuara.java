package com.c1info.preparation;

import java.awt.image.BufferedImage;

public class AreaPerolehanSuara extends AreaInformasi {

	private double percGridX = 0.134d;
	private double percGridY = 0.05d;
	
	private double posX = 5;
	private double posY = 2;
	
	private double countGridX = 10;
	private double countGridY = 11;
	
	private BufferedImage image;
	
	public AreaPerolehanSuara(BufferedImage image,
								double percGridX, double percGridY,
								double posX, double posY,
								double countGridX, double countGridY)
	{
		this.image = image;
		this.percGridX = percGridX;
		this.percGridY = percGridY;
		this.posX = posX;
		this.posY = posY;
		this.countGridX = countGridX;
		this.countGridY = countGridY;
		this.hitungArea();
	}
	
	@Override
	public void hitungArea() {
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		
		this.x = (int) (Math.round(posX*percGridX*imgWidth));
		this.y = (int) (Math.round(posY*percGridY*imgHeight));
		
		this.width = (int) (Math.round(countGridX*percGridX*imgWidth));
		this.height = (int) (Math.round(countGridY*percGridY*imgHeight));
		
	}

	
}
