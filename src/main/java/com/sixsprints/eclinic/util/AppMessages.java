package com.sixsprints.eclinic.util;

public interface AppMessages {

  String EXCEPTION_UNEXPECTED = "exception.unexpected";

  public static interface Appointment {
    String NO_SLOT_ERROR = "appointment.no.slot.error";
    String PAST_DATE_ERROR = "appointment.date.past.error";
    String CALL_NEXT_ERROR = "appointment.call.next.invalid.status.error";
  }

  public static interface User {
    String NOT_FOUND = "user.not.found.error";
    String ALREADY_EXISTS = "user.already.exists.error";
    String OTP_INVALID = "user.otp.error";
  }

  public static interface Generic {
    String FILE_SIZE_EXCEEDED = "file.size.exceeded";
  }

}
