package com.sixsprints.eclinic.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.EmrTemplate;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.EmrTemplateDto;
import com.sixsprints.eclinic.service.EmrTemplateService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.EmrTemplateMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/emr-template")
public class EmrTemplateController extends AbstractCrudController<EmrTemplate, EmrTemplateDto, User> {

  private EmrTemplateService service;

  private EmrTemplateMapper mapper;

  public EmrTemplateController(EmrTemplateService service, EmrTemplateMapper mapper) {
    super(service, mapper);
    this.service = service;
    this.mapper = mapper;
  }

  /**
   * Please note it is taking Database id as the input.
   * 
   * @param user
   * @param id
   * @param dto
   * @return
   * @throws EntityNotFoundException
   * @throws EntityAlreadyExistsException
   */
  @PutMapping("/{id}")
  public ResponseEntity<RestResponse<EmrTemplateDto>> edit(@Authenticated User user, @PathVariable String id,
    @RequestBody EmrTemplateDto dto) throws EntityNotFoundException, EntityAlreadyExistsException {
    EmrTemplate update = service.update(id, mapper.toDomain(dto));
    return RestUtil.successResponse(mapper.toDto(update));
  }

}