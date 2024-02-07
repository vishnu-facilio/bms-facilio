export default {
  methods: {
    generateUniqueKey() {
      return (
        Date.now().toString(36) +
        Math.random()
          .toString(36)
          .substr(2, 5)
      ).toLowerCase()
    },

    subscribe(topicName, params, callback) {
      let uniqueKey = this.generateUniqueKey()

      params.topic = topicName
      params.uniqueKey = uniqueKey
      if (this.$socket) {
        this.$socket.sendObj({
          messageType: 'PUBSUB',
          action: 'subscribe',
          namespace: 'pubsub',
          content: params,
        })

        this.$store.dispatch('pubsub/subscribe', {
          key: uniqueKey,
          callback: callback,
        })
      }

      return uniqueKey
    },

    unsubscribe(uniqueKey, topicName, params) {
      params.topic = topicName
      params.uniqueKey = uniqueKey

      this.$socket.sendObj({
        messageType: 'PUBSUB',
        action: 'unsubscribe',
        namespace: 'pubsub',
        content: params,
      })

      this.$store.dispatch('pubsub/unsubscribe', {
        key: uniqueKey,
      })
    },
  },
}
