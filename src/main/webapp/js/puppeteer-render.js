const pupeteer = require('/home/ubuntu/.npm-global/lib/node_modules/puppeteer');

(async () => {
    const browser = await pupeteer.launch({headless: true, devtools: false, defaultViewport: null})

    const page = await browser.newPage()

    var authType = 'facilio'
    var pageUrl = process.argv[2]
    var output = process.argv[3]
    var token = process.argv[4]
    var domain  = process.argv[5]
    var htmlContent  = process.argv[6]
    var infoStr  = process.argv[7]

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
    		let isPdf = output.endsWith(".pdf");
    		let size = {width: 800, height: 768};
    		let info = JSON.parse(infoStr);
		if (info.width) {
			size.width = parseInt(info.width);
		}
		if (info.height) {
			size.height = parseInt(info.height);
		}
		if (!isPdf) {
			size.deviceScaleFactor = 2;
		}
    	
        await page.setCookie(... cookies)
        await page.emulateMedia('print');
        await page.setViewport(size);
        
        let headers = {
	        'X-Is-Export': 'true',
	        'X-Device-Type': 'puppeteer',
	        	'X-Current-Org': info.orgDomain,
	        	'X-Org-Id': info.orgId
        };
        if (info.currentSite) {
        		headers['X-current-site'] = info.currentSite + "";
        }
        await page.setExtraHTTPHeaders(headers);
        
        if(htmlContent && htmlContent != "false")
        {
        	await page.setContent(htmlContent, {
                waitUntil: 'networkidle0'
            })
        }
        else
        {
        	await page.goto(pageUrl, {
                waitUntil: 'networkidle0'
            });   	
        }
        
        if (isPdf) {
        		await page.pdf({path: output, format: 'A4'});
        }
        else {
        		if (info.printableArea) {
        			const el = await page.$(info.printableArea);
        			await el.screenshot({path: output, quality: 100});
        		}
        		else {
        			await page.screenshot({path: output, quality: 100});        			
        		}
        }

    }catch(e){
        console.log(e)
    }finally {
    	  	await page.close();
    	  	await browser.close();
    }
})()
