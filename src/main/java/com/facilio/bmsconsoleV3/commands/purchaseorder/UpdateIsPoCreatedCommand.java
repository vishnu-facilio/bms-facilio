package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UpdateIsPoCreatedCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PurchaseOrderContext> purchaseOrderContexts = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST);
        if (CollectionUtils.isNotEmpty(purchaseOrderContexts)) {
            Collection<JSONObject> jsonList = new ArrayList<>();

            for(V3PurchaseOrderContext po : purchaseOrderContexts) {
                if(CollectionUtils.isNotEmpty(po.getPrIds())){
                    for(Long prId : po.getPrIds()) {
                        V3PurchaseRequestContext pr = (V3PurchaseRequestContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.PURCHASE_REQUEST, prId, V3PurchaseRequestContext.class);
                        if (pr != null) {
                            Map<String, Object> map = FieldUtil.getAsProperties(pr);
                            map.put("isPoCreated", true);
                            JSONObject json = new JSONObject();
                            json.putAll(map);
                            jsonList.add(json);
                        }
                    }
                }
                if(CollectionUtils.isNotEmpty(jsonList)) {
                    FacilioChain patchChain = ChainUtil.getBulkPatchChain(FacilioConstants.ContextNames.PURCHASE_REQUEST);
                    FacilioContext patchContext = patchChain.getContext();
                    V3Config v3Config = ChainUtil.getV3Config(FacilioConstants.ContextNames.PURCHASE_REQUEST);
                    Class beanClass = ChainUtil.getBeanClass(v3Config, module);

                    Constants.setModuleName(patchContext, FacilioConstants.ContextNames.PURCHASE_REQUEST);
                    Constants.setBulkRawInput(patchContext, (Collection) jsonList);
                    patchContext.put(Constants.BEAN_CLASS, beanClass);
                    patchContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
                    patchContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
                    patchChain.execute();
                }

            }

        }
        return false;
    }
}
