package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class GetRDMCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = ModuleFactory.getReadingDataMetaModule().getName();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getReadingDataMetaFields());
		
		Boolean fetchCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		List<FacilioField> fields = null;
		if (fetchCount != null && fetchCount) {
			FacilioField countFld = new FacilioField();
			countFld.setName("count");
			countFld.setColumnName("COUNT(Reading_Data_Meta.ID)");
			countFld.setDataType(FieldType.NUMBER);
			fields = Collections.singletonList(countFld);
		}
		else {
			fields = FieldFactory.getReadingDataMetaFields();
		}
		
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getReadingDataMetaModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("value"), "-1", StringOperators.ISN_T))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("readingType"), ReadingDataMeta.ReadingType.WRITE.getValue()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("isControllable"), Boolean.TRUE.toString(), BooleanOperators.IS));	// move this criteria to Action
		
		
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			builder.andCriteria(view.getCriteria());
		}

		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}
		
		String criteriaIds = (String) context.get(FacilioConstants.ContextNames.CRITERIA_IDS);
		if (criteriaIds != null) {
			String[] ids = criteriaIds.split(",");
			for(int i = 0; i < ids.length; i++) {
				Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), Long.parseLong(ids[i]));
				builder.andCriteria(criteria);
			}
		}
		
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
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
		
		Long entityId = (Long) context.get(FacilioConstants.ContextNames.ALARM_ENTITY_ID);
		if(entityId != null && entityId != -1)
		{
			builder.andCustomWhere("Alarms.ENTITY_ID = ?", entityId);
		}
		
		
		List<Map<String, Object>> props = builder.get();
		
		if(fetchCount == null || !fetchCount) {
			List<ReadingDataMeta> readingMetaList = ReadingsAPI.getReadingDataFromProps(props, null);
			
			if(readingMetaList != null) {
				for(ReadingDataMeta readingMeta :readingMetaList) {
					AssetContext asset = AssetsAPI.getAssetInfo(readingMeta.getResourceId());
					asset.setCategory(AssetsAPI.getCategoryForAsset(asset.getCategory().getId()));
					readingMeta.setResourceContext(asset);
				}
			}
			
			context.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, readingMetaList);
		}
		else {
			context.put(FacilioConstants.ContextNames.READING_DATA_META_COUNT, props.get(0).get("count"));
		}
		
		return false;
	}

}
