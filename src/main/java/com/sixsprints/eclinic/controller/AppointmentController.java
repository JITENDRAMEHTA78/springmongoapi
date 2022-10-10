package com.sixsprints.eclinic.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.lambdaexpression.annotation.RequestBodyParam;
import com.google.zxing.WriterException;
import com.sixsprints.core.controller.AbstractCrudController;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthorizedException;
import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;
import com.sixsprints.eclinic.auth.Authenticated;
import com.sixsprints.eclinic.domain.Appointment;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.AppointmentDto;
import com.sixsprints.eclinic.dto.NoteDto;
import com.sixsprints.eclinic.dto.PrescriptionDto;
import com.sixsprints.eclinic.dto.SlotResponseDto;
import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.AppointmentStatus;
import com.sixsprints.eclinic.enums.AppointmentType;
import com.sixsprints.eclinic.enums.EntityPermission;
import com.sixsprints.eclinic.enums.UserRole;
import com.sixsprints.eclinic.service.AppointmentService;
import com.sixsprints.eclinic.util.Constants;
import com.sixsprints.eclinic.util.transformer.AppointmentMapper;

@RestController
@RequestMapping(value = Constants.API_PREFIX + "/appointment")
public class AppointmentController extends AbstractCrudController<Appointment, AppointmentDto, User> {

  private AppointmentService service;

  private AppointmentMapper mapper;

  @Resource
  private DateUtil dateUtil;

  public AppointmentController(AppointmentService service, AppointmentMapper mapper) {
    super(service, mapper);
    this.service = service;
    this.mapper = mapper;
  }

  @Override
  public ResponseEntity<RestResponse<AppointmentDto>> add(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.CREATE) User user,
    @Valid @RequestBody AppointmentDto dto)
    throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
    return RestUtil.successResponse(mapper.toDto(service.book(mapper.toDomain(dto))));
  }

  @PutMapping("/update")
  public ResponseEntity<RestResponse<AppointmentDto>> update(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.CREATE) User user,
    @Valid @RequestBody AppointmentDto dto)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    return RestUtil.successResponse(mapper.toDto(service.updateAppointment(mapper.toDomain(dto))), "success");
  }

  @Override
  public ResponseEntity<?> patch(@Authenticated User user, @RequestBody @Valid AppointmentDto dto,
    @RequestParam String propChanged)
    throws BaseException {
    Appointment domain = mapper.toDomain(dto);
    if (domain.getQueue() < 1) {
      throw EntityInvalidException.builder().error("Queue no should not be less than 1").httpStatus(HttpStatus.CONFLICT)
        .build();
    }
    return RestUtil.successResponse(service.patchUpdate(domain.getId(), domain, propChanged));
  }

  @Override
  public ResponseEntity<RestResponse<AppointmentDto>> findBySlug(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.READ) User user,
    @PathVariable String slug) throws EntityNotFoundException {

    Appointment appointment = service.findBySlug(slug);

    if (user.getRoleName().equals(UserRole.PATIENT.name()) && !appointment.getPatientSlug().equals(user.getSlug())) {
      throw BaseRuntimeException.builder().httpStatus(HttpStatus.UNAUTHORIZED)
        .error("You are not authorized to view the details of this appointment").build();
    }
    return RestUtil.successResponse(mapper.toDto(appointment));
  }

  @GetMapping("/qr-code/{slug}")
  public ResponseEntity<RestResponse<AppointmentDto>> findBySlug(@RequestHeader(value = "X-AUTH-TOKEN") String token,
    @PathVariable String slug)
    throws EntityNotFoundException, WriterException, IOException {
	Appointment app = service.generateQrCode(token, slug);
	
	AppointmentDto appsResponse = mapper.toDto(app);
	List<PrescriptionDto> prescriptions = app.getPrevPrescriptions();
	
	if (prescriptions != null && !prescriptions.isEmpty()) {
		Date lastVisit = prescriptions.get(0).getDate();
		appsResponse.setPrevPrescriptions(prescriptions);
		appsResponse.getPatient().setLastVisit(lastVisit);
	}

    return RestUtil.successResponse(appsResponse);
  }

  @Override
  public ResponseEntity<RestResponse<PageDto<AppointmentDto>>> filter(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.READ) User user,
    @RequestBody FilterRequestDto filterRequestDto) {

    return RestUtil.successResponse(mapper.pageEntityToPageDtoDto(service.filterByUser(filterRequestDto, user)));
  }

  @PostMapping("/update/date")
  public ResponseEntity<?> updateDate(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.UPDATE) User user,
    @RequestBody FilterRequestDto filterRequestDto, @RequestParam Integer months) {
    service.updateDate(filterRequestDto, months, user);
    return RestUtil.successResponse(null);
  }

  @PostMapping("/search/all")
  public ResponseEntity<RestResponse<List<AppointmentDto>>> filterAll(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.READ) User user,
    @RequestBody FilterRequestDto filterRequestDto) {
    return RestUtil.successResponse(mapper.toDto(service.filterAllByUser(filterRequestDto, user)));
  }

  /**
   * Get all available slots for a Doctor for a particular date for a type of
   * appointment.
   * 
   * @param doctorSlug
   * @param date
   * @return
   * @throws EntityNotFoundException
   */
  @PostMapping("/available-slots")
  public ResponseEntity<RestResponse<SlotResponseDto>> availableSlots(@RequestBodyParam String doctorSlug,
    @RequestBodyParam String type, @RequestBodyParam Long date) throws EntityNotFoundException {
    return RestUtil.successResponse(service.availableSlots(doctorSlug, date, AppointmentType.valueOf(type)));
  }

  /**
   * Get patient's latest appointment that was booked today if present. Otherwise
   * sends an empty appointment object with patient details pre-filled.
   * 
   * @param mobileNumber
   * @return
   * @throws EntityNotFoundException
   */
  @PostMapping("/patient/today")
  public ResponseEntity<RestResponse<AppointmentDto>> searchNextAppointmentByPatient(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.READ) User user,
    @RequestBodyParam String mobileNumber)
    throws EntityNotFoundException {
    return RestUtil.successResponse(mapper.toDto(service.searchNextAppointmentByPatient(mobileNumber)));
  }

  /**
   * Handles 2 cases. If patient had already booked an appointment then only the
   * status and fees gets updated. Otherwise books a new appointment with the
   * passed values.
   * 
   * Based on the Appointment Slug passed (empty vs not empty) it differentiates
   * if it is a new appointment or an existing one.
   * 
   * Patient UHID is generated (if new patient).
   * 
   * @param dto
   * @return
   * @throws EntityNotFoundException
   * @throws EntityAlreadyExistsException
   * @throws EntityInvalidException
   */
  @PostMapping("/confirm")
  public ResponseEntity<RestResponse<AppointmentDto>> confirmAppointment(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.CREATE) User user,
    @RequestBody @Valid AppointmentDto dto)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    return RestUtil.successResponse(mapper.toDto(service.confirmAppointment(mapper.toDomain(dto))));
  }

  /**
   * Upcoming appointments for the patient.
   * 
   * @param user
   * @return
   */
  @GetMapping("/patient/upcoming")
  public ResponseEntity<RestResponse<List<AppointmentDto>>> upcoming(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.READ) User user) {
    return RestUtil.successResponse(mapper.toDto(service.findMyUpcomingAppointments(user)));
  }

  /**
   * Past appointments of the patient.
   * 
   * @param user, page, size.
   * @return Paginated Appointments.
   */
  @GetMapping("/patient/past")
  public ResponseEntity<RestResponse<PageDto<AppointmentDto>>> past(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.READ) User user,
    @RequestParam(defaultValue = "0", required = false) int page,
    @RequestParam(defaultValue = "20", required = false) int size) {
    return RestUtil.successResponse(mapper.pageEntityToPageDtoDto(service.findMyPastAppointments(user, page, size)));
  }

  /**
   * Cancel the OPEN appointments.
   * 
   * @param user
   * @param slug
   * @return
   * @throws EntityNotFoundException
   * @throws NotAuthorizedException
   */
  @PostMapping("/cancel")
  public ResponseEntity<RestResponse<AppointmentDto>> cancelAppointment(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.UPDATE) User user,
    @RequestParam String slug) throws EntityNotFoundException, NotAuthorizedException {
    return RestUtil.successResponse(mapper.toDto(service.cancel(slug, user)));
  }

  @PutMapping("/prescription/{appointSlug}")
  public ResponseEntity<RestResponse<AppointmentDto>> updatePrescription(
    @Authenticated(entity = EntityPermission.CONSULTATION, access = AccessPermission.CREATE) User user,
    @RequestBody PrescriptionDto prescriptionDto,
    @PathVariable String appointSlug) throws EntityNotFoundException {
    Appointment appointment = service.addPrescription(appointSlug, prescriptionDto, user);
    return RestUtil.successResponse(mapper.toDto(appointment));
  }

  @PutMapping("/prescription/notify/{appointSlug}")
  public ResponseEntity<RestResponse<AppointmentDto>> updatePrescriptionAndNotify(
    @Authenticated(entity = EntityPermission.CONSULTATION, access = AccessPermission.CREATE) User user,
    @RequestBody PrescriptionDto prescriptionDto,
    @PathVariable String appointSlug) throws EntityNotFoundException {
    Appointment appointment = service.addPrescriptionAndNotify(appointSlug, prescriptionDto, user);
    return RestUtil.successResponse(mapper.toDto(appointment));
  }

  @PostMapping("/notify/{appointSlug}")
  public ResponseEntity<RestResponse<AppointmentDto>> notify(
    @Authenticated(entity = EntityPermission.CONSULTATION, access = AccessPermission.READ) User user,
    @PathVariable String appointSlug) throws EntityNotFoundException {
    service.notify(appointSlug, user, true);
    return RestUtil.successResponse(null);
  }

  @PutMapping("/prescription/notes-vitals/{appointSlug}")
  public ResponseEntity<RestResponse<AppointmentDto>> addNotesVitals(
    @Authenticated(entity = EntityPermission.VITALS_NOTES, access = AccessPermission.CREATE) User user,
    @RequestBodyParam(name = "notes", required = false) NoteDto notes,
    @RequestBodyParam(name = "vitals", required = false) Map<String, String> vitals,
    @RequestBodyParam(name = "remarks", required = false) String remarks,
    @PathVariable String appointSlug) throws EntityNotFoundException {
    Appointment appointment = service.addNotesAndVitals(appointSlug, notes, vitals, remarks, user);
    return RestUtil.successResponse(mapper.toDto(appointment));

  }

  @PutMapping("/status/{appointmentSlug}/{status}")
  public ResponseEntity<RestResponse<AppointmentDto>> changeStatus(
    @Authenticated(entity = EntityPermission.APPOINTMENT, access = AccessPermission.UPDATE) User user,
    @PathVariable String appointmentSlug, @PathVariable AppointmentStatus status) throws EntityNotFoundException {
    Appointment appointment = service.changeStatus(appointmentSlug, status);
    return RestUtil.successResponse(mapper.toDto(appointment));
  }

  @PutMapping("/call-next/{appointmentSlug}")
  public ResponseEntity<RestResponse<AppointmentDto>> callNext(
    @Authenticated(entity = EntityPermission.CALL_NEXT) User user, @PathVariable String appointmentSlug)
    throws EntityNotFoundException, EntityInvalidException {
    Appointment appointment = service.callNext(appointmentSlug);
    return RestUtil.successResponse(mapper.toDto(appointment));
  }

  @GetMapping("/generate-pdf/by-doctor/{appointSlug}")
  public ResponseEntity<RestResponse<AppointmentDto>> generatePdfByDoctor(@Authenticated User user,
    @PathVariable String appointSlug) throws EntityNotFoundException, IOException, WriterException {
    return RestUtil.successResponse(mapper.toDto(service.generatePrescriptionPdf(user, appointSlug)));
  }

  @GetMapping("/generate-pdf/receipt/{appointmentSlug}")
  public ResponseEntity<RestResponse<AppointmentDto>> generateReceiptPdf(@Authenticated User user,
    @PathVariable String appointmentSlug) throws EntityNotFoundException, IOException, WriterException {
    return RestUtil.successResponse(mapper.toDto(service.generateReceiptPdf(appointmentSlug)));
  }
  
  @GetMapping("/generate-letterhead/{appointmentSlug}")
  public ResponseEntity<RestResponse<AppointmentDto>> generateLetterHead(@Authenticated User user, @PathVariable String appointmentSlug) throws EntityNotFoundException, IOException, WriterException {
    return RestUtil.successResponse(mapper.toDto(service.generateLetterHeadPdf(appointmentSlug)));
  }

  @GetMapping("/previous-prescription/{appointmentSlug}")
  public ResponseEntity<RestResponse<PrescriptionDto>> previousPrescription(@Authenticated User user,
    @PathVariable String appointmentSlug) throws EntityNotFoundException {
    return RestUtil.successResponse(service.previousPrescription(appointmentSlug));
  }

  @GetMapping("/print/{appointmentSlug}")
  public ResponseEntity<RestResponse<Map<String, Object>>> prescriptionParams(@PathVariable String appointmentSlug)
    throws EntityNotFoundException, WriterException, IOException {
    return RestUtil.successResponse(service.getPrescriptionParams(appointmentSlug));
  }

  @GetMapping("/receipt/print/{appointmentSlug}")
  public ResponseEntity<RestResponse<Map<String, Object>>> receiptParams(@PathVariable String appointmentSlug)
    throws EntityNotFoundException, WriterException, IOException {
    return RestUtil.successResponse(service.getReceiptParams(appointmentSlug));
  }

  @GetMapping("/patient-appointment/{patientSlug}")
  public ResponseEntity<RestResponse<List<AppointmentDto>>> patientAppointment(@Authenticated User user,
    @PathVariable String patientSlug) {
    return RestUtil.successResponse(mapper.toDto(service.patientAppointment(user, patientSlug)), "Success");
  }

  @PutMapping("/doctor/unavailable")
  public ResponseEntity<?> markAsUnavailable(
    @Authenticated(entity = EntityPermission.APPOINTMENT) User user, @RequestBodyParam String doctorSlug,
    @RequestBodyParam Long from, @RequestBodyParam Long to) throws EntityNotFoundException, EntityInvalidException {
    service.markAsUnavailable(doctorSlug, from, to);
    return RestUtil.successResponse(null);
  }

  @PutMapping("/discount/{appointmentSlug}")
  public ResponseEntity<RestResponse<AppointmentDto>> addDiscount(
    @Authenticated(entity = EntityPermission.APPOINTMENT) User user, @PathVariable String appointmentSlug,
    @RequestBodyParam Integer discount, @RequestBodyParam Boolean isFamily, @RequestBodyParam Boolean isFriends)
    throws EntityNotFoundException, EntityInvalidException, EntityAlreadyExistsException {
    return RestUtil.successResponse(
      mapper.toDto(service.addDiscount(appointmentSlug, new BigDecimal(discount).setScale(0, RoundingMode.DOWN),
        isFamily, isFriends)));
  }

}