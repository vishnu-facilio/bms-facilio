package com.facilio.agentv2.point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateRecordBuilder;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class EditPointCommand extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(EditPointCommand.class.getName());

    private static boolean containsAndNotNull(Context context, String key) {
        return ((context != null) && (key != null) && context.containsKey(key) && (context.get(key) != null));
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsAndNotNull(context, FacilioConstants.ContextNames.TO_UPDATE_MAP)) {
            boolean alterChildtable = false;
            int rowsUpdated = 0;
            if (containsAndNotNull(context, AgentConstants.UPDATE_CHILD)) {
                alterChildtable = (boolean) context.get(AgentConstants.UPDATE_CHILD);
            }
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule pointModule = AgentConstants.getPointModule();
            List<FacilioField>fields = new ArrayList<>();
            if (pointModule == null){
                pointModule = ModuleFactory.getPointModule();
                fields = FieldFactory.getPointFields();
            }
            else {
                fields = moduleBean.getAllFields("Point");
                fields.add(FieldFactory.getIdField(pointModule));
            }
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(pointModule.getTableName())
                    .fields(fields);
            if (containsAndNotNull(context, FacilioConstants.ContextNames.CRITERIA)) {
                builder.andCriteria((Criteria) context.get(FacilioConstants.ContextNames.CRITERIA));
                rowsUpdated = builder.update((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP));
                context.put(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
            } else {
                throw new Exception("criteris missing");
            }
            if (alterChildtable && containsAndNotNull(context, AgentConstants.CONTROLLER_TYPE)) {
                rowsUpdated += updateChild(context, (FacilioControllerType) context.get(AgentConstants.CONTROLLER_TYPE));
            }
            
            Map<String, Object> props =  (Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_MAP);
            Boolean isWritable = (Boolean) props.get(AgentConstants.WRITABLE);
            if(isWritable != null) {
            	FacilioChain chain = TransactionChainFactory.editRDMWritableChain();
            	chain.getContext().put(AgentConstants.WRITABLE, isWritable);
            	chain.getContext().put(AgentConstants.CONTROLLER_ID,context.get(AgentConstants.CONTROLLER_ID));
            	chain.getContext().put(AgentConstants.POINT_IDS, context.get(FacilioConstants.ContextNames.CRITERIA));
                chain.execute();
            }
            if (rowsUpdated > 0) {
                return true;
            }
        } else {
            throw new Exception(" to-update-map missing");
        }
        
        return false;
    }

    private int updateChild(Context context, FacilioControllerType controllerType) throws Exception {
        if (containsAndNotNull(context, FacilioConstants.ContextNames.TO_UPDATE_CHILD_MAP) && containsAndNotNull(context, FacilioConstants.ContextNames.CRITERIA)) {
        	FacilioModule module = PointsAPI.getPointModule(controllerType);
        	List<FacilioField> childPointFields = PointsAPI.getChildPointFields(controllerType);
        	
        	// temp handling..TODO remove by handling extended update
        	childPointFields = childPointFields.stream().filter(p -> p.getModule().getName() == module.getName())
        											   .collect(Collectors.toList());
        	
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(childPointFields)
                    .andCriteria((Criteria) context.get(FacilioConstants.ContextNames.CHILD_CRITERIA));
            return updateRecordBuilder.update((Map<String, Object>) context.get(FacilioConstants.ContextNames.TO_UPDATE_CHILD_MAP));
        }
        throw new Exception(" data to update child table missing");
    }
}
