package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.KPIContext;
import com.facilio.bmsconsole.util.KPIUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetKPIListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		FacilioModule module = ModuleFactory.getKpiModule();
		List<FacilioField> fields = FieldFactory.getKPIFields();
		Map<String, FacilioField> fieldMap =FieldFactory.getAsMap(fields);
		
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		boolean fetchCurrentValue = (boolean) context.getOrDefault("fetchCurrentValue", false);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				;
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		
		boolean fetchCount = (boolean) context.getOrDefault(FacilioConstants.ContextNames.FETCH_COUNT, false);
		if (fetchCount) {
			builder.select(new HashSet<>()).aggregate(CommonAggregateOperator.COUNT, fieldMap.get("id"));
			
			long count = 0;
			Map<String, Object> props = builder.fetchFirst();
			if (MapUtils.isNotEmpty(props)) {
				count = (long) props.get("id");
			}
			
			context.put(ContextNames.COUNT, count);
			return false;
		}
		
		builder.select(fields).orderBy(fieldMap.get("createdTime").getCompleteColumnName() + " DESC");
		
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			
			builder.offset(offset);
			builder.limit(perPage);
		}
		
		List<KPIContext> kpis = KPIUtil.fetchKPIFromProps(builder.get(), fetchCurrentValue);

		
		context.put(ContextNames.KPI_LIST, kpis);
		
		return false;
	}

}
