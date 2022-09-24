
package com.facilio.classification.command;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetHasChildCommand  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<ClassificationContext> classificationList = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.CLASSIFICATION);
        if (CollectionUtils.isEmpty(classificationList)) {
            return false;
        }
        if(!Constants.containsQueryParam(context, "hasChild")){
            return false;
        }

        Set<Long> classificationIds = classificationList.stream().map(ModuleBaseWithCustomFields::getId).collect(Collectors.toSet());
        if(CollectionUtils.isNotEmpty(classificationIds)){
            Map<Long, ClassificationContext> classificationMap = classificationList.stream().collect(Collectors.toMap(ClassificationContext::getId, Function.identity()));
            List<Map<String, Object>> hasChildClassificationIdsMapList= getHasChildClassificationIds(classificationIds);
            if(CollectionUtils.isNotEmpty(hasChildClassificationIdsMapList)){
                hasChildClassificationIdsMapList.forEach(map->{
                    Long hasChildClassificationId=(Long)map.get("parentClassification");
                    classificationMap.get(hasChildClassificationId).setHasChild(true);
                });
            }
        }

        return false;
    }
    private  List<Map<String, Object>> getHasChildClassificationIds(Set<Long> classificationIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.CLASSIFICATION;
        FacilioModule module = modBean.getModule(moduleName);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(new ArrayList<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.DISTINCT,modBean.getField("parentClassification",moduleName))
                .andCondition(CriteriaAPI.getCondition(modBean.getField("parentClassification", moduleName), StringUtils.join(classificationIds, ","), NumberOperators.EQUALS));
        List<Map<String, Object>> maps = builder.get();
        return  maps;
    }
}
