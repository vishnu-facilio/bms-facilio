package com.facilio.modules.fields.relations;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class CalculateDependencyCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long startTime = System.currentTimeMillis();
        Map<String, List<ReadingContext>> readingMap = CommonCommandUtil.getReadingMap((FacilioContext) context);
        if (MapUtils.isEmpty(readingMap)) {
            return false;
        }

        Map<String, ReadingDataMeta> previousRDMMap = (Map<String, ReadingDataMeta>)context.get(FacilioConstants.ContextNames.PREVIOUS_READING_DATA_META);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        List<Pair<Long, FacilioField>> newRdmPairs = new ArrayList<>();
        for (Map.Entry<String, List<ReadingContext>> entry : readingMap.entrySet()) {
            String moduleName = entry.getKey();
            if (StringUtils.isEmpty(moduleName)) {
                continue;
            }

            List<ReadingContext> readings = entry.getValue();
            if (readings != null && !readings.isEmpty()) {
//                FacilioModule module = modBean.getModule(moduleName); // Commenting out since it's not used
                List<FacilioField> fields = modBean.getAllFields(moduleName);
//                Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields); // Commenting out since it's not used

                List<BaseRelationContext> relations = RelationFieldUtil.getAllRelations(fields);
                if (CollectionUtils.isNotEmpty(relations)) {
                    for (ReadingContext reading : readings) {
                        for (BaseRelationContext relation : relations) {
                            //Shouldn't we check if the base field has value first?
                            if (relation.getBaseField() != null && reading.getReading(relation.getBaseField().getName()) != null) {
                                ReadingDataMeta previousRDM = previousRDMMap.get(ReadingsAPI.getRDMKey(reading.getParentId(), relation.getBaseField()));
                                Object o = relation.calculateValue(reading, previousRDM);
                                if (o != null) {
                                    reading.addReading(relation.getDerivedField().getName(), o);
                                    newRdmPairs.add(Pair.of(reading.getParentId(), relation.getDerivedField()));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!newRdmPairs.isEmpty()) {
            List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(newRdmPairs) ;
            for(ReadingDataMeta meta : metaList) {
                previousRDMMap.put(ReadingsAPI.getRDMKey(meta.getResourceId(), meta.getField()), meta);
            }
        }
        LOGGER.debug("CalculateDependencyCommand time taken " + (System.currentTimeMillis() - startTime));
        return false;
    }
}
