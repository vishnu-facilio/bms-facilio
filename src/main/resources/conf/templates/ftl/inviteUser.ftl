<!DOCTYPE html>
<html>

<head>
    <title></title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css?family=Roboto:400,400i,500,700&display=swap" rel="stylesheet">
</head>

<body style="padding-left: 20px; padding-right: 20px;background-image: linear-gradient(to bottom, #412689, #271556);">
    <div
        style="max-width: 554px;margin: 0 auto;display: block;padding-top: 30px;background-image: linear-gradient(to bottom, #412689, #271556);">
        <div style="max-width: 474px;margin: 0 auto;display: block;padding-left: 20px; padding-right: 20px;">
            <div style="margin:20px auto 0;padding-left: 0;padding-right: 0;background-image: linear-gradient(to bottom,
            #412689, #271556);box-shadow: 0 1px 15px 0 rgba(0, 0, 0, 0.05); border: solid 1px #ebecf0;border-radius:
            15px;overflow: hidden;width: 100%;">

                <table cellpadding="0" cellspacing="0"
                    style="width:100%;max-width:100%;background: #ffffff;padding: 34px 26px;" border="0">
                    <tbody>
                        <tr>
                            <td colspan="100%">
                                <div
                                    style="font-family:'Roboto', Helvetica,Helvetica Neue,Arial, sans-serif,Times New Roman, Times, serif;font-size: 30px;font-weight: 700;text-align: left;">
                                    <strong
                                        style="color:#19182e;font-family:'Cera Pro','Helvetica Neue',Helvetica,Arial,sans-serif;font-size:18px;line-height:40px;text-align:center;Margin:0;margin:0;letter-spacing: 0.6px;">
                                        Hello ${toUser.name},
                                    </strong>
                                </div>
                                <div
                                    style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:13px;text-align:left;font-weight:300; padding-top:10px;letter-spacing: 0.6px;color: #19182e;line-height: 20px;">
                                    Welcome to ${org.name} on ${brandName}! You have been invited by ${inviter.name} to register.
                                </div>
                                <div
                                    style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:13px;text-align:left;font-weight:300;letter-spacing: 0.6px;color: #19182e;">
                                    Click the button to get started!
                                </div>
                                <div style="text-align: center; padding-top: 27px; padding-bottom: 27px;">
                                    <a href="${invitelink}"
                                        style="border-radius: 22.5px; background-color: #ff2a80;padding: 12px 24px;letter-spacing: 0.92px; text-align: center; color: #ffffff;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;text-decoration: none;cursor: pointer;font-size: 12px;text-transform: uppercase;font-weight: bold;">Activate
                                        My
                                        account</a>
                                </div>
                                <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#282c36;font-size:13px;text-align:left;font-weight:300;letter-spacing:
                                0.6px;color: #19182e;padding-top: 20px;line-height: 30px;">
                                    Or, copy and paste this link into your browser:
                                </div>
                                <div>
                                    <a href="${invitelink}"
                                        style="font-weight: normal; font-stretch: normal; font-style: normal; line-height:
                                    1.54; letter-spacing: 0.5px; word-break:break-all; color: #581dfb;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;text-decoration: none;font-size: 13px;" class="facilio-masked-url-email-log">
                                        ${invitelink}
                                    </a>
                                </div>
                                <div
                                    style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:13px;text-align:left;font-weight:300;letter-spacing: 0.6px;color: #19182e;padding-top: 20px;">
                                    This link will expire on ${expiryDate}.
                                </div>
                                <div
                                    style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:13px;text-align:left;font-weight:300;letter-spacing: 0.6px;color: #19182e;padding-top: 20px;">
                                    Don't forget to check out our Mobile Apps!
                                </div>
                                <div
                                    style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#282c36;font-size:13px;text-align:left;font-weight:300;letter-spacing: 0.6px;color: #19182e;padding-top: 20px;line-height: 25px;">
                                    Cheers,
                                </div>
                                <div
                                    style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-bottom:20px;color:#282c36;font-size:13px;text-align:left;font-weight:bold;letter-spacing: 0.6px;color: #19182e;">
                                    Team ${brandName}
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <table cellpadding="0" cellspacing="0"
                    style="width:100%;max-width:100%;padding: 20px 26px 20px;background: #ffffff;border-top: 1px solid#eae6f3;">
                    <tr>
                        <td colspan="100%">
                            <div style="font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;color:#282c36;font-size:11px;text-align:left;font-weight:300;letter-spacing:
                                0.6px;color: #19182e;line-height: 20px;">
                                If you have any trouble signing up, please reach out to <span
                                    style="color:#ff2a80 !important;"><a style="color:#ff2a80;text-decoration: none;font-size:11px;" href="mailto:${supportemail}" target="_blank">${supportemail}</a></span>
                            </div>
                        </td>
                    </tr>
                </table>

            </div>
        </div>
        <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;padding: 20px 26px 20px;">
            <tr>
                <td colspan="100%">
                    <div style="text-align: center;padding-top: 20px;">
                        <span style="padding-right: 10px;">
                            <a href="https://apps.apple.com/us/developer/facilio-inc/id1438082478" target="_blank"
                                style="color: #271556;">
                                <img src="https://static.facilio.com/guide/emailImg/apple-logo.png"
                                    style="width: 100px; height: auto;" />
                            </a>
                        </span>
                        <span>
                            <a href="https://play.google.com/store/apps/developer?id=Facilio" target="_blank"
                                style="color: #271556;">
                                <img src="https://static.facilio.com/guide/emailImg/google-logo.png"
                                    style="width: 110px; height: auto;" />
                            </a>
                        </span>
                    </div>
                </td>
            </tr>
            <tr>
                <td colspan="100%">
                    <div style="text-align: center;padding-top: 40px;">
                        <span style="padding-right: 10px;">
                            <a href="https://www.facebook.com/facilioinc/" target="_blank" style="color: #271556;">
                                <img src="https://static.facilio.com/guide/emailImg/fb-logo.png"
                                    style="width: 20px; height: 20px;" />
                            </a>
                        </span>
                        <span style="padding-right: 10px;">
                            <a href="https://twitter.com/FacilioInc" target="_blank" style="color: #271556;">
                                <img src="https://static.facilio.com/guide/emailImg/twitter-logo.png"
                                    style="width: 20px; height: 20px;" />
                            </a>
                        </span>
                        <span style="padding-right: 10px;">
                            <a href="https://www.linkedin.com/company/facilio-inc/" target="_blank"
                                style="color: #271556;">
                                <img src="https://static.facilio.com/guide/emailImg/linkedin-logo.png"
                                    style="width: 20px; height: 20px;" />
                            </a>
                        </span>
                        <span style="padding-right: 10px;">
                            <a href="https://www.youtube.com/channel/UCScGzOBp_yCz-2lapFxqjsw" target="_blank"
                                style="color: #271556;">
                                <img src="https://static.facilio.com/guide/emailImg/youtube-logo.png"
                                    style="width: 20px; height: 20px;" />
                            </a>
                        </span>
                        <span style="padding-right: 10px;">
                            <a href="https://www.instagram.com/facilioinc/" target="_blank" style="color: #271556;">
                                <img src="https://static.facilio.com/guide/emailImg/instagram-logo.png"
                                    style="width: 20px; height: 20px;" />
                            </a>
                        </span>
                    </div>
                </td>
            </tr>
            <tr>
                <td colspan="100%;">
                    <div
                        style="text-align: center;font-size: 10px; font-weight: normal; font-stretch: normal; font-style: normal; line-height: 2; letter-spacing: 1px; text-align: center; color: #d0c5ff !important;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;padding-top: 20px;">
                        &#169; 2020 <a href="${brandUrl}"
                            style="text-decoration: none;text-align: center;font-size: 10px; font-weight: normal; font-stretch: normal; font-style: normal; letter-spacing: 1px; text-align: center; color: #d0c5ff !important;font-family:'Roboto','Helvetica Neue',Helvetica,Arial,sans-serif!important;">${brandUrl}</a>
                        | ALL RIGHTS RESERVED
                    </div>
                </td>
            </tr>
        </table>
        <table cellpadding="0" cellspacing="0" style="width:100%;max-width:100%;padding: 0;">
            <tr>
                <td colspan="100%">
                    <div style="text-align: center;margin-top: 20px;width: 100%;">
                        <a href="#">
                            <img src="https://static.facilio.com/guide/emailImg/building-email.png"
                                style="width: 100%; height: auto;" alt="" title="" />
                        </a>
                    </div>
                </td>
            </tr>
        </table>
    </div>

</body>

</html>
