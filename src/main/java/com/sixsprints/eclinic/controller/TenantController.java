package com.sixsprints.eclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.dto.TenantDto;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.util.DataBuilderService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.ThreadContext;
import com.sixsprints.eclinic.util.transformer.DoctorMapper;

@RestController
@RequestMapping(Constants.API_PREFIX + "/tenant")
@CrossOrigin
public class TenantController {

  @Autowired
  private DataBuilderService dataBuilderService;
	
  @Autowired
  private DoctorService doctorService;

  @Autowired
  private DoctorMapper doctorMapper;

  @GetMapping
  public ResponseEntity<RestResponse<TenantDto>> tenantDetails(@RequestParam(required = false, defaultValue = "raman") String doctorWebsite) {
    TenantDto tenant = ThreadContext.getCurrentTenant();
    String actualTenant = ThreadContext.getActualTenant();
    if (actualTenant.contains("localhost") || actualTenant.contains("nixet")) {
      actualTenant = "raman";
    }
    System.out.print(doctorWebsite);
    if (doctorWebsite != "raman") {
      actualTenant = doctorWebsite;
    }
    Doctor doctor = doctorService.findByWebsiteKey(actualTenant);
    if (doctor != null) {
      tenant.setSelectedDoctor(doctorMapper.toDto(doctor));
    } else {
      tenant.setSelectedDoctor(null);
    }
    return RestUtil.successResponse(tenant);
  }
  
  @GetMapping("/addLabAndQ")
  public void addLabAndQData() throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
	  dataBuilderService.initLabAndQUser();
  }

}
