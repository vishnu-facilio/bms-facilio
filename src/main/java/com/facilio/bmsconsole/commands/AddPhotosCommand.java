package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class AddPhotosCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PhotosContext> photos = (List<PhotosContext>) context.get(FacilioConstants.ContextNames.PHOTOS);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		if(photos != null && !photos.isEmpty() && moduleName != null && !moduleName.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			for(PhotosContext photo : photos) {
				if(photo.getTtime() == -1) {
					photo.setTtime(System.currentTimeMillis());
				}
				
				if(photo.getParentId() == -1) {
					throw new IllegalArgumentException("Invalid parent id for readings of module : "+moduleName);
				}
			}
			
			InsertRecordBuilder<PhotosContext> photosBuilder = new InsertRecordBuilder<PhotosContext>()
																		.module(module)
																		.fields(fields)
																		.addRecords(photos);
			
			photosBuilder.save();
			
			context.put(FacilioConstants.ContextNames.PHOTOS, photos);
		}
		
		return false;
	}

}
