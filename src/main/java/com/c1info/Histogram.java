package com.c1info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Histogram {

	private Snippet snippet;
	private Map<Double, List<Bounding>> data = new HashMap<Double, List<Bounding>>();

	private int size = 100;
	private double step = 1;
	private double min = 1;
	private double max = 1;
	private SummaryStatistics stats = new SummaryStatistics();
	
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
		List<Bounding> boundings = snippet.getBoundings();
		for (Bounding bounding:boundings)
		{
			Rectangle rectangle = bounding.getRectangle();
			if (rectangle != null)
			{
				stats.addValue(rectangle.area);
			}
		}
	}
	
	public double mean()
	{
		return stats.getMean();
	}
	
	protected Map<Double, List<Bounding>> calculate()
	{
		min = stats.getMin();
		max = stats.getMax();
		step = (max - min)/size + 1;
		double start = min;
		for (int i=0;i<this.size;i++)
		{
			double from = start;
			double to = from + step;
			List<Bounding> boundings = findBoundings(from, to);
			data.put(to, boundings);
			start = to;
		}
		return data;
	}
	
	protected List<Bounding> findBoundings(double from, double to)
	{
		List<Bounding> resBoundings = new ArrayList<Bounding>();
		List<Bounding> boundings = snippet.getBoundings();
		for (Bounding bounding:boundings)
		{
			Rectangle rectangle = bounding.getRectangle();
			if (rectangle != null)
			{
				double area = rectangle.area;
				if (area >= from && area < to)
				{
					resBoundings.add(bounding);
				}
			}
		}
		return resBoundings;
	}	
	
	public Map<Double, List<Bounding>> getData() {
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
