package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.actions.VisitorKioskAction;
import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class PrinterUtils {

	private static Logger log = LogManager.getLogger(PrinterUtils.class.getName());
	public static void refreshPrinterHostDevice(Long printerId) throws Exception
	{
		//get all devices associated with this printer and send Socket RELOAD EVENT
		
		if(printerId!=null&&printerId>0)
		{
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getVisitorKioskFields())
					.table(ModuleFactory.getVisitorKioskModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("PRINTER_ID","printerId",printerId+"",NumberOperators.EQUALS));
			List<Map<String,Object>> visitorKioskProps= selectBuilder.get();
			if(visitorKioskProps!=null&&visitorKioskProps.size()>0)
			{
				//to do , send message only to live devices
				List<VisitorKioskContext> visitorKiosks=FieldUtil.getAsBeanListFromMapList(visitorKioskProps, VisitorKioskContext.class);
				for(VisitorKioskContext visitorKiosk:visitorKiosks)
				{
					DevicesUtil.reloadConf(visitorKiosk.getId(),visitorKiosk.getOrgId());
				}
																			
			}
								
		}
		else {
			throw new Exception("Invalid printer ID");
		}
	
}
}
