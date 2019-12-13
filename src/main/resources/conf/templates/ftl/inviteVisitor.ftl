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
                                          <strong style="color:#45474d;font-family:'Cera Pro','Helvetica Neue',Helvetica,Arial,sans-serif;font-size:35px;line-height:40px;text-align:center;Margin:0;margin:0;border-bottom:3px solid #ff3989;letter-spacing: 0.6px;">Facilio Inc</strong>
                                      </div>
                                      <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:40px;line-height:50px;text-align:left;font-weight:bold;padding-top: 20px;letter-spacing: 0.6px;">
                                         See you soon! 
                                      </div>
                                        <#if (vLog.visitedSpace) ?? >
                                      <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#474a51;font-size:20px;line-height:30px;text-align:left;min-width:auto!important;letter-spacing: 0.6px;">
                                          You have been invited to ${vLog.visitedSpace.name} <#if (vLog.host) ?? > by ${vLog.host.name} </#if> on ${(vLog.expectedCheckInTime)?number_to_datetime?string("YYYY-MM-dd HH:mm:ss")} ! 
                                    </div>
                                     <#else>
                                      <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#474a51;font-size:20px;line-height:30px;text-align:left;min-width:auto!important;letter-spacing: 0.6px;">
                                          You have been invited  <#if (vLog.host) ?? > by ${vLog.host.name} </#if> on ${(vLog.expectedCheckInTime)?number_to_datetime?string("YYYY-MM-dd HH:mm:ss")} ! 
                                    </div>
                                   
                                   </#if>
                                </td>
                              </tr>
                        </tbody>
                  </table>
                  <#if vLog.qrUrl ??>
                  <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;padding: 30px 55px 40px;">
                        <tr>
                              <td>
                                    <div style="color: #8e8c97;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;font-size: 14px;padding-bottom: 30px;letter-spacing: 0.5px;">
                                          Please show the QR code attached to this invite at the time of entry or use the invite code - ${vLog.passCode}.
                                    </div>
                              </td>
                        </tr>
                        <tr>
                              <td colspan="100%" align="center">
                                    <div style="width: 150px;height: auto;">
                                          <img src="${vLog.qrUrl}" width="130px" height="auto" />
                                    </div>
                              </td>
                        </tr>
                        <#if (location) ??>
                        <tr>
                                <td colspan="100%" style="padding-top: 40px;" align="right">
                                  <a href="${location}" style="border: 1px solid #ff3989; padding: 15px 30px;font-size: 14px;color: #fff;background: #ff3989;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;border-radius: 3px;text-decoration: none;letter-spacing: 0.6px;">
                                  <?xml version="1.0" encoding="iso-8859-1"?> <svg width="15" height="15" version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 512 512" style="enable-background:new 0 0 512 512;" xml:space="preserve"> <g> <g> <path fill="#fff" d="M256,0C153.755,0,70.573,83.182,70.573,185.426c0,126.888,165.939,313.167,173.004,321.035 c6.636,7.391,18.222,7.378,24.846,0c7.065-7.868,173.004-194.147,173.004-321.035C441.425,83.182,358.244,0,256,0z M256,278.719 c-51.442,0-93.292-41.851-93.292-93.293S204.559,92.134,256,92.134s93.291,41.851,93.291,93.293S307.441,278.719,256,278.719z"/> </g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> </svg>
                                  Directions
                                  <?xml version="1.0" encoding="iso-8859-1"?> <!-- Generator: Adobe Illustrator 18.1.1, SVG Export Plug-In . SVG Version: 6.00 Build 0)  --> <svg width="12" height="12" style="margin-left: 5px; vertical-align: middle;" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 185.343 185.343" style="enable-background:new 0 0 185.343 185.343;" xml:space="preserve"> <g> <g> <path style="fill:#fff;" d="M51.707,185.343c-2.741,0-5.493-1.044-7.593-3.149c-4.194-4.194-4.194-10.981,0-15.175 l74.352-74.347L44.114,18.32c-4.194-4.194-4.194-10.987,0-15.175c4.194-4.194,10.987-4.194,15.18,0l81.934,81.934 c4.194,4.194,4.194,10.987,0,15.175l-81.934,81.939C57.201,184.293,54.454,185.343,51.707,185.343z"/> </g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> <g> </g> </svg>
                                  </a>
                                </td>
                        </tr>
                        </#if>
                  </table>
                  </#if>
                  
            </div>
            <div style="text-align: center;margin-top: 20px;">
                        <img src="https://facilio.com/assets/images/logo.png" alt="" title="" style="width: 70px;height: 16x;padding-bottom: 10px;max-width: 100%;">
            </div>
      </div>
</body>

</html>