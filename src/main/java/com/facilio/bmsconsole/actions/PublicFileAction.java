package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;

public class PublicFileAction extends FacilioAction {

    private Long expiryOn = System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000;
    public Long getExpiryOn() {
        return expiryOn;
    }
    public void setExpiryOn(Long expiryOn) {
        this.expiryOn = expiryOn;
    }

    private String fileName;
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private long fileId;
    public long getFileId() {
        return fileId;
    }
    public void setFileId(long fileId) {
        this.fileId = fileId;
    }
    private long publicFileId;
    public long getPublicFileId() {
        return publicFileId;
    }
    public void setPublicFileId(long publicFileId) {
        this.publicFileId = publicFileId;
    }

    private String fileContentType;
    public String getFileContentType() {
        return fileContentType;
    }
    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    private File file;
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }

    private String token;
    public String getToken() {
        return token;
    }
    public void setToken(String fileToken) {
        this.token = fileToken;
    }

    private Boolean isDownload;
    public Boolean getIsDownload() {
        if (isDownload == null) {
            isDownload = false;
        }
        return isDownload;
    }
    public void setIsDownload(Boolean isDownload) {
        this.isDownload = isDownload;
    }

    InputStream downloadStream;
    public InputStream getDownloadStream() {
        return downloadStream;
    }
    public void setDownloadStream(InputStream downloadStream) {
        this.downloadStream = downloadStream;
    }

    public String addPublicFile() throws Exception{
        FacilioChain facilioChain = FacilioChainFactory.getAddPublicFileChain();
        FacilioContext context = facilioChain.getContext();

        context.put(FacilioConstants.ContextNames.FILE_NAME,getFileName());
        context.put(FacilioConstants.ContextNames.FILE_CONTENT_TYPE,getFileContentType());
        context.put(FacilioConstants.ContextNames.FILE,getFile());
        context.put(FacilioConstants.ContextNames.EXPIRY_ON,getExpiryOn());

        facilioChain.execute();

        String publicFileUrl = (String) context.get(FacilioConstants.ContextNames.PUBLIC_FILE_URL);

        setResult(FacilioConstants.ContextNames.PUBLIC_FILE_URL,publicFileUrl);
        return SUCCESS;
    }

    public String addPublicUrlForFile() throws Exception {

        FacilioChain facilioChain = FacilioChainFactory.getAddPublicUrlForFileChain();
        FacilioContext context = facilioChain.getContext();

        context.put(FacilioConstants.ContextNames.FILE_ID,getFileId());
        facilioChain.execute();

        String publicFileUrl = (String) context.get(FacilioConstants.ContextNames.PUBLIC_FILE_URL);

        setResult(FacilioConstants.ContextNames.PUBLIC_FILE_URL,publicFileUrl);
        return SUCCESS;
    }

    public String deletePublicFile() throws Exception{

        FacilioChain facilioChain = FacilioChainFactory.getDeletePublicFileChain();
        FacilioContext context = facilioChain.getContext();

        context.put(FacilioConstants.ContextNames.PUBLIC_FILE_ID, getPublicFileId());

        facilioChain.execute();

        setResult(FacilioConstants.ContextNames.PUBLIC_FILE,"Public File Deleted");
        return SUCCESS;
    }

    public String previewPublicFile(){
        try {
            FacilioChain chain = ReadOnlyChainFactory.getPublicFilePreview();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.FILE_TOKEN_STRING, token);
            context.put(FacilioConstants.ContextNames.IS_DOWNLOAD, isDownload);
            chain.execute();
            setContentType((String) context.get(FacilioConstants.ContextNames.FILE_CONTENT_TYPE));
            if (context.get(FacilioConstants.ContextNames.FILE_NAME) != null) {
                String encodedFilename = URLEncoder.encode((String) context.get(FacilioConstants.ContextNames.FILE_NAME), "UTF-8").replace("+", " ");
                setFilename(encodedFilename);
            }
            if (context.get(FacilioConstants.ContextNames.FILE_DOWNLOAD_STREAM) != null) {
                setDownloadStream((InputStream) context.get(FacilioConstants.ContextNames.FILE_DOWNLOAD_STREAM));
            }
            int responseStatusCode = (int) context.getOrDefault(FacilioConstants.ContextNames.FILE_RESPONSE_STATUS, 200);
            if (responseStatusCode == 404) {
                return ERROR;
            } else if (responseStatusCode == 304) {
                return NONE;
            }
            return SUCCESS;
        }
        catch (Exception e) {
            return ERROR;
        }

    }

}
