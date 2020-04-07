package com.facilio.bmsconsole.context;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class OperationAlarmContext extends  BaseAlarmContext {
	private static final Logger LOGGER = LogManager.getLogger(OperationAlarmContext.class.getName());

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

	private FacilioField readingField;
	public FacilioField getReadingField(){
		try {
			if(readingField == null && readingFieldId > 0) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				readingField = modBean.getField(readingFieldId);
			}
		}
		catch(Exception e) {
			LOGGER.error("Error while fetching reading fieldid : "+readingFieldId, e);
		}
		return readingField;
	}
	public void setReadingField(FacilioField readingField) {
		this.readingField = readingField;
	}

	private long readingFieldId = -1;
	public long getReadingFieldId() {
		return readingFieldId;
	}
	public void setReadingFieldId(long readingFieldId) {
		this.readingFieldId = readingFieldId;
	}
	

}
