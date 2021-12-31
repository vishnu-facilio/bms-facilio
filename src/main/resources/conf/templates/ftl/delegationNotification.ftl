<!DOCTYPE html>
<html>

<head>
      <title></title>
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <link href="https://fonts.googleapis.com/css?family=Open+Sans&display=swap" rel="stylesheet">
</head>

<body style="background: #fafafa;">
      <div style="max-width:700px;margin: 0 auto;display: block;padding-top: 20px;padding-bottom: 30px;padding-left: 20px;padding-right: 20px;">
            <div style="margin:20px auto 0;max-width:700px;padding-left: 0;padding-right: 0;background-color:#FFFFFF;padding-bottom: 50px;box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05); border: solid 1px rgba(0, 0, 0, 0.08);">
                   <table cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;min-width:700px;max-width:700px;padding-left: 50px;padding-right: 50px;padding-bottom: 0px;margin-top: 0px;" border="0">
                        <tbody>
                        <tr>
                              <td>
                                    <div class="td-width-mobile" style="padding-right: 30px;">
                                          <div style="font-size: 22px; font-weight: 600; font-style: normal; font-stretch: normal; line-height: 1.36; letter-spacing: 0.3px; color: #19191c;text-align: left;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;padding-top: 30px;padding-bottom: 30px;">
                                          <img src="https://facilio.com/images/facilio-blue-logo.svg" alt="" title="" width="80"/>
                                          </div>      
                                        <div style="font-size: 16px; font-weight: 500; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 530px;width: 530px;padding-bottom: 10px;">Hello ${delegateUser.name}</div>
                                        <div style="font-size: 14px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">
                                          This is to inform you that <b>${dUser.name}</b> has delegated you with the below activities between the period  <b>${fromTime?number_to_date?string["dd-MM-yyyy"]}</b> to <b>${toTime?number_to_date?string["dd-MM-yyyy"]}</b>.
                                        </div>
                                        <div style="padding-top: 16px;font-size: 14px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 20px; letter-spacing: 0.4px; color: #324056;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 550px;">
                                          You will be able to
                                          </div>
                                          <ul>
                                                <#list responsibilities as module>
                                                <li style="padding-top: 16px;font-size: 14px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 20px; letter-spacing: 0.4px; color: #324056;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 550px;">${module}</li>
                                                </#list>
                                          </ul>
                                          <div style="padding-top: 20px;font-size: 14px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 570px;">
                                          To view and accept the activities, follow the below steps.
                                          </div>
                                          <!-- <div style="margin-top: 35px;margin-bottom: 35px;">
                                          <a href="" style="border: 0;border-radius: 3px;background:#ff3989;font-size: 13px; font-weight: bold; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.8px; text-align: center; color: #ffffff;text-decoration: none;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;margin-right: 15px;padding: 17px 24px;margin-top: 30px;">CONFIRM ACCOUNT</a>
                                          </div> -->
                                          <div style="padding-top: 10px;font-size: 14px; font-weight: 400; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 550px;">
                                           Log in to ${appDomain} <b> > My Profile > Delegates > Select “Delegated to me”</b>
                                          </div>
                                          <div style="padding-top: 30px;font-size: 14px; font-style: normal; font-stretch: normal; line-height: 1.53; letter-spacing: 0.4px; color: #324056;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;max-width: 550px;">
                                          <div style="font-weight: bold;padding-bottom: 5px;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">Regards</div>
                                          Team Facilio
                                          </div>
                                    </div>
                              </td>
                        </tr>
                  </tbody>
            </table>
</td>
</tr>
            </table>
            </div>
            <div style="text-align: center;margin-top: 20px;">
            
            <div style="font-size: 11px; font-weight: normal; font-style: normal; font-stretch: normal; line-height: normal; letter-spacing: 0.28px; text-align: center; color: #8e8c97;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;">© 2021 <a href="" style="padding-left: 5px;font-size: 11px; font-weight: 500; font-style: normal; font-stretch: normal; line-height: 20px; letter-spacing: 0.5px; color: #396dc2;font-family:'Open Sans',Ubuntu, Helvetica,Arial, sans-serif,Times New Roman, Times, serif;text-decoration: none;">https://facilio.com</a> | ALL RIGHTS RESERVED</div>
            </div>
      </div>
</body>

</html>