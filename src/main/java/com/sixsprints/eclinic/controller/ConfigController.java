package com.sixsprints.eclinic.controller;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.domain.Config;
import com.sixsprints.eclinic.service.ConfigService;
import com.sixsprints.eclinic.util.Constants;

@RestController
@RequestMapping(Constants.API_PREFIX + "/config")
public class ConfigController {
  @Autowired
  private ConfigService configService;

  @GetMapping
  public ResponseEntity<RestResponse<Config>> getConfig(Locale locale) {
    return RestUtil.successResponse(configService.get(locale), "success", HttpStatus.OK);
  }
  
  @GetMapping("/addAppointmentTypeAndActions")
  public void addAppointmentTypeAndActions() throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
	  configService.initAppointmentTypeAndActions();
  }
}
