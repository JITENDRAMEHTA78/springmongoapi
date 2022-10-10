package com.sixsprints.eclinic.service.user;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.dto.AppointmentTypeDto;
import com.sixsprints.eclinic.dto.AvailSummaryDto;
import com.sixsprints.eclinic.dto.PaymentOptionDto;
import com.sixsprints.eclinic.dto.SlotConfigDto;
import com.sixsprints.eclinic.dto.user.DoctorDto;
import com.sixsprints.eclinic.enums.AppointmentType;
import com.sixsprints.eclinic.enums.UserRole;
import com.sixsprints.eclinic.repository.user.DoctorRepository;
import com.sixsprints.eclinic.service.user.abs.AbstractUserService;
import com.sixsprints.eclinic.util.csv.DoctorFieldData;
import com.sixsprints.eclinic.util.transformer.DoctorMapper;
import com.sixsprints.notification.service.NotificationService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("doctor")
public class DoctorServiceImpl extends AbstractUserService<Doctor, DoctorDto> implements DoctorService {

  public DoctorServiceImpl(DoctorMapper mapper, @Qualifier("sms") NotificationService notificationService) {
    super(mapper, notificationService);
  }

  @Resource
  private DoctorRepository doctorRepository;

  @Override
  protected DoctorRepository repository() {
    return doctorRepository;
  }

  @Override
  protected MetaData<Doctor> metaData() {
    return MetaData.<Doctor>builder()
      .classType(Doctor.class).dtoClassType(DoctorDto.class).fields(DoctorFieldData.field())
      .build();
  }

  @Override
  protected void preCreate(Doctor entity) {
    super.preCreate(entity);
  }

  @Override
  public List<PaymentOptionDto> doctorPaymentOptions(String doctorSlug) throws BaseException {
    Doctor paymentOptions = findBySlug(doctorSlug);
    if (CollectionUtils.isEmpty(paymentOptions.getPaymentOptions())) {
      throw EntityNotFoundException.builder().error("Payment options not found.").httpStatus(HttpStatus.OK).build();
    }
    paymentOptions.getPaymentOptions().forEach(p -> {
      p.setLogo(p.getPaymentMode().getLogo());
    });
    return paymentOptions.getPaymentOptions();
  }

  @Override
  public Doctor findByWebsiteKey(String websiteKey) {
    return doctorRepository.findByWebsiteKey(websiteKey);
  }

  @Override
  protected Doctor newUser(String mobileNumber) {
    return Doctor.builder().mobileNumber(mobileNumber).build();
  }

  @Override
  public List<Doctor> searchDoctors() {
    return doctorRepository.findByRoleName(UserRole.DOCTOR);
  }

  @Override
  public List<AppointmentTypeDto> appointmentType(String doctorSlug) throws BaseException {
    Doctor doctor = super.findBySlug(doctorSlug);
    if (doctor.getAppointmentType() == null) {
      throw EntityNotFoundException.builder().error("AppointmentType not found for selected doctor.")
        .httpStatus(HttpStatus.OK).build();
    }
    return doctor.getAppointmentType();
  }

  @Override
  public Doctor updateDoctor(Doctor domain) throws BaseException {
    Doctor doctorData = findOne(domain.getId());
    domain.setPassword(doctorData.getPassword());
    domain.setWebsiteKey(doctorData.getWebsiteKey());
    
    Doctor doctor = domain;
    return super.save(doctor);
  }

  public Doctor createSlots(Doctor domain) throws BaseException {
    log.info("createSlots initialize.");
    Map<AppointmentType, Map<DayOfWeek, List<LocalTime>>> slots = new LinkedHashMap<>();
    slots = slots(domain);
    domain.setSlots(slots);
    return domain;
  }

  public List<DayOfWeek> daysRange(String days) {
    String[] split = days.split("-");
    List<DayOfWeek> daysList = new LinkedList<DayOfWeek>();
    try {
      List<DayOfWeek> daysList2 = daysList(split[0].trim(), split[1].trim(), daysList);
      daysList = daysList2;
    } catch (Exception e) {
      daysList.add(DayOfWeek.valueOf(split[0].toUpperCase()));
    }
    return daysList;
  }

  public List<DayOfWeek> daysList(String startDay, String endDay, List<DayOfWeek> daysList) {
    Map<DayOfWeek, Integer> days = new LinkedHashMap<DayOfWeek, Integer>();
    days.put(DayOfWeek.MONDAY, 1);
    days.put(DayOfWeek.TUESDAY, 2);
    days.put(DayOfWeek.WEDNESDAY, 3);
    days.put(DayOfWeek.THURSDAY, 4);
    days.put(DayOfWeek.FRIDAY, 5);
    days.put(DayOfWeek.SATURDAY, 6);
    days.put(DayOfWeek.SUNDAY, 7);
    Integer startDayValue = days.get(DayOfWeek.valueOf(startDay));
    Integer endDayValue = days.get(DayOfWeek.valueOf(endDay));
    if (startDayValue > endDayValue) {
      int temp = startDayValue;
      startDayValue = endDayValue;
      endDayValue = temp;
    }
    for (int i = startDayValue; i <= endDayValue; i++) {
      daysList.add(getKey(days, i));
    }
    return daysList;
  }

  public <K, V> K getKey(Map<K, V> map, V value) {
    for (Entry<K, V> entry : map.entrySet()) {
      if (entry.getValue().equals(value)) {
        return entry.getKey();
      }
    }
    return null;
  }

  private Map<AppointmentType, Map<DayOfWeek, List<LocalTime>>> slots(Doctor domain)
    throws BaseException {
    Map<AppointmentType, Map<DayOfWeek, List<LocalTime>>> parentMap = new LinkedHashMap<>();
    log.info("making slots from AvailabilitySummary");
    Set<Entry<AppointmentType, SlotConfigDto>> entrySet = domain.getAvailabilitySummary().entrySet();
    
    
    for (Entry<AppointmentType, SlotConfigDto> parentEntry : entrySet) {
      Map<DayOfWeek, List<LocalTime>> map = new LinkedHashMap<>();
      validateAvailabilitySummary(parentEntry.getValue().getAvailabilitySummary().entrySet());
      for (Map.Entry<String, String> entry : parentEntry.getValue().getAvailabilitySummary().entrySet()) {
        List<DayOfWeek> daysList = daysRange(entry.getKey());
        String timeRange = entry.getValue();
        String[] splitTime = timeRange.split("-");
        String startOfTime = splitTime[0];
        String endtOfTime = splitTime[1];
        String[] splitStartOfTime = startOfTime.split(":");
        int startHour = Integer.valueOf(splitStartOfTime[0].trim());
        int startMinute = Integer.valueOf(splitStartOfTime[1].trim());
        String[] splitEndOfTime = endtOfTime.split(":");
        int endHour = Integer.valueOf(splitEndOfTime[0].trim());
        int endMinute = Integer.valueOf(splitEndOfTime[1].trim());

        int duration = (int) TimeUnit.MILLISECONDS
          .toMinutes(parentEntry.getValue().getAppointmentSlotDurationInMillis());

        for (DayOfWeek d : daysList) {
        	if (!map.containsKey(d)) {
        		 map.put(d, time(startHour, startMinute, endHour, endMinute, duration));
        	}else {
        		List<LocalTime> oldTime = map.get(d);
        		//List<LocalTime> time  = time(startHour, startMinute, endHour, endMinute, duration);
        		//map.put(d, time(startHour, startMinute, endHour, endMinute, duration).addAll(oldTime));
        	}
         
        }
      }
      
      if (!parentMap.containsKey(parentEntry.getKey())) {
    	  parentMap.put(parentEntry.getKey(), map);
      }else {
    	  Map<DayOfWeek, List<LocalTime>> mapData = parentMap.get(parentEntry.getKey());
    	  map.putAll(mapData);
    	  parentMap.put(parentEntry.getKey(), map);
      }
      
    }
    return parentMap;
  }

  private void validateAvailabilitySummary(Set<Entry<String, String>> set) throws BaseException {
    log.info("validateAvailabilitySummary initialize.");
    List<DayOfWeek> daysList = new ArrayList<>();
    for (Map.Entry<String, String> entry : set) {
      List<DayOfWeek> daysRange = daysRange(entry.getKey());
      daysList.addAll(daysRange);
    }
    Set<DayOfWeek> findDuplicateByFrequency = findDuplicateByFrequency(daysList);
    if (findDuplicateByFrequency.size() > 0) {
      throw EntityInvalidException.builder().error("Invalid availability summary.").httpStatus(HttpStatus.CONFLICT)
        .build();
    }
  }

  public static <T> Set<T> findDuplicateByFrequency(List<T> list) {

    return list.stream().filter(i -> Collections.frequency(list, i) > 1)
      .collect(Collectors.toSet());

  }

  private List<LocalTime> time(int startHour, int startMin, int endHour, int endMin, int duration) {
    Set<LocalTime> set = new HashSet<>();
    LocalTime startTime = LocalTime.of(startHour, startMin);
    LocalTime endTime = LocalTime.of(endHour, endMin);
    LocalTime time = startTime.plusMinutes(duration);
    set.add(startTime);
    set.add(endTime);
    while (time.isBefore(endTime)) {
      set.add(LocalTime.of(time.getHour(), time.getMinute()));
      time = time.plusMinutes(duration);
    }
    List<LocalTime> list = new ArrayList<LocalTime>(set);
    Collections.sort(list);
    return list;
  }
}
