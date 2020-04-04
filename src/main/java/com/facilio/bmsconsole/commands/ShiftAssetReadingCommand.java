package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

public class ShiftAssetReadingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Connection conn = null;
        conn = FacilioConnectionPool.INSTANCE.getConnection();
        long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
        long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
        Long resourceList = (Long) context.get(FacilioConstants.ContextNames.ASSET_ID);
        long readingFieldId = (long) context.get(FacilioConstants.ContextNames.FIELD_ID);
        long orgId = (long) context.get(FacilioConstants.ContextNames.ADMIN_DELTA_ORG);
        long duration = (long) context.get(FacilioConstants.ContextNames.DURATION);
        long type = (long) context.get(FacilioConstants.ContextNames.TYPE);
        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField readingField = bean.getField(readingFieldId);
        duration = duration * 60000;
        FacilioModule readingModule = bean.getModule(readingField.getModuleId());
        List<FacilioField> readingsFields = bean.getAllFields(readingModule.getName());
        Map<String, FacilioField> sourcefieldMap = FieldFactory.getAsMap(readingsFields);
            if (readingFieldId > -1) {
                readingsFields = bean.getAllFields(readingModule.getName());
                sourcefieldMap = FieldFactory.getAsMap(readingsFields);
            } else
            {
                return false;
            }

        SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
                .module(readingModule)
                .beanClass(ReadingContext.class)
                .select(readingsFields)
                .andCondition(  CriteriaAPI.getCondition(readingField, CommonOperators.IS_NOT_EMPTY));
        if (resourceList != null) {
            selectBuilder.andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("parentId"),Collections.singletonList(resourceList), NumberOperators.EQUALS));
        }
        if (startTime > 0 && endTime > 0  ) {
            selectBuilder.andCondition( CriteriaAPI.getCondition(sourcefieldMap.get("ttime"), startTime+","+endTime, DateOperators.BETWEEN));
        }
        SelectRecordsBuilder.BatchResult<ReadingContext> bs = selectBuilder.getInBatches("ID DESC", 5000);
        while (bs.hasNext()) {
            List<ReadingContext> readings = bs.get();
            List<FacilioField> updateFields = new ArrayList<>();
            updateFields.add(sourcefieldMap.get("ttime"));
            updateFields.add(sourcefieldMap.get("actualTtime"));

            List<FacilioField> whereFields = new ArrayList<>();
            whereFields.add(FieldFactory.getIdField(readingModule));

            if (readings != null ) {



                long finalDuration = duration;
                List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList = readings.stream().map(reading -> {
                    GenericUpdateRecordBuilder.BatchUpdateByIdContext updateVal = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
                    if (type == 1) {
                        updateVal.addUpdateValue("ttime",( reading.getTtime() + finalDuration));
                        updateVal.addUpdateValue("actualTtime",( reading.getActualTtime() + finalDuration));
                    }
                    if (type == 2) {
                        updateVal.addUpdateValue("ttime",( reading.getTtime() - finalDuration));
                        updateVal.addUpdateValue("actualTtime",( reading.getActualTtime() - finalDuration));
                    }
                    updateVal.setWhereId(reading.getId());
                    return updateVal;
                }).collect(Collectors.toList());

                new GenericUpdateRecordBuilder()
                        .table(readingModule.getTableName())
                        .fields(updateFields)
                        .batchUpdateById(batchUpdateList)
                ;
//                List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdates = new ArrayList<>();
//                for (Long id : readingDataIds) {
//                    GenericUpdateRecordBuilder.BatchUpdateByIdContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
//                    batchValue.setWhereId(id);
//                    batchValue.addUpdateValue("value", value);
//                    batchUpdates.add(batchValue);
//                }
//
//                GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
//                        .table(readingModule.getTableName());
//                builder.batchUpdateById(batchUpdates);
            }
//            shiftTime(readings, type, duration);
            //Process readings
        }
//        SelectRecordsBuilder<ReadingContext> builder = new SelectRecordsBuilder<ReadingContext>()
//                .module(readingModule)
//                .beanClass(ReadingContext.class)
//                .select(readingsFields)
//                .andCondition(  CriteriaAPI.getCondition(readingField, CommonOperators.IS_NOT_EMPTY));
//        if (resourceList != null) {
//            builder.andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("parentId"),Collections.singletonList(resourceList), NumberOperators.EQUALS));
//        }
//        if (startTime > 0 && endTime > 0  ) {
//            builder.andCondition( CriteriaAPI.getCondition(sourcefieldMap.get("ttime"), startTime+","+endTime, DateOperators.BETWEEN));
//        }
//       Condition timeCriteria = CriteriaAPI.getCondition(sourcefieldMap.get("ttime"), startTime+","+endTime, DateOperators.BETWEEN);
//      List<ReadingContext> readings = builder.get();
//
//        StringBuilder sql = new StringBuilder();
//        sql.append("UPDATE  ").append(readingModule.getTableName()).append("  SET  ");

//        for (String columnName : valueList) {
//            sql.append(columnName).append(" = ");
//            sql.append("(");
//            sql.append(columnName).append(" + ").append(weekDiff).append(")");
//            sql.append(",");
//        }
//        if (type == 2) {
//            sql.append("TTIME").append(" = ");
//            sql.append("(");
//            sql.append("TTIME").append(" + ").append(duration).append(")");
//            sql.append(",");
//            sql.append("ACTUAL_TTIME").append(" = ");
//            sql.append("(");
//            sql.append("ACTUAL_TTIME").append(" + ").append(duration).append(")");
////            value.put("TTIME", "(TTIME + " + duration +")");
////            value.put("ACTUAL_TTIME", "ACTUAL_TTIME + " + duration);
//        }
//        if (type == 1) {
//            sql.append("TTIME").append(" = ");
//            sql.append("(");
//            sql.append("TTIME").append(" + ").append(duration).append(")");
//            sql.append(",");
//            sql.append("ACTUAL_TTIME").append(" = ");
//            sql.append("(");
//            sql.append("ACTUAL_TTIME").append(" + ").append(duration).append(")");
//        }
//        sql.append(" WHERE ORGID = ").append(orgId);
//        if (startTime > 0 && endTime > 0  ) {
//            sql.append(" AND ");
//            sql.append("TTIME")
//                    .append("  BETWEEN ").append(startTime).append(" AND ").append(endTime);
//        }
//        if (resourceList != null && resourceList > -1) {
//            sql.append(" AND ").append(sourcefieldMap.get("parentId").getColumnName()).append(" = ").append( resourceList);
////            builder.andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("parentId"),Collections.singletonList(resourceList), NumberOperators.EQUALS));
//        }
//        sql.append(" ;");
//        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString());) {
//            int count = pstmt.executeUpdate();
//        } catch (Exception e) {
//            throw e;
//        }

//        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
//                .table(readingModule.getTableName())
//                .fields(readingsFields);
//         if (resourceList != null ) {
//             updateBuilder.andCondition(CriteriaAPI.getCondition(sourcefieldMap.get("parentId"), String.valueOf(resourceList), NumberOperators.EQUALS));
//         }
//        if (startTime > -1 && endTime > -1) {
//           updateBuilder.andCondition( CriteriaAPI.getCondition(sourcefieldMap.get("ttime"), startTime+","+endTime, DateOperators.BETWEEN));
//        }
//        Map<String, Object> value = new HashMap<>();
//        if (type == 1) {
//            value.put("TTIME", "(TTIME + " + duration +")");
////            value.put("ACTUAL_TTIME", "ACTUAL_TTIME + " + duration);
//        }
//        if (type == 2) {
//            value.put("TTIME", "(TTIME + " + duration +")");
////            value.put("ACTUAL_TTIME", "ACTUAL_TTIME - " + duration);
//        }
//        updateBuilder.update(value);
 //        conn.close();
        return false;
    }
}
