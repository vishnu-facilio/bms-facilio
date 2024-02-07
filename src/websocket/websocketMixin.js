import VueNativeSock from 'vue-native-websocket'
import Vue from 'vue'

export default {
  created() {},
  beforeDestroy() {
    this.clearIntervalPingPongInterval()
  },
  methods: {
    connectSocket(ws_endpoint) {
      Vue.use(VueNativeSock, ws_endpoint, {
        store: this.$store,
        format: 'json',
        reconnection: true, // (Boolean) whether to reconnect automatically (false)
        reconnectionDelay: 10000, // (Number) how long to initially wait before attempting a new (1000)
      })
      this.setUpPingPongInterval()
    },

    //to avoid server disconnecting idle socket ,periodically ping
    setUpPingPongInterval() {
      if (!this.wsPingPongInterval) {
        this.wsPingPongInterval = setInterval(() => {
          if (this.$socket) {
            let obj = { from: 0, to: 0, content: { ping: 'check' } }
            this.$socket.sendObj(obj)
          }
        }, 60000)
      }
    },

    clearIntervalPingPongInterval() {
      if (this.wsPingPongInterval) {
        clearInterval(this.wsPingPongInterval)
        this.wsPingPongInterval = null
      }
    },
  },
}
