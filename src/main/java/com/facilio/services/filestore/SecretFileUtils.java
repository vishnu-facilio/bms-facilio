package com.facilio.services.filestore;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

import java.util.List;
import java.util.Map;

public class SecretFileUtils {
    static FileInfo getSecretFileInfo(String fileName) throws Exception {
        if(fileName!=null && fileName.length()!=0) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(fileName);
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getSecretFileModule().getTableName())
                    .select(FieldFactory.getSecretFileFields())
                    .andCondition(CriteriaAPI.getCondition(FieldFactory.getSecretFileNameField(), fileName, StringOperators.IS));
            List<Map<String, Object>> list = selectRecordBuilder.get();
            if (list == null || list.size()==0) return null;
            Map<String, Object> row = list.get(0);
            if (row.size() == 0) return null;
            if (row.containsKey("fileId")) fileInfo.setFileId(Long.parseLong(row.get("fileId").toString()));
            if (row.containsKey("contentType")) fileInfo.setContentType(row.get("contentType").toString());
            if (row.containsKey("fileSize")) fileInfo.setFileSize(Long.parseLong(row.get("fileSize").toString()));
            if (row.containsKey("filePath")) fileInfo.setFilePath(row.get("filePath").toString());
            return fileInfo;
        }
        else return null;
    }
}
