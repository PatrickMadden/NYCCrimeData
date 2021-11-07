//
// Tom Sawyer Software
// Copyright 2007 - 2021
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.web;


import com.tomsawyer.licensing.server.TSLicensingFilter;
import com.tomsawyer.licensing.server.TSLicensingServiceImpl;
import com.tomsawyer.util.server.cache.TSCacheFilter;
import com.tomsawyer.util.server.logging.TSLoggingServiceImpl;
import com.tomsawyer.util.server.xsrf.TSXsrfTokenServiceServlet;
import com.tomsawyer.visualization.gwt.server.TSGetImageSourceServlet;
import com.tomsawyer.visualization.gwt.server.TSVisualizationServiceImpl;
import com.tomsawyer.web.server.TSSessionCleanupListener;
import com.tomsawyer.web.server.TSWebViewServiceImpl;
import com.tomsawyer.web.server.fileupload.TSFileUploadServlet;
import com.tomsawyer.web.server.map.marker.TSGetMapMarkerImageServlet;
import com.tomsawyer.web.server.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionListener;


@Configuration
public class PerspectivesWebContextConfiguration
{
	/**
	 * This method returns a {@link ServletContextListener} that bootstraps the
	 * Perspectives system inside a servlet container.
	 *
	 * @return A {@link ServletContextListener} that bootstraps the Perspectives system
	 * inside a servlet container.
	 */
	@Bean
	public ServletContextListener servletContainerBootstrapListener()
	{
		return new ServletContainerBootstrap();
	}


	/**
	 * This method returns a {@link HttpSessionListener} that handles the session cleanup.
	 *
	 * @return A {@link HttpSessionListener} that handles the session cleanup.
	 */

	@Bean
	public HttpSessionListener sessionCleanupListener()
	{
		return new TSSessionCleanupListener();
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the
	 * {@link TSXsrfTokenServiceServlet} and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the
	 * {@link TSXsrfTokenServiceServlet} and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> xsrfServletRegistrationBean()
	{
		ServletRegistrationBean<TSXsrfTokenServiceServlet> registration =
			new ServletRegistrationBean<>(new TSXsrfTokenServiceServlet());

		registration.addUrlMappings("/tsperspectives/xsrf/*");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the {@link TSWebViewServiceImpl}
	 * servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the {@link TSWebViewServiceImpl}
	 * servlet and required mappings.
	 *
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> webViewServlet()
	{
		ServletRegistrationBean<TSWebViewServiceImpl> registration =
			new ServletRegistrationBean<>(new TSWebViewServiceImpl());

		registration.addUrlMappings("/tsperspectives/tswebview/*");

		registration.addInitParameter(
			"com.tomsawyer.util.server.xsrf.TSXsrfTokenProvider",
			com.tomsawyer.nypd.compstat.security.
				PerspectivesSecurityTokenProvider.class.getName());


		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the {@link TSGetImageServlet}
	 * servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the {@link TSGetImageServlet}
	 * servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> imageMapServlet()
	{
		ServletRegistrationBean<TSGetImageServlet> registration =
			new ServletRegistrationBean<>(new TSGetImageServlet());

		registration.addUrlMappings("/tsperspectives/TSGetImage");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the
	 * {@link TSGetMapMarkerImageServlet} servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the
	 * {@link TSGetMapMarkerImageServlet} servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> mapMarkerServlet()
	{
		ServletRegistrationBean<TSGetMapMarkerImageServlet> registration =
			new ServletRegistrationBean<>(new TSGetMapMarkerImageServlet());

		registration.addUrlMappings("/tsperspectives/TSGetMapMarkerImage");
		registration.addInitParameter("markerImagesFolder",
			"/tsperspectives/images/marker");
		registration.addInitParameter("shadowImagesFolder",
			"/tsperspectives/images/shadow");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the {@link TSSaveImageServlet}
	 * servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the {@link TSSaveImageServlet}
	 * servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> saveImageServlet()
	{
		ServletRegistrationBean<TSSaveImageServlet> registration =
			new ServletRegistrationBean<>(new TSSaveImageServlet());

		registration.addUrlMappings("/tsperspectives/TSSaveImage/*");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the {@link TSExportServlet}
	 * servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the {@link TSExportServlet}
	 * servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> exportToTSVServlet()
	{
		ServletRegistrationBean<TSExportServlet> registration =
			new ServletRegistrationBean<>(new TSExportServlet());

		registration.addUrlMappings("/tsperspectives/TSExportToTSV/*");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the {@link TSExportToExcelServlet}
	 * servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the {@link TSExportToExcelServlet}
	 * servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> exportToExcelServlet()
	{
		ServletRegistrationBean<TSExportToExcelServlet> registration =
			new ServletRegistrationBean<>(new TSExportToExcelServlet());

		registration.addUrlMappings("/tsperspectives/TSExportToExcel/*");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the
	 * {@link TSGetPrintPreviewServlet} servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the
	 * {@link TSGetPrintPreviewServlet} servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> printPreviewServlet()
	{
		ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<>(
			new TSGetPrintPreviewServlet());
		registration.addUrlMappings("/tsperspectives/TSGetPrintPreview/*");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the {@link TSLicensingServiceImpl}
	 * servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the {@link TSLicensingServiceImpl}
	 * servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> licensingServlet()
	{
		ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<>(
			new TSLicensingServiceImpl());
		registration.addUrlMappings("/tsperspectives/TSLicensingService/*");

		registration.addInitParameter(
			"com.tomsawyer.util.server.xsrf.TSXsrfTokenProvider",
			com.tomsawyer.nypd.compstat.security.
				PerspectivesSecurityTokenProvider.class.getName());

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the {@link TSFileUploadServlet}
	 * servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the {@link TSFileUploadServlet}
	 * servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> fileUploadServlet()
	{
		ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<>(
			new TSFileUploadServlet());
		registration.addUrlMappings("/tsperspectives/tsfileupload/*");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the
	 * {@link TSVisualizationServiceImpl} servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the
	 * {@link TSVisualizationServiceImpl} servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> visualizationServlet()
	{
		ServletRegistrationBean<TSVisualizationServiceImpl> registration =
			new ServletRegistrationBean<>(new TSVisualizationServiceImpl());

		registration.addUrlMappings("/tsperspectives/TSVisualizationService/*");

		registration.addInitParameter(
			"com.tomsawyer.util.server.xsrf.TSXsrfTokenProvider",
			com.tomsawyer.nypd.compstat.security.
				PerspectivesSecurityTokenProvider.class.getName());

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the
	 * {@link TSGetImageSourceServlet} servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the
	 * {@link TSGetImageSourceServlet} servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> imageSourceServlet()
	{
		ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<>(
			new TSGetImageSourceServlet());
		registration.addUrlMappings("/tsperspectives/TSGetImageSource/*");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the
	 * {@link TSHeartBeatServlet} servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the
	 * {@link TSHeartBeatServlet} servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> heartBeatServlet()
	{
		ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<>(
			new TSHeartBeatServlet());
		registration.addUrlMappings("/tsheartbeat/*");
		registration.setLoadOnStartup(1);

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the
	 * {@link TSRemoteLoggingServlet} servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the
	 * {@link TSRemoteLoggingServlet} servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> remoteLoggingServlet()
	{
		ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<>(
			new TSRemoteLoggingServlet());
		registration.addUrlMappings("/tsperspectives/remote_logging/*");

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link ServletRegistrationBean} for the
	 * {@link TSLoggingServiceImpl} servlet and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the
	 * {@link TSLoggingServiceImpl} servlet and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Servlet> ServletRegistrationBean<T> loggingServlet()
	{
		ServletRegistrationBean<TSLoggingServiceImpl> registration =
			new ServletRegistrationBean<>(new TSLoggingServiceImpl());

		registration.addUrlMappings("/tsperspectives/TSLoggingService/*");

		registration.addInitParameter(
			"com.tomsawyer.util.server.xsrf.TSXsrfTokenProvider",
			com.tomsawyer.nypd.compstat.security.
				PerspectivesSecurityTokenProvider.class.getName());

		return (ServletRegistrationBean<T>) registration;
	}


	/**
	 * Creates the {@link FilterRegistrationBean} for the {@link TSCacheFilter} filter
	 * and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the {@link TSCacheFilter} filter
	 * and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Filter> FilterRegistrationBean<T> cacheFilterRegistrationBean()
	{
		FilterRegistrationBean<T> registrationBean = new FilterRegistrationBean<>();
		TSCacheFilter filter = new TSCacheFilter();
		registrationBean.setFilter((T) filter);
		List<String> urlPatterns = new ArrayList<>(1);
		urlPatterns.add("/tsperspectives/*");
		registrationBean.setUrlPatterns(urlPatterns);
		registrationBean.setOrder(1);
		return registrationBean;
	}


	/**
	 * Creates the {@link FilterRegistrationBean} for the {@link TSLicensingFilter} filter
	 * and required mappings.
	 *
	 * @return The {@link ServletRegistrationBean} for the {@link TSLicensingFilter}
	 * filter and required mappings.
	 * @param <T> The type extending Servlet.
	 */
	@SuppressWarnings("unchecked")
	@Bean
	public <T extends Filter> FilterRegistrationBean<T> licensingFilterRegistrationBean()
	{
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
		TSLicensingFilter filter = new TSLicensingFilter();
		registrationBean.setFilter(filter);
		List<String> urlPatterns = new ArrayList<>(4);
		urlPatterns.add("/tsperspectives/tswebview");
		urlPatterns.add("/tsperspectives/TSGetImage");
		urlPatterns.add("/tsperspectives/TSGetPrintPreview");
		urlPatterns.add("/tsperspectives/tsfileupload");
		registrationBean.setUrlPatterns(urlPatterns);
		registrationBean.setOrder(2);

		return (FilterRegistrationBean<T>) registrationBean;
	}


	/**
	 * Injected {@link WebApplicationContext} field.
	 */
	@Autowired
	WebApplicationContext webApplicationContext;
}
