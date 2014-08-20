package com.c1info;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Rectangle {

	public Point p0;
	public Point p1;
	public Point p2;
	public Point p3;
	public double area;
	
	public Rectangle(Point p0, Point p1, Point p2, Point p3)
	{
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.area = this.calculateArea();
	}
	public List<Point> getPoints()
	{
		List<Point> points = new ArrayList<Point>();
		points.add(p0);
		points.add(p1);
		points.add(p2);
		points.add(p3);		
		return points;
	}
	
	protected double calculateArea()
	{
		return Area.calculateArea(this);
	}
	
	@Deprecated
	public SortedSet<Point> getPointsCcw()
	{
		SortedSet<Point> points = new TreeSet<Point>(new Comparator<Point>(){
			@Override
			public int compare(Point o1, Point o2) {
				if (o1.ccwDegree() < o2.ccwDegree())
				{
					return -1;
				} else if (o1.ccwDegree() > o2.ccwDegree())
				{
					return 1;
				} else
				{
					return 0;
				}
			}			
		});
		points.add(p0);
		points.add(p1);
		points.add(p2);
		points.add(p3);
		return points;
	}
	
	public String toString()
	{
		return "("+p0+","+p1+","+p2+","+p3+","+area+")";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p0 == null) ? 0 : p0.hashCode());
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
		result = prime * result + ((p3 == null) ? 0 : p3.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rectangle other = (Rectangle) obj;
		if (p0 == null) {
			if (other.p0 != null)
				return false;
		} else if (!p0.equals(other.p0))
			return false;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		if (p3 == null) {
			if (other.p3 != null)
				return false;
		} else if (!p3.equals(other.p3))
			return false;
		return true;
	}
	
}
