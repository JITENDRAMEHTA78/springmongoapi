package com.sixsprints.eclinic.util.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.javers.common.collections.Arrays;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;
import com.sixsprints.eclinic.enums.UserRole;

public class DoctorFieldData {
  public static List<FieldDto> field() {
    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;

    fields.add(
      FieldDto.builder().name("slug").displayName("Id").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Id")).build());

    fields.add(
      FieldDto.builder().name("mobileNumber").displayName("Mobile Number").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Mobile Number")).build());

    fields.add(
      FieldDto.builder().name("name").displayName("Name").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Name")).build());

    fields.add(
      FieldDto.builder().name("email").displayName("Email").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Email")).build());

    fields.add(
      FieldDto.builder().name("speciality").displayName("Speciality").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Speciality")).build());

    fields.add(
      FieldDto.builder().name("roleName").displayName("Role").sequence(i++).dataType(DataType.ENUM)
        .allValues(Arrays.asList(UserRole.values()))
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Role")).build());

    return fields;
  }
}
