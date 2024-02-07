<template>
  <div>
    <!-- Kiosk checkout Thank you message -->
    <div class="fc-kiosk-form-con">
      <div class="fc-kiosk-form position-relative">
        <div class="kiosk-thankyou-con">
          <div class="">
            <facilio-thankyou></facilio-thankyou>
            <!-- <img src="~assets/svgs/kiosk-thankyou.svg" alt="" title=""> -->
          </div>
          <template v-if="messageType == 'checkOut'">
            <div class="kiosk-thankyou-H pT20">Thank you for visiting us !</div>
            <div class="kiosk-thankyou-desc pT10">
              We hope you had a hospitable experience <br />
              and we look forward to your next visit soon.
            </div>
          </template>

          <template v-if="messageType == 'checkIn'">
            <div class="kiosk-thankyou-H pT20">Checked-in successfully!</div>
            <div class="kiosk-thankyou-desc pT10">
              Please proceed.
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FacilioThankyou from 'src/devices/VisitorKiosk/FKioskThankyouSvg'
export default {
  components: {
    FacilioThankyou,
  },
  created() {
    this.messageType = this.$route.query.messageType //checkIn or checkOut
  },
  data() {
    return {
      timer: null,
      messageType: null,
    }
  },
  mounted() {
    //redirect to welcome page after shwoing thanks for 15 sec
    this.timer = window.setTimeout(() => {
      this.$router.replace({ name: 'welcome' })
    }, 5000)
  },
  beforeDestroy() {
    window.clearTimeout(this.timer)
  },
}
</script>
