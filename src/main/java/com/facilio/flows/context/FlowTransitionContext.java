package com.facilio.flows.context;

import com.facilio.modules.FacilioStringEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class FlowTransitionContext extends FlowContext {

    private long flowId = -1;
    private String uniqueName;
    private Boolean isStartBlock;
    private long connectedFrom = -1;
    private long position = -1;
    private String configData;

   private Blocks blockType;

    public enum Blocks implements FacilioStringEnum {
        variable_declaration(Group.INPUT, "Set Variable"),

        assignment(Group.INPUT, "Assignment"),

        if_block(Group.DECISION, "If Block"),

        switch_block(Group.DECISION,"switch block"),

        email(Group.ACTION,"Email"),

        push_notification(Group.ACTION,"Push Notification")


        ;

        Blocks(Group group, String name) {
            this.group = group;
            this.name = name;
        }

        Group group;

        public Group getGroup() {
            return group;
        }

        String name;

        private static final Map<String,Object> GROUP_BLOCK_MAP = Collections.unmodifiableMap(initMap());

        public static Map<String,Object> getGroupBlockMap(){
            return GROUP_BLOCK_MAP;
        }

        private static Map<String, Object> initMap() {

            Map<String,Object> typeMap = new HashMap<>();
            for(Blocks block : values()) {

                List<Map<String,Object>> blocks = new ArrayList<>();
                Map<String,Object> result = typeMap.get(block.group.name) == null? new HashMap<>() : (Map<String, Object>) typeMap.get(block.group.name);
                blocks = result.get("blocks") == null ? new ArrayList<>() : (List<Map<String, Object>>) result.get("blocks");
                Map<String,Object> groupMap = new HashMap<>();
                Map<String,Object> blockMap = new HashMap<>();
                blockMap.put("displayName",block.name);
                blockMap.put("name",block);
                blocks.add(blockMap);

                if (typeMap.get(block.group.name) == null){
                    groupMap.put("displayName",block.group.name);
                    groupMap.put("name",block.group);
                    groupMap.put("blocks",blocks);
                    typeMap.put(block.group.name,groupMap);
                }
            }
            return typeMap;
        }

    }

    public enum Group {
        INPUT("Input"),
        DECISION("Decision"),
        ACTION("Action");

        private final String name;
        Group(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        
    }


}
