package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.KPICategoryContext;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;

public class KPIAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private KPICategoryContext kpiCategory;
	
	public KPICategoryContext getKpiCategory() {
		return kpiCategory;
	}
	public void setKpiCategory(KPICategoryContext kpiCategory) {
		this.kpiCategory = kpiCategory;
	}
	
	public String getAllKPICategories() throws Exception {
		
		setResult(KPIUtil.KPI_CATEGORY_CONTEXTS, KPIUtil.getAllKPIContext());
		return SUCCESS;
	}
	
	
	public String addKPICategory() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		
		FacilioChain addMVProjectChain =  TransactionChainFactory.getAddKPICategoryChain();
		addMVProjectChain.execute(context);
		
		setResult(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		return SUCCESS;
	}
	public String updateKPICategory() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		
		FacilioChain addMVProjectChain =  TransactionChainFactory.getUpdateKPICategoryChain();
		addMVProjectChain.execute(context);
		
		setResult(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		
		return SUCCESS;
	}
	public String deleteKPICategory() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
		
		FacilioChain addMVProjectChain =  TransactionChainFactory.getDeleteKPICategoryChain();
		addMVProjectChain.execute(context);
		
		setResult(KPIUtil.KPI_CATEGORY_CONTEXT, kpiCategory);
	
		return SUCCESS;
	}
}
