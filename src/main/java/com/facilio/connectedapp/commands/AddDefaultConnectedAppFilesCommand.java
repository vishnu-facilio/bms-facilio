package com.facilio.connectedapp.commands;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.command.FacilioCommand;
import com.facilio.connectedapp.context.ConnectedAppFileContext;
import com.facilio.connectedapp.util.ConnectedAppHostingAPI;
import com.facilio.fw.FacilioException;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStoreFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class AddDefaultConnectedAppFilesCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(AddDefaultConnectedAppFilesCommand.class.getName());

    private static final String sampleIndexPageBase64Encoded = "PGh0bWw+CjxoZWFkPgogICAgPG1ldGEgY2hhcnNldD0idXRmLTgiIC8+CiAgICA8dGl0bGU+RmFjaWxpbyBDb25uZWN0ZWQgQXBwcyBRdWlja3N0YXJ0PC90aXRsZT4KICAgIAogICAgPGxpbmsgcmVsPSJzdHlsZXNoZWV0IiBocmVmPSIvYXBwL3N0eWxlcy9hcHAuY3NzIj4KCiAgICA8IS0tIEZhY2lsaW8gQ29ubmVjdGVkIEFwcHMgU0RLLS0+CiAgICA8c2NyaXB0IHR5cGU9InRleHQvamF2YXNjcmlwdCIgc3JjPSJodHRwczovL3N0YXRpYy5mYWNpbGlvLmNvbS9hcHBzLXNkay9sYXRlc3QvZmFjaWxpb19hcHBzX3Nkay5taW4uanMiPjwvc2NyaXB0PgogICAgCiAgICA8IS0tIFZ1ZUpTLS0+CiAgICA8c2NyaXB0IHR5cGU9InRleHQvamF2YXNjcmlwdCIgc3JjPSJodHRwczovL2Nkbi5qc2RlbGl2ci5uZXQvbnBtL3Z1ZUAyLjcuMTQiPjwvc2NyaXB0PgoKICAgIDxzY3JpcHQgdHlwZT0idGV4dC9qYXZhc2NyaXB0IiBzcmM9Ii9hcHAvc2NyaXB0cy9hcHAuanMiPjwvc2NyaXB0Pgo8L2hlYWQ+Cjxib2R5PgogICAgPGRpdiBpZD0iYXBwIiBjbGFzcz0iY2VudGVyIGhlaWdodDEwMCI+CiAgICAgICAgPGRpdj4KICAgICAgICAgICAgPGltZyBzcmM9Ii9hcHAvaW1hZ2VzL2xvZ28uc3ZnIiB3aWR0aD0iMTAwIi8+CiAgICAgICAgICAgIDxoMT5GYWNpbGlvIENvbm5lY3RlZCBBcHBzIFF1aWNrc3RhcnQ8L2gxPgogICAgICAgICAgICA8cCB2LWlmPSJjdXJyZW50VXNlciI+SGVsbG8gPGI+e3tjdXJyZW50VXNlci5uYW1lfX08L2I+LCBUaGlzIGlzIGEgc2FtcGxlIGNvbm5lY3RlZCBhcHAgd2lkZ2V0LjwvcD4KICAgICAgICA8L2Rpdj4KICAgIDwvZGl2PgogICAgPHNjcmlwdCB0eXBlPSJ0ZXh0L2phdmFzY3JpcHQiPgogICAgICAgIHZhciBhcHAgPSBuZXcgVnVlKHsKICAgICAgICAgICAgZWw6ICcjYXBwJywKICAgICAgICAgICAgZGF0YTogewogICAgICAgICAgICAgICAgY3VycmVudFVzZXI6IG51bGwKICAgICAgICAgICAgfSwKICAgICAgICAgICAgbW91bnRlZCgpIHsKICAgICAgICAgICAgICAgIHdpbmRvdy5mYWNpbGlvQXBwID0gRmFjaWxpb0FwcFNESy5pbml0KCk7CiAgICAgICAgICAgICAgICB3aW5kb3cuZmFjaWxpb0FwcC5vbigiYXBwLmxvYWRlZCIsIChkYXRhKSA9PiB7CiAgICAgICAgICAgICAgICAgICAgdGhpcy5jdXJyZW50VXNlciA9IHdpbmRvdy5mYWNpbGlvQXBwLmdldEN1cnJlbnRVc2VyKCk7CiAgICAgICAgICAgICAgICAgICAgdGhpcy5mZXRjaERhdGEoKTsKICAgICAgICAgICAgICAgIH0pOwogICAgICAgICAgICB9LAogICAgICAgICAgICBtZXRob2RzOiB7CiAgICAgICAgICAgICAgICBmZXRjaERhdGEoKSB7CiAgICAgICAgICAgICAgICAgICAgY29uc29sZS5sb2coaXNFbXB0eSh0aGlzLmN1cnJlbnRVc2VyKSkKICAgICAgICAgICAgICAgIH0KICAgICAgICAgICAgfSwKICAgICAgICB9KQogICAgPC9zY3JpcHQ+CjwvYm9keT4KPC9odG1sPg==";
    private static final String sampleAppCssBase64Encoded = "Ym9keSB7CiAgICBwYWRkaW5nOiAwOwogICAgbWFyZ2luOiAwOwogICAgZm9udC1zaXplOiAxNHB4OwogICAgZm9udC1mYW1pbHk6IC1hcHBsZS1zeXN0ZW0sCiAgICAgICAgQmxpbmtNYWNTeXN0ZW1Gb250LAogICAgICAgIFNlZ29lIFVJLAogICAgICAgIFJvYm90bywKICAgICAgICBPeHlnZW4sCiAgICAgICAgVWJ1bnR1LAogICAgICAgIEZpcmEgU2FucywKICAgICAgICBEcm9pZCBTYW5zLAogICAgICAgIEhlbHZldGljYSBOZXVlLAogICAgICAgIHNhbnMtc2VyaWY7Cn0KLmNlbnRlciB7CiAgICBkaXNwbGF5OiBmbGV4OwogICAgYWxpZ24taXRlbXM6IGNlbnRlcjsKICAgIGp1c3RpZnktY29udGVudDogY2VudGVyOwogICAgdGV4dC1hbGlnbjogY2VudGVyOwp9Ci5oZWlnaHQxMDAgewogICAgaGVpZ2h0OiAxMDAlOwp9";
    private static final String sampleAppJsBase64Encoded = "Y29uc3QgaXNFbXB0eSA9ICh2YWx1ZSkgPT4gewogIHJldHVybiAoCiAgICB2YWx1ZSA9PT0gdW5kZWZpbmVkIHx8CiAgICB2YWx1ZSA9PT0gbnVsbCB8fAogICAgTnVtYmVyKHZhbHVlKSA9PT0gLTEgfHwKICAgICh0eXBlb2YgdmFsdWUgPT09ICdvYmplY3QnICYmCiAgICAgICEodmFsdWUgaW5zdGFuY2VvZiBCbG9iKSAmJgogICAgICBPYmplY3Qua2V5cyh2YWx1ZSkubGVuZ3RoID09PSAwKSB8fAogICAgKHR5cGVvZiB2YWx1ZSA9PT0gJ3N0cmluZycgJiYgdmFsdWUudHJpbSgpLmxlbmd0aCA9PT0gMCkKICApCn0=";
    private static final String sampleImageBase64Encoded = "PHN2ZyBpZD0ibG9nby0zNyIgd2lkdGg9IjQyIiBoZWlnaHQ9IjM4IiB2aWV3Qm94PSIwIDAgNDIgMzgiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+IDxwYXRoIGQ9Ik01Ljc0NjYxIDI4LjcyNTlDNy4yODY3OCAyOS44MDAyIDkuNzgzODkgMjkuODAwMiAxMS4zMjQxIDI4LjcyNTlDMTIuODY0MiAyNy42NTE2IDEyLjg2NDIgMjUuOTA5OCAxMS4zMjQxIDI0LjgzNTVDOS43ODM4OSAyMy43NjEyIDcuMjg2NzggMjMuNzYxMiA1Ljc0NjYxIDI0LjgzNTVDNC4yMDY0NCAyNS45MDk4IDQuMjA2NDQgMjcuNjUxNiA1Ljc0NjYxIDI4LjcyNTlaIiBjbGFzcz0iY2N1c3RvbSIgZmlsbD0iIzI1Q0FBQyI+PC9wYXRoPiA8cGF0aCBkPSJNMjEuNzMyMiAxNC4xMzcxQzI0LjA0MjUgMTUuNzQ4NSAyNy43ODgxIDE1Ljc0ODUgMzAuMDk4NCAxNC4xMzcxQzMyLjQwODYgMTIuNTI1NiAzMi40MDg2IDkuOTEyOTggMzAuMDk4NCA4LjMwMTU1QzI3Ljc4ODEgNi42OTAxMSAyNC4wNDI1IDYuNjkwMTEgMjEuNzMyMiA4LjMwMTU1QzE5LjQyMiA5LjkxMjk4IDE5LjQyMiAxMi41MjU2IDIxLjczMjIgMTQuMTM3MVoiIGNsYXNzPSJjY3VzdG9tIiBmaWxsPSIjMjVDQUFDIj48L3BhdGg+IDxwYXRoIGQ9Ik0xMy4yNDY0IDIxLjQzMTVDMTUuMTcxNiAyMi43NzQzIDE4LjI5MyAyMi43NzQzIDIwLjIxODIgMjEuNDMxNUMyMi4xNDM0IDIwLjA4ODYgMjIuMTQzNCAxNy45MTE0IDIwLjIxODIgMTYuNTY4NUMxOC4yOTMgMTUuMjI1NyAxNS4xNzE2IDE1LjIyNTcgMTMuMjQ2NCAxNi41Njg1QzExLjMyMTIgMTcuOTExNCAxMS4zMjEyIDIwLjA4ODYgMTMuMjQ2NCAyMS40MzE1WiIgY2xhc3M9ImNjdXN0b20iIGZpbGw9IiMyNUNBQUMiPjwvcGF0aD4gPHBhdGggZD0iTTAuODY2MzQ1IDIwLjQ1ODlDMi4wMjE0NyAyMS4yNjQ2IDMuODk0MyAyMS4yNjQ2IDUuMDQ5NDMgMjAuNDU4OUM2LjIwNDU1IDE5LjY1MzIgNi4yMDQ1NSAxOC4zNDY5IDUuMDQ5NDMgMTcuNTQxMUMzLjg5NDMgMTYuNzM1NCAyLjAyMTQ3IDE2LjczNTQgMC44NjYzNDUgMTcuNTQxMUMtMC4yODg3ODIgMTguMzQ2OSAtMC4yODg3ODEgMTkuNjUzMiAwLjg2NjM0NSAyMC40NTg5WiIgY2xhc3M9ImNjdXN0b20iIGZpbGw9IiMyNUNBQUMiPjwvcGF0aD4gPHBhdGggZD0iTTEzLjI0NjQgNS44NzAwOEMxNS4xNzE2IDcuMjEyOTQgMTguMjkzIDcuMjEyOTQgMjAuMjE4MiA1Ljg3MDA4QzIyLjE0MzQgNC41MjcyMiAyMi4xNDM0IDIuMzUwMDEgMjAuMjE4MiAxLjAwNzE1QzE4LjI5MyAtMC4zMzU3MTUgMTUuMTcxNiAtMC4zMzU3MTYgMTMuMjQ2NCAxLjAwNzE1QzExLjMyMTIgMi4zNTAwMSAxMS4zMjEyIDQuNTI3MjIgMTMuMjQ2NCA1Ljg3MDA4WiIgY2xhc3M9ImNjdXN0b20iIGZpbGw9IiMyNUNBQUMiPjwvcGF0aD4gPHBhdGggZD0iTTUuNzQ2NjEgMTMuMTY0NUM3LjI4Njc4IDE0LjIzODggOS43ODM4OSAxNC4yMzg4IDExLjMyNDEgMTMuMTY0NUMxMi44NjQyIDEyLjA5MDIgMTIuODY0MiAxMC4zNDg0IDExLjMyNDEgOS4yNzQxNUM5Ljc4Mzg5IDguMTk5ODYgNy4yODY3OCA4LjE5OTg2IDUuNzQ2NjEgOS4yNzQxNUM0LjIwNjQ0IDEwLjM0ODQgNC4yMDY0NSAxMi4wOTAyIDUuNzQ2NjEgMTMuMTY0NVoiIGNsYXNzPSJjY3VzdG9tIiBmaWxsPSIjMjVDQUFDIj48L3BhdGg+IDxwYXRoIGQ9Ik0xMy4yNDY0IDM2Ljk5MjlDMTUuMTcxNiAzOC4zMzU3IDE4LjI5MyAzOC4zMzU3IDIwLjIxODIgMzYuOTkyOUMyMi4xNDM0IDM1LjY1IDIyLjE0MzQgMzMuNDcyOCAyMC4yMTgyIDMyLjEyOTlDMTguMjkzIDMwLjc4NzEgMTUuMTcxNiAzMC43ODcxIDEzLjI0NjQgMzIuMTI5OUMxMS4zMjEyIDMzLjQ3MjggMTEuMzIxMiAzNS42NSAxMy4yNDY0IDM2Ljk5MjlaIiBjbGFzcz0iY2N1c3RvbSIgZmlsbD0iIzI1Q0FBQyI+PC9wYXRoPiA8cGF0aCBkPSJNMzEuOTAxMSAyMS45MTc4QzM0LjIxMTQgMjMuNTI5MiAzNy45NTcxIDIzLjUyOTIgNDAuMjY3MyAyMS45MTc4QzQyLjU3NzYgMjAuMzA2NCA0Mi41Nzc2IDE3LjY5MzcgNDAuMjY3MyAxNi4wODIzQzM3Ljk1NzEgMTQuNDcwOCAzNC4yMTE0IDE0LjQ3MDggMzEuOTAxMSAxNi4wODIzQzI5LjU5MDkgMTcuNjkzNyAyOS41OTA5IDIwLjMwNjQgMzEuOTAxMSAyMS45MTc4WiIgY2xhc3M9ImNjdXN0b20iIGZpbGw9IiMyNUNBQUMiPjwvcGF0aD4gPHBhdGggZD0iTTIxLjczMjIgMjkuNjk4NEMyNC4wNDI1IDMxLjMwOTkgMjcuNzg4MSAzMS4zMDk5IDMwLjA5ODQgMjkuNjk4NEMzMi40MDg2IDI4LjA4NyAzMi40MDg2IDI1LjQ3NDQgMzAuMDk4NCAyMy44NjI5QzI3Ljc4ODEgMjIuMjUxNSAyNC4wNDI1IDIyLjI1MTUgMjEuNzMyMiAyMy44NjI5QzE5LjQyMiAyNS40NzQ0IDE5LjQyMiAyOC4wODcgMjEuNzMyMiAyOS42OTg0WiIgY2xhc3M9ImNjdXN0b20iIGZpbGw9IiMyNUNBQUMiPjwvcGF0aD4gPC9zdmc+";

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ConnectedAppContext connectedApp = (ConnectedAppContext) context.get(ConnectedAppHostingAPI.Constants.CONNECTED_APP);
        if (connectedApp != null && connectedApp.getId() > 0) {
            if (connectedApp.getSourceZip() != null) {
                uploadSourceZip(connectedApp);
            }
            else {
                addDefaultFiles(connectedApp);
            }
        }

        return false;
    }

    private void uploadSourceZip(ConnectedAppContext connectedApp) throws Exception {
        File sourceZip = connectedApp.getSourceZip();
        ZipFile sourceZipFile = new ZipFile(sourceZip);
        String rootFolder = null;
        if (sourceZip.length() > ConnectedAppHostingAPI.Constants.SOURCE_ZIP_MAX_SIZE) {
            throw new FacilioException("Source Zip file size cannot be more than 10MB.");
        }
        else if (sourceZipFile.size() > ConnectedAppHostingAPI.Constants.SOURCE_ZIP_MAX_FILES) {
            throw new FacilioException("Number of files in the zip file cannot be more than 100 files.");
        }
        else {
            Enumeration<ZipEntry> zipFiles = (Enumeration<ZipEntry>) sourceZipFile.entries();
            while(zipFiles.hasMoreElements()) {
                ZipEntry entry = zipFiles.nextElement();
                String entryName = entry.getName();
                String[] filePathArray = entryName.split(File.separator);
                if (rootFolder == null) {
                    rootFolder = filePathArray[0];
                }
                if ((!entry.isDirectory() && filePathArray.length < 2) || !entryName.startsWith(rootFolder)) {
                    throw new FacilioException("Source zip should have only one root folder.");
                }
            }
        }
        ConnectedAppFileContext appFolder = ConnectedAppHostingAPI.addAppFolder(connectedApp.getId(), rootFolder);

        Map<String, Long> folderIdMap = new HashMap<>();
        folderIdMap.put(rootFolder, appFolder.getId());

        Enumeration<ZipEntry> zipFiles = (Enumeration<ZipEntry>) sourceZipFile.entries();
        while(zipFiles.hasMoreElements()) {
            ZipEntry entry = zipFiles.nextElement();
            String[] filePathArray = entry.getName().split(File.separator);
            int arrayLen = filePathArray.length;
            if (!entry.isDirectory()) {
                arrayLen = arrayLen - 1; // if its file, removing the file name entry from the array
            }
            if (filePathArray.length >= 2) {
                String parentPath = filePathArray[0];
                // skip the first folder (root) entry because its already created
                for (int i = 1; i < arrayLen; i++) {
                    String folderName = filePathArray[i];
                    String folderPath = parentPath + File.separator + folderName;
                    if (!folderIdMap.containsKey(folderPath)) {
                        ConnectedAppFileContext subFolder = new ConnectedAppFileContext();
                        subFolder.setFileName(folderName);
                        subFolder.setDirectory(true);
                        subFolder.setParentId(folderIdMap.get(parentPath));
                        subFolder.setConnectedAppId(connectedApp.getId());
                        subFolder = ConnectedAppHostingAPI.addConnectedAppFile(connectedApp.getId(), subFolder);
                        folderIdMap.put(folderPath, subFolder.getId());
                    }
                    parentPath = folderPath;
                }
            }
        }

        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(connectedApp.getSourceZip()));
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            if (!entry.isDirectory()) {
                String fileName = FilenameUtils.getName(entry.getName());
                String folderPath = entry.getName().replace(File.separator + fileName, "");
                if (folderIdMap.containsKey(folderPath)) {
                    ConnectedAppFileContext subFile = extractFile(connectedApp, zipIn, fileName, folderIdMap.get(folderPath));
                }
                else {
                    LOGGER.warning("Folder not available for the path: "+folderPath);
                }
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

        ConnectedAppHostingAPI.addConnectedAppDeployment(connectedApp.getId(), true, "Uploaded version");
    }

    private static ConnectedAppFileContext extractFile(ConnectedAppContext connectedApp, ZipInputStream zipIn, String fileName, Long parentFolderId) throws Exception {
        File outFile = File.createTempFile(fileName, "");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
        long fileSize = outFile.length();
        String contentType = ConnectedAppHostingAPI.getContentType(fileName);
        long fileId = FacilioFactory.getFileStore().addFile(fileName, outFile, contentType);
        try {
            outFile.delete();
        } catch (Exception e) {
            LOGGER.info("Unable to delete temp file..");
        }

        ConnectedAppFileContext subFile = new ConnectedAppFileContext();
        subFile.setFileName(fileName);
        subFile.setContentType(contentType);
        subFile.setFileSize(fileSize);
        subFile.setFileId(fileId);
        subFile.setDirectory(false);
        subFile.setParentId(parentFolderId);
        subFile.setConnectedAppId(connectedApp.getId());
        subFile = ConnectedAppHostingAPI.addConnectedAppFile(connectedApp.getId(), subFile);
        return subFile;
    }

    private void addDefaultFiles(ConnectedAppContext connectedApp) throws Exception {
        ConnectedAppFileContext appFolder = ConnectedAppHostingAPI.addAppFolder(connectedApp.getId());

        ConnectedAppFileContext stylesFolder = new ConnectedAppFileContext();
        stylesFolder.setFileName("styles");
        stylesFolder.setDirectory(true);
        stylesFolder.setParentId(appFolder.getId());
        stylesFolder.setConnectedAppId(connectedApp.getId());
        stylesFolder = ConnectedAppHostingAPI.addConnectedAppFile(connectedApp.getId(), stylesFolder);

        String appCssCode = new String(Base64.decodeBase64(sampleAppCssBase64Encoded));
        long appCssFileId = FileStoreFactory.getInstance().getFileStore().addFile("app.css", appCssCode, "text/css");
        ConnectedAppFileContext appCss = new ConnectedAppFileContext();
        appCss.setFileName("app.css");
        appCss.setContentType("text/css");
        appCss.setFileSize(Long.valueOf(appCssCode.length()));
        appCss.setFileId(appCssFileId);
        appCss.setDirectory(false);
        appCss.setParentId(stylesFolder.getId());
        appCss.setConnectedAppId(connectedApp.getId());
        appCss = ConnectedAppHostingAPI.addConnectedAppFile(connectedApp.getId(), appCss);

        ConnectedAppFileContext imagesFolder = new ConnectedAppFileContext();
        imagesFolder.setFileName("images");
        imagesFolder.setDirectory(true);
        imagesFolder.setParentId(appFolder.getId());
        imagesFolder.setConnectedAppId(connectedApp.getId());
        imagesFolder = ConnectedAppHostingAPI.addConnectedAppFile(connectedApp.getId(), imagesFolder);

        String logoSvg = new String(Base64.decodeBase64(sampleImageBase64Encoded));
        long logoFileId = FileStoreFactory.getInstance().getFileStore().addFile("logo.svg", logoSvg, "image/svg+xml");
        ConnectedAppFileContext logoFile = new ConnectedAppFileContext();
        logoFile.setFileName("logo.svg");
        logoFile.setContentType("image/svg+xml");
        logoFile.setFileSize(Long.valueOf(logoSvg.length()));
        logoFile.setFileId(logoFileId);
        logoFile.setDirectory(false);
        logoFile.setParentId(imagesFolder.getId());
        logoFile.setConnectedAppId(connectedApp.getId());
        logoFile = ConnectedAppHostingAPI.addConnectedAppFile(connectedApp.getId(), logoFile);

        ConnectedAppFileContext scriptsFolder = new ConnectedAppFileContext();
        scriptsFolder.setFileName("scripts");
        scriptsFolder.setDirectory(true);
        scriptsFolder.setParentId(appFolder.getId());
        scriptsFolder.setConnectedAppId(connectedApp.getId());
        scriptsFolder = ConnectedAppHostingAPI.addConnectedAppFile(connectedApp.getId(), scriptsFolder);

        String appJsCode = new String(Base64.decodeBase64(sampleAppJsBase64Encoded));
        long appJsFileId = FileStoreFactory.getInstance().getFileStore().addFile("app.js", appJsCode, "application/javascript");
        ConnectedAppFileContext appJs = new ConnectedAppFileContext();
        appJs.setFileName("app.js");
        appJs.setContentType("application/javascript");
        appJs.setFileSize(Long.valueOf(appJsCode.length()));
        appJs.setFileId(appJsFileId);
        appJs.setDirectory(false);
        appJs.setParentId(scriptsFolder.getId());
        appJs.setConnectedAppId(connectedApp.getId());
        appJs = ConnectedAppHostingAPI.addConnectedAppFile(connectedApp.getId(), appJs);

        String indexPageCode = new String(Base64.decodeBase64(sampleIndexPageBase64Encoded));
        long indexPageFileId = FileStoreFactory.getInstance().getFileStore().addFile("index.html", indexPageCode, "text/html");
        ConnectedAppFileContext indexPage = new ConnectedAppFileContext();
        indexPage.setFileName("index.html");
        indexPage.setContentType("text/html");
        indexPage.setFileSize(Long.valueOf(indexPageCode.length()));
        indexPage.setFileId(indexPageFileId);
        indexPage.setDirectory(false);
        indexPage.setParentId(appFolder.getId());
        indexPage.setConnectedAppId(connectedApp.getId());
        indexPage = ConnectedAppHostingAPI.addConnectedAppFile(connectedApp.getId(), indexPage);

        ConnectedAppHostingAPI.addConnectedAppDeployment(connectedApp.getId(), true, "Initial version");
    }
}
