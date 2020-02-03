package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DeviceContext.DeviceType;
import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class VisitorKioskListCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(VisitorKioskListCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule visitorKioskModule = ModuleFactory.getVisitorKioskConfigModule();
		FacilioModule devicesModule = modBean.getModule(FacilioConstants.ModuleNames.DEVICES);

		List<FacilioField> deviceFields = modBean.getAllFields(FacilioConstants.ModuleNames.DEVICES);
		List<FacilioField> visitorKioskFields = FieldFactory.getVisitorKioskConfigFields();

		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.addAll(deviceFields);
		selectFields.addAll(visitorKioskFields);

		
		
		LookupField associatedResource=(LookupField)modBean.getField("associatedResource", FacilioConstants.ModuleNames.DEVICES);

		// DOES not work since printer is a factory module and Printers.MODULEID column absent so manually fill		
//		LookupField printer = new LookupField();
//		printer.setName("printer");
//		printer.setColumnName("PRINTER_ID");
//		printer.setDataType(FieldType.LOOKUP);
//		printer.setModule(ModuleFactory.getVisitorKioskConfigModule());
//		printer.setLookupModule(ModuleFactory.getPrinterModule());
		List<LookupField> fillLookups=new ArrayList<LookupField>();

		fillLookups.add(associatedResource);
//		fillLookups.add(printer);
		
		
		SelectRecordsBuilder<VisitorKioskContext> builder = new SelectRecordsBuilder<VisitorKioskContext>().select(selectFields)
				.beanClass(VisitorKioskContext.class).module(devicesModule).maxLevel(0).leftJoin(visitorKioskModule.getTableName())
				.on("Devices.ID=Devices_Visitor_Kiosk.ID").andCondition(CriteriaAPI.getCondition("DEVICE_TYPE", "deviceType", DeviceType.VISITOR_KIOSK.getIndex()+"", EnumOperators.IS));
		builder.fetchLookups(fillLookups);
		
	
		
		
		List<VisitorKioskContext> visitorKioskList = builder.get();
		if(visitorKioskList!=null)
		{
			context.put(ContextNames.RECORD_LIST, visitorKioskList);
		}
		else {
			context.put(ContextNames.RECORD_LIST, new ArrayList<VisitorKioskContext>());
		}
		
		return false;
	}
}
