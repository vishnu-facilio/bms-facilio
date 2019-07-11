package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

public class AdminRemoveDuplicationCommand implements Command {
	
	
	private static final Logger LOGGER = LogManager.getLogger(AdminRemoveDuplicationCommand.class.getName());


	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Boolean historyReading = (Boolean) context.get(FacilioConstants.ContextNames.HISTORY_READINGS);
		if (historyReading != null && historyReading == true) {
			return false;
		}
		
		try {
			long orgId = (long) context.get(ContextNames.ADMIN_DELTA_ORG);
			long fieldId = (long) context.get(ContextNames.FIELD_ID);
			long parentId = (long) context.get(ContextNames.ASSET_ID);
			long startTtime = (long) context.get(ContextNames.START_TTIME);
			long endTtime = (long) context.get(ContextNames.END_TTIME);

			long TtimeLimit = TimeUnit.DAYS.convert(startTtime - endTtime, TimeUnit.MILLISECONDS);

			if (TtimeLimit > 60) {
				throw new IllegalArgumentException("Number of Days Should not be more than 60 days " + TtimeLimit);
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean",orgId);
			FacilioField valField =modBean.getField(fieldId);
			String nameField = valField.getName();
			FacilioModule module = modBean.getModule(valField.getModuleId());
			String detaFieldName = nameField;

			List<FacilioField> fields = modBean.getAllFields(valField.getModule().getName());
			fields.add(FieldFactory.getIdField(module));
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField parentField = fieldMap.get("parentId");
			FacilioField ttimeField = fieldMap.get("ttime");

			if (isCounterValField(valField)) {

				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(fields).table(module.getTableName())
						.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(parentId), PickListOperators.IS))
						.andCondition(CriteriaAPI.getCondition(valField, CommonOperators.IS_NOT_EMPTY))
						.andCondition(CriteriaAPI.getCondition(ttimeField, startTtime + "," + endTtime,DateOperators.BETWEEN))
						.orderBy("TTIME");

				List<Map<String, Object>> prop = builder.get();
				
				updateDeltaCalculation(prop, module, nameField, valField, fields, detaFieldName);
			} else {
				System.out.println("FieldType is not a CounterField");
				throw new IllegalArgumentException("FieldType is not a CounterField");
				
			}
		} catch (Exception e) {
			System.out.println("DeltaCalculation is failed to Update - orgid"+e);
			CommonCommandUtil.emailException("DeltaCalculation",
					"DeltaCalculation is failed to Update - orgid -- " + AccountUtil.getCurrentOrg().getId(), e);
		}

		return false;
		
		
	}

	private void updateDeltaCalculation(List<Map<String, Object>> prop, FacilioModule module, String nameField,
			FacilioField valField, List<FacilioField> valfields, String detaFieldName) {
		// TODO Auto-generated method stub

		Map<String,Object> updateReading = new HashMap<>();
		int j =0;
		for (int i = 0; i < prop.size(); i++) {
			Map<String, Object> rowFirst = prop.get(i);
			for ( j= i+1; j < prop.size(); j++) {

				Map<String, Object> rowSecond = prop.get(j);
				Long id = (Long) rowSecond.get("id");

				Object deltaVal = -99;
				if (valField.getDataTypeEnum() == FieldType.DECIMAL || valField.getDataTypeEnum() == FieldType.NUMBER) {

					Long prevTtime = (Long) rowFirst.get("ttime");
					Long currentTtime = (Long)rowSecond.get("ttime");
					if (prevTtime.equals(currentTtime)) {
						updateReading.put(detaFieldName, deltaVal);
						GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName())
								.fields(valfields).andCondition(CriteriaAPI.getIdCondition(id, module));
						try {
							int count = updateBuilder.update(updateReading);
							if (count > 0) {
								System.out.println("#####Duplicates Removed Successfully" + count + "  " + "rows");
								LOGGER.info("#####Duplicates Removed Successfully");
							}

						} catch (Exception e) {
							LOGGER.info("###Duplicates Removed is failed to Update : " + e);
							System.out.println("###Duplicates Removed is failed to Update : " + e);
						}
						continue;
					}
					break;
				}
				
			}
			i=j;
			

		}
	}


	private boolean isCounterValField(FacilioField valField) {

		// TODO Auto-generated method stub

		if ((valField.getDataTypeEnum() == FieldType.NUMBER || valField.getDataTypeEnum() == FieldType.DECIMAL)) {
			return true;
		}

		return false;
	}

}



