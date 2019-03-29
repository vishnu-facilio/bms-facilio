package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DerivationContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DerivationAPI {
	
	public static long addDerivation(DerivationContext derivationContext) throws Exception {
		
		long workflowId = WorkflowUtil.addWorkflow(derivationContext.getWorkflow());
		derivationContext.setWorkflowId(workflowId);
		derivationContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		Map<String, Object> prop = FieldUtil.getAsProperties(derivationContext);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(ModuleFactory.getDerivationsModule().getTableName())
														.fields(FieldFactory.getDerivationFields())
														.addRecord(prop);
		insertBuilder.save();
		
		return (long) prop.get("id");
	}
	
	public static List<DerivationContext> getDerivationList(int type) throws Exception {
		return getDerivation(type, -1);
	}
	
	public static DerivationContext getDerivation(long id) throws Exception {
		return getDerivation(-1, id).get(0);
	}
	
	private static List<DerivationContext> getDerivation(int type, long id) throws Exception {
		FacilioModule module = ModuleFactory.getDerivationsModule();
		List<FacilioField> fields = FieldFactory.getDerivationFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		
		if (type != -1) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("analyticsType"), String.valueOf(type), NumberOperators.EQUALS));
		}
		
		if (id != -1) {
			builder.andCondition(CriteriaAPI.getIdCondition(id, module));
		}
		
		List<Map<String, Object>> props = builder.get();
		List<DerivationContext> derivations = new ArrayList();
		for(Map<String, Object> prop : props) 
		{
			DerivationContext derivation = FieldUtil.getAsBeanFromMap(prop, DerivationContext.class);
			derivation.setWorkflow(WorkflowUtil.getWorkflowContext(derivation.getWorkflowId(), true));
			derivations.add(derivation);
		}
		
		return derivations;
	}
	
	public static DerivationContext updateDerivation(DerivationContext derivationContext) throws Exception {
		if (derivationContext.getWorkflow() != null) {
			long oldWorkflowId = derivationContext.getWorkflowId();
			long workflowId = WorkflowUtil.addWorkflow(derivationContext.getWorkflow());
			derivationContext.setWorkflowId(workflowId);
			WorkflowUtil.deleteWorkflow(oldWorkflowId);
		}
		derivationContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		FacilioModule module = ModuleFactory.getDerivationsModule();
		Map<String, Object> prop = FieldUtil.getAsProperties(derivationContext);
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getDerivationFields())
				.andCondition(CriteriaAPI.getIdCondition( derivationContext.getId(), module));
		
		builder.update(prop);
		return getDerivation(derivationContext.getId());
	}
	
	public static void deleteDerivation(long id) throws Exception {
		DerivationContext derivation = getDerivation(id);
		WorkflowUtil.deleteWorkflow(derivation.getWorkflowId());
	}

}
