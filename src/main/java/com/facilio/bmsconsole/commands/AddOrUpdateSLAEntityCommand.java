package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.SLAEntityContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.Map;

import static com.facilio.db.criteria.CriteriaAPI.updateConditionField;

public class AddOrUpdateSLAEntityCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        SLAEntityContext slaEntity = (SLAEntityContext) context.get(FacilioConstants.ContextNames.SLA_ENTITY);
        if (slaEntity != null) {
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Module cannot be empty");
            }
            slaEntity.setModuleId(module.getModuleId());

            FacilioField baseField = modBean.getField(slaEntity.getBaseFieldId(), module.getName());
            if (baseField == null) {
                throw new IllegalArgumentException("Base field cannot be empty");
            }
            FacilioField dueField = modBean.getField(slaEntity.getDueFieldId(), module.getName());
            if (dueField == null) {
                throw new IllegalArgumentException("Due field cannot be empty");
            }
            updateConditionField(moduleName,slaEntity.getCriteria());

            if (slaEntity.getCriteria() != null) {
                long criteriaId = CriteriaAPI.addCriteria(slaEntity.getCriteria());
                slaEntity.setCriteriaId(criteriaId);
            }
            else {
                slaEntity.setCriteriaId(-99);
            }

            if (slaEntity.getId() > 0) {
                updateSLAEntity(slaEntity);
            }
            else {
                addSLAEntity(slaEntity);
            }
            context.put(FacilioConstants.ContextNames.ID,slaEntity.getId());
        }
        return false;
    }

    private void updateSLAEntity(SLAEntityContext slaEntity) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getSLAEntityModule().getTableName())
                .fields(FieldFactory.getSLAEntityFields())
                .andCondition(CriteriaAPI.getIdCondition(slaEntity.getId(), ModuleFactory.getSLAEntityModule()));
        builder.update(FieldUtil.getAsProperties(slaEntity));
    }

    private void addSLAEntity(SLAEntityContext slaEntity) throws Exception {
        Map<String, Object> slaEntityMap = FieldUtil.getAsProperties(slaEntity);
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getSLAEntityModule().getTableName())
                .fields(FieldFactory.getSLAEntityFields());
        builder.insert(slaEntityMap);

        slaEntity.setId((long) slaEntityMap.get("id"));
    }
}
