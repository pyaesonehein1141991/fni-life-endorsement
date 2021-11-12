package org.ace.insurance.proxy;

import java.io.Serializable;
import java.util.Date;

import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.user.User;

public class WF002 implements Serializable {
	private String id;
	private String proposalNo;
	private WorkflowTask workflowTask;
	private String referenceNo;
	private Date date;
	private ReferenceType referenceType;
	private TransactionType transactionType;
	public User responsiblePerson;
	public User tempPerson;


	public WF002() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public WF002(String referenceNo, String id, String proposalNo, WorkflowTask workflowTask, Date date, TransactionType transactionType, ReferenceType referenceType) {
		super();
		this.referenceNo = referenceNo;
		this.id = id;
		this.proposalNo = proposalNo;
		this.workflowTask = workflowTask;
		this.date = date;
		this.transactionType = transactionType;
		this.referenceType = referenceType;
		}
		


	public WF002(String referenceNo, String id, String proposalNo, WorkflowTask workflowTask, Date date, TransactionType transactionType, ReferenceType referenceType,User responsiblePerson) {
		super();
		this.referenceNo = referenceNo;
		this.id = id;
		this.proposalNo = proposalNo;
		this.workflowTask = workflowTask;
		this.date = date;
		this.transactionType = transactionType;
		this.referenceType = referenceType;
		this.responsiblePerson=responsiblePerson;
	}

	
	public String getId() {
		return id;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public WorkflowTask getWorkflowTask() {
		return workflowTask;
	}

	public Date getDate() {
		return date;
	}

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public void setWorkflowTask(WorkflowTask workflowTask) {
		this.workflowTask = workflowTask;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public User getTempPerson() {
		if(tempPerson == null) {
			return new User();
		}
		return tempPerson;
	}

	public void setTempPerson(User tempPerson) {
		this.tempPerson = tempPerson;
	}

}
