package com.facilio.bmsconsole.context.quotation;

import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.io.File;
import java.util.List;

public class QuotationContext extends ModuleBaseWithCustomFields {

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

    public List<QuotationAssociatedTermsContext> getTermsAssociated() {
        return termsAssociated;
    }

    public void setTermsAssociated(List<QuotationAssociatedTermsContext> termsAssociated) {
        this.termsAssociated = termsAssociated;
    }

    private List<QuotationAssociatedTermsContext> termsAssociated;

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


}
