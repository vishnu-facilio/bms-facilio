package com.facilio.bmsconsoleV3.commands.readingimportapp;

import com.facilio.bmsconsoleV3.context.readingimportapp.V3ReadingImportAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchReadingImportDataList extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getReadingImportFields())
                .table(ModuleFactory.getReadingImportAPPModule().getTableName());


        List<Map<String, Object>> props = selectBuilder.get();
        List<V3ReadingImportAppContext> readingDataList = new ArrayList<>();
        if (props != null && !props.isEmpty()) {
                for(Map<String, Object> prop : props) {
                    V3ReadingImportAppContext readingData = FieldUtil.getAsBeanFromMap(prop, V3ReadingImportAppContext.class);
                    readingDataList.add(readingData);
                }
            context.put("READING_IMPORT_DATA_LIST", readingDataList);
        }
        return false;
    }
}
