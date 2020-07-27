package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.QuickFilterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class FetchQuickFilterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Long viewId =  (Long) context.get(FacilioConstants.ContextNames.VIEWID);
		
		if (viewId > 0) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getQuickFilterFields())
					.table(ModuleFactory.getQuickFilterModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("VIEWID","viewId",String.valueOf(viewId), NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectBuilder.get();
			
			if (props != null && !props.isEmpty()) {
				List<QuickFilterContext> quickFilters = new ArrayList<>();
				for(Map<String, Object> prop : props) {
					QuickFilterContext quickFilter = FieldUtil.getAsBeanFromMap(prop, QuickFilterContext.class);
					quickFilters.add(quickFilter);
				}
				context.put(FacilioConstants.ContextNames.QUICK_FILTER_CONTEXT, quickFilters);
			}
		}
		return false;
	}

	

}
