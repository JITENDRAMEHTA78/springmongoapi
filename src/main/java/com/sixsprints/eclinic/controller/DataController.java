package com.sixsprints.eclinic.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.dto.SpecialityDto;
import com.sixsprints.eclinic.service.util.DataBuilderService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.initdata.SpecialityData;

@RestController
@RequestMapping(Constants.API_PREFIX + "/data")
public class DataController {

  @Resource
  private DataBuilderService databuilderService;

  @GetMapping("/init")
  public ResponseEntity<?> init() throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    databuilderService.init();
    return RestUtil.successResponse(null);
  }

  @GetMapping("/add-doc")
  public ResponseEntity<?> addDoc(@RequestParam String name, @RequestParam String mobile, @RequestParam String website,
    @RequestParam String regNo)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    return RestUtil.successResponse(databuilderService.addDoctor(name, mobile, website, regNo));
  }

  @GetMapping("/update")
  public ResponseEntity<?> updateDocData()
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    databuilderService.updateDoctorInfo();
    return RestUtil.successResponse(null);
  }

  @GetMapping("/specialities")
  public ResponseEntity<RestResponse<List<SpecialityDto>>> specialities() {
    return RestUtil.successResponse(SpecialityData.SPECIALITIES);
  }

  @GetMapping("/modify-speciality")
  public ResponseEntity<RestResponse<List<Doctor>>> modifySpeciality()
    throws EntityNotFoundException, EntityAlreadyExistsException {
    return RestUtil.successResponse(databuilderService.modifySpeciality());
  }
}
