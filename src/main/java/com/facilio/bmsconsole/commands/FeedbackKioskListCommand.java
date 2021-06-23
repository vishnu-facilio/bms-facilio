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
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class FeedbackKioskListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

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
//		LookupField site=(LookupField)modBean.getField("site", FacilioConstants.ModuleNames.DEVICES);
//		fillLookups.add(site);

		
		
		SelectRecordsBuilder<FeedbackKioskContext> builder = new SelectRecordsBuilder<FeedbackKioskContext>().select(selectFields)
				.beanClass(FeedbackKioskContext.class).module(devicesModule).innerJoin(feedbackKioskModule.getTableName())
				.on("Devices.ID=Feedback_Kiosk.ID")
				.andCondition(CriteriaAPI.getCondition("DEVICE_TYPE", "deviceType", DeviceType.FEEDBACK_KIOSK.getIndex()+"", EnumOperators.IS)
				);
		builder.fetchSupplements(fillLookups);
	
		List<FeedbackKioskContext> feedbackKioskList=builder.get();
		
		
		if(feedbackKioskList!=null) {
			
			for (FeedbackKioskContext feedbackKiosk : feedbackKioskList) {
				feedbackKiosk.setFeedbackType(DevicesAPI.getFeedbackType(feedbackKiosk.getFeedbackTypeId(),(Boolean)context.getOrDefault(FacilioConstants.ContextNames.FILL_CATALOG_FORM, false)));	
			}
			
		}
		
			context.put(FacilioConstants.ContextNames.RECORD_LIST,feedbackKioskList);

		

		

		return false;
	}

}
