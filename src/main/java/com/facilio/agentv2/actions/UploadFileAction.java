package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.modbustcp.ModbusTcpControllerContext;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import com.facilio.agentv2.point.Point;
import com.facilio.aws.util.FacilioProperties;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class UploadFileAction extends AgentActionV2 {

    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(UploadFileAction.class.getName());


    private String fileUploadContentType;
    private String fileUploadFileName;
    @NotNull
    private File fileUpload;

    public Long getId() {
        return id;
    }

    public Long getAgentId() {
        return id;
    }

    public void setAgentId(Long agentId) {
        this.id = agentId;
    }


    public Long getDeviceId() {
        return id;
    }

    public void setDeviceId(Long deviceId) {
        this.id = deviceId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    private Long id;

    public String getFileUploadContentType() {
        return fileUploadContentType;
    }

    public void setFileUploadContentType(String fileUploadContentType) {
        this.fileUploadContentType = fileUploadContentType;
    }

    public String getFileUploadFileName() {
        return fileUploadFileName;
    }

    public void setFileUploadFileName(String fileUploadFileName) {
        this.fileUploadFileName = fileUploadFileName;
    }

    public File getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(File fileUpload) {
        this.fileUpload = fileUpload;
    }

    public static void main(String[] args) throws IOException {
        File file = new File("/home/vijay/Downloads/Untitled spreadsheet.xlsx");
        System.out.println(" file name " + file.getName());
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
        System.out.println(workbook.getNumberOfSheets());
        XSSFSheet sheetAt = workbook.getSheetAt(0);
        XSSFRow row = sheetAt.getRow(1);
        System.out.println(row.getCell(0).getStringCellValue());
        System.out.println(row.getCell(1).getStringCellValue());
        System.out.println(row.getCell(2).getStringCellValue());
    }


    public String bulkAddModbusDevice() {
        try {
            File fileUpload = getFileUpload();
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileUpload));
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            System.out.println("work book loaded");
            LOGGER.info(" work book loaded " + workbook.getNumberOfSheets());
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                Row firstRow = sheet.getRow(0);
                Objects.requireNonNull(firstRow, "no rows - first row missing");

                int nameIndex = -1;
                int slaveIdIndex = -1;
                int ipIndex = -1;
                for (int i = 0; i < firstRow.getPhysicalNumberOfCells(); i++) {
                    Cell cell = firstRow.getCell(i);
                    Objects.requireNonNull(cell, "theres no cell in first row");
                    if (cell.getCellTypeEnum().equals(CellType.STRING)) {
                        if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.NAME)) {
                            nameIndex = i;
                        } else if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.IP)) {
                            ipIndex = i;
                        } else if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.SLAVE_ID)) {
                            slaveIdIndex = i;
                        }
                    } else {
                        throw new Exception("FirstRow must be heading row");
                    }
                }

                if (nameIndex < 0) {
                    throw new Exception(" name missing from sheet");
                }
                if (ipIndex < 0) {
                    throw new Exception(" ip missing from sheet");
                }
                if (slaveIdIndex < 0) {
                    throw new Exception(" slave Id missinf from sheet");
                }
                sheet.removeRow(firstRow);
                LOGGER.info("header index " + nameIndex + "-" + slaveIdIndex + "-" + ipIndex);
                Iterator<Row> rowIterator = sheet.rowIterator();
                List<Controller> controllersToAdd = new ArrayList<>();
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                    String name;
                    String ip;
                    Long slaveId;
                    Row row = rowIterator.next();
                    ip = row.getCell(ipIndex).getStringCellValue();
                    name = row.getCell(nameIndex).getStringCellValue();
                    Number slaveIdNumber = row.getCell(slaveIdIndex).getNumericCellValue();
                    if (ip != null && name != null) {
                        slaveId = (slaveIdNumber).longValue();
                        if (FacilioProperties.isDevelopment()) {
                            LOGGER.info("id " + id + " name " + name + " slaveId " + slaveId + " ip " + ip);

                            ModbusTcpControllerContext modbusTcpControllerContext = new ModbusTcpControllerContext();
                            modbusTcpControllerContext.setAgentId(id);
                            modbusTcpControllerContext.setName(name);
                            modbusTcpControllerContext.setIpAddress(ip);
                            modbusTcpControllerContext.setSlaveId(slaveId.intValue());
                            controllersToAdd.add(modbusTcpControllerContext);

/*
                            try {
                                AgentMessenger.sendConfigModbusIpControllerCommand(modbusTcpControllerContext);
                            } catch (Exception e) {
                                LOGGER.info("Exception while sending configure modbus controller command",e);
                            }
*/
                            //sendModbusControllerCommand(id,name,slaveId,ip);
                        } else {
                            System.out.println("name " + name + " - slaveId " + slaveId + " - ip " + ip);
                        }
                    }
                }
                AgentMessenger.sendConfigureModbusIpControllers(controllersToAdd);
            }
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while bulk adding modbus controllers", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
            e.printStackTrace();
        }
        return SUCCESS;
    }

    public String bulkAddModbusPoint() {
        try {
            GetControllerRequest request = new GetControllerRequest().forDevice(getDeviceId())
                    .ofType(FacilioControllerType.MODBUS_IP);
            Controller controller = request.getController();
            Objects.requireNonNull(controller, "controller not found");
            File fileUpload = getFileUpload();
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileUpload));
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            System.out.println("work book loaded");
            LOGGER.info(" work book loaded " + workbook.getNumberOfSheets());
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                Row firstRow = sheet.getRow(0);
                Objects.requireNonNull(firstRow, "no rows - first row missing");

                int nameIndex = -1;
                int dataTypeIndex = -1;
                int registerNumberIndex = -1;
                int functionCodeIndex = -1;
                for (int i = 0; i < firstRow.getPhysicalNumberOfCells(); i++) {
                    Cell cell = firstRow.getCell(i);
                    Objects.requireNonNull(cell, "theres no cell in first row");
                    if (cell.getCellTypeEnum().equals(CellType.STRING)) {
                        if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.NAME)) {
                            nameIndex = i;
                        } else if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.DATA_TYPE)) {
                            dataTypeIndex = i;
                        } else if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.REGISTER_NUMBER)) {
                            registerNumberIndex = i;
                        } else if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.FUNCTION_CODE)) {
                            functionCodeIndex = i;
                        }
                    } else {
                        throw new Exception("FirstRow must be heading row");
                    }
                }
                if (nameIndex < 0) {
                    throw new Exception(" name missing from sheet");
                }
                if (dataTypeIndex < 0) {
                    throw new Exception(" dataType missing from sheet");
                }
                if (registerNumberIndex < 0) {
                    throw new Exception(" register number missing from sheet");
                }
                if (functionCodeIndex < 0) {
                    throw new Exception(" function code missing from sheet");
                }

                sheet.removeRow(firstRow);
                LOGGER.info("header index " + nameIndex + " dataTypeIndex " + dataTypeIndex + " registerNumberIndex " + registerNumberIndex + " functionCodeIndex " + functionCodeIndex);
                Iterator<Row> rowIterator = sheet.rowIterator();
                List<Point> points = new ArrayList<>();
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                    String name;
                    Long registerNumber;
                    Long dataType;
                    Long functionCode;
                    Row row = rowIterator.next();
                    registerNumber = ((Number) row.getCell(registerNumberIndex).getNumericCellValue()).longValue();
                    dataType = ((Number) row.getCell(dataTypeIndex).getNumericCellValue()).longValue();
                    functionCode = ((Number) row.getCell(functionCodeIndex).getNumericCellValue()).longValue();
                    name = row.getCell(nameIndex).getStringCellValue();
                    ModbusTcpPointContext pointContext = new ModbusTcpPointContext(controller.getAgentId(), controller.getId());
                    pointContext.setFunctionCode(functionCode);
                    pointContext.setModbusDataType(dataType);
                    pointContext.setRegisterNumber(registerNumber);
                    pointContext.setName(name);
                    points.add(pointContext);
                }
                ControllerMessenger.sendConfigureModbusTcpPoints(controller, points);
            }
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while bulk adding points", e);
            internalError();
            setResult(AgentConstants.EXCEPTION, e.getMessage());
        }
        return SUCCESS;
    }
}
