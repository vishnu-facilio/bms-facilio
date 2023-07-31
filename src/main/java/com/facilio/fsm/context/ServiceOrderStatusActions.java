package com.facilio.fsm.context;

import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class ServiceOrderStatusActions  extends V3Context {
    private Map<String,String> primaryButton;
    private Map<String,String> secondaryButton;
    private List<Map<String,String>> moreActions;
}
