package com.sixsprints.eclinic.util.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;

public class AppointmentFieldData {

  public static final List<FieldDto> fields() {

    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;

    fields.add(
      FieldDto.builder().name("patient.uhid").displayName("Patient UHID").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Patient UHID")).build());

    fields.add(
      FieldDto.builder().name("patient.name").displayName("Patient Name").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Patient Name")).build());

    fields.add(
      FieldDto.builder().name("patient.email").displayName("Patient Email").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Patient Email")).build());

    fields.add(
      FieldDto.builder().name("patient.mobileNumber").displayName("Patient MobileNumber").sequence(i++)
        .dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Patient MobileNumber")).build());

    fields.add(
      FieldDto.builder().name("doctor.name").displayName("Doctor Name").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Doctor Name")).build());

    fields.add(
      FieldDto.builder().name("dateTime").displayName("Appointment Date").sequence(i++).dataType(DataType.DATE)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Appoinment Date")).build());

    fields.add(
      FieldDto.builder().name("mode").displayName("Mode").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Mode")).build());

    fields.add(
      FieldDto.builder().name("fees").displayName("Fees").sequence(i++).dataType(DataType.NUMBER)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Fees")).build());

    fields.add(
      FieldDto.builder().name("receiptPdf").displayName("Receipt URL").sequence(i++).dataType(DataType.LINK)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Receipt URL")).build());

    fields.add(
      FieldDto.builder().name("prescription.prov").displayName("Prov").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Prov")).build());

    fields.add(
      FieldDto.builder().name("prescription.hopi").displayName("HOPI").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "HOPI")).build());

    fields.add(
      FieldDto.builder().name("prescription.chiefComplaints").displayName("ChiefComplaints").sequence(i++)
        .dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "ChiefComplaints")).build());

    fields.add(
      FieldDto.builder().name("prescription.treatment").displayName("Treatment").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Treatment")).build());

    fields.add(
      FieldDto.builder().name("prescription.surgicalPlan").displayName("Surgical Plan").sequence(i++)
        .dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Surgical Plan")).build());

    fields.add(
      FieldDto.builder().name("prescription.examination").displayName("Examination").sequence(i++)
        .dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Examination")).build());

    fields.add(
      FieldDto.builder().name("prescription.investigation").displayName("Investigation").sequence(i++)
        .dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Investigation")).build());

    return fields;
  }
}
