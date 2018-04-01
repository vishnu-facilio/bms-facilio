package com.facilio.bmsconsole.commands;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.amazonaws.services.rekognition.model.TextDetection;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.image.ImageRecognitionUtil;

public class FilterReadingsFromImageCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Boolean filter = (Boolean) context.get(FacilioConstants.ContextNames.FILTERS);
		if (filter != null && !filter) {
			return false;
		}
		
		List<TextDetection> detectedTexts = (List<TextDetection>) context.get(FacilioConstants.ContextNames.PHOTO_TEXTS);
		if (detectedTexts != null && !detectedTexts.isEmpty()) {
			long readingFieldId = (long) context.get(FacilioConstants.ContextNames.READING_FIELD);
			FacilioField readingField = null;
			if (readingFieldId != -1) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				readingField = modBean.getField(readingFieldId);
			}
			double previousValue = (double) context.get(FacilioConstants.ContextNames.PREVIOUS_VALUE);
			
			Iterator<TextDetection> it = detectedTexts.iterator();
			while (it.hasNext()) {
				TextDetection text = it.next();
				if (text.getConfidence() < ImageRecognitionUtil.MINIMUM_CONFIDENCE || !text.getType().equals("LINE") || !isNumber(text.getDetectedText())) {
					it.remove();
					continue;
				}
				
				if (readingField != null) {
					if (readingField.getModule().getName().equals(FacilioConstants.ContextNames.ENERGY_DATA_READING) && readingField.getName().equals("totalEnergyConsumption") && previousValue != -1) {
						double currentValue = Double.parseDouble(text.getDetectedText());
						if (currentValue < previousValue) {
							it.remove();
						}
					}
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
