package com.facilio.bmsconsole.criteria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class CriteriaAPI {
	
	public static long addCriteria(Criteria criteria, long orgId) throws Exception {
		if(criteria != null) {
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				criteria.setCriteriaId(-1);
				criteria.setOrgId(orgId);
				Map<String, Object> criteriaProp = FieldUtil.getAsProperties(criteria);
				
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
																.connection(conn)
																.table("Criteria")
																.fields(FieldFactory.getCriteriaFields())
																.addRecord(criteriaProp);
				
				insertBuilder.save();
				long criteriaId = (long) criteriaProp.get("id");
				addConditions(criteria.getConditions(), criteriaId, orgId);
				
				return criteriaId;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw e;
			}
		}
		return -1;
	}
	
	private static void addConditions(Map<Integer, Condition> conditions, long parentCriteriaId, long orgId) throws Exception {
		if(conditions != null && !conditions.isEmpty()) {
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
																.connection(conn)
																.table("Conditions")
																.fields(FieldFactory.getConditionFields())
																;
				
				for(Condition condition : conditions.values()) {
					condition.setConditionId(-1);
					condition.setParentCriteriaId(parentCriteriaId);
					
					FacilioField field = condition.getField();
					if(field.getFieldId() == -1) {
						String moduleName = field.getModuleName();
						if(moduleName == null || moduleName.isEmpty()) {
							throw new IllegalArgumentException("Module Name is empty for field : "+field);
						}
						field = modBean.getField(field.getName(), moduleName);
						condition.setField(field);
					}
					
					if(condition.getCriteriaValue() != null) {
						if (condition.getCriteriaValue().getCriteriaId() == -1) {
							long criteriaValueId = addCriteria(condition.getCriteriaValue(), orgId);
							condition.setCriteriaValueId(criteriaValueId);
						}
						else {
							condition.setCriteriaValueId(condition.getCriteriaValue().getCriteriaId());
						}
					}
					
					Map<String, Object> conditionProp = FieldUtil.getAsProperties(condition);
					insertBuilder.addRecord(conditionProp);
				}
				insertBuilder.save();
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		else {
			throw new IllegalArgumentException("Condition can not be empty for a Criteria");
		}
	}
	
	public static Criteria getCriteria(long orgId, long criteriaId, Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT Criteria.CRITERIAID, Criteria.ORGID, Criteria.PATTERN, Conditions.CONDITIONID, Conditions.PARENT_CRITERIA_ID, Conditions.SEQUENCE, Conditions.FIELDID, Conditions.OPERATOR, Conditions.VAL, Conditions.CRITERIA_VAL_ID, Conditions.COMPUTED_WHERE_CLAUSE FROM Conditions INNER JOIN Criteria ON Conditions.PARENT_CRITERIA_ID = Criteria.CRITERIAID WHERE Criteria.ORGID = ? AND Criteria.CRITERIAID = ?");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, criteriaId);
			
			rs = pstmt.executeQuery();
			boolean isCriteriaNull = true;
			Criteria criteria = null;
			Map<Integer, Condition> conditions = new HashMap<>();
			
			while(rs.next()) {
				if(isCriteriaNull) {
					criteria = getCriteriaFromRS(rs);
					isCriteriaNull = false;
				}
				Condition condition = getConditionFromRS(rs, orgId, conn);
				conditions.put(condition.getSequence(), condition);
			}
			if(!isCriteriaNull) {
				criteria.setConditions(conditions);
			}
			return criteria;
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
	
	private static Criteria getCriteriaFromRS(ResultSet rs) throws SQLException {
		Criteria criteria = new Criteria();
		criteria.setCriteriaId(rs.getLong("Criteria.CRITERIAID"));
		criteria.setOrgId(rs.getLong("Criteria.ORGID"));
		criteria.setPattern(rs.getString("Criteria.PATTERN"));
		return criteria;
	}
	
	private static Condition getConditionFromRS(ResultSet rs, long orgId, Connection conn) throws Exception {
		Condition condition = new Condition();
		condition.setConditionId(rs.getLong("Conditions.CONDITIONID"));
		condition.setParentCriteriaId(rs.getLong("Conditions.PARENT_CRITERIA_ID"));
		condition.setSequence(rs.getInt("Conditions.SEQUENCE"));
		condition.setFieldId(rs.getLong("Conditions.FIELDID"));
		condition.setValue(rs.getString("Conditions.VAL"));
		condition.setCriteriaValueId(rs.getLong("Conditions.CRITERIA_VAL_ID"));
		condition.setComputedWhereClause(rs.getString("Conditions.COMPUTED_WHERE_CLAUSE"));
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
		FacilioField field = modBean.getField(condition.getFieldId());
		condition.setField(field);
		
		condition.setOperator(field.getDataType().getOperator(rs.getString("OPERATOR")));
		
		if(condition.getCriteriaValueId() != 0) {
			condition.setCriteriaValue(getCriteria(orgId, condition.getCriteriaValueId(), conn));
		}
		
		return condition;
	}
}
