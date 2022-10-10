package com.sixsprints.eclinic.service.user;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.dto.user.PatientDto;
import com.sixsprints.eclinic.enums.UserRole;
import com.sixsprints.eclinic.repository.user.PatientRepository;
import com.sixsprints.eclinic.service.user.abs.AbstractUserService;
import com.sixsprints.eclinic.util.ThreadContext;
import com.sixsprints.eclinic.util.csv.PatientFieldData;
import com.sixsprints.eclinic.util.transformer.PatientMapper;
import com.sixsprints.notification.service.NotificationService;

@Service("patient")
public class PatientServiceImpl extends AbstractUserService<Patient, PatientDto> implements PatientService {

  public PatientServiceImpl(PatientMapper mapper, @Qualifier("sms") NotificationService notificationService) {
    super(mapper, notificationService);
  }

  @Resource
  private PatientRepository patientRepository;

  @Override
  protected PatientRepository repository() {
    return patientRepository;
  }

  @Override
  protected MetaData<Patient> metaData() {
    return MetaData.<Patient>builder()
      .classType(Patient.class).dtoClassType(PatientDto.class).fields(PatientFieldData.fields())
      .build();
  }

  @Override
  protected void preCreate(Patient entity) {
    super.preCreate(entity);
    entity.setRoleName(UserRole.PATIENT.toString());
  }

  @Override
  public Patient update(String id, Patient domain) throws EntityNotFoundException, EntityAlreadyExistsException {
    Patient patient = findOne(id);

    patient.setName(domain.getName());
    patient.setEmail(domain.getEmail());
    patient.setDob(domain.getDob());
    patient.setGender(domain.getGender());
    patient.setAddress(domain.getAddress());

    return save(patient);
  }

  @Override
  public Patient generateUhid(String patientSlug) throws EntityNotFoundException {
    Patient patient = findBySlug(patientSlug);
    if (StringUtils.isBlank(patient.getUhid())) {
      patient.setUhid(uhid(patient.getSequence()));
      patient = save(patient);
    }
    return patient;
  }

  private String uhid(long sequence) {
    String prefix = ThreadContext.getCurrentTenant().getUhidPrefix();
    if (sequence < 10) {
      prefix += "0000";
    } else if (sequence < 100) {
      prefix += "000";
    } else if (sequence < 1000) {
      prefix += "00";
    } else if (sequence < 10000) {
      prefix += "0";
    }

    return prefix + sequence;
  }

  @Override
  protected Patient newUser(String mobileNumber) {
    return Patient.builder().mobileNumber(mobileNumber).build();
  }

  @Override
  public List<Patient> searchPatient() {
    return patientRepository.findByRoleName(UserRole.PATIENT);
  }

}
