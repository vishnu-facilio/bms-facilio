<template>
  <iframe
    v-if="url"
    :src="url"
    ref="microFrame"
    style="width: 100%; height: 100%; border: 0px"
  ></iframe>
</template>

<script>
export default {
  props: ['name', 'url', 'context', 'handlers'],
  mounted() {
    window.addEventListener('message', this.handleEvent)
  },
  destroyed() {
    window.removeEventListener('message', this.handleEvent)
  },
  methods: {
    _isValidEvent(event) {
      let data = event.data
      if (typeof data === 'string') {
        try {
          data = JSON.parse(event.data)
        } catch (e) {
          return false
        }
      }
      if (data.name != this.name) {
        return false
      }
      return this.$refs.microFrame.contentWindow === event.source
    },
    _namePrefix(key) {
      return this.name + '.' + key
    },
    _sendResponse(replyId, key, result, error) {
      let data = {
        id: replyId,
        key: key,
        result: result,
        error: error,
      }
      this.$refs.microFrame.contentWindow.postMessage(JSON.stringify(data), '*')
    },
    sendEvent(key, msg) {
      let data = {
        key: this._namePrefix(key),
        message: msg,
      }
      this.$refs.microFrame.contentWindow.postMessage(JSON.stringify(data), '*')
    },
    handleEvent(event) {
      if (!this._isValidEvent(event)) return

      let data = JSON.parse(event.data)

      const id = data.id
      const key = data.key
      const params = data.params

      let eventName = key

      if (eventName === 'handshake') {
        this.sendEvent('loaded', {
          context: this.context,
          account: this.$account,
        })
        this.$emit('loaded', params)
      } else if (
        this.handlers &&
        this.handlers[eventName] &&
        typeof this.handlers[eventName] === 'function'
      ) {
        Promise.resolve(this.handlers[eventName](params))
          .then(response => {
            this._sendResponse(id, key, response, null)
          })
          .catch(error => {
            let errorMsg = error ? error.toString() : null
            this._sendResponse(id, key, null, errorMsg)
          })
      } else {
        this.$emit(eventName, params)
      }
    },
  },
}
</script>
