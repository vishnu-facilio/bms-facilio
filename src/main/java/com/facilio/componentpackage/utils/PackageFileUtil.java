package com.facilio.componentpackage.utils;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.componentpackage.bean.OrgSwitchBean;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.google.common.io.Files;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Priority;
import org.zeroturnaround.zip.ZipUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

@Log4j
public class PackageFileUtil {

    public static long saveAsZipFile(PackageFolderContext rootFolder) throws Exception {

        String rootPath = System.getProperties().getProperty("java.io.tmpdir") + File.separator + "sandbox"+ File.separator + "Unzipped-Package-Files" + File.separator + rootFolder.getName();

        File rootFile = new File(rootPath);
        if (!(rootFile.exists() && rootFile.isDirectory())) {
            rootFile.mkdirs();
        }

        rootFolder.setPath(rootPath);

        Queue<PackageFolderContext> foldersQueue = new LinkedList<PackageFolderContext>();
        foldersQueue.add(rootFolder);

        while(!foldersQueue.isEmpty()) {
            PackageFolderContext folder = foldersQueue.poll();
            if(!folder.getFolders().isEmpty()) {
                for(String folderName : folder.getFolders().keySet()) {
                    String subFolderPath = folder.getPath() + File.separator + folderName;

                    File subFolder = new File(subFolderPath);
                    subFolder.mkdirs();

                    PackageFolderContext subFolderContext = folder.getFolders().get(folderName);
                    subFolderContext.setPath(subFolderPath);
                    foldersQueue.add(subFolderContext);
                }
            }

            if(!folder.getFiles().isEmpty()) {
                for(String fileName : folder.getFiles().keySet()) {
                    PackageFileContext fileContext = folder.getFiles().get(fileName);
                    String content = fileContext.getXmlContent().getAsXMLString();
                    try(FileWriter fWriter = new FileWriter(folder.getPath() + File.separator + fileContext.getName() + "." + fileContext.getExtension())) {
                        fWriter.write(content);
                    }
                    catch (IOException e) {
                        LOGGER.log(Priority.ERROR, e.getMessage(), e);
                        throw e;
                    }
                }
            }
        }

        File zipFile = new File(rootPath+".zip");
        ZipUtil.pack(rootFile, zipFile);

        FileStore fs = FacilioFactory.getFileStore();
        long fileId = fs.addFile(rootFolder.getName()+".zip", zipFile, "application/zip");

        FileUtils.deleteDirectory(rootFile);
        zipFile.delete();

        return fileId;
    }
    public static File getPackageZipFile(Long fileId, Long sourceOrgId) throws Exception {
        OrgSwitchBean orgSwitchBean = (OrgSwitchBean) BeanFactory.lookup("OrgSwitchBean", sourceOrgId);
        try (InputStream inputStream = NewTransactionService.newTransactionWithReturn(() -> orgSwitchBean.getParentOrgFile(sourceOrgId, fileId))){
            String dirPath = System.getProperties().getProperty("java.io.tmpdir") + File.separator + "sandbox"+ File.separator + "Unzipped-Package-Files";
            String path = dirPath + File.separator +fileId+".zip";
            File file = new File(path);
            File dir = new File(dirPath);
            boolean directoryExits = (dir.exists() && dir.isDirectory());
            if (!directoryExits) {
                dir.mkdirs();
            }
            file.createNewFile();
            try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
                int read;
                byte[] bytes = new byte[8192];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
            return file;
        }
    }
    public static void accountSwitch(long orgId) throws Exception {
        Organization org = IAMOrgUtil.getOrg(orgId);
        User superAdminUser = AccountUtil.getOrgBean().getSuperAdmin(orgId);
        Account account = new Account(org, superAdminUser);
        AccountUtil.setCurrentAccount(account);
    }

    public static String readFileContent(String filePath) throws IOException {
        String text = Files.asCharSource(new File(filePath), Charset.defaultCharset()).read();
        return text;
    }

}
