package com.facilio.bmsconsole.context;

public class BimIntegrationLogsContext{
	/**
	 * 
	 */

	private long id;
	private long fileId;
	private long uploadedBy;
    private int noOfModules;
    private long importedTime;
    private String fileName;
	private String siteName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public int getNoOfModules() {
		return noOfModules;
	}

	public void setNoOfModules(int noOfModules) {
		this.noOfModules = noOfModules;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public long getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(long uploadedBy) {
		this.uploadedBy = uploadedBy;
	}
	
	public long getImportedTime() {
		return importedTime;
	}

	public void setImportedTime(long importedTime) {
		this.importedTime = importedTime;
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
	
	private ThirdParty thirdParty;

	public ThirdParty getThirdPartyEnum() {
		return thirdParty;
	}

	public void setThirdParty(ThirdParty thirdParty) {
		this.thirdParty = thirdParty;
	}

	public int getThirdParty() {
		if (thirdParty != null) {
			return thirdParty.getValue();
		}
		return -1;
	}

	public void setThirdParty(int thirdParty) {
		this.thirdParty = ThirdParty.valueOf(thirdParty);
	}

	public enum ThirdParty {
		INTERNAL,
		INVICARA,
		YOUBIM;

		public int getValue() {
			return ordinal() + 1;
		}

		public static ThirdParty valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}