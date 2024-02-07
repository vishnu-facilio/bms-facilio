export default {
  data() {
    return {
      onlineHandler: null,
      offlineHandler: null,
      OfflineOnly: false,
      OnlineOnly: false,
    }
  },
  mounted() {
    if (window) {
      if (navigator.onLine) {
        this.OnlineOnly = true
      } else {
        this.OfflineOnly = true
      }

      this.onlineHandler = () => {
        this.$emit('online')
        this.OnlineOnly = true
        this.OfflineOnly = false
      }

      this.offlineHandler = () => {
        this.$emit('offline')
        this.OfflineOnly = true
        this.OnlineOnly = false
      }

      window.addEventListener('online', this.onlineHandler)
      window.addEventListener('offline', this.offlineHandler)
    }
  },
  destroyed() {
    if (this.onlineHandler) {
      window.removeEventListener('online', this.onlineHandler)
    }
    if (this.offlineHandler) {
      window.removeEventListener('offline', this.offlineHandler)
    }
  },
}
