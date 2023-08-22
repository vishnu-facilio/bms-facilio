package com.facilio.workflowv2.util;

import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.contexts.WorkflowFieldsRelContext;
import com.facilio.workflowv2.contexts.WorkflowModuleRelContext;
import com.facilio.workflowv2.contexts.WorkflowNameSpaceRelContext;
import org.apache.commons.collections4.CollectionUtils;
import org.owasp.esapi.util.CollectionsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class WorkflowRelUtil {
    private static org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(WorkflowRelUtil.class.getName());
    private static final String WORKFLOW_REL_TABLE= "Workflow_Rel_Modules";
    private static final String NAMESPACE_REL_TABLE= "Workflow_Rel_Namespaces";
    private static final String FIELD_REL_TABLE="Workflow_Rel_Fields";


    public static void addWorkflowRelations(WorkflowContext workflowContext ){
    	
    	try {
    		addWorkflowModuleRelations(workflowContext.getId(), workflowContext.getModuleRels());
            addWorkflowNameSpaceRelations(workflowContext.getId(), workflowContext.getNameSpaceRels());
            addWorkflowNameFieldRelations(workflowContext.getId(), workflowContext.getFieldRels());
    	}
    	catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    /** Util for adding workflow - module relation */
    private static void insertWorkflowModuleRelations(long workflowId, Set<WorkflowModuleRelContext> moduleRels) throws Exception{
        deleteExistingWorkflowRel(workflowId);
        List <WorkflowModuleRelContext> moduleRelContexts= new ArrayList<>(moduleRels);
        List<FacilioField> addFields = new ArrayList<>();
        addFields.add(FieldFactory.getDefaultField("workflowId","Workflow ID","WORKFLOWID", FieldType.NUMBER));
        addFields.add(FieldFactory.getDefaultField("moduleId","Module ID","MODULEID", FieldType.NUMBER));

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(WORKFLOW_REL_TABLE)
                .fields(addFields);
        builder.addRecords(FieldUtil.getAsMapList(moduleRelContexts,WorkflowModuleRelContext.class ));
        builder.save();
    }

    private static void deleteExistingWorkflowRel(long workflowId) throws Exception{
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(WORKFLOW_REL_TABLE);
        builder.andCondition(CriteriaAPI.getCondition("WORKFLOWID", "workflowId", String.valueOf(workflowId), NumberOperators.EQUALS));
        builder.delete();
    }

    private static void addWorkflowModuleRelations(long workflowId, Set<WorkflowModuleRelContext> moduleRels ) throws Exception{
        if(CollectionUtils.isNotEmpty(moduleRels)){
            for (WorkflowModuleRelContext moduleRel : moduleRels) {
                moduleRel.setWorkflowId(workflowId);
            }
            insertWorkflowModuleRelations(workflowId, moduleRels);
        }
    }


    /** Util for adding workflow - userNameSpaceFunction relation */
    private static void insertWorkflowNameSpaceRelations(long workflowId, Set<WorkflowNameSpaceRelContext> nameSpaceRels) throws Exception{
        deleteExistingNameSpaceRel(workflowId);
        List <WorkflowNameSpaceRelContext> nameSpaceRelContexts= new ArrayList<>(nameSpaceRels);
        List<FacilioField> addFields = new ArrayList<>();
        addFields.add(FieldFactory.getDefaultField("workflowId","Workflow ID","WORKFLOWID", FieldType.NUMBER));
        addFields.add(FieldFactory.getDefaultField("functionId","Function ID","FUNCTIONID", FieldType.NUMBER));

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(NAMESPACE_REL_TABLE)
                .fields(addFields);
        builder.addRecords(FieldUtil.getAsMapList(nameSpaceRelContexts,WorkflowNameSpaceRelContext.class ));
        builder.save();
    }

    private static void deleteExistingNameSpaceRel(long workflowId) throws Exception{
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(NAMESPACE_REL_TABLE);
        builder.andCondition(CriteriaAPI.getCondition("WORKFLOWID", "workflowId", String.valueOf(workflowId), NumberOperators.EQUALS));
        builder.delete();
    }

    private static void addWorkflowNameSpaceRelations(long workflowId, Set<WorkflowNameSpaceRelContext> nameSpaceRels ) throws Exception{
        if(CollectionUtils.isNotEmpty(nameSpaceRels)){
            for (WorkflowNameSpaceRelContext moduleRel : nameSpaceRels) {
                moduleRel.setWorkflowId(workflowId);
            }
            insertWorkflowNameSpaceRelations(workflowId, nameSpaceRels);
        }
    }


    /** Util for adding workflow - fields relation */
    private static void insertWorkflowFieldsRelations(long workflowId, Set<WorkflowFieldsRelContext> fieldsRel) throws Exception{
        deleteExistingFieldsRel(workflowId);
        List<WorkflowFieldsRelContext> fieldsRelContexts = new ArrayList<>(fieldsRel);
        List<FacilioField> addFields = new ArrayList<>();
        addFields.add(FieldFactory.getDefaultField("workflowId","Workflow ID","WORKFLOWID", FieldType.NUMBER));
        addFields.add(FieldFactory.getDefaultField("moduleId","Module ID","MODULEID", FieldType.NUMBER));
        addFields.add(FieldFactory.getDefaultField("fieldId","Field ID","FIELDID", FieldType.NUMBER));

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(FIELD_REL_TABLE)
                .fields(addFields);
        builder.addRecords(FieldUtil.getAsMapList(fieldsRelContexts,WorkflowFieldsRelContext.class ));
        builder.save();
    }

    private static void deleteExistingFieldsRel(long workflowId) throws Exception{
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(FIELD_REL_TABLE);
        builder.andCondition(CriteriaAPI.getCondition("WORKFLOWID", "workflowId", String.valueOf(workflowId), NumberOperators.EQUALS));
        builder.delete();
    }

    private static void addWorkflowNameFieldRelations(long workflowId, Set<WorkflowFieldsRelContext> fieldRels )  throws Exception{
        if(CollectionUtils.isNotEmpty(fieldRels)){
            for (WorkflowFieldsRelContext fieldRel : fieldRels) {
                fieldRel.setWorkflowId(workflowId);
            }
            insertWorkflowFieldsRelations(workflowId, fieldRels);
        }
    }
}

