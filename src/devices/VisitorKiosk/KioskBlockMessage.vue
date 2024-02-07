<template>
  <div>
    <div class="kiosk-print-header">
      <div class="fc-kiosk-f18 text-center">
        {{ currentAccount.org.name }}
      </div>
    </div>
    <div class="fc-kiosk-form-con">
      <div class="fc-kiosk-form position-relative">
        <div class="">
          <el-alert
            :show-icon="false"
            v-if="messageType == 'blocked'"
            class="kiosk-block-alert"
            type="warning"
            description="Looks like your entry is restricted! Please contact your host or administrator for access."
          >
          </el-alert>
          <el-alert
            :show-icon="false"
            v-if="messageType == 'approval'"
            class="kiosk-block-alert"
            type="info"
            description="You host has been notified of your visit! Please wait for approval from them "
          >
          </el-alert>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
export default {
  created() {
    this.messageType = this.$route.query.messageType
  },
  mounted() {
    //redirect to welcome page after shwoing thanks for 15 sec
    this.timer = window.setTimeout(() => {
      this.$router.replace({ name: 'welcome' })
    }, 45000)
  },
  data() {
    return {
      messageType: null,
    }
  },
  computed: {
    ...mapGetters(['currentAccount']),
  },
  beforeDestroy() {
    window.clearTimeout(this.timer)
  },
}
</script>
<style lang="scss">
.kiosk-block-alert .el-alert__description {
  font-size: 20px;
  padding: 50px;
  font-weight: 500;
}
.kiosk-block-alert .el-alert__closebtn {
  display: none;
}
</style>
