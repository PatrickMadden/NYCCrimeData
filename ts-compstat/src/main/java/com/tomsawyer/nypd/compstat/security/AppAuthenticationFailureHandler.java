//
// Tom Sawyer Software
// Copyright 2007 - 2020
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.security;


import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Custom AuthenticationFailureHandler to process redirects based on exception type.
 */
public class AppAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onAuthenticationFailure(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		AuthenticationException e) throws IOException, ServletException
	{
		this.setDefaultFailureUrl("/Security/SignIn?failed=true");

		super.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
	}
}
