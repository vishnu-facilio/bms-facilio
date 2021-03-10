package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DeviceContext.DeviceType;
import com.facilio.bmsconsole.context.PrinterContext;
import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class VisitorKioskListCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(VisitorKioskListCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule visitorKioskModule = ModuleFactory.getVisitorKioskModule();
		FacilioModule devicesModule = modBean.getModule(FacilioConstants.ModuleNames.DEVICES);

		List<FacilioField> deviceFields = modBean.getAllFields(FacilioConstants.ModuleNames.DEVICES);
		List<FacilioField> visitorKioskFields = FieldFactory.getVisitorKioskFields();

		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.addAll(deviceFields);
		selectFields.addAll(visitorKioskFields);

		
		
		LookupField associatedResource=(LookupField)modBean.getField("associatedResource", FacilioConstants.ModuleNames.DEVICES);
		List<LookupField> fillLookups=new ArrayList<LookupField>();
		fillLookups.add(associatedResource);

		
		
		SelectRecordsBuilder<VisitorKioskContext> builder = new SelectRecordsBuilder<VisitorKioskContext>().select(selectFields)
				.beanClass(VisitorKioskContext.class).module(devicesModule).maxLevel(0).innerJoin(visitorKioskModule.getTableName())
				.on("Devices.ID=Devices_Visitor_Kiosk.ID").andCondition(CriteriaAPI.getCondition("DEVICE_TYPE", "deviceType", DeviceType.VISITOR_KIOSK.getIndex()+"", EnumOperators.IS));
		builder.fetchSupplements(fillLookups);
		
	
		
		
		List<VisitorKioskContext> visitorKioskList = builder.get();
		
		
		
		if(visitorKioskList!=null)
		{
			this.fillPrinterLookup(visitorKioskList);
		
			context.put(ContextNames.RECORD_LIST, visitorKioskList);
		}
		else {
			context.put(ContextNames.RECORD_LIST, new ArrayList<VisitorKioskContext>());
		}
		
		return false;
	}
	
	private void fillPrinterLookup(List<VisitorKioskContext> visitorKioskList) throws Exception
	{
		List<Long> printerIds=visitorKioskList.stream()
				.filter(visitorKiosk -> visitorKiosk.getPrinterId() > 0)
			   .map(visitorKiosk -> visitorKiosk.getPrinterId()).collect(Collectors.toList());
		if(printerIds.isEmpty())
		{
			return ;
		}
		
		FacilioModule printerModule=ModuleFactory.getPrinterModule();
		GenericSelectRecordBuilder fetchPrinterBuilder=new GenericSelectRecordBuilder()
				.table(printerModule.getTableName())
				.select(FieldFactory.getPrinterFields()).andCondition(
						CriteriaAPI.getIdCondition(printerIds,printerModule));
		
		 
		List<Map<String, Object>> propList=fetchPrinterBuilder.get();
		if(propList!=null)
		{
		
   		List<PrinterContext> printers=FieldUtil.getAsBeanListFromMapList(propList, PrinterContext.class);
   		
   		Map<Long, PrinterContext> printerMap=new HashMap<>();
   		
   		for(PrinterContext printer :printers)
   		{
   			printerMap.put(printer.getId(), printer);
   		}
   		
   		for(VisitorKioskContext kiosk:visitorKioskList)
   		{
   			if(printerMap.containsKey(kiosk.getPrinterId()))
   			{
   				kiosk.setPrinter(printerMap.get(kiosk.getPrinterId()));
   			}
   		}
   		
		}
		
	}
}
