export const emitEvent = (eventName, params = null) => {
  let { JSReceiver, webkit } = window
  let { messageHandlers = {} } = webkit || {}

  let paramString = params ? JSON.stringify(params) : null

  if (JSReceiver) {
    JSReceiver.emitEvent(eventName, paramString)
  } else if (messageHandlers.emitEvent) {
    messageHandlers.emitEvent.postMessage({ eventName, params: paramString })
  }
  console.warn(`Event ${eventName} called with params ${paramString}`)
}

export const sendData = data => {
  let { JSReceiver, webkit } = window
  let { messageHandlers = {} } = webkit || {}

  if (JSReceiver) {
    JSReceiver.sendData(JSON.stringify(data))
  } else if (messageHandlers.sendData) {
    messageHandlers.sendData.postMessage({ data: JSON.stringify(data) })
  }
}

export const registerActions = (actionName, handler = () => {}) => {
  window.webViewActions = {
    ...(window.webViewActions || {}),
    [actionName]: handler,
  }
}
