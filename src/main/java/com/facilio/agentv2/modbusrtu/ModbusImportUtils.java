package com.facilio.agentv2.modbusrtu;

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
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

public class ModbusImportUtils {
    public static final Integer CONTROLLER_IMPORT = 1;
    public static final Integer POINTS_IMPORT = 2;
    private static final org.apache.log4j.Logger LOGGER = Logger.getLogger(ModbusImportUtils.class.getName());

    public static long addControllersEntry(long agentId, long fileId) throws Exception {
        if (agentId < 0) {
            throw new Exception("AgentId can't be less than 1");
        }
        if (fileId < 0) {
            throw new Exception("FileId can't be less than 1");
        }
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put(AgentConstants.IDX, agentId);
        toInsert.put(AgentConstants.TYPE, CONTROLLER_IMPORT);
        toInsert.put(AgentConstants.STATUS, 0);
        toInsert.put(AgentConstants.FILE_ID, fileId);
        return insert(toInsert);
    }

    public static long addPointsImportEntry(long deviceId, long fileId) throws Exception {
        if (deviceId < 0) {
            throw new Exception("deviceId can't be less than 1");
        }
        if (fileId < 0) {
            throw new Exception("FileId can't be less than 1");
        }
        Map<String, Object> toInsert = new HashMap<>();
        toInsert.put(AgentConstants.IDX, deviceId);
        toInsert.put(AgentConstants.TYPE, POINTS_IMPORT);
        toInsert.put(AgentConstants.STATUS, 0);
        toInsert.put(AgentConstants.FILE_ID, fileId);
        return insert(toInsert);
    }

    public static long insert(Map<String, Object> toInsert) throws Exception {
        FacilioModule module = ModuleFactory.getModbusImportModule();
        long currTime = System.currentTimeMillis();
        toInsert.put(AgentConstants.CREATED_TIME, currTime);
        toInsert.put(AgentConstants.LAST_MODIFIED_TIME, currTime);
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getModbusImportFields());
        return insertRecordBuilder.insert(toInsert);
    }

    public static List<Map<String, Object>> getControllerImportData(long agentId) throws Exception {
        FacilioModule module = ModuleFactory.getModbusImportModule();
        if (agentId < 0) {
            throw new Exception("AgentId can't be less than 1");
        }
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdxField(module), String.valueOf(agentId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getTypeField(module), String.valueOf(CONTROLLER_IMPORT), NumberOperators.EQUALS));
        return getEntry(criteria);
    }

    public static List<Map<String, Object>> getPointImportData(long deviceId) throws Exception {
        FacilioModule module = ModuleFactory.getModbusImportModule();
        if (deviceId < 0) {
            throw new Exception("deviceId can't be less than 1");
        }
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdxField(module), String.valueOf(deviceId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getTypeField(module), String.valueOf(POINTS_IMPORT), NumberOperators.EQUALS));
        return getEntry(criteria);
    }

    public static Map<String, Object> getImport(long importId) throws Exception {
        if (importId > 0) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getIdCondition(importId, ModuleFactory.getModbusImportModule()));
            List<Map<String, Object>> entry = getEntry(criteria);
            if (!entry.isEmpty()) {
                return entry.get(0);
            } else {
                return new HashMap<>();
            }
        } else {
            throw new Exception("import id cant be less than 0");
        }
    }

    private static List<Map<String, Object>> getEntry(Criteria criteria) throws Exception {
        FacilioModule module = ModuleFactory.getModbusImportModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getModbusImportFields())
                .andCriteria(criteria);
        return selectRecordBuilder.get();
    }

    public static boolean notNull(Object object) {
        return object != null;
    }

    public static boolean containsValueCheck(String key, Map<String, Object> map) {
        if (notNull(key) && notNull(map) && map.containsKey(key) && (map.get(key) != null)) {
            return true;
        }
        return false;
    }

    public static void markImportComplete(long importId) throws SQLException {
        FacilioModule module = ModuleFactory.getModbusImportModule();
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.STATUS, 1);
        toUpdate.put(AgentConstants.LAST_MODIFIED_TIME, System.currentTimeMillis());
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getModbusImportFields())
                .andCondition(CriteriaAPI.getIdCondition(importId, module));
        updateRecordBuilder.update(toUpdate);
    }

    public static List<Map<String, Object>> getpendingControllerImports(long agentId) throws Exception {
        if (agentId > 0) {
            Criteria criteria = new Criteria();
            FacilioModule modbusImportModule = ModuleFactory.getModbusImportModule();
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdxField(modbusImportModule), String.valueOf(agentId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getImportStatusField(modbusImportModule), String.valueOf(0), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getTypeField(modbusImportModule), String.valueOf(CONTROLLER_IMPORT), NumberOperators.EQUALS));
            return getImports(criteria,null
            );
        } else {
            throw new Exception("agent can't be less than 1");
        }
    }

    public static long getPendingControllerImportCount(long agentId) throws Exception {
        if(agentId < 1){
            throw new Exception(" deviceId cant be less than 1");
        }
        FacilioModule modbusImportModule = ModuleFactory.getModbusImportModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdxField(modbusImportModule), String.valueOf(agentId),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getTypeField(modbusImportModule), String.valueOf(CONTROLLER_IMPORT), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getImportStatusField(modbusImportModule), String.valueOf(0), NumberOperators.EQUALS));
        return getCount(criteria);
    }
    public static long getPendingPointImportCount(long deviceId) throws Exception {
        if(deviceId < 1){
            throw new Exception(" deviceId cant be less than 1");
        }
        FacilioModule modbusImportModule = ModuleFactory.getModbusImportModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdxField(modbusImportModule), String.valueOf(deviceId),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getTypeField(modbusImportModule), String.valueOf(POINTS_IMPORT), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getImportStatusField(modbusImportModule), String.valueOf(0), NumberOperators.EQUALS));
        return getCount(criteria);
    }

    private static long getCount(Criteria criteria) throws Exception {
        FacilioModule modbusImportModule = ModuleFactory.getModbusImportModule();
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(modbusImportModule.getTableName())
                .select(new HashSet<>())
                .andCriteria(criteria)
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(modbusImportModule));
        List<Map<String, Object>> result = genericSelectRecordBuilder.get();
        return (long) result.get(0).get(AgentConstants.ID);
    }

    public static List<Map<String, Object>> getpendingPointsImports(long deviceId) throws Exception {
        if (deviceId > 0) {
            Criteria criteria = new Criteria();
            FacilioModule modbusImportModule = ModuleFactory.getModbusImportModule();
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdxField(modbusImportModule), String.valueOf(deviceId), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getImportStatusField(modbusImportModule), String.valueOf(0), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getTypeField(modbusImportModule), String.valueOf(POINTS_IMPORT), NumberOperators.EQUALS));
            return getImports(criteria,null );
        } else {
            throw new Exception("deviceId can't be less than 1");
        }
    }

    private static List<Map<String, Object>> getImports(Criteria criteria, FacilioContext context) throws Exception {
        FacilioModule module = ModuleFactory.getModbusImportModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getModbusImportFields())
                .andCriteria(criteria);
        if(context != null){
            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            if (pagination != null) {
                int page = (int) pagination.get("page");
                int perPage = (int) pagination.get("perPage");

                int offset = ((page - 1) * perPage);
                if (offset < 0) {
                    offset = 0;
                }

                selectRecordBuilder.offset(offset);
                selectRecordBuilder.limit(perPage);
            }
        }
        return selectRecordBuilder.get();
    }

    public static void processFileAndSendAddControllerCommand(long agentId, InputStream inputStream) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
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
                if (cell.getCellType().equals(CellType.STRING)) {
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
                        LOGGER.info("agentId " + agentId + " name " + name + " slaveId " + slaveId + " ip " + ip);

                        ModbusTcpControllerContext modbusTcpControllerContext = new ModbusTcpControllerContext();
                        modbusTcpControllerContext.setAgentId(agentId);
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
    }

    public static void processFileAndSendConfigureModbusPointsCommand(long deviceId, InputStream inputStream) throws Exception {
        GetControllerRequest request = new GetControllerRequest().forDevice(deviceId)
                .ofType(FacilioControllerType.MODBUS_IP);
        Controller controller = request.getController();
        Objects.requireNonNull(controller, "controller not found");
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        LOGGER.info(" work book loaded " + workbook.getNumberOfSheets());
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
            Row firstRow = sheet.getRow(0);
            Objects.requireNonNull(firstRow, "no rows - first row missing");

            int nameIndex = -1;
            int dataTypeIndex = -1;
            int registerNumberIndex = -1;
            int registerTypeIndex = -1;
            for (int i = 0; i < firstRow.getPhysicalNumberOfCells(); i++) {
                Cell cell = firstRow.getCell(i);
                Objects.requireNonNull(cell, "theres no cell in first row");
                if (cell.getCellType().equals(CellType.STRING)) {
                    if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.NAME)) {
                        nameIndex = i;
                    } else if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.DATA_TYPE)) {
                        dataTypeIndex = i;
                    } else if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.REGISTER_NUMBER)) {
                        registerNumberIndex = i;
                    } else if (cell.getStringCellValue().equalsIgnoreCase(AgentConstants.REGISTER_TYPE)) {
                        registerTypeIndex = i;
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
            if (registerTypeIndex < 0) {
                throw new Exception(" function code missing from sheet");
            }

            sheet.removeRow(firstRow);
            LOGGER.info("header index " + nameIndex + " dataTypeIndex " + dataTypeIndex + " registerNumberIndex " + registerNumberIndex + " functionCodeIndex " + registerTypeIndex);
            Iterator<Row> rowIterator = sheet.rowIterator();
            List<Point> points = new ArrayList<>();
            for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                String name;
                Long registerNumber;
                Long dataType;
                Long registerType;
                Row row = rowIterator.next();
                registerNumber = ((Number) row.getCell(registerNumberIndex).getNumericCellValue()).longValue();
                dataType = ((Number) row.getCell(dataTypeIndex).getNumericCellValue()).longValue();
                registerType = ((Number) row.getCell(registerTypeIndex).getNumericCellValue()).longValue();
                name = row.getCell(nameIndex).getStringCellValue();
                ModbusTcpPointContext pointContext = new ModbusTcpPointContext(controller.getAgentId(), controller.getId());
                pointContext.setRegisterType(registerType);
                pointContext.setModbusDataType(dataType);
                pointContext.setRegisterNumber(registerNumber);
                pointContext.setName(name);
                points.add(pointContext);
            }
            ControllerMessenger.sendConfigureModbusTcpPoints(controller, points);
        }
    }

    public static boolean ignoreImport(Long id) throws SQLException {
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.STATUS, 2);
        FacilioModule module = ModuleFactory.getModbusImportModule();
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getModbusImportFields())
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        return updateRecordBuilder.update(toUpdate) > 0;
    }
}
