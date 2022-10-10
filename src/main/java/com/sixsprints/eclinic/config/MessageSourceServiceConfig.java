package com.sixsprints.eclinic.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.sixsprints.core.service.MessageSourceService;
import com.sixsprints.eclinic.util.AppMessages;

@Component
public class MessageSourceServiceConfig implements MessageSourceService {

  @Autowired
  private MessageSource messageSource;

  @Override
  public MessageSource messageSource() {
    return messageSource;
  }

  @Override
  public String genericError() {
    return AppMessages.EXCEPTION_UNEXPECTED;
  }

  @Override
  public Locale defaultLocale() {
    return Locale.ENGLISH;
  }

}
