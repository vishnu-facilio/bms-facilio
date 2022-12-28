package com.facilio.bmsconsoleV3.commands.purchaseorder;


import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.InventoryType;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GetPurchaseOrdersListOnInventoryTypeIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(Objects.nonNull(Constants.getQueryParam(context,"filterPO"))){
            boolean filterPO=FacilioUtil.parseBoolean((Constants.getQueryParam(context, "filterPO")));
            if(Objects.nonNull(Constants.getQueryParam(context,"storeRoomId"))
                    &&Objects.nonNull(Constants.getQueryParam(context,"inventoryType"))
                    &&Objects.nonNull(Constants.getQueryParam(context,"inventoryId"))
                    &&filterPO)
            {
                long storeRoomId=FacilioUtil.parseLong((Constants.getQueryParam(context, "storeRoomId")));
                int inventoryType=FacilioUtil.parseInt((Constants.getQueryParam(context, "inventoryType")));
                long inventoryId = FacilioUtil.parseLong((Constants.getQueryParam(context, "inventoryId")));
                ModuleBean modBean = Constants.getModBean();
                FacilioModule purchaseOrderModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER);
                FacilioModule lineItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS);
                List<FacilioField> poFields = modBean.getAllFields(purchaseOrderModule.getName());

                SelectRecordsBuilder<V3PurchaseOrderContext> builder = new SelectRecordsBuilder<V3PurchaseOrderContext>()
                        .moduleName(purchaseOrderModule.getName())
                        .select(poFields)
                        .beanClass(V3PurchaseOrderContext.class)
                        .innerJoin(lineItemModule.getTableName())
                        .on(purchaseOrderModule.getTableName() + ".ID = " + lineItemModule.getTableName() + ".PO_ID")
                        .andCondition(CriteriaAPI.getCondition("INVENTORY_TYPE", "inventoryType", String.valueOf(inventoryType), NumberOperators.EQUALS));
                if (storeRoomId > 0L) {
                    builder.andCondition(CriteriaAPI.getCondition("STOREROOM", "storeRoom", String.valueOf(storeRoomId), NumberOperators.EQUALS));
                }
                if (inventoryType == InventoryType.ITEM.ordinal() + 1) {
                    builder.andCondition(CriteriaAPI.getCondition("ITEM_TYPE", "itemType", String.valueOf(inventoryId), NumberOperators.EQUALS));
                } else if (inventoryType == InventoryType.TOOL.ordinal() + 1) {
                    builder.andCondition(CriteriaAPI.getCondition("TOOL_TYPE", "toolType", String.valueOf(inventoryId), NumberOperators.EQUALS));
                }
                List<V3PurchaseOrderContext> list = builder.get();
                List<Long> poIds = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(list)) {
                    poIds = list.stream().map(p -> p.getId()).collect(Collectors.toList());
                }
                Condition condition;
                if (CollectionUtils.isNotEmpty(poIds)) {
                    condition = CriteriaAPI.getCondition("ID", "id", String.valueOf(StringUtils.join(poIds, ",")), NumberOperators.EQUALS);
                } else {
                    condition = CriteriaAPI.getCondition("ID", "id", "-1", NumberOperators.EQUALS);
                }
                context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
            }
            else{
                throw new IllegalArgumentException("QueryParams for Filtering Purchase Order cannot be null");
            }
        }
        return false;
    }
}
