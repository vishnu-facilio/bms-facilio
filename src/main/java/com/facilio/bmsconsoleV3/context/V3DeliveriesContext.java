package com.facilio.bmsconsoleV3.context;

import java.io.File;

import com.facilio.v3.context.V3Context;

public class V3DeliveriesContext extends V3Context{

    private static final long serialVersionUID = 1L;
    
    private String trackingNumber;
    
    public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

    private V3EmployeeContext employee;

    public V3EmployeeContext getEmployee() {
		return employee;
	}
	public void setEmployee(V3EmployeeContext employee) {
		this.employee = employee;
	}
	
	private V3DeliveryAreaContext deliveryArea;

    public V3DeliveryAreaContext getDeliveryArea() {
		return deliveryArea;
	}
	public void setDeliveryArea(V3DeliveryAreaContext deliveryArea) {
		this.deliveryArea = deliveryArea;
	}
	
	private Long receivedTime;
	
	public Long getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Long receivedTime) {
        this.receivedTime = receivedTime;
    }
    
    private Long deliveredTime;
	
	public Long getDeliveredTime() {
        return deliveredTime;
    }

    public void setDeliveredTime(Long deliveredTime) {
        this.deliveredTime = deliveredTime;
    }
    
    private Long avatarId;
    public Long getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Long avatarId) {
        this.avatarId = avatarId;
    }

    private String avatarUrl;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    private File avatar;
    public File getAvatar() {
        return avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }

    private String avatarFileName;
    public String getAvatarFileName() {
        return avatarFileName;
    }

    public void setAvatarFileName(String avatarFileName) {
        this.avatarFileName = avatarFileName;
    }

    private  String avatarContentType;

    public String getAvatarContentType() {
        return avatarContentType;
    }

    public void setAvatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
    }
    
    private Long signatureId;
    private String signatureUrl;
    private File signature;

    private String signatureFileName;
    private  String signatureContentType;

    public Long getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(Long signatureId) {
        this.signatureId = signatureId;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public File getSignature() {
        return signature;
    }

    public void setSignature(File signature) {
        this.signature = signature;
    }

    public String getSignatureFileName() {
        return signatureFileName;
    }

    public void setSignatureFileName(String signatureFileName) {
        this.signatureFileName = signatureFileName;
    }

    public String getSignatureContentType() {
        return signatureContentType;
    }

    public void setSignatureContentType(String signatureContentType) {
        this.signatureContentType = signatureContentType;
    }
}
