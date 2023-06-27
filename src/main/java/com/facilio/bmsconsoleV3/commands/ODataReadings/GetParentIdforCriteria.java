package com.facilio.bmsconsoleV3.commands.ODataReadings;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.ODataReadingsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetParentIdforCriteria extends FacilioCommand {

    private static final Logger LOGGER = LogManager.getLogger(GetParentIdforCriteria.class.getName());
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ODataReadingsContext readings = (ODataReadingsContext) context.get(FacilioConstants.ContextNames.ODATA_READING_VIEW);
        List<Long> parentId = new ArrayList<>();
        LOGGER.info("ReadingType - "+readings.getReadingType());
        if(readings.getReadingType() == 1){
            getAssetsforSpecificCondition(readings,parentId);
        }else if(readings.getReadingType() == 2){
            getSpaceCondition(readings,parentId);
        }else if(readings.getReadingType() == 3){
            parentId.add(readings.getCategorymoduleId());
        }
        context.put(FacilioConstants.ContextNames.PARENT_ID,parentId);
        return false;
    }

    private void getSpaceCondition(ODataReadingsContext context1, List<Long> spaceIds) throws Exception{
        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modbean.getAllFields("basespace");
        SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
                .table(ModuleFactory.getBaseSpaceModule().getTableName())
                .moduleName(ModuleFactory.getBaseSpaceModule().getName())
                .select(fields)
                .beanClass(BaseSpaceContext.class);
        if(context1.getCriteriaId() != -1 && context1.getCriteriaId() != 0){
            selectBuilder.andCriteria(CriteriaAPI.getCriteria(context1.getCriteriaId()));
        }
        List<Map<String,Object>> props= selectBuilder.getAsProps();
        props.stream().forEach(prop -> {
            spaceIds.add((Long) prop.get("id"));
        });
    }
    private void getAssetsforSpecificCondition(ODataReadingsContext readings, List<Long> assetIds) throws Exception{
        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modbean.getAllFields("asset");
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        SelectRecordsBuilder<ResourceContext> builder = new SelectRecordsBuilder<ResourceContext>()
                .select(fields)
                .table(ModuleFactory.getAssetsModule().getTableName())
                .moduleName("asset")
                .beanClass(ResourceContext.class)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("category"), String.valueOf(readings.getCategorymoduleId()),StringOperators.IS));
        if(readings.getCriteriaId() != -1l && readings.getCriteriaId() != 0){
            builder.andCriteria(CriteriaAPI.getCriteria(readings.getCriteriaId()));
        }
        List<Map<String,Object>> props= builder.getAsProps();
        props.stream().forEach(prop -> {
            assetIds.add((Long) prop.get("id"));
        });
    }
}