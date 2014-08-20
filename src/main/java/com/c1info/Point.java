package com.c1info;

import georegression.metric.UtilAngle;

public class Point implements Comparable<Point>
{
	public int x;
	public int y;
	
	public Point() {}
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public double length()
	{
		long lenSq = x*x + y*y;
		return Math.sqrt(lenSq);
	}
	
	public double ccwDegree()
	{		
		return UtilAngle.radianToDegree(Math.tan(y/x));
	}
	
	@Override
	public String toString()
	{
		return "("+x+","+y+")";
	}
	
	@Override
	public int compareTo(Point o) {
		if (this.length() < o.length())
		{
			return -1;
		} else if (this.length() > o.length())
		{
			return 1;
		} else
		{
			return 0;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Point other = (Point) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
}
