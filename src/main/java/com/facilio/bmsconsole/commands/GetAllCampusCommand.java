package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.constants.FacilioConstants;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllCampusCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>().table(dataTableName)
				.moduleName(moduleName).beanClass(SiteContext.class).select(fields).maxLevel(2)
				.orderBy(fieldMap.get("name").getColumnName() + " ASC");
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria(moduleName);
		if (scopeCriteria != null) {
			builder.andCriteria(scopeCriteria);
		}

		List<SiteContext> campuses = builder.get();
		List<Long> spaceId = new ArrayList<Long>();
		List<Long> businessHourIds = new ArrayList<Long>();
		Map<Long, Long> spaceBhIdsMap = new HashMap<Long, Long>();
		if (campuses != null) {
			campuses.forEach((e) -> {
				long spaceid = e.getId();
				spaceId.add(spaceid);
				if (e.getData() != null) {
					if (e.getData().get("operatingHour") != null) {
						long bhid = Long.parseLong(e.getData().get("operatingHour").toString());
						businessHourIds.add(bhid);
						spaceBhIdsMap.put(spaceid, bhid);
					}
				}
			});
		}
		List<BusinessHoursContext> businessHourList = BusinessHoursAPI.getBusinessHours(businessHourIds);
		if (campuses != null) {
			campuses.forEach((e) -> {
				if (spaceBhIdsMap.get(e.getId()) != null) {
					e.setBusinessHour(businessHourList.stream().filter(bh -> spaceBhIdsMap.get(e.getId()).equals(bh.getId())).findFirst().get());
				}
			});
		}
		context.put(FacilioConstants.ContextNames.SITE_LIST, campuses);

		return false;
	}

}
