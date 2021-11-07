//
// Tom Sawyer Software
// Copyright 2007-2020
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.security;


import com.tomsawyer.util.logging.TSLogger;
import com.tomsawyer.util.server.xsrf.TSHeaderXsrfTokenProvider;
import com.tomsawyer.util.server.xsrf.TSXsrfTokenException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Integrate Tom Sawyer Perspectives CSRF with Spring Security CSRF. It uses the
 * Spring class {@link HttpSessionCsrfTokenRepository} to create and manage CSRF
 * tokens.
 */
public class PerspectivesSecurityTokenProvider extends TSHeaderXsrfTokenProvider
{
	/**
	 * Constructed by the framework for Tom Sawyer CSRF protected services.
	 */
	public PerspectivesSecurityTokenProvider()
	{
		super();

		TSLogger.debug(this.getClass(),
			"Creating Tom Sawyer Perspectives Spring Security CSRF Token Provider");

		this.setHeaderName(DEFAULT_CSRF_HEADER_NAME);
		this.setSessionAttributeValue(DEFAULT_CSRF_TOKEN_SESSION_ATTR_NAME);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToken(HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse)
	{
		String tokenString;

		Object objectToken =
			httpServletRequest.getAttribute(DEFAULT_CSRF_PARAMETER_NAME);

		if (objectToken instanceof CsrfToken)
		{
			// see if it has been specified as a request parameter.
			tokenString = ((CsrfToken) objectToken).getToken();
		}
		else
		{
			// loads the session token.

			CsrfToken sessionToken = getTokenRepository(httpServletRequest).
				loadToken(httpServletRequest);

			if (sessionToken != null)
			{
				tokenString = sessionToken.getToken();
			}
			else
			{
				tokenString = null;
			}
		}

		return tokenString;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String createToken(HttpSession httpSession,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) throws TSXsrfTokenException
	{
		try
		{
			CsrfToken token = getTokenRepository(httpServletRequest).
				generateToken(httpServletRequest);

			String tokenString;

			if (token != null)
			{
				getTokenRepository(httpServletRequest).saveToken(
					token,
					httpServletRequest,
					httpServletResponse);

				tokenString = token.getToken();
			}
			else
			{
				tokenString = null;
			}

			return tokenString;
		}
		catch (Throwable th)
		{
			throw new TSXsrfTokenException(th);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTokenName()
	{
		return DEFAULT_CSRF_HEADER_NAME;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateToken(HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		String expectedToken) throws TSXsrfTokenException
	{
		super.validateToken(httpServletRequest, httpServletResponse, expectedToken);

		TSLogger.debug(this.getClass(), "Validated CSRF Token for request.");
	}


	/**
	 * Helper method to get the CSRF token repository.
	 * @param httpServletRequest The current http servlet request.
	 * @return The token repository.
	 */
	protected CsrfTokenRepository getTokenRepository(
		HttpServletRequest httpServletRequest)
	{
		if (this.csrfTokenRepository == null)
		{
			this.csrfTokenRepository =
				WebApplicationContextUtils.getWebApplicationContext(
					httpServletRequest.getServletContext()).getBean(
						CsrfTokenRepository.class);
		}

		return this.csrfTokenRepository;
	}


	/**
	 * Facade to hide loading, saving and accessing CSRF token.
	 */
	protected CsrfTokenRepository csrfTokenRepository;


	/**
	 * Parameter name.
	 */
	protected static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";


	/**
	 * Header name.
	 */
	protected static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";


	/**
	 * Session Attribute name for token instance.
	 */
	protected static final String DEFAULT_CSRF_TOKEN_SESSION_ATTR_NAME =
		HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN");


	/**
	 * Java Serialization ID.
	 */
	private static final long serialVersionUID = 1L;
}

