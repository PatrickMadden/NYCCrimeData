//
// Tom Sawyer Software
// Copyright 2007-2020
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.web;


import com.tomsawyer.util.TSSystem;
import com.tomsawyer.util.logging.TSLogger;
import com.tomsawyer.visualization.gwt.server.api.TSHttpSessionContext;
import com.tomsawyer.visualization.gwt.server.bootstrap.TSServletContainerBootstrap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;


/**
 * The servlet container bootstrap container.
 */
public class ServletContainerBootstrap extends TSServletContainerBootstrap
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sessionCreated(HttpSessionEvent httpSessionEvent)
	{
		HttpSession session = httpSessionEvent.getSession();
		int sessionTimeoutInSeconds = session.getMaxInactiveInterval();

		TSLogger.debug(this.getClass(),
			"Session Created : #0. Inactive interval is #1 seconds.",
			session.getId(),
			sessionTimeoutInSeconds);

		// Do not initialize Licensing here. We don't want anonymous sessions.
		// Use AppAuthenticationSuccessHandler's onAuthenticationSuccess instead.

		if (TSSystem.getCoreInfo() instanceof TSHttpSessionContext)
		{
			((TSHttpSessionContext) TSSystem.getCoreInfo()).
				sessionCreated(httpSessionEvent.getSession());
		}
	}
}
