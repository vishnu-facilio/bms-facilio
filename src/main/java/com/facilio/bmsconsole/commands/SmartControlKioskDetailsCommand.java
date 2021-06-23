package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.DeviceContext.DeviceType;
import com.facilio.bmsconsole.context.SmartControlKioskContext;
import com.facilio.bmsconsole.util.TenantsAPI;
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

public class SmartControlKioskDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		 
		
		Long deviceId=(Long)context.get(ContextNames.RECORD_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule smartControlKioskModule = ModuleFactory.getSmartControlKioskModule();
		FacilioModule devicesModule = modBean.getModule(FacilioConstants.ModuleNames.DEVICES);

		List<FacilioField> deviceFields = modBean.getAllFields(FacilioConstants.ModuleNames.DEVICES);
		List<FacilioField> smartControlKioskFields = FieldFactory.getSmartControlKioskFields();

		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.addAll(deviceFields);
		selectFields.addAll(smartControlKioskFields);
		List<LookupField> fillLookups=new ArrayList<LookupField>();
		LookupField associatedResource=(LookupField)modBean.getField("associatedResource", FacilioConstants.ModuleNames.DEVICES);
		fillLookups.add(associatedResource);
//		
		
		
		SelectRecordsBuilder<SmartControlKioskContext> builder = new SelectRecordsBuilder<SmartControlKioskContext>().select(selectFields)
				.beanClass(SmartControlKioskContext.class).module(devicesModule).innerJoin(smartControlKioskModule.getTableName())
				.on("Devices.ID=Smart_Control_Kiosk.ID")
				.andCondition(CriteriaAPI.getCondition("DEVICE_TYPE", "deviceType", DeviceType.SMART_CONTROL_KIOSK.getIndex()+"", EnumOperators.IS))
			    .andCondition(CriteriaAPI.getCondition("Smart_Control_Kiosk.ID", "id",+deviceId+"", NumberOperators.EQUALS)
			   );
		builder.fetchSupplements(fillLookups);
	
		SmartControlKioskContext kiosk=builder.fetchFirst();
		if(kiosk!=null) {
			if((kiosk.getSpaceTypeEnum()==BaseSpaceContext.SpaceType.FLOOR) && (kiosk.getTenantId()>-1))
			{
				kiosk.setTenant(TenantsAPI.getTenant(kiosk.getTenantId()));
			}
		}
		
			context.put(FacilioConstants.ContextNames.RECORD,kiosk);

		

		return false;
	}


	

}
