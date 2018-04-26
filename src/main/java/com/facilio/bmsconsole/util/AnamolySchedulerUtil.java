package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AnalyticsAnamolyContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;


public class AnamolySchedulerUtil {
	

	private static final Logger logger = Logger.getLogger(AnamolySchedulerUtil.class.getName());
	private static final String energyDataTable = ModuleFactory.getAnalyticsAnamolyModule().getTableName();
	private static final String anamolyIdTable =  ModuleFactory.getAnalyticsAnamolyIDListModule().getTableName();
	
	public static List<AnalyticsAnamolyContext> getAllReadings(String moduleName, long startTime, long endTime, long meterID, long orgID) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<AnalyticsAnamolyContext> listOfReadings=new ArrayList<AnalyticsAnamolyContext>();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getAnamolySchedulerFields())
				.table(energyDataTable)
				.andCustomWhere("TTIME > ? AND TTIME < ? AND PARENT_METER_ID=? AND TOTAL_ENERGY_CONSUMPTION_DELTA IS NOT NULL  AND ORGID = ? ", startTime, endTime, meterID, orgID);
	
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop:props) {
				AnalyticsAnamolyContext anamolyContext = FieldUtil.getAsBeanFromMap(prop, AnalyticsAnamolyContext.class);
				listOfReadings.add(anamolyContext);
			}
		}

		return listOfReadings;
	}
	
	public static LinkedHashSet<Long> getExistingAnamolyIDs(String moduleName, String anamolyIDList) throws Exception{
		LinkedHashSet<Long> setOfAnamolyIDs=new LinkedHashSet<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getAnamolyIDFields())
				.table(anamolyIdTable)
				.andCustomWhere(" ID in  " + anamolyIDList);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop:props) {
				logger.info("Query generated: " + prop);
				AnalyticsAnamolyContext context = FieldUtil.getAsBeanFromMap(prop, AnalyticsAnamolyContext.class);
				setOfAnamolyIDs.add(context.getId());
			}
		}

		return setOfAnamolyIDs;
	}
}