package com.sixsprints.eclinic.enums;

public enum AppointmentStatus {

	
  INITIATED("Initiated", false), PENDING_APPROVAL("Pending Approval"), OPEN("Open"), CONFIRMED("Confirmed"),
  IN_PROGRESS("In Progress"), CANCELLED("Cancelled", false), NO_SHOW("No Show"), COMPLETED("Completed"),
  CHECKED_IN("Checked In"), TEST_NEEDED("Test Needed"), REPORT_READY("Report Ready"), AWAITING_REPORT("Awaiting Report"), RECORDING_VITALS("Recording Vitals"), NEEDS_ATTENTION("Needs Attention");

  private final String label;

  private final boolean isBlocking;

  private AppointmentStatus(String label, boolean isBlocking) {
    this.label = label;
    this.isBlocking = isBlocking;
  }

  private AppointmentStatus(String label) {
    this(label, true);
  }

  public String getLabel() {
    return label;
  }

  public boolean isBlocking() {
    return isBlocking;
  }

}
