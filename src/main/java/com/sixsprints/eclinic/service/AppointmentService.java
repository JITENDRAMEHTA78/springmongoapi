package com.sixsprints.eclinic.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.google.zxing.WriterException;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthorizedException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.eclinic.domain.Appointment;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.NoteDto;
import com.sixsprints.eclinic.dto.PaymentDto;
import com.sixsprints.eclinic.dto.PrescriptionDto;
import com.sixsprints.eclinic.dto.SlotResponseDto;
import com.sixsprints.eclinic.enums.AppointmentStatus;
import com.sixsprints.eclinic.enums.AppointmentType;

public interface AppointmentService extends GenericCrudService<Appointment> {

  Appointment book(Appointment appointment)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

  Appointment confirmAppointment(Appointment appointment)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

  Appointment searchNextAppointmentByPatient(String patientMobileNumber) throws EntityNotFoundException;

  SlotResponseDto availableSlots(String doctorSlug, Long date, AppointmentType type) throws EntityNotFoundException;

  List<Appointment> findMyUpcomingAppointments(User user);

  Page<Appointment> findMyPastAppointments(User user, int page, int size);

  Appointment cancel(String appointmentSlug, User user) throws EntityNotFoundException, NotAuthorizedException;

  Appointment addPrescription(String appointSlug, PrescriptionDto prescriptionDto, User user)
    throws EntityNotFoundException;

  Appointment addPrescriptionAndNotify(String appointSlug, PrescriptionDto prescriptionDto, User user)
    throws EntityNotFoundException;

  void notify(String appointSlug, User user, Boolean notify);

  Appointment book(Appointment appointment, AppointmentStatus status)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

  Appointment confirmPayment(String appointmentSlug, PaymentDto paymentDto) throws EntityNotFoundException;

  Appointment addNotesAndVitals(String appointSlug, NoteDto notes, Map<String, String> vitals, String remarks,
    User user) throws EntityNotFoundException;

  Appointment changeStatus(String appointmentSlug, AppointmentStatus status) throws EntityNotFoundException;

  Appointment generateQrCode(String token, String slug) throws EntityNotFoundException, WriterException, IOException;

  Page<Appointment> filterByUser(FilterRequestDto filters, User user);

  List<Appointment> filterAllByUser(FilterRequestDto filters, User user);

  Appointment callNext(String appointmentSlug) throws EntityNotFoundException, EntityInvalidException;

  Appointment generatePrescriptionPdf(User user, String appointSlug)
    throws IOException, EntityNotFoundException, WriterException;

  PrescriptionDto previousPrescription(String appointmentSlug) throws EntityNotFoundException;

  Map<String, Object> getPrescriptionParams(String appointmentSlug)
    throws WriterException, IOException, EntityNotFoundException;

  Appointment updateAppointment(Appointment domain)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

  void markAsUnavailable(String doctorSlug, Long fromTime, Long toTime);

  Appointment addDiscount(String appointmentSlug, BigDecimal discount, Boolean isFamily, Boolean isFriend)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException;

  Map<String, Object> getReceiptParams(String appointmentSlug) throws EntityNotFoundException;

  Appointment generateReceiptPdf(String appointmentSlug) throws WriterException, IOException, EntityNotFoundException;
  
  public Appointment generateLetterHeadPdf(String appointmentSlug) throws WriterException, IOException, EntityNotFoundException;

  List<Appointment> patientAppointment(User user, String patientSlug);
  
  void updateDate(FilterRequestDto dto, int monthsToAdjust, User user);

}