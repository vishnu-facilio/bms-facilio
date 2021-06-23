package com.facilio.bmsconsole.commands;

import java.io.File;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.image.ImageRecognitionUtil;

public class GetTextsFromImageCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
