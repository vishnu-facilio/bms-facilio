const homedir = require('os').homedir();
//const pupeteer = require('/usr/local/lib/node_modules/puppeteer');
const pupeteer = require(homedir + '/.npm-global/lib/node_modules/puppeteer');

(async () => {
    const browser = await pupeteer.launch({headless: true, devtools: false, defaultViewport: null})

    const page = await browser.newPage();
    const widgetPage = await browser.newPage();
    
    await page.setDefaultNavigationTimeout(0);
    await widgetPage.setDefaultNavigationTimeout(0); 

    var pageUrl = process.argv[2]
    var output = process.argv[3]
    var token = process.argv[4]
    var domain  = process.argv[5]
    var exportType  = process.argv[6]
    var infoStr  = process.argv[7]
    var configStr  = process.argv[8]
    var widgetContextStr  = process.argv[9]

    var cookies = [
        {
            name: 'fc.overrideSession',
            value: 'true',
            domain: domain,
            path: '/'
        },
        {
            name:'fc.loggedIn',
            value: 'true',
            domain: domain,
            path: '/'
        }
    ]

    try {
    	let info = JSON.parse(infoStr);
    	let config = JSON.parse(configStr);
    	let widgetContext = JSON.parse(widgetContextStr);
    	
    	await page.setCookie(... cookies);
        
        let headers = {
        	'Authorization': token,
        	'X-Is-Export': 'true',
	        'X-Device-Type': 'puppeteer',
	        'X-Current-Org': info.orgDomain,
	        'X-Org-Id': info.orgId
        };
        if (info.currentSite) {
        	headers['X-current-site'] = info.currentSite + "";
        }
        await page.setExtraHTTPHeaders(headers);
        
        await page.evaluateOnNewDocument((data) => {
            window.widgetContext = data;
        }, widgetContext);
        
        await page.goto(pageUrl, {
	        waitUntil: 'networkidle0'
	    });
        
        const elementHandle = await page.$('.fc-connected-app-iframe');
		const frame = await elementHandle.contentFrame();
		const content = await frame.content();
	    console.log(content);
	    
		await widgetPage.setContent(content, {
            waitUntil: 'networkidle0'
        })
        
        if (exportType == "PDF") {
        	let landscape = config.landscape || false;
        	let screen = config.screen || false;
        	let scale = config.scale || null;
        	let pageRanges = config.pageRanges || null;
        	let page_size = config.page_size || 'A4';
        	let header_template = config.header_template;
        	let footer_template = config.footer_template;
        	let default_footer = config.default_footer || false;
        	let margin = {};
        	if (config.margin_top) {
        		margin.top = config.margin_top;
        	}
        	if (config.margin_right) {
        		margin.right = config.margin_right;
        	}
        	if (config.margin_bottom) {
        		margin.bottom = config.margin_bottom;
        	}
        	if (config.margin_left) {
        		margin.left = config.margin_left;
        	}
        	
        	let pdfOptions = {path: output};
        	pdfOptions.format = page_size;
        	pdfOptions.margin = margin;
        	
        	if (scale) {
        		pdfOptions.scale = scale;
        	}
        	if (pageRanges) {
        		pdfOptions.pageRanges = pageRanges;
        	}
        	if (header_template || footer_template || default_footer) {
        		pdfOptions.displayHeaderFooter = true;
        	}
        	if (header_template) {
        		pdfOptions.headerTemplate = header_template;
        	}
        	if (footer_template) {
        		pdfOptions.footerTemplate = footer_template;
        	}
        	else if (default_footer) {
        		var cssb = [];
    			cssb.push('<style>');
    			cssb.push('h1 {font-size:10px; margin-left:30px;}');
    			cssb.push('</style>');
    			var css = cssb.join('');
    			
    			pdfOptions.footerTemplate = css + '<h1>Page <span class="pageNumber"></span> of <span class="totalPages"></span></h1>';
        	}
        	
        	if (!screen) {
        		await widgetPage.emulateMedia('print');
        	}
        	
        	await widgetPage.pdf(pdfOptions);
        }
        else {
        	let selector = config.selector || null;
        	let quality = config.quality || 100;
        	let fullPage = config.fullPage || false;
        	let omitBackground = config.omitBackground || false;
        	
        	let imageOptions = {path: output};
        	imageOptions.quality = quality;
        	imageOptions.fullPage = fullPage;
        	imageOptions.omitBackground = omitBackground;
        
        	if (selector) {
    			const el = await widgetPage.$(selector);
    			await el.screenshot(imageOptions);
    		}
    		else {
    			await widgetPage.screenshot(imageOptions);        			
    		}
        }
    }
    catch(e) {
        console.log(e);
    }
    finally {
    	await widgetPage.close();
    	await page.close();
    	await browser.close();
    }
})()
