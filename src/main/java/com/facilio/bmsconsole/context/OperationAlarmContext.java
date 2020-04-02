package com.facilio.bmsconsole.context;
import com.facilio.modules.FacilioEnum;
public class OperationAlarmContext extends  BaseAlarmContext {
	
	private static final long serialVersionUID = 1L;
//	private long assetid;
//
//	public void setassetid(long assetid)
//	{
//		this.assetid = assetid;
//	}
//
//	public long getassetid()
//	{
//		return assetid;
//	}
	private CoverageType coverageType;
	public int getCoverageType() {
		if (coverageType == null) {
			return -1;
		}
		return coverageType.getIndex();
	}

	public CoverageType getCoverageTypeEnum() {
		return coverageType;
	}
	public void setCoverageType(CoverageType coverageType) {
		this.coverageType = coverageType;
	}
	public void setCoverageType(int coverageType) {
		this.coverageType = CoverageType.valueOf(coverageType);
	}

	public static enum CoverageType implements FacilioEnum {
		EXCEEDED_SCHEDULE,
		SHORT_OF_SCHEDULE
		;
		public int getIndex() {
			return ordinal() + 1;
		}

		@Override
		public String getValue() {
			return name();
		}

		public static OperationAlarmContext.CoverageType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	

}
