package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.builder.mysql.SelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.AggregateOperator.CommonAggregateOperator;
import com.facilio.modules.AggregateOperator.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
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
			String email = (String)context.get(ContextNames.ADMIN_USER_EMAIL);

			long TtimeLimit = TimeUnit.DAYS.convert(startTtime - endTtime, TimeUnit.MILLISECONDS);

			if (TtimeLimit > 60) {
				throw new IllegalArgumentException("Number of Days Should not be more than 60 days " + TtimeLimit);
			}
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean",orgId);
			FacilioField valField =modBean.getField(fieldId);
			String nameField = valField.getName();
			FacilioModule module = valField.getModule();
			String detaFieldName = nameField;
			

			List<FacilioField> fields = modBean.getAllFields(valField.getModule().getName());
			fields.add(FieldFactory.getIdField(module));
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField parentField = fieldMap.get("parentId");
			FacilioField ttimeField = fieldMap.get("ttime");
			FacilioField idFields = FieldFactory.getIdField(module);
			List<FacilioField> selectFields = new ArrayList<>();
			selectFields.add(parentField);
			selectFields.add(ttimeField);
			
			if (isCounterValField(valField)) {

				SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
						.aggregate(NumberAggregateOperator.MIN,idFields )
						.select(selectFields)
						.module(module)
						.beanClass(ReadingContext.class)
						.andCondition(CriteriaAPI.getCondition(parentField, String.valueOf(parentId), PickListOperators.IS))
						.andCondition(CriteriaAPI.getCondition(valField, CommonOperators.IS_NOT_EMPTY))
						.andCondition(CriteriaAPI.getCondition(ttimeField, startTtime + "," + endTtime,DateOperators.BETWEEN))
						.groupBy("TTIME")
						.having("COUNT(TTIME) > 1")
						.orderBy("TTIME");

				List<ReadingContext> prop = builder.get();
				ReadingContext time = prop.get(0);
				System.out.println("#######reading Value  :"+time.getTtime()+"id "+time.getId());
				
				updateDeltaCalculation(prop, module, nameField, valField, fields, detaFieldName,parentField,parentId,email,selectFields,ttimeField);
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
			FacilioField valField, List<FacilioField> valfields, String detaFieldName, FacilioField parentField, long parentId, String email, List<FacilioField> selectFields,FacilioField ttimeField) {
		// TODO Auto-generated method stub

		for (int i = 0; i < prop.size(); i++) {
			ReadingContext rowFirst = prop.get(i);
			
			long id =  rowFirst.getId();
			
			long deltaVal = -99;
			if (valField.getDataTypeEnum() == FieldType.DECIMAL || valField.getDataTypeEnum() == FieldType.NUMBER) {

				long prevTtime = rowFirst.getTtime();
				rowFirst.addReading(detaFieldName, deltaVal);
				UpdateRecordBuilder<ReadingContext> updateBuilder = new UpdateRecordBuilder<ReadingContext>().module(module)
						.fields(valfields)
						.andCondition(CriteriaAPI.getCondition(ttimeField, String.valueOf(prevTtime), StringOperators.IS))
						.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module),String.valueOf(id), StringOperators.ISN_T));
				try {
					int count = updateBuilder.update(rowFirst);
					if (count > 0) {
						sendEmail(email);
						System.out.println("#####Duplicates Removed Successfully" + count + "  " + "rows");
						LOGGER.info("#####Duplicates Removed Successfully");
					}

				} catch (Exception e) {
					LOGGER.info("###Duplicates Removed is failed to Update : " + e);
					System.out.println("###Duplicates Removed is failed to Update : " + e);
				}
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
		json.put("subject", "#####Duplicates Removal in Readings Table");
		json.put("message", "#####Duplicates Removed Successfully");
		
		AwsUtil.sendEmail(json);
	}

}



