package com.facilio.bmsconsole.context;

public class BimImportProcessMappingContext{
	/**
	 * 
	 */

	private Long id;
	private Long bimId;
	private Long importProcessId;
	private String sheetName;
	private String moduleName;
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBimId() {
		return bimId;
	}

	public void setBimId(Long bimId) {
		this.bimId = bimId;
	}

	public Long getImportProcessId() {
		return importProcessId;
	}

	public void setImportProcessId(Long importProcessId) {
		this.importProcessId = importProcessId;
	}

	private Status status;

	public Status getStatusEnum() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getStatus() {
		if (status != null) {
			return status.getValue();
		}
		return -1;
	}

	public void setStatus(int status) {
		this.status = Status.valueOf(status);
	}

	public enum Status {
		INPROGRESS,
		COMPLETED,
		FAILED;

		public int getValue() {
			return ordinal() + 1;
		}

		public static Status valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}