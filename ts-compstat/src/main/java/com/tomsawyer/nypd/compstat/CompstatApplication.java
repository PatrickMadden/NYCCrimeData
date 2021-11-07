package com.tomsawyer.nypd.compstat;

import com.tomsawyer.licensing.TSLicenseManager;
import com.tomsawyer.licensing.TSSystemUserID;
import com.tomsawyer.licensing.spring.TSLicensingSpringConfiguration;
import com.tomsawyer.platform.TSPlatformConfig;
import com.tomsawyer.util.logging.TSLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import java.util.Locale;

import nz.net.ultraq.thymeleaf.LayoutDialect;


@SpringBootApplication
@Import({TSPlatformConfig.class})
public class CompstatApplication implements WebMvcConfigurer
{
	@Autowired
	public CompstatApplication(
		TSLicensingSpringConfiguration licensingConfigurationSpring)
	{
		licensingConfigurationSpring.loadLicensingPropertiesFromSpring();

		TSLicenseManager.setUserName(new TSSystemUserID().getName());
		TSLicenseManager.initTSSLicensing();
	}


	/**
	 * Creates a {@link SessionLocaleResolver} bean, which uses the locale attribute in
	 * the user's session to set the locale, and defaults to en_US.
	 *
	 * @return A {@link SessionLocaleResolver} bean.
	 */
	@Bean
	public LocaleResolver localeResolver()
	{
		SessionLocaleResolver slr = new SessionLocaleResolver();
		slr.setDefaultLocale(Locale.US);
		return slr;
	}


	/**
	 * Creates a {@link LocaleChangeInterceptor} bean that allows for changing the current
	 * locale using the {@code lang} request parameter.
	 *
	 * @return A {@link LocaleChangeInterceptor} bean.
	 */
	@Bean
	public LocaleChangeInterceptor getLocaleChangeInterceptor()
	{
		LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}


	/**
	 * Adds Spring MVC lifecycle interceptors for pre- and post-processing of controller
	 * method invocations.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(this.getLocaleChangeInterceptor());
	}


	/**
	 * Context closed event.
	 *
	 * @param event The event.
	 */
	@EventListener
	public void handleContextClosed(ContextClosedEvent event)
	{
		TSLogger.info(this.getClass(),
			"#0 context closed.",
			this.applicationName);

		TSLicenseManager.shutdown();
	}


	/**
	 * Context stopped event.
	 *
	 * @param event The event.
	 */
	@EventListener
	public void handleContextStopped(ContextStoppedEvent event)
	{
		TSLogger.info(this.getClass(),
			"#0 context stopped.",
			this.applicationName);
	}


	/**
	 * Context started event.
	 *
	 * @param event The event.
	 */
	@EventListener
	public void handleContextStarted(ContextStartedEvent event)
	{
		TSLogger.info(this.getClass(),
			"#0 context started.",
			this.applicationName);
	}


	/**
	 * Setup conversion service.
	 *
	 * @return A {@link DefaultFormattingConversionService} instance.
	 */
	@Bean
	public DefaultFormattingConversionService defaultConversionService()
	{
		DefaultFormattingConversionService conversionService =
			new DefaultFormattingConversionService();

//		// Add a domain class converter. This allows our Tom Sawyer model elements to be
//		// converted on the fly based on their ID in contoller methods.
//
//		DomainClassConverter<FormattingConversionService> converter =
//			new DomainClassConverter<FormattingConversionService>(conversionService);
//		converter.setApplicationContext(this.applicationContext);

		// can also register date format conversion here etc.

		return conversionService;
	}


	/**
	 * Configures the Thymeleaf view resolver.
	 *
	 * @return The {@link ThymeleafViewResolver} instance.
	 */
	@Bean
	public ViewResolver viewResolver()
	{
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setOrder(2147483642);
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");

		return resolver;
	}


	/**
	 * Configures the Spring template engine.
	 *
	 * @return The {@link SpringTemplateEngine} instance.
	 */
	@Bean
	@Primary
	public SpringTemplateEngine templateEngine()
	{
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();

		// we add the classloader template resolver to search the classpath
		// for templates and fragments in addition to main web application.

		templateEngine.addTemplateResolver(classloaderTemplateResolver());
		templateEngine.addTemplateResolver(resourceTemplateResolver());

		// Enabling the SpringEL compiler with Spring 4.2.4 or newer can
		// speed up execution in most scenarios, but might be incompatible
		// with specific cases when expressions in one template are reused
		// across different data types, so this flag is "false" by default
		// for safer backwards compatibility.

		templateEngine.setEnableSpringELCompiler(true);

		templateEngine.addDialect(new LayoutDialect());
		//templateEngine.addDialect(new SpringSecurityDialect());

		return templateEngine;
	}


	/**
	 * Configures the Spring resource template resolver.
	 *
	 * @return The {@link SpringResourceTemplateResolver} instance.
	 */
	protected ITemplateResolver resourceTemplateResolver()
	{
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();

		resolver.setOrder(2);
		resolver.setApplicationContext(this.applicationContext);
		resolver.setPrefix(this.thymeleafProperties.getPrefix());
		resolver.setSuffix(this.thymeleafProperties.getSuffix());
		resolver.setTemplateMode(this.thymeleafProperties.getMode());
		resolver.setCacheable(this.thymeleafProperties.isCache());

		TSLogger.info(this.getClass(),
			"Initialized #0 with prefix=#1, suffix=#2 and mode=#3.",
			resolver.getClass().getSimpleName(),
			resolver.getPrefix(),
			resolver.getSuffix(),
			resolver.getTemplateMode());

		return resolver;
	}


	/**
	 * Configures the class loader resource template resolver.
	 *
	 * @return The {@link ClassLoaderTemplateResolver} instance.
	 */
	protected ITemplateResolver classloaderTemplateResolver()
	{
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();

		resolver.setOrder(1);
		resolver.setPrefix(this.thymeleafProperties.getPrefix());
		resolver.setSuffix(this.thymeleafProperties.getSuffix());
		resolver.setTemplateMode(this.thymeleafProperties.getMode());
		resolver.setCacheable(this.thymeleafProperties.isCache());

		//resolver.getResolvablePatternSpec().addPattern("/templates/*");

		resolver.getResolvablePatternSpec().addPattern("templates/*");

		TSLogger.info(this.getClass(),
			"Initialized #0 with prefix=#1, suffix=#2 and mode=#3.",
			resolver.getClass().getSimpleName(),
			resolver.getPrefix(),
			resolver.getSuffix(),
			resolver.getTemplateMode());

		return resolver;
	}


	public static void main(String[] args)
	{
		SpringApplication.run(CompstatApplication.class, args);
	}


	/**
	 * Injected {@link WebApplicationContext} field.
	 */
	@Autowired
	WebApplicationContext applicationContext;


	/**
	 * Thymeleaf properties field.
	 */
	@Autowired
	private ThymeleafProperties thymeleafProperties;


	/**
	 * The application name field.
	 */
	@Value("${spring.application.name}")
	private String applicationName;


	/**
	 * The Spring Environment.
	 */
	@Autowired
	Environment environment;
}
