import http from './http'

export const constructBaseURL = (appLinkName = null) => {
  // BaseURL points to given base url (env) or default to domain on which client is running
  // Env variables depend on the environment where app is running, .env.dev for development, .env.prod for production
  // BaseURL will be further modified by webtabs.js by calling this fn again to change
  // the default APIEndpoint to `<appLinkName>/api` once app details are fetched
  let { VUE_APP_FACILIO_PORT, VUE_APP_FACILIO_CONTEXT } = process.env
  let {
    location: { protocol, hostname, host },
  } = window
  let defaultAPIEndpoint = appLinkName ? `${appLinkName}/api` : 'api'

  let baseURL = `${protocol}//${host}/${defaultAPIEndpoint}`

  if (process.env.VUE_APP_FACILIO_BASE_URL) {
    baseURL = `${process.env.VUE_APP_FACILIO_BASE_URL}/${defaultAPIEndpoint}`
  } else if (VUE_APP_FACILIO_CONTEXT && VUE_APP_FACILIO_PORT) {
    baseURL = `${protocol}//${hostname}:${VUE_APP_FACILIO_PORT}/${VUE_APP_FACILIO_CONTEXT}/${defaultAPIEndpoint}`
  }

  return baseURL
}

export const setBaseURL = baseURL => (http.defaults.baseURL = baseURL)

export const getBaseURL = () => http.defaults.baseURL
