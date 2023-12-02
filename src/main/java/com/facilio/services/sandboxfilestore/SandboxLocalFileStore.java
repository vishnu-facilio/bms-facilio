package com.facilio.services.sandboxfilestore;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.filestore.LocalFileStore;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Log4j
public class SandboxLocalFileStore extends SandboxFileStore {
    public SandboxLocalFileStore(long orgId, long userId) {
        super(orgId, userId);
    }

    private static final String SANDBOX_FILES_DIR = "sandbox-data";

    private static final Map<String, String> ROOT_PATHS = new ConcurrentHashMap<>();

    @Override
    public String getRootPath(String namespace) {
        NamespaceConfig namespaceConfig = getNamespace(namespace);
        Objects.requireNonNull(namespaceConfig, "Invalid namespace in getting root path");
        String key = namespaceConfig.getName();
        String rootPath = ROOT_PATHS.get(key);
        if (rootPath == null) {
            String localFileStorePath = FacilioProperties.getLocalFileStorePath();
            if (StringUtils.isEmpty(localFileStorePath)) {
                ClassLoader classLoader = LocalFileStore.class.getClassLoader();
                URL fcDataFolder = classLoader.getResource("");
                localFileStorePath = fcDataFolder.getFile();
            }
            StringBuilder path = new StringBuilder()
                    .append(localFileStorePath)
                    .append(File.separator)
                    .append(SANDBOX_FILES_DIR)
                    .append(File.separator)
                    .append(getOrgId())
                    .append(File.separator)
                    .append(namespace);
            rootPath = path.toString();
            File rootDir = new File(rootPath);
            if (!(rootDir.exists() && rootDir.isDirectory())) {
                rootDir.mkdirs();
            }
            ROOT_PATHS.put(key, rootPath);
        }
        return rootPath;
    }

    @Override
    public String addFileAndGetURL(File file, String fileName) throws Exception {
        String rootFilePath = getRootPath(DEFAULT_NAMESPACE) + File.separator + fileName;
        File rootFile = new File(rootFilePath);
        if (rootFile.getParentFile() != null) {
            rootFile.getParentFile().mkdirs();
        }
        if (!file.isDirectory()) {
            addNewFile(rootFilePath, file);
        }
        return rootFilePath;
    }

    @Override
    public InputStream readFile(String fileURL) throws Exception {
        if (StringUtils.isNotEmpty(fileURL)) {
            Path path = Paths.get(fileURL);
            if (Files.exists(path)) {
                return Files.newInputStream(path);
            }
        }
        return null;
    }

    @Override
    public String addDirectoryAndGetURL(File file) throws Exception {
        String rootFilePath = getRootPath(DEFAULT_NAMESPACE) + File.separator + file.getName();

        File rootFile = new File(rootFilePath);
        rootFile.mkdirs();

        addFiles(rootFile, file, file);

        return rootFilePath;
    }

    private static void addFiles(File newRoot, File oldRoot, File current) throws Exception {
        File[] files = current.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    String relativePathStr = oldRoot.toPath().relativize(file.toPath()).toString();
                    File createDir = new File(constructFilePath(newRoot.getPath(), relativePathStr));
                    createDir.mkdirs();

                    addFiles(newRoot, oldRoot, file);
                } else {
                    String relativePathStr = oldRoot.toPath().relativize(file.toPath()).toString();
                    addNewFile(constructFilePath(newRoot.getPath(), relativePathStr), file);
                }
            }
        }
    }

    private static String constructFilePath(String rootPath, String filePath) {
        return rootPath + File.separator + filePath;
    }

    private static void addNewFile(String filePath, File file) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            File createFile = new File(filePath);
            createFile.createNewFile();

            is = Files.newInputStream(file.toPath());
            os = Files.newOutputStream(createFile.toPath());

            byte[] buffer = new byte[8192];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (IOException ioe) {
            LOGGER.info("Error while adding file " + filePath);
            throw ioe;
        } finally {
            is.close();
            os.close();
        }
    }
}
