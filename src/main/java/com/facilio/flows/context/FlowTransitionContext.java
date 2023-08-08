package com.facilio.flows.context;

import com.facilio.blockfactory.enums.BlockType;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Getter
@Setter
public class FlowTransitionContext implements Serializable {
    private long id;
    private long flowId = -1l;
    private String name;

    public void setName(String name) {
        if(StringUtils.isNotEmpty(name)&& name.length()>255){
            throw new IllegalArgumentException("Name length should be less than 255 characters");
        }
        this.name = name;
    }

    private Boolean isStartBlock;
    private long connectedFrom = -1l;
    private String position;
    private String handlePosition;
    private String configData;
    private BlockType blockType;


}
