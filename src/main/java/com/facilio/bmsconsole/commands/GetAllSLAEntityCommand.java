package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.SLAEntityContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class GetAllSLAEntityCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Module cannot be empty");
            }

            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getSLAEntityModule().getTableName())
                    .select(FieldFactory.getSLAEntityFields())
                    .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
            List<SLAEntityContext> slaEntities = FieldUtil.getAsBeanListFromMapList(builder.get(), SLAEntityContext.class);

            if (CollectionUtils.isNotEmpty(slaEntities)) {
                Map<Long, SLAEntityContext> map = new HashMap<>();
                Set<Long> criteriaIds = new HashSet<>();
                for (SLAEntityContext entityContext : slaEntities) {
                    map.put(entityContext.getId(), entityContext);
                    if (entityContext.getCriteriaId() > 0) {
                        criteriaIds.add(entityContext.getCriteriaId());
                    }
                }
                if (CollectionUtils.isNotEmpty(criteriaIds)) {
                    Map<Long, Criteria> criteriaAsMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
                    if (MapUtils.isNotEmpty(criteriaAsMap)) {
                        for (SLAEntityContext entityContext : slaEntities) {
                            entityContext.setCriteria(criteriaAsMap.get(entityContext.getCriteriaId()));
                        }
                    }
                }
            }

            context.put(FacilioConstants.ContextNames.SLA_ENTITY_LIST, slaEntities);
        }
        return false;
    }
}
