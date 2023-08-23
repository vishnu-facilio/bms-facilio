package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateMeterResourcePhotoCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		long photoId = -1L;

		List<PhotosContext> photos = (List) context.get(FacilioConstants.ContextNames.PHOTOS);
		if (photos != null && photos.size() > 0) {
			photoId = photos.get(0).getPhotoId();
		}

		if (photoId > 0) {
			FacilioModule resourceModule = modBean.getModule("resource");
			
			FacilioField photoIdField = new FacilioField();
			photoIdField.setName("photoId");
			photoIdField.setDataType(FieldType.NUMBER);
			photoIdField.setColumnName("PHOTO_ID");
			photoIdField.setModule(resourceModule);
			
			List<FacilioField> baseSpaceFields = new ArrayList<>();
			baseSpaceFields.add(photoIdField);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(resourceModule.getTableName())
					.fields(baseSpaceFields)
					.andCustomWhere(resourceModule.getTableName()+".ID = ?", recordId);	

			Map<String, Object> props = new HashMap<>();
			props.put("photoId", photoId);

			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, updateBuilder.update(props));
		}
		return false;
	}
}