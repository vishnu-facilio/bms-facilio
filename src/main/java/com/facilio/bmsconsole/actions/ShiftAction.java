package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.opensymphony.xwork2.ActionSupport;

public class ShiftAction extends ActionSupport {
	
	public String add() throws Exception {
		long id = BusinessHoursAPI.addBusinessHours(shift.getDays());
		shift.setBusinessHoursId(id);
		Map<String, Object> props = FieldUtil.getAsProperties(shift);
		props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		GenericInsertRecordBuilder shiftBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getShiftModule().getTableName())
				.fields(FieldFactory.getShiftField())
				.addRecord(props);
		
		shiftBuilder.save();
		
		return SUCCESS;
	}
	
	public String update() throws Exception {
		long oldId = shift.getBusinessHoursId();
		
		long id = BusinessHoursAPI.addBusinessHours(shift.getDays());
		shift.setBusinessHoursId(id);
		
		FacilioModule module = ModuleFactory.getShiftModule();
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(FieldFactory.getShiftField())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(shift.getId(), module))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), module));
		
		Map<String, Object> prop = FieldUtil.getAsProperties(shift);
		builder.update(prop);
		
		BusinessHoursAPI.deleteBusinessHours(oldId);
				
		return SUCCESS;
	}
	
	public String all() throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getShiftField())
				.table(ModuleFactory.getShiftModule().getTableName())
				.andCustomWhere(ModuleFactory.getShiftModule().getTableName() + ".ORGID=?", AccountUtil.getCurrentOrg().getOrgId())
				.orderBy("name");
		List<Map<String, Object>> props = selectBuilder.get();
		StringJoiner j = new StringJoiner(",");
		if (props != null && !props.isEmpty()) {
			this.shifts = new ArrayList<>();
			ShiftContext s = FieldUtil.getAsBeanFromMap(props.get(0), ShiftContext.class);
			j.add(String.valueOf(s.getBusinessHoursId()));
			this.shifts.add(s);
		}
		
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSingleDayBusinessHoursFields())
				.table(businessHoursTable)
				.innerJoin(singleDayTable)
				.on(businessHoursTable+".ID = "+singleDayTable+".PARENT_ID")
				.andCustomWhere(businessHoursTable+".ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", j.toString(), NumberOperators.EQUALS))
				.orderBy("dayOfWeek");
		
		props = selectBuilder.get();
		
		List<BusinessHourContext> days = new ArrayList<>();
		Map<Long, List<BusinessHourContext>> parentIdVsContext = new HashMap<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop: props) {
				BusinessHourContext b = FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class);
				long id = b.getParentId();
				if (!parentIdVsContext.containsKey(id)) {
					parentIdVsContext.put(id, new ArrayList<>());
				}
				parentIdVsContext.get(id).add(b);
			}
		}
		
		this.shifts.forEach(s -> {
			List<BusinessHourContext> b = parentIdVsContext.get(s.getBusinessHoursId());
			s.setDays(b);
		});
		
		return SUCCESS;
	}
	
	private ShiftContext shift;
	public void setShift(ShiftContext shift) {
		this.shift = shift;
	}
	
	public ShiftContext getShift() {
		return this.shift;
	}
	
	private List<ShiftContext> shifts;
	public void setShifts(List<ShiftContext> shifts) {
		this.shifts = shifts;
	}
	
	public List<ShiftContext> getShifts() {
		return this.shifts;
	}
	
}
