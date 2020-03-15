package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SmartControlKioskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class KioskAPI {

	public static SmartControlKioskContext getSmartControlDevice(long deviceId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule smartControlKiosk = ModuleFactory.getSmartControlKioskModule();
		FacilioModule devicesModule = modBean.getModule(FacilioConstants.ModuleNames.DEVICES);

		List<FacilioField> deviceFields = modBean.getAllFields(FacilioConstants.ModuleNames.DEVICES);
		List<FacilioField> smartControlKioskFields = FieldFactory.getSmartControlKioskFields();
		
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.addAll(deviceFields);
		selectFields.addAll(smartControlKioskFields);
		
		SelectRecordsBuilder<SmartControlKioskContext> builder = new SelectRecordsBuilder<SmartControlKioskContext>().select(selectFields)
				.beanClass(SmartControlKioskContext.class).module(devicesModule).innerJoin(smartControlKiosk.getTableName())
				.on("Devices.ID=Smart_Control_Kiosk.ID")
				.andCondition(CriteriaAPI.getIdCondition(deviceId, devicesModule));
	
		List<SmartControlKioskContext> smartControlKioskList = builder.get();
		
		if (smartControlKioskList != null) {
			return smartControlKioskList.get(0);
		}
		return null;
	}
}
