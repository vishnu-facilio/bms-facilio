package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetMeterPhotoCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		String moduleName = FacilioConstants.ContextNames.RESOURCE;

		long parentId = (long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
			
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
		Condition idCondition = new Condition();
		idCondition.setField(modBean.getField("id", moduleName));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(String.valueOf(parentId));

		SelectRecordsBuilder<V3ResourceContext> selectBuilder = new SelectRecordsBuilder<V3ResourceContext>()
				.moduleName(moduleName)
				.beanClass(V3ResourceContext.class)
				.select(Collections.singletonList(fieldMap.get("photoId")))
				.table(module.getTableName())
				.andCondition(idCondition);

		List<V3ResourceContext> photoList = selectBuilder.get();
		if (photoList != null && !photoList.isEmpty()) {
			List<PhotosContext> photos = new ArrayList<>();
			if(photoList.get(0).getPhotoId() != null && photoList.get(0).getPhotoId()>0) {
				PhotosContext photo =  new PhotosContext();
				photo.setPhotoId(photoList.get(0).getPhotoId());
				photo.setParentId(photoList.get(0).getId());
				photos.add(photo);
			}
			context.put(FacilioConstants.ContextNames.PHOTOS, photos);
		}
		return false;
	}

}
