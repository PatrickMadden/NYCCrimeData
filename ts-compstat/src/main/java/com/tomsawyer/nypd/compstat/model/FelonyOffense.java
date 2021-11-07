//
// Tom Sawyer Software
// Copyright 2007 - 2021
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.model;


public class FelonyOffense
{
	public FelonyOffense(String name, int year, int count)
	{
		this.name = name;
		this.year = year;
		this.count = count;
	}

	public FelonyOffense()
	{

	}

	/**
	 * Gets name.
	 *
	 * @return value of name
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * Sets name.
	 *
	 * @param name value of name
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * Gets year.
	 *
	 * @return value of year
	 */
	public int getYear()
	{
		return year;
	}


	/**
	 * Sets year.
	 *
	 * @param year value of year
	 */
	public void setYear(int year)
	{
		this.year = year;
	}


	/**
	 * Gets count.
	 *
	 * @return value of count
	 */
	public int getCount()
	{
		return count;
	}


	/**
	 * Sets count.
	 *
	 * @param count value of count
	 */
	public void setCount(int count)
	{
		this.count = count;
	}


	String name;
	int year;
	int count;
}
