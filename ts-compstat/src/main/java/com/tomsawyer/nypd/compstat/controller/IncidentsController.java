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
@RequestMapping("/incidents")
public class IncidentsController
{
	/**
	 * Index request for the manufacturers.
	 * @return The index location
	 */
	@GetMapping({"/", ""})
	public String index()
	{
		return "incidents/index";
	}


	/**
	 * The the module name for navigation to <code>netflow</code>.
	 * @return The module name for navigation. Value is <code>netflow</code>.
	 */
	@ModelAttribute("module")
	public String module()
	{
		return "incidents";
	}
}
