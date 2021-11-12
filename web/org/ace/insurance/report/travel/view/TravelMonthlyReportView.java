package org.ace.insurance.report.travel.view;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_FNI_SPECIALTRAVEL_MONTHLY_INCOME)
@ReadOnly
public class TravelMonthlyReportView implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String receiptNo;
	private String agentName;
	private double premium;
	@Temporal(TemporalType.DATE)
	private Date paymentDate;
	private String branchId;
	@Temporal(TemporalType.DATE)
	private Date fromDate;
	@Temporal(TemporalType.DATE)
	private Date toDate;
	private double commission;
	private String expressName;
	private String travelPath;
	private String vehicleNo;
	private int noOfPass;
	private int totalUnit;
	private double totalSI;

	public TravelMonthlyReportView() {
	}

	public String getId() {
		return id;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public String getAgentName() {
		return agentName;
	}

	public double getPremium() {
		return premium;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getBranchId() {
		return branchId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public double getCommission() {
		return commission;
	}

	public String getExpressName() {
		return expressName;
	}

	public String getTravelPath() {
		return travelPath;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public int getNoOfPass() {
		return noOfPass;
	}

	public int getTotalUnit() {
		return totalUnit;
	}

	public double getTotalSI() {
		return totalSI;
	}

	public String getReceiptNoAndDate() {
		return getReceiptNo() + " (" + Utils.getDateFormatString(getPaymentDate()) + ")";
	}

	public String getTerm() {
		return "From" + " (" + Utils.getDateFormatString(getFromDate()) + ") " + "To" + " (" + Utils.getDateFormatString(getToDate()) + ")";
	}

	public double getTotalPremium() {
		return getPremium() - getCommission();
	}
}
