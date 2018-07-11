package com.facilio.bmsconsole.tenant;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.fw.BeanFactory;

public enum TenantUtility {

	ENERGY {
		@Override
		public FacilioField getReadingField() throws Exception {
			// TODO Auto-generated method stub
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			return modBean.getField("totalEnergyConsumptionDelta", "energydata");
		}
	},
	WATER {
		@Override
		public FacilioField getReadingField() {
			// TODO Auto-generated method stub
			return null;
		}
	},
	NATURAL_GAS {
		@Override
		public FacilioField getReadingField() {
			// TODO Auto-generated method stub
			return null;
		}
	},
	BTU {
		@Override
		public FacilioField getReadingField() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	;
	
	public abstract FacilioField getReadingField() throws Exception;
	
	public int getValue() {
		return ordinal() + 1;
	}
	
	public static TenantUtility valueOf (int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}

}
