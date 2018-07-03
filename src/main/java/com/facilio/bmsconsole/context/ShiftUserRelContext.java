package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ShiftUserRelContext extends ModuleBaseWithCustomFields {
	private long shiftId;
	private long userId;
	
	public long getShiftId() {
		return shiftId;
	}
	public void setShiftId(long shiftId) {
		this.shiftId = shiftId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

}