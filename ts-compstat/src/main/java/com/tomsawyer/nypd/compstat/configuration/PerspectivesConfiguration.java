//
// Tom Sawyer Software
// Copyright 2007 - 2019
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.configuration;


import com.tomsawyer.configuration.TSPerspectivesConfiguration;
import com.tomsawyer.web.server.configuration.TSPerspectivesWebConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;


/**
 * Overrides some Perspectives Web beans.
 */
@Configuration
@ImportResource({"classpath*:META-INF/TSPluginOverrides.xml"})
@Import({TSPerspectivesConfiguration.class, TSPerspectivesWebConfiguration.class})
public class PerspectivesConfiguration
{
	// place any overrides from PersectivesConfiguration here.
}
