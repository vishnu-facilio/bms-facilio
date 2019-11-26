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
      <div style="max-width:600px;margin: 0 auto;display: block;">
            <div style="margin:20px auto 0;padding-left: 0;padding-right: 0;background-color:#FFFFFF;border-radius: 10px;overflow: hidden;width: 100%;">
                 
                 
                  <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;padding: 30px 55px 40px;">
                        <tr>
                              <td colspan="100%" style="border-bottom: 1px solid #eee;">
                                    <img src="http://api.qrserver.com/v1/create-qr-code/?data=test" alt="" title="" style="width: 70px;height: 16x;padding-bottom: 10px;max-width: 100%;">
                              </td>
                        </tr>
                        <tr>
                              <td style="padding-top: 30px;">
                              <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:40px;line-height:50px;text-align:left;font-weight:bold;padding-top: 20px;letter-spacing: 0.6px;">
                                          Please Respond Soon
                                          </div>
                                          <#if (vLog.visitedSpace) ??>
                                    <div style="color: #000;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;font-size: 16px;padding-bottom: 30px;letter-spacing: 0.5px;">
                                          ${vLog.visitor.name} has arrived at ${vLog.visitedSpace.name} and is awaiting your approval! 
                                    </div>
                                    <#else>
                                    <div style="color: #000;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;font-size: 16px;padding-bottom: 30px;letter-spacing: 0.5px;">
                                          ${vLog.visitor.name} has arrived and is awaiting your approval! 
                                    </div>
                                    
                                    </#if>
                                    <div style="color: #000;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;font-size: 18px;padding-bottom: 30px;letter-spacing: 0.5px;font-weight: 700;">
                                          What do you wish to do? 
                                    </div>
                              </td>
                        </tr>
                        <tr>
                              <td>
                                    <a href="https://app.facilio.com/app/home/visitor/logs/all" style="border-radius: 3px; background-color: #5bc293;font-size: 14px;font-weight: 600;border: none;text-decoration: none;color: #fff;padding: 10px 20px;letter-spacing: 0.5px;cursor: pointer !important;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;">Approve</a>
                                    <a href="https://app.facilio.com/app/home/visitor/logs/all" style="border-radius: 3px; background-color: #f66a6a;font-size: 14px;font-weight: 600;border: none;text-decoration: none;color: #fff;padding: 10px 27px;letter-spacing: 0.5px;cursor: pointer !important;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;margin-left: 20px;">Reject</a>
                                    <a href="https://app.facilio.com/app/home/visitor/logs/all" style="border-radius: 3px; background-color: #fdbd39;font-size: 14px;font-weight: 600;border: none;text-decoration: none;color: #fff;padding: 10px 33px;letter-spacing: 0.5px;cursor: pointer !important;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;margin-left: 20px;">Wait</a>
                              </td>
                        </tr>
                        <!-- <tr>
                              <td style="padding-top: 80px;">
                                    <div style="font-family: 'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color: #7f8498; font-size: 14px; letter-spacing: 0.9px;">
                                          Signature: 
                                    </div>
                                    <div style="color: #000; font-family: 'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important; font-size: 16px; padding-bottom: 30px; letter-spacing: 0.5px; font-weight: 500;padding-top: 5px;">
                                          Please respond soon.
                                    </div>
                              </td>
                        </tr> -->
                  </table>
                  
            </div>
      </div>
</body>

</html>