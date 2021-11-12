package org.ace.insurance.life.policyExtraAmount.persistence.interfaces;



import org.ace.insurance.life.policyExtraAmount.PolicyExtraAmount;
import org.ace.java.component.persistence.exception.DAOException;

public interface IPolicyExtraAmountDAO {
	public void insert(PolicyExtraAmount policyExtraAmount) throws DAOException;

	public void update(PolicyExtraAmount policyExtraAmount) throws DAOException;

	public void delete(PolicyExtraAmount policyExtraAmount) throws DAOException;



}
