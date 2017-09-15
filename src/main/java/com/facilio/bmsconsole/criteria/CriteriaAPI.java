package com.facilio.bmsconsole.criteria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;

public class CriteriaAPI {
	
	public static Criteria getCriteria(long orgId, long criteriaId, Connection conn) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT Criteria.CRITERIAID, Criteria.ORGID, Criteria.PATTERN, Conditions.CONDITIONID, Conditions.PARENT_CRITERIA_ID, Conditions.SEQUENCE, Conditions.FIELDID, Conditions.OPERATOR, Conditions.VAL, Conditions.CRITERIA_VAL_ID, Conditions.COMPUTED_WHERE_CLAUSE FROM Conditions INNER JOIN Criteria ON Conditions.PARENT_CRITERIA_ID = Criteria.CRITERIAID WHERE Criteria.ORGID = ? AND Criteria.CRITERIAID = ?");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, criteriaId);
			
			rs = pstmt.executeQuery();
			boolean first = true;
			Criteria criteria = null;
			Map<Integer, Condition> conditions = new HashMap<>();
			
			while(rs.next()) {
				if(first) {
					criteria = getCriteriaFromRS(rs);
					first = false;
				}
				Condition condition = getConditionFromRS(rs, orgId, conn);
				conditions.put(condition.getSequence(), condition);
			}
			criteria.setConditions(conditions);
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
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField(condition.getFieldId());
		condition.setField(field);
		
		condition.setOperator(field.getDataType().getOperator(rs.getString("OPERATOR")));
		
		if(condition.getCriteriaValueId() != 0) {
			condition.setCriteriaValue(getCriteria(orgId, condition.getCriteriaValueId(), conn));
		}
		
		return condition;
	}
}
