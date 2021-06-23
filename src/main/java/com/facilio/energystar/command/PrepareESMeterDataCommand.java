package com.facilio.energystar.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.energystar.context.*;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrepareESMeterDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarMeterContext meter = (EnergyStarMeterContext) context.get(EnergyStarUtil.ENERGY_STAR_METER_CONTEXT);
		
		List<DateRange> intervals = (List<DateRange>) context.get(FacilioConstants.ContextNames.INTERVAL);
		
		List<EnergyStarMeterDataContext> dataList = new ArrayList<EnergyStarMeterDataContext>(); 
		for(DateRange interval :intervals) {
			
			EnergyStarMeterDataContext esMeterData = getEnergyStarMeterData(meter, interval.getStartTime(), interval.getEndTime());
			if(esMeterData != null) {
				dataList.add(esMeterData);
			}
		}
		
		context.put(EnergyStarUtil.ENERGY_STAR_METER_DATA_CONTEXTS,dataList);
		
		return false;
	}

	
	private EnergyStarMeterDataContext getEnergyStarMeterData(EnergyStarMeterContext meter,long startTime,long endTime) throws Exception {
		
		// assuming statTime and endTime are month's start and end Time resp. 
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Meter_Category type = meter.getTypeEnum();
		
		EnergyStarMeterDataContext data = new EnergyStarMeterDataContext();
		
		data.addReading("fromDate", startTime);
		data.addReading("toDate", endTime);
		data.setParent(meter);
		data.setParentId(meter.getId());
		
		List<Meter_Category_Points> points = Meter_Category_Points.getPointList(type.getIntVal());
		
		Map<Integer, EnergyStarMeterPointContext> pointMap = EnergyStarUtil.getEnergyStarMeterPointMap(meter.getId());
		
		boolean filledAtleastOneProp = false;
		
		for(Meter_Category_Points point :points) {
			
			String moduleName = point.getModuleName();
			String fieldName = point.getFieldName();
			AggregateOperator aggr = point.getAggr();
			
			if(pointMap.get(point.getPointId()) != null) {
				EnergyStarMeterPointContext pointContext = pointMap.get(point.getPointId());
				if(pointContext.getFieldId() > 0) {
					
					FacilioField field = modBean.getField(pointContext.getFieldId());
					fieldName = field.getName();
					moduleName = field.getModule().getName();
				}
				if(pointContext.getAggr() > 0) {
					aggr = AggregateOperator.getAggregateOperator(pointContext.getAggr());
				}
			}
			
			if(moduleName != null && fieldName != null && aggr != null) {
				
				FacilioModule module = modBean.getModule(moduleName);
				FacilioField field = modBean.getField(fieldName, moduleName);
				
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(moduleName));
				
				SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<>()
						.aggregate(aggr, field)
						.module(module)
						.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), meter.getMeterId()+"", NumberOperators.EQUALS))
						.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN))
						.skipUnitConversion();
				
				List<Map<String, Object>> props = select.getAsProps();
				
				if(props != null && !props.isEmpty()) {
					Object value = props.get(0).get(field.getName());
					if(value != null) {
						data.addReading(point.getName(), value);
						filledAtleastOneProp = true;
					}
				}
			}
		}
		if(filledAtleastOneProp) {
			return data;
		}
		else {
			return null;
		}
	}
}
