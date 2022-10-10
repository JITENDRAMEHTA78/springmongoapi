package com.sixsprints.eclinic.util.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;

public class ChatPackageFieldData {
  public static final List<FieldDto> fields() {

    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;

    fields.add(
      FieldDto.builder().name("packageName").displayName("Package Name").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Package Name")).build());

    fields.add(
      FieldDto.builder().name("totalQuestions").displayName("Total Questions").sequence(i++).dataType(DataType.NUMBER)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Total Questions")).build());

    fields.add(
      FieldDto.builder().name("totalDays").displayName("Total Days").sequence(i++).dataType(DataType.NUMBER)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Total Days")).build());

    fields.add(
      FieldDto.builder().name("amount").displayName("Amount").sequence(i++).dataType(DataType.NUMBER)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Amount")).build());

    return fields;
  }
}
