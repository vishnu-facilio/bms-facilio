package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.modules.ModuleBaseWithCustomFields;

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
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean containsTime(long currentTime) {
		if ((startTime == ShiftAPI.UNLIMITED_PERIOD || startTime <= currentTime) 
				&& (endTime == ShiftAPI.UNLIMITED_PERIOD || endTime >= currentTime)) {
			return true;
		}
		return false;
	}

}