package org.c1info;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import boofcv.alg.feature.detect.quadblob.DetectQuadBlobsBinary;
import boofcv.alg.feature.detect.quadblob.QuadBlob;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.gui.image.ImageGridPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.ImageUInt8;

public class DetectQuadBlobsTest {

	public static void main(String[] args) throws IOException {
		BufferedImage input = UtilImageIO.loadImage("outputAve.jpg");
		DetectQuadBlobsBinary detect = new DetectQuadBlobsBinary(10, 0.25, 3);
		ImageUInt8 gray = ConvertBufferedImage.convertFromSingle(input, null, ImageUInt8.class);
		boolean isDetected = detect.process(gray);

		if (isDetected)
		{
			ImageGridPanel gui = new ImageGridPanel(1,2);
			gui.setImage(0,0,input);
			gui.setImage(0,1,input);
			gui.setPreferredSize(new Dimension(3*input.getWidth(), 3*input.getHeight()));
			ShowImages.showWindow(gui,"DetectQuadBlobsTest");
			
			List<QuadBlob> blobs = detect.getDetected();
			System.err.println(blobs);
			Graphics2D g2 = gui.getImage(0,1).createGraphics();
			g2.setColor(Color.RED);
			for (QuadBlob blob:blobs)
			{
				System.err.println(blob.center);
				//g2.drawOval(blob.center.x, blob.center.y, 100, 100);
			}
			gui.repaint();
		}
	}
}
