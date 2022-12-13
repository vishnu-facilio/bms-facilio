package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;

import java.util.ArrayList;
import java.util.Map;

public class V3InventoryUtil {
    public static List<V3PurchasedItemContext> getPurchasedItemsBasedOnCostType(V3ItemContext item) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule purchasedItemModule = modBean.getModule(FacilioConstants.ContextNames.PURCHASED_ITEM);
        List<FacilioField> purchasedItemFields = modBean.getAllFields(FacilioConstants.ContextNames.PURCHASED_ITEM);
        List<V3PurchasedItemContext> purchasedItems = new ArrayList<>();
        if (item.getCostTypeEnum() == null || item.getCostType() <= 0
                || item.getCostType().equals(V3ItemContext.CostType.FIFO.getIndex())) {
            purchasedItems = getPurchasedItemList(item.getId(), " asc", purchasedItemModule,
                    purchasedItemFields);
        } else if (item.getCostType().equals(V3ItemContext.CostType.LIFO.getIndex())) {
            purchasedItems = getPurchasedItemList(item.getId(), " desc", purchasedItemModule,
                    purchasedItemFields);
        }
        return purchasedItems;
    }
    public static List<V3PurchasedItemContext> getPurchasedItemList(long id, String orderByType, FacilioModule module,
                                                                    List<FacilioField> fields) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<V3PurchasedItemContext> selectBuilder = new SelectRecordsBuilder<V3PurchasedItemContext>()
                .select(fields).table(module.getTableName()).moduleName(module.getName())
                .beanClass(V3PurchasedItemContext.class)
                .andCondition(
                        CriteriaAPI.getCondition(fieldMap.get("item"), String.valueOf(id), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("currentQuantity"), String.valueOf(0),
                        NumberOperators.GREATER_THAN))
                .orderBy(fieldMap.get("costDate").getColumnName() + orderByType);

        List<V3PurchasedItemContext> purchasedItemlist = selectBuilder.get();

        if (purchasedItemlist != null && !purchasedItemlist.isEmpty()) {
            return purchasedItemlist;
        }
        return null;
    }
    public static Double getServiceCost(V3ServiceContext service, Double duration, Double quantity) {
        Double costOccured = null;
        if(service.getBuyingPrice()!=null && service.getBuyingPrice() > 0) {
            if (service.getPaymentTypeEnum() == V3ServiceContext.PaymentType.FIXED) {
                costOccured = service.getBuyingPrice() * quantity;
            }
            else {
                costOccured = service.getBuyingPrice() * duration * quantity;
            }
        }
        return costOccured;
    }

    public static Double getWorkorderActualsDuration(Long issueTime, Long returnTime, V3WorkOrderContext workorder) {
        Double duration = null;
        if (issueTime!=null && returnTime!=null && issueTime >= 0 && returnTime >= 0) {
            duration = getEstimatedWorkDuration(issueTime, returnTime);
        } else {
            if(workorder.getActualWorkDuration()!=null && workorder.getActualWorkDuration() > 0) {
                double hours = (((double)workorder.getActualWorkDuration()) / (60 * 60));
                duration = Math.round(hours*100.0)/100.0;
            }
        }
        return duration;
    }

    public static double getEstimatedWorkDuration(long issueTime, long returnTime) {
        double duration = -1;
        if (issueTime != -1 && returnTime != -1) {
            duration = returnTime - issueTime;
        }
        return duration / 1000;
    }

    public static Long getReturnTimeFromDurationAndIssueTime(Double duration, Long issueTime) {
        Long returnTime = null;
        if (issueTime!=null && issueTime >= 0) {
            returnTime = (long) (issueTime + (duration * 1000));
        }
        return returnTime;
    }

    public static Long getIssueTimeFromDurationAndReturnTime(Double duration, Long returnTime) {
        Long issueTime = null;
        if(returnTime != null && returnTime >= 0) {
            issueTime = (long) (returnTime - (duration * 1000));
        }
        return issueTime;
    }

    public static Boolean hasVendorPortalAccess(Long vendorId) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String vendorContact = FacilioConstants.ContextNames.VENDOR_CONTACT;
        List<FacilioField> fields = modBean.getAllFields(vendorContact);

        SelectRecordsBuilder<V3VendorContactContext> builder = new SelectRecordsBuilder<V3VendorContactContext>()
                .moduleName(vendorContact)
                .select(fields)
                .beanClass(V3VendorContactContext.class)
                .andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("VENDOR_PORTAL_ACCESS", "isVendorPortalAccess", String.valueOf(true), BooleanOperators.IS));

        List<V3VendorContactContext> list = builder.get();
        if(CollectionUtils.isNotEmpty(list) && list.size()>0){
            return true;
        }
        return false;
    }
}
