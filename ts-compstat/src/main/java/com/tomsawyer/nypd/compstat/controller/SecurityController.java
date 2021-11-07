//
// Tom Sawyer Software
// Copyright 2007 - 2019
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.net.MalformedURLException;


/**
 * Security controller.
 */
@Controller
public class SecurityController
{
	/**
	 * Handler method for Spring Security's custom Login page.
	 *
	 * @return the login view name.
	 */
	@RequestMapping("/Security/SignIn")
	String toSignIn(Model model, @RequestParam(required = false) boolean failed)
		throws MalformedURLException
	{
		return "home/homeNotSignedIn";
	}


	/**
	 * Handler method for the root path, which redirects the request to the HostPath
	 * page.
	 *
	 * @return Redirect command for to the HostPath page.
	 */
	@RequestMapping({"", "/"})
	String root()
	{
		return "redirect:/homepage";
	}


	// -----------------------------------------------------------------------------------
	// Section: Security Error Handler methods.
	// -----------------------------------------------------------------------------------


	/**
	 * Handler method for Spring Security's custom "Access Denied" page.
	 *
	 * @return The access denied view name.
	 */
	@RequestMapping("/Security/AccessDenied")
	String toAccessDenied()
	{
		return "redirect:/homepage";
	}
}
