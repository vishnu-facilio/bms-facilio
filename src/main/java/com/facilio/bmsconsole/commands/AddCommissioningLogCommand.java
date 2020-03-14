package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class AddCommissioningLogCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		CommissioningLogContext log = (CommissioningLogContext) context.get(ContextNames.LOG);
		validateLog(log);
		
		log.setSysCreatedTime(System.currentTimeMillis());
		log.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
		Map<String, Object> prop = FieldUtil.getAsProperties(log);
		
		FacilioModule module = ModuleFactory.getCommissioningLogModule();
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getCommissioningLogFields())
				.addRecord(prop);
		
		builder.save();
		long logId = (long) prop.get("id");
		log.setId(logId);
		
		addControllers(log);
		
		return false;
	}
	
	private void validateLog(CommissioningLogContext log) throws Exception {
		if (log.getControllerTypeEnum() == null) {
			throw new IllegalArgumentException("Please select controller type");
		}
		List<Long> controllerIds = log.getControllerIds();
		FacilioModule module = ModuleFactory.getCommissioningLogModule();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getCommissioningLogFields());
		FacilioModule controllerModule = ModuleFactory.getCommissioningLogControllerModule();
		Map<String, FacilioField> controllerFieldMap = FieldFactory.getAsMap(FieldFactory.getCommissioningLogControllerFields());
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.innerJoin(controllerModule.getTableName())
				.on(fieldMap.get("id").getCompleteColumnName()+"="+controllerFieldMap.get("commissioningLogId").getCompleteColumnName())
				.select(new HashSet<>())
				.aggregate(CommonAggregateOperator.COUNT, FieldFactory.getIdField())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("publishedTime"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(controllerFieldMap.get("controllerId"), controllerIds, NumberOperators.EQUALS));
				;
				
		Map<String, Object> props = builder.fetchFirst();
		long count = (long) props.get("id");
		if (count > 0) {
			throw new IllegalArgumentException("Some controllers selected are already in draft mode");
		}
	}
	
	private void addControllers(CommissioningLogContext log) throws Exception {
		long logId = log.getId();
		List<Map<String, Object>> props = log.getControllerIds().stream().map(controllerId -> {
			Map<String, Object> prop = new HashMap<>();
			prop.put("commissioningLogId", logId);
			prop.put("controllerId", controllerId);
			return prop;
		}).collect(Collectors.toList());
		
		FacilioModule module = ModuleFactory.getCommissioningLogControllerModule();
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getCommissioningLogControllerFields())
				.addRecords(props);
		
		builder.save();
	}

}
