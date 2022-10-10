package com.sixsprints.eclinic.enums;

public enum ParentPaymentMode {
  CASH("Cash"), UPI("UPI"), CC_DC("Credit/Debit Card"), E_MONEY("E-Money");

  private String label;

  private ParentPaymentMode(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

}
