<template>
  <div></div>
</template>
<script>
export default {
  props: ['urlAction'],
  mounted() {
    this.executeAction()
  },
  methods: {
    executeAction() {
      let { urlAction } = this
      let selfHost = window.location.protocol + '//' + window.location.host
      if (urlAction.url) {
        let url = this.applyLiveVariables(urlAction.url, true)
        if (urlAction.target === 'self') {
          if (url.startsWith(selfHost)) {
            if (
              this.$helpers.isLicenseEnabled('NEW_LAYOUT') &&
              url.startsWith(selfHost + '/app/em/newdashboard/')
            ) {
              url = url.replace(
                selfHost + '/app/em/newdashboard/',
                selfHost + '/app/home/dashboard/'
              )
            }
            let appUrl = url.replace(selfHost, '')
            this.$router.push({ path: appUrl })
          } else {
            window.location.href = url
          }
        } else if (urlAction.target === 'popup') {
          this.$popupView.openPopup({
            type: urlAction.linkType,
            url: urlAction.url,
            alt: '',
            dashboardId: '',
            reportId: '',
            target: urlAction.target,
          })
        } else {
          window.open(url, '_blank')
          this.$emit('showUrl', false)
        }
      }
    },
    applyLiveVariables(str, applyRawValue) {
      if (!str) {
        return str
      }
      let readingVariables = []
      let matched = str.match(/[^\\${}]+(?=\})/g)
      if (matched && matched.length) {
        readingVariables.push(...matched)
      }

      if (readingVariables && readingVariables.length) {
        for (let rv of readingVariables) {
          let liveVal = null
          let replaceVal = ''
          if (liveVal) {
            if (applyRawValue) {
              replaceVal = liveVal.value
            } else {
              replaceVal = liveVal.label
            }
          }
          if (!replaceVal) {
            replaceVal = ''
          }
          str = str.replace('${' + rv + '}', replaceVal)
        }
      }
      return str
    },
  },
}
</script>

<style></style>
