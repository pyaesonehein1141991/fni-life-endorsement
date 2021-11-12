package org.ace.insurance.report.life;

import java.util.Date;
import java.util.List;

import org.ace.insurance.payment.Payment;

public class PaymentTypeChangeReportDTO {

	private String receiptNO;
	private Date paymentDate;
	private int oldDueNo;
	private double oldAmount;
	private int newDueNo;
	private double newAmount;
	private String remark;
	private String oldValue;
	private String newValue;

	public PaymentTypeChangeReportDTO() {
		// TODO Auto-generated constructor stub
	}

	public String getReceiptNO() {
		return receiptNO;
	}

	public void setReceiptNO(String receiptNO) {
		this.receiptNO = receiptNO;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public int getOldDueNo() {
		return oldDueNo;
	}

	public void setOldDueNo(int oldDueNo) {
		this.oldDueNo = oldDueNo;
	}

	public double getOldAmount() {
		return oldAmount;
	}

	public void setOldAmount(double oldAmount) {
		this.oldAmount = oldAmount;
	}

	public int getNewDueNo() {
		return newDueNo;
	}

	public void setNewDueNo(int newDueNo) {
		this.newDueNo = newDueNo;
	}

	public double getNewAmount() {
		return newAmount;
	}

	public void setNewAmount(double newAmount) {
		this.newAmount = newAmount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
}
