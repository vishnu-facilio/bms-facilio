package com.facilio.bmsconsoleV3.context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailToModuleDataContext extends BaseMailMessageContext {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Long dataModuleId;
	Long recordId;
	BaseMailMessageContext parentBaseMail;
}
