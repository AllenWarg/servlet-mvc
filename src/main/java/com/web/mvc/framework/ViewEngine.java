package com.web.mvc.framework;


import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

public class ViewEngine {
	private final TemplateEngine TemplateEngine ;
	private final ServletContext servletContext;

	public ViewEngine(ServletContext servletContext) {
		this.servletContext=servletContext;
		JavaxServletWebApplication application = JavaxServletWebApplication.buildApplication(servletContext);
		// Templates will be resolved as application (ServletContext) resources
		final WebApplicationTemplateResolver templateResolver =
				new WebApplicationTemplateResolver(application);

		// HTML is the default mode, but we will set it anyway for better understanding of code
		templateResolver.setTemplateMode(TemplateMode.HTML);
		// This will convert "home" to "/WEB-INF/templates/home.html"
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		// Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
		templateResolver.setCacheTTLMs(Long.valueOf(3600000L));

		// Cache is set to true by default. Set to false if you want templates to
		// be automatically updated when modified.
		templateResolver.setCacheable(false);
		templateResolver.setCharacterEncoding("UTF-8");
		final TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		this.TemplateEngine= templateEngine;
	}
	public void render(ModelAndView mv, Writer writer) throws IOException {
		TemplateSpec templateSpec = new TemplateSpec(mv.getView(), (String) null);
		Context context = new Context(Locale.CHINA);
		context.setVariables(mv.getModel());
		this.TemplateEngine.process(templateSpec,context,writer);
	}
}
