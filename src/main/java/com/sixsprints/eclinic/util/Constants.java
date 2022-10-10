package com.sixsprints.eclinic.util;

import java.util.Locale;

import com.sixsprints.eclinic.domain.user.User;

public interface Constants {

  String AUTH_TOKEN = "X-AUTH-TOKEN";

  String API_PREFIX = "/api/v1";

  String CSV_IMPORT_MESSAGE = "CSV file import processing is complete. Number of rows successfully processed: %d (number of error cells: %d), number of rows failed in processing: %d. See the CSV error log for details.";

  String DEFAULT_DATE_FORMAT = "dd-MM-yyyy";

  Locale DEFAULT_LOCALE = Locale.ENGLISH;

  String DEFAULT_TIMEZONE = "+05:30";

  User SYSTEM_USER = User.builder().mobileNumber("SYSTEM").name("SYSTEM").build();
}
