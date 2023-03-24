package com.facilio.bmsconsoleV3.commands.pmImport;

//
//import com.facilio.bmsconsole.context.PMJobPlan;
//import com.facilio.bmsconsoleV3.context.V3TaskContext;
//import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTaskSectionContext;
//import com.facilio.bmsconsoleV3.context.jobplan.JobPlanTasksContext;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.ParseException;
//import org.junit.jupiter.api.Test;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class HandleTasksImportCommandTest {
//    final String DEFAULT_VALUE = "defaultValue";
//    final String REMARKS = "remarksRequired";
//    final String REMARKS_OPTIONS = "remarkOptionValues";
//
//    @Test
//    void taskWithNoInput() throws ParseException {
//        Map<String, Object> rec = new HashMap<>();
//        rec.put("jpTaskSubject", "subject");
//        rec.put("jpTaskDescription", "desc");
//
//        PMJobPlan jp = new PMJobPlan();
//        JobPlanTaskSectionContext section = new JobPlanTaskSectionContext();
//
//        JobPlanTasksContext task = Util.createJobPlanTask(jp, section, rec, 1);
//        assertEquals(task.getSubject(), "subject");
//        assertEquals(task.getDescription(), "desc");
//        assertEquals(task.getInputTypeEnum(), V3TaskContext.InputType.NONE);
//
//        JSONObject info = task.getAdditionInfo();
//        assertEquals(info.get(DEFAULT_VALUE), "");
//        assertEquals(info.get(REMARKS), false);
//        assertArrayEquals((String[]) info.get(REMARKS_OPTIONS), new String[]{});
//    }
//
//    @Test
//    void taskWithDefaultValue() throws ParseException {
//        Map<String, Object> rec = new HashMap<>();
//        rec.put("jpTaskSubject", "subject");
//        rec.put("jpTaskDescription", "desc");
//        rec.put("jpDefaultValue", "default value");
//
//        PMJobPlan jp = new PMJobPlan();
//        JobPlanTaskSectionContext section = new JobPlanTaskSectionContext();
//
//        JobPlanTasksContext task = Util.createJobPlanTask(jp, section, rec, 1);
//        assertEquals(task.getSubject(), "subject");
//        assertEquals(task.getDescription(), "desc");
//
//        JSONObject info = task.getAdditionInfo();
//        assertEquals(info.get(DEFAULT_VALUE), "default value");
//    }
//
//    @Test
//    void taskWithTextInput() throws ParseException {
//        Map<String, Object> rec = new HashMap<>();
//        rec.put("jpTaskSubject", "subject");
//        rec.put("jpTaskDescription", "desc");
//        rec.put("jpInputType", "TEXT");
//
//        PMJobPlan jp = new PMJobPlan();
//        JobPlanTaskSectionContext section = new JobPlanTaskSectionContext();
//
//        JobPlanTasksContext task = Util.createJobPlanTask(jp, section, rec, 1);
//        assertEquals(task.getSubject(), "subject");
//        assertEquals(task.getDescription(), "desc");
//        assertEquals(task.getInputTypeEnum(), V3TaskContext.InputType.TEXT);
//
//        JSONObject info = task.getAdditionInfo();
//        assertEquals(info.get(DEFAULT_VALUE), "");
//        assertEquals(info.get(REMARKS), false);
//        assertArrayEquals((String[]) info.get(REMARKS_OPTIONS), new String[]{});
//    }
//
//    @Test
//    void taskWithRadio() throws ParseException {
//        Map<String, Object> rec = new HashMap<>();
//        rec.put("jpTaskSubject", "subject");
//        rec.put("jpTaskDescription", "desc");
//        rec.put("jpInputType", "RADIO");
//
//        PMJobPlan jp = new PMJobPlan();
//        JobPlanTaskSectionContext section = new JobPlanTaskSectionContext();
//
//        JobPlanTasksContext task = Util.createJobPlanTask(jp, section, rec, 1);
//        assertEquals(task.getSubject(), "subject");
//        assertEquals(task.getDescription(), "desc");
//        assertEquals(V3TaskContext.InputType.RADIO, task.getInputTypeEnum());
//
//        JSONObject info = task.getAdditionInfo();
//        assertEquals(info.get(REMARKS), false);
//        assertArrayEquals((String[]) info.get(REMARKS_OPTIONS), new String[]{});
//    }
//
//    @Test
//    void taskWithNumber() throws ParseException {
//        Map<String, Object> rec = new HashMap<>();
//        rec.put("jpTaskSubject", "subject");
//        rec.put("jpTaskDescription", "desc");
//        rec.put("jpInputType", "RADIO");
//
//        PMJobPlan jp = new PMJobPlan();
//        JobPlanTaskSectionContext section = new JobPlanTaskSectionContext();
//
//        JobPlanTasksContext task = Util.createJobPlanTask(jp, section, rec, 1);
//        assertEquals(task.getSubject(), "subject");
//        assertEquals(task.getDescription(), "desc");
//        assertEquals(V3TaskContext.InputType.RADIO, task.getInputTypeEnum());
//
//        JSONObject info = task.getAdditionInfo();
//        assertEquals(info.get(REMARKS), false);
//        assertArrayEquals((String[]) info.get(REMARKS_OPTIONS), new String[]{});
//    }
//
//}