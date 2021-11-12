package org.ace.insurance.life.proposal.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ContentInfo;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.FamilyInfo;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.MaritalStatus;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.OfficeAddress;
import org.ace.insurance.common.PassportType;
import org.ace.insurance.common.PermanentAddress;
import org.ace.insurance.common.ProposalStatus;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.RiskyOccupation;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.persistence.interfaces.ILifePolicyDAO;
import org.ace.insurance.life.policyHistory.persistence.interfaces.ILifePolicyHistoryDAO;
import org.ace.insurance.life.proposal.ClassificationOfHealth;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.InsuredPersonKeyFactorValue;
import org.ace.insurance.life.proposal.LPL001;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.persistence.interfaces.ILifeProposalDAO;
import org.ace.insurance.life.proposalTemp.LifeMedicalProposal;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.persistence.interfaces.ILifeSurrenderProposalDAO;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.persistence.interfaces.IProductDAO;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.agent.persistence.interfaces.IAgentDAO;
import org.ace.insurance.system.common.bankBranch.persistence.interfaces.IBankBranchDAO;
import org.ace.insurance.system.common.branch.persistence.interfaces.IBranchDAO;
import org.ace.insurance.system.common.country.persistence.CountryDAO;
import org.ace.insurance.system.common.country.persistence.interfaces.ICountryDAO;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.gradeinfo.persistence.interfaces.IGradeInfoDAO;
import org.ace.insurance.system.common.industry.persistence.IndustryDAO;
import org.ace.insurance.system.common.industry.persistence.interfaces.IIndustryDAO;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.occupation.Occupation;
import org.ace.insurance.system.common.occupation.persistence.interfaces.IOccupationDAO;
import org.ace.insurance.system.common.organization.persistence.interfaces.IOrganizationDAO;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.paymenttype.persistence.interfaces.IPaymentTypeDAO;
import org.ace.insurance.system.common.qualification.persistence.QualificationDAO;
import org.ace.insurance.system.common.qualification.persistence.interfaces.IQualificationDAO;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.persistence.interfaces.IRelationShipDAO;
import org.ace.insurance.system.common.religion.persistence.ReligionDAO;
import org.ace.insurance.system.common.religion.persistence.interfaces.IReligionDAO;
import org.ace.insurance.system.common.riskyOccupation.persistence.interfaces.IRiskyOccupationDAO;
import org.ace.insurance.system.common.salesPoints.persistence.interfaces.ISalesPointsDAO;
import org.ace.insurance.system.common.school.pesistance.interfaces.ISchoolDAO;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.persistence.interfaces.ITownshipDAO;
import org.ace.insurance.system.common.typesOfSport.persistence.interfaces.ITypesOfSportDAO;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.life.proposal.InsuredPersonInfoDTO;
import org.ace.insurance.web.mobileforlife.LifeProposalDTO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("LifeProposalDAO")
public class LifeProposalDAO extends BasicDAO implements ILifeProposalDAO {

	@Resource(name = "AgentDAO")
	private IAgentDAO agentDAO;

	@Resource(name = "BranchDAO")
	private IBranchDAO branchDAO;

	@Resource(name = "LifePolicyDAO")
	private ILifePolicyDAO lifePolicyDAO;

	@Resource(name = "OrganizationDAO")
	private IOrganizationDAO organizationDAO;

	@Resource(name = "BankBranchDAO")
	private IBankBranchDAO bankBranchDAO;

	@Resource(name = "SalesPointsDAO")
	private ISalesPointsDAO salesPointsDAO;

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Resource(name = "TownshipDAO")
	private ITownshipDAO townshipDAO;

	@Resource(name = "OccupationDAO")
	private IOccupationDAO occupationDAO;

	@Resource(name = "ProductDAO")
	private IProductDAO productDAO;

	@Resource(name = "RelationShipDAO")
	private IRelationShipDAO relationshipDAO;

	@Resource(name = "PaymentTypeDAO")
	private IPaymentTypeDAO paymentTypeDAO;

	@Resource(name = "TypesOfSportDAO")
	private ITypesOfSportDAO typesOfSportDAO;

	@Resource(name = "RiskyOccupationDAO")
	private IRiskyOccupationDAO riskyOccupationDAO;

	@Resource(name = "GradeInfoDAO")
	private IGradeInfoDAO gradeInfoDAO;

	@Resource(name = "SchoolDAO")
	private ISchoolDAO schoolDAO;

	@Resource(name = "CountryDAO")
	private ICountryDAO countryDAO;

	@Resource(name = "IndustryDAO")
	private IIndustryDAO industryDAO;

	@Resource(name = "QualificationDAO")
	private IQualificationDAO qualificationDAO;

	@Resource(name = "ReligionDAO")
	private IReligionDAO religionDAO;

	@Resource(name = "LifePolicyHistoryDAO")
	private ILifePolicyHistoryDAO lifePolicyHistoryDAO;

	@Resource(name = "LifeSurrenderProposalDAO")
	private ILifeSurrenderProposalDAO lifeSurrenderProposalDAO;
	
	// Declaring local variables to indicate customer existence
	private boolean customerExist = false;
	private String customerTempId = "";
	private int period = 0;
	private String paymentTypeID = "";

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal insert(LifeProposal lifeProposal) throws DAOException {
		try {
			em.persist(lifeProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeProposal", pe);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void insertSurvey(LifeSurvey lifeSurvey) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM LifeSurvey l WHERE l.lifeProposal.id = :lifeProposalId");
			delQuery.setParameter("lifeProposalId", lifeSurvey.getLifeProposal().getId());
			delQuery.executeUpdate();
			em.persist(lifeSurvey);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Survey", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(LifeProposal lifeProposal) throws DAOException {
		try {
			em.merge(lifeProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifeProposal", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(LifeProposal lifeProposal) throws DAOException {
		try {
			lifeProposal = em.merge(lifeProposal);
			em.remove(lifeProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifeProposal", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void addAttachment(LifeProposal lifeProposal) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM LifeProposalAttachment l WHERE l.lifeProposal.id = :lifeProposalId");
			delQuery.setParameter("lifeProposalId", lifeProposal.getId());
			delQuery.executeUpdate();
			for (LifeProposalAttachment att : lifeProposal.getAttachmentList()) {
				em.persist(att);
			}
			for (ProposalInsuredPerson pin : lifeProposal.getProposalInsuredPersonList()) {
				Query query = em.createQuery("DELETE FROM InsuredPersonAttachment l WHERE l.proposalInsuredPerson.id = :proposalInsuredPersonId");
				query.setParameter("proposalInsuredPersonId", pin.getId());
				query.executeUpdate();
			}
			for (ProposalInsuredPerson pin : lifeProposal.getProposalInsuredPersonList()) {
				for (InsuredPersonAttachment att : pin.getAttachmentList()) {
					em.persist(att);
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Attachment", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal findById(String id) throws DAOException {
		LifeProposal result = null;
		try {
			result = em.find(LifeProposal.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal findLifePortalById(String id) throws DAOException {
		LifeProposal lifeProposal = null;
		try {
			String queryStr = "select distinct l.SUBMITTEDDATE, l.PAYMENTTYPEID, l.AGENTID, l.CUSTOMERID, " + "p.ID, p.INITIALID, p.FIRSTNAME, p.MIDDLENAME, p.LASTNAME, "
					+ "p.FATHERNAME, p.IDNO, p.IDTYPE, p.DATEOFBIRTH, p.PROPOSEDSUMINSURED, p.RESIDENTADDRESS, " + "p.RESIDENTTOWNSHIPID, p.OCCUPATIONID, p.GENDER, p.PRODUCTID "
					+ "from lproposal_request l inner join lproposal_request_insuredperson p on l.id = p.lproposalrequestid " + "where l.ID = '" + id + "' ";
			Query q = em.createNativeQuery(queryStr);
			List<Object[]> results = q.getResultList();
			if (results.size() > 0) {

				lifeProposal = new LifeProposal();
				lifeProposal.setPortalId(id);
				lifeProposal.setSubmittedDate((Date) results.get(0)[0]);

				String paymentTypeId = (String) results.get(0)[1];
				if (!StringUtils.isBlank(paymentTypeId)) {
					PaymentType paymentType = paymentTypeDAO.findById(paymentTypeId);
					lifeProposal.setPaymentType(paymentType);
				}

				String agentId = (String) results.get(0)[2];
				if (!StringUtils.isBlank(agentId)) {
					Agent agent = agentDAO.findById(agentId);
					lifeProposal.setAgent(agent);
				}

				String customerId = (String) results.get(0)[3];
				if (!StringUtils.isBlank(customerId)) {
					Customer customer = customerDAO.findById(customerId);
					lifeProposal.setCustomer(customer);
				}

				List<ProposalInsuredPerson> proposalInsuredPersonList = new ArrayList<ProposalInsuredPerson>();
				for (Object[] o : results) {
					ProposalInsuredPerson insuredPerson = new ProposalInsuredPerson();
					String insuredPersonId = (String) o[4];
					insuredPerson.setInitialId((String) o[5]);

					Name name = new Name();
					name.setFirstName((String) o[6]);
					name.setMiddleName((String) o[7]);
					name.setLastName((String) o[8]);
					insuredPerson.setName(name);

					insuredPerson.setFatherName((String) o[9]);
					insuredPerson.setIdNo((String) o[10]);
					insuredPerson.setIdType(IdType.valueOf((String) o[11]));
					insuredPerson.setDateOfBirth((Date) o[12]);
					insuredPerson.setProposedSumInsured((Double) o[13]);

					ResidentAddress residentAddress = new ResidentAddress();
					residentAddress.setResidentAddress(((String) o[14]));
					String townshipId = (String) o[15];
					if (!StringUtils.isBlank(townshipId)) {
						Township township = townshipDAO.findById(townshipId);
						residentAddress.setTownship(township);
					}
					insuredPerson.setResidentAddress(residentAddress);

					String occupationId = (String) o[16];
					if (!StringUtils.isBlank(occupationId)) {
						Occupation occupation = occupationDAO.findById(occupationId);
						insuredPerson.setOccupation(occupation);
					}

					insuredPerson.setGender(Gender.valueOf(((String) o[17])));

					String productId = (String) o[18];
					if (!StringUtils.isBlank(productId)) {
						Product product = productDAO.findById(productId);
						insuredPerson.setProduct(product);
						insuredPerson.setKeyFactorValueList(loadKeyFactor(product, insuredPerson));
					}

					String queryStr1 = "select distinct b.initialid, b.firstname, b.middlename, b.lastname, b.idno, b.idtype, "
							+ "b.gender, b.residentaddress, b.residenttownshipid, b.percentage, b.age, b.relationshipid "
							+ "from lproposal_request_insuredperson p inner join lproposal_request_beneficiaries b on p.id = b.isuraedpersonid " + "where p.id = '"
							+ insuredPersonId + "' ";
					Query q1 = em.createNativeQuery(queryStr1);
					List<Object[]> beneficiaries = q1.getResultList();
					if (beneficiaries.size() > 0) {

						List<InsuredPersonBeneficiaries> insuredPersonBeneficiariesList = new ArrayList<InsuredPersonBeneficiaries>();
						for (Object[] b : beneficiaries) {
							InsuredPersonBeneficiaries insuredPersonBeneficiaries = new InsuredPersonBeneficiaries();

							insuredPersonBeneficiaries.setInitialId((String) b[0]);
							Name iName = new Name();
							iName.setFirstName((String) b[1]);
							iName.setMiddleName((String) b[2]);
							iName.setLastName((String) b[3]);
							insuredPersonBeneficiaries.setName(iName);

							insuredPersonBeneficiaries.setIdNo((String) b[4]);
							insuredPersonBeneficiaries.setIdType(IdType.valueOf((String) b[5]));
							insuredPersonBeneficiaries.setGender(Gender.valueOf(((String) b[6])));

							ResidentAddress iResidentAddress = new ResidentAddress();
							iResidentAddress.setResidentAddress(((String) b[7]));
							String iTownshipId = (String) b[8];
							if (!StringUtils.isBlank(iTownshipId)) {
								Township township = townshipDAO.findById(iTownshipId);
								iResidentAddress.setTownship(township);
							}
							insuredPersonBeneficiaries.setResidentAddress(iResidentAddress);

							insuredPersonBeneficiaries.setPercentage(((Double) b[9]).floatValue());
							insuredPersonBeneficiaries.setAge((Integer) b[10]);

							String relationshipId = (String) o[11];
							if (!StringUtils.isBlank(relationshipId)) {
								RelationShip relationShip = relationshipDAO.findById(relationshipId);
								insuredPersonBeneficiaries.setRelationship(relationShip);
							}

							insuredPersonBeneficiariesList.add(insuredPersonBeneficiaries);
						}
						insuredPerson.setInsuredPersonBeneficiariesList(insuredPersonBeneficiariesList);
					}

					proposalInsuredPersonList.add(insuredPerson);
				}
				lifeProposal.setInsuredPersonList(proposalInsuredPersonList);
			}

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal", pe);
		}
		return lifeProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findAll() throws DAOException {
		List<LifeProposal> result = null;
		try {
			Query q = em.createNamedQuery("LifeProposal.findAll");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of LifeProposal", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findByDate(Date startDate, Date endDate) throws DAOException {
		List<LifeProposal> result = null;
		try {
			Query q = em.createNamedQuery("LifeProposal.findByDate");
			q.setParameter("startDate", startDate);
			q.setParameter("endDate", endDate);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by Date: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposal> findByIdList(List<String> proposalIdList) throws DAOException {
		List<LifeProposal> result = new ArrayList<LifeProposal>();
		try {
			Query query = em.createQuery("SELECT l FROM LifeProposal l WHERE l.id IN :ids");
			query.setParameter("ids", proposalIdList);
			result = query.getResultList();
			em.flush();
		} catch (NoResultException ne) {
			// do nothing
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by Date: ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException {
		try {
			Query q = em.createNamedQuery("LifeProposal.updateCompleteStatus");
			q.setParameter("complete", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update complete status", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuPersonMedicalStatus(List<ProposalInsuredPerson> proposalInsuredPersonList) throws DAOException {
		try {
			for (ProposalInsuredPerson insuPerson : proposalInsuredPersonList) {
				String queryString = "UPDATE ProposalInsuredPerson p SET p.clsOfHealth = :clsOfHealth WHERE p.id = :insuPersonId";
				Query query = em.createQuery(queryString);
				query.setParameter("clsOfHealth", insuPerson.getClsOfHealth());
				query.setParameter("insuPersonId", insuPerson.getId());
				query.executeUpdate();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to approved InsuredPerson Approbal Info", pe);
		}
	}

	/* used for Life Proposal Enquire (lifeProposalEnquiry.xhtml) */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LPL001> findByEnquiryCriteria(EnquiryCriteria criteria, List<Product> productList) throws DAOException {
		List<LPL001> results = new ArrayList<LPL001>();
		try {
			/* create query */
			StringBuffer queryString = new StringBuffer();
			queryString.append(" SELECT l.id, l.proposalNo, a.initialId, a.name,  ");
			queryString.append(" CONCAT(TRIM(c.initialId),' ', TRIM(c.name.firstName), ' ', TRIM(c.name.middleName), ' ', TRIM(c.name.lastName)),");
			queryString.append(" o.name, l.branch.name, SUM(pi.proposedPremium),SUM(pi.proposedSumInsured),SUM(COALESCE(pi.unit,0)), ");
			queryString.append(" l.paymentType.name, l.submittedDate, ");
			queryString.append(" l.saleChannelType,b.name,sp.name");
			queryString.append(" FROM LifeProposal l INNER JOIN l.proposalInsuredPersonList pi ");
			queryString.append(" LEFT OUTER JOIN l.agent a ");
			queryString.append(" LEFT OUTER JOIN l.customer c ");
			queryString.append(" LEFT OUTER JOIN l.organization o ");
			queryString.append(" LEFT OUTER JOIN l.saleBank b");
			queryString.append(" LEFT OUTER JOIN l.salesPoints sp");
			queryString.append(" WHERE l.proposalNo IS NOT NULL  ");
			if (ProposalType.UNDERWRITING.equals(criteria.getProposalType())) {
				queryString.append(" AND l.lifePolicy IS NULL");
			} else if (ProposalType.ENDORSEMENT.equals(criteria.getProposalType())) {
				queryString.append(" AND l.lifePolicy IS NOT NULL");
			}
			if (criteria.getInsuranceType() != null) {
				queryString.append(" AND pi.product.insuranceType = :insuranceType");
			}
			if (criteria.getAgent() != null) {
				queryString.append(" AND l.agent.id = :agentId");
			}
			if (criteria.getStartDate() != null) {
				queryString.append(" AND l.submittedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				queryString.append(" AND l.submittedDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				queryString.append(" AND l.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				queryString.append(" AND l.organization.id = :organizationId");
			}
			if (criteria.getSaleBank() != null) {
				queryString.append(" AND l.saleBank.id = :saleBankId");
			}
			if (criteria.getBranch() != null) {
				queryString.append(" AND l.branch.id = :branchId");
			}
			if (criteria.getSalePoint() != null) {
				queryString.append(" AND sp.id = :salePointId");
			}
			if (criteria.getProduct() != null) {
				queryString.append(" AND pi.product.id = :productId");
			} else if (productList != null && !productList.isEmpty()) {
				queryString.append(" AND pi.product IN :productList");
			}
			if (!criteria.getProposalNo().isEmpty()) {
				queryString.append(" AND l.proposalNo like :proposalNo");
			}
			if (criteria.getProposalType() != null) {
				queryString.append(" AND l.proposalType = :proposalType");
			}
			if (criteria.getSaleChannelType() != null) {
				queryString.append(" AND l.saleChannelType =:saleChannelType");
			}
			if (criteria.getInsuredPersonName() != null) {
				queryString.append(" AND CONCAT(FUNCTION('REPLACE',pi.name.firstName,' ',''),FUNCTION('REPLACE',pi.name.middleName,' ','')");
				queryString.append(",FUNCTION('REPLACE',pi.name.lastName,' ','')) LIKE :insuredPersonName");
			}
			queryString.append(" GROUP BY l.id, l.proposalNo, a.initialId, a.name,c.initialId,c.name.firstName,c.name.middleName,c.name.lastName");
			queryString.append(", o.name, l.branch.name,b, l.branch.name,l.paymentType.name, l.submittedDate,l.saleChannelType,b.name,sp.name");
			/* Executed query */
			Query query = em.createQuery(queryString.toString());
			if (criteria.getProposalType() != null) {
				query.setParameter("proposalType", criteria.getProposalType());
			}
			if (criteria.getInsuranceType() != null) {
				query.setParameter("insuranceType", criteria.getInsuranceType());
			}
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
			if (criteria.getSaleBank() != null) {
				query.setParameter("saleBankId", criteria.getSaleBank().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSalePoint() != null) {
				query.setParameter("salePointId", criteria.getSalePoint().getId());
			}
			if (criteria.getProduct() != null) {
				query.setParameter("productId", criteria.getProduct().getId());
			} else if (productList != null && !productList.isEmpty()) {
				query.setParameter("productList", productList);
			}
			if (!criteria.getProposalNo().isEmpty()) {
				if(criteria.getProposalType().equals(ProposalType.SURRENDER)) {
					LifeSurrenderProposal surrenderProposal = lifeSurrenderProposalDAO.findByProposalNo(criteria.getProposalNo());
					LifePolicy lifePolicy = lifePolicyDAO.findById(surrenderProposal.getLifePolicy().getId());
					LifeProposal lifeProposal = findById(lifePolicy.getLifeProposal().getId());
					query.setParameter("proposalNo", "%" + lifeProposal.getProposalNo() + "%");
				} else {
					query.setParameter("proposalNo", "%" + criteria.getProposalNo() + "%");
				}
			}
			if (criteria.getSaleChannelType() != null) {
				query.setParameter("saleChannelType", criteria.getSaleChannelType());
			}
			if (criteria.getInsuredPersonName() != null) {
				query.setParameter("insuredPersonName", "%" + criteria.getInsuredPersonName().replace(" ", "") + "%");
			}
			List<Object[]> objectList = query.getResultList();
			String proposalId;
			String proposalNo;
			String agent = null;
			String customer = null;
			String oranization;
			String branch;
			double proposedPremium;
			double proposedSumInsured;
			int unit;
			String paymentType;
			Date submittedDate;
			String saleBank, salePoint;
			SaleChannelType saleChannelType;

			for (Object[] objArr : objectList) {
				agent = null;
				customer = null;
				proposalId = (String) objArr[0];
				proposalNo = (String) objArr[1];
				if (objArr[2] != null) {
					agent = (String) objArr[2] + ((Name) objArr[3]).getFullName();
				}
				if (objArr[4] != null) {
					customer = (String) objArr[4];
				}
				oranization = (String) objArr[5];
				branch = (String) objArr[6];
				proposedPremium = (Double) objArr[7];
				proposedSumInsured = (Double) objArr[8];
				unit = (int) objArr[9];
				paymentType = (String) objArr[10];
				submittedDate = (Date) objArr[11];
				saleChannelType = (SaleChannelType) objArr[12];
				saleBank = (String) objArr[13];
				salePoint = (String) objArr[14];
				customer = customer == null ? oranization : customer;
				results.add(new LPL001(proposalId, proposalNo, customer, agent, branch, proposedPremium, proposedSumInsured, paymentType, submittedDate, saleChannelType, saleBank,
						unit, salePoint));
			}
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by EnquiryCriteria : ", pe);
		}

		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ProposalInsuredPerson findProposalInsuredPersonById(String id) throws DAOException {
		ProposalInsuredPerson result = null;
		try {
			result = em.find(ProposalInsuredPerson.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find ProposalInsuredPerson", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeSurvey findSurveyByProposalId(String proposalId) throws DAOException {
		LifeSurvey result = null;
		try {
			Query query = em.createQuery("SELECT l FROM LifeSurvey l WHERE l.lifeProposal.id = :id");
			query.setParameter("id", proposalId);
			result = (LifeSurvey) query.getSingleResult();
			em.flush();
		} catch (NoResultException ne) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find Proposal Survey : ", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PolicyInsuredPerson> findPolicyInsuredPersonByParameters(Name name, String idNo, String fatherName, Date dob) throws DAOException {
		List<PolicyInsuredPerson> results = null;

		try {
			Query q = em.createQuery("SELECT c From PolicyInsuredPerson c WHERE c.name = :name " + " AND c.fatherName = :fatherName AND c.dateOfBirth = :dob AND c.idNo = :idNo");
			q.setParameter("name", name);
			q.setParameter("fatherName", fatherName);
			q.setParameter("dob", dob);
			q.setParameter("idNo", idNo);
			results = (List<PolicyInsuredPerson>) q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of Customer", pe);
		}
		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findStatusById(String id) throws DAOException {
		String result = null;
		try {
			Query q = em.createNativeQuery("SELECT STATUS FROM LPROPOSAL_REQUEST WHERE ID = '" + id + "' ");
			result = (String) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStatus(String status, String id) throws DAOException {
		try {
			Query q = em.createNativeQuery("UPDATE LPROPOSAL_REQUEST SET STATUS = '" + status + "' WHERE ID = '" + id + "' ");
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update status", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal findByProposalNo(String proposalNo) throws DAOException {
		LifeProposal result = null;
		try {
			Query query = em.createQuery("SELECT m FROM LifeProposal m WHERE m.proposalNo = :proposalNo");
			query.setParameter("proposalNo", proposalNo);
			result = (LifeProposal) query.getSingleResult();
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by Proposal No : " + proposalNo, pe);
		}
		return result;
	}

	private List<InsuredPersonKeyFactorValue> loadKeyFactor(Product product, ProposalInsuredPerson insuredPersonInfoDTO) {
		List<InsuredPersonKeyFactorValue> keyFactorValueList = new ArrayList<InsuredPersonKeyFactorValue>();
		for (KeyFactor insKFV : product.getKeyFactorList()) {
			InsuredPersonKeyFactorValue value = new InsuredPersonKeyFactorValue(insKFV);
			if (KeyFactorChecker.isSumInsured(insKFV)) {
				value.setValue(insuredPersonInfoDTO.getProposedSumInsured() + "");
			} else if (KeyFactorChecker.isTerm(insKFV)) {
				value.setValue(insuredPersonInfoDTO.getLifeProposal().getPeriodMonth() + "");
			} else if (KeyFactorChecker.isAge(insKFV)) {
				value.setValue(insuredPersonInfoDTO.getAgeForNextYear() + "");
			}
			keyFactorValueList.add(value);
		}
		return keyFactorValueList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProposalPersonCode(List<ProposalInsuredPerson> personList) throws DAOException {
		try {
			for (ProposalInsuredPerson person : personList) {
				Query query = em.createQuery("UPDATE ProposalInsuredPerson p SET p.insPersonCodeNo = :personCodeNo WHERE p.id = :id");
				query.setParameter("id", person.getId());
				query.setParameter("personCodeNo", person.getInsPersonCodeNo());
				query.executeUpdate();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update insured person code no", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProposalBeneficiaryCode(List<InsuredPersonBeneficiaries> beneficiaryList) throws DAOException {
		try {
			for (InsuredPersonBeneficiaries beneficiary : beneficiaryList) {
				Query query = em.createQuery("UPDATE InsuredPersonBeneficiaries p SET p.beneficiaryNo = :beneficiaryNo WHERE p.id = :id");
				query.setParameter("id", beneficiary.getId());
				query.setParameter("beneficiaryNo", beneficiary.getBeneficiaryNo());
				query.executeUpdate();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to update beneficiary code no", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProposalStatus(ProposalStatus status, String proposalId) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE LifeProposal l SET l.proposalStatus = :status WHERE l.id = :id");
			q.setParameter("status", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Proposal status", pe);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public Object[] updateProposalTempStatus(String proposalNo, boolean status) throws DAOException {
		
		Object[] obj = {this.customerExist, this.customerTempId};
		
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

	@Transactional(propagation = Propagation.REQUIRED)
	public boolean findOverMaxSIByMotherNameAndNRCAndInsuNameAndNRC(LifeProposal lifeProposal, InsuredPersonInfoDTO insuredPersonInfoDTO) {
		double totalSI = 0;
		try {
			StringBuffer query = new StringBuffer();
			Map<String, Object> param = new HashMap<String, Object>();
			query.append("select case when sum(i.proposedSumInsured) is null then 0.0 else sum(i.proposedSumInsured) end");
			query.append(" from LifeProposal p join p.proposalInsuredPersonList i join i.product pro join p.customer c where pro.id like :productId");
			param.put("productId", "%" + KeyFactorChecker.getStudentLifeID() + "%");
			query.append(
					" AND function('Replace',(CONCAT(COALESCE(c.initialId,''),COALESCE(c.name.firstName,''), COALESCE(c.name.middleName,''), COALESCE(c.name.lastName,''))),' ','') like :customerName");
			param.put("customerName", "%" + lifeProposal.getCustomer().getFullName().replaceAll(" ", "") + "%");
			query.append(" AND c.dateOfBirth = :customerDOB");
			param.put("customerDOB", lifeProposal.getCustomer().getDateOfBirth());
			if (lifeProposal.getCustomer().getIdNo() != null) {
				query.append(" AND c.fullIdNo like :customerIdNo");
				param.put("customerIdNo", "%" + lifeProposal.getCustomer().getFullIdNo().replaceAll(" ", "") + "%");
			}

			query.append(" AND function('Replace',i.parentName,' ','') like :parentName");
			param.put("parentName", "%" + insuredPersonInfoDTO.getParentName().replaceAll(" ", "") + "%");
			if (insuredPersonInfoDTO.getParentDOB() != null) {
				query.append(" AND i.parentDOB = :parentDOB");
				param.put("parentDOB", insuredPersonInfoDTO.getParentDOB());
			}
			if (insuredPersonInfoDTO.getParentIdNo() != null) {
				query.append(" AND i.parentIdNo like :parentIdNo");
				param.put("parentIdNo", "%" + insuredPersonInfoDTO.getParentIdNo().replaceAll(" ", "") + "%");
			}
			query.append(
					" AND function('Replace',(CONCAT(COALESCE(i.initialId,''),COALESCE(i.name.firstName,''),COALESCE(i.name.middleName,''), COALESCE(i.name.lastName,''))),' ','') like :childName");
			param.put("childName", "%" + insuredPersonInfoDTO.getFullName().replaceAll(" ", "") + "%");
			if (insuredPersonInfoDTO.getIdNo() != null && !insuredPersonInfoDTO.getIdNo().isEmpty()) {
				query.append(" AND i.idNo like :childIdNo");
				param.put("childIdNo", "%" + insuredPersonInfoDTO.getIdNo().replaceAll(" ", "") + "%");
			} else {
				query.append(" AND i.dateOfBirth = :childDOB");
				param.put("childDOB", insuredPersonInfoDTO.getDateOfBirth());
			}
			if (lifeProposal.getId() != null && !lifeProposal.getId().isEmpty()) {
				query.append(" AND p.id!=:lifeProposalId");
				param.put("lifeProposalId", lifeProposal.getId());
			}
			Query q = em.createQuery(query.toString());
			for (String key : param.keySet()) {
				q.setParameter(key, param.get(key));
			}
			totalSI = (double) q.getSingleResult();
		} catch (PersistenceException pe) {
			throw translate("Failed to find OverMaxSI By MotherName And NRC And InsuName And NRC", pe);
		}

		return totalSI + insuredPersonInfoDTO.getSumInsuredInfo() > 100000000 ? true : false;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeProposalDTO> findMobileLifeProposal() throws DAOException {
		List<LifeProposalDTO> lifeproposallist = new ArrayList<>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query q = em.createNativeQuery("select p.proposalNo as proposalNo,MAX(p.SUBMITTEDDATE) as submitteddate,Max(p.SALECHANNELTYPE) as salechanneltype,\r\n"
					+ "				 MAX((COALESCE(RTRIM (C.INITIALID),'')+' '+COALESCE(LTRIM(C.FIRSTNAME),'')+' '+COALESCE(RTRIM (C.MIDDLENAME),'') +' '+COALESCE(RTRIM (C.LASTNAME),'')))  AS insuredpersonname \r\n"
					+ "				  from PROPOSAL_LIFE_MEDICAL_TEMP p \r\n"
					+ "				left join PROPOSAL_LIFE_MEDICAL_INSUREDPERSON_TEMP c on p.id=c.lifeproposalid where status='false' and healthType is null group by p.proposalNo, p.CREATEDDATE order by p.CREATEDDATE desc");
			objectList = q.getResultList();
			for (Object[] object : objectList) {
				LifeProposalDTO lifeProposal = new LifeProposalDTO();
				lifeProposal.setProposalNo(String.valueOf(object[0]));
				lifeProposal.setSubmitteddate((Date) (object[1]));
				lifeProposal.setSalechanneltype(String.valueOf(object[2]));
				lifeProposal.setInsuredpersonName(String.valueOf(object[3]));
				lifeproposallist.add(lifeProposal);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Life Proposal", pe);

		}
		return lifeproposallist;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal findByProposalNoFromTemp(String proposalNo) throws DAOException {
		LifeProposal result = null;
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_TEMP t WHERE t.proposalNo = ?");
			query.setParameter(1, proposalNo);
			objectList = query.getResultList();

			for (Object[] object : objectList) {
				LifeProposal temp = new LifeProposal();
				temp.setComplete(StringUtils.contains("null", String.valueOf(object[1])) ? null :(Boolean) object[1]);
				temp.setNonFinancialEndorse(StringUtils.contains("null", String.valueOf(object[2])) ? null :(Boolean) object[2]);
				temp.setPaymentTerm(StringUtils.contains("null", String.valueOf(object[3])) ? null :(Integer) object[3]);
				temp.setPortalId(StringUtils.contains("null", String.valueOf(object[4])) ? null : String.valueOf(object[4]));
				temp.setProposalNo(StringUtils.contains("null", String.valueOf(object[5])) ? null :String.valueOf(object[5]));
				temp.setProposalType(StringUtils.contains("null", String.valueOf(object[6])) ? null :ProposalType.valueOf(String.valueOf(object[6])));
				temp.setSaleChannelType(StringUtils.contains("null", String.valueOf(object[7])) ? null :SaleChannelType.valueOf(String.valueOf(object[7])));
				temp.setSubmittedDate(StringUtils.contains("null", String.valueOf(object[8])) ? null :(Date) object[8]);
				temp.setVersion(StringUtils.contains("null", String.valueOf(object[9])) ? null :(Integer) object[9]);
				temp.setAgent(StringUtils.contains("null", String.valueOf(object[14])) ? null : agentDAO.findById(String.valueOf(object[14])));
				temp.setBranch(StringUtils.contains("null", String.valueOf(object[15])) ? null : branchDAO.findById(String.valueOf(object[15])));
				temp.setLifePolicy(StringUtils.contains("null", String.valueOf(object[17])) ? null : lifePolicyDAO.findById(String.valueOf(object[17])));
				temp.setOrganization(StringUtils.contains("null", String.valueOf(object[18])) ? null : organizationDAO.findById(String.valueOf(object[18])));
				temp.setPaymentType(StringUtils.contains("null", String.valueOf(object[19])) ? null : paymentTypeDAO.findById(String.valueOf(object[19])));
				temp.setSaleBank(StringUtils.contains("null", String.valueOf(object[20])) ? null : bankBranchDAO.findById(String.valueOf(object[20])));
				temp.setProposalStatus(StringUtils.contains("null", String.valueOf(object[21])) ? null : ProposalStatus.valueOf(String.valueOf(object[21])));
				temp.setSpecialDiscount(StringUtils.contains("null", String.valueOf(object[22])) ? null :(Double) object[22]);
				temp.setPeriodMonth(StringUtils.contains("null", String.valueOf(object[23])) ? null :(Integer) object[23]);
				temp.setStartDate(StringUtils.contains("null", String.valueOf(object[24])) ? null :(Date) object[24]);
				temp.setEndDate(StringUtils.contains("null", String.valueOf(object[25])) ? null :(Date) object[25]);
				temp.setCurrencyRate(StringUtils.contains("null", String.valueOf(object[26])) ? null :(Double) object[26]);
				temp.setSalesPoints(StringUtils.contains("null", String.valueOf(object[27])) ? null : salesPointsDAO.findById(String.valueOf(object[27])));
				temp.setCustomerClsOfHealth(StringUtils.contains("null", String.valueOf(object[28])) ? null : ClassificationOfHealth.valueOf(String.valueOf(object[28])));
				temp.setSkipPaymentTLF(StringUtils.contains("null", String.valueOf(object[29])) ? null :(Boolean) object[29]);
				temp.setCustomer(findCustomerFromTemp(String.valueOf(object[16])));
				
				this.paymentTypeID = temp.getPaymentType().getId();
				List<ProposalInsuredPerson> insuredPersonList = findProposalInsuredPersonFromTemp(String.valueOf(object[0]), temp);
				insuredPersonList.forEach(insuredPerson -> {
					temp.getProposalInsuredPersonList().add(insuredPerson);
				});
				
				result = temp;
			}
			em.flush();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeProposal by Proposal No : " + proposalNo, pe);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ProposalInsuredPerson> findProposalInsuredPersonFromTemp(String proposalId, LifeProposal lifeProposal) throws DAOException {
		List<ProposalInsuredPerson> resultList = new ArrayList<ProposalInsuredPerson>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_INSUREDPERSON_TEMP t WHERE t.LIFEPROPOSALID = ?");
			query.setParameter(1, proposalId);
			objectList = query.getResultList();
			
			for (Object[] object : objectList) {
				
				ProposalInsuredPerson temp = new ProposalInsuredPerson();
				
				temp.setAddOnTermPremium(StringUtils.contains("null", String.valueOf(object[1])) ? null : (Double) object[1]); 
				temp.setAge(StringUtils.contains("null", String.valueOf(object[2])) ? null : (Integer) object[2]);
				temp.setApproved(StringUtils.contains("null", String.valueOf(object[3])) ? null : (Boolean) object[3]);
				temp.setApprovedSumInsured(StringUtils.contains("null", String.valueOf(object[4])) ? null : (Double) object[4]);
				temp.setBasicTermPremium(StringUtils.contains("null", String.valueOf(object[5])) ? null : (Double) object[5]);
				temp.setClsOfHealth(StringUtils.contains("null", String.valueOf(object[6])) ? null : ClassificationOfHealth.valueOf(String.valueOf(object[6])));
				temp.setDateOfBirth(StringUtils.contains("null", String.valueOf(object[7])) ? null : (Date) object[7]);
				temp.setEndorsementNetAddonPremium(StringUtils.contains("null", String.valueOf(object[8])) ? null : (Double) object[8]);
				temp.setEndorsementNetBasicPremium(StringUtils.contains("null", String.valueOf(object[9])) ? null : (Double) object[9]);
				temp.setEndorsementStatus(StringUtils.contains("null", String.valueOf(object[10])) ? null : EndorsementStatus.valueOf(String.valueOf(object[10])));
				temp.setFatherName(StringUtils.contains("null", String.valueOf(object[11])) ? null : String.valueOf(object[11]));
				temp.setGender(StringUtils.contains("null", String.valueOf(object[12])) ? null : Gender.valueOf(String.valueOf(object[12])));
				temp.setIdNo(StringUtils.contains("null", String.valueOf(object[13])) ? null : String.valueOf(object[13]));
				temp.setIdType(StringUtils.contains("null", String.valueOf(object[14])) ? null : IdType.valueOf(String.valueOf(object[14])));
				temp.setInPersonGroupCodeNo(StringUtils.contains("null", String.valueOf(object[15])) ? null : String.valueOf(object[15]));
				temp.setInitialId(StringUtils.contains("null", String.valueOf(object[16])) ? null : String.valueOf(object[16]));
				temp.setInsPersonCodeNo(StringUtils.contains("null", String.valueOf(object[17])) ? null : String.valueOf(object[17]));
				temp.setInterest(StringUtils.contains("null", String.valueOf(object[18])) ? null : (Double) object[18]);
				temp.setNeedMedicalCheckup(StringUtils.contains("null", String.valueOf(object[19])) ? null : (Boolean) object[19]);
				temp.setProposedPremium(StringUtils.contains("null", String.valueOf(object[20])) ? null : (Double) object[20]);
				temp.setProposedSumInsured(StringUtils.contains("null", String.valueOf(object[21])) ? null : (Double) object[21]);
				temp.setRejectReason(StringUtils.contains("null", String.valueOf(object[22])) ? null : String.valueOf(object[22]));
				temp.setUnit(StringUtils.contains("null", String.valueOf(object[23])) ? null : (Integer) object[23]);

				Name name = new Name();
				name.setFirstName(StringUtils.contains("null", String.valueOf(object[25])) ? null : String.valueOf(object[25]));
				name.setLastName(StringUtils.contains("null", String.valueOf(object[26])) ? null : String.valueOf(object[26]));
				name.setMiddleName(StringUtils.contains("null", String.valueOf(object[27])) ? null : String.valueOf(object[27]));

				temp.setName(name);

				ResidentAddress residentAddress = new ResidentAddress();
				residentAddress.setResidentAddress(StringUtils.contains("null", String.valueOf(object[32])) ? null : String.valueOf(object[32]));
				residentAddress.setTownship(StringUtils.contains("null", String.valueOf(object[33])) ? null : townshipDAO.findById(String.valueOf(object[33])));

				temp.setResidentAddress(residentAddress);

				temp.setOccupation(StringUtils.contains("null", String.valueOf(object[36])) ? null : occupationDAO.findById(String.valueOf(object[36])));
				temp.setProduct(StringUtils.contains("null", String.valueOf(object[37])) ? null : productDAO.findById(String.valueOf(object[37])));
				temp.setTypesOfSport(StringUtils.contains("null", String.valueOf(object[38])) ? null : typesOfSportDAO.findById(String.valueOf(object[38])));
				temp.setRelationship(StringUtils.contains("null", String.valueOf(object[39])) ? null : relationshipDAO.findById(String.valueOf(object[39])));
				temp.setApprovedUnit(StringUtils.contains("null", String.valueOf(object[40])) ? null : (Integer) object[40]);
				temp.setWeight(StringUtils.contains("null", String.valueOf(object[41])) ? null : (Integer) object[41]);
				temp.setHeight(StringUtils.contains("null", String.valueOf(object[42])) ? null : (Integer) object[42]);
				temp.setPremiumRate(StringUtils.contains("null", String.valueOf(object[43])) ? null : ((BigDecimal) object[43]).doubleValue());
				temp.setRiskyOccupation(StringUtils.contains("null", String.valueOf(object[44])) ? null : riskyOccupationDAO.findById(String.valueOf(object[44])));
				temp.setPhone(StringUtils.contains("null", String.valueOf(object[45])) ? null : String.valueOf(object[45]));
				temp.setParentDOB(StringUtils.contains("null", String.valueOf(object[46])) ? null : (Date) object[46]);
				temp.setGradeInfo(StringUtils.contains("null", String.valueOf(object[47])) ? null : gradeInfoDAO.findById(String.valueOf(object[47])));
				temp.setParentName(StringUtils.contains("null", String.valueOf(object[48])) ? null : String.valueOf(object[48]));
				temp.setParentIdNo(StringUtils.contains("null", String.valueOf(object[49])) ? null : String.valueOf(object[49]));
				temp.setParentIdType(StringUtils.contains("null", String.valueOf(object[50])) ? null : IdType.valueOf(String.valueOf(object[50])));
				temp.setSchool(StringUtils.contains("null", String.valueOf(object[51])) ? null : schoolDAO.findById(String.valueOf(object[51])));
				temp.setIsPaidPremiumForPaidup(StringUtils.contains("null", String.valueOf(object[52])) ? null : (Boolean) object[52]);
				
				if (KeyFactorChecker.isPersonalAccident(temp.getProduct())) {
					this.period = lifeProposal.getPeriodMonth();
				} else {
					this.period = (lifeProposal.getPeriodMonth() / 12);
				}
				
				temp.setLifeProposal(lifeProposal);
				temp.getProduct().getKeyFactorList().forEach(keyfactor -> {
					temp.getKeyFactorValueList().add(createKeyFactorValue(keyfactor, temp));
				});
				temp.setInsuredPersonBeneficiariesList(findBeneficiariesFromTemp(String.valueOf(object[0]), temp));
				temp.setCustomer(findCustomerFromTemp(String.valueOf(object[35])));
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
	public List<InsuredPersonBeneficiaries> findBeneficiariesFromTemp(String insuredPersonId, ProposalInsuredPerson insuredPerson) throws DAOException {
		List<InsuredPersonBeneficiaries> resultList = new ArrayList<InsuredPersonBeneficiaries>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_BENEFICIARIES_TEMP t WHERE t.INSUREDPERSONID = ?");
			query.setParameter(1, insuredPersonId);
			objectList = query.getResultList();
			
			for (Object[] object : objectList) {
				InsuredPersonBeneficiaries temp = new InsuredPersonBeneficiaries();
				
				temp.setAge(StringUtils.contains("null", String.valueOf(object[1])) ? null : (Integer) object[1]);
				temp.setBeneficiaryNo(StringUtils.contains("null", String.valueOf(object[2])) ? null : String.valueOf(object[2]));
				temp.setGender(StringUtils.contains("null", String.valueOf(object[3])) ? null : Gender.valueOf(String.valueOf(object[3])));
				temp.setIdNo(StringUtils.contains("null", String.valueOf(object[4])) ? null : String.valueOf(object[4]));
				temp.setIdType(StringUtils.contains("null", String.valueOf(object[5])) ? null : IdType.valueOf(String.valueOf(object[5])));
				temp.setInitialId(StringUtils.contains("null", String.valueOf(object[6])) ? null : String.valueOf(object[6]));
				temp.setPercentage(StringUtils.contains("null", String.valueOf(object[7])) ? null : (Float) object[7]);

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
				temp.setPhone(StringUtils.contains("null", String.valueOf(object[20])) ? null : String.valueOf(object[20]));
				temp.setDateOfBirth(StringUtils.contains("null", String.valueOf(object[21])) ? null : (Date) object[21]);
				temp.setProposalInsuredPerson(insuredPerson);
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
	public Customer findCustomerFromTemp(String customerId) throws DAOException {

		
		Customer customer = new Customer();

		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_CUSTOMER_TEMP t WHERE t.ID = ?");
			query.setParameter(1, customerId);
			List<Object[]> customerObj = query.getResultList();

			if (!customerObj.isEmpty()) {
				this.customerExist = (Boolean) (customerObj.get(0))[45];
				this.customerTempId = String.valueOf(customerObj.get(0)[0]);

				if (this.customerExist) {
					customer = customerDAO.findById(String.valueOf(customerObj.get(0)[46]));
				} else {
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
					customer.setMaritalStatus(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[14])) ? null : MaritalStatus.valueOf(String.valueOf(customerObj.get(0)[14])));
					customer.setPassportType(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[15])) ? null : PassportType.valueOf(String.valueOf(customerObj.get(0)[15])));
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
					officeAddress.setTownship(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[28])) ? null : townshipDAO.findById(String.valueOf(customerObj.get(0)[28])));

					customer.setOfficeAddress(officeAddress);

					PermanentAddress permanentAddress = new PermanentAddress();
					permanentAddress.setPermanentAddress(StringUtils.contains("null", String.valueOf(customerObj.get(0)[29])) ? null : String.valueOf(customerObj.get(0)[29]));
					permanentAddress.setTownship(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[30])) ? null : townshipDAO.findById(String.valueOf(customerObj.get(0)[30])));

					customer.setPermanentAddress(permanentAddress);

					ResidentAddress residentAddress = new ResidentAddress();
					residentAddress.setResidentAddress(StringUtils.contains("null", String.valueOf(customerObj.get(0)[35])) ? null : String.valueOf(customerObj.get(0)[35]));
					residentAddress.setTownship(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[36])) ? null : townshipDAO.findById(String.valueOf(customerObj.get(0)[36])));

					customer.setResidentAddress(residentAddress);

					customer.setBankBranch(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[37])) ? null : bankBranchDAO.findById(String.valueOf(customerObj.get(0)[37])));
					customer.setBranch(StringUtils.contains("null", String.valueOf(customerObj.get(0)[38])) ? null : branchDAO.findById(String.valueOf(customerObj.get(0)[38])));
					customer.setCountry(StringUtils.contains("null", String.valueOf(customerObj.get(0)[39])) ? null : countryDAO.findById(String.valueOf(customerObj.get(0)[39])));
					customer.setIndustry(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[40])) ? null : industryDAO.findById(String.valueOf(customerObj.get(0)[40])));
					customer.setOccupation(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[41])) ? null : occupationDAO.findById(String.valueOf(customerObj.get(0)[41])));
					customer.setQualification(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[42])) ? null : qualificationDAO.findById(String.valueOf(customerObj.get(0)[42])));
					customer.setReligion(
							StringUtils.contains("null", String.valueOf(customerObj.get(0)[43])) ? null : religionDAO.findById(String.valueOf(customerObj.get(0)[43])));
				
					customer.setFamilyInfo(findFamilyInfoFromTemp(String.valueOf(customerObj.get(0)[0])));
					
					customerDAO.insert(customer);
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
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FamilyInfo> findFamilyInfoFromTemp(String customerId) {
		
		List<FamilyInfo> familyInfoList = new ArrayList<FamilyInfo>();
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query query = em.createNativeQuery("SELECT * FROM PROPOSAL_LIFE_MEDICAL_CUSTOMERFAMILY_TEMP t WHERE t.CUSTOMERID = ?");
			query.setParameter(1, customerId);
			objectList = query.getResultList();
			
			FamilyInfo temp = new FamilyInfo();
			
			for (Object[] object : objectList) {
				
				temp.setDateOfBirth(StringUtils.contains("null", String.valueOf(object[0])) ? null :(Date) object[0]);
				temp.setIdNo(StringUtils.contains("null", String.valueOf(object[1])) ? null :String.valueOf(object[1]));
				temp.setIdType(StringUtils.contains("null", String.valueOf(object[2])) ? null : IdType.valueOf(String.valueOf(object[2])));
				temp.setInitialId(StringUtils.contains("null", String.valueOf(object[3])) ? null :String.valueOf(object[3]));
				
				Name name = new Name();
				name.setFirstName(StringUtils.contains("null", String.valueOf(object[4])) ? null :String.valueOf(object[4]));
				name.setLastName(StringUtils.contains("null", String.valueOf(object[5])) ? null :String.valueOf(object[5]));
				name.setMiddleName(StringUtils.contains("null", String.valueOf(object[6])) ? null :String.valueOf(object[6]));
				
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
	public InsuredPersonKeyFactorValue createKeyFactorValue(KeyFactor keyfactor, ProposalInsuredPerson insuredPerson) {
		
		InsuredPersonKeyFactorValue insuredPersonKeyFactorValue = new InsuredPersonKeyFactorValue();
		insuredPersonKeyFactorValue.setKeyFactor(keyfactor);
		insuredPersonKeyFactorValue.setProposalInsuredPerson(insuredPerson);
		
		if (KeyFactorChecker.isSumInsured(keyfactor)) {
			insuredPersonKeyFactorValue.setValue(insuredPerson.getApprovedSumInsured() + "");
		} else if (KeyFactorChecker.isAge(keyfactor) || KeyFactorChecker.isMedicalAge(keyfactor)) {
			insuredPersonKeyFactorValue.setValue(insuredPerson.getAgeForNextYear() + "");
		} else if (KeyFactorChecker.isTerm(keyfactor)) {
			insuredPersonKeyFactorValue.setValue(this.period + "");
		} else if (KeyFactorChecker.isPaymentType(keyfactor)) {
			insuredPersonKeyFactorValue.setValue(this.paymentTypeID + "");
		} else if (KeyFactorChecker.isRiskyOccupation(keyfactor)) {
			if (insuredPerson.getRiskyOccupation() == null) {
				insuredPersonKeyFactorValue.setValue(RiskyOccupation.NO + "");
			} else {
				insuredPersonKeyFactorValue.setValue(RiskyOccupation.YES + "");
			}
		} else if (KeyFactorChecker.isPound(keyfactor)) {
			insuredPersonKeyFactorValue.setValue(insuredPerson.getWeight() + "");
		} else if (KeyFactorChecker.isDangerousOccupation(keyfactor)) {
			insuredPersonKeyFactorValue.setValue(insuredPerson.getRiskyOccupation() == null ? 0 + ""
					: insuredPerson.getRiskyOccupation().getExtraRate() + "");
		}
		
		return insuredPersonKeyFactorValue;
	}

}
