package com.sixsprints.eclinic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.domain.ChatPackage;
import com.sixsprints.eclinic.dto.ChatPackageDto;
import com.sixsprints.eclinic.repository.ChatPackageRepository;
import com.sixsprints.eclinic.service.ChatPackageService;
import com.sixsprints.eclinic.util.csv.ChatPackageFieldData;

@Service
public class ChatPackageServiceImpl extends AbstractCrudService<ChatPackage> implements ChatPackageService {

  @Autowired
  private ChatPackageRepository chatPackageRepository;

  @Override
  protected GenericRepository<ChatPackage> repository() {
    return chatPackageRepository;
  }

  @Override
  protected MetaData<ChatPackage> metaData() {
    return MetaData.<ChatPackage>builder()
      .classType(ChatPackage.class).dtoClassType(ChatPackageDto.class)
      .fields(ChatPackageFieldData.fields()).build();
  }

  @Override
  protected ChatPackage findDuplicate(ChatPackage entity) {
    return chatPackageRepository.findBySlug(entity.getSlug());
  }

  @Override
  public List<ChatPackage> findByDoctorSlug(String doctorSlug) {
    return chatPackageRepository.findByDoctorSlug(doctorSlug);
  }

}
