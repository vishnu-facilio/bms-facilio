package com.facilio.qa.context.questions;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
@Log4j
public class MCQOptionContext extends V3Context {
    private Long parentId; // Not having it as lookup because this is not used in forms or anything. Also this means I can avoid the extra handling done for lookup since this won't be a separate list anyway
    private String label;
    private Integer position;
}
