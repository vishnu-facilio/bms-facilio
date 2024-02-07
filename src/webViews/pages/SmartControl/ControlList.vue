<template>
  <div :class="`height100vh ${currentControl}-container`">
    <router-view class="height91"></router-view>

    <div class="navigation-container">
      <router-link :to="'/webview/sc/home'" class="home">
        <div class="click-icon">
          <inline-svg
            src="control-mobile-ui/home"
            iconClass="icon icon-xlg fill-grey p3"
          ></inline-svg>
        </div>
      </router-link>
      <router-link
        :to="`/webview/sc/${$route.params.asset}/lighting`"
        class="light"
        :class="currentControl === 'lighting' && 'selected-tab'"
      >
        <div class="click-icon">
          <inline-svg
            src="control-mobile-ui/light-icon"
            iconClass="icon icon-xlg fill-grey p3"
          ></inline-svg>
        </div>
      </router-link>

      <router-link
        :to="`/webview/sc/${$route.params.asset}/temperature`"
        class="temper"
        :class="currentControl === 'temperature' && 'selected-tab'"
      >
        <div class="click-icon">
          <inline-svg
            src="control-mobile-ui/temperature-icon"
            iconClass="icon icon-xlg fill-grey p3"
          ></inline-svg>
        </div>
      </router-link>
      <router-link
        :to="`/webview/sc/${$route.params.asset}/fan`"
        class="fan"
        :class="currentControl === 'fan' && 'selected-tab'"
      >
        <div class="click-icon">
          <inline-svg
            src="control-mobile-ui/fan-icon"
            iconClass="icon icon-xlg fill-grey p3"
          ></inline-svg>
        </div>
      </router-link>
    </div>
  </div>
</template>
<script>
export default {
  computed: {
    currentControl() {
      return this.$route.name
    },
  },
}
</script>

<style lang="scss" scoped>
.height91 {
  height: 91%;
}
.fan-container {
  background-color: #61c584;
}
.lighting-container {
  background-color: #fcca20;
}
.temperature-container {
  background-color: #1ea9d7;
}
.navigation-container {
  height: 9%;
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: center;
  border-top-right-radius: 20px;
  border-top-left-radius: 20px;
  overflow: hidden;
  background: #fff;
}
.hello {
  align-items: center;
  justify-content: center;
}

.temper,
.light,
.fan,
.home {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  width: 25%;
  color: black;
  border-block-start: 5px solid #fff;
}
.house-icon {
  font-size: 33px;
  color: #324056;
}
.click-icon {
  display: flex;
  flex-flow: column;
  align-items: center;
}
.label {
  font-size: 13px;
  color: black;
}
.selected-tab {
  border-color: #594de1;
  background-image: linear-gradient(to bottom, #efedfc, #fff);
}
.comp {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
