package com.facilio.energystar.command;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.energystar.context.EnergyStarCustomerContext.Data_Exchange_Mode;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.util.EnergyStarSDK;
import com.facilio.energystar.util.EnergyStarUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class EnergyStarPushDataCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		EnergyStarMeterContext meter = (EnergyStarMeterContext) context.get(EnergyStarUtil.ENERGY_STAR_METER_CONTEXT);
		
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
			
		FacilioModule module = null;
		
		FacilioField field = null;
		
		Data_Exchange_Mode type = meter.getTypeEnum();
		
		if(meter.getMeterDataModuleId() > 0) {
			module = modBean.getModule(meter.getMeterDataModuleId());
			field = modBean.getField(meter.getMeterDataFieldId());
		}
		else {
			module = modBean.getModule(type.getModule());
			field = modBean.getField(type.getField(), type.getModule());
		}
		
		NumberAggregateOperator aggr = type.getAggr();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<>()
				.aggregate(aggr, field)
				.module(module)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), meter.getMeterId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));
		
		List<Map<String, Object>> props = select.getAsProps();
		
		if(props != null) {
			Object value = props.get(0).get(field.getName());
			
			String from = DateTimeUtil.getFormattedTime(startTime, "yyyy-MM-dd");
			
			String to = DateTimeUtil.getFormattedTime(DateTimeUtil.addDays(startTime, 1), "yyyy-MM-dd");
			
			String id = EnergyStarSDK.addConsumptionData(meter, value.toString(), from, to);
			
			System.out.println("recieved id --- "+id);
		}
		return false;
	}

}
