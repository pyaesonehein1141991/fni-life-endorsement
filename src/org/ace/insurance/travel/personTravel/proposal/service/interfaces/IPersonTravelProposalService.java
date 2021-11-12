package org.ace.insurance.travel.personTravel.proposal.service.interfaces;

import java.util.List;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.proposal.PTPL001;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.SystemException;

public interface IPersonTravelProposalService {

	public void addNewPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO);

	public void updatePersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO);

	public void deletePersonTravelProposal(PersonTravelProposal personTravelProposal);

	public List<PersonTravelProposal> findAllPersonTravelProposal();

	public PersonTravelProposal findPersonTravelProposalById(String id);

	public List<Payment> confirmPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO) throws SystemException;

	public void rejectPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO) throws SystemException;

	public void paymentPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, List<Payment> payment, Branch userBranch);

	public List<PTPL001> findPersonTravelDTOByCriteria(EnquiryCriteria criteria);

	public void issuePersonTravelProposal(PersonTravelProposal personTravelProposal);

	public PersonTravelProposal calculatePremium(PersonTravelProposal travelProposal);

	// public ProposalTraveller findProposalTravellerById(String id);
	public void deletePayment(PersonTravelPolicy personTravelPolicy, WorkFlowDTO workFlowDTO);
}
