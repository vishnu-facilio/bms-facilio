package com.facilio.bmsconsole.context;

public class ShiftRotationDetailsContext {
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
