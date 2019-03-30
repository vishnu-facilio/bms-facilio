package com.facilio.sql;

import com.facilio.transaction.FacilioConnectionPool;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.Map;

public class SQLScriptRunner {

	private static final Logger LOGGER = LogManager.getLogger(DBUtil.class.getName());

	private static final String DELIMITER = ";";
	
	private final boolean stopOnError;
	
	private StringSubstitutor substitutor = null;
	private File file = null;
	
	private boolean executeSingleStatement = true;		// for some database, we should execute complete sql, rather than executing single statement. example, MSSQL

	public SQLScriptRunner(File file, boolean stopOnError, Map<String, String> paramValues, boolean executeSingleStatement) {
		this.file = file;
		this.stopOnError = stopOnError;
		
		if(paramValues != null && paramValues.size() > 0) {
			substitutor = new StringSubstitutor(paramValues);
		}
		this.executeSingleStatement = executeSingleStatement;
	}

	public void runScript() throws SQLException, IOException {
	//	Connection conn =FacilioConnectionPool.getInstance().getConnection();
		Reader fileReader = null;
		try {
			fileReader = new FileReader(file);
			runScript( fileReader);
		} 
		catch (SQLException | FileNotFoundException e) {
			throw e;
		}
		finally {
			if(fileReader != null) {
				try {
					fileReader.close();
				}
				catch(IOException e) {
					LOGGER.log(Level.ERROR, "Exception while closing resource ", e);
				}
			}
		}
	}

	private void runScript( Reader reader) throws SQLException, IOException {
		Connection conn = FacilioConnectionPool.getInstance().getConnection();
		StringBuilder command = null;
		try {
			LineNumberReader lineReader = new LineNumberReader(reader);
			String line;
			String delimiter = DELIMITER;
			
			StringBuilder overallStringBuffer = new StringBuilder();
			
			while ((line = lineReader.readLine()) != null) {
				if (command == null) {
					command = new StringBuilder();
				}
				String trimmedLine = line.trim();

				if(!trimmedLine.isEmpty()) {
					if(trimmedLine.startsWith("--") || trimmedLine.startsWith("//")) {
						System.out.println(trimmedLine.substring(2));
					}
					else if(trimmedLine.startsWith("#")) {
						System.out.println(trimmedLine.substring(1));
					}
					else if(trimmedLine.toUpperCase().startsWith("DELIMITER")) {
						String[] delimiters = trimmedLine.toUpperCase().split("DELIMITER");
						if(delimiters != null && delimiters.length > 1) {
							delimiter = delimiters[1].trim();
						}
					}
					else  {
						if(trimmedLine.endsWith(delimiter)) {
							command.append(trimmedLine.substring(0, trimmedLine.lastIndexOf(delimiter)));
							command.append(" ");
							if (executeSingleStatement) {
								execCommand(conn, command, lineReader.getLineNumber());	
							} else {
								overallStringBuffer.append(command + "\n");
							}
							command = null;
						}
						else {
							command.append(trimmedLine);
							command.append("\n");
						}
					}
				}
			}
			if (command != null && command.length() != 0) {
				if (executeSingleStatement) {
					execCommand(conn, command, lineReader.getLineNumber());
				} else {
					overallStringBuffer.append(command);
					overallStringBuffer.append("\n");
				}
			}
			if (!executeSingleStatement) {
				execCommand(conn, overallStringBuffer, -1);
			}
		}
		catch (SQLException e) {
			throw e;
		}finally
		{
			conn.close();
		}
	}

	private void execCommand(Connection conn, StringBuilder command, int lineNumber) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		boolean hasResults = false;
		try {
			stmt = conn.createStatement();
			
			String sql = null;
			
			if(substitutor != null) {
				sql = substitutor.replace(command.toString());
			}
			else {
				sql = command.toString();
			}
					
			hasResults = stmt.execute(sql);
			
			rs = stmt.getResultSet();
	        if (hasResults && rs != null) {
	            ResultSetMetaData md = rs.getMetaData();
	            int cols = md.getColumnCount();
	            for (int i = 1; i <= cols; i++) {
	                String name = md.getColumnLabel(i);
	                System.out.print(name + "\t");
	            }
	            System.out.println();
	            while (rs.next()) {
	                for (int i = 1; i <= cols; i++) {
	                    String value = rs.getString(i);
	                    System.out.print(value + "\t");
	                }
	                System.out.println();
	            }
	        }
			
		} 
		catch (SQLException e) {
			System.err.println(String.format("Error executing '%s' (line %d): %s", command, lineNumber, e.getMessage()));
			if (stopOnError) {
				throw e;
			}
		}
		finally {
	        DBUtil.closeAll(stmt, rs);
		}
    }
}