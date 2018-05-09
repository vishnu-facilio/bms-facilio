package com.facilio.bmsconsole.commands.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class FacilioTablePrinter {

    private static final int DEFAULT_MAX_TEXT_COL_WIDTH = 150;
    public static final int CATEGORY_STRING = 1;
    public static final int CATEGORY_INTEGER = 2;
    public static final int CATEGORY_DOUBLE = 3;
    public static final int CATEGORY_DATETIME = 4;
    public static final int CATEGORY_BOOLEAN = 5;
    public static final int CATEGORY_OTHER = 0;
    private static class Column {

        private String label;
        private int type;
        private String typeName;
        private int width = 0;
        private List<String> values = new ArrayList<>();
        private String justifyFlag = "";
        private int typeCategory = 0;

        public Column (String label, int type, String typeName) {
            this.label = label;
            this.type = type;
            this.typeName = typeName;
        }

        public String getLabel() {
            return label;
        }

        public int getType() {
            return type;
        }

        public String getTypeName() {
            return typeName;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void addValue(String value) {
            values.add(value);
        }

        public String getValue(int i) {
            return values.get(i);
        }

        public String getJustifyFlag() {
            return justifyFlag;
        }

        public void justifyLeft() {
            this.justifyFlag = "-";
        }

        public int getTypeCategory() {
            return typeCategory;
        }

        public void setTypeCategory(int typeCategory) {
            this.typeCategory = typeCategory;
        }
    }

    public static void printResultSet(ResultSet rs) {
        printResultSet(rs, DEFAULT_MAX_TEXT_COL_WIDTH);
    }

    public static void printResultSet(ResultSet rs, int maxStringColWidth) {
        System.out.println(getResultSetData(rs, maxStringColWidth));
    }
    
    public static String getResultSetData(ResultSet rs) {
    	return getResultSetData(rs, DEFAULT_MAX_TEXT_COL_WIDTH);
    }
    
    public static String getResultSetData(ResultSet rs, int maxStringColWidth) {
        try {
            if (rs == null) {
                System.err.println("FacilioTablePrinter Error: Result set is null!");
                return null;
            }
            if (rs.isClosed()) {
                System.err.println("FacilioTablePrinter Error: Result Set is closed!");
                return null;
            }
            if (maxStringColWidth < 1) {
                maxStringColWidth = DEFAULT_MAX_TEXT_COL_WIDTH;
            }

            ResultSetMetaData rsmd;
            rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            List<Column> columns = new ArrayList<>(columnCount);
            List<String> tableNames = new ArrayList<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                Column c = new Column(rsmd.getColumnLabel(i),
                        rsmd.getColumnType(i), rsmd.getColumnTypeName(i));
                c.setWidth(c.getLabel().length());
                c.setTypeCategory(whichCategory(c.getType()));
                columns.add(c);

                if (!tableNames.contains(rsmd.getTableName(i))) {
                    tableNames.add(rsmd.getTableName(i));
                }
            }
            int rowCount = 0;
            while (rs.next()) {
                for (int i = 0; i < columnCount; i++) {
                    Column c = columns.get(i);
                    String value;
                    int category = c.getTypeCategory();

                    if (category == CATEGORY_OTHER) {
                        value = "(" + c.getTypeName() + ")";

                    } else {
                        value = rs.getString(i+1) == null ? "NULL" : rs.getString(i+1);
                    }
                    switch (category) {
                        case CATEGORY_DOUBLE:
                            if (!value.equals("NULL")) {
                                Double dValue = rs.getDouble(i+1);
                                value = String.format("%.3f", dValue);
                            }
                            break;
                        case CATEGORY_STRING:
                            c.justifyLeft();
                            if (value.length() > maxStringColWidth) {
                                value = value.substring(0, maxStringColWidth - 3) + "...";
                            }
                            break;
                    }
                    c.setWidth(value.length() > c.getWidth() ? value.length() : c.getWidth());
                    c.addValue(value);
                }
                rowCount++;

            }
            StringBuilder strToPrint = new StringBuilder();
            StringBuilder rowSeparator = new StringBuilder();
            for (Column c : columns) {
                int width = c.getWidth();
                String toPrint;
                String name = c.getLabel();
                int diff = width - name.length();

                if ((diff%2) == 1) {
                    width++;
                    diff++;
                    c.setWidth(width);
                }
                int paddingSize = diff/2;
                String padding = new String(new char[paddingSize]).replace("\0", " ");
                toPrint = "| " + padding + name + padding + " ";
                strToPrint.append(toPrint);
                rowSeparator.append("+");
                rowSeparator.append(new String(new char[width + 2]).replace("\0", "-"));
            }
            String lineSeparator = System.getProperty("line.separator");
            lineSeparator = lineSeparator == null ? "\n" : lineSeparator;
            rowSeparator.append("+").append(lineSeparator);
            strToPrint.append("|").append(lineSeparator);
            strToPrint.insert(0, rowSeparator);
            strToPrint.append(rowSeparator);
            StringBuilder output = new StringBuilder();
            output.append(rowCount).append(" row(s)").append("\n");
            output.append(strToPrint.toString());
            String format;
            for (int i = 0; i < rowCount; i++) {
                for (Column c : columns) {
                    format = String.format("| %%%s%ds ", c.getJustifyFlag(), c.getWidth());
                    output.append(
                            String.format(format, c.getValue(i))
                    );
                }
                output.append("|").append("\n");
                output.append(rowSeparator);
            }
            output.append("\n");
            return output.toString();
        } catch (SQLException e) {
            System.err.println("SQL exception in DBTablePrinter. Message:");
            System.err.println(e.getMessage());
            return null;
        }
    }

    private static int whichCategory(int type) {
        switch (type) {
            case Types.BIGINT:
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return CATEGORY_INTEGER;

            case Types.REAL:
            case Types.DOUBLE:
            case Types.DECIMAL:
                return CATEGORY_DOUBLE;

            case Types.DATE:
            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return CATEGORY_DATETIME;

            case Types.BOOLEAN:
                return CATEGORY_BOOLEAN;

            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.CHAR:
            case Types.NCHAR:
                return CATEGORY_STRING;

            default:
                return CATEGORY_OTHER;
        }
    }
}