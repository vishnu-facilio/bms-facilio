export default {
  data() {
    return {
      handleReloadFunction: null,
      isFormSaved: false,
    }
  },

  methods: {
    handleFormReload() {
      let self = this
      return new Promise((resolve, reject) => {
        if (typeof self.isFormSaved !== 'undefined') {
          if (!self.isFormSaved) {
            self.$dialog
              .confirm({
                title: self.$t('common.dialog.unchanges'),
                message: self.$t('common.dialog.unchanges_body'),
                lbLabel: self.$t('common.dialog.stay_here'),
                rbLabel: self.$t('common.dialog.leave'),
              })
              .then(function(value) {
                resolve(value)
              })
          } else {
            resolve(true)
          }
        } else {
          resolve(true)
        }
      })
    },
  },

  beforeRouteLeave(to, from, next) {
    let self = this
    self.handleFormReload().then(function(result) {
      if (result) {
        next()
      } else {
        next(false)
      }
    })
  },

  destroyed() {
    window.removeEventListener('onbeforeunload', this.handleReloadFunction)
  },

  mounted() {},
}
