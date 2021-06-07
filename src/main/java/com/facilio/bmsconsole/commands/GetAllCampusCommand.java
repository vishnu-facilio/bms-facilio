package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAllCampusCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>().table(dataTableName)
				.moduleName(moduleName).beanClass(SiteContext.class).select(fields)
				.fetchSupplement((LookupField) fieldMap.get("location"))
				.maxLevel(2)
				.orderBy(fieldMap.get("name").getColumnName() + " ASC");
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
		if (scopeCriteria != null) {
			builder.andCriteria(scopeCriteria);
		}
		boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
		if (skipModuleCriteria) {
			builder.skipModuleCriteria();
		}


		List<SiteContext> campuses = builder.get();
		List<Long> spaceId = new ArrayList<Long>();
		List<Long> businessHourIds = new ArrayList<Long>();
		Map<Long, Long> spaceBhIdsMap = new HashMap<Long, Long>();
		if (campuses != null) {
			campuses.forEach((e) -> {
				long spaceid = e.getId();
				spaceId.add(spaceid);
				if (e.getOperatingHour() != -1) {
					long bhid =e.getOperatingHour();
						businessHourIds.add(bhid);
						spaceBhIdsMap.put(spaceid, bhid);
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
		
		for (SiteContext site : campuses) {
			List<PhotosContext> photos = SpaceAPI.getBaseSpacePhotos(site.getId());
			if (photos != null && !photos.isEmpty()) {
				PhotosContext photo = photos.get(0);
				if (photo != null) {
					site.setPhotoId(photo.getPhotoId());
				}
			}
		}
		context.put(FacilioConstants.ContextNames.SITE_LIST, campuses);

		return false;
	}

}
