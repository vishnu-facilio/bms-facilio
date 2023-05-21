package com.facilio.flows.context;

import com.facilio.blockfactory.enums.BlockType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowTransitionContext extends FlowContext {

    private long flowId = -1l;
    private String uniqueName;
    private Boolean isStartBlock;
    private long connectedFrom = -1l;
    private String position;
    private String configData;
    private BlockType blockType;


}
