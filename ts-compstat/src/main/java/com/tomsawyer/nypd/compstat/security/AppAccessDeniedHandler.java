//
// Tom Sawyer Software
// Copyright 2007 - 2019
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.security;


import com.tomsawyer.util.logging.TSLogger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This class extends the AccessDeniedHandler class to improve logging.
 */
public class AppAccessDeniedHandler extends AccessDeniedHandlerImpl
{
	/**
	 * The access denied handler constructor sets the error page to /403
	 */
	public AppAccessDeniedHandler()
	{
		this("/403");
	}


	/**
	 * The access denied handler with a specific error page.
	 * @param errorPage The error page to go to.
	 */
	public AppAccessDeniedHandler(String errorPage)
	{
		this.setErrorPage("/403");
	}


	/**
	 * This method logs and handles the {@link AccessDeniedException}.
	 *
	 * @param request The Http request which caused the exception.
	 * @param response The Http response to be sent.
	 * @param accessDeniedException The Exception.
	 */
	@Override
	public void handle(HttpServletRequest request,
		HttpServletResponse response,
		AccessDeniedException accessDeniedException)
		throws IOException, ServletException
	{
		TSLogger.error(
			this.getClass(),
			"AccessDeniedException thrown in request to #0?#1",
			accessDeniedException,
			request.getRequestURL(),
			request.getQueryString());

		super.handle(request, response, accessDeniedException);
	}
}
