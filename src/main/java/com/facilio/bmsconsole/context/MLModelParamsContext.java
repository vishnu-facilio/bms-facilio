package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Data;

@Data
public class MLModelParamsContext extends ModuleBaseWithCustomFields {
	private long id;
	private String modelName;
	private Integer dataType;
	private Boolean isMandatory;
	private String keyName;
	private Object keyValue;
}
