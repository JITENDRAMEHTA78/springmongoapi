package com.sixsprints.eclinic.util.transformer;

import javax.annotation.Resource;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.eclinic.domain.ChatSession;
import com.sixsprints.eclinic.dto.ChatSessionDto;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.user.PatientService;

@Mapper(componentModel = "spring")
public abstract class ChatSessionMapper extends GenericTransformer<ChatSession, ChatSessionDto> {

  @Resource
  private DoctorService doctorService;

  @Resource
  private DoctorMapper doctorMapper;

  @Resource
  private PatientService patientService;

  @Resource
  private PatientMapper patientMapper;

  @Override
  public abstract ChatSession toDomain(ChatSessionDto dto);

  @Override
  public abstract ChatSessionDto toDto(ChatSession domain);

  @AfterMapping
  protected void afterToDto(ChatSession domain, @MappingTarget ChatSessionDto.ChatSessionDtoBuilder builder) {
    try {
      builder.doctor(doctorMapper.toDto(doctorService.findBySlug(domain.getDoctorSlug())));
    } catch (EntityNotFoundException e) {
    }
    try {
      builder.patient(patientMapper.toDto(patientService.findBySlug(domain.getPatientSlug())));
    } catch (EntityNotFoundException e) {
    }
  }

}
