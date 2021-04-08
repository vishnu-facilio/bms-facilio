package com.facilio.bmsconsoleV3.util;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;

public class V3Util {

	
	public static FacilioContext createRecord(FacilioModule module,JSONObject data) throws Exception {
		V3Config v3Config = ChainUtil.getV3Config(module.getName());
        FacilioChain createRecordChain = ChainUtil.getCreateRecordChain(module.getName());
        FacilioContext contextNew = createRecordChain.getContext();

        if (module.isCustom()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioField localIdField = modBean.getField("localId", module.getName());
            if (localIdField != null) {
            	contextNew.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
            }
        }

        Constants.setV3config(contextNew, v3Config);
        contextNew.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
        Constants.setModuleName(contextNew, module.getName());
        contextNew.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
        Constants.setRawInput(contextNew, data);
        
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        contextNew.put(Constants.BEAN_CLASS, beanClass);
        
        createRecordChain.execute();
        
        
        return contextNew;
	}
}
