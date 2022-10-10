package com.sixsprints.eclinic.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.domain.EmrTemplate;
import com.sixsprints.eclinic.dto.EmrTemplateDto;
import com.sixsprints.eclinic.repository.EmrTemplateRepository;
import com.sixsprints.eclinic.service.EmrTemplateService;
import com.sixsprints.eclinic.util.ThreadContext;
import com.sixsprints.eclinic.util.csv.EmrTemplateFieldData;

@Service
public class EmrTemplateServiceImpl extends AbstractCrudService<EmrTemplate> implements EmrTemplateService {

  @Autowired
  private EmrTemplateRepository emrTemplateRepository;

  @Override
  protected EmrTemplateRepository repository() {
    return emrTemplateRepository;
  }

  @Override
  protected MetaData<EmrTemplate> metaData() {
    return MetaData.<EmrTemplate>builder()
      .classType(EmrTemplate.class)
      .dtoClassType(EmrTemplateDto.class)
      .defaultSort(Sort.by(Direction.ASC, "name"))
      .fields(EmrTemplateFieldData.fields())
      .build();
  }

  @Override
  protected EmrTemplate findDuplicate(EmrTemplate entity) {
    return emrTemplateRepository.findByNameAndTenantId(entity.getName(), ThreadContext.getCurrentTenant().getId());
  }

}
