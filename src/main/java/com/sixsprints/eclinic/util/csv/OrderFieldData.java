package com.sixsprints.eclinic.util.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;
import com.sixsprints.eclinic.enums.OrderStatus;
import com.sixsprints.eclinic.enums.ProductType;

public class OrderFieldData {

  public static final List<FieldDto> fields() {

    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;

    fields.add(
      FieldDto.builder().name("patient.name").displayName("Name").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Name")).isPinned(true).build());

    fields.add(
      FieldDto.builder().name("patient.mobileNumber").displayName("Number").sequence(i++)
        .dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Number")).isPinned(true).build());

    fields.add(
      FieldDto.builder().name("doctor.name").displayName("Doctor").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Doctor")).isPinned(false).build());

    fields.add(
      FieldDto.builder().name("patient.uhid").displayName("UHID").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "UHID")).build());

    fields.add(
      FieldDto.builder().name("products.productType").displayName("Product Type").sequence(i++).dataType(DataType.ENUM)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Product Type"))
        .allValues(Arrays.asList(ProductType.values())).enumClass(ProductType.class).build());

    fields.add(
      FieldDto.builder().name("products.productId").displayName("Product ID").sequence(i++).dataType(DataType.ENUM)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Product ID"))
        .allValues(Arrays.asList(ProductType.values())).enumClass(ProductType.class).build());

    fields.add(
      FieldDto.builder().name("appointmentDate").displayName("Date").sequence(i++).dataType(DataType.DATE)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Date")).build());

    fields.add(
      FieldDto.builder().name("paymentDetails.mode").displayName("Mode").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Mode")).build());

    fields.add(
      FieldDto.builder().name("paymentDetails.paymentMode").displayName("Mode (At Hospital)").sequence(i++)
        .dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Mode (At Hospital)")).build());

    fields.add(
      FieldDto.builder().name("paymentDetails.utr").displayName("TransactionId").sequence(i++).dataType(DataType.TEXT)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "TransactionId")).build());

    fields.add(
      FieldDto.builder().name("paymentDetails.amount").displayName("Amount").sequence(i++).dataType(DataType.NUMBER)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Amount")).build());

    fields.add(
      FieldDto.builder().name("status").displayName("Status").sequence(i++).dataType(DataType.ENUM)
        .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Status"))
        .allValues(Arrays.asList(OrderStatus.values())).build());

    fields.add(
      FieldDto.builder().name("paymentDetails.isReceiptPrinted").displayName("Receipt Printed").sequence(i++)
        .dataType(DataType.BOOLEAN).localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Receipt Printed"))
        .build());

    return fields;
  }
}
