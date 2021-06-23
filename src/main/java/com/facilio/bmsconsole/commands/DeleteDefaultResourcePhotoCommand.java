package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class DeleteDefaultResourcePhotoCommand extends FacilioCommand {


	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long recordId = -1;
		long photoId = -1;
		if(context.get(FacilioConstants.ContextNames.RECORD_ID) != null) {
			recordId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			photoId = (long) context.get(FacilioConstants.ContextNames.PHOTO_ID);
			ResourceContext resource = ResourceAPI.getResource(recordId);
			if (resource.getPhotoId() > 0 && photoId > 0) {
				if (resource.getPhotoId() != photoId) {
					return false;
				}
				else {
					
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
							.andCondition(idCondition)
							.orderBy("ID DESC");
					List<PhotosContext> photos = selectBuilder.get();
					
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

						
						if (photos != null && photos.size() > 0) {
							props.put("photoId", photos.get(0).getPhotoId());
						}
						else {
							props.put("photoId", -99);
						}
						updateBuilder.update(props);
					
				}
					
				return false;
			}
		}
		
		return false;
	}

}
