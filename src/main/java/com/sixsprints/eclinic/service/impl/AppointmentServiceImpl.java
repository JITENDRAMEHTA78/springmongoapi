package com.sixsprints.eclinic.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.MetaData;
import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.DateColumnFilter;
import com.sixsprints.core.dto.filter.ExactMatchColumnFilter;
import com.sixsprints.core.dto.filter.SetColumnFilter;
import com.sixsprints.core.dto.filter.SortModel;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthorizedException;
import com.sixsprints.core.repository.GenericRepository;
import com.sixsprints.core.service.AbstractCrudService;
import com.sixsprints.core.utils.AppConstants;
import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.eclinic.domain.Appointment;
import com.sixsprints.eclinic.domain.Order;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.domain.user.Patient;
import com.sixsprints.eclinic.domain.user.User;
import com.sixsprints.eclinic.dto.AppointmentDto;
import com.sixsprints.eclinic.dto.AppointmentTypeDto;
import com.sixsprints.eclinic.dto.DatePair;
import com.sixsprints.eclinic.dto.MedicationDto;
import com.sixsprints.eclinic.dto.NoteDto;
import com.sixsprints.eclinic.dto.OrderProductDto;
import com.sixsprints.eclinic.dto.PaymentDto;
import com.sixsprints.eclinic.dto.PrescriptionDto;
import com.sixsprints.eclinic.dto.SlotDto;
import com.sixsprints.eclinic.dto.SlotResponseDto;
import com.sixsprints.eclinic.dto.TenantDto;
import com.sixsprints.eclinic.enums.AppointmentStatus;
import com.sixsprints.eclinic.enums.AppointmentType;
import com.sixsprints.eclinic.enums.OrderStatus;
import com.sixsprints.eclinic.enums.PaymentMode;
import com.sixsprints.eclinic.enums.ProductType;
import com.sixsprints.eclinic.enums.UserRole;
import com.sixsprints.eclinic.repository.AppointmentRepository;
import com.sixsprints.eclinic.service.AppointmentService;
import com.sixsprints.eclinic.service.MedicationService;
import com.sixsprints.eclinic.service.OrderService;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.user.PatientService;
import com.sixsprints.eclinic.util.AppMessages;
import com.sixsprints.eclinic.util.ThreadContext;
import com.sixsprints.eclinic.util.csv.AppointmentFieldData;
import com.sixsprints.eclinic.util.initdata.TenantData;
import com.sixsprints.eclinic.util.transformer.AppointmentMapper;
import com.sixsprints.eclinic.util.transformer.MedicationMapper;
import com.sixsprints.notification.dto.AttachmentDto;
import com.sixsprints.notification.dto.MessageDto;
import com.sixsprints.notification.dto.ShortURLDto;
import com.sixsprints.notification.service.NotificationService;
import com.sixsprints.notification.service.URLShorteningService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AppointmentServiceImpl extends AbstractCrudService<Appointment> implements AppointmentService {

  @Resource
  private AppointmentRepository appointmentRepository;

  @Resource
  private AppointmentMapper appointmentMapper;

  @Resource
  private DoctorService doctorService;

  @Lazy
  @Resource
  private OrderService orderService;

  @Resource
  private PatientService patientService;

  @Resource
  private DateUtil dateUtil;

  @Resource
  private MedicationService medicationService;

  @Resource
  private MedicationMapper medicationMapper;

  @Autowired
  @Qualifier("sms")
  private NotificationService smsService;

  @Autowired
  @Qualifier("email")
  private NotificationService emailService;

  @Autowired
  private PdfService pdfService;

  @Autowired
  private SpringTemplateEngine templateEngine;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private URLShorteningService urlShorteningService;

  @Value("${server.url}")
  private String serverUrl;

  @Override
  public Appointment book(Appointment appointment, AppointmentStatus status)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    validateDateAndTime(appointment.getDate(), appointment.getTime());
    Doctor doc = doctorService.findBySlug(appointment.getDoctorSlug());
    patientService.findBySlug(appointment.getPatientSlug());
    User currentUserData = ThreadContext.getCurrentUserData();
    if (currentUserData == null || UserRole.PATIENT.toString().equalsIgnoreCase(currentUserData.getRoleName())) {
      validateSlot(doc, appointment);
    }
    if (appointment.getQueue() == null) {
      appointment.setQueue(0);
    }
    appointment.setCreatorRole(currentUserData.getRoleName().toString());
    appointment.setCreatorName(currentUserData.getName().toString());
    appointment.setStatus(status);
    return create(appointment);
  }

  private Integer findLastQueuNo(Date appintmentDate) {
    Date endOfDay = endDay(appintmentDate);
    Date startOfdDay = startOdDay(appintmentDate);
    Query query = new Query();
    query.addCriteria(
      new Criteria().andOperator(
        Criteria.where("date").gte(startOfdDay).lte(endOfDay)))
      .with(Sort.by(Sort.Direction.DESC, "queue")).limit(1);
    Appointment appointment = mongoTemplate.findOne(query, Appointment.class);
    return appointment != null ? appointment.getQueue() + 1 : 1;
  }

  public Date endDay(Date appintmentDate) {
    Date endOfDay = dateUtil.endOfDay(appintmentDate.getTime()).toDate();
    return endOfDay;
  }

  public Date startOdDay(Date appintmentDate) {
    Date startOfDay = dateUtil.startOfDay(appintmentDate.getTime()).toDate();
    return startOfDay;
  }

  @Override
  public Appointment book(Appointment appointment)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    return book(appointment, AppointmentStatus.INITIATED);
  }

  @Override
  public Appointment confirmAppointment(Appointment appointment)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    Appointment fromDb = fetchExistingAppointment(appointment);
    fromDb.setMode(appointment.getMode());
    if (AppointmentStatus.OPEN.equals(fromDb.getStatus())) {
      fromDb.setStatus(AppointmentStatus.CONFIRMED);
      fromDb.setFees(appointment.getFees());
      patientService.generateUhid(fromDb.getPatientSlug());
      fromDb = save(fromDb);
    }
    createAppointmentPayment(fromDb);
    return fromDb;
  }

  @Override
  protected void postCreate(Appointment app) {
    try {
      if (app.getDiscount() != null && app.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
        createRefundEntry(app);
      }
    } catch (Exception ex) {
      log.error("Unable to create refund entry for app {}", app.getSlug());
      log.error(ex.getMessage(), ex);
    }
  }

  @Override
  public SlotResponseDto availableSlots(String doctorSlug, Long date, AppointmentType type)
    throws EntityNotFoundException {
    Doctor doc = doctorService.findBySlug(doctorSlug);
    SlotResponseDto dto = empty(doc, date, type);
    List<SlotDto> slots = findAvailableSlotsForDoctor(doc, date, type);
    return convertToTimeOfDay(dto, slots);
  }

  @Override
  public Appointment searchNextAppointmentByPatient(String patientMobileNumber) throws EntityNotFoundException {
    Query q = new Query();
    q.addCriteria(Criteria.where("mobileNumber").is(patientMobileNumber));
    Patient patient = mongoTemplate.findOne(q, Patient.class, "user");
    if (patient == null) {
      int length = patientMobileNumber.length();
      if (length == 10) {
        Query newQ = new Query();
        newQ.addCriteria(Criteria.where("mobileNumber").is("+91" + patientMobileNumber));
        patient = mongoTemplate.findOne(newQ, Patient.class, "user");
      }
      if (patient == null) {
        return null;
      }
    }
    Date date = dateUtil.startOfDay(dateUtil.now().getMillis()).toDate();
    List<Appointment> apps = appointmentRepository.findByPatientSlugAndDateAndStatus(patient.getSlug(),
      date, AppointmentStatus.OPEN);
    if (!CollectionUtils.isEmpty(apps)) {
      Collections.sort(apps);
      return apps.get(0);
    }
    return Appointment.builder().patientSlug(patient.getSlug()).date(date).build();
  }

  @Override
  public List<Appointment> findMyUpcomingAppointments(User user) {
    return appointmentRepository.findByPatientSlugAndDateTimeGreaterThanAndStatusInOrderByDateTimeAsc(user.getSlug(),
      dateUtil.now().toDate(),
      ImmutableList.<AppointmentStatus>of(AppointmentStatus.OPEN, AppointmentStatus.CONFIRMED,
        AppointmentStatus.PENDING_APPROVAL));
  }

  @Override
  public Page<Appointment> findMyPastAppointments(User user, int page, int size) {
    return appointmentRepository.findByPatientSlugAndStatusOrderByDateTimeDesc(user.getSlug(),
      AppointmentStatus.COMPLETED, PageRequest.of(page, size));
  }

  @Override
  public Appointment cancel(String appointmentSlug, User user) throws EntityNotFoundException, NotAuthorizedException {
    Appointment app = findBySlug(appointmentSlug);

    if (!AppointmentStatus.OPEN.equals(app.getStatus())) {
      throw BaseRuntimeException.builder().error("Cannot cancel a " + app.getStatus().getLabel() + " appointment.")
        .build();
    }

    if (!user.getSlug().equals(app.getPatientSlug())) {
      throw NotAuthorizedException.childBuilder().error("You can only cancel your own appointments.").build();
    }
    app.setStatus(AppointmentStatus.CANCELLED);
    return save(app);
  }

  @Override
  protected void preCreate(Appointment app) {
    super.preCreate(app);
    Date date = dateUtil.startOfDay(app.getDate().getTime()).toDate();
    app.setDate(date);
    app.setDateTime(
      dateUtil.initDateFromDate(date).withHourOfDay(app.getTime().getHour()).withMinuteOfHour(app.getTime().getMinute())
        .withSecondOfMinute(app.getTime().getSecond()).toDate());
  }

  @Override
  protected boolean isInvalid(Appointment domain) {
    return domain == null || StringUtils.isBlank(domain.getDoctorSlug()) || StringUtils.isBlank(domain.getPatientSlug())
      || domain.getDate() == null || domain.getTime() == null;
  }

  @Override
  protected GenericRepository<Appointment> repository() {
    return appointmentRepository;
  }

  @Override
  protected MetaData<Appointment> metaData() {
    return MetaData.<Appointment>builder()
      .classType(Appointment.class).fields(AppointmentFieldData.fields())
      .defaultSort(Sort.by(Direction.ASC, "date"))
      .dtoClassType(AppointmentDto.class)
      .build();
  }

  @Override
  protected Appointment findDuplicate(Appointment app) {
    return appointmentRepository.findByDoctorSlugAndDateAndTimeAndStatusIn(app.getDoctorSlug(), app.getDate(),
      app.getTime(), blockingStatuses());
  }

  @Override
  public Appointment confirmPayment(String appointmentSlug, PaymentDto paymentDto) throws EntityNotFoundException {
    Appointment app = findBySlug(appointmentSlug);
    app.setStatus(AppointmentStatus.CONFIRMED);
    if (PaymentMode.PAY_AT_HOSPITAL.equals(paymentDto.getMode())) {
      app.setStatus(AppointmentStatus.OPEN);
    }
    return save(app);
  }

  @Override
  public Appointment addPrescription(String appointSlug, PrescriptionDto prescriptionDto, User user)
    throws EntityNotFoundException {
    Appointment appointment = addPrescriptionForAppointment(appointSlug, prescriptionDto, user);
    notify(appointSlug, user, false);
    return appointment;
  }

  @Override
  public Appointment addPrescriptionAndNotify(String appointSlug, PrescriptionDto prescriptionDto, User user)
    throws EntityNotFoundException {
    Appointment appointment = addPrescriptionForAppointment(appointSlug, prescriptionDto, user);
    notify(appointSlug, user, true);
    return appointment;
  }

  @Override
  public void notify(String appointSlug, User user, Boolean notify) {
    try {
      new Thread(() -> {
        try {
          final Appointment dbApp = generatePrescriptionPdf(user, appointSlug);
          addMedications(dbApp);
          if (notify) {
            Patient patient = patientService.findBySlug(dbApp.getPatientSlug());
            sendMessageForPrecription(patient, dbApp);
            sendEmailForPrescription(patient, dbApp);
            save(dbApp);
          }
        } catch (Exception ex) {
          log.error(ex.getMessage(), ex);
          log.error("Unable to generate prescription pdf for appointment {}", appointSlug);
        }

      }).start();
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      log.error("Error executing the thread while trying to generate prescription pdf for appointment {}", appointSlug);
    }
  }

  private Appointment addPrescriptionForAppointment(String appointSlug, PrescriptionDto prescriptionDto, User user)
    throws EntityNotFoundException {
    Appointment appointment = findBySlug(appointSlug);
    appointment.setPrescription(prescriptionDto);
    if (prescriptionDto.getIsDetailedConsult() != null && prescriptionDto.getIsDetailedConsult()) {
      appointment.setIsDetailedConsult(prescriptionDto.getIsDetailedConsult());
    }
    Date nextAppDate = prescriptionDto.getNextAppointmentDate();
    if (nextAppDate != null && new Boolean(nextAppDate.toString())) {
      appointment.setNextAppointmentDate(nextAppDate);
    }
    if (isDoctor(user)) {
      postAppointmentCompletion(appointment, prescriptionDto);
    }
    if (!CollectionUtils.isEmpty(prescriptionDto.getNotes())) {
      prescriptionDto.getNotes().forEach(n -> {
        n.setUserName(user.getName());
        n.setUserRole(user.getRoleName());
        n.setUserSlug(user.getSlug());
        n.setDateAdded(dateUtil.now().toDate());
      });
      appointment.setPrescription(prescriptionDto);
    }
    if (appointment.getPrescription().getTestNeeded() != null && appointment.getPrescription().getTestNeeded()) {
      appointment.setStatus(AppointmentStatus.TEST_NEEDED);
    }
    appointment = save(appointment);
    return appointment;
  }

  private void addMedications(Appointment dbApp) {
    List<MedicationDto> medications = dbApp.getPrescription().getMedications();
    medicationService.updateAll(medicationMapper.toDomain(medications));
  }

  @Override
  public Appointment addNotesAndVitals(String appointSlug, NoteDto note, Map<String, String> vitals,
    String remarks, User user) throws EntityNotFoundException {
    Appointment appointment = findBySlug(appointSlug);
    PrescriptionDto prescriptionDto = appointment.getPrescription();
    if (prescriptionDto == null) {
      prescriptionDto = PrescriptionDto.builder().build();
    }
    prescriptionDto.setVitals(vitals);
    prescriptionDto.setRemarks(remarks);
//    if (CollectionUtils.isEmpty(prescriptionDto.getNotes())) {
//      prescriptionDto.setNotes(new ArrayList<>());
//    }
//    List<NoteDto> notes = prescriptionDto.getNotes();
//    NoteDto dto = findNote(prescriptionDto, user.getSlug());
//    boolean notFound = dto == null;
//    if (notFound && note != null) {
//      dto = NoteDto.builder().userName(user.getName()).userSlug(user.getSlug())
//        .userRole(user.getRoleName()).dateAdded(dateUtil.now().toDate())
//        .noteDescription(note.getNoteDescription())
//        .build();
//      notes.add(dto);
//      dto.setNoteDescription(note.getNoteDescription());
//    } else {
//      dto.setNoteDescription(note.getNoteDescription());
//    }
//    prescriptionDto.setNotes(notes);
    appointment.setPrescription(prescriptionDto);
    return save(appointment);
  }

  @Override
  public Appointment changeStatus(String appointmentSlug, AppointmentStatus status) throws EntityNotFoundException {
    Appointment appointment = findBySlug(appointmentSlug);
    return changeStatus(status, appointment);
  }

  private Appointment changeStatus(AppointmentStatus status, Appointment appointment) {
    if (status.equals(appointment.getStatus())) {
      return appointment;
    }
    if (AppointmentStatus.COMPLETED.equals(status)) {
      setPatientAsOld(appointment.getPatientSlug());
    } else if (AppointmentStatus.CHECKED_IN.equals(status)) {
      LocalTime now = Instant.ofEpochMilli(dateUtil.now().getMillis())
        .atZone(ZoneId.of(dateUtil.getTimeZone().getID()))
        .toLocalTime();
      appointment.setCheckInTime(now);
    }
    appointment.setCalled(Boolean.FALSE);
    appointment.setStatus(status);
    return save(appointment);
  }

  @Override
  public Page<Appointment> filterByUser(FilterRequestDto filters, User user) {
    filters = checkUserAndUpdateFilter(filters, user);
    if (filters.getFilterModel().get("status") == null) {
      filters = filterCancelledAndNoShow(filters);
    }
    SetColumnFilter setColumnFilter = (SetColumnFilter) filters.getFilterModel().get("status");
    if (setColumnFilter != null && CollectionUtils.isNotEmpty(setColumnFilter.getValues())
      && setColumnFilter.getValues().contains(AppointmentStatus.CHECKED_IN.name())) {
      filters = filterCheckedInAndReportReady(filters);
    }
    Page<Appointment> filter = filter(filters);
    return filter;
  }

  private FilterRequestDto filterCancelledAndNoShow(FilterRequestDto filters) {
    List<String> list = new ArrayList<>();
    list.add(AppointmentStatus.COMPLETED.toString());
    list.add(AppointmentStatus.PENDING_APPROVAL.toString());
    list.add(AppointmentStatus.OPEN.toString());
    list.add(AppointmentStatus.CONFIRMED.toString());
    list.add(AppointmentStatus.IN_PROGRESS.toString());
    list.add(AppointmentStatus.CHECKED_IN.toString());
    list.add(AppointmentStatus.TEST_NEEDED.toString());
    list.add(AppointmentStatus.REPORT_READY.toString());
    list.add(AppointmentStatus.AWAITING_REPORT.toString());
    list.add(AppointmentStatus.RECORDING_VITALS.toString());
    list.add(AppointmentStatus.NEEDS_ATTENTION.toString());
    
    filters.getFilterModel().put("status", SetColumnFilter.builder().values(list).build());
    return filters;
  }

  private FilterRequestDto filterCheckedInAndReportReady(FilterRequestDto filters) {
    List<String> list = ((SetColumnFilter) filters.getFilterModel().get("status")).getValues();
    list.add(AppointmentStatus.REPORT_READY.toString());
    filters.getFilterModel().put("status", SetColumnFilter.builder().values(list).build());
    return filters;
  }

  @Override
  public List<Appointment> filterAllByUser(FilterRequestDto filters, User user) {
    filters = checkUserAndUpdateFilter(filters, user);
    return filterAll(filters);
  }

  @Override
  public Appointment callNext(String appointmentSlug) throws EntityNotFoundException, EntityInvalidException {
    Appointment appointment = findBySlug(appointmentSlug);

    if (!AppointmentStatus.CHECKED_IN.equals(appointment.getStatus())
      && !AppointmentStatus.REPORT_READY.equals(appointment.getStatus())) {
      throw EntityInvalidException.childBuilder().error(AppMessages.Appointment.CALL_NEXT_ERROR)
        .arg(AppointmentStatus.CHECKED_IN.getLabel()).arg(AppointmentStatus.REPORT_READY.getLabel())
        .arg(appointment.getStatus().getLabel()).build();
    }

    if (appointment.getCalled() != null && appointment.getCalled()) {
      return appointment;
    }

    appointment.setCalled(Boolean.TRUE);
    return save(appointment);
  }

  @Override
  public Appointment generateQrCode(String token, String slug)
    throws EntityNotFoundException, WriterException, IOException {
    Appointment appoinment = findBySlug(slug);
    byte[] qrCodeImage = getQrCodeImage(token, slug);
    appoinment.setQrCode(qrCodeImage);
    
    //adding prescription to appointment
    //one prescription for one appointment 
    List<PrescriptionDto> prescriptions = previousPrescriptionAll(slug);
    if (prescriptions != null) {
    	appoinment.setPrevPrescriptions(prescriptions);
    }
    
    return appoinment;
  }

  @Override
  public Appointment generatePrescriptionPdf(User user, String appointSlug)
    throws IOException, EntityNotFoundException, WriterException {
    Appointment appointment = findBySlug(appointSlug);
    return generatePrescriptionPdf(appointment);
  }

  @Override
  public Appointment generateReceiptPdf(String appointmentSlug)
    throws WriterException, IOException, EntityNotFoundException {
    Appointment appointment = findBySlug(appointmentSlug);
    Patient patient = patientService.findBySlug(appointment.getPatientSlug());
    final Context ctx = new Context();
    ctx.setVariable("appointmentSlug", appointment.getSlug());
    ctx.setVariable("serverUrl", serverUrl);
    String html = templateEngine.process("receipt.html", ctx);
    String pdfUrl = pdfService.htmlToPdf(html,
      appointment.getDoctorSlug() + "/" + "receipt/" + appointment.getSlug(), patient.getName());
    appointment.setReceiptPdf(pdfUrl);
    try {
      orderService.setReceiptPrinted(OrderStatus.CONFIRMED, appointmentSlug);
    } catch (Exception ex) {
      log.error("Unable to mark receipt as printed for appointment {}", appointmentSlug);
      log.error(ex.getMessage(), ex);
    }

    return save(appointment);
  }
  
  @Override
  public Appointment generateLetterHeadPdf(String appointmentSlug)
    throws WriterException, IOException, EntityNotFoundException {
	  Appointment appointment = findBySlug(appointmentSlug);
	  Patient patient = patientService.findBySlug(appointment.getPatientSlug());
	  
	  // calculate age of patient
	  if (patient.getDob() != null) {
		  LocalDate dateofbirth = patient.getDob().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		  Period age = Period.between(dateofbirth, LocalDate.now());
		  patient.setAge(age.getYears());
	  }
	  
    final Context ctx = new Context();
    ctx.setVariable("appointmentSlug", appointment.getSlug());
    ctx.setVariable("serverUrl", serverUrl);
    String html = templateEngine.process("letterHead.html", ctx);
    String pdfUrl = pdfService.htmlToPdf(html,
    	      appointment.getDoctorSlug() + "/" + "letterHead/" + appointment.getSlug(), patient.getName());
    appointment.setLetterHeadPdf(pdfUrl);

    return save(appointment);
  }

  @Override
  public PrescriptionDto previousPrescription(String appointmentSlug) throws EntityNotFoundException {
    Appointment app = findBySlug(appointmentSlug);
    Page<Appointment> apps = filter(previousAppointmentFilters(app, 0, 1));
    if (apps != null && apps.hasContent() && CollectionUtils.isNotEmpty(apps.getContent())) {
      return apps.getContent().get(0).getPrescription();
    }
    return null;
  }
  
  public List<PrescriptionDto> previousPrescriptionAll(String appointmentSlug) throws EntityNotFoundException {
	   List<PrescriptionDto> prescriptions = new ArrayList<>();
	    Appointment app = findBySlug(appointmentSlug);
	    Page<Appointment> apps = filter(previousAppointmentFilters(app, 0, 10));
	    
	    if (apps != null && apps.hasContent() && CollectionUtils.isNotEmpty(apps.getContent())) {
	      for (Appointment appointment : apps.getContent()) {
	    	  prescriptions.add(appointment.getPrescription());
	      }
	    }
	  return prescriptions;
  }

  @Override
  public Map<String, Object> getPrescriptionParams(String appointmentSlug)
    throws WriterException, IOException, EntityNotFoundException {
    Appointment app = findBySlug(appointmentSlug);
    return getPrescriptionParams(app);
  }

  private FilterRequestDto previousAppointmentFilters(Appointment app, int page, int size) {
    FilterRequestDto dto = FilterRequestDto
      .builder()
      .page(page)
      .size(size)
      .sortModel(ImmutableList.<SortModel>of(SortModel.builder().colId("dateModified").sort(Direction.DESC).build()))
      .filterModel(ImmutableMap.<String, ColumnFilter>builder()
        .put("doctorSlug", ExactMatchColumnFilter.builder().filter(app.getDoctorSlug()).build())
        .put("patientSlug", ExactMatchColumnFilter.builder().filter(app.getPatientSlug()).build())
        .put("dateTime",
          DateColumnFilter.builder().filter(app.getDateTime().getTime()).type(AppConstants.LESS_THAN).exactMatch(true)
            .build())
        .put("prescription", ExactMatchColumnFilter.builder().type(AppConstants.EXISTS).build())
        .build())
      .build();
    return dto;
  }

  private byte[] getQrCodeImage(String token, String slug) throws WriterException, IOException {
    String qrData = ThreadContext.getCurrentTenant().getAdminWebUrl() + "/admin/app/patient/prescription?slug=" + slug;
    if (StringUtils.isNotBlank(token)) {
      qrData += "&token=" + token;
    }
    return getQRCodeImage(qrData, 200, 200);
  }

  private void setPatientAsOld(String patientSlug) {
    try {
      Patient patient = patientService.findBySlug(patientSlug);
      patient.setIsOld(Boolean.TRUE);
      patientService.save(patient);
    } catch (Exception ex) {
      log.error("Unable to set patient as old {}", patientSlug);
      log.error(ex.getMessage(), ex);
    }
  }

  private FilterRequestDto checkUserAndUpdateFilter(FilterRequestDto filters, User user) {
    if (filters == null) {
      filters = FilterRequestDto.builder().build();
    }
    if (isDoctor(user)) {
      Map<String, ColumnFilter> filterModel = filters.getFilterModel();
      if (filterModel == null) {
        filterModel = new HashMap<>();
      }
      filterModel.put("doctorSlug", ExactMatchColumnFilter.builder().filter(user.getSlug()).build());
      filters.setFilterModel(filterModel);
    }
    return filters;
  }

  private boolean isDoctor(User user) {
    return UserRole.DOCTOR.toString().equals(user.getRoleName());
  }

//  private NoteDto findNote(PrescriptionDto prescriptionDto, String userSlug) {
//    if (prescriptionDto == null || CollectionUtils.isEmpty(prescriptionDto.getNotes())) {
//      return null;
//    }
//    return prescriptionDto.getNotes().stream().filter(note -> userSlug.equals(note.getUserSlug())).findFirst()
//      .orElse(null);
//  }

  private void validateDateAndTime(Date date, LocalTime time) throws EntityInvalidException {
    DateTime appDate = dateUtil.initDateFromDate(date).withHourOfDay(time.getHour()).withMinuteOfHour(time.getMinute())
      .withSecondOfMinute(time.getSecond());
    if (appDate.isBefore(dateUtil.now())) {
      throw invalidError(date, time, AppMessages.Appointment.PAST_DATE_ERROR);
    }
  }

  private void validateSlot(Doctor doc, Appointment appointment) throws EntityInvalidException {
    List<SlotDto> slots = findAvailableSlotsForDoctor(doc, appointment.getDate().getTime(), appointment.getType());
    slots.stream().filter(slot -> slot.getTime().equals(appointment.getTime()) && !slot.getIsAlreadyBooked()).findAny()
      .orElseThrow(
        () -> invalidError(appointment.getDate(), appointment.getTime(), AppMessages.Appointment.NO_SLOT_ERROR));
  }

  private DayOfWeek extractDayOfWeekFromEpoch(Long date) {
    String week = dateUtil.initDateFromLong(date).dayOfWeek().getAsText().toUpperCase();
    return DayOfWeek.valueOf(week);
  }

  private SlotResponseDto empty(Doctor doctor, Long date, AppointmentType type) {
    return SlotResponseDto.builder()
      .doctorSlug(doctor.getSlug())
      .fees(doctor.getAvailabilitySummary().get(type).getFees())
      .date(dateUtil.initDateFromLong(date).toDate())
      .dayOfWeek(extractDayOfWeekFromEpoch(date))
      .earlyMorning(Lists.newArrayList()).midDay(Lists.newArrayList())
      .evening(Lists.newArrayList())
      .build();
  }

  /**
   * Using Equals for Date Filter because the Date in {@link Appointment} is
   * always converted to START of DAY in {@code preCreate()}
   * 
   * @param doctorSlug
   * @param date
   * @return
   */
  private FilterRequestDto appointmentsByDateAndDoctor(String doctorSlug, Long date) {
    Map<String, ColumnFilter> filterMap = ImmutableMap.<String, ColumnFilter>builder()
      .put("doctorSlug", ExactMatchColumnFilter.builder().filter(doctorSlug).build())
      .put("date", DateColumnFilter.builder()
        .type(AppConstants.EQUALS)
        .filter(dateUtil.startOfDay(date).getMillis())
        .build())
      .put("status",
        SetColumnFilter.builder()
          .values(blockingStatusesAsString())
          .build())
      .build();
    return FilterRequestDto.builder().filterModel(filterMap).build();
  }

  private List<AppointmentStatus> blockingStatuses() {
    return Stream.of(AppointmentStatus.values()).filter(AppointmentStatus::isBlocking).collect(Collectors.toList());
  }

  private List<String> blockingStatusesAsString() {
    return blockingStatuses().stream().map(status -> status.toString())
      .collect(Collectors.toList());
  }

  private List<SlotDto> filterTimeSlots(List<LocalTime> slotTimes, List<Appointment> appointments) {
    List<SlotDto> dtos = slotTimes.stream()
      .map(slot -> SlotDto.builder().isAlreadyBooked(Boolean.FALSE).time(slot).build()).collect(Collectors.toList());
    if (CollectionUtils.isEmpty(appointments)) {
      return dtos;
    }
    List<LocalTime> appTimes = appointments.stream().filter(app -> !app.getStatus().equals(AppointmentStatus.CANCELLED))
      .map(app -> {
        return app.getTime();
      }).collect(Collectors.toList());

    return dtos.stream().map(dto -> {
      dto.setIsAlreadyBooked(appTimes.contains(dto.getTime()));
      return dto;
    }).collect(Collectors.toList());

  }

  private List<LocalTime> findDoctorSlotsByDate(Doctor doc, Long date, AppointmentType type) {
    DayOfWeek dayOfWeek = extractDayOfWeekFromEpoch(date);
    Map<DayOfWeek, List<LocalTime>> map = doc.getSlots().get(type);
    if (map == null) {
      return new ArrayList<>();
    }
    List<LocalTime> slotTimes = map.get(dayOfWeek);
    if (slotTimes == null) {
      return new ArrayList<>();
    }
    return slotTimes;
  }

  private SlotResponseDto convertToTimeOfDay(SlotResponseDto dto, List<SlotDto> slotDtos) {
    fillSlots(dto.getEarlyMorning(), slotDtos, 0, 10);
    fillSlots(dto.getMidDay(), slotDtos, 11, 16);
    fillSlots(dto.getEvening(), slotDtos, 17, 23);
    return dto;
  }

  private void fillSlots(List<SlotDto> toFill, List<SlotDto> slotDtos, int startHour, int endHour) {
    final LocalTime start = LocalTime.of(startHour, 0, 0);
    final LocalTime end = LocalTime.of(endHour, 59, 59);
    toFill.addAll(slotDtos.stream()
      .filter(
        dto -> (dto.getTime().isAfter(start) || dto.getTime().compareTo(start) == 0) && dto.getTime().isBefore(end))
      .sorted().collect(Collectors.toList()));
  }

  private List<SlotDto> findAvailableSlotsForDoctor(Doctor doc, Long date, AppointmentType type) {
    List<LocalTime> slotTimes = findDoctorSlotsByDate(doc, date, type);
    List<Appointment> appointments = filterAll(appointmentsByDateAndDoctor(doc.getSlug(), date));
    List<SlotDto> slots = filterTimeSlots(slotTimes, appointments);
    slots = filterBlockedSlots(slots, date, doc.getBlockedSlots());
    return slots;
  }

  private List<SlotDto> filterBlockedSlots(List<SlotDto> slots, Long date, List<DatePair> blockedSlots) {
    if (CollectionUtils.isEmpty(slots)) {
      return new ArrayList<>();
    }
    if (CollectionUtils.isEmpty(blockedSlots)) {
      return slots;
    }

    DateTime dateTime = dateUtil.initDateFromLong(date);

    slots.forEach(slot -> {
      LocalTime time = slot.getTime();
      DateTime time2 = dateTime.withHourOfDay(time.getHour()).withMinuteOfHour(time.getMinute())
        .withSecondOfMinute(time.getSecond());

      for (DatePair pair : blockedSlots) {
        if (pair.isBetween(time2.getMillis())) {
          slot.setIsAlreadyBooked(true);
        }
      }

    });

    return slots;
  }

  private EntityInvalidException invalidError(Date date, LocalTime time, String message) {
    return EntityInvalidException
      .childBuilder().error(message)
      .arg(dateUtil.dateToShortString(date)).arg(time.toString())
      .build();
  }

  private Appointment fetchExistingAppointment(Appointment appointment)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    if (StringUtils.isBlank(appointment.getSlug())) {
      return book(appointment, AppointmentStatus.OPEN);
    }
    return findBySlug(appointment.getSlug());
  }

  private byte[] getQRCodeImage(String text, int width, int height) throws WriterException, IOException {
    QRCodeWriter qrCodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
    ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
    MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
    byte[] pngData = pngOutputStream.toByteArray();
    return pngData;
  }

  private void postAppointmentCompletion(Appointment appointment, PrescriptionDto prescriptionDto) throws EntityNotFoundException {
    appointment.getStatus();
    
    if (prescriptionDto.getAction().equals("DRAFT")) {
    	appointment.setStatus(appointment.getStatus());
        appointment.setCalled(Boolean.FALSE);
    }
    else {
      appointment.setStatus(AppointmentStatus.COMPLETED);
      appointment.setCalled(Boolean.FALSE);
      setPatientAsOld(appointment.getPatientSlug());
      patientService.generateUhid(appointment.getPatientSlug());      
    }
  }

  private Appointment generatePrescriptionPdf(Appointment appointment)
    throws WriterException, IOException, EntityNotFoundException {
    PrescriptionDto prescription = appointment.getPrescription();
    if (prescription == null) {
      throw BaseRuntimeException.builder().error("Kindly save the prescription atleast once before printing.")
        .httpStatus(HttpStatus.FAILED_DEPENDENCY)
        .build();
    }
    Patient patient = patientService.findBySlug(appointment.getPatientSlug());
    final Context ctx = new Context();
    ctx.setVariable("appointmentSlug", appointment.getSlug());
    ctx.setVariable("serverUrl", serverUrl);
    String html = templateEngine.process("index.html", ctx);
    String pdfUrl = pdfService.htmlToPdf(html,
      appointment.getDoctorSlug() + "/" + "prescriptions/" + appointment.getSlug(), patient.getName());
    appointment.setAppointmentPdf(pdfUrl);
    return save(appointment);
  }

  private String disclaimer(Doctor doctor, AppointmentType appointmentType) {
    String disclaimer = null;
    for (AppointmentTypeDto a : doctor.getAppointmentType()) {
      if (appointmentType.equals(a.getLabel()))
        disclaimer = a.getDisclaimer();
    }
    return disclaimer;
  }

  private Map<String, Object> getPrescriptionParams(Appointment appointment)
    throws WriterException, IOException, EntityNotFoundException {
    AppointmentDto dto = appointmentMapper.toDto(appointment);
    TenantDto tenant = TenantData.search(dto.getDoctor().getTenantId());
    PrescriptionDto prescription = appointment.getPrescription();
    if (prescription == null) {
      throw BaseRuntimeException.builder().error("Kindly save the prescription atleast once before printing.")
        .httpStatus(HttpStatus.FAILED_DEPENDENCY)
        .build();
    }
    if (prescription.getQrCode() == null) {
      String appSlug = dto.getSlug();
      String docWebsite = dto.getDoctor().getWebsiteUrl();
      String authUrl = tenant.getAuthWebUrl();
      String url = buildAppointmentUrl(appSlug, docWebsite, authUrl, dto.getPatient().getMobileNumber());

      byte[] qrCodeImage = getQRCodeImage(url, 200, 200);
      prescription.setQrCode(Base64.getEncoder().encodeToString(qrCodeImage));
    }
    Doctor doctorObj = doctorService.findBySlug(appointment.getDoctorSlug());
    String doctorDisclaimer = disclaimer(doctorObj, appointment.getType());
    Map<String, Object> map = new HashMap<>();
    map.put("prescription", prescription);
    map.put("doctor", dto.getDoctor());
    Date nextAppDate = appointment.getNextAppointmentDate();
    if (nextAppDate != null) {
      map.put("nextAppointmentDate;", dateUtil.dateToString(nextAppDate));
    }
    map.put("patient", dto.getPatient());
    map.put("tenant", tenant);
    map.put("disclaimer", doctorDisclaimer);
    map.put("appointmentDate", dateUtil.dateToString(appointment.getDate()));
    if (AppointmentType.AUDIO.equals(appointment.getType())) {
      String disclaimer = disclaimer(doctorObj, AppointmentType.AUDIO);
      map.put("disclaimer", disclaimer);
    }
    return map;
  }

  private void sendMessageForPrecription(Patient patient, Appointment appointment) {
    try {
      Doctor doc = doctorService.findBySlug(appointment.getDoctorSlug());
      String url = buildAppointmentUrl(appointment.getSlug(), doc.getWebsiteUrl(),
        TenantData.search(doc.getTenantId()).getAuthWebUrl(), patient.getMobileNumber());
//      String url = appointment.getAppointmentPdf();
      smsService.sendMessage(
        MessageDto.builder()
          .to(patient.getMobileNumber())
          .templateId("1207161467070888886")
          .subject("Prescription is ready")
          .content(
            "Hi " + patient.getName() + ". Your prescription is ready and can be accessed via the below link:\n" + url)
          .build());
      appointment.setNotificationSent(true);
    } catch (Exception ex) {
      log.error("Error in sendMessageForPrecription for appointment {}", appointment.getSlug());
      log.error(ex.getMessage(), ex);
    }
  }

  private void sendEmailForPrescription(Patient patient, Appointment appointment) {
    sendEmailToPatient(patient, appointment);
    sendEmailForReferral(patient, appointment);
    sendEmailForOthers(patient, appointment);
  }

  private void sendEmailForOthers(Patient patient, Appointment appointment) {
    try {
      if (appointment.getPrescription() == null
        || CollectionUtils.isEmpty(appointment.getPrescription().getOtherEmails())) {
        return;
      }
      Doctor doctor = doctorService.findBySlug(appointment.getDoctorSlug());
      String message = "For your kind reference.\n" +
        "\n" +
        "The prescription for {PATIENT_NAME} for appointment dated {DATE} is ready and eclosed.\n" +
        "\n" +
        "Warm Regards,\n" +
        "{DOCTOR_NAME}\n" +
        "\n" +
        "This mail has been sent by {DOCTOR_NAME}s office.";

      message = message
        .replace("{PATIENT_NAME}", patient.getName())
        .replace("{DOCTOR_NAME}", doctor.getName())
        .replace("{DATE}", dateUtil.dateToStringWithFormat(appointment.getDateTime(), "dd MMM YYYY hh:mm aa"))
        .replace("{REFERRING_DOCTOR_NAME}", appointment.getPrescription().getReferringDoctor())
        .replace("{DOCTOR_EMAIL}", doctor.getEmail())
        .replace("{DOCTOR_MOBILE}", doctor.getMobileNumber());

      AttachmentDto attachment = AttachmentDto.builder().attachmentUrl(appointment.getAppointmentPdf())
        .name("prescription")
        .description(appointment.getSlug()).build();

      for (String email : appointment.getPrescription().getOtherEmails()) {
        emailService.sendMessage(
          MessageDto.builder()
            .to(email)
            .subject("For your kind reference.")
            .content(message)
            .attachment(attachment)
            .build());
      }
      appointment.setNotificationSent(true);
    } catch (Exception ex) {
      log.error("Error in sendEmailForPrescription for appointment {}", appointment.getSlug());
      log.error(ex.getMessage(), ex);
    }
  }

  private void sendEmailToPatient(Patient patient, Appointment appointment) {
    try {
      if (StringUtils.isBlank(patient.getEmail())) {
        return;
      }
      Doctor doctor = doctorService.findBySlug(appointment.getDoctorSlug());
      String message = "Dear {PATIENT_NAME}\n" +
        "\n" +
        "Thank you for your visit to Dr. {DOCTOR_NAME} on {DATE}\n" +
        "A copy of your prescription is enclosed for your reference.\n" +
        "Book your next appointment online at {WEBSITE}\n" +
        "\n" +
        "Warm Regards,\n" +
        "{DOCTOR_NAME}'s Support Team";

      message = message.replace("{PATIENT_NAME}", patient.getName())
        .replace("{DOCTOR_NAME}", doctor.getName())
        .replace("{WEBSITE}", doctor.getWebsiteUrl())
        .replace("{DATE}", dateUtil.dateToStringWithFormat(appointment.getDateTime(), "dd MMM YYYY hh:mm aa"));

      AttachmentDto attachment = AttachmentDto.builder().attachmentUrl(appointment.getAppointmentPdf())
        .name("prescription")
        .description(appointment.getSlug()).build();

      emailService.sendMessage(
        MessageDto.builder()
          .to(patient.getEmail())
          .subject("Your prescription is ready.")
          .content(message)
          .attachment(attachment)
          .build());
      appointment.setNotificationSent(true);
    } catch (Exception ex) {
      log.error("Error in sendEmailForPrescription for appointment {}", appointment.getSlug());
      log.error(ex.getMessage(), ex);
    }
  }

  private void sendEmailForReferral(Patient patient, Appointment appointment) {
    try {
      if (appointment.getPrescription() == null
        || StringUtils.isBlank(appointment.getPrescription().getReferringDocEmail())) {
        return;
      }
      Doctor doctor = doctorService.findBySlug(appointment.getDoctorSlug());
      String message = "Dear Dr. {REFERRING_DOCTOR_NAME},\n" +
        "\n" +
        "Thank you for referring your patient {PATIENT_NAME} to me. I have seen the patient on {DATE} and I am attaching the prescription for your kind reference. I will be glad to answer your questions regarding the same. Please mail them to me at {DOCTOR_EMAIL} or call me at {DOCTOR_MOBILE}. \n"
        +
        "\n" +
        "I will keep you updated regarding the future developments.\n" +
        "\n" +
        "Warm Regards,\n" +
        "{DOCTOR_NAME}\n" +
        "\n" +
        "This mail has been sent by {DOCTOR_NAME}s office.";

      message = message
        .replace("{PATIENT_NAME}", patient.getName())
        .replace("{DOCTOR_NAME}", doctor.getName())
        .replace("{DATE}", dateUtil.dateToStringWithFormat(appointment.getDateTime(), "dd MMM YYYY hh:mm aa"))
        .replace("{REFERRING_DOCTOR_NAME}", appointment.getPrescription().getReferringDoctor())
        .replace("{DOCTOR_EMAIL}", doctor.getEmail())
        .replace("{DOCTOR_MOBILE}", doctor.getMobileNumber());

      AttachmentDto attachment = AttachmentDto.builder().attachmentUrl(appointment.getAppointmentPdf())
        .name("prescription")
        .description(appointment.getSlug()).build();

      emailService.sendMessage(
        MessageDto.builder()
          .to(appointment.getPrescription().getReferringDocEmail())
          .subject("For your kind reference.")
          .content(message)
          .attachment(attachment)
          .build());
      appointment.setNotificationSent(true);
    } catch (Exception ex) {
      log.error("Error in sendEmailForPrescription for appointment {}", appointment.getSlug());
      log.error(ex.getMessage(), ex);
    }
  }

  private String buildAppointmentUrl(String appSlug, String docWebsite, String authUrl, String patientMobile) {
    String url = new StringBuilder(authUrl).append("/auth/login?redirectUrl=").append(docWebsite)
      .append("/home/callback").append("&redirectPath=/app/appointment?slug=").append(appSlug).append("&mobileNumber=")
      .append(patientMobile).toString();
    ShortURLDto shorten = urlShorteningService.shorten(url);
    if (shorten == null || StringUtils.isBlank(shorten.getShortLink())) {
      return url;
    }
    return shorten.getShortLink();
  }

  @Override
  public List<Appointment> patientAppointment(User user, String patientSlug) {
    log.info("getting appointments for patient slug :" + patientSlug);
    List<Appointment> findByPatientSlug = appointmentRepository.findByPatientSlug(patientSlug);
    List<Appointment> appointmentPdfFilter = appointmentPdfFilter(findByPatientSlug);
    return appointmentPdfFilter;
  }

  private List<Appointment> appointmentPdfFilter(List<Appointment> appointments) {
    log.info("filtering only  AppointmentPdf appointments");
    // sort by date
    Collections.sort(appointments);
    return appointments.stream().filter(a -> a.getAppointmentPdf() != null).collect(Collectors.toList());
  }

  @Override
  public Appointment updateAppointment(Appointment appointment)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    validateDateAndTime(appointment.getDate(), appointment.getTime());
    Doctor doc = doctorService.findBySlug(appointment.getDoctorSlug());
    patientService.findBySlug(appointment.getPatientSlug());
    User currentUserData = ThreadContext.getCurrentUserData();
    if (currentUserData == null || UserRole.PATIENT.toString().equalsIgnoreCase(currentUserData.getRoleName())) {
      validateSlot(doc, appointment);
    }
    return super.update(appointment.getId(), appointment);
  }

  @Override
  public void markAsUnavailable(String doctorSlug, Long fromTime, Long toTime) {
    Date from = dateUtil.initDateFromLong(fromTime).toDate();
    Date to = dateUtil.initDateFromLong(toTime).toDate();
    log.info("Marking doctor {} as unavailable from {} to {}", doctorSlug, from, to);
    List<Appointment> appointmentsToCancel = findAppointmentsBasedOnTimeAndDoctorSlug(doctorSlug, fromTime, toTime);
    log.info("Found {} appointments to cancel", appointmentsToCancel.size());
    cancelAppointmentsAndSendSms(appointmentsToCancel);
    blockSlots(doctorSlug, fromTime, toTime);
  }

  private void blockSlots(String doctorSlug, Long fromTime, Long toTime) {
    try {
      Doctor doctor = doctorService.findBySlug(doctorSlug);
      List<DatePair> blockedSlots = doctor.getBlockedSlots();
      if (blockedSlots == null) {
        blockedSlots = new ArrayList<>();
      }
      blockedSlots.add(DatePair.builder()
        .from(fromTime)
        .to(toTime)
        .build());
      doctor.setBlockedSlots(blockedSlots);
      doctorService.save(doctor);
    } catch (Exception ex) {
      log.error("Error while blocking slots for the doctor with slug {}", doctorSlug);
      log.error(ex.getMessage(), ex);
    }
  }

  private void cancelAppointmentsAndSendSms(List<Appointment> appointmentsToCancel) {
    for (Appointment app : appointmentsToCancel) {
      try {
        if (!AppointmentStatus.CANCELLED.equals(app.getStatus())
          || !AppointmentStatus.COMPLETED.equals(app.getStatus())) {
          changeStatus(AppointmentStatus.CANCELLED, app);
          sendSmsForCancellation(app);
        }
      } catch (Exception ex) {
        log.error(ex.getMessage(), ex);
        log.error("Unable to cancelAppointmentsAndSendSms for appointment {}", app.getSlug());
      }
    }
  }

  private void sendSmsForCancellation(Appointment app) throws EntityNotFoundException {

    Patient patient = patientService.findBySlug(app.getPatientSlug());
    Doctor doc = doctorService.findBySlug(app.getDoctorSlug());

    String message = "It is a regret to inform you that your appointment with Dr. "
      + doc.getName() + " scheduled on "
      + dateUtil.dateToStringWithFormat(app.getDate(), DateUtil.DEFAULT_SHORT_DATE_PATTERN) + " "
      + app.getTime() + " has been cancelled. Kindly re-schedule by calling the reception at " + doc.getContactNumber();

    smsService.sendMessage(
      MessageDto.builder()
        .templateId("1207161467082242622")
        .to(patient.getMobileNumber())
        .subject("Appointment Cancelled")
        .content(message)
        .build());
  }

  private List<Appointment> findAppointmentsBasedOnTimeAndDoctorSlug(String doctorSlug, Long fromTime, Long toTime) {
    List<Appointment> all = filterAll(dateBasedFilters(doctorSlug, fromTime, toTime));
    if (all == null) {
      all = new ArrayList<>();
    }
    return all;
  }

  private FilterRequestDto dateBasedFilters(String doctorSlug, Long from, Long to) {
    FilterRequestDto dto = FilterRequestDto
      .builder()
      .filterModel(ImmutableMap.<String, ColumnFilter>builder()
        .put("doctorSlug", ExactMatchColumnFilter.builder().filter(doctorSlug).build())
        .put("dateTime",
          DateColumnFilter.builder().filter(from).filterTo(to).type(AppConstants.IN_RANGE).exactMatch(true).build())
        .build())
      .build();
    return dto;
  }

  @Override
  public Appointment addDiscount(String appointmentSlug, BigDecimal discount, Boolean isFamily, Boolean isFriend)
    throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidException {
    Appointment app = findBySlug(appointmentSlug);
    app.setDiscount(discount);
    app.setIsFamily(isFamily);
    app.setIsFriends(isFriend);
    app = save(app);
    createRefundEntry(app);
    return app;
  }

  private void createRefundEntry(Appointment app) throws EntityAlreadyExistsException, EntityInvalidException {
    orderService.createOrderEntry(
      Order.builder()
        .patientSlug(app.getPatientSlug())
        .doctorSlug(app.getDoctorSlug())
        .status(OrderStatus.REFUND)
        .paymentDetails(
          PaymentDto.builder().amount(app.getDiscount())
            .mode(PaymentMode.PAY_AT_HOSPITAL)
            .build())
        .products(
          OrderProductDto.builder()
            .productId(app.getSlug())
            .productType(ProductType.APPOINTMENT)
            .build())
        .build(),
      OrderStatus.REFUND);
  }

  private void createAppointmentPayment(Appointment app) {
    try {
      orderService.createOrderEntry(
        Order.builder()
          .patientSlug(app.getPatientSlug())
          .doctorSlug(app.getDoctorSlug())
          .status(OrderStatus.CONFIRMED)
          .paymentDetails(
            PaymentDto.builder().amount(app.getFees())
              .mode(PaymentMode.PAY_AT_HOSPITAL)
              .paymentMode(app.getMode())
              .build())
          .products(
            OrderProductDto.builder()
              .productId(app.getSlug())
              .productType(ProductType.APPOINTMENT)
              .build())
          .build(),
        OrderStatus.CONFIRMED);
    } catch (Exception ex) {

    }
  }

  @Override
  public Map<String, Object> getReceiptParams(String appointmentSlug) throws EntityNotFoundException {
    Appointment appointment = findBySlug(appointmentSlug);
    AppointmentDto dto = appointmentMapper.toDto(appointment);
    TenantDto tenant = TenantData.search(dto.getDoctor().getTenantId());
    Map<String, Object> map = new HashMap<>();
    map.put("appointment", dto);
    map.put("patient", dto.getPatient());
    map.put("tenant", tenant);
    map.put("appointmentDate", dateUtil.now().getMillis());
    map.put("totalAmount", dto.getDiscount() == null ? dto.getFees() : dto.getFees().subtract(dto.getDiscount()));
    return map;
  }

  @Override
  public void updateDate(FilterRequestDto dto, final int monthsToAdjust, User user) {
    List<Appointment> list = filterAllByUser(dto, user);
    List<Appointment> updatedList = list.stream()
      .filter(app -> StringUtils.isBlank(app.getReceiptPdf()))
      .map(app -> {
        app.setDateTime(dateUtil.initDateFromDate(app.getDateTime()).minusMonths(monthsToAdjust).toDate());
        app.setDate(dateUtil.startOfDay(app.getDateTime().getTime()).toDate());
        return app;
      }).collect(Collectors.toList());

    saveAll(updatedList);
  }

}