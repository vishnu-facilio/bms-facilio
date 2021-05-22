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

	let retryCount = 2
	let errorOccurred = false
	const TIMEOUT_MS = 2000

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
		await page.setExtraHTTPHeaders(headers);

		page.on('response', async (response) => {
			if (!errorOccurred && response.status() == 502) { // If server is not available, retry again
				errorOccurred = true;
				--retryCount;
				console.log('Server not reachable for pdf generation', await response.text());
			}
			else if (info.orgId == '396' && response.status() != 200) {
				console.log('Error occurred on pdf generation---',response.url(),  response.status());
				errorOccurred = true;
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
			if (errorOccurred && retryCount > 0) {
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
