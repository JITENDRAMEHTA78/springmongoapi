package com.sixsprints.eclinic.service.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableList;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.utils.EncryptionUtil;
import com.sixsprints.eclinic.domain.Branch;
import com.sixsprints.eclinic.domain.Role;
import com.sixsprints.eclinic.domain.user.Doctor;
import com.sixsprints.eclinic.dto.PermissionDto;
import com.sixsprints.eclinic.enums.AccessPermission;
import com.sixsprints.eclinic.enums.EntityPermission;
import com.sixsprints.eclinic.enums.UserRole;
import com.sixsprints.eclinic.service.BranchService;
import com.sixsprints.eclinic.service.HospitalService;
import com.sixsprints.eclinic.service.RoleService;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.user.UserService;
import com.sixsprints.eclinic.util.PaymentOptionDataBuilder;
import com.sixsprints.eclinic.util.PhoneNumberUtil;
import com.sixsprints.eclinic.util.SocialMediaBuilder;

@Service
public class DataBuilderService {

  @Resource
  private RoleService roleService;

  @Resource
  private HospitalService hospitalService;

  @Resource
  private BranchService branchService;

  @Resource
  private DoctorService doctorService;

  @Resource
  private UserService userService;

  @Value("${domain.name}")
  private String domainName;

  @Autowired
  private PaymentOptionDataBuilder paymentOptionDataBuilder;

  public void initLabAndQUser() throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {
	  
	  Role labRole = roleService.create(Role.builder()
      .name(UserRole.LAB.toString())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.DASHBOARD)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.APPOINTMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PATIENT)
        .accessPermission(AccessPermission.ANY)
        .build())

      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CONSULTATION)
        .accessPermission(AccessPermission.UPDATE)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHECK_IN)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.VITALS_NOTES)
        .accessPermission(AccessPermission.ANY)
        .build())
      .build());
	  
	  
	  Role qRole = roleService.create(Role.builder()
      .name(UserRole.QUEUE.toString())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.DASHBOARD)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.APPOINTMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PATIENT)
        .accessPermission(AccessPermission.ANY)
        .build())

      
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHECK_IN)
        .accessPermission(AccessPermission.ANY)
        .build())
      .build());
	  
	  //call to check whether lab manger record available
	  
    // doctorService.find
    doctorService.create(Doctor.builder().mobileNumber("9999999986")
      .password("lab.rakesh.kumar").name("Rakesh Kumar").email("pathcelldiagnostics@gmail.com")
      .roleName(labRole.getName())
      .build());

    doctorService.create(Doctor.builder().mobileNumber("9999999987")
      .password("lab.dharmender.kumar").name("Dharmender Kumar").email("pathcelldiagnostics1@gmail.com")
      .roleName(labRole.getName())
      .build());

    //call to check whether q manger record available
    doctorService.create(Doctor.builder().mobileNumber("9999999988")
      .password("9999999988").name("Jyoti Hospital").email("tpajyotihospital@gmail.com")
      .roleName(qRole.getName())
      .build());
  }

  public void init() throws EntityAlreadyExistsException, EntityInvalidException, EntityNotFoundException {

    Role adminRole = roleService.create(Role.builder()
      .name(UserRole.ADMIN.toString())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.DASHBOARD)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.APPOINTMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PATIENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.DOCTOR)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHAT_PACKAGE)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PAYMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHECK_IN)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.VITALS_NOTES)
        .accessPermission(AccessPermission.ANY)
        .build())
      .build());

    Role doctorRole = roleService.create(Role.builder()
      .name(UserRole.DOCTOR.toString())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.DASHBOARD)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.APPOINTMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CONSULTATION)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PATIENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.DOCTOR)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PAYMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHAT_SESSION)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHAT_PACKAGE)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHECK_IN)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.VITALS_NOTES)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CALL_NEXT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .build());

    Role nurseRole = roleService.create(Role.builder()
      .name(UserRole.NURSE.toString())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.DASHBOARD)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.APPOINTMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PATIENT)
        .accessPermission(AccessPermission.ANY)
        .build())

      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CONSULTATION)
        .accessPermission(AccessPermission.UPDATE)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHECK_IN)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.VITALS_NOTES)
        .accessPermission(AccessPermission.ANY)
        .build())
      .build());

    Role assistantRole = roleService.create(Role.builder()
      .name(UserRole.ASSISTANT.toString())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.DASHBOARD)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.APPOINTMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CONSULTATION)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHECK_IN)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PATIENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .build());

    Role receptionistRole = roleService.create(Role.builder()
      .name(UserRole.RECEPTIONIST.toString())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.DASHBOARD)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.APPOINTMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PAYMENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.CHECK_IN)
        .accessPermission(AccessPermission.ANY)
        .build())
      .permission(PermissionDto.builder()
        .entityPermission(EntityPermission.PATIENT)
        .accessPermission(AccessPermission.ANY)
        .build())
      .build());

    roleService.create(Role.builder()
      .name(UserRole.PATIENT.toString())
      .permission(PermissionDto.builder()
        .accessPermission(AccessPermission.ANY)
        .entityPermission(EntityPermission.ANY)
        .build())
      .build());

    Branch branch = branchService.create(Branch.builder().address("464, Udyog Vihar Phase V, Gurgaon")
      .contactNumber("9999999992").contactEmailId("").build());

    doctorService.create(raman(doctorRole.getName(), branch.getSlug()));

    doctorService.create(milind(doctorRole.getName(), branch.getSlug()));

    doctorService.create(rpsingh(doctorRole.getName(), branch.getSlug()));

    doctorService.create(kirti(doctorRole, branch));

    doctorService.create(astha(doctorRole, branch));

    doctorService.create(Doctor.builder().mobileNumber("9999999996")
      .password("9999999996").name("Nurse 1").email("nurse@gmail.com")
      .roleName(nurseRole.getName())
      .build());

    doctorService.create(Doctor.builder().mobileNumber("9999999997")
      .password("9999999997").name("Admin").email("admin@gmail.com")
      .roleName(adminRole.getName())
      .build());

    doctorService.create(Doctor.builder().mobileNumber("9999999998")
      .password("9999999998").name("CA 1").email("assistant@gmail.com")
      .roleName(assistantRole.getName())
      .build());

    doctorService.create(Doctor.builder().mobileNumber("9999999999")
      .password("9999999999").name("R 1").email("receptionist@gmail.com")
      .roleName(receptionistRole.getName())
      .build());

  }

  public void updateDoctorInfo() throws EntityNotFoundException, EntityAlreadyExistsException {
    Doctor raman = doctorService.findByMobileNumber("9868378312");
    Doctor raman2 = raman(raman.getRoleName(), raman.getBranchSlugList().get(0));

    raman2.setTenantId(raman.getTenantId());
    raman2.setPassword(EncryptionUtil.encrypt(raman2.getPassword()));
    raman2.setMobileNumber(PhoneNumberUtil.cleanNumber(raman2.getMobileNumber()));

    doctorService.update(raman.getId(), raman2);

    Doctor milind = doctorService.findByMobileNumber("9053267890");
    Doctor milind2 = milind(milind.getRoleName(), milind.getBranchSlugList().get(0));

    milind2.setTenantId(milind.getTenantId());
    milind2.setPassword(EncryptionUtil.encrypt(milind2.getPassword()));
    milind2.setMobileNumber(PhoneNumberUtil.cleanNumber(milind2.getMobileNumber()));

    doctorService.update(milind.getId(), milind2);

    Doctor rpsingh = doctorService.findByMobileNumber("9868430819");
    Doctor rpsingh2 = rpsingh(rpsingh.getRoleName(), rpsingh.getBranchSlugList().get(0));

    rpsingh2.setTenantId(rpsingh.getTenantId());
    rpsingh2.setPassword(EncryptionUtil.encrypt(rpsingh2.getPassword()));
    rpsingh2.setMobileNumber(PhoneNumberUtil.cleanNumber(rpsingh2.getMobileNumber()));

    doctorService.update(rpsingh.getId(), rpsingh2);

  }

  private Doctor astha(Role doctorRole, Branch branch) {
    return Doctor.builder().mobileNumber("9999999995")
      .password("9999999995").name("Astha Dayal").email("dr.aastha@gmail.com").roleName(doctorRole.getName())
      .contactNumber("9999999995").branchSlugList(ImmutableList.<String>of(branch.getSlug()))
      .speciality("gynaecology")
//      .slots(slots())
//      .availabilitySummary(availability())
//      .fees(BigDecimal.valueOf(400))
      .roomNumber("005")
      .profilePic("https://images1-fabric.practo.com/doctor/329307/dr-astha-dayal-5b9f8e2bb329a.jpg")
      .bio(
        "Dr. Astha has completed her medical education and specialist training in Obstetrics & Gynaecology from Maulana Azad Medical College, New Delhi and MRCOG from Royal College of Obstetrics & Gynaecology, London(UK). She is a member of Royal College of Obstetricians & Gynaecologists, London (RCOG), Federation of Obstetric and Gynaecological Societies of India (FOGSI), World Association of Laparoscopic Surgeons (WALS) and Gurgaon Obstetrics & Gynaecologic Society (GOGS). The doctor specializes in Normal and Cesarean deliveries, MTP and minimal invasive surgeries.")
      .shortTitle(
        "MBBS, MS - Obstetrics & Gynaecology, MRCOG(UK), Diploma in Laparoscopy Gynecologist, Obstetrician, 17 Years Experience Overall (12 years as specialist)")
      .paymentOptions(paymentOptionDataBuilder.paymentOptions())
      .registrationNo("DMC/R/03705")
      .signature("https://storage.googleapis.com/eclinic_assets/signatures/signature.png")
      .websiteKey("astha")
      .websiteUrl("https://astha." + domainName)
      .socialMedia(SocialMediaBuilder.socialMedia())
      .build();
  }

  private Doctor kirti(Role doctorRole, Branch branch) {
    return Doctor.builder().mobileNumber("9999999994")
      .password("9999999994").name("Kirti Vijay Rathore").email("dr.kirti@gmail.com").roleName(doctorRole.getName())
      .contactNumber("9999999994").branchSlugList(ImmutableList.<String>of(branch.getSlug()))
      .speciality("pathology")
//      .slots(slotsRandom(30))
//      .availabilitySummary(availability())
//      .fees(BigDecimal.valueOf(400))
      .roomNumber("004")
      .profilePic("https://images1-fabric.practo.com/doctor/644594/dr-kirti-vijay-rathore-5a9836d714b04.JPG")
      .bio(
        "Dr. Kirti Vijay Rathore is a doctor with special interest in Diabetes Mellitus. Due to her deep understanding in the pathology of diabetes her focus in treating patients with Diabetes Mellitus is on encouraging the natural mechanisms that control sugar to acheive natural diabetic control. With a team of specialty educators and endocrinologists, Dr. Kirti provides comprehensive diabetes care with an algorithm based approach.")
      .shortTitle(
        "MBBS, MD - Pathology, General Physician, Pathologist, 9 Years Experience Overall (5 years as specialist)")
      .paymentOptions(paymentOptionDataBuilder.paymentOptions())
      .registrationNo("DMC/R/03704")
      .signature("https://storage.googleapis.com/eclinic_assets/signatures/signature.png")
      .websiteKey("kirti")
      .websiteUrl("https://kirti." + domainName)
      .socialMedia(SocialMediaBuilder.socialMedia())
      .build();
  }

  private Doctor rpsingh(String doctorRole, String branch) {
    return Doctor.builder()
      .mobileNumber("9868430819")
      .password("drrpsinghtanwar")
      .name("R.P. Singh")
      .email("dr.rpsingh@gmail.com")
      .roleName(doctorRole)
      .contactNumber("+918383812737")
      .branchSlugList(ImmutableList.<String>of(branch))
      .speciality("general-surgery")
//      .slots(slotsRpSingh())
//      .availabilitySummary(availabilityRpSingh())
//      .fees(BigDecimal.valueOf(300))
      .roomNumber("003")
      .profilePic("https://storage.googleapis.com/eclinic_assets/data/doctor/rpsingh/picp.jpg")
      .bio(
        "Dr R P Singh is a renowned Surgeon with special interest in Urology and Andrology. He has served as Senior Medical Officer in Haryana Civil Medical Services (HCMS) for 5 years and worked as the main surgeon in Gurgaon Civil Hospital for many years. Presently. He is the Chairman of Jyoti Hospital and Urology Center. He provides medical advice at very nominal rates to patients across all fields of medicine.")
      .shortTitle("MBBS, MS, FIAGES")
      .paymentOptions(paymentOptionDataBuilder.paymentOptions())
      .registrationNo("7290")
      .signature("https://storage.googleapis.com/eclinic_assets/data/doctor/rpsingh/sign.png")
      .websiteKey("rpsingh")
      .websiteUrl("https://rpsingh." + domainName)
      .socialMedia(new ArrayList<>())
      .build();
  }

  private Doctor milind(String doctorRole, String branch) {
    return Doctor.builder()
      .mobileNumber("9053267890")
      .password("dr.milindtanwar")
      .name("Milind Tanwar")
      .email("dr.milindtanwar@gmail.com")
      .roleName(doctorRole)
      .contactNumber("+918383812737")
      .branchSlugList(ImmutableList.<String>of(branch))
      .speciality("orthopedic")
//      .slots(slots2())
//      .availabilitySummary(availability2())
//      .fees(BigDecimal.valueOf(400))
      .roomNumber("002")
      .profilePic("https://storage.googleapis.com/eclinic_assets/data/doctor/milindtanwar/picp.jpg")
      .bio(
        "Dr. Milind is a graduate from Post Graduate Institute of Medical Sciences, Rohtak and has wide experience in paediatric orthopaedics & orthopaedic oncology along with intra & peri-articular trauma. He also has vast experience in both operative and conservative management of degenerative, infective and traumatic disorders of the spine & minimally invasive spine (MIS) surgery.")
      .shortTitle(
        "MBBS, MS, Fellowship in Arthroplasty and Arthroscopy")
      .paymentOptions(paymentOptionDataBuilder.paymentOptions2())
      .registrationNo("DMC76763")
      .signature("https://storage.googleapis.com/eclinic_assets/data/doctor/milindtanwar/sign.png")
      .websiteKey("milind")
      .websiteUrl("https://milind." + domainName)
      .socialMedia(SocialMediaBuilder.socialMedia2())
      .build();
  }

  private Doctor raman(String doctorRole, String branch) {
    return Doctor.builder().mobileNumber("9868378312")
      .password("dr.ramantanwar").name("Raman Tanwar").email("dr.ramantanwar@gmail.com").roleName(doctorRole)
      .contactNumber("+918383812737").branchSlugList(ImmutableList.<String>of(branch))
      .speciality("urology")
//      .slots(slots())
//      .availabilitySummary(availability())
//      .fees(BigDecimal.valueOf(500))
      .roomNumber("001")
      .profilePic("https://storage.googleapis.com/eclinic_assets/data/doctor/ramantanwar/pic.jpg")
      .bio(
        "Dr. Raman Tanwar is an Internationally renowned Urologist and fellowship trained Andrologist with 5 Gold Medals in Urology to his credit. He has been awarded by the British Medical Association, International Society of Sexual Medicine, Council of Sex Education and Parenthood International, Indian Science Congress, Indian Medical Association, Urological Society of India, Men's Health Society of India and many other national societies.")
      .shortTitle(
        "MBBS, MS, FMAS, MCh (Urology), Fellowship in Andrology and Men's Health")
      .paymentOptions(paymentOptionDataBuilder.paymentOptions())
      .registrationNo("DMC/R/03703")
      .signature("https://storage.googleapis.com/eclinic_assets/data/doctor/ramantanwar/sign.png")
      .websiteKey("raman")
      .websiteUrl("https://raman." + domainName)
      .socialMedia(SocialMediaBuilder.socialMedia())
      .designation("")
      .build();
  }

//  private Map<String, String> availability() {
//    return ImmutableMap.<String, String>builder()
//      .put("Mon - Sat", "11.30pm - 2pm")
//      .build();
//  }
//
//  private Map<String, String> availabilityRpSingh() {
//    return ImmutableMap.<String, String>builder()
//      .put("Mon - Fri", "6pm - 7pm")
//      .build();
//  }
//
//  private Map<String, String> availability2() {
//    return ImmutableMap.<String, String>builder()
//      .put("Mon - Sat", "12pm - 2pm")
//      .build();
//  }
//
//  private Map<DayOfWeek, List<LocalTime>> slots() {
//    return ImmutableMap.<DayOfWeek, List<LocalTime>>builder()
//      .put(DayOfWeek.MONDAY, time(11, 30, 14, 0, 10))
//      .put(DayOfWeek.TUESDAY, time(11, 30, 14, 0, 10))
//      .put(DayOfWeek.WEDNESDAY, time(11, 30, 14, 0, 10))
//      .put(DayOfWeek.THURSDAY, time(11, 30, 14, 0, 10))
//      .put(DayOfWeek.FRIDAY, time(11, 30, 14, 0, 10))
//      .put(DayOfWeek.SATURDAY, time(11, 30, 14, 0, 10))
//      .put(DayOfWeek.SUNDAY, new ArrayList<>())
//      .build();
//  }
//
//  private Map<DayOfWeek, List<LocalTime>> slotsRpSingh() {
//    return ImmutableMap.<DayOfWeek, List<LocalTime>>builder()
//      .put(DayOfWeek.MONDAY, time(18, 0, 19, 0, 10))
//      .put(DayOfWeek.TUESDAY, time(18, 0, 19, 0, 10))
//      .put(DayOfWeek.WEDNESDAY, time(18, 0, 19, 0, 10))
//      .put(DayOfWeek.THURSDAY, time(18, 0, 19, 0, 10))
//      .put(DayOfWeek.FRIDAY, time(18, 0, 19, 0, 10))
//      .put(DayOfWeek.SATURDAY, new ArrayList<>())
//      .put(DayOfWeek.SUNDAY, new ArrayList<>())
//      .build();
//  }
//
//  private Map<DayOfWeek, List<LocalTime>> slots2() {
//    return ImmutableMap.<DayOfWeek, List<LocalTime>>builder()
//      .put(DayOfWeek.MONDAY, time(12, 0, 14, 0, 10))
//      .put(DayOfWeek.TUESDAY, time(12, 0, 14, 0, 10))
//      .put(DayOfWeek.WEDNESDAY, time(12, 0, 14, 0, 10))
//      .put(DayOfWeek.THURSDAY, time(12, 0, 14, 0, 10))
//      .put(DayOfWeek.FRIDAY, time(12, 0, 14, 0, 10))
//      .put(DayOfWeek.SATURDAY, time(12, 0, 14, 0, 10))
//      .put(DayOfWeek.SUNDAY, new ArrayList<>())
//      .build();
//  }
//
//  private Map<DayOfWeek, List<LocalTime>> slotsRandom(int mins) {
//    return ImmutableMap.<DayOfWeek, List<LocalTime>>builder()
//      .put(DayOfWeek.MONDAY, times(mins))
//      .put(DayOfWeek.TUESDAY, times(mins))
//      .put(DayOfWeek.WEDNESDAY, times(mins))
//      .put(DayOfWeek.THURSDAY, times(mins))
//      .put(DayOfWeek.FRIDAY, times(mins))
//      .put(DayOfWeek.SATURDAY, times(mins))
//      .put(DayOfWeek.SUNDAY, new ArrayList<>())
//      .build();
//  }
//
//  private List<LocalTime> time(int startHour, int startMin, int endHour, int endMin, int duration) {
//    Set<LocalTime> set = new HashSet<>();
//    LocalTime startTime = LocalTime.of(startHour, startMin);
//    LocalTime endTime = LocalTime.of(endHour, endMin);
//    LocalTime time = startTime.plusMinutes(duration);
//    set.add(startTime);
//    set.add(endTime);
//    while (time.isBefore(endTime)) {
//      set.add(LocalTime.of(time.getHour(), time.getMinute()));
//      time = time.plusMinutes(duration);
//    }
//    List<LocalTime> list = new ArrayList<LocalTime>(set);
//    Collections.sort(list);
//    return list;
//  }
//
//  private List<LocalTime> times(int min) {
//
//    Integer[] mins = { 0, 15, 30, 45 };
//    if (min == 30) {
//      mins = new Integer[] { 0, 30 };
//    }
//    Set<LocalTime> set = new HashSet<>();
//    for (int i = 0; i < 10; i++) {
//      set.add(LocalTime.of(RandomUtil.randomInt(0, 24), mins[RandomUtil.randomInt(0, mins.length)]));
//    }
//    List<LocalTime> list = new ArrayList<LocalTime>(set);
//    Collections.sort(list);
//    return list;
//  }

  public List<Doctor> modifySpeciality() throws EntityNotFoundException, EntityAlreadyExistsException {
    List<Doctor> findAll = doctorService.findAll();
    for (Doctor d : findAll) {
      if ("General Surgery".equals(d.getSpeciality())) {
        d.setSpeciality("general-surgery");
      } else if ("Orthopedic".equals(d.getSpeciality())) {
        d.setSpeciality("orthopedic");
      } else if ("Urology".equals(d.getSpeciality())) {
        d.setSpeciality("urology");
      } else if ("Pathology".equals(d.getSpeciality())) {
        d.setSpeciality("pathology");
      } else if ("Gynaecology".equals(d.getSpeciality())) {
        d.setSpeciality("gynaecology");
      }
    }
    doctorService.saveAll(findAll);
    return findAll;
  }

  public Doctor addDoctor(String name, String mobile, String website, String regNo)
    throws EntityAlreadyExistsException, EntityInvalidException {

    Role doctorRole = roleService.findByName(UserRole.DOCTOR.toString());
    Doctor doc = Doctor.builder().mobileNumber(mobile)
      .password(mobile)
      .name(name)
      .email("vikramshahbatra@gmail.com")
      .roleName(doctorRole.getName())
      .contactNumber("+918383812737")
      .branchSlugList(ImmutableList.<String>of(branchService.findAll().get(0).getSlug()))
      .speciality("urology")
      .roomNumber("001")
      .profilePic("https://storage.googleapis.com/eclinic_assets/data/doctor/ramantanwar/pic.jpg")
      .bio("Sample Bio.")
      .shortTitle("Sample short title")
      .paymentOptions(paymentOptionDataBuilder.paymentOptions())
      .registrationNo(regNo)
      .signature("https://storage.googleapis.com/eclinic_assets/data/doctor/ramantanwar/sign.png")
      .websiteKey(website)
      .websiteUrl("https://" + website + "." + domainName)
      .designation("")
      .build();

    return doctorService.create(doc);
  }

}
