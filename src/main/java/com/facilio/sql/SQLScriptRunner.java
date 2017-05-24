package com.facilio.sql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.facilio.transaction.FacilioConnectionPool;

public class SQLScriptRunner {

	private static final String DELIMITER = ";";
	
	private final boolean stopOnError;
	private final boolean autoCommit;
	
	private File file = null;

	public SQLScriptRunner(File file, boolean autoCommit, boolean stopOnError) {
		this.file = file;
		this.autoCommit = autoCommit;
		this.stopOnError = stopOnError;
	}

	public void runScript() throws SQLException, IOException {
		Reader fileReader = null;
		Connection conn = null;
		try {
			fileReader = new FileReader(file);
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			boolean originalAutoCommit = conn.getAutoCommit();
			try {
				if (originalAutoCommit != autoCommit) {
					conn.setAutoCommit(autoCommit);
				}
				runScript(conn, fileReader);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				throw e;
			} 
			finally {
				conn.setAutoCommit(originalAutoCommit);
			}
		} 
		catch (SQLException | FileNotFoundException e) {
			throw e;
		}
		finally {
			if(conn != null) {
				try {
					conn.close();
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}

			if(fileReader != null) {
				try {
					fileReader.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void runScript(Connection conn, Reader reader) throws SQLException, IOException {
		StringBuilder command = null;
		try {
			LineNumberReader lineReader = new LineNumberReader(reader);
			String line;
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
					else  {
						if(trimmedLine.endsWith(DELIMITER)) {
							command.append(trimmedLine.substring(0, trimmedLine.lastIndexOf(DELIMITER)));
							command.append(" ");
							execCommand(conn, command, lineReader.getLineNumber());
							command = null;
						}
						else {
							command.append(trimmedLine);
							command.append("\n");
						}
					}
				}
			}
			if (command != null) {
				execCommand(conn, command, lineReader.getLineNumber());
			}
			if (!autoCommit) {
				conn.commit();
			}
		}
		catch (SQLException e) {
			if(!autoCommit) {
				conn.rollback();
			}
			throw e;
		}
	}

	private void execCommand(Connection conn, StringBuilder command, int lineNumber) throws SQLException {
		System.out.println(command);
		Statement stmt = null;
		ResultSet rs = null;
		boolean hasResults = false;
		try {
			stmt = conn.createStatement();
			hasResults = stmt.execute(command.toString());
			
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
	        if(rs != null) {
	        	try {
	        		rs.close();
	        	}
	        	catch(SQLException e) {
	        		e.printStackTrace();
	        	}
	        }
	        if(stmt != null) {
		        try {
		            stmt.close();
		        } catch (SQLException e) {
		        	e.printStackTrace();
		        }
	        }
		}
    }
}