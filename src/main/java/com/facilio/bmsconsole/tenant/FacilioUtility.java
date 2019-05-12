package com.facilio.bmsconsole.tenant;

import com.facilio.beans.ModuleBean;
import com.facilio.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public enum FacilioUtility {

	ENERGY {
		@Override
		public FacilioField getReadingField() throws Exception {
			// TODO Auto-generated method stub
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			return modBean.getField("totalEnergyConsumptionDelta", FacilioConstants.ContextNames.ENERGY_DATA_READING);
		}
	},
	WATER {
		@Override
		public FacilioField getReadingField() throws Exception {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			return modBean.getField("waterConsumptionDelta", FacilioConstants.ContextNames.WATER_READING);
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
	
	public static FacilioUtility valueOf (int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}

}
