package com.sixsprints.eclinic.util.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.javers.common.collections.Arrays;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;
import com.sixsprints.eclinic.enums.Gender;

public class PatientFieldData {

  public static final List<FieldDto> fields() {

    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;

    fields.add(
      FieldDto.builder().name("slug").displayName("Id").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Id")).isLocked(true).build());

    fields.add(
      FieldDto.builder().name("mobileNumber").displayName("Mobile Number").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Mobile Number")).isLocked(true).build());

    fields.add(
      FieldDto.builder().name("uhid").displayName("UHID").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "UHID")).isLocked(true).build());

    fields.add(
      FieldDto.builder().name("name").displayName("Name").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Name")).build());

    fields.add(
      FieldDto.builder().name("email").displayName("Email").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Email")).build());

    fields.add(
      FieldDto.builder().name("dob").displayName("DOB").sequence(i++).dataType(DataType.DATE)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "DOB")).build());

    fields.add(
      FieldDto.builder().name("gender").displayName("Gender").sequence(i++).dataType(DataType.ENUM)
        .allValues(Arrays.asList(Gender.values())).enumClass(Gender.class)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Gender")).build());

    fields.add(
      FieldDto.builder().name("isOld").displayName("Is Old Patient?").sequence(i++).dataType(DataType.BOOLEAN)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Is Old Patient?")).build());

    return fields;
  }
}
