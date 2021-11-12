package org.ace.insurance.web.mobileforlife;

import java.util.Date;

public class LifeProposalDTO {
	private String proposalNo;
	private Date submitteddate;
	private String insuredpersonName;
	private String salechanneltype;
	
	
	
	public LifeProposalDTO() {
		
	}
	public LifeProposalDTO(String proposalNo, Date submitteddate, String insuredpersonName,String salechanneltype) {
		this.proposalNo = proposalNo;
		this.submitteddate = submitteddate;
		this.insuredpersonName = insuredpersonName;
		this.salechanneltype=salechanneltype;
	}
	public String getProposalNo() {
		return proposalNo;
	}
	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}
	public Date getSubmitteddate() {
		return submitteddate;
	}
	public void setSubmitteddate(Date submitteddate) {
		this.submitteddate = submitteddate;
	}
	public String getInsuredpersonName() {
		return insuredpersonName;
	}
	public void setInsuredpersonName(String insuredpersonName) {
		this.insuredpersonName = insuredpersonName;
	}
	public String getSalechanneltype() {
		return salechanneltype;
	}
	public void setSalechanneltype(String salechanneltype) {
		this.salechanneltype = salechanneltype;
	}
	
	
	

}
