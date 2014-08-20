package com.c1info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Histogram {

	private Snippet snippet;
	private Map<Double, List<Rectangle>> data = new HashMap<Double, List<Rectangle>>();

	private int size = 100;
	private double step = 1;
	private double min = 1;
	private double max = 1;
	private SummaryStatistics stats = new SummaryStatistics();
	private Map<Rectangle, Double> areaMap = new HashMap<Rectangle, Double>();
	
	public Histogram(Snippet snippet)
	{
		this(snippet, 100);		
	}
	
	public Histogram(Snippet snippet, int size)
	{
		this.snippet = snippet;
		this.size = size;
		init();
		calculate();
	}	
	
	public Snippet getSnippet() {
		return snippet;
	}

	protected void init()
	{
		areaMap = Area.calculateAreaMap(snippet);
		for(Map.Entry<Rectangle, Double> entry:this.areaMap.entrySet())
		{
			double area = entry.getValue().doubleValue();
			stats.addValue(area);
		}
	}
	
	public double mean()
	{
		return stats.getMean();
	}
	
	protected Map<Double, List<Rectangle>> calculate()
	{
		min = stats.getMin();
		max = stats.getMax();
		step = (max - min)/size + 1;
		double start = min;
		for (int i=0;i<this.size;i++)
		{
			double from = start;
			double to = from + step;
			List<Rectangle> rectangles = findRectangles(from, to);
			data.put(to, rectangles);
			start = to;
		}
		return data;
	}
	
	protected List<Rectangle> findRectangles(double from, double to)
	{
		List<Rectangle> rectangles = new ArrayList<Rectangle>();
		for(Map.Entry<Rectangle, Double> entry:this.areaMap.entrySet())
		{
			double area = entry.getValue().doubleValue();
			if (area >= from && area < to)
			{
				rectangles.add(entry.getKey());
			}
		}
		return rectangles;
	}
	
	public Map<Double, List<Rectangle>> getData() {
		return data;
	}

	public double getStep() {
		return step;
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}	
	
	
}
