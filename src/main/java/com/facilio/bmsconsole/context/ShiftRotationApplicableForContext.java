package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ShiftRotationApplicableForContext extends ModuleBaseWithCustomFields{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long applicableForId;

	public long getApplicableForId() {
		return applicableForId;
	}

	public void setApplicableForId(long applicableForId) {
		this.applicableForId = applicableForId;
	}

	private ApplicableFor applicableForType;

	public ApplicableFor getApplicableForTypeEnum() {
		return applicableForType;
	}

	public void setApplicableForType(ApplicableFor applicableForType) {
		this.applicableForType = applicableForType;
	}

	public int getApplicableForType() {
		if (applicableForType != null) {
			return applicableForType.getValue();
		}
		return -1;
	}

	public void setApplicableForType(int applicableForType) {
		this.applicableForType = ApplicableFor.valueOf(applicableForType);
	}
	
	public enum ApplicableFor {
		USERS,
		GROUPS,
		ROLES;
		
		public int getValue() {
			return ordinal() + 1;
		}

		public static ApplicableFor valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	private long shiftRotationId;

	public long getShiftRotationId() {
		return shiftRotationId;
	}

	public void setShiftRotationId(long shiftRotationId) {
		this.shiftRotationId = shiftRotationId;
	}
}
