<template>
  <div class="f-kiosk-checkout-page">
    <!-- kiosk checkout details page -->
    <mobile-or-qr @authDone="handleAuth" action="checkout"> </mobile-or-qr>
  </div>
</template>

<script>
import mobileOrQr from 'src/devices/VisitorKiosk/MobileOrQR'
import { errorRedirect } from './visitorHelpers'

export default {
  components: {
    mobileOrQr,
  },
  methods: {
    handleAuth(authData) {
      if (authData.type == 'qr') {
        let splitQr = authData.qr.split('_')
        // let visitorLogId=splitQr[1]
        let passCode = splitQr[1]
        this.checkoutWithPassCode(passCode)
          .then(() => {
            this.$router.replace({
              name: 'visitorthankyou',
              query: { messageType: 'checkOut' },
            })
          })
          .catch(() => {
            this.$message({
              message: 'Eror occured during checkout ,please contact admin',
              type: 'error',
            })
            errorRedirect()
          })
      } else if (authData.type == 'mobile') {
        this.checkoutWithPhone(authData.mobile).then(() => {
          this.$router.replace({
            name: 'visitorthankyou',
            query: { messageType: 'checkOut' },
          })
        })
      }
    },
    async checkoutWithPhone(phone) {
      await this.$http.post('/v2/visitorLogging/checkOutWithPhone', {
        contactNumber: phone,
      })
      return
    },
    async checkoutWithPassCode(passCode) {
      let visitorLogResp = await this.$http.post(
        '/v2/visitorLogging/getVisitorLogforPassCode',
        { passCode }
      )
      if (
        visitorLogResp.data.result.visitorlogging &&
        visitorLogResp.data.result.visitorlogging.visitor &&
        visitorLogResp.data.result.visitorlogging.visitor.phone
      ) {
        await this.$http.post('/v2/visitorLogging/checkOutWithPhone', {
          contactNumber:
            visitorLogResp.data.result.visitorlogging.visitor.phone,
        })
      } else {
        let visitorLogId = visitorLogResp.data.result.visitorlogging.id
        await this.$http.post('/v2/visitorLogging/checkInFromKiosk', {
          recordId: visitorLogId,
        })
      }
      return
    },
  },
}
</script>
