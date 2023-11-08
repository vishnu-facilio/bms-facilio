package com.facilio.readingrule.util;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ConnectedRuleUtil {
    public static Long getConnectedRuleIdWithLinkName(String linkName,String moduleName) throws Exception {
        if (!linkName.isEmpty()) {
            ModuleBean moduleBean = Constants.getModBean();
            FacilioModule module = moduleBean.getModule(moduleName);
            GenericSelectRecordBuilder selectRecordsBuilder = new GenericSelectRecordBuilder()
                    .select(Collections.singletonList(FieldFactory.getField("id", "ID", FieldType.NUMBER)))
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getCondition("LINK_NAME", "linkName", linkName, StringOperators.IS));

            List<Map<String, Object>> faultImpList = selectRecordsBuilder.get();
            if (CollectionUtils.isNotEmpty(faultImpList)) {
                Map<String, Object> prop =faultImpList.get(0);
                return (Long) prop.get("id");
            }
        }
        return null;
    }

    public static Map<Long, Long> getAllConnectedRuleIdsAndModuleIds(String moduleName) throws Exception {


        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<FacilioField> fields = new ArrayList<FacilioField>() {
            {
                add(FieldFactory.getField("id", "ID", FieldType.NUMBER));
                add(FieldFactory.getField("moduleId", "MODULEID", FieldType.NUMBER));
            }
        };

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(bean.getModule(moduleName).getTableName());

        List<Map<String, Object>> maps = selectBuilder.get();
        Map<Long, Long> impactIds = new HashMap<>();
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Object> res : maps) {
                impactIds.put((Long) res.get("id"), (Long) res.get("moduleId"));
            }
        }
        return impactIds;
    }

}
