package com.facilio.bmsconsole.criteria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.sql.DBUtil;

public class CriteriaUtil {
	
	public static Criteria getCriteria(long criteriaId, Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT Criteria.CRITERIAID, Criteria.PATTERN, Conditions.CONDITIONID, Conditions.CRITERIAID, Conditions.SEQUENCE, Conditions.OPERATOR, Conditions.VAL, Conditions.COMPUTED_WHERE_CLAUSE, Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE FROM Conditions INNER JOIN Criteria ON Conditions.CRITERIAID = Criteria.CRITERIAID INNER JOIN Fields ON Conditions.FIELDID = Fields.FIELDID WHERE Criteria.CRITERIAID = ?");
			pstmt.setLong(1, criteriaId);
			
			rs = pstmt.executeQuery();
			boolean first = true;
			Criteria criteria = null;
			Map<Integer, Condition> conditions = new HashMap<>();
			
			while(rs.next()) {
				if(first) {
					criteria = getCriteriaFromRS(rs);
					first = false;
				}
				Condition condition = getConditionFromRS(rs);
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
		criteria.setCriteriaId(rs.getLong("CRITERIAID"));
		criteria.setPattern(rs.getString("PATTERN"));
		return criteria;
	}
	
	private static Condition getConditionFromRS(ResultSet rs) throws SQLException {
		Condition condition = new Condition();
		condition.setConditionId(rs.getLong("CONDITIONID"));
		condition.setCriteriaId(rs.getLong("CRITERIAID"));
		condition.setSequence(rs.getInt("SEQUENCE"));
		condition.setFieldId(rs.getLong("FIELDID"));
		condition.setValue(rs.getString("VAL"));
		condition.setComputedWhereClause(rs.getString("COMPUTED_WHERE_CLAUSE"));
		
		FacilioField field = CommonCommandUtil.getFieldFromRS(rs);
		condition.setField(field);
		condition.setOperator(field.getDataType().getOperator(rs.getString("OPERATOR")));
		
		return condition;
	}
}
