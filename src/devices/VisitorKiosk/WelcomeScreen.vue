<template>
  <div>
    <div class="fc-kiosk-welScreen">
      <div class="fc-kiosk-welScreen-con">
        <div>
          <div class="fc-kiosk-welScreen-f42 fc-animated slideInDown">
            Welcome to
          </div>
          <div class="fc-kiosk-welScreen-f46 fc-animated slideInDown">
            {{ currentAccount.org.name }}
          </div>
          <div class="pT60">
            <div class="fc-kiosk-welScreen-f22 fc-animated slideInLeft">
              I would like to
            </div>
            <div class="fc-kiosk-in-out-con">
              <div
                class="fc-kiosk-in-out-bg fc-animated slideInUp"
                @click="$router.push({ name: 'checkin' })"
              >
                <img
                  src="~assets/svgs/check-in.svg"
                  alt=""
                  title=""
                  width="50"
                  height="50"
                />
                <!-- <InlineSvg src="~svgs/login" iconClass="icon text-center icon-xxlg"></InlineSvg> -->
                <div class="fc-check-in-f22 text-center font-bold pT30">
                  CHECK-IN
                </div>
              </div>
              <div
                class="fc-kiosk-in-out-bg mL40 fc-animated slideInUp"
                @click="$router.push({ name: 'checkout' })"
              >
                <img
                  src="~assets/svgs/check-out.svg"
                  alt=""
                  title=""
                  width="50"
                  height="50"
                />
                <!-- <InlineSvg src="~svgs/login" iconClass="icon text-center icon-xxlg"></InlineSvg> -->
                <div class="fc-check-in-f22-red pT30 text-center font-bold">
                  CHECK-OUT
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapGetters } from 'vuex'
export default {
  props: ['welcomeScreen'],
  computed: {
    ...mapGetters(['currentAccount']),
  },
}
</script>
<style lang="scss">
.fc-kiosk-welScreen {
  .fc-kiosk-welScreen-con {
    width: 100%;
    height: 100vh;
    background: #46377d;
    position: relative;
    display: grid;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    // flex-wrap: nowrap;
    // flex-direction: column;
  }
}
</style>
