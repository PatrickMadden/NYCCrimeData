//
// Tom Sawyer Software
// Copyright 2007 - 2019
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.security;


import com.tomsawyer.licensing.TSLicenseManager;
import com.tomsawyer.licensing.server.TSLicensingConstants;
//import com.tomsawyer.solutions.framework.session.AppSession;
import com.tomsawyer.util.logging.TSLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;
import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * This class handles authentication success.
 */
public class AppAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException
	{
		TSLogger.info(this.getClass(),
			"Initializing Tom Sawyer Licensing using #0 for session #1.",
			authentication.getName(),
			request.getSession().getId());

//		// Get the AppSession Bean and set the authentication instance so we
//		// can use it to send web socket messages in a uni-cast method.
//
//		AppSession appSession = WebApplicationContextUtils.getWebApplicationContext(
//			((ServletRequestAttributes) RequestContextHolder
//				.getRequestAttributes()).getRequest().getServletContext()).getBean(
//			AppSession.class);
//
//		appSession.setAuthentication(authentication);

		request.getSession().setAttribute(
			TSLicensingConstants.TS_LICENSING_USER_SESSION_ATTRIBUTE,
			authentication.getName());

		TSLicenseManager.setUserName(authentication.getName());

		this.clearAuthenticationAttributes(request);

//		if (this.isSecure())
//		{
//			TSLogger.warn(this.getClass(), "Enabling secure SameSite: None cookies");
//			this.allowSameSiteNone(response);
//		}
//		else
//		{
//			TSLogger.warn(this.getClass(), "No SSL - SameSite default cookies");
//		}

		this.handle(request, response, authentication);
	}


	private boolean isSecure()
	{
		TSLogger.warn(this.getClass(), "Active Profiles: " + this.profiles);
		return this.secure || this.profiles.contains("xforward");
	}



	private void allowSameSiteNone(HttpServletResponse response) {
		Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
		boolean firstHeader = true;
		// there can be multiple Set-Cookie attributes
		for (String header : headers) {
			if (firstHeader) {
				response.setHeader(HttpHeaders.SET_COOKIE,
					String.format("%s; %s", header, "SameSite=None; Secure"));
				firstHeader = false;
				continue;
			}
			response.addHeader(HttpHeaders.SET_COOKIE,
				String.format("%s; %s", header, "SameSite=None; Secure"));
		}
	}

	/**
	 * Processes the first phase of authentication success.
	 * @param request The {@link HttpServletRequest} instance.
	 * @param response The {@link HttpServletResponse} instance.
	 * @param authentication The {@link Authentication} instance.
	 * @throws IOException Thrown if an error occurs.
	 */
	protected void handle(HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication)
		throws IOException
	{
		String targetUrl = determineTargetUrl(authentication);

		if (response.isCommitted())
		{
			TSLogger.debug(this.getClass(),
				"Response has already been committed. Unable to redirect to #0",
				targetUrl);
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}


	/**
	 * Determines the target URL based on the logged in user.
	 * @param authentication The {@link Authentication} instance.
	 * @return The URI for the target URL.
	 */
	protected String determineTargetUrl(Authentication authentication)
	{
		boolean isUser = false;
		boolean isAdmin = false;
		Collection<? extends GrantedAuthority> authorities =
			authentication.getAuthorities();

		for (GrantedAuthority grantedAuthority : authorities)
		{
			if (grantedAuthority.getAuthority().equals("ROLE_USER"))
			{
				isUser = true;
				break;
			}
			else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
			{
				isAdmin = true;
				break;
			}
		}

		String targetURL = "/";

//		if (isUser)
//		{
//			targetURL = "/";
//		}
//		else if (isAdmin)
//		{
//			targetURL = "/console";
//		}
//		else
//		{
//			throw new IllegalStateException();
//		}

		return targetURL;
	}


	/**
	 * Clears authentication attribute.
	 * @param request The {@link HttpServletRequest}.
	 */
	protected void clearAuthenticationAttributes(HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);

		if (session == null)
		{
			return;
		}

		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}


	/**
	 * This method sets the redirect strategy.
	 * @param redirectStrategy The redirect strategy.
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy)
	{
		this.redirectStrategy = redirectStrategy;
	}


	/**
	 * The method returns {@link RedirectStrategy} instance.
	 * @return The {@link RedirectStrategy} instance.
	 */
	protected RedirectStrategy getRedirectStrategy()
	{
		return redirectStrategy;
	}


	/**
	 * The {@link RedirectStrategy} field.
	 */
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Value("${spring.profiles.active:}")
	private String profiles;

	@Value("${server.ssl.enabled:false}")
	private boolean secure;
}
