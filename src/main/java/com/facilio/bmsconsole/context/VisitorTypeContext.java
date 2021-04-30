package com.facilio.bmsconsole.context;

import java.util.Map;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class VisitorTypeContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Boolean enabled;
	
	  
	  private VisitorLogo visitorLogo;
	  @JsonFormat(shape = JsonFormat.Shape.OBJECT)
	  public static enum VisitorLogo implements FacilioIntEnum {
	        GUEST("kiosk-guest"),
	        VENDOR("kiosk-vendor"),
	        EMPLOYEE("kiosk-employee"),
	        CUSTOM("kiosk-custom"),
	        DELIVERY("kiosk-delivery"),
	        EMPLOYEE_2("employee"),
	        ELECTRICIAN("electrician"),
	        STUDENT("student"),
	        DOCTOR("doc"),
	        MESSAGE("kiosk-message")
	        ;

	        private String name;
	        VisitorLogo (String name) {
	            this.name = name;
	        }

	        @Override
	        public Integer getIndex() {
	            return ordinal() + 1;
	        }

	        @Override
	        public String getValue() {
	            return name;
	        }

	        public static VisitorLogo valueOf(int value) {
	            if (value > 0 && value <= values().length) {
	                return values()[value - 1];
	            }
	            return null;
	        }
	    }
	

		public VisitorLogo getVisitorLogoEnum() {
			return visitorLogo;
		}
		
		public int getVisitorLogo() {
			if(visitorLogo!=null)
			{
				return visitorLogo.getIndex();
			}
			return -1;
		}
		@JsonIgnore
		public Map<String, Object> getVisitorLogoObj() throws Exception {
			return FieldUtil.getAsProperties(visitorLogo);
		}
		

		public void setVisitorLogo(int visitorLogo) {
			this.visitorLogo = VisitorLogo.valueOf(visitorLogo);
		}
		public void setVisitorLog(VisitorLogo visitorLogo) {
			this.visitorLogo = visitorLogo;
		}

	
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
	
}
