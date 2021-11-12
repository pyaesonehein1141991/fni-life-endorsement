package org.ace.insurance.web.manage.travel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.IdConditionType;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.PeriodType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.common.SetUpIDConfig;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.UsageOfVehicle;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.province.service.interfaces.IProvinceService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.PolicyTraveller;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
//import org.ace.insurance.system.common.townshipCode.service.interfaces.ITownshipCodeService;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalInfo;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalKeyfactorValue;
import org.ace.insurance.travel.personTravel.proposal.ProposalPersonTravelVehicle;
import org.ace.insurance.travel.personTravel.proposal.ProposalTraveller;
import org.ace.insurance.travel.personTravel.proposal.ProposalTravellerBeneficiary;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ContractorType;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.SetUpIDChecker;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "AddNewPersonTravelProposalActionBean")
public class AddNewPersonTravelProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
	}

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{RelationShipService}")
	private IRelationShipService relationShipService;

	public void setRelationShipService(IRelationShipService relationShipService) {
		this.relationShipService = relationShipService;
	}

	@ManagedProperty(value = "#{CurrencyService}")
	private ICurrencyService currencyService;

	public void setCurrencyService(ICurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	@ManagedProperty(value = "#{ProvinceService}")
	private IProvinceService provinceService;

	public void setProvinceService(IProvinceService provinceService) {
		this.provinceService = provinceService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	private final String PROPOSAL_DIR = "/upload/personTravel-proposal/";
	private User user;
	private User responsiblePerson;
	private Boolean isConfirmEdit;
	private Boolean isEnquiryEdit;
	private Boolean isInsurancePersonEdit;
	private boolean createNewInsuredPersonInfo;
	private boolean crateNewBeneficiary;
	private boolean createNewBeneficiariesInfo;
	private boolean isNewProposalTraveller;
	private List<Product> productsList;
	private List<RelationShip> relationshipList;
	private List<String> stateCodeList;
	private List<String> townshipCodeList;
	private List<String> beneficiaryTownshipCodeList;
	private List<UsageOfVehicle> selectedUsages;
	private List<Currency> currencyList;
	private int days;
	private String userType;
	private String remark;
	private String temporyDir;
	private String travelProposalId;
	private PersonTravelProposal travelProposal;
	private PersonTravelProposalInfo travelInfo;
	private ProposalTraveller proposalTraveller;
	private ProposalTravellerBeneficiary beneficiary;

	private PersonTravelPolicy personTravelPolicy;

	private Map<String, ProposalTraveller> proposalTravellerMap;
	private Map<String, ProposalTravellerBeneficiary> beneficiaryMap;
	private Map<String, String> proposalUploadedFileMap;
	private List<PolicyTraveller> policyTravellerList;
	private ProposalTravellerBeneficiary beneficaryCloneDTO;

	private String flightNo;
	private String voyageNo;
	private String registrationNo;
	private String trainNo;

	private boolean isFlight;
	private boolean isVoyage;
	private boolean isVehicle;
	private boolean isTrain;
	private boolean isUnder100Travel;
	private boolean haveTownship;
	private boolean isNrcCustomer = true;
	private boolean isStillApplyCustomer = true;

	private String contractorType;

	@PreDestroy
	public void destroy() {
		removeParam("personTravelProposal");
		removeParam("isConfirmEdit");
		removeParam("isEnquiryEdit");
		removeParam("isInsurancePersonEdit");
	}

	@PostConstruct
	public void init() {
		/* Create temp directory for upload */
		user = (User) getParam(Constants.LOGIN_USER);
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";
		loadParam();
		createNewInstance();
		loadDatas();
		initializeInjection();
		destroy();
	}

	public void prepareAddNewBeneficiariesInfo() {
		createNewBeneficiariesInfo();
		if (beneficaryCloneDTO != null) {
			beneficiary.setTempId(System.nanoTime() + "");
			// beneficiary.setExistEntity(false);
			beneficiary.setAge(beneficaryCloneDTO.getAge());
			beneficiary.setPercentage(beneficaryCloneDTO.getPercentage());
			beneficiary.setInitialId(beneficaryCloneDTO.getInitialId());
			beneficiary.setFullIdNo(beneficaryCloneDTO.getFullIdNo());
			beneficiary.setGender(beneficaryCloneDTO.getGender());
			beneficiary.setPhone(beneficaryCloneDTO.getPhone());
			beneficiary.setIdType(beneficaryCloneDTO.getIdType());
			beneficiary.setStateCode(beneficaryCloneDTO.getStateCode());
			beneficiary.setTownshipCode(beneficaryCloneDTO.getTownshipCode());
			beneficiary.setIdNo(beneficaryCloneDTO.getIdNo());
			beneficiary.setIdConditionType(beneficaryCloneDTO.getIdConditionType());
			changeBeneficiaryStateCode();
			ResidentAddress address = new ResidentAddress();
			address.setTownship(beneficaryCloneDTO.getResidentAddress().getTownship());
			address.setResidentAddress(beneficaryCloneDTO.getResidentAddress().getResidentAddress());
			beneficiary.setResidentAddress(address);
			Name name = new Name();
			name.setFirstName(beneficaryCloneDTO.getName().getFirstName());
			name.setMiddleName(beneficaryCloneDTO.getName().getMiddleName());
			name.setLastName(beneficaryCloneDTO.getName().getLastName());
			beneficiary.setName(name);
			beneficiary.setRelationship(beneficaryCloneDTO.getRelationship());
			
		}
	}

	public void prepareEditBeneficiary(ProposalTravellerBeneficiary beneficiary) {
		this.beneficiary = new ProposalTravellerBeneficiary(beneficiary);
		this.beneficiary.loadTransientIdNo();
		if (this.beneficiary.getStateCode() != null) {
			changeBeneficiaryStateCode();
		}
		createNewBeneficiariesInfo = false;
	}

	public void removeBeneficiary(String removalTempId) {
		beneficiaryMap.remove(removalTempId);
		createNewBeneficiariesInfo();
	}

	public void saveBeneficiary() {
		beneficiary.setFullIdNo(setFullIdNo());
		beneficiaryMap.put(beneficiary.getTempId(), beneficiary);
		proposalTraveller.setProposalTravellerBeneficiaryList(new ArrayList<ProposalTravellerBeneficiary>(beneficiaryMap.values()));
		createNewBeneficiariesInfo();
		RequestContext.getCurrentInstance().execute("PF('beneficiariesInfoEntryDialog').hide()");
	}

	public String setFullIdNo() {
		String result = "";
		if (beneficiary.getIdType().equals(IdType.NRCNO)) {
			result = beneficiary.getStateCode() + "/" + beneficiary.getTownshipCode() + "(" + beneficiary.getIdConditionType() + ")" + beneficiary.getIdNo();
		} else if (beneficiary.getIdType().equals(IdType.FRCNO) || beneficiary.getIdType().equals(IdType.PASSPORTNO)) {
			result = beneficiary.getIdNo();
		}
		return result;
	}

	/** Proposal Traveller */
	public void prepareAddNewProposalTraveller() {
		createNewProposalTraveller();
	}

	public void prepareEditProposalTraveller(ProposalTraveller proposalTraveller) {
		isNewProposalTraveller = false;
		createNewInsuredPersonInfo = false;
		this.proposalTraveller = new ProposalTraveller(proposalTraveller);
		this.proposalTraveller.loadTransientIdNo();
		if (this.proposalTraveller.getStateCode() != null) {
			changeStateCode();
		}
		for (ProposalTravellerBeneficiary bene : proposalTraveller.getProposalTravellerBeneficiaryList()) {
			beneficiaryMap.put(bene.getTempId(), bene);
		}
	}

	public void removeProposalTraveller(String removalTempId) {
		proposalTravellerMap.remove(removalTempId);
		createNewProposalTraveller();
	}

	public boolean validInsuredPerson() {
		boolean valid = true;
		String formID = "personTravelProposalEntryForm";
		if (proposalTraveller.getName().isEmpty()) {
			addErrorMessage(formID + ":name", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (travelProposal.getProduct().isBaseOnKeyFactor()) {
			if (proposalTraveller.getUnit() < 1) {
				addErrorMessage(formID + ":unit", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if ((proposalTraveller.getUnit() * travelProposal.getProduct().getMinValue() > travelProposal.getProduct().getMaxValue())) {
				addErrorMessage(formID + ":unit", MessageId.INVALID_TRAVELLER_UINT);
				valid = false;
			}
		}
		if (proposalTraveller.getPhone().isEmpty()) {
			addErrorMessage(formID + ":phoneNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}

		if (beneficiaryMap.values().isEmpty()) {
			addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.REQUIRED_BENEFICIARY_PERSON);
			valid = false;
		} else {
			float totalPercent = 0.0f;
			for (ProposalTravellerBeneficiary beneficiary : beneficiaryMap.values()) {
				totalPercent += beneficiary.getPercentage();
			}
			if (totalPercent > 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.OVER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
			if (totalPercent < 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.LOWER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
		}

		if (isNewProposalTraveller && proposalTravellerMap.size() + 1 > travelInfo.getNoOfPassenger()) {
			addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.INVALID_PASSENGER);
			valid = false;
		}
		int totalUnit = 0;
		for (ProposalTraveller traveller : proposalTravellerMap.values()) {
			if (proposalTraveller.getTempId() != traveller.getTempId()) {
				totalUnit += traveller.getUnit();
			}
		}
		totalUnit += proposalTraveller.getUnit();
		if (travelInfo.getTotalUnit() < totalUnit) {
			addErrorMessage(formID + ":unit", MessageId.INVALID_UNIT);
			valid = false;
		}
		return valid;
	}

	public void saveProposalTraveller() {
		if (validInsuredPerson()) {
			beneficaryCloneDTO = proposalTraveller.getProposalTravellerBeneficiaryList().get(0);
			proposalTraveller.setFullIdNo();
			proposalTraveller.setProposalTravellerBeneficiaryList(new ArrayList<ProposalTravellerBeneficiary>(beneficiaryMap.values()));
			proposalTravellerMap.put(proposalTraveller.getTempId(), proposalTraveller);
			prepareAddNewProposalTraveller();
			createNewBeneficiaryMap();
		}
	}

	public void addNewProposalTraveller() {
		isNewProposalTraveller = true;
		saveProposalTraveller();
	}

	public void updateProposalTraveller() {
		isNewProposalTraveller = false;
		saveProposalTraveller();
	}

	/** PersonTravel Proposal */
	public PersonTravelProposal calculatePremium() {
		try {
			travelInfo.loadKeyFactor(travelProposal.getProduct());
			setKeyfactorValue();
			travelInfo.setProposalPersonTravelVehicleList(getVehicleList());
			travelInfo.setProposalTravellerList(getProposalTravellerList());
			travelProposal.setPersonTravelInfo(travelInfo);
			travelProposal = personTravelProposalService.calculatePremium(travelProposal);
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return travelProposal;
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		String formID = "personTravelProposalEntryForm";

		if ("TravelInfo".equals(event.getOldStep()) && "insuredPersonInfo".equals(event.getNewStep())) {
			Product product = travelProposal.getProduct();
			int maxTerm = product.getMaxTerm();
			int minTerm = product.getMinTerm();
			int maxValidDays = 0;
			PeriodType maxTermType = product.getMaxTermType();
			Calendar cal = Calendar.getInstance();
			if (maxTermType.equals(PeriodType.MONTH)) {
				cal.add(Calendar.MONTH, maxTerm);
				maxValidDays = travelDaysBaseOnMonthEndDate(new Date(), cal.getTime(), false);
			} else if (maxTermType.equals(PeriodType.DAY)) {
				maxValidDays = maxTerm;
			}
			days = travelDaysBaseOnMonthEndDate(travelInfo.getDepartureDate(), travelInfo.getArrivalDate(), false);
			if ((maxValidDays != 0 && days > maxValidDays) || days < minTerm) {
				addErrorMessage(formID + ":arrivalDate", MessageId.INVALID_START_END_DATE);
				valid = false;
			}
			if (travelProposal.getProduct().isBaseOnKeyFactor() && travelInfo.getTotalUnit() < travelInfo.getNoOfPassenger()) {
				addErrorMessage(formID + ":noOfUnit", MessageId.INVALID_TOTALUNIT);
				valid = false;
			}
			if(!travelProposal.getProduct().getProductContent().getName().equals("TRAVEL INSURACE")) {
			if (travelInfo.getTotalUnit() > (travelInfo.getNoOfPassenger() * product.getMaxValue())) {
				addErrorMessage(formID + ":noOfUnit", MessageId.OVER_TOTAL_UNIT);
				valid = false;
			 }
			}
		}
		if ("insuredPersonInfo".equals(event.getOldStep()) && "premiumCalculation".equals(event.getNewStep())) {
			double totalUnit = 0;
			for(ProposalTraveller proposal : proposalTravellerMap.values()) {
				totalUnit += proposal.getUnit();
			}
			if (isInsurancePersonEdit) {
				if (getProposalTravellerList().size() > 0) {
					for (ProposalTraveller traveller : getProposalTravellerList()) {
						totalUnit = totalUnit + traveller.getUnit();
					}
				}
				//totalUnit += proposalTraveller.getUnit();
				if (totalUnit < travelInfo.getTotalUnit()) {
					addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.INVALID_PERSONTRAVELLER_UNIT);
					valid = false;
				}
			}
			if(!travelProposal.getProduct().getProductContent().getName().equals("UNDER 100 MILES TRAVEL INSURANCE")) {
			 //proposalTraveller.getUnit();
			if (totalUnit < travelInfo.getTotalUnit()) {
				addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.INVALID_PERSONTRAVELLER_UNIT);
				valid = false;
			   }
			}
			if (valid) {
				calculatePremium();
			}
		}
		return valid ? event.getNewStep() : event.getOldStep();
	}

	public String addNewPersonTravelProposal() {
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), travelProposal.getBranch().getId(), remark, WorkflowTask.CONFIRMATION, ReferenceType.TRAVEL,
					TransactionType.UNDERWRITING, user, responsiblePerson);
			loadAttachment();
			if (isConfirmEdit) {
				personTravelProposalService.updatePersonTravelProposal(travelProposal, workFlowDTO);
			} else if (isEnquiryEdit) {
				personTravelProposalService.updatePersonTravelProposal(travelProposal, null);
			} else if (isInsurancePersonEdit) {
				personTravelProposalService.updatePersonTravelProposal(travelProposal, null);
				if (personTravelPolicy != null) {
					policyTravellerList = new ArrayList<>();
					if (personTravelPolicy.getPersonTravelProposal().getId().equals(travelProposal.getId())) {
						for (ProposalTraveller t : travelProposal.getPersonTravelInfo().getProposalTravellerList()) {
							policyTravellerList.add(new PolicyTraveller(t));
						}
						personTravelPolicy.getPersonTravelPolicyInfo().setPolicyTravellerList(policyTravellerList);
						personTravelPolicyService.updatePersonTravelPolicy(personTravelPolicy);
					}
				}
			} else {
				personTravelProposalService.addNewPersonTravelProposal(travelProposal, workFlowDTO);
			}
			if (travelProposalId != null) {
				moveUploadedFiles();
			}
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
			createNewPersonTravelProposal();
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	/** handle Attachment */
	public void handleProposalAttachment(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		String fileName = uploadedFile.getFileName().replaceAll("\\s", "_");
		travelProposalId = travelInfo.getTempId();
		if (!proposalUploadedFileMap.containsKey(fileName)) {
			String filePath = temporyDir + travelProposalId + "/" + fileName;
			proposalUploadedFileMap.put(fileName, filePath);
			createFile(new File(getUploadPath() + filePath), uploadedFile.getContents());
		}
	}

	public void removeProposalUploadedFile(String filePath) {
		try {
			String fileName = getFileName(filePath);
			proposalUploadedFileMap.remove(fileName);
			FileHandler.forceDelete(new File(getUploadPath() + filePath));
			if (proposalUploadedFileMap.isEmpty()) {
				FileHandler.forceDelete(new File(getUploadPath() + temporyDir));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** events */
	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (contractorType.equals(ContractorType.CUSTOMER.toString())) {
			travelProposal.setOrganization(null);
		} else if (contractorType.equals(ContractorType.ORGANIZATION.toString())) {
			travelProposal.setCustomer(null);
		}
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		travelProposal.setAgent(null);
	}

	public void changeStateCode() {
		townshipCodeList = townshipService.findTspShortNameByProvinceNo(proposalTraveller.getStateCode());
	}

	public void changeIdType(AjaxBehaviorEvent e) {
		IdType idType = (IdType) ((UIOutput) e.getSource()).getValue();
		if (idType.equals(IdType.NRCNO)) {
			isNrcCustomer = true;
			isStillApplyCustomer = true;
		} else if (idType.equals(IdType.STILL_APPLYING)) {
			haveTownship = false;
			isNrcCustomer = false;
			isStillApplyCustomer = false;
		} else {
			haveTownship = false;
			isNrcCustomer = false;
			isStillApplyCustomer = true;
		}
	}

	public void changeUsageOfVehicle(AjaxBehaviorEvent event) {
		isFlight = false;
		isVoyage = false;
		isVehicle = false;
		isTrain = false;
		for (UsageOfVehicle usage : selectedUsages) {
			switch (usage) {
				case FLIGHT:
					isFlight = true;
					break;
				case VOYAGE:
					isVoyage = true;
					break;
				case VEHICLE:
					isVehicle = true;
					break;
				case TRAIN:
					isTrain = true;
					break;
				default:
					break;
			}
		}
	}

	public void productChange(AjaxBehaviorEvent event) {
		if (travelProposal.getProduct() != null) {
			travelProposal.setCurrency(travelProposal.getProduct().getCurrency());
			travelProposal.setPaymentType(null);
			isUnder100Travel = SetUpIDConfig.isUnder100MileTravelInsurance(travelProposal.getProduct());
			if (isUnder100Travel) {
				travelInfo.setTotalUnit(0);
				proposalTraveller.setUnit(0);
			}
		}
	}

	public void changeBeneficiaryStateCode() {
		beneficiaryTownshipCodeList = townshipService.findTspShortNameByProvinceNo(beneficiary.getStateCode());
	}

	/**
	 * Calculate number of days between start date and end date according to
	 * month end day.
	 * 
	 * @param startDate
	 * @param endDate
	 * @param startDayInclude
	 * @return number of days between start date and end date
	 */
	public IdType[] getIdTypeSelectItemList() {
		return IdType.values();
	}

	public UsageOfVehicle[] getUsageOfVehicleList() {
		return UsageOfVehicle.values();
	}

	public void returnBeneficiariesTownShip(SelectEvent event) {
		Township township = (Township) event.getObject();
		beneficiary.getResidentAddress().setTownship(township);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		travelProposal.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		travelProposal.setOrganization(organization);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		travelProposal.setPaymentType(paymentType);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		travelProposal.setAgent(agent);
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		proposalTraveller.setTownship(township);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnExpress(SelectEvent event) {
		Express express = (Express) event.getObject();
		this.travelInfo.setExpress(express);
	}

	public String updateProposal() {
		String result = null;
		try {
			loadAttachment();
			travelInfo.setProposalTravellerList(new ArrayList<ProposalTraveller>(proposalTravellerMap.values()));
			travelProposal.setPersonTravelInfo(travelInfo);
			personTravelProposalService.updatePersonTravelProposal(travelProposal, null);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void selectBeneficiariesTownship() {
		selectTownship();
	}

	public void selectPaymentType() {
		selectPaymentType(travelProposal.getProduct());
	}

	public void selectUser() {
		selectUser(WorkflowTask.CONFIRMATION, WorkFlowType.TRAVEL, TransactionType.UNDERWRITING, travelProposal.getBranch().getId());
	}

	public List<ProposalTraveller> getProposalTravellerList() {
		return new ArrayList<ProposalTraveller>(proposalTravellerMap.values());
	}

	public List<ProposalTravellerBeneficiary> getBeneficiaryList() {
		return new ArrayList<ProposalTravellerBeneficiary>(beneficiaryMap.values());
	}

	public boolean isNewProposalTraveller() {
		return isNewProposalTraveller;
	}

	public void setNewProposalTraveller(boolean isNewProposalTraveller) {
		this.isNewProposalTraveller = isNewProposalTraveller;
	}

	public boolean isCreateNewBeneficiariesInfo() {
		return createNewBeneficiariesInfo;
	}

	public List<String> getTownshipCodeList() {
		return townshipCodeList;
	}

	public List<String> getStateCodeList() {
		return stateCodeList;
	}

	public List<String> getBeneficiaryTownshipCodeList() {
		return beneficiaryTownshipCodeList;
	}

	public IdConditionType[] getIdConditionTypeSelectItemList() {
		return IdConditionType.values();
	}

	public boolean getIsUnder100Travel() {
		return isUnder100Travel;
	}

	/**
	 * @return the flightNo
	 */
	public String getFlightNo() {
		return flightNo;
	}

	/**
	 * @param flightNo
	 *            the flightNo to set
	 */
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	/**
	 * @return the voyageNo
	 */
	public String getVoyageNo() {
		return voyageNo;
	}

	/**
	 * @param voyageNo
	 *            the voyageNo to set
	 */
	public void setVoyageNo(String voyageNo) {
		this.voyageNo = voyageNo;
	}

	/**
	 * @return the registrationNo
	 */
	public String getRegistrationNo() {
		return registrationNo;
	}

	/**
	 * @param registrationNo
	 *            the registrationNo to set
	 */
	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	/**
	 * @return the trainNo
	 */
	public String getTrainNo() {
		return trainNo;
	}

	/**
	 * @param trainNo
	 *            the trainNo to set
	 */
	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

	/**
	 * @param trainNo
	 *            the trainNo to set
	 */
	public void setTrain(boolean isTrain) {
		this.isTrain = isTrain;
	}

	public boolean isTrain() {
		return isTrain;
	}

	/**
	 * @return the isFlight
	 */
	public boolean isFlight() {
		return isFlight;
	}

	/**
	 * @param isFlight
	 *            the isFlight to set
	 */
	public void setFlight(boolean isFlight) {
		this.isFlight = isFlight;
	}

	/**
	 * @return the isVoyage
	 */
	public boolean isVoyage() {
		return isVoyage;
	}

	/**
	 * @param isVoyage
	 *            the isVoyage to set
	 */
	public void setVoyage(boolean isVoyage) {
		this.isVoyage = isVoyage;
	}

	/**
	 * @return the isVehicle
	 */
	public boolean isVehicle() {
		return isVehicle;
	}

	/**
	 * @param isVehicle
	 *            the isVehicle to set
	 */
	public void setVehicle(boolean isVehicle) {
		this.isVehicle = isVehicle;
	}

	/**
	 * @return the selectedUsages
	 */
	public List<UsageOfVehicle> getSelectedUsages() {
		return selectedUsages;
	}

	/**
	 * @param selectedUsages
	 *            the selectedUsages to set
	 */
	public void setSelectedUsages(List<UsageOfVehicle> selectedUsages) {
		this.selectedUsages = selectedUsages;
	}

	public IdType[] getIdTypes() {
		return IdType.values();
	}

	public Gender[] getGender() {
		return Gender.values();
	}

	public boolean isHaveTownship() {
		return haveTownship;
	}

	public void setHaveTownship(boolean haveTownship) {
		this.haveTownship = haveTownship;
	}

	public boolean isNrcCustomer() {
		return isNrcCustomer;
	}

	public void setNrcCustomer(boolean isNrcCustomer) {
		this.isNrcCustomer = isNrcCustomer;
	}

	public boolean isStillApplyCustomer() {
		return isStillApplyCustomer;
	}

	public void setStillApplyCustomer(boolean isStillApplyCustomer) {
		this.isStillApplyCustomer = isStillApplyCustomer;
	}

	public List<String> getProposalUploadedFileList() {
		return new ArrayList<String>(proposalUploadedFileMap.values());
	}

	public String getUserType() {
		if (userType == null) {
			userType = SaleChannelType.AGENT.toString();
		}
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public PersonTravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(PersonTravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public PersonTravelProposalInfo getTravelInfo() {
		return travelInfo;
	}

	public void setTravelInfo(PersonTravelProposalInfo travelInfo) {
		this.travelInfo = travelInfo;
	}

	public ProposalTraveller getProposalTraveller() {
		return proposalTraveller;
	}

	public void setProposalTraveller(ProposalTraveller proposalTraveller) {
		this.proposalTraveller = proposalTraveller;
	}

	public ProposalTravellerBeneficiary getBeneficiary() {
		return beneficiary;
	}

	public void setBeneficiary(ProposalTravellerBeneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}

	public Boolean getIsConfirmEdit() {
		return isConfirmEdit;
	}

	public Boolean getIsEnquiryEdit() {
		return isEnquiryEdit;
	}

	public Boolean getIsInsurancePersonEdit() {
		return isInsurancePersonEdit;
	}

	public boolean isCreateNewInsuredPersonInfo() {
		return createNewInsuredPersonInfo;
	}

	public boolean isCrateNewBeneficiary() {
		return crateNewBeneficiary;
	}

	public String getContractorType() {
		if (travelProposal.getOrganization() != null) {
			contractorType = ContractorType.ORGANIZATION.toString();
		} else if (travelProposal.getCustomer() != null) {
			contractorType = ContractorType.CUSTOMER.toString();
		}
		return contractorType;
	}

	public void setContractorType(String contractorType) {
		this.contractorType = contractorType;
	}

	public SaleChannelType[] getSaleChannel() {
		SaleChannelType[] types = { SaleChannelType.AGENT, SaleChannelType.WALKIN, SaleChannelType.DIRECTMARKETING };
		return types;
	}

	public List<Product> getProductsList() {
		return productsList;
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	public List<Currency> getCurrencyList() {
		return currencyList;
	}

	private void loadParam() {
		travelProposal = ((PersonTravelProposal) getParam("personTravelProposal") != null) ? (PersonTravelProposal) getParam("personTravelProposal") : travelProposal;
		isEnquiryEdit = (Boolean) getParam("isEnquiryEdit") == null ? false : true;
		isInsurancePersonEdit = (Boolean) getParam("isInsurancePersonEdit") == null ? false : true;
		isConfirmEdit = isEnquiryEdit ? false : isInsurancePersonEdit ? false : travelProposal != null ? true : false;
	}

	private void initializeInjection() {
		if (isInsurancePersonEdit) {
			personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyByProposalId(travelProposal.getId());
		}

		if (travelProposal != null) {
			isUnder100Travel = SetUpIDConfig.isUnder100MileTravelInsurance(travelProposal.getProduct());

			/** Load Travel Info */
			travelInfo = travelProposal.getPersonTravelInfo();
			checkUsageOfVehicle();

			/** Load Attachments */
			for (Attachment attach : travelInfo.getAttachmentList()) {
				proposalUploadedFileMap.put(attach.getName(), attach.getFilePath());
			}

			/** Load ProposalTravellers */
			for (ProposalTraveller traveller : travelInfo.getProposalTravellerList()) {
				proposalTravellerMap.put(traveller.getTempId(), traveller);
			}
		} else {
			createNewPersonTravelProposal();
		}
	}

	private void loadDatas() {
		productsList = productService.findProductByInsuranceType(InsuranceType.PERSON_TRAVEL);
		relationshipList = relationShipService.findAllRelationShip();
		stateCodeList = provinceService.findAllProvinceNo();
		currencyList = new ArrayList<Currency>();
		currencyList.add(currencyService.findCurrencyById(SetUpIDConfig.getKYTCurrencyId()));
	}

	private void createNewInstance() {
		proposalUploadedFileMap = new HashMap<String, String>();
		createNewProposalTravellerMap();
		createNewProposalTraveller();
		townshipCodeList = new ArrayList<String>();
		beneficiaryTownshipCodeList = new ArrayList<String>();
	}

	private void createNewPersonTravelProposal() {
		travelProposal = new PersonTravelProposal();
		travelProposal.setSubmittedDate(new Date());
		travelProposal.setBranch(user.getLoginBranch());
		travelProposal.setCurrency(currencyList.get(0));
		travelInfo = new PersonTravelProposalInfo();
		initializeUsageOfVehicle();
	}

	private void initializeUsageOfVehicle() {
		flightNo = "";
		voyageNo = "";
		registrationNo = "";
		trainNo = "";
		isFlight = false;
		isVoyage = false;
		isVehicle = false;
		selectedUsages = new ArrayList<>();
	}

	private void createNewProposalTravellerMap() {
		proposalTravellerMap = new HashMap<String, ProposalTraveller>();
	}

	private void checkUsageOfVehicle() {
		selectedUsages = new ArrayList<>();
		for (ProposalPersonTravelVehicle vehicle : travelInfo.getProposalPersonTravelVehicleList()) {
			switch (vehicle.getUsageOfVehicle()) {
				case FLIGHT:
					isFlight = true;
					flightNo = vehicle.getRegistrationNo();
					break;
				case VOYAGE:
					isVoyage = true;
					voyageNo = vehicle.getRegistrationNo();
					break;
				case VEHICLE:
					isVehicle = true;
					registrationNo = vehicle.getRegistrationNo();
					break;
				case TRAIN:
					isTrain = true;
					trainNo = vehicle.getRegistrationNo();
					break;
				default:
					break;
			}
			selectedUsages.add(vehicle.getUsageOfVehicle());
		}
	}

	/** Beneficiary */
	private void createNewBeneficiaryMap() {
		beneficiaryMap = new HashMap<String, ProposalTravellerBeneficiary>();
	}

	private void createNewBeneficiariesInfo() {
		createNewBeneficiariesInfo = true;
		beneficiaryTownshipCodeList = new ArrayList<>();
		beneficiary = new ProposalTravellerBeneficiary();
	}

	private void createNewProposalTraveller() {

		createNewInsuredPersonInfo = true;
		proposalTraveller = new ProposalTraveller();
		createNewBeneficiaryMap();
		createNewBeneficiariesInfo();
		townshipCodeList = new ArrayList<>();
	}

	private void setKeyfactorValue() {
		for (PersonTravelProposalKeyfactorValue tkf : travelInfo.getTravelProposalKeyfactorValueList()) {
			if (SetUpIDChecker.isTravelDay(tkf.getKeyfactor()))
				tkf.setValue(days + "");
		}
	}

	private List<ProposalPersonTravelVehicle> getVehicleList() {
		List<ProposalPersonTravelVehicle> vehicleList = new ArrayList<>();
		ProposalPersonTravelVehicle vehicle;
		for (UsageOfVehicle usage : selectedUsages) {
			vehicle = new ProposalPersonTravelVehicle();
			switch (usage) {
				case FLIGHT:
					vehicle.setRegistrationNo(flightNo);
					break;
				case VOYAGE:
					vehicle.setRegistrationNo(voyageNo);
					break;
				case VEHICLE:
					vehicle.setRegistrationNo(registrationNo);
					break;
				case TRAIN:
					vehicle.setRegistrationNo(trainNo);
					break;
				default:
					break;
			}
			vehicle.setUsageOfVehicle(usage);
			vehicleList.add(vehicle);
		}
		return vehicleList;
	}

	private void moveUploadedFiles() {
		try {
			FileHandler.moveFiles(getUploadPath(), temporyDir + travelProposalId, PROPOSAL_DIR + travelProposalId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadAttachment() {
		for (String fileName : proposalUploadedFileMap.keySet()) {
			String filePath = PROPOSAL_DIR + travelProposalId + "/" + fileName;
			travelInfo.addAttachment(new Attachment(fileName, filePath));
		}
	}

	private int travelDaysBaseOnMonthEndDate(Date startDate, Date endDate, boolean startDayInclude) {
		DateTime start = new DateTime(startDate);
		DateTime end = new DateTime(endDate);
		Period period = new Period(start, end);
		int month = period.getMonths();
		int week = period.getWeeks();
		int day = period.getDays();
		int totalDays = (month * 30) + (week * 7) + day;
		if ((month == 0 && week == 0) || (day == 0 && month == 0)) {
			totalDays += 1;
		}
		return totalDays;
	}
}
