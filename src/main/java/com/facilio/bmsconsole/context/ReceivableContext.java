package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ReceivableContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private long poId = -1;
	public long getPoId() {
		return poId;
	}
	public void setPoId(long poId) {
		this.poId = poId;
	}
	
	private Status status;
	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}
	public Status getStatusEnum() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}
	
	public static enum Status {
		YET_TO_RECEIVE,
		PARTIALLY_RECEIVED,
		RECEIVED,
		COMPLETED;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
