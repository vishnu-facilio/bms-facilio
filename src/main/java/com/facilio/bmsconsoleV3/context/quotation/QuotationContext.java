package com.facilio.bmsconsoleV3.context.quotation;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

public class QuotationContext extends BaseLineItemsParentModuleContext {

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
    private List<QuotationLineItemsContext> lineItems;
    private List<QuotationAssociatedTermsContext> termsAssociated;
    private CustomerType customerType;
    private V3TenantContext tenant;
    private Long parentId;
    private Long revisionNumber;
    private Boolean isQuotationRevised;
    private Boolean revisionHistoryAvailable;

    @Getter @Setter
    private Double markup;

    @Getter @Setter
    private QuotationSettingContext quotationSetting;

    @Getter @Setter
    private Double totalMarkup;

    @Getter @Setter
    private Boolean showMarkupValue;


    @Getter @Setter
    private Boolean isGlobalMarkup;

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
    private QuotationContext parentQuotationId;

    @Getter @Setter
    private V3VendorContext vendor;


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

    public void setCustomerType(Integer customerType) {
        if (customerType != null) {
            this.customerType = CustomerType.valueOf(customerType);
        }
    }

    public CustomerType getCustomerTypeEnum() {
        return customerType;
    }
    public Integer getCustomerType() {
        if (customerType != null) {
            return customerType.getIndex();
        }
        return null;
    }

    public List<QuotationAssociatedTermsContext> getTermsAssociated() {
        return termsAssociated;
    }

    public void setTermsAssociated(List<QuotationAssociatedTermsContext> termsAssociated) {
        this.termsAssociated = termsAssociated;
    }

    public List<QuotationLineItemsContext> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<QuotationLineItemsContext> lineItems) {
        this.lineItems = lineItems;
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

    public enum CustomerType implements FacilioIntEnum {
        TENANT("Tenant"),
        CLIENT("Client"),
        VENDOR("Vendor");

        private String name;

        CustomerType(String name) {
            this.name = name;
        }

        public static CustomerType valueOf(int value) {
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

    public QuotationContext clone() {

        QuotationContext newQuote = FieldUtil.cloneBean(this, QuotationContext.class);
        newQuote.setId(-1);
        newQuote.setRevisionNumber(this.getRevisionNumber() + 1);
        newQuote.setParentId(this.getParentId());
        newQuote.setIsQuotationRevised(false);
        newQuote.setRevisionHistoryAvailable(true);
        return newQuote;

    }


    public Boolean getIsQuotationRevised() {
        return isQuotationRevised;
    }

    public void setIsQuotationRevised(Boolean isQuotationRevised) {
        this.isQuotationRevised = isQuotationRevised;
    }

    public Boolean isQuotationRevised() {
        if (isQuotationRevised != null) {
            return isQuotationRevised.booleanValue();
        }
        return false;
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
}
