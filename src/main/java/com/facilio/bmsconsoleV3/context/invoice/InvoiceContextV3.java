package com.facilio.bmsconsoleV3.context.invoice;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

public class InvoiceContextV3 extends BaseLineItemsParentModuleContext {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name;
    private String subject;
    private String description;
    private LocationContext billToAddress;
    private LocationContext shipToAddress;
    private Long billDate;
    private Long expiryDate;
    private Long signatureId;
    private String signatureUrl;
    private File signature;
    private String signatureFileName;
    private String signatureContentType;
    private Double shippingCharges;
    private Double adjustmentsCost;
    private String adjustmentsCostName;
    private Double miscellaneousCharges;

    @Getter @Setter
    private List<InvoiceLineItemsContext> lineItems;

    @Getter @Setter
    private List<InvoiceAssociatedTermsContext> termsAssociated;
    private InvoiceType invoiceType;
    private InvoiceStatus invoiceStatus;
    private V3TenantContext tenant;
    private Long parentId;
    private Long revisionNumber;
    @Getter @Setter
    private Boolean isInvoiceRevised;
    private Boolean revisionHistoryAvailable;

    @Getter @Setter
    private Double markup;

    @Getter @Setter
    private InvoiceSettingContext invoiceSetting;

    @Getter @Setter
    private Double totalMarkup;

    @Getter @Setter
    private Boolean showMarkupValue;


    @Getter @Setter
    private Boolean isGlobalMarkup;

    @Getter @Setter
    private Double invoiceVersion;

    @Getter @Setter
    private InvoiceContextV3 group;

    @Getter @Setter
    private InvoiceContextV3 childInvoice;

    @Getter @Setter
    private QuotationContext quote;

    @Getter @Setter
    private V3PurchaseOrderContext purchaseOrder;

    @Getter @Setter
    private String invoiceNumber;

    @Getter @Setter
    private V3ServiceRequestContext serviceRequest;



//    public Boolean getIsGlobalMarkup() {
//        return isGlobalMarkup;
//    }
//
//    public void setIsGlobalMarkup(Boolean isGlobalMarkup) {
//        this.isGlobalMarkup = isGlobalMarkup;
//    }
//
//    public Boolean isGlobalMarkup() {
//        if (isGlobalMarkup != null) {
//            return isGlobalMarkup.booleanValue();
//        }
//        return false;
//    }

//    public Boolean getShowMarkupValue() {
//        return showMarkupValue;
//    }
//
//    public void setShowMarkupValue(Boolean showMarkupValue) {
//        this.showMarkupValue = showMarkupValue;
//    }
//
//    public Boolean showMarkupValue() {
//        if (showMarkupValue != null) {
//            return showMarkupValue.booleanValue();
//        }
//        return false;
//    }


    @Getter @Setter
    private InvoiceContextV3 parentInvoiceId;

    @Getter @Setter
    private V3VendorContext vendor;

    @Getter @Setter
    private List<V3PeopleContext> approvers;


    public V3WorkOrderContext getWorkorder() {
        return workorder;
    }

    public void setWorkorder(V3WorkOrderContext workorder) {
        this.workorder = workorder;
    }

    private V3WorkOrderContext workorder;


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    private String notes;

    public V3TenantContext getTenant() {
        return tenant;
    }

    public void setTenant(V3TenantContext tenant) {
        this.tenant = tenant;
    }

    public V3ClientContext getClient() {
        return client;
    }

    public void setClient(V3ClientContext client) {
        this.client = client;
    }

    private V3ClientContext client;

    public void setInvoiceType(Integer invoiceType) {
        if (invoiceType != null) {
            this.invoiceType = InvoiceType.valueOf(invoiceType);
        }
    }

    public InvoiceType getInvoiceTypeEnum() {
        return invoiceType;
    }
    public Integer getInvoiceType() {
        if (invoiceType != null) {
            return invoiceType.getIndex();
        }
        return null;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocationContext getBillToAddress() {
        return billToAddress;
    }

    public void setBillToAddress(LocationContext billToAddress) {
        this.billToAddress = billToAddress;
    }

    public LocationContext getShipToAddress() {
        return shipToAddress;
    }

    public void setShipToAddress(LocationContext shipToAddress) {
        this.shipToAddress = shipToAddress;
    }

    public Long getBillDate() {
        return billDate;
    }

    public void setBillDate(Long billDate) {
        this.billDate = billDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(Long signatureId) {
        this.signatureId = signatureId;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public File getSignature() {
        return signature;
    }

    public void setSignature(File signature) {
        this.signature = signature;
    }

    public String getSignatureFileName() {
        return signatureFileName;
    }

    public void setSignatureFileName(String signatureFileName) {
        this.signatureFileName = signatureFileName;
    }

    public String getSignatureContentType() {
        return signatureContentType;
    }

    public void setSignatureContentType(String signatureContentType) {
        this.signatureContentType = signatureContentType;
    }

    public Double getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(Double shippingCharges) {
        this.shippingCharges = shippingCharges;
    }

    public Double getAdjustmentsCost() {
        return adjustmentsCost;
    }

    public void setAdjustmentsCost(Double adjustmentsCost) {
        this.adjustmentsCost = adjustmentsCost;
    }

    public String getAdjustmentsCostName() {
        return adjustmentsCostName;
    }

    public void setAdjustmentsCostName(String adjustmentsCostName) {
        this.adjustmentsCostName = adjustmentsCostName;
    }

    public Double getMiscellaneousCharges() {
        return miscellaneousCharges;
    }

    public void setMiscellaneousCharges(Double miscellaneousCharges) {
        this.miscellaneousCharges = miscellaneousCharges;
    }

    public enum InvoiceType implements FacilioIntEnum {
        TENANT("Tenant"),
        CLIENT("Client"),
        VENDOR("Vendor");

        private String name;

        InvoiceType(String name) {
            this.name = name;
        }

        public static InvoiceType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getRevisionNumber() {
        if(revisionNumber == null){
            return 0L;
        }
        return revisionNumber;
    }

    public void setRevisionNumber(Long revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public InvoiceContextV3 clone() {

        InvoiceContextV3 newInvoice = FieldUtil.cloneBean(this, InvoiceContextV3.class);
        newInvoice.setId(-1);
        newInvoice.setRevisionNumber(this.getRevisionNumber() + 1);
        newInvoice.setParentId(this.getParentId());
        newInvoice.setIsInvoiceRevised(false);
        newInvoice.setRevisionHistoryAvailable(true);
        return newInvoice;

    }




    public Boolean getRevisionHistoryAvailable() {
        return revisionHistoryAvailable;
    }

    public void setRevisionHistoryAvailable(Boolean revisionHistoryAvailable) {
        this.revisionHistoryAvailable = revisionHistoryAvailable;
    }

    public Boolean isRevisionHistoryAvailable() {
        if (revisionHistoryAvailable != null) {
            return revisionHistoryAvailable.booleanValue();
        }
        return false;
    }

    private V3PeopleContext contact;

    public V3PeopleContext getContact() {
        return contact;
    }

    public void setContact(V3PeopleContext contact) {
        this.contact = contact;
    }



    public void setInvoiceStatus(Integer invoiceStatus) {
        if (invoiceStatus != null) {
            this.invoiceStatus = InvoiceStatus.valueOf(invoiceStatus);
        }
    }

    public void setInvoiceStatusEnum(InvoiceStatus invoiceStatus) {
        if (invoiceStatus != null) {
            this.invoiceStatus = invoiceStatus;
        }
    }

    public InvoiceStatus getInvoiceStatusEnum() {
        return invoiceStatus;
    }

    public String getInvoiceStatusEnumName() {
        if(invoiceStatus != null)
        {
            invoiceStatus.getValue();
        }
         return null;
    }

    public void setInvoiceStatusType(String name) {
        if(invoiceStatus != null)
        {
            this.invoiceStatus.statusType = name;
        }
    }
    public String getInvoiceStatusType() {
        if(invoiceStatus != null)
        {
            invoiceStatus.getStatusType();
        }
        return null;
    }
    public Integer getInvoiceStatus() {
        if (invoiceStatus != null) {
            return invoiceStatus.getIndex();
        }
        return null;
    }


    @AllArgsConstructor
    @Getter
    public static enum InvoiceStatus implements FacilioIntEnum {
        DRAFT("Draft","default"),
        DELIVERED("Invoice Delivered","warning"),
        REJECTED("Invoice Rejected","danger"),
        APPROVED("Invoice Approved","information"),
        CANCELLED("Invoice Cancelled","danger"),
        PAYMENT_ACKNOWLEDGED("Payment Acknowledged","success"),
        REVISED("Revised","neutral");
        public String getValue() {
            return this.name;
        }

        public String getStatusType() {
            return this.statusType;
        }

        private String name;
        private String statusType;


        public static InvoiceStatus valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }
    }
}
