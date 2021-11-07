//
// Tom Sawyer Software
// Copyright 2007 - 2019
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * The request controller for Perspectives.
 */
@Controller
@RequestMapping("/compstat")
public class CompStatController
{
	/**
	 * Index request for the manufacturers.
	 * @return The index location
	 */
	@GetMapping({"/", ""})
	public String index()
	{
		return "compstat/index";
	}


	/**
	 * Loads the plain Tom Sawyer Perspectives plain html page. This can be embedded
	 * in an IFrame from a browser application template.
	 * @return The default dashboard request.
	 */
	@GetMapping("/tspframe")
	public String toTspFrameTemplate()
	{
		return "compstat/tspframe";
	}


	/**
	 * The the module name for navigation to <code>netflow</code>.
	 * @return The module name for navigation. Value is <code>netflow</code>.
	 */
	@ModelAttribute("module")
	public String module()
	{
		return "compstat";
	}
}
