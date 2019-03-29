package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.image.ImageRecognitionUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.io.File;

public class GetTextsFromImageCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long photoId = (Long) context.get(FacilioConstants.ContextNames.PHOTO_ID);
		if (photoId != null && photoId != -1) {
			context.put(FacilioConstants.ContextNames.PHOTO_TEXTS, ImageRecognitionUtil.getTexts(photoId));
		}
		else {
			File photo = (File) context.get(FacilioConstants.ContextNames.PHOTO);
			if (photo != null) {
				context.put(FacilioConstants.ContextNames.PHOTO_TEXTS, ImageRecognitionUtil.getTexts(photo));
			}
		}
		return false;
	}

}
