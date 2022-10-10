package com.sixsprints.eclinic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.utils.RestUtil;

@RestController
public class IndexController {

  @GetMapping(value = { "", "/", "/api/v1", "/api/v1/" })
  public ResponseEntity<?> index() {
    return RestUtil.successResponse(null);
  }

}
