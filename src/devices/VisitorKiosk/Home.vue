<template>
  <div class="device-home">
    <!-- just for testing ui-->
    <!-- <component :is="screenName"> </component> -->
    <router-view></router-view>
  </div>
</template>

<script>
import 'src/devices/styles/kiosk.scss'
import 'src/devices/styles/kiosk-helpers.scss'
// import welcomescreen from 'src/devices/VisitorKiosk/WelcomeScreen'

export default {
  data() {
    return {}
  },
  components: {},
}
</script>

<style></style>
