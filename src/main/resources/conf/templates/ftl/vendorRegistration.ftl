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
                                                Dear Vendor
                                      </div>
                                        <#if (fetchVendor.registeredBy) ??>
                                      <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#474a51;font-size:20px;line-height:30px;text-align:left;min-width:auto!important;letter-spacing: 0.6px;">
                                          Thank you for your desire to work with ${fetchVendor.registeredBy.name} (${fetchVendor.registeredBy.email}) and welcome!
                                    </div>
                                    <#else>
                                         
                                    <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#474a51;font-size:20px;line-height:30px;text-align:left;min-width:auto!important;letter-spacing: 0.6px;">
                                          Thank you for your desire to work with us and welcome!
                                    </div>
                                 </#if>
                                </td>
                              </tr>
                        </tbody>
                  </table>
                  <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;padding: 30px 55px;">
                        <tr>
                              <td>
                                    <div style="color: #8e8c97;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;font-size: 14px;padding-bottom: 30px;letter-spacing: 0.5px;line-height: 20px;">
                                          You are hereby requested to upload your certificate of Insurance [COI] to be listed as an approved vendor in ${org.name}. 
                                    </div>
                              </td>
                        </tr>
                        <tr>
                              <td colspan="100%" align="center" style="padding-top: 30px;padding-bottom: 30px;">
                              <#if (getVendorPermaLink[0]) ?? >
                                    <a href="${getVendorPermaLink[0]}" style="border-radius: 3px; background-color: #ff3184;font-size: 14px;font-weight: 600;border: none;text-decoration: none;color: #fff;padding: 18px 50px;letter-spacing: 0.5px;cursor: pointer !important;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;margin-left: 20px;">Upload COI</a>
                               <#else>
                                    <a href="https://app.facilio.com/app/at/vendors/list/all" style="border-radius: 3px; background-color: #ff3184;font-size: 14px;font-weight: 600;border: none;text-decoration: none;color: #fff;padding: 18px 50px;letter-spacing: 0.5px;cursor: pointer !important;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;margin-left: 20px;">Upload COI</a>
                                    
                              </td>
                        </tr>
                        <tr>
                              <td>
                                    <div style="color: #8e8c97;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;font-size: 14px;letter-spacing: 0.5px;padding-top: 30px;line-height: 23px;">
                                          We look forward to doing business with you!
                                    </div>
                              </td>
                        </tr>
                       
                  </table>
                  
            </div>
            <div style="text-align: center;margin-top: 20px;">
                        <img src="https://facilio.com/assets/images/logo.png" alt="" title="" style="width: 70px;height: 16x;padding-bottom: 10px;max-width: 100%;">
            </div>
      </div>
</body>

</html>