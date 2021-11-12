package org.ace.insurance.report.TLF;

import java.util.Date;

import org.ace.insurance.report.TLF.view.DailyIncomeReportView;

public class DailyIncomeReportDTO {

	private Date paymentDate;
	private String paymentChannel;
	private String receiptNo;
	private String policyNo;
	private String productName;
	private double homeAmount;
	private String salePointName;
	private String bankAccountNo;
	private String poNo;

	public DailyIncomeReportDTO() {
	}

	public DailyIncomeReportDTO(DailyIncomeReportView view) {
		this.paymentDate = view.getPaymentDate();
		this.paymentChannel = view.getPaymentChannel();
		this.receiptNo = view.getReceiptNo();
		this.policyNo = view.getPolicyNo();
		this.productName = view.getProductName();
		this.homeAmount = view.getHomeAmount();
		this.salePointName = view.getSalePointName();
		this.bankAccountNo = view.getBankAccountNo();
		this.poNo=view.getPoNo();
	}

	public DailyIncomeReportDTO(Date paymentDate, String paymentChannel, String receiptNo, String policyNo, String productName, double homeAmount, String salePointName, String bankAccountNo,String poNo) {
		this.paymentDate = paymentDate;
		this.paymentChannel = paymentChannel;
		this.receiptNo = receiptNo;
		this.policyNo = policyNo;
		this.productName = productName;
		this.homeAmount = homeAmount;
		this.salePointName = salePointName;
		this.bankAccountNo = bankAccountNo;
		this.poNo=poNo;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getPaymentChannel() {
		return paymentChannel;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getProductName() {
		return productName;
	}

	public double getHomeAmount() {
		return homeAmount;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public String getPoNo() {
		return poNo;
	}

	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}

}
