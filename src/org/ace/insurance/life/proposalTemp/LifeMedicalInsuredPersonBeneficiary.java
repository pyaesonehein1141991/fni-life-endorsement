package org.ace.insurance.life.proposalTemp;

import java.util.Date;

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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.java.component.idgen.service.IDInterceptor;

import lombok.Data;

@Entity
@Table(name = TableName.PROPOSAL_LIFE_MEDICAL_BENEFICIARIES_TEMP)
@TableGenerator(name = "INSUREDPERSONBENEFICIARIES_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "INSUREDPERSONBENEFICIARIES_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
@Data
public class LifeMedicalInsuredPersonBeneficiary {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "INSUREDPERSONBENEFICIARIES_GEN")
	private String id;
	
	private int age;
	private String beneficiaryNo;
	
	@Enumerated(value = EnumType.STRING)
	private Gender gender;
	
	private String idNo;
	
	@Enumerated(value = EnumType.STRING)
	private IdType idType;
	
	private String initialId;
	
	private float percentage;
	
	@Version
	private int version;
	
	@Embedded
	private Name name;
	
	@Embedded
	private UserRecorder recorder;
	
	@Embedded
	private ResidentAddress residentAddress;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INSUREDPERSONID", referencedColumnName = "ID")
	private LifeMedicalInsuredPerson proposalInsuredPerson;
	
	private String relationshipId;
	private String phone;
	
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;
	
	private String fatherName;
	private String email;
	private String fax;
	private String mobile;
	
}
