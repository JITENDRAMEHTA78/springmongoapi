package com.sixsprints.eclinic.enums;

public enum PaymentMode {

  PAYTM("https://storage.googleapis.com/eclinic_assets/payment_option_images/paytm.svg", ParentPaymentMode.E_MONEY),
  GOOGLEPAY("https://storage.googleapis.com/eclinic_assets/payment_option_images/google-pay.svg", ParentPaymentMode.E_MONEY),
  PHONEPAY("https://storage.googleapis.com/eclinic_assets/payment_option_images/PhonePay.svg", ParentPaymentMode.E_MONEY),
  BHIM_UPI("https://storage.googleapis.com/eclinic_assets/payment_option_images/bhim.png", ParentPaymentMode.UPI),
  PAY_AT_HOSPITAL("https://storage.googleapis.com/eclinic_assets/payment_option_images/pay-at-hospital.png",
    ParentPaymentMode.CASH);

  private String logo;

  private ParentPaymentMode mode;

  private PaymentMode(String logo, ParentPaymentMode mode) {
    this.logo = logo;
    this.mode = mode;
  }

  public String getLogo() {
    return logo;
  }

  public ParentPaymentMode getMode() {
    return mode;
  }

}
