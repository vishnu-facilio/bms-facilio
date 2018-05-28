package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DerivationContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;

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
		FacilioModule module = ModuleFactory.getDerivationsModule();
		List<FacilioField> fields = FieldFactory.getDerivationFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("analyticsType"), String.valueOf(type), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		
		List<Map<String, Object>> props = builder.get();
		List<DerivationContext> derivations = new ArrayList();
		for(Map<String, Object> prop : props) 
		{
			DerivationContext derivation = FieldUtil.getAsBeanFromMap(prop, DerivationContext.class);
			derivation.setWorkflow(WorkflowUtil.getWorkflowContext(derivation.getWorkflowId()));
			derivations.add(derivation);
		}
		
		return derivations;
	}

}
