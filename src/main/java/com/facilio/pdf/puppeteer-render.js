const pupeteer = require('puppeteer');

(async () => {
    const browser = await pupeteer.launch({devtools: false, defaultViewport: null})
    const page = await browser.newPage()

    console.log(process.argv)
    var authType = 'facilio'
    var pageUrl = process.argv[2]
    var output = process.argv[3]
    var token = process.argv[4]
    var domain  = process.argv[5]
    
    var cookies = [
        {
            name:'fc.idToken.facilio',
            value: token,
            domain: domain,
            path: '/'
        },
        {
            name: 'fc.overrideSession',
            value: 'true',
            domain: domain,
            path: '/'
        },
        {
            name:'fc.idToken',
            value: 'facilio ' + token,
            domain: 'localhost',
            path: '/'
        },
        {
            name:'fc.authtype',
            value: authType,
            domain: domain,
            path: '/'
        }
    ]
    await page.setCookie(... cookies)
    await page.emulateMedia('print');
    await page.setViewport({width: 800, height: 768});
    
    await page.goto(pageUrl, {
        waitUntil: 'networkidle0'
    });
    await page.pdf({path: output, format: 'A4'})

    await browser.close()
})()