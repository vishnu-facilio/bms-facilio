package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.device.Device;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class DeviceAPI 
{
	private static Logger logger = Logger.getLogger(DeviceAPI.class.getName());

	public static List<ControllerContext> getAllControllers() throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(ModuleFactory.getControllerModule().getTableName())
														.select(FieldFactory.getControllerFields())
														.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<ControllerContext> controllers = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				controllers.add(FieldUtil.getAsBeanFromMap(prop, ControllerContext.class));
			}
			return controllers;
		}
		return null;
	}
	
	public static List<EnergyMeterContext> getAllEnergyMeters() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
																		.select(modBean.getAllFields(module.getName()))
																		.module(module)
																		.beanClass(EnergyMeterContext.class)
																		.maxLevel(0);
		
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getEnergyMetersOfSpace(long spaceId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
																		.select(modBean.getAllFields(module.getName()))
																		.module(module)
																		.beanClass(EnergyMeterContext.class)
																		.andCustomWhere("PURPOSE_SPACE_ID = ?", spaceId)
																		.maxLevel(0)
																		;
		
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getEnergyMetersOfPurpose(long purposeId,boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
																		.select(modBean.getAllFields(module.getName()))
																		.module(module)
																		.beanClass(EnergyMeterContext.class)
																		.andCustomWhere("PURPOSE_ID = ? AND IS_ROOT=?", purposeId,root)
																		.maxLevel(0)
																		;
		
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getAllEnergyMeters(long spaceId,long purposeId, boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("PURPOSE_SPACE_ID = ? AND IS_ROOT= ?", spaceId,root)
				.andCondition(getCondition("PURPOSE_ID",""+purposeId,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getAllEnergyMeters(long spaceId, boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("PURPOSE_SPACE_ID = ? AND IS_ROOT= ?", spaceId,root)
				.maxLevel(0);
		return selectBuilder.get();
	}
			
	public static List<EnergyMeterPurposeContext> getEnergyMeterPurpose(String purpose) throws Exception {
		long orgId=OrgInfo.getCurrentOrgInfo().getOrgid();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
		
		SelectRecordsBuilder<EnergyMeterPurposeContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterPurposeContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterPurposeContext.class)
				.andCustomWhere("ORGID=? AND NAME = ?", orgId,purpose)
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterPurposeContext> getAllEnergyMeterPurpose() throws Exception {
		long orgId=OrgInfo.getCurrentOrgInfo().getOrgid();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
		
		SelectRecordsBuilder<EnergyMeterPurposeContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterPurposeContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterPurposeContext.class)
				.andCustomWhere("ORGID=?", orgId)
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Condition getCondition (String colName,String valueList,Operator operator)
	{
				Condition condition = new Condition();
				condition.setColumnName(colName);
				condition.setOperator(operator);
				condition.setValue(valueList);
				return condition;
	}
			
	 public static List<Device> getDevices() throws SQLException
	 {
		String sql="SELECT * FROM Device LEFT JOIN Assets ON Device.DEVICE_ID = Assets.ASSETID where ORGID = ?";
		
		try (Connection conn =FacilioConnectionPool.INSTANCE.getConnection();PreparedStatement pstmt=conn.prepareStatement(sql) )
		{
			pstmt.setObject(1, OrgInfo.getCurrentOrgInfo().getOrgid());
			
			try(ResultSet rs = pstmt.executeQuery())
			{
				List<Device> deviceList= new ArrayList<Device>();
				while(rs.next()) 
				{
					Device device = new Device();
					device.setDeviceId(rs.getLong("DEVICE_ID"));
					device.setName(rs.getString("NAME"));
					device.setParentId(rs.getLong("PARENT_DEVICE_ID"));
					device.setTypeVal(rs.getString("DEVICE_TYPE"));
					device.setSpaceId(rs.getLong("SPACE_ID"));
					deviceList.add(device);
				}
				return deviceList;
			}
			catch (SQLException e) 
			{
				logger.log(Level.SEVERE, "Exception while getting devices" +e.getMessage(), e);
				throw e;
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting devices" +e.getMessage(), e);
			throw e;
		}
	 }
	
}
