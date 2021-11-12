package org.ace.insurance.medical.proposal.persistence;

/***************************************************************************************
 * @author Kyaw Myat Htut
 * @Date 2014-6-31
 * @Version 1.0
 * @Purpose This class serves as the DAO to manipulate the <code>Process</code>
 *          object.
 * 
 ***************************************************************************************/
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.CustomerType;
import org.ace.insurance.common.FamilyInfo;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.OfficeAddress;
import org.ace.insurance.common.PassportType;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.ProposalStatus;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.Utils;
import org.ace.insurance.medical.policy.persistence.interfaces.IMedicalPolicyDAO;
import org.ace.insurance.medical.proposal.CustomerInfoStatus;
import org.ace.insurance.medical.proposal.MP001;
import org.ace.insurance.medical.proposal.MedicalKeyFactorValue;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAddOn;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonBeneficiaries;
import org.ace.insurance.medical.proposal.MedicalSurvey;
import org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO;
import org.ace.insurance.medical.surveyAnswer.SurveyQuestionAnswer;
import org.ace.insurance.product.persistence.interfaces.IProductDAO;
import org.ace.insurance.system.common.addon.persistence.interfaces.IAddOnDAO;
import org.ace.insurance.system.common.agent.persistence.interfaces.IAgentDAO;
import org.ace.insurance.system.common.bankBranch.persistence.interfaces.IBankBranchDAO;
import org.ace.insurance.system.common.branch.persistence.interfaces.IBranchDAO;
import org.ace.insurance.system.common.country.persistence.interfaces.ICountryDAO;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.industry.persistence.interfaces.IIndustryDAO;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.persistence.interfaces.IOccupationDAO;
import org.ace.insurance.system.common.organization.persistence.interfaces.IOrganizationDAO;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.persistence.interfaces.IPaymentTypeDAO;
import org.ace.insurance.system.common.qualification.persistence.interfaces.IQualificationDAO;
import org.ace.insurance.system.common.relationship.persistence.interfaces.IRelationShipDAO;
import org.ace.insurance.system.common.religion.persistence.interfaces.IReligionDAO;
import org.ace.insurance.system.common.salesPoints.persistence.interfaces.ISalesPointsDAO;
import org.ace.insurance.system.common.township.persistence.interfaces.ITownshipDAO;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.mobileforhealth.MedicalProposalDTO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("MedicalProposalDAO")
public class MedicalProposalDAO extends BasicDAO implements IMedicalProposalDAO {
	private Logger logger = LogManager.getLogger(this.getClass());

	@Resource(name = "MedicalPolicyDAO")
	private IMedicalPolicyDAO medicalPolicyDAO;

	@Resource(name = "AgentDAO")
	private IAgentDAO agentDAO;

	@Resource(name = "BranchDAO")
	private IBranchDAO branchDAO;

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Resource(name = "OrganizationDAO")
	private IOrganizationDAO organizationDAO;

	@Resource(name = "PaymentTypeDAO")
	private IPaymentTypeDAO paymentTypeDAO;

	@Resource(name = "SalesPointsDAO")
	private ISalesPointsDAO salesPointsDAO;

	@Resource(name = "ProductDAO")
	private IProductDAO productDAO;

	@Resource(name = "RelationShipDAO")
	private IRelationShipDAO relationshipDAO;

	@Resource(name = "TownshipDAO")
	private ITownshipDAO townshipDAO;

	@Resource(name = "BankBranchDAO")
	private IBankBranchDAO bankBranchDAO;

	@Resource(name = "OccupationDAO")
	private IOccupationDAO occupationDAO;

	@Resource(name = "CountryDAO")
	private ICountryDAO countryDAO;

	@Resource(name = "IndustryDAO")
	private IIndustryDAO industryDAO;

	@Resource(name = "QualificationDAO")
	private IQualificationDAO qualificationDAO;

	@Resource(name = "ReligionDAO")
	private IReligionDAO religionDAO;

	@Resource(name = "AddOnDAO")
	private IAddOnDAO addonDAO;

	// Declaring local variables to indicate customer existence
	private boolean customerExist, isInsuredPerson, insuredPersonCustomerExist = false;
	private String customerTempId, insuredPersonCustomerTempId = "";
	private Date startDate = new Date();
	private PaymentType paymentType = new PaymentType();
	private List<CustomerInfoStatus> statusList = new ArrayList<CustomerInfoStatus>();

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsPerWithReasonAndApprove(String rejectReason, boolean approved, String id) throws DAOException {
		try {
			Query query = em
					.createQuery("UPDATE MedicalProposalInsuredPerson s SET s.rejectReason =:rejectReason , s.approved =:approved, s.finishedApproved=true WHERE s.id =:id");
			query.setParameter("rejectReason", rejectReason);
			query.setParameter("approved", approved);
			query.setParameter("id", id);
			query.executeUpdate();

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update InsPerWithReasonAndApprove", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 *      #insert(org.ace.insurance.medical.proposal.MedicalProposal)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalProposal insert(MedicalProposal medicalProposal) throws DAOException {
		try {
			em.persist(medicalProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert MedicalProposal", pe);
		}
		return medicalProposal;
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 *      #update(org.ace.insurance.medical.proposal.MedicalProposal)
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(MedicalProposal medicalProposal) throws DAOException {
		try {
			em.merge(medicalProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update MedicalProposal", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 *      #delete(org.ace.insurance.medical.proposal.MedicalProposal)
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(MedicalProposal medicalProposal) throws DAOException {
		try {
			medicalProposal = em.merge(medicalProposal);
			em.remove(medicalProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete MedicalProposal", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 *      #findById(String)
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalProposal findById(String id) throws DAOException {
		MedicalProposal result = null;
		try {
			result = em.find(MedicalProposal.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find MedicalProposal", pe);
		}
		return result;
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalProposal> findAll() throws DAOException {
		List<MedicalProposal> result = null;
		try {
			Query q = em.createNamedQuery("MedicalProposal.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of MedicalProposal", pe);
		}
		return result;
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException {
		try {
			Query q = em.createNamedQuery("MedicalProposal.updateCompleteStatus");
			q.setParameter("complete", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update complete status", pe);
		}

	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 */

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertSurvey(MedicalSurvey medicalSurvey) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM MedicalSurvey l WHERE l.medicalProposal.id = :medicalProposalId");
			delQuery.setParameter("medicalProposalId", medicalSurvey.getMedicalProposal().getId());
			delQuery.executeUpdate();
			em.persist(medicalSurvey);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Survey", pe);
		}
	}

	/**
	 * @see org.ace.insurance.medical.proposal.persistence.interfaces.IMedicalProposalDAO
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuPersonMedicalStatus(MedicalProposalInsuredPerson proposalInsuredPerson) throws DAOException {
		try {

			String queryString = "UPDATE MedicalProposalInsuredPerson p SET p.clsOfHealth = :clsOfHealth WHERE p.id = :insuPersonId";
			Query query = em.createQuery(queryString);
			query.setParameter("insuPersonId", proposalInsuredPerson.getId());
			query.executeUpdate();

		} catch (PersistenceException pe) {
			throw translate("Failed to approved InsuredPerson Approbal Info", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuredPersonApprovalInfo(MedicalProposalInsuredPerson proposalInsuredPerson) throws DAOException {
		try {
			em.merge(proposalInsuredPerson);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to approved InsuredPerson Approbal Info", pe);
		}
	}

	/* used for Medical Proposal Enquire (underwritingEnquery.xhtml) */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<MP001> findByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException {
		List<Object[]> objArr = null;
		List<MP001> result = new ArrayList<>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" SELECT m.id, m.proposalNo,m.saleChannelType, a.initialId, a.name, ");
			buffer.append(" CONCAT(TRIM(c.initialId),' ', TRIM(c.name.firstName), ' ', TRIM(c.name.middleName), ' ', TRIM(c.name.lastName)), ");
			buffer.append(" o.name,b.name, SUM(mp.unit),");
			buffer.append(" SUM(COALESCE(mp.premium, 0)),mp.product.id,");
			buffer.append(" m.submittedDate,sp.name");
			buffer.append(" FROM MedicalProposal m JOIN m.medicalProposalInsuredPersonList mp");
			buffer.append(" LEFT OUTER JOIN mp.customer ip");
			buffer.append(" LEFT OUTER JOIN m.agent a");
			buffer.append(" LEFT OUTER JOIN m.salesPoints sp");
			buffer.append(" LEFT OUTER JOIN m.customer c LEFT OUTER JOIN m.branch b LEFT OUTER JOIN m.organization o");
			buffer.append(" WHERE m.proposalNo IS NOT NULL");
			if (criteria.getAgent() != null) {
				buffer.append(" AND m.agent.id = :agentId");
			}
			if (criteria.getStartDate() != null) {
				buffer.append(" AND m.submittedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				buffer.append(" AND m.submittedDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				buffer.append(" AND m.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				buffer.append(" AND m.organization.id = :organizationId");
			}
			if (criteria.getBranch() != null) {
				buffer.append(" AND m.branch.id = :branchId");
			}
			if (criteria.getSalePoint() != null) {
				buffer.append(" AND sp.id = :salePointId");
			}
			if (criteria.getProduct() != null) {
				buffer.append(" AND mp.product.id = :productId");
			}
			if (criteria.getProposalNo() != null && !criteria.getProposalNo().isEmpty()) {
				buffer.append(" AND m.proposalNo like :proposalNo");
			}
			if (criteria.getProposalType() != null) {
				buffer.append(" AND m.proposalType = :proposalType");
			}
			if (criteria.getSaleChannelType() != null) {
				buffer.append(" AND m.saleChannelType = :saleChannelType");
			}
			if (criteria.getInsuredPersonName() != null) {
				buffer.append(" AND CONCAT(FUNCTION('REPLACE',ip.name.firstName,' ',''),FUNCTION('REPLACE',ip.name.middleName,' ','')");
				buffer.append(",FUNCTION('REPLACE',ip.name.lastName,' ','')) LIKE :insuredPersonName");
			}
			buffer.append(" GROUP BY m.id, m.proposalNo,m.saleChannelType,");
			buffer.append("  a.initialId, a.name,c.initialId, c.name,o.name,");
			buffer.append(" b.name,mp.product.id,m.submittedDate,sp.name");

			/* Executed query */
			Query query = em.createQuery(buffer.toString());
			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getCustomer() != null) {
				query.setParameter("customerId", criteria.getCustomer().getId());
			}
			if (criteria.getOrganization() != null) {
				query.setParameter("organizationId", criteria.getOrganization().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				query.setParameter("salePointId", criteria.getSalePoint().getId());
			}
			if (criteria.getProduct() != null) {
				query.setParameter("productId", criteria.getProduct().getId());
			}
			if (criteria.getProposalNo() != null && !criteria.getProposalNo().isEmpty()) {
				query.setParameter("proposalNo", "%" + criteria.getProposalNo() + "%");
			}
			if (criteria.getProposalType() != null) {
				query.setParameter("proposalType", criteria.getProposalType());
			}
			if (criteria.getSaleChannelType() != null) {
				query.setParameter("saleChannelType", criteria.getSaleChannelType());
			}
			if (criteria.getInsuredPersonName() != null) {
				query.setParameter("insuredPersonName", "%" + criteria.getInsuredPersonName().replace(" ", "") + "%");
			}
			objArr = query.getResultList();
			String id = null;
			String proposalNo = null;
			SaleChannelType saleChannelType = null;
			String branch = null;
			String salePerson = null;
			String customer = null;
			String organization = null;
			String salePoint = null;
			Long unit = null;
			Double totalPremium = null;
			String productId = null;
			Date submittedDate = null;
			for (Object[] arr : objArr) {
				salePerson = "";
				id = (String) arr[0];
				proposalNo = (String) arr[1];
				saleChannelType = (SaleChannelType) arr[2];
				if (arr[3] != null) {
					salePerson = arr[3] + ((Name) arr[4]).getFullName();
				}
				customer = (String) arr[5];
				organization = (String) arr[6];
				branch = (String) arr[7];
				unit = (Long) arr[8];
				totalPremium = (Double) arr[9];
				productId = (String) arr[10];
				submittedDate = (Date) arr[11];
				salePoint = (String) arr[12];
				result.add(new MP001(id, proposalNo, saleChannelType, salePerson, customer, organization, branch, unit, totalPremium.doubleValue(), productId, submittedDate, "",
						"", salePoint));
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by EnquiryCriteria : ", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalSurvey findSurveyByProposalId(String proposalId) throws DAOException {
		MedicalSurvey result = null;
		try {
			logger.debug("findSurveyByProposalId() method has been started.");
			Query query = em.createQuery("SELECT l FROM MedicalSurvey l WHERE l.medicalProposal.id = :id");
			query.setParameter("id", proposalId);
			result = (MedicalSurvey) query.getSingleResult();
			em.flush();
			logger.debug("findSurveyByProposalId() method has been started.");
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Proposal Survey : ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SurveyQuestionAnswer> findSurveyQuestionAnswerByProposalId(String proposalId) throws DAOException {
		List<SurveyQuestionAnswer> surveyQuestionAnswerList = null;
		try {
			Query q = em.createQuery("SELECT p.surveyQuestionAnswerList FROM MedicalProposal m  JOIN m.medicalProposalInsuredPersonList p  where m.id =:proposalId ");
			q.setParameter("proposalId", proposalId);
			surveyQuestionAnswerList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Survey Question Answer : ", pe);
		}
		return surveyQuestionAnswerList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMedicalProposalCompleteStatus(String proposalId) throws DAOException {
		try {
			Query query = em.createNamedQuery("MedicalProposal.updateCompleteStatus");
			query.setParameter("id", proposalId);
			query.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Medical Proposal Complete Status", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateSpecialDiscount(String medicalProposalId, double specialDiscount) throws DAOException {
		try {
			Query query = em.createQuery("UPDATE MedicalProposal p SET p.specialDiscount = :specialDiscount WHERE p.id = :medicalProposalId");
			query.setParameter("medicalProposalId", medicalProposalId);
			query.setParameter("specialDiscount", specialDiscount);
			query.executeUpdate();
		} catch (PersistenceException pe) {
			throw translate("Failed to update specialDiscount", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateNCBAmount(String medicalProposalId, double ncbAmount) throws DAOException {
		try {
			Query query = em.createQuery("UPDATE MedicalProposal p SET p.totalNcbAmount = :totalNcbAmount WHERE p.id = :medicalProposalId");
			query.setParameter("medicalProposalId", medicalProposalId);
			query.setParameter("totalNcbAmount", ncbAmount);
			query.executeUpdate();
		} catch (PersistenceException pe) {
			throw translate("Failed to update specialDiscount", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProposalStatus(ProposalStatus status, String proposalId) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE MedicalProposal f SET f.proposalStatus = :status WHERE f.id = :id");
			q.setParameter("status", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Proposal status", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateMedicalProposalAttachment(MedicalProposal medicalProposal) throws DAOException {
		try {
			em.merge(medicalProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Medical Proposal", pe);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalProposalDTO> findMedicalProposal() throws DAOException {
		List<MedicalProposalDTO> medicalproposallist = new ArrayList<>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query q = em.createNativeQuery("select p.proposalNo as proposalNo,MAX(p.SUBMITTEDDATE) as submitteddate,MAX(p.SALECHANNELTYPE) as salechanneltype,\r\n"
					+ "				 MAX((COALESCE(RTRIM (cu.INITIALID),'')+' '+COALESCE(LTRIM(cu.FIRSTNAME),'')+' '+COALESCE(RTRIM (cu.MIDDLENAME),'') +' '+COALESCE(RTRIM (cu.LASTNAME),'')))  AS insuredpersonname \r\n"
					+ "				  from PROPOSAL_LIFE_MEDICAL_TEMP p \r\n"
					+ "				left join PROPOSAL_LIFE_MEDICAL_INSUREDPERSON_TEMP c on p.id=c.MEDICALPROPOSALID \r\n"
					+ "				left join PROPOSAL_LIFE_MEDICAL_CUSTOMER_TEMP cu on cu.ID = c.CUSTOMERID \r\n"
					+ "				where p.status='false' and p.healthType is not null group by p.proposalNo, p.CREATEDDATE order by p.CREATEDDATE desc");
			objectList = q.getResultList();
			for (Object[] object : objectList) {
				MedicalProposalDTO medicalProposal = new MedicalProposalDTO();
				medicalProposal.setProposalNo(String.valueOf(object[0]));
				medicalProposal.setSubmitteddate((Date) (object[1]));
				medicalProposal.setSalechanneltype(String.valueOf(object[2]));
				medicalProposal.setInsuredpersonName(String.valueOf(object[3]));
				medicalproposallist.add(medicalProposal);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Medical Proposal", pe);

		}
		return medicalproposallist;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalProposal findByProposalNoFromTerm(String proposalNo) throws DAOException {
		MedicalProposal result = null;
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_TEMP t WHERE t.proposalNo = ?");
			query.setParameter(1, proposalNo);
			objectList = query.getResultList();

			for (Object[] object : objectList) {
				MedicalProposal temp = new MedicalProposal();
				temp.setCustomerType(StringUtils.contains("null", String.valueOf(object[32])) ? null : CustomerType.valueOf(String.valueOf(object[32])));
				temp.setProposalNo(StringUtils.contains("null", String.valueOf(object[5])) ? null : String.valueOf(object[5]));
				temp.setProposalType(StringUtils.contains("null", String.valueOf(object[6])) ? null : ProposalType.valueOf(String.valueOf(object[6])));
				temp.setSubmittedDate(StringUtils.contains("null", String.valueOf(object[8])) ? null : (Date) object[8]);
				temp.setVersion(StringUtils.contains("null", String.valueOf(object[9])) ? null : (Integer) object[9]);
				temp.setAgent(StringUtils.contains("null", String.valueOf(object[14])) ? null : agentDAO.findById(String.valueOf(object[14])));
				temp.setBranch(StringUtils.contains("null", String.valueOf(object[15])) ? null : branchDAO.findById(String.valueOf(object[15])));
				temp.setOldMedicalPolicy(StringUtils.contains("null", String.valueOf(object[33])) ? null : medicalPolicyDAO.findById(String.valueOf(object[33])));
				temp.setPaymentType(StringUtils.contains("null", String.valueOf(object[19])) ? null : paymentTypeDAO.findById(String.valueOf(object[19])));
				temp.setSaleChannelType(StringUtils.contains("null", String.valueOf(object[7])) ? null : SaleChannelType.valueOf(String.valueOf(object[7])));
				temp.setOrganization(StringUtils.contains("null", String.valueOf(object[18])) ? null : organizationDAO.findById(String.valueOf(object[18])));
				temp.setHealthType(StringUtils.contains("null", String.valueOf(object[35])) ? null : HealthType.valueOf(String.valueOf(object[35])));
				temp.setPaymentTerm(StringUtils.contains("null", String.valueOf(object[3])) ? null : (Integer) object[3]);
				temp.setStartDate(StringUtils.contains("null", String.valueOf(object[24])) ? null : (Date) object[24]);
				temp.setEndDate(StringUtils.contains("null", String.valueOf(object[25])) ? null : (Date) object[25]);
				temp.setPeriodMonth(StringUtils.contains("null", String.valueOf(object[23])) ? null : (Integer) object[23]);
				temp.setSpecialDiscount(StringUtils.contains("null", String.valueOf(object[22])) ? null : (Double) object[22]);
				temp.setRate(StringUtils.contains("null", String.valueOf(object[36])) ? null : (Double) object[36]);
				temp.setProposalStatus(StringUtils.contains("null", String.valueOf(object[21])) ? null : ProposalStatus.valueOf(String.valueOf(object[21])));
				temp.setComplete(StringUtils.contains("null", String.valueOf(object[1])) ? null : (Boolean) object[1]);
				temp.setSalesPoints(StringUtils.contains("null", String.valueOf(object[27])) ? null : salesPointsDAO.findById(String.valueOf(object[27])));
				temp.setTotalNcbAmount(StringUtils.contains("null", String.valueOf(object[37])) ? null : (Double) object[37]);

				temp.setCustomer(findCustomerFromTemp(String.valueOf(object[16]), false));

				this.startDate = temp.getStartDate();
				this.paymentType = temp.getPaymentType();
				List<MedicalProposalInsuredPerson> insuredPersonList = findProposalInsuredPersonFromTemp(String.valueOf(object[0]));
				insuredPersonList.forEach(insuredPerson -> {
					temp.getMedicalProposalInsuredPersonList().add(insuredPerson);
					insuredPerson.getCustomer().getCustomerStatusList().forEach(status -> {
						this.statusList.add(status);
					});
				});
				
				if (temp.getCustomer() != null) {
					temp.getCustomer().getCustomerStatusList().forEach(status -> {
						this.statusList.add(status);
					});
				}
				
				result = temp;
			}
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find MedicalProposal by Proposal No : " + proposalNo, pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalProposalInsuredPerson> findProposalInsuredPersonFromTemp(String proposalId) throws DAOException {
		List<MedicalProposalInsuredPerson> resultList = new ArrayList<MedicalProposalInsuredPerson>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_INSUREDPERSON_TEMP t WHERE t.MEDICALPROPOSALID = ?");
			query.setParameter(1, proposalId);
			objectList = query.getResultList();
			
			for (Object[] object : objectList) {
				MedicalProposalInsuredPerson temp = new MedicalProposalInsuredPerson();
				temp.setApproved(StringUtils.contains("null", String.valueOf(object[3])) ? null : (Boolean) object[3]);
				temp.setInPersonGroupCodeNo(StringUtils.contains("null", String.valueOf(object[15])) ? null : String.valueOf(object[15]));
				temp.setInsPersonCodeNo(StringUtils.contains("null", String.valueOf(object[17])) ? null : String.valueOf(object[17]));
				temp.setIsPaidPremiumForPaidup(StringUtils.contains("null", String.valueOf(object[52])) ? null : (Boolean) object[52]);
				temp.setNeedMedicalCheckup(StringUtils.contains("null", String.valueOf(object[19])) ? null : (Boolean) object[19]);
				temp.setPremium(StringUtils.contains("null", String.valueOf(object[53])) ? null : (Double) object[53]);
				temp.setRejectReason(StringUtils.contains("null", String.valueOf(object[22])) ? null : String.valueOf(object[22]));
				temp.setSameCustomer(StringUtils.contains("null", String.valueOf(object[54])) ? null : (Boolean) object[54]);
				temp.setUnit(StringUtils.contains("null", String.valueOf(object[23])) ? null : (Integer) object[23]);
				temp.setCustomer(findCustomerFromTemp(String.valueOf(object[35]), true));

				temp.setAge(StringUtils.contains("null", String.valueOf(object[2])) ? null : Utils.getAgeForNextYear(temp.getCustomer().getDateOfBirth(), startDate));
				temp.setProduct(StringUtils.contains("null", String.valueOf(object[37])) ? null : productDAO.findById(String.valueOf(object[37])));
				temp.setRelationship(StringUtils.contains("null", String.valueOf(object[39])) ? null : relationshipDAO.findById(String.valueOf(object[39])));
				temp.setBasicTermPremium(StringUtils.contains("null", String.valueOf(object[5])) ? null : (Double) object[5]);
				temp.setAddOnTermPremium(StringUtils.contains("null", String.valueOf(object[1])) ? null : (Double) object[1]);
				temp.setSumInsured(StringUtils.contains("null", String.valueOf(object[57])) ? null : (Double) object[57]);

				temp.setInsuredPersonAddOnList(findInsuredPersonAddonFromTemp(String.valueOf(object[0])));
				temp.getInsuredPersonAddOnList().forEach(addOn -> {
					addOn.getAddOn().getKeyFactorList().forEach(keyfactor -> {
						addOn.getKeyFactorValueList().add(createKeyFactorValue(keyfactor, temp));
					});
				});

				temp.getProduct().getKeyFactorList().forEach(keyfactor -> {
					temp.getKeyFactorValueList().add(createKeyFactorValue(keyfactor, temp));
				});
				temp.setInsuredPersonBeneficiariesList(findBeneficiariesFromTemp(String.valueOf(object[0])));
				resultList.add(temp);
			}
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by Life Proposal ID : " + proposalId, pe);
		}
		return resultList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalProposalInsuredPersonBeneficiaries> findBeneficiariesFromTemp(String insuredPersonId) throws DAOException {
		List<MedicalProposalInsuredPersonBeneficiaries> resultList = new ArrayList<MedicalProposalInsuredPersonBeneficiaries>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_BENEFICIARIES_TEMP t WHERE t.INSUREDPERSONID = ?");
			query.setParameter(1, insuredPersonId);
			objectList = query.getResultList();
			MedicalProposalInsuredPersonBeneficiaries temp = new MedicalProposalInsuredPersonBeneficiaries();
			for (Object[] object : objectList) {
				temp.setBeneficiaryNo(StringUtils.contains("null", String.valueOf(object[2])) ? null : String.valueOf(object[2]));
				temp.setFatherName(StringUtils.contains("null", String.valueOf(object[22])) ? null : String.valueOf(object[22]));
				temp.setGender(StringUtils.contains("null", String.valueOf(object[3])) ? null : Gender.valueOf(String.valueOf(object[3])));
				temp.setIdNo(StringUtils.contains("null", String.valueOf(object[4])) ? null : String.valueOf(object[4]));
				temp.setIdType(StringUtils.contains("null", String.valueOf(object[5])) ? null : IdType.valueOf(String.valueOf(object[5])));
				temp.setInitialId(StringUtils.contains("null", String.valueOf(object[6])) ? null : String.valueOf(object[6]));
				temp.setPercentage(StringUtils.contains("null", String.valueOf(object[7])) ? null : (Float) object[7]);

				ContentInfo contentInfo = new ContentInfo();
				contentInfo.setEmail(StringUtils.contains("null", String.valueOf(object[23])) ? null : String.valueOf(object[23]));
				contentInfo.setFax(StringUtils.contains("null", String.valueOf(object[24])) ? null : String.valueOf(object[24]));
				contentInfo.setMobile(StringUtils.contains("null", String.valueOf(object[25])) ? null : String.valueOf(object[25]));
				contentInfo.setPhone(StringUtils.contains("null", String.valueOf(object[20])) ? null : String.valueOf(object[20]));

				temp.setContentInfo(contentInfo);

				Name name = new Name();
				name.setFirstName(StringUtils.contains("null", String.valueOf(object[9])) ? null : String.valueOf(object[9]));
				name.setLastName(StringUtils.contains("null", String.valueOf(object[10])) ? null : String.valueOf(object[10]));
				name.setMiddleName(StringUtils.contains("null", String.valueOf(object[11])) ? null : String.valueOf(object[11]));

				temp.setName(name);

				ResidentAddress residentAddress = new ResidentAddress();
				residentAddress.setResidentAddress(StringUtils.contains("null", String.valueOf(object[16])) ? null : String.valueOf(object[16]));
				residentAddress.setTownship(StringUtils.contains("null", String.valueOf(object[17])) ? null : townshipDAO.findById(String.valueOf(object[17])));

				temp.setResidentAddress(residentAddress);
				temp.setRelationship(StringUtils.contains("null", String.valueOf(object[19])) ? null : relationshipDAO.findById(String.valueOf(object[19])));
				temp.setDateOfBirth(StringUtils.contains("null", String.valueOf(object[21])) ? null : (Date) object[21]);

				resultList.add(temp);
			}
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Beneficiary person by Insured Person ID : " + insuredPersonId, pe);
		}
		return resultList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Customer findCustomerFromTemp(String customerId, boolean isInsuredPerson) throws DAOException {
		Customer customer = new Customer();

		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_CUSTOMER_TEMP t WHERE t.ID = ?");
			query.setParameter(1, customerId);
			List<Object[]> customerObj = query.getResultList();

			if (!customerObj.isEmpty()) {
				if (isInsuredPerson) {
					this.insuredPersonCustomerExist = (Boolean) (customerObj.get(0))[45];
					this.insuredPersonCustomerTempId = String.valueOf(customerObj.get(0)[0]);

					if (this.insuredPersonCustomerExist) {
						customer = customerDAO.findById(String.valueOf(customerObj.get(0)[46]));
					} else {
						customer = getCustomer(customerObj);
					}
				} else {
					this.customerExist = (Boolean) (customerObj.get(0))[45];
					this.customerTempId = String.valueOf(customerObj.get(0)[0]);

					if (this.customerExist) {
						customer = customerDAO.findById(String.valueOf(customerObj.get(0)[46]));
					} else {
						customer = getCustomer(customerObj);
					}
				}

			} else {
				customer = null;
			}

		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find customer by customer ID : " + customerId, pe);
		}

		return customer;
	}

	public Customer getCustomer(List<Object[]> customerObj) {

		Customer customer = new Customer();

		try {

			customer.setActivePolicy(StringUtils.contains("null", String.valueOf(customerObj.get(0)[1])) ? null : (Integer) customerObj.get(0)[1]);
			customer.setActivedDate(StringUtils.contains("null", String.valueOf(customerObj.get(0)[2])) ? null : (Date) customerObj.get(0)[2]);
			customer.setBankAccountNo(StringUtils.contains("null", String.valueOf(customerObj.get(0)[3])) ? null : String.valueOf(customerObj.get(0)[3]));
			customer.setBirthMark(StringUtils.contains("null", String.valueOf(customerObj.get(0)[4])) ? null : String.valueOf(customerObj.get(0)[4]));
			customer.setClosedPolicy(StringUtils.contains("null", String.valueOf(customerObj.get(0)[5])) ? null : (Integer) customerObj.get(0)[5]);
			customer.setDateOfBirth(StringUtils.contains("null", String.valueOf(customerObj.get(0)[6])) ? null : (Date) customerObj.get(0)[6]);
			customer.setFatherName(StringUtils.contains("null", String.valueOf(customerObj.get(0)[7])) ? null : String.valueOf(customerObj.get(0)[7]));
			customer.setFullIdNo(StringUtils.contains("null", String.valueOf(customerObj.get(0)[8])) ? null : String.valueOf(customerObj.get(0)[8]));
			customer.setGender(StringUtils.contains("null", String.valueOf(customerObj.get(0)[9])) ? null : Gender.valueOf(String.valueOf(customerObj.get(0)[9])));
			customer.setHeight(StringUtils.contains("null", String.valueOf(customerObj.get(0)[10])) ? null : (Double) customerObj.get(0)[10]);
			customer.setIdType(StringUtils.contains("null", String.valueOf(customerObj.get(0)[11])) ? null : IdType.valueOf(String.valueOf(customerObj.get(0)[11])));
			customer.setInitialId(StringUtils.contains("null", String.valueOf(customerObj.get(0)[12])) ? null : String.valueOf(customerObj.get(0)[12]));
			customer.setLabourNo(StringUtils.contains("null", String.valueOf(customerObj.get(0)[13])) ? null : String.valueOf(customerObj.get(0)[13]));
			customer.setMaritalStatus(StringUtils.contains("null", String.valueOf(customerObj.get(0)[14])) ? null : MaritalStatus.valueOf(String.valueOf(customerObj.get(0)[14])));
			customer.setPassportType(StringUtils.contains("null", String.valueOf(customerObj.get(0)[15])) ? null : PassportType.valueOf(String.valueOf(customerObj.get(0)[15])));
			customer.setPlaceOfBirth(StringUtils.contains("null", String.valueOf(customerObj.get(0)[16])) ? null : String.valueOf(customerObj.get(0)[16]));
			customer.setSalary(StringUtils.contains("null", String.valueOf(customerObj.get(0)[17])) ? null : String.valueOf(customerObj.get(0)[17]));
			customer.setWeight(StringUtils.contains("null", String.valueOf(customerObj.get(0)[19])) ? null : (Double) customerObj.get(0)[19]);

			Name name = new Name();
			name.setFirstName(StringUtils.contains("null", String.valueOf(customerObj.get(0)[24])) ? null : String.valueOf(customerObj.get(0)[24]));
			name.setLastName(StringUtils.contains("null", String.valueOf(customerObj.get(0)[25])) ? null : String.valueOf(customerObj.get(0)[25]));
			name.setMiddleName(StringUtils.contains("null", String.valueOf(customerObj.get(0)[26])) ? null : String.valueOf(customerObj.get(0)[26]));

			customer.setName(name);

			ContentInfo contentInfo = new ContentInfo();
			contentInfo.setEmail(StringUtils.contains("null", String.valueOf(customerObj.get(0)[20])) ? null : String.valueOf(customerObj.get(0)[20]));
			contentInfo.setFax(StringUtils.contains("null", String.valueOf(customerObj.get(0)[21])) ? null : String.valueOf(customerObj.get(0)[21]));
			contentInfo.setMobile(StringUtils.contains("null", String.valueOf(customerObj.get(0)[22])) ? null : String.valueOf(customerObj.get(0)[22]));
			contentInfo.setPhone(StringUtils.contains("null", String.valueOf(customerObj.get(0)[23])) ? null : String.valueOf(customerObj.get(0)[23]));

			customer.setContentInfo(contentInfo);

			OfficeAddress officeAddress = new OfficeAddress();
			officeAddress.setOfficeAddress(StringUtils.contains("null", String.valueOf(customerObj.get(0)[27])) ? null : String.valueOf(customerObj.get(0)[27]));
			officeAddress.setTownship(StringUtils.contains("null", String.valueOf(customerObj.get(0)[28])) ? null : townshipDAO.findById(String.valueOf(customerObj.get(0)[28])));

			customer.setOfficeAddress(officeAddress);

			PermanentAddress permanentAddress = new PermanentAddress();
			permanentAddress.setPermanentAddress(StringUtils.contains("null", String.valueOf(customerObj.get(0)[29])) ? null : String.valueOf(customerObj.get(0)[29]));
			permanentAddress
					.setTownship(StringUtils.contains("null", String.valueOf(customerObj.get(0)[30])) ? null : townshipDAO.findById(String.valueOf(customerObj.get(0)[30])));

			customer.setPermanentAddress(permanentAddress);

			ResidentAddress residentAddress = new ResidentAddress();
			residentAddress.setResidentAddress(StringUtils.contains("null", String.valueOf(customerObj.get(0)[35])) ? null : String.valueOf(customerObj.get(0)[35]));
			residentAddress.setTownship(StringUtils.contains("null", String.valueOf(customerObj.get(0)[36])) ? null : townshipDAO.findById(String.valueOf(customerObj.get(0)[36])));

			customer.setResidentAddress(residentAddress);

			customer.setBankBranch(StringUtils.contains("null", String.valueOf(customerObj.get(0)[37])) ? null : bankBranchDAO.findById(String.valueOf(customerObj.get(0)[37])));
			customer.setBranch(StringUtils.contains("null", String.valueOf(customerObj.get(0)[38])) ? null : branchDAO.findById(String.valueOf(customerObj.get(0)[38])));
			customer.setCountry(StringUtils.contains("null", String.valueOf(customerObj.get(0)[39])) ? null : countryDAO.findById(String.valueOf(customerObj.get(0)[39])));
			customer.setIndustry(StringUtils.contains("null", String.valueOf(customerObj.get(0)[40])) ? null : industryDAO.findById(String.valueOf(customerObj.get(0)[40])));
			customer.setOccupation(StringUtils.contains("null", String.valueOf(customerObj.get(0)[41])) ? null : occupationDAO.findById(String.valueOf(customerObj.get(0)[41])));
			customer.setQualification(
					StringUtils.contains("null", String.valueOf(customerObj.get(0)[42])) ? null : qualificationDAO.findById(String.valueOf(customerObj.get(0)[42])));
			customer.setReligion(StringUtils.contains("null", String.valueOf(customerObj.get(0)[43])) ? null : religionDAO.findById(String.valueOf(customerObj.get(0)[43])));

			customer.setFamilyInfo(findFamilyInfoFromTemp(String.valueOf(customerObj.get(0)[0])));

			customerDAO.insert(customer);
			
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to add new customer.", pe);
		}

		return customer;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FamilyInfo> findFamilyInfoFromTemp(String customerId) throws DAOException {
		List<FamilyInfo> familyInfoList = new ArrayList<FamilyInfo>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_CUSTOMERFAMILY_TEMP t WHERE t.CUSTOMERID = ?");
			query.setParameter(1, customerId);
			objectList = query.getResultList();

			FamilyInfo temp = new FamilyInfo();

			for (Object[] object : objectList) {

				temp.setDateOfBirth(StringUtils.contains("null", String.valueOf(object[0])) ? null : (Date) object[0]);
				temp.setIdNo(StringUtils.contains("null", String.valueOf(object[1])) ? null : String.valueOf(object[1]));
				temp.setIdType(StringUtils.contains("null", String.valueOf(object[2])) ? null : IdType.valueOf(String.valueOf(object[2])));
				temp.setInitialId(StringUtils.contains("null", String.valueOf(object[3])) ? null : String.valueOf(object[3]));

				Name name = new Name();
				name.setFirstName(StringUtils.contains("null", String.valueOf(object[4])) ? null : String.valueOf(object[4]));
				name.setLastName(StringUtils.contains("null", String.valueOf(object[5])) ? null : String.valueOf(object[5]));
				name.setMiddleName(StringUtils.contains("null", String.valueOf(object[6])) ? null : String.valueOf(object[6]));

				temp.setName(name);

				temp.setIndustry(StringUtils.contains("null", String.valueOf(object[7])) ? null : industryDAO.findById(String.valueOf(object[7])));
				temp.setOccupation(StringUtils.contains("null", String.valueOf(object[8])) ? null : occupationDAO.findById(String.valueOf(object[8])));
				temp.setRelationShip(StringUtils.contains("null", String.valueOf(object[9])) ? null : relationshipDAO.findById(String.valueOf(object[9])));

				familyInfoList.add(temp);
			}
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Beneficiary person by Insured Person ID : " + customerId, pe);
		}

		return familyInfoList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Object[] updateProposalTempStatus(String proposalNo, boolean status) throws DAOException {

		Object[] obj = { this.customerExist, this.customerTempId, this.insuredPersonCustomerExist, this.insuredPersonCustomerTempId };

		try {
			Query q = em.createNativeQuery("UPDATE PROPOSAL_LIFE_MEDICAL_TEMP SET STATUS = ? WHERE PROPOSALNO = ?");
			q.setParameter(1, status);
			q.setParameter(2, proposalNo);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Proposal temp status", pe);
		}

		return obj;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public MedicalKeyFactorValue createKeyFactorValue(KeyFactor keyfactor, MedicalProposalInsuredPerson insuredPerson) {

		int age = insuredPerson.getAge();
		Gender gender = insuredPerson.getCustomer().getGender();

		MedicalKeyFactorValue medicalKeyFactorValue = new MedicalKeyFactorValue();

		medicalKeyFactorValue.setKeyFactor(keyfactor);

		if (KeyFactorChecker.isGender(keyfactor)) {
			medicalKeyFactorValue.setValue(gender + "");
		} else if (KeyFactorChecker.isMedicalAge(keyfactor)) {
			medicalKeyFactorValue.setValue(age + "");
		} else if (KeyFactorChecker.isPaymentType(keyfactor)) {
			medicalKeyFactorValue.setValue(paymentType.getId());
		}

		return medicalKeyFactorValue;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<MedicalProposalInsuredPersonAddOn> findInsuredPersonAddonFromTemp(String insuredPersonId) {

		List<MedicalProposalInsuredPersonAddOn> insuredPersonAddOnList = new ArrayList<MedicalProposalInsuredPersonAddOn>();
		List<Object[]> objectList = new ArrayList<>();

		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_INSUREDPERSON_ADDON_TEMP t WHERE t.MEDIPROPOSALINSUREDPERSONID = ?");
			query.setParameter(1, insuredPersonId);
			objectList = query.getResultList();

			MedicalProposalInsuredPersonAddOn temp = new MedicalProposalInsuredPersonAddOn();

			for (Object[] object : objectList) {

				temp.setPremium(StringUtils.contains("null", String.valueOf(object[4])) ? null : (Double) object[1]);
				temp.setUnit(StringUtils.contains("null", String.valueOf(object[4])) ? null : (Integer) object[2]);
				temp.setAddOn(StringUtils.contains("null", String.valueOf(object[4])) ? null : addonDAO.findById(String.valueOf(object[9])));
				temp.setSumInsured(StringUtils.contains("null", String.valueOf(object[4])) ? null : (Double) object[10]);

				insuredPersonAddOnList.add(temp);
			}
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Insured person AddOn by Insured Person ID : " + insuredPersonId, pe);
		}

		return insuredPersonAddOnList;
	}

}
