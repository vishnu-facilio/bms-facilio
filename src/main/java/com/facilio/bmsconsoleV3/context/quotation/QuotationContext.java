package com.facilio.bmsconsoleV3.context.quotation;

import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.modules.FacilioEnum;
import com.facilio.v3.context.V3Context;

import java.io.File;
import java.util.List;

public class QuotationContext extends V3Context {

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
    private Double subTotal;
    private Double totalTaxAmount;
    private Double discountAmount;
    private Double discountPercentage;
    private Double shippingCharges;
    private Double adjustmentsCost;
    private String adjustmentsCostName;
    private Double miscellaneousCharges;
    private Double totalCost;
    private List<QuotationLineItemsContext> lineItems;
    private List<QuotationAssociatedTermsContext> termsAssociated;
    private CustomerType customerType;
    private V3TenantContext tenant;
    private Long parentId;
    private Long revisionNumber;
    private Boolean isQuotationRevised;
    private Boolean revisionHistoryAvailable;

    public List<TaxSplitUpContext> getTaxSplitUp() {
        return taxSplitUp;
    }

    public void setTaxSplitUp(List<TaxSplitUpContext> taxSplitUp) {
        this.taxSplitUp = taxSplitUp;
    }

    private List<TaxSplitUpContext> taxSplitUp;

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

    public QuotationContext() {

    }
    public QuotationContext(QuotationContext quotation) {
        setName(quotation.getName());
        setSubject(quotation.getSubject());
        setDescription(quotation.getDescription());
        setBillToAddress(quotation.getBillToAddress());
        setShipToAddress(quotation.getShipToAddress());
        setBillDate(quotation.getBillDate());
        setExpiryDate(quotation.getExpiryDate());
        setSubTotal(quotation.getSubTotal());
        setTotalTaxAmount(quotation.getTotalTaxAmount());
        setDiscountAmount(quotation.getDiscountAmount());
        setDiscountPercentage(quotation.getDiscountPercentage());
        setShippingCharges(quotation.getShippingCharges());
        setAdjustmentsCost(quotation.getAdjustmentsCost());
        setAdjustmentsCostName(quotation.getAdjustmentsCostName());
        setMiscellaneousCharges(quotation.getMiscellaneousCharges());
        setTotalCost(quotation.getTotalCost());
        setLineItems(quotation.getLineItems());
        setTermsAssociated(quotation.getTermsAssociated());
        setCustomerType(quotation.getCustomerType());
        setTotalCost(quotation.getTotalCost());
        setTenant(quotation.getTenant());
        setRevisionNumber(quotation.getRevisionNumber() + 1);
        setParentId(quotation.getParentId());
        setSiteId(quotation.getSiteId());
        setFormId(quotation.getFormId());
        setIsQuotationRevised(false);
        setNotes(quotation.getNotes());
        setRevisionHistoryAvailable(true);
        setWorkorder(quotation.getWorkorder());
        setId(-1);
    }

    public V3TenantContext getTenant() {
        return tenant;
    }

    public void setTenant(V3TenantContext tenant) {
        this.tenant = tenant;
    }

    public ClientContext getClient() {
        return client;
    }

    public void setClient(ClientContext client) {
        this.client = client;
    }

    private ClientContext client;

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

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(Double totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
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

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }


    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public enum CustomerType implements FacilioEnum {
        TENANT("Tenant"),
        CLIENT("Client");
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
        public int getIndex() {
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
        return new QuotationContext(this);
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
