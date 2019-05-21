package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateDefaultSpacePhotoCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub

		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);

		Condition idCondition = new Condition();
		idCondition.setField(modBean.getField("parentId", moduleName));
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(String.valueOf(recordId));

		SelectRecordsBuilder<PhotosContext> selectBuilder = new SelectRecordsBuilder<PhotosContext>()
				.moduleName(moduleName)
				.beanClass(PhotosContext.class)
				.select(fields)
				.table(module.getTableName())
				.andCondition(idCondition);

		long photoId = -1L;

		List<PhotosContext> photos = selectBuilder.get();
		if (photos != null && photos.size() > 0) {
			photoId = photos.get(0).getPhotoId();
		}

		if (photoId > 0) {
			FacilioModule baseSpaceModule = modBean.getModule("basespace");
			
			FacilioField photoIdField = new FacilioField();
			photoIdField.setName("photoId");
			photoIdField.setDataType(FieldType.NUMBER);
			photoIdField.setColumnName("PHOTO_ID");
			photoIdField.setModule(baseSpaceModule);
			
			List<FacilioField> baseSpaceFields = new ArrayList<>();
			baseSpaceFields.add(photoIdField);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(baseSpaceModule.getTableName())
					.fields(baseSpaceFields)
					.andCustomWhere(baseSpaceModule.getTableName()+".ID = ?", recordId);	

			Map<String, Object> props = new HashMap<>();
			props.put("photoId", photoId);

			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(props));
		}
		return false;
	}
}