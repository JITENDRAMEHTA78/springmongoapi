package com.sixsprints.eclinic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.sixsprints.core.config.ParentBeans;
import com.sixsprints.eclinic.api.PdfApi;
import com.sixsprints.json.util.ApiFactory;
import com.sixsprints.notification.service.URLShorteningService;
import com.sixsprints.notification.service.impl.URLShorteningServiceImpl;

@Configuration
public class Beans extends ParentBeans {

  @Value("${pdf.api.base.url}")
  private String pdfApiBaseUrl;

  @Value("${url.shorten.api.key}")
  private String urlShortenAPIKey;

  @Override
  protected String defaultShortDateFormat() {
    return "dd MMM YYYY";
  }

  @Bean
  public PdfApi pdfApi() {
    return ApiFactory.create(PdfApi.class, pdfApiBaseUrl, mapper());
  }

  @Bean
  public URLShorteningService urlShorteningService() {
    return new URLShorteningServiceImpl(urlShortenAPIKey);
  }

  @Bean
  public SpringTemplateEngine templateEngine() {
    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    templateResolver.setPrefix("templates/");
    templateResolver.setCacheable(false);
    templateResolver.setSuffix(".html");
    templateResolver.setTemplateMode("HTML5");
    templateEngine.setTemplateResolver(templateResolver);
    return templateEngine;
  }

}
