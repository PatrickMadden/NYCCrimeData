//
// Tom Sawyer Software
// Copyright 2007 - 2019
// All rights reserved.
//
// www.tomsawyer.com
//


package com.tomsawyer.nypd.compstat.security;


import com.tomsawyer.util.logging.TSLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


/**
 * The security configuration for the application.
 */
@Configuration
@EnableWebSecurity
@EnableAutoConfiguration
public class AppSecurityConfiguration extends WebSecurityConfigurerAdapter
{
	public AppSecurityConfiguration()
	{
		TSLogger.info(this.getClass(), "AppSecurityConfiguration");
	}


	/**
	 * Defines the password encoder used by the application.
	 *
	 * @return The password encoder used by the application.
	 */
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}


	/**
	 * Temporary default simple user details service.
	 *
	 * @return The user details service.
	 * @throws Exception Throws an error if required.
	 */
	@Bean
	public UserDetailsService inMemoryUserDetailsService() throws Exception
	{
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

		manager.createUser(User.withUsername("user").password(passwordEncoder().
			encode("password")).roles("USER").build());

		manager.createUser(User.withUsername("admin").password(passwordEncoder().
			encode("admin")).roles("ADMIN").build());

		return manager;
	}


	/**
	 * This method returns the authentication success handler.
	 *
	 * @return The {@link AuthenticationSuccessHandler} instance for this application.
	 */
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler()
	{
		return new AppAuthenticationSuccessHandler();
	}


	/**
	 * Sets up the authentication service to use our account service and password encoder.
	 * @param auth The {@link AuthenticationManagerBuilder} instance.
	 * @throws Exception Thrown if an error occurs.
	 */
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.userDetailsService(this.inMemoryUserDetailsService()).
			passwordEncoder(this.passwordEncoder());
	}


	/**
	 * This method returns the {@link AppAccessDeniedHandler} bean, which is an
	 * extension of Spring's default
	 * {@link org.springframework.security.web.access.AccessDeniedHandlerImpl}.
	 *
	 * @return The {@link AccessDeniedHandler} bean.
	 */
	@Bean
	public AccessDeniedHandler accessDeniedHandler()
	{
		return new AppAccessDeniedHandler();
	}


	/**
	 * Configures the {@link HttpSecurity} instance.
	 *
	 * @param httpSecurity The {@link HttpSecurity} instance to configure.
	 * @throws Exception Thrown if an error occurs configuring security.
	 */
	protected void configure(HttpSecurity httpSecurity) throws Exception
	{
		// This needs work but the paths in the antMatchers will be allowed through
		// without authentication.

		httpSecurity.
			authorizeRequests().
			antMatchers(

				//Resources
				"/images/**",
				"/javascript/**",
				"/style/**",
				"/webjars/**",
				"/",
				"/signup",
				"/actuator/**").
			permitAll().
			anyRequest().
			authenticated().
			and().
			formLogin().
			loginPage("/Security/SignIn").permitAll().
			loginProcessingUrl("/Security/SignIn").
			failureUrl("/Security/SignIn?failed=true").successHandler(
				this.authenticationSuccessHandler()).
			and().
			logout().
			logoutUrl("/Security/Logout").permitAll().
			logoutSuccessUrl("/Security/SignIn").
			deleteCookies("JSESSIONID").
			and().
			exceptionHandling().
			accessDeniedPage("/Security/AccessDenied").
			accessDeniedHandler(this.accessDeniedHandler()).
// 			and().
// 			httpBasic().
			and().
			csrf().ignoringAntMatchers(
				"/tsperspectives/*").and().
			headers().frameOptions().disable();
	}


//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http
//			.authorizeRequests()
//			.anyRequest().fullyAuthenticated()
//			.and()
//			.formLogin();
//	}
//
//	@Override
//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth
//			.ldapAuthentication()
//			.userDnPatterns("uid={0},ou=Personnel")
////			.groupSearchBase("ou=Personnel")
//			.contextSource()
//			.url("ldaps://ldap.jpl.nasa.gov:636/" +
//				"ou=Personnel,dc=dir,dc=jpl,dc=nasa,dc=gov?uid")
//			.and()
//			.passwordCompare()
//			.passwordEncoder(new LdapShaPasswordEncoder())
//			.passwordAttribute("userPassword");
//	}
}


