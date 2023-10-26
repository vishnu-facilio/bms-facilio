package com.facilio.blockfactory.enums;

import com.facilio.blockfactory.blocks.*;
import com.facilio.flows.context.FlowType;
import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;
import org.apache.commons.beanutils.ConstructorUtils;

import java.util.*;


public enum BlockType implements FacilioStringEnum {
    set_variable(Group.LOGIC, "Set Variable", SetVariableBlock.class),
    if_else(Group.LOGIC, "Decision", If_Else_Block.class),
    summary_record(Group.CRUD, "Get Record", SummaryRecordBlock.class),
    delete_record(Group.CRUD, "Delete Record", DeleteRecordBlock.class,FlowType.GENERIC,FlowType.MODULE),
    create_record(Group.CRUD,"Create Record", CreateRecordBlock.class,FlowType.GENERIC,FlowType.MODULE),
    update_record(Group.CRUD,"Update Record", UpdateRecordBlock.class,FlowType.GENERIC,FlowType.MODULE),
    change_status(Group.ACTION,"Change Status",ChangeStatusBlock.class,FlowType.GENERIC,FlowType.MODULE),
    script(Group.ACTION,"Script", ScriptBlock.class),
    send_notification(Group.ACTION,"Send Notification", SendPushNotificationBlock.class,FlowType.GENERIC,FlowType.MODULE),
    send_mail(Group.ACTION,"Send Mail", BaseBlock.class,FlowType.GENERIC,FlowType.MODULE);

    private Class clazz;
    @Getter
    private String name;
    private Group group;
    @Getter
    private Long supportedFlowTypes;

    BlockType(Group group, String name, Class clazz, FlowType... supportedFlowTypes) {
        this.group = group;
        this.name = name;
        this.clazz = clazz;
        this.supportedFlowTypes = sum(supportedFlowTypes);
    }
    private long sum(FlowType... supportedFlowTypes){
        if(supportedFlowTypes.length==0){
            return 0;
        }
        return Arrays.stream(supportedFlowTypes).map(flowType -> flowType.getKey()).reduce((key,sum)->key+sum).get();
    }

    public Group getGroup() {
        return group;
    }

    public BaseBlock getInstance(Map<String, Object> config) {
        try {
            BaseBlock o = (BaseBlock) ConstructorUtils.invokeConstructor(clazz, config);
            return o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public enum Group {
        LOGIC("Logic Controls", "#666eff"),
        ACTION("Actions", "#33cc78"),
        CRUD("Data", "#33ccc4");

        @Getter
        private final String name;
        @Getter
        private final String color;

        Group(String name, String color) {
            this.name = name;
            this.color = color;
        }

    }
    private static final Map<String, Object> GROUP_BLOCK_MAP = Collections.unmodifiableMap(initGroupBlockMap());
    private static final List<Map<String, Object>> GROUP_BLOCK_LIST = Collections.unmodifiableList(initList());
    //private static final Map<FlowType,List<Map<String, Object>>> FLOW_TYPE_VS_SUPPORTED_BLOCKS = Collections.unmodifiableMap(initFlowTypeVsSupportedBlocks());
    public static List<Map<String, Object>> getGroupBlockList() {
        return GROUP_BLOCK_LIST;
    }
    public static Map<String, Object> getGroupBlockMap() {return GROUP_BLOCK_MAP;
    }
    private static List<Map<String, Object>> initList() {
        List<Map<String, Object>> blockList = new ArrayList<>();
        Map<String, Object> groupMap = initGroupBlockMap();
        for (Map.Entry<String, Object> entry : groupMap.entrySet()) {
            Map<String, Object> blocks = (Map<String, Object>) entry.getValue();
            blockList.add(blocks);
        }
        return blockList;
    }
    private static Map<String, Object> initGroupBlockMap() {

        Map<String, Object> typeMap = new LinkedHashMap<>();
        for(Group group:Group.values()){
            typeMap.put(group.name,null);
        }

        for (BlockType block : values()) {

            List<Map<String, Object>> blocks = new ArrayList<>();
            Map<String, Object> result = typeMap.get(block.group.name) == null ? new HashMap<>() : (Map<String, Object>) typeMap.get(block.group.name);
            blocks = result.get("blocks") == null ? new ArrayList<>() : (List<Map<String, Object>>) result.get("blocks");
            Map<String, Object> groupMap = new HashMap<>();
            Map<String, Object> blockMap = new HashMap<>();
            blockMap.put("displayName", block.name);
            blockMap.put("name", block);
            blocks.add(blockMap);

            if (typeMap.get(block.group.name) == null) {
                groupMap.put("displayName", block.group.name);
                groupMap.put("name", block.group);
                groupMap.put("blocks", blocks);
                groupMap.put("color", block.group.color);
                typeMap.put(block.group.name, groupMap);
            }
        }
        return typeMap;
    }
    public boolean isSupportedBlock(FlowType flowType){
        if(supportedFlowTypes == 0){
            return true;
        }
        return (supportedFlowTypes & flowType.getKey()) == flowType.getKey();
    }
}





