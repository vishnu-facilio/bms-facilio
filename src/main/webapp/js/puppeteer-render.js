const homedir = require('os').homedir();
const pupeteer = require(homedir + '/.npm-global/lib/node_modules/puppeteer');

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

	let retryCount = 5
	let errorOccurred = false
	const TIMEOUT_MS = 4000
	const LOG_MAX_LEN = 5000;

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
				'Authorization': token,
				'X-Is-Export': 'true',
				'X-Device-Type': 'puppeteer',
				'X-Current-Org': info.orgDomain,
				'X-Org-Id': info.orgId
		};
		if (info.currentSite) {
			headers['X-current-site'] = info.currentSite + "";
		}
		if (info.switchSiteValue) {
        	headers['X-Switch-Value'] = info.switchSiteValue + "";
        }
		await page.setExtraHTTPHeaders(headers);
		await page.setRequestInterception(true);
		page.on('request', async (request) => {
			let url = request.url();
			if (url.startsWith("https://app.facilio.com/")) {
				url += !url.includes("?") ? "?" : "&";
				url += "fetchStackTrace=true";
				request.continue({url});
			}
			else {
				request.continue();
			}
		})

		async function parseResponse(response) {
			const contentType = response.headers()['content-type'];
			let text = "";
			if (response.url().startsWith("https://app.facilio.com/")) {
				if (contentType && contentType.indexOf("application/json") !== -1) {
					text = await response.json();
					text = JSON.stringify(text);
				}
				else {
					text = await response.text();
				}
				text = text.substring(0,LOG_MAX_LEN);
			}
			return "url - "+response.url() +",  responsecode - " + response.status() + ", Body - \n" + text;
		}

		page.on('response', async (response) => {
			if (response.status() != 200 && response.status() != 304) {
				let responseText = await parseResponse(response);
				console.log('Error occurred on pdf generation----\n', responseText);
				if (response.status() == 204 && !response.url().startsWith("https://app.facilio.com/")) {
					return;
				}
				if (!errorOccurred) {
					errorOccurred = true;
					--retryCount;
				}
			}
		});
		
		page.on('requestfailed', request => {
			if (request.url().startsWith("https://app.facilio.com/")) {
				console.log('pdf requestfailed---', request.url(), " ",request.failure().errorText.substring(0,LOG_MAX_LEN));
			}
		});


		const sleep = ms => new Promise(resolve => setTimeout(resolve, ms))

		async function loadPage() {

			if(htmlContent && htmlContent != "false")
			{
				const buff = Buffer.from(htmlContent, 'base64');
				htmlContent = buff.toString('utf-8');
				await page.setContent(htmlContent, {
					waitUntil: 'networkidle0',
					timeout: 0
				})
			}
			else
			{ 
				await page.goto(pageUrl, {
					waitUntil: 'networkidle0',
					timeout: 0
				});
			}
			let isOrg = info.orgId == "396" ||  info.orgId == "320" || info.orgId == "274";
			if (errorOccurred && retryCount > 0 && isOrg) {
				await sleep(TIMEOUT_MS);
				errorOccurred = false;
				await loadPage();
			}
		}

		await loadPage();

		if (isPdf) {
			let config = {path: output, format: 'A4'};
			if (info.showFooter) {
				var cssb = [];
				cssb.push('<style>');
				if (info.footerStyle) {
					cssb.push(info.footerStyle);
				}
				else {
					cssb.push('h1 {font-size:10px; margin-left:30px;}');
				}
				cssb.push('</style>');
				var css = cssb.join('');

				config.displayHeaderFooter = true;
				if (info.footerHtml) {
					config.footerTemplate = css + info.footerHtml;
				}
				else {
					config.footerTemplate = css + '<h1>Page <span class="pageNumber"></span> of <span class="totalPages"></span></h1>';
				}
			}
			await page.pdf(config);

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
