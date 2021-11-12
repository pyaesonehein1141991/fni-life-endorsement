package org.ace.insurance.life.proposalTemp;

import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.ace.insurance.common.IdType;
import org.ace.insurance.common.Name;

import lombok.Data;

@Embeddable
@Data
public class LifeMedicalCustomerFamily {

	private Date dateOfBirth;
	
	private String idNo;
	
	@Enumerated(value = EnumType.STRING)
	private IdType idType;
	
	private String initialId;
	
	@Embedded
	private Name name;
	
	private String industryId;
	private String occupationId;
	private String relationshipId;
}
