package com.facilio.bmsconsoleV3.commands.transferRequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ItemContext;
import com.facilio.bmsconsole.context.ItemTypesContext;
import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.bmsconsole.util.ItemsApi;
import com.facilio.bmsconsole.util.ToolsApi;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import nl.basjes.shaded.org.springframework.util.ObjectUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class TransferRequestValidationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TransferRequestContext> transferRequestContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(transferRequestContexts)) {
            for (V3TransferRequestContext transferRequestContext : transferRequestContexts) {

                if (transferRequestContext != null && CollectionUtils.isNotEmpty(transferRequestContext.getSubForm().get("transferrequestlineitems"))) {
                    //Line Item Validation
                    List<Map<String, Object>> lineItems = transferRequestContext.getSubForm().get("transferrequestlineitems");

                    for (Map<String, Object> lineItem : lineItems) {
                        V3TransferRequestLineItemContext lineItemContext = FieldUtil.getAsBeanFromMap(lineItem, V3TransferRequestLineItemContext.class);
                        if (lineItemContext.getInventoryType().equals(V3TransferRequestLineItemContext.InventoryType.TOOL.getIndex())) {
                            if (ObjectUtils.isEmpty(lineItemContext.getQuantity()) || ObjectUtils.isEmpty(lineItemContext.getToolType())) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Line items cannot be empty");
                            }
                        }
                        if (lineItemContext.getInventoryType().equals(V3TransferRequestLineItemContext.InventoryType.ITEM.getIndex())) {
                            if (ObjectUtils.isEmpty(lineItemContext.getQuantity()) || ObjectUtils.isEmpty(lineItemContext.getItemType())) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Line items cannot be empty");
                            }
                        }
                    }
                    // Storeroom Validation
                    if (transferRequestContext.getTransferToStore().getId() == transferRequestContext.getTransferFromStore().getId()) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storerooms cannot be same");
                    }
                    //rotating item validation
                    for (Map<String, Object> lineItem : lineItems) {
                        V3TransferRequestLineItemContext lineItemContext = FieldUtil.getAsBeanFromMap(lineItem, V3TransferRequestLineItemContext.class);
                        if (lineItemContext.getInventoryType().equals(V3TransferRequestLineItemContext.InventoryType.TOOL.getIndex())) {
                            Long id = lineItemContext.getToolType().getId();
                            if (isRotating(id, 2)) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating tools cannot be transferred");
                            }
                        }
                        if (lineItemContext.getInventoryType().equals(V3TransferRequestLineItemContext.InventoryType.ITEM.getIndex())) {
                            Long id = lineItemContext.getItemType().getId();
                            if (isRotating(id, 1)) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Rotating items cannot be transferred");
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private Boolean isRotating(Long id, int inventoryType) throws Exception {
        if (inventoryType == 1) {
            ItemTypesContext record = ItemsApi.getItemTypes(id);
            return record.getIsRotating();
        } else{
            ToolTypesContext record = ToolsApi.getToolTypes(id);
            return record.getIsRotating();
        }
    }
}