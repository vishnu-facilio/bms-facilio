package com.facilio.bmsconsoleV3.context.ocr;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParsedBillTableContext extends V3Context {
    private ParsedBillContext parsedBill;
    private Long parsedBillTableFileId;
}
