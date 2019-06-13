package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ShiftRotationDetailsContext extends ModuleBaseWithCustomFields{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long shiftRotationId;

	public long getShiftRotationId() {
		return shiftRotationId;
	}

	public void setShiftRotationId(long shiftRotationId) {
		this.shiftRotationId = shiftRotationId;
	}

	private long fromShiftId;

	public long getFromShiftId() {
		return fromShiftId;
	}

	public void setFromShiftId(long fromShiftId) {
		this.fromShiftId = fromShiftId;
	}

	private long toShiftId;

	public long getToShiftId() {
		return toShiftId;
	}

	public void setToShiftId(long toShiftId) {
		this.toShiftId = toShiftId;
	}
}
