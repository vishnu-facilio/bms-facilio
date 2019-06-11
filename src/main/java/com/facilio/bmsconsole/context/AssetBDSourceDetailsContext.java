package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class AssetBDSourceDetailsContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	private long id = -1;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private long parentId = -1;

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	private long assetid = -1;

	public long getAssetid() {
		return assetid;
	}

	public void setAssetid(long assetid) {
		this.assetid = assetid;
	}

	private String condition;

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	private long fromtime = -1;

	public long getFromtime() {
		return fromtime;
	}

	public void setFromtime(long fromtime) {
		this.fromtime = fromtime;
	}

	private long totime = -1;

	public long getTotime() {
		return totime;
	}

	public void setTotime(long totime) {
		this.totime = totime;
	}
	private long sourceId = -1;

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
	private long sourceType = -1;
	
	public long getSourceType() {
		if (sourceTypeEnum != null) {
			return sourceTypeEnum.getValue();
		} else {
			return sourceType;
		}
	}

	public void setSourceType(long sourceType) {
		this.sourceType = sourceType;
	}

	private SourceType sourceTypeEnum;

	public SourceType getSourceTypeEnum() {
		return sourceTypeEnum;
	}

	public void setSourceTypeEnum(SourceType sourceTypeEnum) {
		this.sourceTypeEnum = sourceTypeEnum;
	}

	public static enum SourceType {
		WORKORDER, ALARM, ASSET;

		public int getValue() {
			return ordinal() + 1;
		}

		public static SourceType valueOf(int val) {
			if (val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}
}
