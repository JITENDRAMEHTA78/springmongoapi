package com.sixsprints.eclinic.repository;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.eclinic.domain.Appointment;
import com.sixsprints.eclinic.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends GenericRepository<Appointment> {

  Appointment findByDoctorSlugAndDateAndTimeAndStatusIn(String doctorSlug, Date date, LocalTime time,
    List<AppointmentStatus> statuses);

  List<Appointment> findByPatientSlugAndDateAndStatus(String patientSlug, Date date, AppointmentStatus status);

  List<Appointment> findByPatientSlugAndDateTimeGreaterThanAndStatusInOrderByDateTimeAsc(String patientSlug, Date date,
    List<AppointmentStatus> status);

  Page<Appointment> findByPatientSlugAndStatusOrderByDateTimeDesc(String patientSlug, AppointmentStatus status,
    Pageable pageable);

  List<Appointment> findByPatientSlug(String patientSlug);
  
  List<Appointment> findByPatientSlugAndDoctorSlug(String patientSlug, String doctorSlug);

}