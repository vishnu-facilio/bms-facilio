package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReadingFieldsCommand implements Command {

	private static final  List<String> DEFAULT_READING_FIELDS = Collections.unmodifiableList(getDefaultReadingFieldNames());
	private static final List<String> getDefaultReadingFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		List<FacilioField> fields = FieldFactory.getDefaultReadingFields(null);
		for(FacilioField field : fields) {
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}
	
	private List<FacilioField> excludeDefaultReadingFields(List<FacilioField> fields) {
		return fields.stream().filter(field -> !DEFAULT_READING_FIELDS.contains(field.getName())).collect(Collectors.toList());
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioModule> readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if(readings != null && !readings.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(FacilioModule reading : readings) {
				List<FacilioField> dataPoints = excludeDefaultReadingFields(modBean.getAllFields(reading.getName()));
				reading.setFields(dataPoints);
			}
		}
		return false;
	}
	
	

}
