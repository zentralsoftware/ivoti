package com.c1info;

import georegression.struct.point.Point2D_F64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Area {

	public static SummaryStatistics getSummaryStatistics(Snippet snippet)
	{
		SummaryStatistics stats = new SummaryStatistics();
		List<Double> areas = calculateAreas(snippet);
		for (Double area:areas)
		{
			stats.addValue(area);
		}
		return stats;
	}
	
	public static double calculateAvgArea(Snippet snippet)
	{
		double avgArea = 0;
		List<Double> areas = calculateAreas(snippet);
		for (Double area:areas)
		{
			avgArea += area;
		}
		return avgArea / areas.size();
	}
	
	public static List<Double> calculateAreas(Snippet snippet)
	{
		List<Double> areas = new ArrayList<Double>();
		List<Bounding> boundings = snippet.getBoundings();
		for (Bounding bounding:boundings)
		{
			List<Point2D_F64> corners = bounding.getCorners();
			double area = calculateArea(corners);
			areas.add(new Double(area));
		}
		return areas;
	}
	
	public static Map<Rectangle, Double> calculateAreaMap(Snippet snippet)
	{
		Map<Rectangle, Double> areaMap = new HashMap<Rectangle, Double>();
		List<Bounding> boundings = snippet.getBoundings();
		for (Bounding bounding:boundings)
		{
			Rectangle rectangle = bounding.getRectangle();
			if (rectangle != null)
			{
				double area = calculateArea(rectangle);
				areaMap.put(rectangle, area);
			}
		}
		return areaMap;
	}	
	
	public static double calculateArea(List<Point2D_F64> corners)
	{
		int size = corners.size();
		double x[] = new double[size];
		double y[] = new double[size];
		for (int i=0;i<size;i++)
		{
			Point2D_F64 point = corners.get(i);
			x[i] = point.x;
			y[i] = point.y;
		}
		return rectangularArea(x, y, size);
	}
	
	public static double calculateArea(Rectangle rectangle)
	{
		int size = 4;
		double x[] = new double[size];
		double y[] = new double[size];
		List<Point> points = rectangle.getPoints();
		for (int i=0;i<points.size();i++)
		{
			Point point = points.get(i);
			x[i] = point.x;
			y[i] = point.y;
		}
		return rectangularArea(x, y, size);
	}	
	
	public static double rectangularArea(double x[], double y[], int size)
	{
		double area = 0;
		int j = size - 1;
		for (int i=0;i<size;i++)
		{
			area = area + (x[j]+x[i])*(y[j]-y[i]); 
			j = i;
		}
		return Math.abs(area/2);
	}	
}
