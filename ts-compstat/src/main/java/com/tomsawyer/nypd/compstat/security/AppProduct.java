//
// Tom Sawyer Software
// Copyright 2007 - 2019
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * A bean to query product information.
 */
@Component("AppProduct")
public class AppProduct
{
	/**
	 * This method gets the name of the product.
	 *
	 * @return The name of the product.
	 */
	public String getName()
	{
		return name;
	}


	/**
	 * This method sets the name of the product.
	 *
	 * @param name The name of the product.
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * This method gets the edition of the product.
	 *
	 * @return The edition of the product.
	 */
	public String getEdition()
	{
		return edition;
	}


	/**
	 * This method sets the edition of the product.
	 *
	 * @param edition The edition of the product.
	 */
	public void setEdition(String edition)
	{
		this.edition = edition;
	}


	/**
	 * This method gets the build string of the product.
	 *
	 * @return The build string of the product.
	 */
	public String getBuildString()
	{
		return buildString;
	}


	/**
	 * This method sets the buildString of the product.
	 *
	 * @param buildString The buildString of the product.
	 */
	public void setBuildString(String buildString)
	{
		this.buildString = buildString;
	}


	/**
	 * This method gets the release of the product.
	 *
	 * @return The release of the product.
	 */
	public String getRelease()
	{
		return release;
	}


	/**
	 * This method sets the release of the product.
	 *
	 * @param release The release of the product.
	 */
	public void setRelease(String release)
	{
		this.release = release;
	}


	/**
	 * This method gets the availablity of the product.
	 *
	 * @return The availability of the product.
	 */
	public String getAvailablity()
	{
		return availablity;
	}


	/**
	 * This method sets the availability of the product.
	 *
	 * @param availablity The availability of the product.
	 */
	public void setAvailablity(String availablity)
	{
		this.availablity = availablity;
	}


	/**
	 * This method gets the certification of the product.
	 *
	 * @return The certification of the product.
	 */
	public String getCertification()
	{
		return certification;
	}


	/**
	 * This method sets the certificatation of the product.
	 *
	 * @param certificatation The availability of the product.
	 */
	public void setCertification(String certificatation)
	{
		this.certification = certificatation;
	}


	/**
	 * The name field.
	 */
	@Value("${ts.product.name}")
	String name;

	/**
	 * The edition field.
	 */
	@Value("${ts.product.edition}")
	String edition;

	/**
	 * The buildString field.
	 */
	@Value("${ts.product.buildString}")
	String buildString;

	/**
	 * The release field.
	 */
	@Value("${ts.product.release}")
	String release;

	/**
	 * The name availablity.
	 */
	@Value("${ts.product.availablity}")
	String availablity;

	/**
	 * The name certification.
	 */
	@Value("${ts.product.certification}")
	String certification;
}
