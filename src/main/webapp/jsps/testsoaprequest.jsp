<%@page import="java.io.IOException"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.InputStream"%>
<%@page import="java.io.BufferedReader"%>
<%
	String body = null;
	StringBuilder stringBuilder = new StringBuilder();
	BufferedReader bufferedReader = null;
	
	try {
	    InputStream inputStream = request.getInputStream();
	    if (inputStream != null) {
	        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	        char[] charBuffer = new char[128];
	        int bytesRead = -1;
	        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	            stringBuilder.append(charBuffer, 0, bytesRead);
	        }
	    } else {
	        stringBuilder.append("");
	    }
	} catch (IOException ex) {
	    throw ex;
	} finally {
	    if (bufferedReader != null) {
	        try {
	            bufferedReader.close();
	        } catch (IOException ex) {
	            throw ex;
	        }
	    }
	}
	
	body = stringBuilder.toString();
%>
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://www.w3.org/2003/05/soap-envelope" xmlns:SOAP-ENC="http://www.w3.org/2003/05/soap-encoding" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:EWSv122="http://www.schneider-electric.com/common/dataexchange/2011/05/DataExchangeInterface/Fault" xmlns:EWSv121="http://www.schneider-electric.com/common/dataexchange/2011/05">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <EWSv121:GetValuesResponse version="1.2">
         <EWSv121:GetValuesItems>
            <EWSv121:ValueItem>
               <EWSv121:Id>00/Server 1/Servers/AS-01/BACnet Interface/IP Network/BCU/Application/CDW Return Header Temp</EWSv121:Id>
               <EWSv121:State>0</EWSv121:State>
               <EWSv121:Value>23.9799995422363</EWSv121:Value>
            </EWSv121:ValueItem>
            <EWSv121:ValueItem>
               <EWSv121:Id>00/Server 1/Servers/AS-MZ/Modbus/FCU01/signals/Room_Temperature</EWSv121:Id>
               <EWSv121:State>0</EWSv121:State>
               <EWSv121:Value>24.5</EWSv121:Value>
            </EWSv121:ValueItem>
            <EWSv121:ValueItem>
               <EWSv121:Id>00/Server 1/Servers/AS-Roof/Modbus_VFDs/AHU_01_RF/Modbus Signals/Output Frequency</EWSv121:Id>
               <EWSv121:State>0</EWSv121:State>
               <EWSv121:Value>37.53</EWSv121:Value>
            </EWSv121:ValueItem>
         </EWSv121:GetValuesItems>
         <EWSv121:GetValuesErrorResults>
            <EWSv121:ErrorResult>
               <EWSv121:Id>00/Server 1/Servers/AS-01/AS-02/ReturnT</EWSv121:Id>
               <EWSv121:Message>INVALID_ID</EWSv121:Message>
            </EWSv121:ErrorResult>
         </EWSv121:GetValuesErrorResults>
      </EWSv121:GetValuesResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>