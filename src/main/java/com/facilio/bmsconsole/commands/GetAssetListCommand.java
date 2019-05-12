package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetAssetListCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(GetAssetListCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<String> selectFields = (List<String>) context.get(FacilioConstants.ContextNames.FETCH_SELECTED_FIELDS);
		
		Boolean getCount = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_COUNT);
		List<FacilioField> fields;
		List<FacilioField> specifiedfields =  new ArrayList<>();
		if (getCount != null && getCount) {
			fields = FieldFactory.getCountField(module);
		}
		else {
			fields = modBean.getAllFields(moduleName);
			if (selectFields != null) {
				Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
				for (String fil : selectFields)
				{
					if (fieldsMap.get(fil) != null) {
						specifiedfields.add(fieldsMap.get(fil));
					}
				}
			}
		}
		if(specifiedfields.isEmpty()) {
			specifiedfields = fields;
		}
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModuleName(moduleName);
		if (beanClassName == null) {
			beanClassName = AssetContext.class;
		}
		SelectRecordsBuilder<AssetContext> builder = new SelectRecordsBuilder<AssetContext>()
															.module(module)
															.beanClass(beanClassName)
															.select(specifiedfields)
															;
		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			builder.orderBy(orderBy);
		}
		
		Integer maxLevel = (Integer) context.get(FacilioConstants.ContextNames.MAX_LEVEL);
		if(maxLevel != null && maxLevel != -1) {
			builder.maxLevel(maxLevel);
		}
		
		List<Long> idsToSelect = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if (idsToSelect != null && !idsToSelect.isEmpty()) {
			builder.andCondition(CriteriaAPI.getIdCondition(idsToSelect, module));
		}
		
		JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		Boolean includeParentCriteria = (Boolean) context.get(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA);
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		} 
		if (( filters == null || includeParentCriteria) && view != null && view.getCriteria() != null && !view.getCriteria().isEmpty()) {
			builder.andCriteria(view.getCriteria());
		}
		
		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}
		if (AccountUtil.getCurrentUser() != null) { // temp handled for service portal space 
		
			Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
			if(scopeCriteria != null) {
				builder.andCriteria(scopeCriteria);
			}
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
		
		Boolean withReadings = (Boolean) context.get(FacilioConstants.ContextNames.WITH_READINGS);
		if (withReadings != null && withReadings) {
            builder.andCustomWhere("exists(select 1 from Reading_Data_Meta r where r.ORGID=? and r.RESOURCE_ID=Assets.ID and r.VALUE <> '-1' and r.VALUE IS NOT NULL)", AccountUtil.getCurrentOrg().getId());		
		}
		Long readingId = (Long) context.get(FacilioConstants.ContextNames.READING_ID);
		ReadingInputType inputType = (ReadingInputType) context.get(FacilioConstants.ContextNames.INPUT_TYPE);
		if (readingId != null && readingId > 0)
		{
			builder.andCustomWhere("exists(select 1 from Reading_Data_Meta r where r.ORGID=? and r.RESOURCE_ID=Assets.ID and r.FIELD_ID = ? and r.INPUT_TYPE = ? )",
					AccountUtil.getCurrentOrg().getId(), readingId, String.valueOf(inputType.getValue()));
		}
		// String.valueOf(inputType.getValue())
		long getStartTime = System.currentTimeMillis();
		List<AssetContext> records = builder.get();
		long getTimeTaken = System.currentTimeMillis() - getStartTime;
		LOGGER.debug("Time taken to execute Fetch assets in GetAssetListCommand : "+getTimeTaken);
		
		if (records != null && !records.isEmpty()) {
			if (getCount != null && getCount) {
				context.put(FacilioConstants.ContextNames.RECORD_COUNT, records.get(0).getData().get("count"));
			}
			else {
				AssetsAPI.loadAssetsLookups(records);
				context.put(FacilioConstants.ContextNames.RECORD_LIST, records);
			}
		}
		
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute GetAssetListCommand : "+timeTaken);
		return false;
	}
}
