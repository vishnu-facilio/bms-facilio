package com.facilio.bmsconsoleV3.signup.attendance;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.context.attendance.AttendanceSettings;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.StringSystemEnumField;
import com.facilio.v3.context.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddAttendanceModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        createMod(composeAttendanceMod());
        createMod(composeAttendanceTransactionMod());
        addAttendanceSettings();
        createAttendanceTxnChildMod(composeAttendanceTxnRichTextModule());
        persistRichTextNotesFieldForAttendanceTxn();
    }

    private void addAttendanceSettings() throws Exception {

        final Long HOUR_IN_MILLIS = 60 * 60 * 1000L;

        AttendanceSettings defaultSettings =
                new AttendanceSettings(AttendanceSettings.WorkingHoursMode.CUSTOM, 8 * HOUR_IN_MILLIS);

        FacilioModule mod = ModuleFactory.getAttendanceSettingsPseudoModule();
        List<FacilioField> fields = FieldFactory.getAttendanceSettingsPseudoModuleFields();

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(mod.getTableName())
                .fields(fields);
        insertBuilder.addRecord(FieldUtil.getAsProperties(defaultSettings));
        insertBuilder.save();

    }

    private void persistRichTextNotesFieldForAttendanceTxn() throws Exception {
        FacilioModule attendanceTxnRichTextMod = Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION_RICH_TEXT);
        FacilioModule attendanceTxnMod = Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION);

        LargeTextField richTextField = FieldFactory.getDefaultField("notes", "Notes", null, FieldType.LARGE_TEXT);
        richTextField.setModule(attendanceTxnMod);
        richTextField.setRelModuleId(attendanceTxnRichTextMod.getModuleId());
        Constants.getModBean().addField(richTextField);
    }

    private FacilioModule composeAttendanceTxnRichTextModule() throws Exception {
        FacilioModule attendanceTxnMod = Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION);
        FacilioModule mod = new FacilioModule(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION_RICH_TEXT,
                "Attendance Transaction Rich Text",
                "Attendance_Transaction_Rich_Text",
                FacilioModule.ModuleType.SUB_ENTITY
        );
        List<FacilioField> fields = new ArrayList<>();
        LookupField parentField = FieldFactory.getDefaultField("parentId", "Parent ID", "PARENT_ID", FieldType.LOOKUP);
        parentField.setLookupModule(attendanceTxnMod);
        fields.add(parentField);
        fields.add(FieldFactory.getDefaultField("fileId", "File ID", "FILE_ID", FieldType.NUMBER));
        mod.setFields(fields);
        return mod;
    }

    private void createAttendanceTxnChildMod(FacilioModule mod) throws Exception {
        final int CASCADE_DELETE = 2;
        FacilioModule attendanceTxnMod = Constants.getModBean().getModule(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION);
        List<FacilioModule> childModules = new ArrayList<>();
        childModules.add(mod);
        FacilioChain chain = TransactionChainFactory.addSystemModuleChain();
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, childModules);
        chain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, attendanceTxnMod.getName());
        chain.getContext().put(FacilioConstants.ContextNames.DELETE_TYPE, CASCADE_DELETE);
        chain.execute();

    }

    private FacilioModule composeAttendanceTransactionMod() throws Exception {

        FacilioModule attendanceTransactionMod = new FacilioModule(FacilioConstants.Attendance.ATTENDANCE_TRANSACTION,
                "Attendance Transaction",
                "Attendance_Transactions",
                FacilioModule.ModuleType.BASE_ENTITY
        );
        List<FacilioField> fields = new ArrayList<>();

        FacilioField transactionTimeField = FieldFactory.getDefaultField("transactionTime", "Transaction Time", "TRANSACTION_TIME", FieldType.DATE_TIME);
        transactionTimeField.setMainField(true);
        fields.add(transactionTimeField);

        FacilioModule peopleMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE);
        LookupField peopleField = FieldFactory.getDefaultField("people", "People", "PEOPLE_ID", FieldType.LOOKUP);
        peopleField.setLookupModule(peopleMod);
        fields.add(peopleField);

        StringSystemEnumField transactionType = FieldFactory.getDefaultField("transactionType", "Transaction Type", "TRANSACTION_TYPE", FieldType.STRING_SYSTEM_ENUM);
        transactionType.setEnumName("AttendanceTransactionType");
        fields.add(transactionType);

        StringSystemEnumField sourceType = FieldFactory.getDefaultField("sourceType", "Source Type", "SOURCE_TYPE", FieldType.STRING_SYSTEM_ENUM);
        sourceType.setEnumName("AttendanceTransactionSource");
        fields.add(sourceType);

        FacilioModule breakMod = Constants.getModBean().getModule(FacilioConstants.Break.BREAK);
        LookupField breakField = FieldFactory.getDefaultField("shiftBreak", "Break", "BREAK_ID", FieldType.LOOKUP);
        breakField.setLookupModule(breakMod);
        fields.add(breakField);

        attendanceTransactionMod.setFields(fields);

        return attendanceTransactionMod;
    }

    private FacilioModule composeAttendanceMod() throws Exception {

        FacilioModule attendanceMod = new FacilioModule(FacilioConstants.Attendance.ATTENDANCE,
                "Attendance",
                "Attendance",
                FacilioModule.ModuleType.BASE_ENTITY
        );

        attendanceMod.setDescription("To keep a tab on the available workforce.");

        List<FacilioField> fields = new ArrayList<>();

        FacilioField dayField = FieldFactory.getDefaultField("day", "Day", "DAY", FieldType.DATE_TIME);
        dayField.setMainField(true);
        fields.add(dayField);

        FacilioModule peopleMod = Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE);
        LookupField peopleField = FieldFactory.getDefaultField("people", "People", "PEOPLE", FieldType.LOOKUP);
        peopleField.setLookupModule(peopleMod);
        fields.add(peopleField);

        fields.add(FieldFactory.getDefaultField("checkInTime", "Check-in Time", "CHECK_IN_TIME", FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("checkOutTime", "Check-out Time", "CHECKOUT_OUT_TIME", FieldType.DATE_TIME));
        fields.add(FieldFactory.getDefaultField("workingHours", "Working Hours", "WORKING_HOURS", FieldType.NUMBER));

        StringSystemEnumField status = FieldFactory.getDefaultField("status", "Status", "STATUS", FieldType.STRING_SYSTEM_ENUM);
        status.setEnumName("AttendanceStatus");
        fields.add(status);

        fields.add(FieldFactory.getDefaultField("totalPaidBreakHrs", "Total Paid Break Hours", "TOTAL_PAID_BREAK_HRS", FieldType.NUMBER));
        fields.add(FieldFactory.getDefaultField("totalUnpaidBreakHrs", "Total Unpaid Break Hours", "TOTAL_UNPAID_BREAK_HRS", FieldType.NUMBER));


        FacilioModule shiftMod = Constants.getModBean().getModule(FacilioConstants.Shift.SHIFT);
        LookupField shiftField = FieldFactory.getDefaultField("shift", "Shift", "SHIFT_ID", FieldType.LOOKUP);
        shiftField.setLookupModule(shiftMod);
        fields.add(shiftField);

        attendanceMod.setFields(fields);

        return attendanceMod;
    }

    private void createMod(FacilioModule mod) throws Exception {
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(mod));
        addModuleChain.execute();
    }
}
