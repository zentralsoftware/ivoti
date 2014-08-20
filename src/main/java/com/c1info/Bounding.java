package com.c1info;

import georegression.struct.point.Point2D_F64;
import georegression.struct.point.Point2D_I32;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import boofcv.alg.feature.detect.quadblob.FindBoundingQuadrilateral;
import boofcv.alg.feature.shapes.ShapeFittingOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.misc.ImageStatistics;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.PointIndex_I32;
import boofcv.struct.image.ImageFloat32;

public class Bounding {

	private Contour contour;
	private double polyPixelTol;
	private double angleTol;
	private List<PointIndex_I32> externalVertexes = new ArrayList<PointIndex_I32>();
	private List<List<PointIndex_I32>> internalVertexes = new ArrayList<List<PointIndex_I32>>();
	private List<Point2D_F64> corners = new ArrayList<Point2D_F64>();
	private Rectangle rectangle = null;
	private BufferedImage image = null;
	private double meanIntensity;
	
	private Bounding() {};
	
	public static Bounding getInstance(BufferedImage image,Contour contour, double polyPixelTol, double angleTol)
	{
		Bounding bounding = new Bounding();
		bounding.image = image;
		bounding.contour = contour;
		bounding.polyPixelTol = polyPixelTol;
		bounding.angleTol = angleTol;
		bounding.populate();
		return bounding;
	}
	
	private Bounding(Contour contour, double polyPixelTol, double angleTol)
	{
		this.contour = contour;
		this.polyPixelTol = polyPixelTol;
		this.angleTol = angleTol;
		populate();
	}
	
	public Contour getContour() {
		return contour;
	}
	public void setContour(Contour contour) {
		this.contour = contour;
	}
	public List<PointIndex_I32> getExternalVertexes() {
		return externalVertexes;
	}
	public void setExternalVertexes(List<PointIndex_I32> externalVertexes) {
		this.externalVertexes = externalVertexes;
	}
	public List<List<PointIndex_I32>> getInternalVertexes() {
		return internalVertexes;
	}
	public void setInternalVertexes(List<List<PointIndex_I32>> internalVertexes) {
		this.internalVertexes = internalVertexes;
	}
	public List<Point2D_F64> getCorners() {
		return corners;
	}
	public void setCorners(List<Point2D_F64> corners) {
		this.corners = corners;
	}
	
	public void populate()
	{
		this.externalVertexes = ShapeFittingOps.fitPolygon(this.contour.external,true,this.polyPixelTol,this.angleTol,100);
		for( List<Point2D_I32> internal : this.contour.internal ) {
			List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(internal,true,this.polyPixelTol,this.angleTol,100);
			internalVertexes.add(vertexes);
		}
		if (this.externalVertexes.size() >= 4)
		{
			List<Point2D_F64> points = pointsFrom(this.externalVertexes);
			this.corners = FindBoundingQuadrilateral.findCorners(points);	
			Point2D_F64[] arr = this.corners.toArray(new Point2D_F64[this.corners.size()]);
			this.rectangle = new Rectangle(new Point((int)arr[0].x,(int)arr[0].y),
					new Point((int)arr[1].x,(int)arr[1].y),
					new Point((int)arr[2].x,(int)arr[2].y),
					new Point((int)arr[3].x,(int)arr[3].y)
					);
			calculateMeanIntensity();
		}
	}

	protected void calculateMeanIntensity()
	{
		BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
		out.getGraphics().setColor(Color.white);
		out.getGraphics().fillRect(0, 0, out.getWidth(), out.getHeight());	
		Polygon p = new Polygon();
		p.addPoint(rectangle.p0.x, rectangle.p0.y);
		p.addPoint(rectangle.p1.x, rectangle.p1.y);
		p.addPoint(rectangle.p2.x, rectangle.p2.y);
		p.addPoint(rectangle.p3.x, rectangle.p3.y);
		Graphics2D g2 = out.createGraphics();
		g2.setClip(p);
		g2.drawImage(image, 0, 0, null);
		// convert into a usable format
		ImageFloat32 outFloat32 = ConvertBufferedImage.convertFromSingle(out, null, ImageFloat32.class);
		// the mean pixel value
		meanIntensity = ImageStatistics.mean(outFloat32);			
	}
	
	public Rectangle getRectangle() {
		return rectangle;
	}

	protected List<Point2D_F64> pointsFrom(List<PointIndex_I32> points)
	{
		List<Point2D_F64> list = new ArrayList<Point2D_F64>();
		for (PointIndex_I32 point:points)
		{
			list.add(pointFrom(point));
		}
		return list;
	}

	protected Point2D_F64 pointFrom(PointIndex_I32 point)
	{
		Point2D_F64 p = new Point2D_F64(point.getX(), point.getY());
		return p;
	}

	public BufferedImage getImage() {
		return image;
	}

	public double getMeanIntensity() {
		return meanIntensity;
	}	
}
