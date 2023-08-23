package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.util.V3ResourceAPI;
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

public class DeleteMeterResourcePhotoCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long recordId = -1;
		long photoId = -1;
		if(context.get(FacilioConstants.ContextNames.RECORD_ID) != null) {
			recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			photoId = (long) context.get(FacilioConstants.ContextNames.PHOTO_ID);
			V3ResourceContext resource = V3ResourceAPI.getResource(recordId);
			if (resource.getPhotoId() > 0 && photoId > 0) {
				if (resource.getPhotoId() != photoId) {
					return false;
				}
				else {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					
						FacilioModule resourceModule = modBean.getModule("resource");
						FacilioField photoIdField = new FacilioField();
						photoIdField.setName("photoId");
						photoIdField.setDataType(FieldType.NUMBER);
						photoIdField.setColumnName("PHOTO_ID");
						photoIdField.setModule(resourceModule);
						
						List<FacilioField> baseSpaceFields = new ArrayList<>();
						baseSpaceFields.add(photoIdField);
						Map<String, Object> props = new HashMap<>();
						
						GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
								.table(resourceModule.getTableName())
								.fields(baseSpaceFields)
								.andCustomWhere(resourceModule.getTableName()+".ID = ?", recordId);	

						props.put("photoId", -99);
						updateBuilder.update(props);
					
				}
					
				return false;
			}
		}
		
		return false;
	}

}
