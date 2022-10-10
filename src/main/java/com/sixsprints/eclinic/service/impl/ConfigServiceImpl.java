package com.sixsprints.eclinic.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.javers.common.collections.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.eclinic.domain.Config;
import com.sixsprints.eclinic.domain.HospitalVitals;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.AppointmentTypeDto;
import com.sixsprints.eclinic.dto.VitalsDto;
import com.sixsprints.eclinic.enums.AppointmentStatus;
import com.sixsprints.eclinic.enums.MasterType;
import com.sixsprints.eclinic.repository.ConfigRepository;
import com.sixsprints.eclinic.service.ConfigService;
import com.sixsprints.eclinic.service.HospitalVitalsService;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.user.UserService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.PaymentOptionDataBuilder;
import com.sixsprints.eclinic.util.ThreadContext;
import com.sixsprints.eclinic.util.csv.AppointmentFieldData;
import com.sixsprints.eclinic.util.csv.ChatPackageFieldData;
import com.sixsprints.eclinic.util.csv.DoctorFieldData;
import com.sixsprints.eclinic.util.csv.EmrTemplateFieldData;
import com.sixsprints.eclinic.util.csv.MedicationFieldData;
import com.sixsprints.eclinic.util.csv.OrderFieldData;
import com.sixsprints.eclinic.util.csv.PatientFieldData;

@Service
public class ConfigServiceImpl extends AbstractCrudService<Config> implements ConfigService {

  @Autowired
  private ConfigRepository configRepository;

  @Autowired
  private HospitalVitalsService hospitalVitalsService;
  
  @Resource
  private DoctorService doctorService;

  @Resource
  private UserService userService;
  
  @Autowired
  private PaymentOptionDataBuilder paymentOptionDataBuilder;

  @Override
  public Config get() {
    return get(Constants.DEFAULT_LOCALE);
  }

  @Override
  public Config get(Locale locale) {
    Config config = getConfigFromDb();
    config.setVitals(vitals());
    config.setCoMorbidities(coMorbidities());
    return localize(config, locale);
  }

  public void initAppointmentTypeAndActions() throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
	  List<Config> configs = findAll();
	  if (!configs.isEmpty()) {
		  List<AppointmentStatus> appointmentStatus = configs.get(0).getAppointmentStatus();
		   List<AppointmentStatus> status = ImmutableList.<AppointmentStatus>of(AppointmentStatus.CHECKED_IN,
				      AppointmentStatus.NO_SHOW, AppointmentStatus.CANCELLED, AppointmentStatus.REPORT_READY,AppointmentStatus.AWAITING_REPORT,AppointmentStatus.RECORDING_VITALS,
				      AppointmentStatus.NEEDS_ATTENTION);
		  configs.get(0).setAppointmentStatus(status);  
		  update(configs.get(0).getId(), configs.get(0));
	  }
	  
	  //adding VEDIo type options
	 List<User> users =  userService.findAll();
	 for (User user : users) {
		 if(user.getRoleName() != null) {
			 if (user.getRoleName().equals("DOCTOR")) {
				 Doctor doctor = doctorService.findBySlug(user.getSlug());
				 if (doctor.getAppointmentType() != null) {
					 if (!doctor.getAppointmentType().contains(paymentOptionDataBuilder.appointmentTypeOptions())) {
						 doctor.getAppointmentType().add(paymentOptionDataBuilder.appointmentTypeOptions());
						 doctorService.update(doctor.getId(), doctor);
					 }
				 }else {
					 List<AppointmentTypeDto> type = new ArrayList<>();
					 type.add(paymentOptionDataBuilder.appointmentTypeOptions());
					 doctor.setAppointmentType(type);
					 doctorService.update(doctor.getId(), doctor);
				 }
				
			 } 
		 }
	 }
	  
  }
  
  private Config getConfigFromDb() {
    List<Config> configs = findAll();
    if (configs.isEmpty()) {
      return init();
    }
    return configs.get(0);
  }

  private Config localize(Config config, Locale locale) {
    config.setSelectedLocale(locale);
    localiseStrings(locale, config.getChatPackageField());
    localiseStrings(locale, config.getOrderField());
    localiseStrings(locale, config.getPatientField());
    localiseStrings(locale, config.getDoctorField());
    localiseStrings(locale, config.getEmrTemplateFields());
    localiseStrings(locale, config.getAppointmentField());
    return config;
  }

  private void localiseStrings(Locale locale, List<FieldDto> fields) {
    if (fields != null && !fields.isEmpty()) {
      for (FieldDto dto : fields) {
        String displayName = dto.getLocalizedDisplay().get(locale);
        if (StringUtils.isNotBlank(displayName)) {
          dto.setDisplayName(displayName);
        } else {
          dto.setDisplayName(dto.getLocalizedDisplay().get(Locale.ENGLISH));
        }
        dto.setLocalizedDisplay(null);
      }
    }
  }

  private Config init() {
    List<FieldDto> chatPackageFieldData = chatPackageFieldData();
    List<FieldDto> orderFieldData = orderFieldData();
    List<FieldDto> patientFieldData = patientFieldData();
    List<FieldDto> doctorFieldData = doctorFieldData();
    List<FieldDto> medicationFieldData = medicationFieldData();
    List<FieldDto> appointmentFieldData = appointmentFieldData();
    
    List<AppointmentStatus> status = ImmutableList.<AppointmentStatus>of(AppointmentStatus.CHECKED_IN,
      AppointmentStatus.NO_SHOW, AppointmentStatus.CANCELLED, AppointmentStatus.REPORT_READY,AppointmentStatus.AWAITING_REPORT,AppointmentStatus.RECORDING_VITALS,
      AppointmentStatus.NEEDS_ATTENTION);
    
    Config config = Config.builder().chatPackageField(chatPackageFieldData)
      .orderField(orderFieldData).patientField(patientFieldData).doctorField(doctorFieldData)
      .appointmentStatus(status).medicationField(medicationFieldData)
      .emrTemplateFields(EmrTemplateFieldData.fields())
      .appointmentField(appointmentFieldData)
      .build();
    return save(config);
  }

  private List<FieldDto> appointmentFieldData() {
    return AppointmentFieldData.fields();
  }

  private List<FieldDto> chatPackageFieldData() {
    return ChatPackageFieldData.fields();
  }

  private List<FieldDto> orderFieldData() {
    return OrderFieldData.fields();
  }

  private List<FieldDto> patientFieldData() {
    return PatientFieldData.fields();
  }

  private List<FieldDto> doctorFieldData() {
    return DoctorFieldData.field();
  }

  private List<FieldDto> medicationFieldData() {
    return MedicationFieldData.field();
  }

  private List<VitalsDto> vitals() {
    HospitalVitals hospitalVitals = hospitalVitalsService
      .findByTenantIdAndType(ThreadContext.getCurrentTenant().getId(), MasterType.VITAL);
    if (hospitalVitals == null) {
      return null;
    }
    return hospitalVitals.getVitals();
  }

  private List<VitalsDto> coMorbidities() {
    HospitalVitals hospitalVitals = hospitalVitalsService
      .findByTenantIdAndType(ThreadContext.getCurrentTenant().getId(), MasterType.CO_MORBIDITY);
    if (hospitalVitals == null) {
      return null;
    }
    return hospitalVitals.getVitals();
  }

  @Override
  protected GenericRepository<Config> repository() {
    return configRepository;
  }

  @Override
  protected MetaData<Config> metaData() {
    return MetaData.<Config>builder().classType(Config.class).build();
  }

  @Override
  protected Config findDuplicate(Config entity) {
    return configRepository.findBySlug(entity.getSlug());
  }

}
