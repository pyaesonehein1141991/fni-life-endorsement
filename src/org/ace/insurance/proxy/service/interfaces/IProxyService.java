package org.ace.insurance.proxy.service.interfaces;

import java.util.List;

import org.ace.insurance.common.BC001;
import org.ace.insurance.proxy.AGT001;
import org.ace.insurance.proxy.LCB001;
import org.ace.insurance.proxy.LCLD001;
import org.ace.insurance.proxy.LCP001;
import org.ace.insurance.proxy.LIF001;
import org.ace.insurance.proxy.LPP001;
import org.ace.insurance.proxy.LSP001;
import org.ace.insurance.proxy.MED001;
import org.ace.insurance.proxy.MEDCLM002;
import org.ace.insurance.proxy.MEDFEES001;
import org.ace.insurance.proxy.SPMA001;
import org.ace.insurance.proxy.TRA001;
import org.ace.insurance.proxy.WorkflowCriteria;

public interface IProxyService {

	public List<LIF001> find_LIF001(WorkflowCriteria wfc);

	public List<LCLD001> find_LCLD001(WorkflowCriteria wfc);

	public List<LCB001> find_LCB001(WorkflowCriteria wfc);

	public List<AGT001> find_AGT001(WorkflowCriteria wfc);

	public List<MEDCLM002> find_MEDCLM002(WorkflowCriteria wfc);

	public List<LSP001> find_LSP001(WorkflowCriteria wfc);

	public List<LPP001> find_LPP001(WorkflowCriteria wfc);

	public List<BC001> find_BC001(WorkflowCriteria wfc);

	public List<BC001> find_Health_BC001(WorkflowCriteria wfc);

	public List<MED001> find_MED001(WorkflowCriteria wfc);
	
	public List<MED001> find_MED002(WorkflowCriteria wfc);

	public List<SPMA001> findSoprtManAbroad_SPMA001(WorkflowCriteria wfc);

	public List<LCP001> find_LCP001(WorkflowCriteria wfc);

	public List<MEDFEES001> find_MEDFEES001(WorkflowCriteria wfc);

	public List<TRA001> find_TRA001(WorkflowCriteria wfc);

	public List<TRA001> find_TRA001_WFC(WorkflowCriteria wfc);
	
	public List<TRA001> findTravelProposalForDashboard(WorkflowCriteria wfc);
	
	//public List<TRA001> find_TRA001(WorkflowCriteria wfc);
	
	public List<TRA001> findTravel_WFC(WorkflowCriteria wfc);
	
	List<LSP001> find_LSP001_WFC(WorkflowCriteria wfc);
}
