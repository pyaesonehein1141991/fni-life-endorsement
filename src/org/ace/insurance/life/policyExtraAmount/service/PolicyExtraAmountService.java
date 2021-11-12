package org.ace.insurance.life.policyExtraAmount.service;



	import java.util.List;

	import javax.annotation.Resource;

	import org.ace.insurance.accept.AcceptedInfo;
	import org.ace.insurance.accept.persistence.interfaces.IAcceptedInfoDAO;
	import org.ace.insurance.accept.service.interfaces.IAcceptedInfoService;
	import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.life.policyExtraAmount.PolicyExtraAmount;
import org.ace.insurance.life.policyExtraAmount.persistence.interfaces.IPolicyExtraAmountDAO;
import org.ace.insurance.life.policyExtraAmount.service.interfaces.IPolicyExtraAmountService;
import org.ace.java.component.SystemException;
	import org.ace.java.component.persistence.exception.DAOException;
	import org.ace.java.component.service.BaseService;
	import org.springframework.stereotype.Service;

	@Service(value = "PolicyExtraAmountService")
	public class PolicyExtraAmountService extends BaseService implements IPolicyExtraAmountService {

		@Resource(name = "PolicyExtraAmountDAO")
		private IPolicyExtraAmountDAO policyExtraAmountDAO;

		public void addNewPolicyExtraAmountInfo(PolicyExtraAmount policyExtraAmount) {
			try {
				policyExtraAmountDAO.insert(policyExtraAmount);
			} catch (DAOException e) {
				throw new SystemException(e.getErrorCode(), "Faield to add a new PolicyExtraAmount", e);
			}
		}

		public void updatePolicyExtraAmountInfo(PolicyExtraAmount policyExtraAmount) {
			try {
				policyExtraAmountDAO.update(policyExtraAmount);
			} catch (DAOException e) {
				throw new SystemException(e.getErrorCode(), "Faield to update a PolicyExtraAmount", e);
			}
		}

		public void deletePolicyExtraAmountInfo(PolicyExtraAmount policyExtraAmount) {
			try {
				policyExtraAmountDAO.delete(policyExtraAmount);
			} catch (DAOException e) {
				throw new SystemException(e.getErrorCode(), "Faield to delete a PolicyExtraAmount", e);
			}
		}

	

}
