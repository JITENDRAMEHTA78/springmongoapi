
package com.sixsprints.eclinic.util.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;

public class EmrTemplateFieldData {

  public static final List<FieldDto> fields() {

    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;

    fields.add(
      FieldDto.builder().name("slug").displayName("ID").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "ID")).isPinned(true).build());

    fields.add(
      FieldDto.builder().name("name").displayName("Name").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Name")).isPinned(false).build());

    fields.add(
      FieldDto.builder().name("speciality.label").displayName("Speciality").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Speciality")).isPinned(false).build());

    fields.add(
      FieldDto.builder().name("dateCreated").displayName("Date Created").sequence(i++).dataType(DataType.DATE)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Date Created")).isPinned(false).build());

    fields.add(
      FieldDto.builder().name("createdByUser.name").displayName("Created By").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Created By")).isPinned(false).build());

    fields.add(
      FieldDto.builder().name("lastModifiedByUser.name").displayName("Last Modified By").sequence(i++)
        .dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Last Modified By")).isPinned(false).build());

    fields.add(
      FieldDto.builder().name("dateModified").displayName("Date Modified").sequence(i++).dataType(DataType.DATE)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Date Modified")).isPinned(false).build());

    return fields;
  }
}
