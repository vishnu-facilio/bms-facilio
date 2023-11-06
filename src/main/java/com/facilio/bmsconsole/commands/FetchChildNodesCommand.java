package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class FetchChildNodesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long buildingId = (long) context.get("buildingId");
        List<Map<String,Object>> siteChildren = getSiteNode((Long) context.get("siteId"),context);
        if(buildingId != -1){
            getTreeNode(buildingId, context);
            List<Map<String, Object>> nodeProps = (List<Map<String, Object>>) context.get("node");
            Map<String, List<Map<String, Object>>> parentIdVsChildrenMap = (Map<String, List<Map<String, Object>>>) context.get("parentIdVsChildrenMap");
            List<Map<String, Object>> childNodes = new ArrayList<>();
            List<Long> addedKeys = new ArrayList<>();
            for (Map<String, Object> nodeProp : nodeProps) {
                if (!nodeProp.containsKey("parentId")) {
                    Map<String, Object> modifiedProp = nodeProp;
                    if (parentIdVsChildrenMap.containsKey(nodeProp.get("id").toString())) {
                        getChilNodes(modifiedProp, parentIdVsChildrenMap, nodeProp.get("id").toString(), addedKeys);
                    }
                    modifiedProp.put("children", modifiedProp.get("children"));
                    modifiedProp.put("isOpen",true);
                    childNodes.add(modifiedProp);
                }
            }
            for (int i=0;i<siteChildren.size();i++){
                if(siteChildren.get(i).get("id").equals(childNodes.get(0).get("id"))){
                    siteChildren.remove(i);
                    siteChildren.add(i,childNodes.get(0));
                }
            }
        }
        context.put("node", siteChildren);
        return false;
        }

    private List<Map<String, Object>> getSiteNode(Long spaceId,Context context) throws Exception {
        List<BuildingContext> siteChildren = SpaceAPI.getSiteBuildings(spaceId);
        List<SpaceContext> independantSpaces = SpaceAPI.getIndependentSpaces(spaceId,context);
        List<Map<String, Object>> siteChildrenProps = new ArrayList<>();
        if(siteChildren != null && !siteChildren.isEmpty()) {
            for (BuildingContext building : siteChildren) {
                Map<String, Object> buildingProp = new HashMap<>();
                buildingProp.put("title", building.getName());
                buildingProp.put("id", building.getId());
                buildingProp.put("isOpen",false);
                buildingProp.put("isEnd",false);
                buildingProp.put("moduleName", building.getSpaceTypeEnum().getStringVal());
                siteChildrenProps.add(buildingProp);
            }
        }
        if(independantSpaces != null && !independantSpaces.isEmpty()) {
            for (SpaceContext space : independantSpaces) {
                Map<String, Object> buildingProp = new HashMap<>();
                buildingProp.put("title", space.getName());
                buildingProp.put("id", space.getId());
                buildingProp.put("moduleName", space.getSpaceTypeEnum().getStringVal());
                buildingProp.put("isOpen",false);
                buildingProp.put("isEnd", false);
                siteChildrenProps.add(buildingProp);
            }
        }
        return siteChildrenProps;
    }

    private void getChilNodes(Map<String, Object> parent, Map<String, List<Map<String, Object>>> parentIdVsChildrenMap, String key,List<Long> addedKeys) {
        parent.put("children",parentIdVsChildrenMap.get(key));
        List<Map<String,Object>> childNode = (List<Map<String, Object>>) parent.get("children");
        for(int i=0;i<childNode.size();i++){
            Map<String,Object> childrenProp = childNode.get(i);
            if(parentIdVsChildrenMap.containsKey(childrenProp.get("id").toString())){
                addedKeys.add((Long) childrenProp.get("id"));
                getChilNodes(childrenProp,parentIdVsChildrenMap,childrenProp.get("id").toString(),addedKeys);
            }
        }
    }

    private static void getTreeNode(long parentId, Context context) throws Exception{
        String moduleName = FacilioConstants.ContextNames.BASE_SPACE;
        List<FacilioField> moduleFields = Constants.getModBean().getAllFields(moduleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(moduleFields);
        FacilioModule baseSpaceModule = Constants.getModBean().getModule(moduleName);
        FacilioModule resourceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.RESOURCE);
        moduleFields.add(FieldFactory.getIdField(baseSpaceModule));

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(baseSpaceModule.getTableName())
                .select(moduleFields)
                .innerJoin(resourceModule.getTableName())
                .on(resourceModule.getTableName()+".id = "+baseSpaceModule.getTableName()+".id")
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED_TIME","sysDeletedTime",null, CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("building"), Collections.singleton(parentId), NumberOperators.EQUALS))
                .orCondition(CriteriaAPI.getCondition(fieldMap.get("site"), Collections.singleton(parentId),NumberOperators.EQUALS))
                .orCondition(CriteriaAPI.getCondition(fieldMap.get("floor"), Collections.singleton(parentId),NumberOperators.EQUALS));
        List<Map<String,Object>> props = selectRecordBuilder.get();
        List<Map<String,Object>> nodeProps = new ArrayList<>();

        Map<String,List<Map<String,Object>>> parentIdVsChildrenMap = new HashMap<>();

        if(CollectionUtils.isNotEmpty(props)){
            props.forEach((prop)->{
                Map<String,Object> nodeProp = new HashMap<>();
                nodeProp.put("title",prop.get("name"));
                nodeProp.put("id",prop.get("id"));
                nodeProp.put("moduleName", BaseSpaceContext.SpaceType.getType((Integer) prop.get("spaceType")).getStringVal());
                if(nodeProp.get("moduleName").equals("Floor")){
                    nodeProp.put("parentId",prop.get("building"));
                }else if(nodeProp.get("moduleName").equals("Space")){
                     if(prop.get("space5")!=null && ((long)prop.get("space5")!=-1)){
                        nodeProp.put("parentId",prop.get("space5"));
                    }else if(prop.get("space4")!=null && ((long)prop.get("space4")!=-1)){
                        nodeProp.put("parentId",prop.get("space4"));
                    }else if(prop.get("space3")!=null && ((long)prop.get("space3")!=-1)){
                        nodeProp.put("parentId",prop.get("space3"));
                    }else if(prop.get("space2")!=null && ((long)prop.get("space2")!=-1)){
                        nodeProp.put("parentId",prop.get("space2"));
                    }else if(prop.get("space1")!=null && ((long)prop.get("space1")!=-1)){
                        nodeProp.put("parentId",prop.get("space1"));
                    }else if(prop.get("floor")!=null && ((long)prop.get("floor")!=-1)) {
                         nodeProp.put("parentId", prop.get("floor"));
                     }
                }
                if(nodeProp.containsKey("parentId")){
                    if(parentIdVsChildrenMap.containsKey(nodeProp.get("parentId").toString())){
                        List<Map<String,Object>> childNode = parentIdVsChildrenMap.get(String.valueOf(nodeProp.get("parentId")));
                        childNode.add(nodeProp);
                    }else{
                        List<Map<String,Object>> childNode = new ArrayList<>();
                        childNode.add(nodeProp);
                        parentIdVsChildrenMap.put((String.valueOf(nodeProp.get("parentId"))),childNode);
                    }
                }
                nodeProps.add(nodeProp);
            });
            List<Map<String,Object>> oldNodeDetails = (List<Map<String, Object>>) context.getOrDefault("node",new ArrayList<>());
            oldNodeDetails.addAll(nodeProps);

            context.put("node",oldNodeDetails);
            context.put("parentIdVsChildrenMap",parentIdVsChildrenMap);
        }
    }

}
