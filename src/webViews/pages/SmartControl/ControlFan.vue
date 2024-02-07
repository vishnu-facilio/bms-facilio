<template>
  <div class="control-light-container main">
    <div class="fan-svg">
      <div class="fan-rotation-svg" :class="fanSvgClass">
        <inline-svg
          src="control-mobile-ui/fan"
          iconClass="icon fill-white icon-200"
        ></inline-svg>
      </div>
    </div>
    <div class="slider">
      <el-slider
        @change="onSpeedChange"
        :disabled="!mode"
        class="width65"
        :marks="marks"
        :max="100"
        v-model="speed"
      >
      </el-slider>
    </div>
    <div class="control-switch">
      <el-switch
        v-model="mode"
        active-color="#409EFF"
        inactive-color="#e5e5e5"
        @change="resetSpeed"
      />
    </div>
  </div>
</template>
<script>
import ControlMixin from './ControlMixin'
import { isEmpty } from '@facilio/utils/validation'
export default {
  mixins: [ControlMixin],
  data() {
    return {
      speed: 0,
      marks: {
        0: '0',
        100: '100',
      },
      mode: false,
      isvisible: false,
      readingsMap: {},
    }
  },
  mounted() {
    let { assetId } = this || {}
    this.getReadingValues(assetId, 'FAN').then(data => {
      this.readingsMap = data
      let fanspeed = this.$getProperty(data, 'fanspeed1level.value')
      let switchposition = this.$getProperty(data, 'fanspeed1.value')
      this.speed = fanspeed
      this.mode = switchposition
    })
  },
  computed: {
    assetId() {
      return this.$getProperty(this, '$route.params.asset', -1)
    },
    roundOffSpeed() {
      let { speed } = this
      return speed + 25 / 2 - ((speed + 25 / 2) % 25)
    },
    fanSvgClass() {
      let { roundOffSpeed, mode } = this || {}
      if (mode) {
        let actualSpeed = roundOffSpeed / 25

        return `rotating-speed-${actualSpeed}`
      } else {
        return ''
      }
    },
  },
  methods: {
    onSpeedChange(currValue) {
      let fieldId = this.$getProperty(
        this,
        'readingsMap.fanspeed1level.fieldId'
      )
      let value = this.$getProperty(this, 'readingsMap.fanspeed1level.value')
      value = value + 25 / 2 - ((value + 25 / 2) % 25)
      if (value !== currValue) this.setReading(fieldId, this.speed)
    },
    resetSpeed(currValue) {
      let fieldId = this.$getProperty(this, 'readingsMap.fanspeed1.fieldId')
      let value = this.$getProperty(this, 'readingsMap.fanspeed1level.value')
      if (!isEmpty(value) && value !== currValue)
        this.setReading(fieldId, `${this.mode}`)
    },
    async setReading(fieldId, value) {
      let { assetId } = this || {}

      if (!isEmpty(assetId) && !isEmpty(fieldId) && !isEmpty(value))
        this.setReadingValue(parseInt(assetId), fieldId, value)
    },
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

.slider {
  display: flex;
  justify-content: center;
}
.fan-svg {
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.fan-rotation-svg {
  display: block;
  position: fixed;
  width: 200px;
  height: 200px;
}

.rotating-speed-1 {
  animation: rotation 4s infinite linear;
}
.rotating-speed-2 {
  animation: rotation 3s infinite linear;
}
.rotating-speed-3 {
  animation: rotation 2s infinite linear;
}
.rotating-speed-4 {
  animation: rotation 1s infinite linear;
}
@keyframes rotation {
  0% {
    -webkit-transform: rotate(0deg);
    transform: rotate(0deg);
    transform-box: fill-box;
    -webkit-transform-origin: 50% 50%;
    transform-origin: 50% 50%;
  }
  100% {
    -webkit-transform: rotate(-1080deg);
    transform: rotate(-1080deg);
    transform-box: fill-box;
    -webkit-transform-origin: 50% 50%;
    transform-origin: 50% 50%;
  }
}
</style>

<style lang="scss">
.control-light-container {
  .control-switch {
    margin-top: 45px;
    display: flex;
    justify-content: center;
  }
  .control-switch .el-switch__core {
    width: 77px !important;
    height: 37px;
    border-radius: 20px;
  }
  .el-slider__marks-text {
    color: #fff;
  }
  .control-switch .el-switch.is-checked .el-switch__core::after {
    left: 100%;
    margin-left: -32px;
  }
  .control-switch .el-switch__core:after {
    width: 30px;
    height: 30px;
    top: 3px;
  }
}
.rotating1 {
  #S1NZ3tnYwf * {
    animation: rotation 0.5s infinite linear;
  }
}
.rotating2 {
  #S1NZ3tnYwf * {
    animation: rotation 0.25s infinite linear;
  }
}
.rotating3 {
  #S1NZ3tnYwf * {
    animation: rotation 0.1s infinite linear;
  }
}
#S1NZ3tnYwf * {
  -webkit-animation-iteration-count: infinite;
  animation-iteration-count: infinite;
  -webkit-animation-timing-function: cubic-bezier(0, 0, 1, 1);
  animation-timing-function: cubic-bezier(0, 0, 1, 1);
}
</style>
