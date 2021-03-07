<!DOCTYPE html>
<html>

<head>
      <title></title>
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link href="https://fonts.googleapis.com/css?family=Roboto:400,400i,500,700&display=swap" rel="stylesheet">
</head>

<body style="background: #ffffff;">
<#setting time_zone=org.timezone>
      <div style="max-width:600px;margin: 0 auto;display: block;padding-top: 50px;">
            <div style="margin:20px auto 0;padding-left: 0;padding-right: 0;background-color:#FFFFFF;box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05); border: solid 1px #ebecf0;border-radius: 10px;overflow: hidden;width: 100%;">
                 
                  <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;background: #f8f9fc;padding: 30px 55px;"
                        border="0">
                        <tbody>
                              <tr>
                                <td colspan="100%">
                                      <div style="font-family:'Roboto', Helvetica,Helvetica Neue,Arial, sans-serif,Times New Roman, Times, serif;font-size: 30px;font-weight: 700;text-align: left;">
                                          <strong style="color:#45474d;font-family:'Cera Pro','Helvetica Neue',Helvetica,Arial,sans-serif;font-size:35px;line-height:40px;text-align:center;Margin:0;margin:0;border-bottom:3px solid #ff3989;letter-spacing: 0.6px;">${org.name}</strong>
                                      </div>
                                      <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:40px;line-height:50px;text-align:left;font-weight:bold;padding-top: 20px;letter-spacing: 0.6px;">
                                                Happy Meeting! 
                                      </div>
                                      <#if (vLog.visitedSpace) ?? >
                                      <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#474a51;font-size:20px;line-height:30px;text-align:left;min-width:auto!important;letter-spacing: 0.6px;">
                                          You have successfully checked in at ${spaceName} on ${(vLog.checkInTime)?number_to_datetime?string("YYYY-MM-dd HH:mm:ss")}.<#if (vLog.host) ??> You are on your way to meet ${vLog.host.name}.<#else>Have a Good Day!</#if>
                                    </div>
                                    <#else>
                                     <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#474a51;font-size:20px;line-height:30px;text-align:left;min-width:auto!important;letter-spacing: 0.6px;">
                                          You have successfully checked in on ${(vLog.checkInTime)?number_to_datetime?string("YYYY-MM-dd HH:mm:ss")}.<#if (vLog.host) ??> You are on your way to meet ${vLog.host.name}.<#else>Have a Good Day! </#if>
                                    </div>
                                    </#if>
                                </td>
                              </tr>
                        </tbody>
                  </table>
                  <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;padding: 30px 55px;">
                          <#if vLog.qrUrl ??>
                
                        <tr>
                              <td>
                                    <div style="color: #8e8c97;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;font-size: 14px;padding-bottom: 30px;letter-spacing: 0.5px;">Here is the QR code for quick access</div>
                              </td>
                        </tr>
                        <tr>
                              <td colspan="100%" align="center">
                                    <div style="width: 150px;height: auto;">
                                          <img src="${vLog.qrUrl}" width="130px" height="auto" />
                                    </div>
                              </td>
                        </tr>
                        </#if>
                  </table>
                  
            </div>
            <div style="text-align: center;margin-top: 20px;">
                        <img src="https://facilio.com/images/facilio-blue-logo.svg" alt="" title="" style="width: 70px;height: 16x;padding-bottom: 10px;max-width: 100%;">
            </div>
      </div>
</body>

</html>