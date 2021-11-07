//
// Tom Sawyer Software
// Copyright 2007 - 2020
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * List utilities.
 */
public class TSLists
{
	/**
	 * Converts an iterator into a list.
	 * @param iterator The iterator to convert.
	 * @param <T> The type of element in the list.
	 * @return The converted list.
	 */
	public static <T> List<T> toArrayList(Iterator<T> iterator)
	{
		List<T> list = new ArrayList<>();

		iterator.forEachRemaining(e -> list.add(e));

		return list;
	}
}
