package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleBeanImpl;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
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
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;

public class AdminDeltaCalculationCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(AdminDeltaCalculationCommand.class.getName());

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
			String email = (String)context.get(ContextNames.ADMIN_USER_EMAIL);
			
			long TtimeLimit = TimeUnit.DAYS.convert(startTtime - endTtime, TimeUnit.MILLISECONDS);

			if (TtimeLimit > 60) {
				throw new IllegalArgumentException("Number of Days Should not be more than 60 days " + TtimeLimit);
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean",orgId);
			FacilioField valField =modBean.getField(fieldId);
			String nameField = valField.getName();
			FacilioModule module = valField.getModule();
			String detaFieldName = nameField+"Delta";

			List<FacilioField> fields = modBean.getAllFields(valField.getModule().getName());
			fields.add(FieldFactory.getIdField(module));
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField parentField = fieldMap.get("parentId");
			FacilioField ttimeField = fieldMap.get("ttime");

			if (isCounterValField(valField)) {

				SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
				.select(fields).module(module).beanClass(ReadingContext.class)
						.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(parentId), PickListOperators.IS))
						.andCondition(CriteriaAPI.getCondition(valField, CommonOperators.IS_NOT_EMPTY))
						.andCondition(CriteriaAPI.getCondition(ttimeField, startTtime + "," + endTtime,DateOperators.BETWEEN))
						.orderBy("TTIME");

				List<ReadingContext> prop = builder.get();
				
				updateDeltaCalculation(prop, module, nameField, valField, fields, detaFieldName,email);
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

	private void updateDeltaCalculation(List<ReadingContext> prop, FacilioModule module, String nameField,
			FacilioField valField, List<FacilioField> valfields, String detaFieldName, String email) {
		// TODO Auto-generated method stub
		

		for (int i = 1; i < prop.size(); i++) {
			ReadingContext rowFirst = prop.get(i - 1);
			ReadingContext rowSecond = prop.get(i);
			long id =  rowSecond.getId();

			Object deltaVal = null;
			if (valField.getDataTypeEnum() == FieldType.DECIMAL) {
				Double prevVal = (Double) rowFirst.getReading(nameField);
				Double currentValue = (Double) rowSecond.getReading(nameField);
				if (prevVal != -1) {
					deltaVal = (Double) currentValue - prevVal;
				}
			} else {
				Long prevVal = (Long) rowFirst.getReading(nameField);
				Long currentValue = (Long) rowSecond.getReading(nameField);
				if (prevVal != -1) {
					deltaVal = (Long) currentValue - prevVal;
				}
			}

			if (deltaVal != null) {
				rowSecond.addReading(detaFieldName, deltaVal);
				if (AccountUtil.getCurrentOrg().getId() == 78) {
					LOGGER.info("Delta Value for "+detaFieldName+" is : "+deltaVal);
				}
			}
			
			UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>().module(module)
					.fields(valfields).andCondition(CriteriaAPI.getIdCondition(id, module));
			try {
				int count = updateBuilder.update(rowSecond);
				if (count > 0) {
					sendEmail(email);
					System.out.println("#####DeltaCalculation Updated Successfully" + count + "  " + "rows");
					LOGGER.info("#####DeltaCalculation Updated Successfully");
				}

			} catch (Exception e) {
				LOGGER.info("###DeltaCalculation is failed to Update : " + e);
				System.out.println("###DeltaCalculation is failed to Update : " + e);
			}

		}
	}

	private boolean isCounterValField(FacilioField valField) {

		// TODO Auto-generated method stub

		if ((valField.getDataTypeEnum() == FieldType.NUMBER || valField.getDataTypeEnum() == FieldType.DECIMAL)) {
			return true;
		}

		return false;
	}
	private void sendEmail(String email)throws Exception{
		JSONObject json = new JSONObject();
		json.put("to", email);
		json.put("sender", "noreply@facilio.com");
		json.put("subject", "#####DeltaCalculation Removed Successfully");
		json.put("message", "#####DeltaCalculation Removed Successfully");
		
		AwsUtil.sendEmail(json);
	}

}
