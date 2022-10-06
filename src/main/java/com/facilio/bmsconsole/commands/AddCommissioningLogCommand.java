package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CommissioningLogContext;
import com.facilio.bmsconsole.util.CommissioningApi;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericInsertRecordBuilder;

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
