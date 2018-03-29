package com.facilio.bmsconsole.commands;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.amazonaws.services.rekognition.model.TextDetection;
import com.facilio.constants.FacilioConstants;
import com.facilio.image.ImageRecognitionUtil;

public class FilterTextsFromImageCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TextDetection> detectedTexts = (List<TextDetection>) context.get(FacilioConstants.ContextNames.PHOTO_TEXTS);
		if (detectedTexts != null && !detectedTexts.isEmpty()) {
			Iterator<TextDetection> it = detectedTexts.iterator();
			while (it.hasNext()) {
				TextDetection text = it.next();
				if (text.getConfidence() < ImageRecognitionUtil.MINIMUM_CONFIDENCE || !text.getType().equals("LINE") || !isNumber(text.getDetectedText())) {
					it.remove();
				}
			}
		}
		return false;
	}
	
	private boolean isNumber(String text) {
		try {
			Double.parseDouble(text);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

}
