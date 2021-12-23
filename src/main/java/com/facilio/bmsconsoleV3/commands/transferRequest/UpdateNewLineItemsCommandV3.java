package com.facilio.bmsconsoleV3.commands.transferRequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class UpdateNewLineItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequestContexts = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule trLineItems = modBean.getModule(FacilioConstants.ContextNames.TRANSFER_REQUEST_LINE_ITEM);

        if (CollectionUtils.isNotEmpty(transferRequestContexts)) {
            for (V3TransferRequestContext transferRequestContext : transferRequestContexts) {
                if (transferRequestContext != null && transferRequestContext.getIsStaged().equals(false)) {
                    if (transferRequestContext.getTransferrequestlineitems() != null) {
                            DeleteRecordBuilder<V3TransferRequestLineItemContext> deleteBuilder = new DeleteRecordBuilder<V3TransferRequestLineItemContext>()
                                    .module(trLineItems)
                                    .andCondition(CriteriaAPI.getCondition("TRANSFER_REQUEST_ID", "transferRequest", String.valueOf(transferRequestContext.getId()), NumberOperators.EQUALS));
                            deleteBuilder.delete();
                            V3TransferRequestContext trContext = new V3TransferRequestContext();
                            trContext.setId(transferRequestContext.getId());
                            for (V3TransferRequestLineItemContext lineItemContext : transferRequestContext.getTransferrequestlineitems()) {
                                lineItemContext.setTransferRequest(trContext);
                            }
                            RecordAPI.addRecord(false, transferRequestContext.getTransferrequestlineitems(), trLineItems, modBean.getAllFields(trLineItems.getName()));
                        }
                }
            }
        }
        return false;
    }
}