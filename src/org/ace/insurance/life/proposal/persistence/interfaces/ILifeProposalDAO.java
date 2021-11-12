package org.ace.insurance.life.proposal.persistence.interfaces;

import java.util.Date;
import java.util.List;

import org.ace.insurance.common.FamilyInfo;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.ProposalStatus;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LPL001;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonInfoDTO;
import org.ace.insurance.web.mobileforlife.LifeProposalDTO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.poi.ss.formula.functions.T;

public interface ILifeProposalDAO {
	public LifeProposal insert(LifeProposal lifeProposal) throws DAOException;

	public void update(LifeProposal lifeProposal) throws DAOException;

	public void delete(LifeProposal lifeProposal) throws DAOException;

	public void addAttachment(LifeProposal lifeProposal) throws DAOException;

	public LifeProposal findById(String id) throws DAOException;

	public List<LifeProposal> findAll() throws DAOException;

	public List<LifeProposal> findByDate(Date startDate, Date endDate) throws DAOException;

	public List<LifeProposal> findByIdList(List<String> proposalIdList) throws DAOException;

	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException;

	public void updateInsuPersonMedicalStatus(List<ProposalInsuredPerson> proposalInsuredPersonList) throws DAOException;

	public void insertSurvey(LifeSurvey lifeSurvey) throws DAOException;

	public List<LPL001> findByEnquiryCriteria(EnquiryCriteria criteria, List<Product> productList) throws DAOException;

	public LifeProposal findLifePortalById(String id) throws DAOException;

	// prepare
	public ProposalInsuredPerson findProposalInsuredPersonById(String id) throws DAOException;

	public LifeSurvey findSurveyByProposalId(String proposalId) throws DAOException;

	public List<PolicyInsuredPerson> findPolicyInsuredPersonByParameters(Name name, String idNo, String fatherName, Date dob) throws DAOException;

	public String findStatusById(String id) throws DAOException;

	public void updateStatus(String status, String id) throws DAOException;

	public LifeProposal findByProposalNo(String proposalNo) throws DAOException;

	public void updateProposalPersonCode(List<ProposalInsuredPerson> personList) throws DAOException;

	public void updateProposalBeneficiaryCode(List<InsuredPersonBeneficiaries> beneficiaryList) throws DAOException;

	public void updateProposalStatus(ProposalStatus status, String proposalId) throws DAOException;

	public boolean findOverMaxSIByMotherNameAndNRCAndInsuNameAndNRC(LifeProposal lifeProposal, InsuredPersonInfoDTO insuredPersonInfoDTO);

	public List<LifeProposalDTO> findMobileLifeProposal() throws DAOException;

	public LifeProposal findByProposalNoFromTemp(String proposalNo) throws DAOException;

	public List<ProposalInsuredPerson> findProposalInsuredPersonFromTemp(String proposalId, LifeProposal lifeProposal) throws DAOException;

	public List<InsuredPersonBeneficiaries> findBeneficiariesFromTemp(String insuredPersonId, ProposalInsuredPerson insuredPerson) throws DAOException;

	public Customer findCustomerFromTemp(String customerId) throws DAOException;

	public List<FamilyInfo> findFamilyInfoFromTemp(String customerId) throws DAOException;

	public Object[] updateProposalTempStatus(String proposalNo, boolean status) throws DAOException;

	public InsuredPersonKeyFactorValue createKeyFactorValue(KeyFactor keyfactor, ProposalInsuredPerson insuredPerson);
}
