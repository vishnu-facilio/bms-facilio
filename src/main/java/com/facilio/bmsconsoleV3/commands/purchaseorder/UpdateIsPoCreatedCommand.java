package com.facilio.bmsconsoleV3.commands.purchaseorder;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.util.PurchaseOrderAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3TermsAndConditionContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PoAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.PrAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.purchaserequest.V3PurchaseRequestContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
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
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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
                            map.put("purchaseOrder", FieldUtil.getAsProperties(po));
                            JSONObject json = new JSONObject();
                            json.putAll(map);
                            jsonList.add(json);
                        }
                    }
                    List<PrAssociatedTermsContext> terms = PurchaseOrderAPI.fetchTermsForConvertPo(po.getPrIds());
                    if (CollectionUtils.isNotEmpty(terms)) {
                        List<Long> uniqueTermsIds = terms.stream().filter(term -> QuotationAPI.lookupValueIsNotEmpty(term.getTerms())).map(term -> term.getTerms().getId()).distinct().collect(Collectors.toList());
                        List<V3PoAssociatedTermsContext> poAssociatedTerms = new ArrayList<>();
                        for (Long id : uniqueTermsIds) {
                            V3PoAssociatedTermsContext associatedTerm = new V3PoAssociatedTermsContext();
                            V3TermsAndConditionContext term = new V3TermsAndConditionContext();
                            term.setId(id);
                            associatedTerm.setPurchaseOrder(po);
                            associatedTerm.setTerms(term);
                            poAssociatedTerms.add(associatedTerm);
                        }
                        PurchaseOrderAPI.updateTermsAssociatedV3(poAssociatedTerms);
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

                //updating PO_ID in vendorQuote
                if(po.getVendorQuote()!=null){
                    Map<String, Object> patchObj = new HashMap<>();
                    Map<String, Object> poCreated = new HashMap<>();
                    poCreated.put("isPoCreated",true);
                    poCreated.put("purchaseOrderId", po.getId());
                    V3Util.processAndUpdateSingleRecord(FacilioConstants.ContextNames.VENDOR_QUOTES, po.getVendorQuote().getId(), patchObj, FieldUtil.getAsJSON(poCreated) , null, null, null, null,null, null);

                }
            }

        }
        return false;
    }
}
