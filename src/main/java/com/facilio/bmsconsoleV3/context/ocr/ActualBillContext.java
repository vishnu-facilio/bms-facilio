package com.facilio.bmsconsoleV3.context.ocr;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
public class ActualBillContext extends V3Context {
   private String name;
   private Long billMonth;
   private BillTemplateContext billTemplate;
   private File billFile;
   private Long billFileId;
   private String billFileUrl;
   private String billFileFileName;
   private String billFileContentType;

   private ActualBillStatus status;

   public Integer getStatus() {
      if (status == null) {
         return null;
      }
      return status.getIndex();
   }
   public void setStatus(Integer status) {
      if (status != null) {
         this.status = ActualBillStatus.valueOf(status);
      } else {
         this.status = null;
      }
   }
   @AllArgsConstructor
   public static enum ActualBillStatus implements FacilioIntEnum {
      BILL_UPLOADED("Bill Uploaded"),
      BILL_UPLOAD_FAILED("Bill Upload Failed"),
      PARSING_IN_PROGRESS("Parsing In Progress"),
      PARSING_FAILED("Parsing Failed"),
      PARSED_BILL_GENERATED("Parsed Bill Generated")
      ;
      public int getVal() {
         return ordinal() + 1;
      }
      String name;
      @Override
      public String getValue() {
         // TODO Auto-generated method stub
         return this.name;
      }
      private static final ActualBillStatus[] STATUS = ActualBillStatus.values();
      public static ActualBillStatus valueOf(int type) {
         if (type > 0 && type <= STATUS.length) {
            return STATUS[type - 1];
         }
         return null;
      }
   }

}
