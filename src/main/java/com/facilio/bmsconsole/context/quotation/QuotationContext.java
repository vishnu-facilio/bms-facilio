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
    private long billDate = -1;
    private long expiryDate = -1;
    private long signatureId;
    private String signatureUrl;
    private File signature;
    private String signatureFileName;
    private String signatureContentType;
    private double subTotal = -1;
    private double totalTaxAmount = -1;
    private double discountAmount = -1;
    private double discountPercentage = -1;
    private double shippingCharges = -1;
    private double adjustmentsCost = -1;
    private String adjustmentsCostName;
    private double miscellaneousCharges = -1;
    private double totalCost = -1;
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

    public long getBillDate() {
        return billDate;
    }

    public void setBillDate(long billDate) {
        this.billDate = billDate;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(long signatureId) {
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

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(double totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    public double getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(double shippingCharges) {
        this.shippingCharges = shippingCharges;
    }

    public double getAdjustmentsCost() {
        return adjustmentsCost;
    }

    public void setAdjustmentsCost(double adjustmentsCost) {
        this.adjustmentsCost = adjustmentsCost;
    }

    public String getAdjustmentsCostName() {
        return adjustmentsCostName;
    }

    public void setAdjustmentsCostName(String adjustmentsCostName) {
        this.adjustmentsCostName = adjustmentsCostName;
    }

    public double getMiscellaneousCharges() {
        return miscellaneousCharges;
    }

    public void setMiscellaneousCharges(double miscellaneousCharges) {
        this.miscellaneousCharges = miscellaneousCharges;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }


    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }


}
