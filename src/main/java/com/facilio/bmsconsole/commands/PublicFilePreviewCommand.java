package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.PublicFileContext;
import com.facilio.bmsconsole.util.FileJWTUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.filestore.PublicFileUtil;
import org.apache.commons.chain.Context;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PublicFilePreviewCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String token = (String) context.get(FacilioConstants.ContextNames.FILE_TOKEN_STRING);
        HttpServletResponse response = null;
        if (token == null) {
            response.setStatus(404);
            context.put(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 404);
            return false;
        }

        boolean isDownload = false;
        if (context.get(FacilioConstants.ContextNames.IS_DOWNLOAD) != null) {
            isDownload = (boolean) context.get(FacilioConstants.ContextNames.IS_DOWNLOAD);
        }
        HttpServletRequest request = ServletActionContext.getRequest();
        response = ServletActionContext.getResponse();
        Map<String, String> decodedjwtClaims = FileJWTUtil.validateJWT(token);

        if (decodedjwtClaims == null && decodedjwtClaims.isEmpty()) {
            response.setStatus(404);
            context.put(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 404);
            return false;
        }

        long expiresAt = Long.parseLong(decodedjwtClaims.get("expiresAt"));
        if (expiresAt != -1 && expiresAt < System.currentTimeMillis()) {
            response.setStatus(404);
            context.put(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 404);
            return false;
        }

        String namespace = decodedjwtClaims.get("namespace");
        long publicFileId = Long.parseLong(decodedjwtClaims.get("publicFileId"));

        PublicFileContext publicFileContext = FieldUtil.getAsBeanFromMap(PublicFileUtil.getPublicFileObj(publicFileId, -1), PublicFileContext.class);

        if(publicFileContext==null){
            response.setStatus(404);
            context.put(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 404);
            return false;
        }

        long fileId = publicFileContext.getFileId();
        long orgId = publicFileContext.getOrgId();

        String modifiedHeader = request.getHeader("If-Modified-Since");  
        if (modifiedHeader != null) {
            response.setStatus(304);
            context.put(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 304);
            return false;
        }

        ModuleCRUDBean bean = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", orgId);
        InputStream downloadStream = bean.getDownloadStream(namespace, fileId);
        FileInfo fileInfo = bean.getFileInfo(namespace, fileId);
        if (downloadStream == null) {
            throw new IllegalArgumentException("File not Found");
        }

        String dateStamp = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z").format(new Date());
        response.setHeader("Last-Modified", dateStamp);
        if (isDownload) {
            context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, "application/x-download");
            context.put(FacilioConstants.ContextNames.FILE_NAME, fileInfo.getFileName());
        } else {
            context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE, fileInfo.getContentType());
        }
        context.put(FacilioConstants.ContextNames.FILE_DOWNLOAD_STREAM, downloadStream);

        return false;
    }
}
