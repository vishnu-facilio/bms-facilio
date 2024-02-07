import UAparser from 'ua-parser-js'
import http from '../src/util/http.js'

let uaStats = new UAparser(window.navigator.userAgent).getResult()
let browser = uaStats.browser
  ? `${uaStats.browser.name || 'Unknown'} ${uaStats.browser.version || ''}`
  : ''
let os =
  uaStats.os && uaStats.os.name
    ? `${uaStats.os.name} ${uaStats.os.version || ''}`
    : ''

const errorHandler = (error, vm, lifecycle, type = 'client', info = null) => {
  let data = {
    userId: window.userId,
    orgId: window.orgId,
    route: window.location.pathname,
    message: error.message,
    stacktrace: error.stack,
    browser: browser,
    os: os,
    ceType: type,
    ceInfo: info || { vuelifecycle: lifecycle },
    ceUa: uaStats,
  }

  if (window.canLogError === undefined) {
    window.canLogError = window.location.hostname.includes('app.facilio.com')
  }

  if (window.canLogError) {
    http.post('/v2/errors/web', data).catch(() => {
      window.canLogError = false
      setTimeout(function cb() {
        window.canLogError = true
      }, 10 * 60 * 1000) //delays logging by 10mins if api call fails
    })

    let { $ga: { exception = null } = {} } = vm || {}
    if (exception) {
      exception(data.message)
    }
  }
  console.error('Error in ' + lifecycle + ': "' + error.toString() + '"')
}

export default errorHandler
