package org.ace.insurance.web.manage.report.travel;

import java.util.Date;

import org.ace.insurance.system.common.branch.Branch;

public class TravelReportCriteria {
	private Date submittedDate;
	private Date fromDate;
	private Date toDate;
	private String registration;
	private String tour;
	private int month;
	private int year;
	private Branch branch;
	private Branch paymentBranch;
	private String travelExpress;

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getRegistration() {
		return registration;
	}

	public void setRegistration(String registration) {
		this.registration = registration;
	}

	public String getTour() {
		return tour;
	}

	public void setTour(String tour) {
		this.tour = tour;
	}

	public String getTravelExpress() {
		return travelExpress;
	}

	public void setTravelExpress(String travelExpress) {
		this.travelExpress = travelExpress;
	}

	public Branch getPaymentBranch() {
		return paymentBranch;
	}

	public void setPaymentBranch(Branch paymentBranch) {
		this.paymentBranch = paymentBranch;
	}

}
