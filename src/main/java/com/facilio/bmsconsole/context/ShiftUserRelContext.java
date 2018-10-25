package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ShiftUserRelContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long shiftId;
	private long ouid;
	
	public long getShiftId() {
		return shiftId;
	}
	public void setShiftId(long shiftId) {
		this.shiftId = shiftId;
	}
	public long getOuid() {
		return ouid;
	}
	public void setOuid(long ouid) {
		this.ouid = ouid;
	}

}