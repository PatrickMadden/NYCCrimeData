//
// Tom Sawyer Software
// Copyright 2007 - 2019
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import java.security.Principal;


/**
 * Home Controller.
 */
@Controller
public class HomeController
{
	/**
	 * Index request for the application. If the input {@link Principal} is valid the
	 * homepage for logged in users is shown.
	 * @param principal The currently logged in user
	 * @return The home page view name depending on authentication status.
	 */
	@GetMapping("/")
	public String index(Principal principal)
	{
		String homeView;

		if (principal != null)
		{
			homeView = "redirect:/compstat/";
		}
		else
		{
			homeView = "home/homeNotSignedIn";
		}

		return homeView;
	}


	/**
	 * Called by the framework when access is denied to a page..
	 * @return The error/403 view name.
	 */
	@GetMapping("/403")
	public ModelAndView accessDenied(@AuthenticationPrincipal Principal user)
	{
		ModelAndView model = new ModelAndView();

		if (user != null)
		{
			model.addObject("msg",
				"Hi " +
					user.getName() +
					", you do not have permission to access this application!");
		}
		else
		{
			model.addObject("msg",
				"You do not have permission to access this page!");
		}

		model.setViewName("error/403");
		return model;
	}



	/**
	 * Index request for the application. If the input {@link Principal} is valid the
	 * homepage for logged in users is shown. If not a
	 * @return The home page view name depending on authentication status.
	 */
	@GetMapping("/home")
	public String home()
	{
		return "home/home";
	}


	/**
	 * The the module name for navigation to <code>browser</code>.
	 * @return The module name for navigation. Value is <code>browser</code>.
	 */
	@ModelAttribute("module")
	public String module()
	{
		return "home";
	}
}

