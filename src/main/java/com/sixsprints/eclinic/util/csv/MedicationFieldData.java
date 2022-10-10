package com.sixsprints.eclinic.util.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;

public class MedicationFieldData {

  public static List<FieldDto> field() {
    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;

    fields.add(
      FieldDto.builder().name("name").displayName("Name").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Name")).build());

    fields.add(
      FieldDto.builder().name("dose").displayName("Dose").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Name")).build());

    fields.add(
      FieldDto.builder().name("frequency").displayName("Frequency").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Frequency")).build());

    fields.add(
      FieldDto.builder().name("num").displayName("Num").sequence(i++).dataType(DataType.NUMBER)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Num")).build());

    fields.add(
      FieldDto.builder().name("interval").displayName("Interval").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Interval")).build());

    fields.add(
      FieldDto.builder().name("remarks").displayName("Remarks").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Remarks")).build());

    return fields;
  }
}
