package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class GetAllSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		
		Long siteId = (Long) context.get(FacilioConstants.ContextNames.SITE_ID);
		Long buildingId = (Long) context.get(FacilioConstants.ContextNames.BUILDING_ID);
		Long floorId = (Long) context.get(FacilioConstants.ContextNames.FLOOR_ID);
		Long categoryId = (Long) context.get(FacilioConstants.ContextNames.SPACE_CATEGORY);
		
		
		SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
				.table(dataTableName)
				.moduleName(moduleName)
				.beanClass(SpaceContext.class)
				.select(fields)
				.orderBy("ID");
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		if (filterCriteria != null) {
			builder.andCriteria(filterCriteria);
		}
		
		if (siteId != null && siteId > 0) {
			builder.andCustomWhere("BaseSpace.SITE_ID = ?", siteId);
		}
		else if (buildingId != null && buildingId > 0) {
			builder.andCustomWhere("BaseSpace.BUILDING_ID = ?", buildingId);
		}
		else if (floorId != null && floorId > 0) {
			builder.andCustomWhere("BaseSpace.FLOOR_ID = ?", floorId);
		}
		
		if (categoryId != null && categoryId > 0) {
			builder.andCustomWhere("Space.SPACE_CATEGORY_ID = ?", categoryId);
		}
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria(moduleName);
		if(scopeCriteria != null)
		{
			builder.andCriteria(scopeCriteria);
		}
		boolean skipModuleCriteria = (boolean) context.getOrDefault(FacilioConstants.ContextNames.SKIP_MODULE_CRITERIA, false);
		if (skipModuleCriteria) {
			builder.skipModuleCriteria();
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

		Criteria searchCriteria = (Criteria) context.get(FacilioConstants.ContextNames.SEARCH_CRITERIA);
		if (searchCriteria != null) {
			builder.andCriteria(searchCriteria);
		}

		List<SpaceContext> spaces = builder.get();
		for (SpaceContext space : spaces) {
			List<PhotosContext> photos = SpaceAPI.getBaseSpacePhotos(space.getId());
			if (photos != null && !photos.isEmpty()) {
				PhotosContext photo = photos.get(0);
				if (photo != null) {
					space.setPhotoId(photo.getPhotoId());
				}
			}
		}
		context.put(FacilioConstants.ContextNames.SPACE_LIST, spaces);
		
		return false;
	}

}
