package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class AddCommissioningLogCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		CommissioningLogContext log = (CommissioningLogContext) context.get(ContextNames.LOG);
		validateLog(log);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.COMMISSIONING_LOG);
		Map<String,Object> prop;
		if(module != null){
			List<FacilioField>fields = modBean.getAllFields(module.getName());
			prop = FieldUtil.getAsProperties(log);
			InsertRecordBuilder builder = new InsertRecordBuilder()
					.module(module)
					.fields(fields)
					.addRecordProps(Collections.singletonList(prop));

			builder.save();
			long logId = (long) prop.get("id");
			log.setId(logId);

			if (!log.isLogical()) {
				addControllers(log);
			}
		}
		else{
			module = ModuleFactory.getCommissioningLogModule();
			List<FacilioField>fields = FieldFactory.getCommissioningLogFields();
			log.setSysCreatedTime(System.currentTimeMillis());
			log.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
			prop = FieldUtil.getAsProperties(log);
			prop.put("sysCreatedBy",log.getSysCreatedBy());
			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
					.table(module.getTableName())
					.fields(fields)
					.addRecord(prop);

			builder.save();
			long logId = (long) prop.get("id");
			log.setId(logId);

			if (!log.isLogical()) {
				addControllers(log);
			}
		}

		return false;
	}
	
	private void validateLog(CommissioningLogContext log) throws Exception {
		if (log.getControllerTypeEnum() == null) {
			throw new IllegalArgumentException("Please select controller type");
		}
		validatePointsCount(log);
		List<Long> controllerIds = log.getControllerIds();
		if (controllerIds.contains(0l)) {
			if (controllerIds.size() > 1) {
				throw new IllegalArgumentException("Logical controller cannot be selected with other controllers");
			}
			log.setLogical(true);
			controllerIds = null;
		}
		Long draftId = CommissioningApi.checkDraftMode(log.getAgentId(), controllerIds);
		if (draftId != null && draftId > 0) {
			throw new IllegalArgumentException("Some controllers selected are already in draft mode");
		}
	}

	private void validatePointsCount(CommissioningLogContext log) throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Long> controllerIds = log.getControllerIds();
		FacilioModule controllerModule = ModuleFactory.getNewControllerModule();
		FacilioModule pointModule = ModuleFactory.getPointModule();
		FacilioModule resourceModule = ModuleFactory.getResourceModule();

		List<FacilioField> allFields = new ArrayList<>();
//		allFields.addAll(modBean.getModuleFields(controllerModule.getName()));
		allFields.add(FieldFactory.getIdField(controllerModule));
//		allFields.add(FieldFactory.getPointsCount());
		allFields.add(FieldFactory.getConfiguredPointCountConditionField());
		allFields.add(FieldFactory.getSubscribedPointCountConditionField());
		allFields.add(FieldFactory.getNameField(ModuleFactory.getResourceModule()));

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(controllerModule.getTableName())
				.leftJoin(pointModule.getTableName()).on(controllerModule.getTableName() + ".ID = " + pointModule.getTableName() + "." + FieldFactory.getControllerIdField(pointModule).getColumnName())
				.innerJoin(resourceModule.getTableName()).on(controllerModule.getTableName() + ".ID = " + resourceModule.getTableName() + ".ID")
				.andCondition(CriteriaAPI.getIdCondition(controllerIds, controllerModule))
				.select(allFields)
				.groupBy(controllerModule.getTableName()+".ID");


		List<Map<String, Object>> result = selectRecordBuilder.get();
		Map<String,Long>NameVsPointsCountMap = result.stream().collect(Collectors.toMap(prop->(String)prop.get("name"),prop->(((BigDecimal) prop.get(AgentConstants.SUBSCRIBED_COUNT)).longValue()) + ((BigDecimal) prop.get(AgentConstants.CONFIGURED_COUNT)).longValue()));
		for (String name : NameVsPointsCountMap.keySet()){
			if (NameVsPointsCountMap.get(name)==0){
				NameVsPointsCountMap.remove(name);
				if(NameVsPointsCountMap.containsValue(0L)){
					throw new IllegalArgumentException("No configured or subscribed points available for "+name+" and some other controllers");
				}
				else{
					throw new IllegalArgumentException("No configured or subscribed points available for the controller '"+name+"'");
				}
			}
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
