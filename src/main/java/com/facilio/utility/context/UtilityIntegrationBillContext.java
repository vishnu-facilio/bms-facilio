package com.facilio.utility.context;

import com.facilio.bmsconsoleV3.context.UtilityAccountBillContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class UtilityIntegrationBillContext extends V3Context {

    private static final long serialVersionUID = 1L;
    public UtilityIntegrationBillContext(long id){
       this.setId(id);
    };

   String billUid;
   String meterUid;
   String customerUid;
   private UtilityAccountBillContext utilityAccountBill;
   String meta;
   Long createdTime;
   Long updatedTime;
   String utilityID;
   String serviceIdentifier;
   String serviceTariff;
   String serviceAddress;
   List<String> meterNumber;
   String billingContact;
   String billingAddress;
   String billingAccount;
   String serviceClass;
   Long billStatementDate;
   Long billStartDate;
   Long billEndDate;
   String billTotalUnit;
   Double billTotalCost;
   Double billTotalVolume;
   String supplierType;
   String supplierName;
   String supplierServiceId;
   String supplierTariff;
   String supplierTotalUnit;
   Double supplierTotalCost;
   Double supplierTotalVolume;
   String sourceType;
   String sourceUrl;
   Long billFileId;
   String sourceDownloadUrl;
   UtilityIntegrationCustomerContext utilityIntegrationCustomer;
   UtilityIntegrationMeterContext utilityIntegrationMeter;

   Type billType;
   public Integer getBillType() {
      if (billType != null) {
         return billType.getValue();
      }
      return -1;
   }

   public void setBillType(Integer billType) {
      if (billType != null) {
         this.billType = UtilityIntegrationBillContext.Type.valueOf(billType);
      }
   }




   public static enum Type {
      MANUAL(1,"Manually Generated"),
      AUTO_GENERATED(2, "Auto Generated"),

      ;

      Integer intVal;
      String name;

      private Type(Integer intVal, String name) {
         this.intVal = intVal;
         this.name = name;

      }
      public Integer getIntVal() {
         return intVal;
      }
      public void setIntVal(Integer intVal) {
         this.intVal = intVal;
      }
      public String getName() {
         return name;
      }
      public void setName(String name) {
         this.name = name;
      }
      public Integer getValue() {
         return ordinal() + 1;
      }

      public static Type valueOf(int value) {
         if (value > 0 && value <= values().length) {
            return values()[value - 1];
         }
         return null;
      }
   }
   UtilityBillStatus utilityBillStatus;
   public UtilityBillStatus getUtilityBillStatusEnum() {
      return utilityBillStatus;
   }

   public Integer getUtilityBillStatus() {
      if(utilityBillStatus != null) {
         return utilityBillStatus.getIndex();
      }
      else {
         return null;
      }
   }
   public void setUtilityBillStatus(Integer utilityBillStatus) {
      if (utilityBillStatus != null) {
         this.utilityBillStatus = UtilityBillStatus.valueOf(utilityBillStatus);
      }
   }
   public  enum UtilityBillStatus implements FacilioIntEnum {

      CLEAR(1, "Clear"),
      PARTIALLYDISPUTED(2, "Partial dispute"),
      DISPUTED(3, "Disputed");


      Integer intVal;
      String value;

      UtilityBillStatus(Integer intVal, String value) {
         this.intVal = intVal;
         this.value = value;
      }

      @Override
      public Integer getIndex() {
         return ordinal() + 1;
      }

      @Override
      public String getValue() {
         return value;
      }

      public void setValue(String value) {
         this.value = value;
      }

      public Integer getIntVal() {
         return intVal;
      }

      public void setIntVal(Integer intVal) {
         this.intVal = intVal;
      }

      public static UtilityBillStatus valueOf(int value) {
         if (value > 0 && value <= values().length) {
            return values()[value - 1];
         }
         return null;
      }

   }

   List<UtilityIntegrationLineItemContext> utilityIntegrationLineItemContexts;
   List<UtilityIntegrationTierItemContext> utilityIntegrationTierItemContexts;
   List<UtilityIntegrationSupplierLineItemContext> utilityIntegrationSupplierLineItemContexts;
   List<UtilityIntegrationTOUContext> utilityIntegrationTOUContexts;
   List<UtilityIntegrationDemandContext> utilityIntegrationDemandContexts;
   List<UtilityIntegrationPowerContext> utilityIntegrationPowerContexts;
   List<UtilityDisputeContext> utilityDisputeContexts;



}
