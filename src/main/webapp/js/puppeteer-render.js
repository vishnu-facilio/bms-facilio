const pupeteer = require('/home/ubuntu/.npm-global/lib/node_modules/puppeteer');

(async () => {
    const browser = await pupeteer.launch({headless: true, devtools: false, defaultViewport: null})

    const page = await browser.newPage()

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
            value: encodeURIComponent('facilio ' + token),
            domain: domain,
            path: '/'
        },
        {
            name:'fc.authtype',
            value: authType,
            domain: domain,
            path: '/'
        }
    ]
    try{
    await page.setCookie(... cookies)
    await page.emulateMedia('print');
    await page.setViewport({width: 800, height: 768});

    await page.goto(pageUrl, {
        waitUntil: 'networkidle0'
    });
    await page.pdf({path: output, format: 'A4'})
    await browser.close()

    }catch(e){
        console.log(e)
    }
})()
