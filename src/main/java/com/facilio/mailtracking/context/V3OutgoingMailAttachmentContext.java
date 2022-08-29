package com.facilio.mailtracking.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class V3OutgoingMailAttachmentContext extends V3Context {
    private V3OutgoingMailLogContext mailId;
    private String fileName;
}
