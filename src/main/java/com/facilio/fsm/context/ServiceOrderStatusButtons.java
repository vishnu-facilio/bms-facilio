package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
@Getter @Setter
public class ServiceOrderStatusButtons extends V3Context {
    private String primaryButton;
    private String secondaryButton;
    private List<String> moreActions;
}
