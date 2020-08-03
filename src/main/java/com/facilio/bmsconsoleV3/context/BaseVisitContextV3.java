package com.facilio.bmsconsoleV3.context;

import java.io.File;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.VisitorTypeContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

public class BaseVisitContextV3 extends V3Context {
	
	private static final long serialVersionUID = 1L;

	private VisitorTypeContext visitorType;
    private V3ContactsContext host;
    private V3VisitorContext visitor;
    private Integer purposeOfVisit;
    private String purposeOfVisitEnum;
    
	private Long avatarId;
	private String avatarUrl;
	private File avatar;
	private String avatarFileName;
	private String avatarContentType;
	
	public Boolean isBlocked;
	public Boolean isVip;
	private VendorContext vendor;
    private Long sourceId;
    private BaseVisitContextV3.Source source;
    private PMTriggerContext trigger;
    private String qrUrl;
    private User requestedBy; 
    private Boolean isReturningVisitor;
    private String passCode;
    private V3TenantContext tenant;
    private String visitorName;
    private String visitorEmail;
    private String visitorPhone;
    private Boolean photoStatus;
    private Boolean hostStatus;
    private Boolean idProofScanned;
    private ChildVisitType childVisitType;
  
	public VisitorTypeContext getVisitorType() {
		return visitorType;
	}

	public void setVisitorType(VisitorTypeContext visitorType) {
		this.visitorType = visitorType;
	}
	 
	public V3ContactsContext getHost() {
		return host;
	}

	public void setHost(V3ContactsContext host) {
		this.host = host;
	}

	public V3VisitorContext getVisitor() {
		return visitor;
	}

	public void setVisitor(V3VisitorContext visitor) {
		this.visitor = visitor;
	}

	public Integer getPurposeOfVisit() {
		return purposeOfVisit;
	}

	public void setPurposeOfVisit(Integer purposeOfVisit) {
		this.purposeOfVisit = purposeOfVisit;
	}

	public String getPurposeOfVisitEnum() {
		return purposeOfVisitEnum;
	}

	public void setPurposeOfVisitEnum(String purposeOfVisitEnum) {
		this.purposeOfVisitEnum = purposeOfVisitEnum;
	}
	

	public Long getAvatarId() {
		return avatarId;
	}

	public void setAvatarId(Long avatarId) {
		this.avatarId = avatarId;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public File getAvatar() {
		return avatar;
	}

	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}

	public String getAvatarContentType() {
		return avatarContentType;
	}

	public void setAvatarContentType(String avatarContentType) {
		this.avatarContentType = avatarContentType;
	}

	public Boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public Boolean isBlocked() {
		if (isBlocked != null) {
			return isBlocked.booleanValue();
		}
		return false;
	}

	public Boolean getIsVip() {
		return isVip;
	}

	public void setIsVip(Boolean isVip) {
		this.isVip = isVip;
	}

	public Boolean isVip() {
		if (isVip != null) {
			return isVip.booleanValue();
		}
		return false;
	}

	public VendorContext getVendor() {
		return vendor;
	}

	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}
	
	public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getSource() {
        if (source != null) {
            return source.getIndex();
        }
        return null;
    }
    public void setSource(Integer source) {
        if(source != null) {
            this.source = BaseVisitContextV3.Source.valueOf(source);
        }
    }
    public BaseVisitContextV3.Source getSourceEnum() {
        return source;
    }
    
    public static enum Source implements FacilioEnum {
        WORKORDER, PURCHASE_ORDER, TENANT, MANUAL;

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name();
        }

        public static BaseVisitContextV3.Source valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }
    
    public PMTriggerContext getTrigger() {
        return trigger;
    }

    public void setTrigger(PMTriggerContext trigger) {
        this.trigger = trigger;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Boolean getIsReturningVisitor() {
        return isReturningVisitor;
    }

    public void setIsReturningVisitor(Boolean isReturning) {
        this.isReturningVisitor = isReturning;
    }
    
    public Boolean isReturningVisitor() {
        if (isReturningVisitor != null) {
            return isReturningVisitor.booleanValue();
        }
        return false;
    }
    
    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public V3TenantContext getTenant() {
        return tenant;
    }
    public void setTenant(V3TenantContext tenant) {
        this.tenant = tenant;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorEmail() {
        return visitorEmail;
    }

    public void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }

    public String getVisitorPhone() {
        return visitorPhone;
    }

    public void setVisitorPhone(String visitorPhone) {
        this.visitorPhone = visitorPhone;
    }
    
    public Boolean getPhotoStatus() {
        if (photoStatus == null) {
            return false;
        }
        return photoStatus;
    }
    public void setPhotoStatus(Boolean photoStatus) {
        this.photoStatus = photoStatus;
    }

    public Boolean getHostStatus() {
        if (hostStatus == null) {
            return false;
        }
        return hostStatus;
    }
    public void setHostStatus(Boolean hostStatus) {
        this.hostStatus = hostStatus;
    }

    public Boolean getIdProofScanned() {
        if (idProofScanned == null) {
            return false;
        }
        return idProofScanned;
    }
    public void setIdProofScanned(Boolean idProofScanned) {
        this.idProofScanned = idProofScanned;
    }
    
    public ChildVisitType getChildVisitTypeEnum() {
		return childVisitType;
	}

	public void setChildVisitType(ChildVisitType childVisitType) {
		this.childVisitType = childVisitType;
	}

	public int getChildVisitType() {
		if(childVisitType != null) {
			return childVisitType.getIndex();
		}
		return -1;
	}
	public void setChildVisitType(int childVisitType) {
		if (childVisitType > 0) {
			this.childVisitType = ChildVisitType.values()[childVisitType - 1];
		}
		else {
			this.childVisitType = null;
		}
	}
	
	public enum ChildVisitType implements FacilioEnum{	
		INVITE,
		VISIT,	
		;
		
		public int getIndex() {
			return ordinal()+1;
		}
		
		public String getValue() {
	        return name();
	    }

		public static ChildVisitType valuOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
