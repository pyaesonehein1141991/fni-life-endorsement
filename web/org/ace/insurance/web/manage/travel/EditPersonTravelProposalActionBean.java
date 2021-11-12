package org.ace.insurance.web.manage.travel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.SetUpIDConfig;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.UsageOfVehicle;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.city.service.interfaces.ICityService;
import org.ace.insurance.system.common.country.service.interfaces.ICountryService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.organization.service.interfaces.IOrganizationService;
import org.ace.insurance.system.common.relationship.RelationShip;
import org.ace.insurance.system.common.relationship.service.interfaces.IRelationShipService;
import org.ace.insurance.system.common.township.Township;
import org.ace.insurance.system.common.township.service.interfaces.ITownshipService;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalInfo;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalKeyfactorValue;
import org.ace.insurance.travel.personTravel.proposal.ProposalTraveller;
import org.ace.insurance.travel.personTravel.proposal.ProposalTravellerBeneficiary;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

@ViewScoped
@ManagedBean(name = "EditPersonTravelProposalActionBean")
public class EditPersonTravelProposalActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	IPersonTravelProposalService personTravelProposalService;

	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
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

	@ManagedProperty(value = "#{OrganizationService}")
	private IOrganizationService organizationService;

	public void setOrganizationService(IOrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@ManagedProperty(value = "#{CityService}")
	private ICityService cityService;

	public void setCityService(ICityService cityService) {
		this.cityService = cityService;
	}

	@ManagedProperty(value = "#{TownshipService}")
	private ITownshipService townshipService;

	public void setTownshipService(ITownshipService townshipService) {
		this.townshipService = townshipService;
	}

	@ManagedProperty(value = "#{CountryService}")
	private ICountryService countryService;

	public void setCountryService(ICountryService countryService) {
		this.countryService = countryService;
	}

	private boolean createNewInsuredPersonInfo;
	private boolean crateNewBeneficiary;
	private String userType;
	private String remark;
	private final String PROPOSAL_DIR = "/upload/personTravel-proposal/";
	private String temporyDir;
	private String travelProposalId;
	private User user;
	private User responsiblePerson;
	private PersonTravelProposal travelProposal;
	private PersonTravelProposalInfo travelInfo;
	private ProposalTraveller proposalTraveller;
	private ProposalTravellerBeneficiary beneficiary;
	private List<Product> productsList;
	private List<RelationShip> relationshipList;
	private Map<String, ProposalTraveller> proposalTravellerMap;
	private Map<String, ProposalTravellerBeneficiary> beneficiaryMap;
	private Map<String, String> proposalUploadedFileMap;

	private boolean organization;
	private boolean isUnder100Travel;
	private boolean isLocalTravel;
	private boolean isForeignTravel;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (travelProposal == null) ? (PersonTravelProposal) getParam("personTravel") : travelProposal;
	}

	@PreDestroy
	public void destroy() {
		removeParam("personTravelProposal");
	}

	@PostConstruct
	public void init() {
		/* Create temp directory for upload */
		temporyDir = "/temp/" + System.currentTimeMillis() + "/";
		relationshipList = relationShipService.findAllRelationShip();
		proposalUploadedFileMap = new HashMap<String, String>();
		initializeInjection();
		if (travelProposal.getCustomer() == null) {
			organization = true;
		}
		isLocalTravel = SetUpIDConfig.isLocalTravelInsurance(travelProposal.getProduct());
		isUnder100Travel = SetUpIDConfig.isUnder100MileTravelInsurance(travelProposal.getProduct());
		isForeignTravel = SetUpIDConfig.isForeignTravelInsurance(travelProposal.getProduct());
		travelInfo = travelProposal.getPersonTravelInfo();
		proposalTravellerMap = new HashMap<String, ProposalTraveller>();
		if (!travelInfo.getProposalTravellerList().isEmpty()) {
			for (ProposalTraveller traveller : travelInfo.getProposalTravellerList()) {
				proposalTravellerMap.put(traveller.getTempId(), traveller);
			}
		}
		if (!travelInfo.getAttachmentList().isEmpty()) {
			for (Attachment attach : travelInfo.getAttachmentList()) {
				proposalUploadedFileMap.put(attach.getName(), attach.getFilePath());
			}
		}
		createNewProposalTraveller();
		organization = travelProposal.getCustomer() == null ? true : false;
	}

	public void createNewProposalTraveller() {
		createNewInsuredPersonInfo = true;
		proposalTraveller = new ProposalTraveller();
		createNewBeneficiary();
		createNewBeneficiaryMap();
	}

	public void createNewProposalTravellerMap() {
		proposalTravellerMap = new HashMap<String, ProposalTraveller>();
	}

	public void createNewBeneficiary() {
		beneficiary = new ProposalTravellerBeneficiary();
	}

	public void createNewBeneficiaryMap() {
		beneficiaryMap = new HashMap<String, ProposalTravellerBeneficiary>();
	}

	public Map<String, ProposalTraveller> getProposalTravellerMap() {
		return proposalTravellerMap;
	}

	public Map<String, ProposalTravellerBeneficiary> getBeneficiaryMap() {
		return beneficiaryMap;
	}

	public void saveBeneficiary() {
		beneficiaryMap.put(beneficiary.getTempId(), beneficiary);
		createNewBeneficiary();
		RequestContext.getCurrentInstance().execute("PF('beneficiariesInfoEntryDialog').hide()");
	}

	public void prepareEditBeneficiary(ProposalTravellerBeneficiary beneficiary) {
		this.beneficiary = new ProposalTravellerBeneficiary(beneficiary);

	}

	public void removeBeneficiary(String removalTempId) {
		if (beneficiary.getTempId().equals(removalTempId)) {
			createNewBeneficiary();
		}
		beneficiaryMap.remove(removalTempId);
	}

	public void saveProposalTraveller() {
		if (validInsuredPerson()) {
			proposalTraveller.setProposalTravellerBeneficiaryList(new ArrayList<ProposalTravellerBeneficiary>(beneficiaryMap.values()));
			proposalTravellerMap.put(proposalTraveller.getTempId(), proposalTraveller);
			createNewProposalTraveller();
			createNewBeneficiaryMap();
		}

	}

	public boolean validInsuredPerson() {
		boolean valid = true;
		String formID = "personTravelProposalEditForm";
		if (proposalTraveller.getName().isEmpty()) {
			addErrorMessage(formID + ":name", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (proposalTraveller.getFullIdNo().isEmpty()) {
			addErrorMessage(formID + ":travellerIdNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (travelProposal.getProduct().isBaseOnKeyFactor()) {
			if (proposalTraveller.getUnit() < 1) {
				addErrorMessage(formID + ":unit", UIInput.REQUIRED_MESSAGE_ID);
				valid = false;
			}
			if ((proposalTraveller.getUnit() * travelProposal.getProduct().getMinValue() > travelProposal.getProduct().getMinValue())) {
				addErrorMessage(formID + ":unit", MessageId.INVALID_TRAVELLER_UINT);
				valid = false;
			}
		}
		if (proposalTraveller.getPhone().isEmpty()) {
			addErrorMessage(formID + ":phoneNo", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (!beneficiaryMap.values().isEmpty()) {
			Double totalPercentage = 0.0;
			for (ProposalTravellerBeneficiary b : beneficiaryMap.values()) {
				totalPercentage += b.getPercentage();
			}
			if (totalPercentage > 100) {
				addErrorMessage(formID + ":beneficiariesInfoTablePanel", MessageId.OVER_BENEFICIARY_PERCENTAGE);
				valid = false;
			}
		}
		return valid;

	}

	public void prepareAddNewProposalTraveller() {
		createNewProposalTraveller();
		createNewBeneficiaryMap();
	}

	public void prepareEditProposalTraveller(ProposalTraveller proposalTraveller) {
		this.createNewInsuredPersonInfo = false;
		this.proposalTraveller = new ProposalTraveller(proposalTraveller);
		for (ProposalTravellerBeneficiary beneficiary : proposalTraveller.getProposalTravellerBeneficiaryList()) {
			beneficiaryMap.put(beneficiary.getTempId(), beneficiary);
		}
	}

	public void removeProposalTraveller(String removalTempId) {
		proposalTravellerMap.remove(removalTempId);
		createNewProposalTraveller();
	}

	public void setKeyfactor(int days) {
		for (PersonTravelProposalKeyfactorValue tkf : travelInfo.getTravelProposalKeyfactorValueList()) {
			tkf.setValue(days + "");
		}

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

	public boolean isCreateNewInsuredPersonInfo() {
		return createNewInsuredPersonInfo;
	}

	public boolean isCrateNewBeneficiary() {
		return crateNewBeneficiary;
	}

	public boolean isOrganization() {
		return organization;
	}

	public void setOrganization(boolean organization) {
		this.organization = organization;
	}

	public void changeOrgEvent(AjaxBehaviorEvent event) {
		if (organization) {
			travelProposal.setCustomer(null);
			// insuredPersonInfoDTO = new InsuredPersonInfoDTO();
		} else {
			travelProposal.setOrganization(null);
		}
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		if (userType.equals(SaleChannelType.AGENT.toString())) {
		}
	}

	public String onFlowProcess(FlowEvent event) {
		boolean valid = true;
		String formID = "personTravelProposalEditForm";

		if ("insuredPersonInfo".equals(event.getOldStep()) && "workflow".equals(event.getNewStep())) {
			double totalUnit = 0;
			if (getProposalTravellerList().size() > 0) {
				for (ProposalTraveller traveller : getProposalTravellerList()) {
					totalUnit = totalUnit + traveller.getUnit();
				}
				if (totalUnit > travelInfo.getTotalUnit()) {
					addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.INVALID_UNIT);
					valid = false;
				}
				if (getProposalTravellerList().size() > travelInfo.getNoOfPassenger()) {
					addErrorMessage(formID + ":insuredPersonInfoTable", MessageId.INVALID_PASSENGER);
					valid = false;
				}
			}

		}

		return valid ? event.getNewStep() : event.getOldStep();
	}

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

	private void loadAttachment() {
		for (String fileName : proposalUploadedFileMap.keySet()) {
			String filePath = PROPOSAL_DIR + travelProposalId + "/" + fileName;
			travelInfo.addAttachment(new Attachment(fileName, filePath));
		}

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

	public boolean getIsLocalTravel() {
		return isLocalTravel;
	}

	public boolean getIsForeignTravel() {
		return isForeignTravel;
	}

	public boolean getIsUnder100Travel() {
		return isUnder100Travel;
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

	public UsageOfVehicle[] getUsageOfVehicleList() {
		return UsageOfVehicle.values();
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		travelProposal.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		travelProposal.setOrganization(organization);
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		travelProposal.setAgent(agent);
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		travelProposal.setBranch(branch);
	}

	public void returnTownship(SelectEvent event) {
		Township township = (Township) event.getObject();
		proposalTraveller.setTownship(township);
	}

	public void returnBeneficiariesTownShip(SelectEvent event) {
		Township township = (Township) event.getObject();
		beneficiary.setTownship(township);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnExpress(SelectEvent event) {
		Express express = (Express) event.getObject();
		this.travelInfo.setExpress(express);
	}

	public void selectUser() {
		selectUser(WorkflowTask.CONFIRMATION, WorkFlowType.TRAVEL, TransactionType.UNDERWRITING, travelProposal.getBranch().getId());
	}

	public List<Product> getProductsList() {
		return productsList;
	}

	public List<RelationShip> getRelationshipList() {
		return relationshipList;
	}

	public List<ProposalTraveller> getProposalTravellerList() {
		return new ArrayList<ProposalTraveller>(proposalTravellerMap.values());
	}

	public List<ProposalTravellerBeneficiary> getBeneficiaryList() {
		return new ArrayList<ProposalTravellerBeneficiary>(beneficiaryMap.values());
	}

	private String getLoginBranchId() {
		return user.getLoginBranch().getId();
	}
}
