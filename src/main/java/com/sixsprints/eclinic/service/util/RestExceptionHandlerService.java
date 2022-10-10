package com.sixsprints.eclinic.service.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.google.common.collect.ImmutableList;
import com.sixsprints.core.service.MessageSourceService;
import com.sixsprints.core.utils.RestExceptionHandler;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.util.AppMessages;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandlerService extends RestExceptionHandler {

  @Value("${spring.servlet.multipart.max-file-size}")
  private String maxSize;

  @Autowired
  private MessageSourceService messageSourceService;

  @Override
  protected MessageSourceService messageSourceService() {
    return messageSourceService;
  }

  @ExceptionHandler(value = { MaxUploadSizeExceededException.class })
  protected ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
    WebRequest request, Locale locale) {
    log.error(getErrorMessage(ex.getMessage()), ex);
    String errorMessage = getErrorMessage(AppMessages.Generic.FILE_SIZE_EXCEEDED,
      ImmutableList.of(maxSize), locale);
    return RestUtil.errorResponse(null, errorMessage, HttpStatus.BAD_REQUEST);
  }

}
