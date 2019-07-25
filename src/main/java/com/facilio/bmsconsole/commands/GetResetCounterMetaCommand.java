package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ResetCounterMetaContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;

public class GetResetCounterMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ResetCounterMetaContext> resetCounterMetaList = (List<ResetCounterMetaContext>) context.get(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST);
		List<Long> resetCounterIds =  resetCounterMetaList.stream().map(ResetCounterMetaContext::getId).collect(Collectors.toList());
		
		FacilioModule module = ModuleFactory.getResetCounterMetaModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(FieldFactory.getResetCounterMetaFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(resetCounterIds, module));
		
		List<Map<String, Object>> props = selectBuilder.get();
		resetCounterMetaList = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props){
				ResetCounterMetaContext reset = FieldUtil.getAsBeanFromMap(prop, ResetCounterMetaContext.class);
				resetCounterMetaList.add(reset);
			}
		}
		context.put(FacilioConstants.ContextNames.RESET_COUNTER_META_LIST,resetCounterMetaList);
		return false;
	}

}
