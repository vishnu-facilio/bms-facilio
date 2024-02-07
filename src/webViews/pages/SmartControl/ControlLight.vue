<template>
  <div class="control-light-container main">
    <p class="floor">
      {{ floor }}
    </p>
    <p class="building">
      {{ building }}
    </p>
    <div class="bulb-svg">
      <div class="bulb-svg-inner" :class="{ off: !mode }">
        <inline-svg
          src="control-mobile-ui/light"
          iconClass="fill-white"
          class="bulb-icon"
        ></inline-svg>
      </div>
      <p class="bulb-status">
        {{ mode ? 'ON' : 'OFF' }}
      </p>

      <div class="controlLigth-switch">
        <el-switch
          v-model="mode"
          active-color="#13ce66"
          inactive-color="#e5e5e5"
        >
        </el-switch>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      mode: true,
      floor: '1st Floor',
      building: 'Atlantis Tower 2',
    }
  },
}
</script>
<style scoped>
.main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-bottom: 18%;
}
.el-icon-back {
  font-size: 30px;
  color: #000;
}
.bulb-svg-inner {
  width: 100%;
  display: flex;
  justify-content: center;
}
.bulb-svg-inner.off {
  opacity: 0.5;
}
.light {
  width: 83%;
  display: flex;
  justify-content: center;
}

.header-Ltext {
  font-size: 20px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: 0.57px;
  margin-top: 13px;
}
.floor {
  font-size: 20px;
  text-align: center;
  font-weight: 600;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: 0.57px;
}
.building {
  text-align: center;
  font-size: 15px;
  font-weight: 400;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: 0.57px;
}
.bulb-svg {
  width: 100%;
  height: 370px;
  display: flex;
  justify-content: center;
  flex-direction: column;
}
.bulb-status {
  font-size: 25pxpx;
  font-weight: 600;
  text-align: center;
  margin-top: 15px;
}

.bulb-icon {
  height: 175px;
  width: 175px;
}
</style>
<style lang="scss">
.control-light-container {
  .controlLigth-switch {
    margin-top: 45px;
    display: flex;
    justify-content: center;
  }
  .controlLigth-switch .el-switch__core {
    width: 77px !important;
    height: 37px;
    border-radius: 20px;
  }

  .controlLigth-switch .el-switch.is-checked .el-switch__core::after {
    left: 100%;
    margin-left: -32px;
  }
  .controlLigth-switch .el-switch__core:after {
    width: 30px;
    height: 30px;
    top: 3px;
  }
}
</style>
