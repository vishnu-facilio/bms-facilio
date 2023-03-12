package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.QRCodeGenerator;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bundle.command.PackBundleChangeSetCommand;
import com.facilio.bundle.context.BundleFileContext;
import com.facilio.bundle.context.BundleFolderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Priority;
import org.zeroturnaround.zip.ZipUtil;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class V3SiteQRDownloadAPI {

    private static final String PNG_EXTENSION ="png";
    private static final String SITE_FOLDER = "Site";
    private static final String BUILDING_FOLDER = "Buildings";
    private static final String FLOOR_FOLDER = "Floors";
    private static final String SPACE_FOLDER = "Spaces";
    private static final String SUB_SPACE_FOLDER = "Sub Spaces";

    private static final String FACILIO_QR = "facilio_";
    private static final String DELIMITER="_";

    public static String getSiteQRDownloadURL(Long siteId) throws Exception {

        BaseSpaceContext site = SpaceAPI.getBaseSpace(siteId);

        FacilioUtil.throwIllegalArgumentException(site == null,"Invalid Site ID");

        List<V3BaseSpaceContext> baseSpaces = V3SpaceAPI.getBaseSpaceChildren(FacilioConstants.ContextNames.SITE, siteId);

        BundleFolderContext siteFolder = new BundleFolderContext(site.getName()+DELIMITER+siteId);

        String qrVal = site.getQrVal().trim();

        if (CollectionUtils.isEmpty(baseSpaces)) {

            BundleFileContext siteFile = new BundleFileContext(site.getName()+DELIMITER+siteId, PNG_EXTENSION, StringUtils.isEmpty(qrVal) ? FACILIO_QR+siteId : qrVal);
            siteFolder.addFile(site.getName()+DELIMITER+siteId, siteFile);

            return saveFile(siteFolder);
        }

        if (CollectionUtils.isNotEmpty(baseSpaces)) {

            Map<Long, V3BaseSpaceContext> idVsBaseSpace = baseSpaces.stream().collect(Collectors.toMap(V3BaseSpaceContext::getId, Function.identity()));

            Map<Long, List<V3BaseSpaceContext>> siteIdVsBuildingMap = baseSpaces.stream().filter(v -> v.getSpaceType() == V3BaseSpaceContext.SpaceType.BUILDING.getIntVal()).collect(Collectors.groupingBy(V3BaseSpaceContext::getSiteId));

            Map<Long, List<V3BaseSpaceContext>> buildingIdVsFloorMap = baseSpaces.stream().filter(v -> v.getSpaceType() == V3BaseSpaceContext.SpaceType.FLOOR.getIntVal()).collect(Collectors.groupingBy(V3BaseSpaceContext::getBuildingId));

            Map<Long, List<V3BaseSpaceContext>> buildingIdVsSpaceMap = baseSpaces.stream().filter(v -> v.getSpaceType() == V3BaseSpaceContext.SpaceType.SPACE.getIntVal() && v.getBuildingId() != null && v.getFloorId() == null).collect(Collectors.groupingBy(V3BaseSpaceContext::getBuildingId));

            Map<Long, List<V3BaseSpaceContext>> floorIdVsSpaceMap = baseSpaces.stream().filter(v -> v.getSpaceType() == V3BaseSpaceContext.SpaceType.SPACE.getIntVal() && v.getFloorId() != null).collect(Collectors.groupingBy(V3BaseSpaceContext::getFloorId));

            Map<Long, List<V3BaseSpaceContext>> siteIdIdVsSpaceMap = baseSpaces.stream().filter(v -> v.getSpaceType() == V3BaseSpaceContext.SpaceType.SPACE.getIntVal() && v.getBuilding() == null && v.getFloor() == null).collect(Collectors.groupingBy(V3BaseSpaceContext::getSiteId));


            BundleFileContext siteFile = new BundleFileContext(site.getName()+DELIMITER+siteId, PNG_EXTENSION, StringUtils.isEmpty(qrVal) ? FACILIO_QR+siteId : qrVal);
            siteFolder.addFile(site.getName()+DELIMITER+siteId, siteFile);

            if (CollectionUtils.isNotEmpty(siteIdIdVsSpaceMap.get(site.getId()))) {
                fillSiteSpaces(siteFolder, siteIdIdVsSpaceMap.get(site.getId()), idVsBaseSpace);
            }

            List<V3BaseSpaceContext> buildings = siteIdVsBuildingMap.get(site.getSiteId());

            if (CollectionUtils.isNotEmpty(buildings)) {

                for (V3BaseSpaceContext building : buildings) {    // Building Folder

                    fillBuildingChildren(idVsBaseSpace, buildingIdVsSpaceMap, siteFolder, building);

                    List<V3BaseSpaceContext> floors = buildingIdVsFloorMap.get(building.getId());

                    if (CollectionUtils.isEmpty(floors)) {
                        continue;
                    }

                    for (V3BaseSpaceContext floor : floors) {     // Floor Folders

                        fillFloorChildren(siteFolder, building, floor);

                        List<V3BaseSpaceContext> spaces = floorIdVsSpaceMap.get(floor.getId());

                        if (CollectionUtils.isEmpty(spaces)) {
                            continue;
                        }

                        BundleFolderContext spaceFolder = siteFolder.getOrAddFolder(BUILDING_FOLDER).getFolder(building.getName()+DELIMITER+building.getId()).getOrAddFolder(FLOOR_FOLDER).getOrAddFolder(floor.getName()+DELIMITER+floor.getId()).getOrAddFolder(SPACE_FOLDER);

                        for (V3BaseSpaceContext space : spaces) {   // Space and SubSpace Folders

                            fillSpaceChildren(idVsBaseSpace, spaceFolder, space);

                        }

                    }

                }
            }

            return saveFile(siteFolder);
        }

        return null;
    }

    private static String saveFile(BundleFolderContext siteFolder) throws Exception {
        long fileId = saveAsZipFile(siteFolder);

        return fileId > 0 ? FacilioFactory.getFileStore().getDownloadUrl(fileId) : "v2 script is empty";
    }

    private static void fillSpaceChildren(Map<Long, V3BaseSpaceContext> idVsBaseSpace, BundleFolderContext spaceFolder, V3BaseSpaceContext space) throws Exception {
        if (space.getSpace1() == null) {
            BundleFileContext spaceFile = new BundleFileContext(space.getName()+DELIMITER+space.getId() , PNG_EXTENSION,  space.getQrVal() != null ? space.getQrVal() : FACILIO_QR+ space.getId());

            BundleFolderContext spaceChild = spaceFolder.getOrAddFolder(space.getName()+DELIMITER+space.getId());
            if (spaceChild.getFile(idVsBaseSpace.get(space.getId()).getName()+DELIMITER+space.getId()) == null) {
                spaceChild.addFile(idVsBaseSpace.get(space.getId()).getName()+DELIMITER+space.getId(), spaceFile);
            }
        }
        if (space.getSpace1() != null) {

            BundleFileContext subSpaceFile = new BundleFileContext(space.getName()+DELIMITER+space.getId(), PNG_EXTENSION,  space.getQrVal() != null ? space.getQrVal() : FACILIO_QR+ space.getId());
            spaceFolder.getOrAddFolder(idVsBaseSpace.get(space.getSpace1().getId()).getName()+DELIMITER+ space.getSpace1().getId()).getOrAddFolder(SUB_SPACE_FOLDER).addFile(space.getName()+DELIMITER+space.getId(), subSpaceFile);
        }
    }

    private static void fillFloorChildren(BundleFolderContext siteFolder, V3BaseSpaceContext building, V3BaseSpaceContext floor) throws Exception {

        BundleFileContext floorFile = new BundleFileContext(floor.getName()+DELIMITER+floor.getId(), PNG_EXTENSION, floor.getQrVal()!= null ? floor.getQrVal() : FACILIO_QR+floor.getId());

        siteFolder.getFolder(BUILDING_FOLDER).getFolder(building.getName()+DELIMITER+building.getId()).getOrAddFolder(FLOOR_FOLDER).getOrAddFolder(floor.getName()+DELIMITER+floor.getId()).addFile(floor.getName()+DELIMITER+floor.getId(), floorFile);

    }

    private static void fillBuildingChildren(Map<Long, V3BaseSpaceContext> idVsBaseSpace, Map<Long, List<V3BaseSpaceContext>> buildingIdVsSpaceMap, BundleFolderContext siteFolder, V3BaseSpaceContext building) throws Exception {


        BundleFileContext buildingFile = new BundleFileContext(building.getName()+DELIMITER+building.getId(), PNG_EXTENSION,  building.getQrVal() != null ? building.getQrVal() : FACILIO_QR+ building.getId());
        siteFolder.getOrAddFolder(BUILDING_FOLDER).addFolder(building.getName()+DELIMITER+building.getId()).addFile(building.getName()+DELIMITER+building.getId(), buildingFile);

        if (CollectionUtils.isNotEmpty(buildingIdVsSpaceMap.get(building.getId()))) {
            buildingSpace(siteFolder, building, buildingIdVsSpaceMap.get(building.getId()), idVsBaseSpace);
        }
    }

    private static void fillSiteSpaces(BundleFolderContext siteFolder, List<V3BaseSpaceContext> siteIdIdVsSpaces, Map<Long, V3BaseSpaceContext> idVsBaseSpace) throws Exception {

        BundleFolderContext spaceFolder = siteFolder.getOrAddFolder(SPACE_FOLDER);

        for (V3BaseSpaceContext space : siteIdIdVsSpaces) {

            if (space.getSpace1() == null) {
                BundleFileContext spaceFile = new BundleFileContext(space.getName()+DELIMITER+space.getId(), PNG_EXTENSION, space.getQrVal() != null ? space.getQrVal() : FACILIO_QR+ space.getId());
                spaceFolder.getOrAddFolder(space.getName()+DELIMITER+space.getId()).addFile(space.getName()+DELIMITER+space.getId(), spaceFile);
            } else if (space.getSpace1() != null) {
                BundleFileContext subSpaceFile = new BundleFileContext(space.getName()+DELIMITER+space.getId() , PNG_EXTENSION, space.getQrVal() != null ? space.getQrVal() : FACILIO_QR+ space.getId());
                spaceFolder.getOrAddFolder(idVsBaseSpace.get(space.getSpace1().getId()).getName()+DELIMITER+space.getId()).getOrAddFolder(SUB_SPACE_FOLDER).addFile(space.getName()+DELIMITER+space.getId(), subSpaceFile);
            }
        }
    }

    private static void buildingSpace(BundleFolderContext siteFolder, V3BaseSpaceContext building, List<V3BaseSpaceContext> v3BaseSpaceContexts, Map<Long, V3BaseSpaceContext> idVsBaseSpace) throws Exception {

        if (CollectionUtils.isEmpty(v3BaseSpaceContexts)) {
            return;
        }
        BundleFolderContext buildingFolder = siteFolder.getOrAddFolder(BUILDING_FOLDER).getOrAddFolder(building.getName()+DELIMITER+building.getId()).getOrAddFolder(SPACE_FOLDER);
        for (V3BaseSpaceContext space : v3BaseSpaceContexts) {

            if (space.getSpace1() == null) {
                BundleFileContext spaceFile = new BundleFileContext(space.getName()+DELIMITER+space.getId(), PNG_EXTENSION, space.getQrVal() != null ? space.getQrVal() : FACILIO_QR+ space.getId());

                buildingFolder.getOrAddFolder(space.getName()+DELIMITER+space.getId()).addFile(space.getName()+DELIMITER+space.getId(), spaceFile);
            } else if (space.getSpace1() != null) {
                BundleFileContext subSpaceFile = new BundleFileContext(space.getName()+DELIMITER+space.getId(), PNG_EXTENSION, space.getQrVal() != null ? space.getQrVal() : FACILIO_QR+ space.getId());
                buildingFolder.getOrAddFolder(idVsBaseSpace.get(space.getSpace1().getId()).getName()+DELIMITER+space.getId()).getOrAddFolder(SUB_SPACE_FOLDER).addFile(space.getName()+DELIMITER+space.getId(), subSpaceFile);
            }

        }
    }

    private static long saveAsZipFile(BundleFolderContext rootFolder) throws Exception {

        String rootPath = PackBundleChangeSetCommand.class.getClassLoader().getResource("").getFile() + File.separator + "facilio-temp-files" + File.separator + AccountUtil.getCurrentOrg().getOrgId() + File.separator + "bundles" + File.separator + rootFolder.getName();

        File rootFile = new File(rootPath);
        if (!(rootFile.exists() && rootFile.isDirectory())) {
            rootFile.mkdirs();
        }

        rootFolder.setPath(rootPath);

        Queue<BundleFolderContext> foldersQueue = new LinkedList<BundleFolderContext>();

        foldersQueue.add(rootFolder);

        while (!foldersQueue.isEmpty()) {
            BundleFolderContext folder = foldersQueue.poll();

            if (!folder.getFolders().isEmpty()) {

                for (String folderName : folder.getFolders().keySet()) {

                    String subFolderPath = folder.getPath() + File.separator + folderName;

                    File subFolder = new File(subFolderPath);
                    subFolder.mkdirs();

                    BundleFolderContext subFolderContext = folder.getFolders().get(folderName);
                    subFolderContext.setPath(subFolderPath);
                    foldersQueue.add(subFolderContext);
                }
            }

            if (!folder.getFiles().isEmpty()) {

                for (String fileName : folder.getFiles().keySet()) {

                    BundleFileContext fileContext = folder.getFiles().get(fileName);

                    String content = fileContext.isXMLFile() ? fileContext.getXmlContent().getAsXMLString() : fileContext.getFileContent();

                    try {
                        ImageIO.write(QRCodeGenerator.generateQRCodeImage(content), PNG_EXTENSION, new File(folder.getPath() + File.separator + fileContext.getName()+".png"));
                    } catch (IOException e) {
                        LOGGER.log(Priority.ERROR, e.getMessage(), e);
                        throw e;
                    }

                }
            }
        }

        File zipFile = new File(rootPath + ".zip");

        ZipUtil.pack(rootFile, zipFile);

        FileStore fs = FacilioFactory.getFileStore();

        long fileId = fs.addFile(rootFolder.getName() + ".zip", zipFile, "application/zip");

        FileUtils.deleteDirectory(rootFile);
        zipFile.delete();

        return fileId;
    }

}
