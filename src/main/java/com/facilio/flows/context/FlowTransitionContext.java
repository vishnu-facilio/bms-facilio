package com.facilio.flows.context;

import com.facilio.blockfactory.enums.BlockType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    @JsonIgnore
    private JSONObject config;

    @JSON(serialize = false)
    @JsonIgnore
    public JSONObject getConfig() {
        if (config == null) {
            config = new JSONObject();
        }
        return config;
    }

    public void setConfigData(String configData) {
        this.configData = configData;
        if (StringUtils.isNotEmpty(configData)) {
            try{
                config = (JSONObject) new JSONParser().parse(configData);
            }catch (Exception e){

            }
        }
    }

    protected void addConfigData(String key, Object value) {
        JSONObject config = getConfig();
        config.put(key, value);
    }
    @JsonIgnore
    @JSON(serialize = false)
    public Object getDatumFromConfig(String key){
        JSONObject config = getConfig();
        return config.get(key);
    }

    public void updateConfig() throws Exception{
    }

}
