package com.facilio.bmsconsole.placeholder.enums;

import com.facilio.modules.FacilioIntEnum;

public enum PlaceholderSourceType implements FacilioIntEnum {

	SCRIPT,
	FORM_RULE
	;

	public static PlaceholderSourceType valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
	
}
