package com.facilio.energystar.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.FacilioFrequency;
import com.facilio.bmsconsole.util.FormulaFieldAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarMeterDataContext;
import com.facilio.energystar.context.EnergyStarMeterPointContext;
import com.facilio.energystar.context.Meter_Category;
import com.facilio.energystar.context.Meter_Category_Points;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class PrepareESMeterDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		EnergyStarMeterContext meter = (EnergyStarMeterContext) context.get(EnergyStarUtil.ENERGY_STAR_METER_CONTEXT);
		
		List<DateRange> intervals = (List<DateRange>) context.get(FacilioConstants.ContextNames.INTERVAL);
		
		List<EnergyStarMeterDataContext> dataList = new ArrayList<EnergyStarMeterDataContext>(); 
		for(DateRange interval :intervals) {
			
			EnergyStarMeterDataContext esMeterData = getEnergyStarMeterData(meter, interval.getStartTime(), interval.getEndTime());
			
			dataList.add(esMeterData);
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
						.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));
				
				List<Map<String, Object>> props = select.getAsProps();
				
				if(props != null) {
					Object value = props.get(0).get(field.getName());
					
					data.addReading(point.getName(), value);
				}
			}
		}
		return data;
	}
}
