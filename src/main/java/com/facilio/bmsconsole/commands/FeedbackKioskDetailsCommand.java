package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DeviceContext.DeviceType;
import com.facilio.bmsconsole.context.FeedbackKioskContext;
import com.facilio.bmsconsole.util.DevicesAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class FeedbackKioskDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		 
		
		Long deviceId=(Long)context.get(ContextNames.RECORD_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule feedbackKioskModule = ModuleFactory.getFeedbackKioskModule();
		FacilioModule devicesModule = modBean.getModule(FacilioConstants.ModuleNames.DEVICES);

		List<FacilioField> deviceFields = modBean.getAllFields(FacilioConstants.ModuleNames.DEVICES);
		List<FacilioField> feedbackKioskFields = FieldFactory.getFeedbackKioskFields();

		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.addAll(deviceFields);
		selectFields.addAll(feedbackKioskFields);
		List<LookupField> fillLookups=new ArrayList<LookupField>();
		LookupField associatedResource=(LookupField)modBean.getField("associatedResource", FacilioConstants.ModuleNames.DEVICES);
		fillLookups.add(associatedResource);
//		
		
		
		SelectRecordsBuilder<FeedbackKioskContext> builder = new SelectRecordsBuilder<FeedbackKioskContext>().select(selectFields)
				.beanClass(FeedbackKioskContext.class).module(devicesModule).innerJoin(feedbackKioskModule.getTableName())
				.on("Devices.ID=Feedback_Kiosk.ID")
				.andCondition(CriteriaAPI.getCondition("DEVICE_TYPE", "deviceType", DeviceType.FEEDBACK_KIOSK.getIndex()+"", EnumOperators.IS))
			    .andCondition(CriteriaAPI.getCondition("Feedback_Kiosk.ID", "id",+deviceId+"", NumberOperators.EQUALS)
			   );
		builder.fetchSupplements(fillLookups);
	
		FeedbackKioskContext feedbackKioskContext=builder.fetchFirst();
		if(feedbackKioskContext!=null) {
			feedbackKioskContext.setFeedbackType(DevicesAPI.getFeedbackType(feedbackKioskContext.getFeedbackTypeId(), true));
		}
		
			context.put(FacilioConstants.ContextNames.RECORD,feedbackKioskContext);

		

		return false;
	}


	

}
