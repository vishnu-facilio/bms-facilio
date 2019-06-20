package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.User;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class NotificationUserContext extends ModuleBaseWithCustomFields{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long configId;
	private User toUser;
	public long getConfigId() {
		return configId;
	}
	public void setConfigId(long configId) {
		this.configId = configId;
	}
	public User getToUser() {
		return toUser;
	}
	public void setToUser(User toUser) {
		this.toUser = toUser;
	}
	
	

}
