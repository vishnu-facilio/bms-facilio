package com.facilio.multiImport.importFileWriter;

import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class XLSheetWriter implements AbstractSheetWriter{
    private Sheet sheet;
    private Map<Integer,String> headerMap;
    private Map<Integer,String> columnIndexVsDateFormat;
    private Map<Integer,Short> columnIndexVsColour;
    private RowFunction rowFunction;
    private int currentRowNo;
    private XLFileWriter parent;
    private CreationHelper creationHelper;
    private Font headerFont;
    XLSheetWriter(Sheet sheet,XLFileWriter parent){
        Objects.requireNonNull(sheet);
        this.sheet = sheet;
        this.parent = parent;
        this.creationHelper = parent.workbook.getCreationHelper();
        headerFont = parent.workbook.createFont();
        headerFont.setFontHeightInPoints((short) 10.5);
    }

    @Override
    public AbstractSheetWriter setHeaderColors(Map<Integer, Short> columnIndexVsColour) throws Exception {
        this.columnIndexVsColour=columnIndexVsColour;
        return this;
    }

    @Override
    public XLSheetWriter createHeader(Map<Integer, String> headerMap) {
         FacilioUtil.throwIllegalArgumentException(MapUtils.isEmpty(headerMap),"header map should not be empty");
         this.headerMap=headerMap;
         Row row = sheet.createRow(0);

         for(int i=0;i<headerMap.size();i++){
             String columnName = headerMap.get(i);
             Cell headerCell = row.createCell(i);
             headerCell.setCellValue(columnName);

             if(MapUtils.isNotEmpty(columnIndexVsColour)){
                 if(columnIndexVsColour.get(i)!=null){
                     short indexColour = columnIndexVsColour.get(i);
                     CellStyle headerCellStyle = parent.workbook.createCellStyle();
                     headerCellStyle.setFillForegroundColor(indexColour);
                     headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                     headerCellStyle.setFont(headerFont);

                     headerCell.setCellStyle(headerCellStyle);
                 }
             }
             autoSizeColumn(headerCell);
         }
         currentRowNo++;
         return this;
    }

    @Override
    public AbstractSheetWriter setDateFormatColumnIndex(Map<Integer, String> columnIndexVsDateFormat) {
        this.columnIndexVsDateFormat = columnIndexVsDateFormat;


        return this;
    }

    @Override
    public void writeData(List<Map<String, Object>> dataList) throws Exception {
        for(Map<String,Object> data : dataList){
            Row row = sheet.createRow(currentRowNo);
            for(int i=0;i<headerMap.size();i++){
               String columnName = headerMap.get(i);

               Object cellValue = data.get(columnName);
               Cell cell = row.createCell(i);

               boolean isDateTypeCell=false;
               if(MapUtils.isNotEmpty(columnIndexVsDateFormat)){
                   if(columnIndexVsDateFormat.containsKey(i)){
                       isDateTypeCell = true;
                   }
               }
               if(cellValue == null){
                   cell.setCellValue("");
               }
               else if(cellValue instanceof Number){
                   if(cellValue instanceof Double){
                       cell.setCellValue(FacilioUtil.parseDouble(cellValue));
                   }
                   else if (cellValue instanceof Long){
                       cell.setCellValue(FacilioUtil.parseLong(cellValue));
                   }
                   else if(cellValue instanceof Integer){
                       cell.setCellValue(FacilioUtil.parseInt(cellValue));
                   }
                   if(isDateTypeCell){
                       try{
                           setDateCellValue(cell,cellValue,i);
                       }catch (Exception e){
                           e.printStackTrace();
                           cell.setCellValue(cellValue.toString());
                       }
                   }
               }
               else if (cellValue instanceof Boolean){
                   cell.setCellValue(FacilioUtil.parseBoolean(cellValue));
               }
               else if (cellValue instanceof String){
                   cell.setCellValue(cellValue.toString());
               }

              if(currentRowNo==3) {
                  autoSizeColumn(cell);
              }

            }
            if(rowFunction!=null){
                rowFunction.apply(currentRowNo);
            }
            currentRowNo++;
        }
    }

    @Override
    public AbstractSheetWriter afterRowFunction(RowFunction rowFunction) {
        this.rowFunction = rowFunction;
        return this;
    }

    private void setDateCellValue(Cell cell,Object cellValue,int index) throws ParseException {
        Long millis = FacilioUtil.parseLong(cellValue);
        String dateFormat = columnIndexVsDateFormat.get(index);
        String dateString = null;
        if(StringUtils.isNotEmpty(dateFormat)){
            dateString = DateTimeUtil.getFormattedTime(millis,dateFormat);
        }else{
            dateString = DateTimeUtil.getFormattedTime(millis);
        }
        SimpleDateFormat formatter =null;
        short dateFormatShortValue =0;
        if(StringUtils.isNotEmpty(dateFormat)){
            formatter = new SimpleDateFormat(dateFormat);
            dateFormatShortValue = creationHelper.createDataFormat().getFormat(dateFormat);
        }else{
            formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            dateFormatShortValue = creationHelper.createDataFormat().getFormat("dd-MMM-yyyy HH:mm:ss");
        }

        Date date = formatter.parse(dateString);
        System.out.println(date);
        System.out.println(formatter.format(date));
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(date);

        cell.setCellStyle(parent.workbook.createCellStyle());
        cell.getCellStyle().setDataFormat(dateFormatShortValue);
    }
    private void autoSizeColumn(Cell cell) {
        try {
            sheet.autoSizeColumn(cell.getColumnIndex());
            int maxWidth = 10000;
            if (sheet.getColumnWidth(cell.getColumnIndex()) > maxWidth) {
                CellStyle style = cell.getCellStyle();
                style.setWrapText(true);
                cell.setCellStyle(style);
                sheet.setColumnWidth(cell.getColumnIndex(), maxWidth);
            }
        }
        catch (Exception e) {
          e.printStackTrace();
        }
    }
}
