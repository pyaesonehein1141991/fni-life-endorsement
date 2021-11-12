package org.ace.insurance.life.proposalTemp;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.OfficeAddress;
import org.ace.insurance.common.PassportType;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.system.common.industry.Industry;
import org.ace.java.component.idgen.service.IDInterceptor;

import lombok.Data;

@Entity
@Table(name = TableName.PROPOSAL_LIFE_MEDICAL_CUSTOMER_TEMP)
@TableGenerator(name = "CUSTOMER_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "CUSTOMER_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
@Data
public class LifeMedicalCustomer {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "CUSTOMER_GEN")
	private String id;
	
	private int activePolicy;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date activedDate;

	private String bankAccountNo;
	private String birthMark;

	private int closedPolicy;

	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	private String fatherName;
	private String fullIdNo;
	
	@Enumerated(value = EnumType.STRING)
	private Gender gender;
	
	private double height;
	
	@Enumerated(value = EnumType.STRING)
	private IdType idType;
	
	private String initialId;
	private String labourNo;
	
	@Enumerated(value = EnumType.STRING)
	private MaritalStatus maritalStatus;

	@Enumerated(value = EnumType.STRING)
	private PassportType passportType;

	private String placeOfBirth;
	private String salary;
	
	@Version
	private int version;
	
	private double weight;
	
	@Embedded
	private ContentInfo contentInfo;

	@Embedded
	private Name name;
	
	@Embedded
	private OfficeAddress officeAddress;

	@Embedded
	private PermanentAddress permanentAddress;

	@Embedded
	private UserRecorder recorder;

	@Embedded
	private ResidentAddress residentAddress;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "PROPOSAL_LIFE_MEDICAL_CUSTOMERFAMILY_TEMP", joinColumns = @JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID"))
	private List<LifeMedicalCustomerFamily> familyInfo;
	
	//@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "customer", orphanRemoval = true)
	//private List<LifeMedicalCustomerInfoStatus> customerStatusList;

	private String bankBranchId;
	private String branchId;
	private String nationalityId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INDURSTRYID", referencedColumnName = "ID")
	private Industry industry;
	
	private String occupationId;
	private String qualificationId;
	private String religionId;
	private boolean isExisting;
	private boolean status;
	
}
