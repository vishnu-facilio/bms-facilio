import wsclient from './ws-client'

export default class WMSClient {
  constructor(options) {
    this._endpoint = options.endpoint
    this._subscribedTopics = {}
    this._log = options.log || false
    this._ping = options.ping || true
    this._pingInterval = options.pingInterval || 60000
    this._pingIntervalInstance = null
    this._connectionActive = false

    this._wsclient = wsclient(this._endpoint, {
      timeout: 10000,
      onopen: () => {
        this.log('connection established.', true)
        this._connectionActive = true
        this._resubscribe()
      },
      onclose: () => {
        this._connectionActive = false
        this.log('connection closed.', true)
      },
      onerror: () => {
        this._connectionActive = false
        this.log('failed to connect.', true)
      },
      onreconnect: () => {
        this.log('trying to reconnect.', true)
      },
      onmessage: event => {
        this.log('message received: ' + event.data)
        this._connectionActive = true
        this._handleMessage(event.data)
      },
    })

    if (this._ping && this._pingIntervalInstance === null) {
      let intervalTime = this._pingInterval < 5000 ? 5000 : this._pingInterval
      this._pingIntervalInstance = setInterval(() => {
        if (!this._connectionActive) {
          this.log('connection not available, hence ping skiped')
          return
        }

        this._send({ topic: '__ping__' })
      }, intervalTime)
    }
  }

  close() {
    this._wsclient.close()
    if (this._pingIntervalInstance) {
      clearInterval(this._pingIntervalInstance)
      this._pingIntervalInstance = null
    }
  }

  _handleMessage(message) {
    let jsonMessage = JSON.parse(message)
    if (jsonMessage.topic) {
      if (this._subscribedTopics[jsonMessage.topic]) {
        for (let subscriber of this._subscribedTopics[jsonMessage.topic]) {
          try {
            if (typeof subscriber === 'function') {
              subscriber(jsonMessage.content)
            }
          } catch (err) {
            console.log(err)
          }
        }
      }
    }
  }

  _resubscribe() {
    if (this._subscribedTopics) {
      for (let topic in this._subscribedTopics) {
        this._subscribeTopic(topic)
      }
    }
  }

  _send(message) {
    if (!this._connectionActive) {
      this.log(
        'connection not available, hence send message failed: ' + message,
        true
      )
      return false
    }
    this._wsclient.json(message)
    return true
  }

  _subscribeTopic(topic) {
    this._send({
      topic: '__subscribe__',
      content: { topic: topic },
    })
  }

  _unsubscribeTopic(topic) {
    this._send({
      topic: '__unsubscribe__',
      content: { topic: topic },
    })
  }

  log(message, force) {
    if (this._log || force) {
      console.log('[' + this._endpoint + '] ' + message)
    }
  }

  subscribe(topic, handler) {
    if (!topic || !handler) {
      this.log('topic or handler missing')
      return false
    }

    if (!this._subscribedTopics[topic]) {
      this._subscribedTopics[topic] = []
      this._subscribeTopic(topic)
    }

    this._subscribedTopics[topic].push(handler)
    this.log('subscribed: ' + topic)
    return true
  }

  unsubscribe(topic, handler) {
    if (!topic || !handler) {
      return false
    }

    if (this._subscribedTopics[topic]) {
      let subscribers = this._subscribedTopics[topic]
      if (subscribers.indexOf(handler) >= 0) {
        let idx = subscribers.indexOf(handler)
        subscribers.splice(idx, 1)

        this.log('unsubscribed topic handler: ' + topic)

        if (subscribers.length === 0) {
          this.unsubscribeTopic(topic)
        }
        return true
      }
    }
    return false
  }

  unsubscribeTopic(topic) {
    if (!topic) {
      return false
    }

    if (this._subscribedTopics[topic]) {
      this._unsubscribeTopic(topic)

      delete this._subscribedTopics[topic]
      this.log('unsubscribed topic: ' + topic)
      return true
    }
    return false
  }

  unsubscribeAll() {
    for (let topic in this._subscribedTopics) {
      this.unsubscribeTopic(topic)
    }
    this.log('unsubscribed all topics')
    return true
  }
}
