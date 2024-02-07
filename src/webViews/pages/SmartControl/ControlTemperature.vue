<template>
  <div class="main">
    <div class="flex-middle pL10 pT10"></div>
    <div class="room-values">
      <div class="cloud">
        <inline-svg
          src="control-mobile-ui/cloud"
          iconClass="icon fill-white icon-xlg"
        ></inline-svg>
        <p class="f20 pT8">{{ roomtemp }}°</p>
      </div>
      <div class="cloud">
        <inline-svg
          src="control-mobile-ui/humiditydrop"
          iconClass="icon fill-white icon-xxxl"
        ></inline-svg>
        <p class="f20 pT8">{{ humidity }}%</p>
      </div>
    </div>

    <div class="set-temperature-container">
      <div class="d-flex justify-content-center mT40 pB3">
        <el-progress
          type="dashboard"
          :percentage="temperature"
          :stroke-width="15"
          :width="250"
          :color="colors"
          :format="format"
        ></el-progress>
      </div>
      <div class="inc-dec-button">
        <el-button
          circle
          class="el-icon-plus inc-dec-icon"
          @click="increase"
        ></el-button>
        <el-button
          circle
          class="el-icon-minus inc-dec-icon"
          @click="decrease"
        ></el-button>
      </div>
      <div class="set-temperature" @click="setReading">
        <p class="mT13 letter-spacing1">
          {{ $t('controlmobileui.control-temperature.set-temperature') }}
        </p>
      </div>
    </div>
  </div>
</template>
<script>
import ControlMixin from './ControlMixin'
export default {
  mixins: [ControlMixin],
  data() {
    return {
      temperature: 0,
      humidity: 0,
      roomtemp: 0,
      readingsMap: null,
      colors: [
        { color: '#f56c6c', percentage: 100 },
        { color: '#e6a23c', percentage: 80 },
        { color: '#5cb87a', percentage: 60 },
        { color: '#1989fa', percentage: 40 },
        { color: '#2553E6', percentage: 20 },
      ],
      maxTemp: 85,
      minTemp: 65,
      actualTemperature: 0,
      range: 10,
    }
  },
  mounted() {
    let { assetId } = this || {}
    this.getReadingValues(assetId, 'TEMPERATURE').then(data => {
      this.readingsMap = data
      let temperature = this.$getProperty(data, 'actualcoolingsetpoint.value')
      let spacehumidity = this.$getProperty(data, 'spacehumidity.value')
      let spacetemp = this.$getProperty(data, 'spacetemp.value')
      this.actualTemperature = this.roundOff(temperature) || 0
      this.humidity = this.roundOff(spacehumidity)
      this.roomtemp = this.roundOff(spacetemp)
      this.setMinMaxRange(temperature)
    })
  },
  computed: {
    assetId() {
      return this.$getProperty(this, '$route.params.asset', -1)
    },
  },
  methods: {
    roundOff(num) {
      return Math.round(num * 100) / 100
    },
    increase() {
      let temperature = this.temperature + 10
      if (temperature <= 100) {
        this.temperature = temperature
      }
    },
    decrease() {
      let temperature = this.temperature - 10
      if (temperature >= 0) {
        this.temperature = temperature
      }
    },
    setReading() {
      let { assetId } = this || {}

      let fieldId = this.$getProperty(
        this,
        'readingsMap.actualcoolingsetpoint.fieldId'
      )
      this.setReadingValue(parseInt(assetId), fieldId, this.temperature)
    },
    format(percentage) {
      let { range, minTemp } = this
      let currentStep = percentage / 10
      let modifiedTemp = currentStep * range + minTemp
      modifiedTemp = this.roundOff(modifiedTemp)
      return `${modifiedTemp} °F`
    },
    setMinMaxRange(temp) {
      if (temp > this.maxTemp) {
        this.maxTemp = temp
        this.temperature = 100
      } else if (temp < this.minTemp && temp > 0) {
        this.minTemp = temp
        this.temperature = 0
      }
      let range = Math.abs(this.maxTemp - this.minTemp) / 10
      this.range = range
    },
  },
}
</script>
<style lang="scss">
.el-progress__text,
.el-progress__text {
  color: #fff;
}
</style>
<style scoped>
.icon-div {
  width: 10%;
}
.room-values {
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  padding-left: 30px;
  padding-right: 30px;
  padding-top: 20px;
}
.cloud {
  display: flex;
  align-items: center;
  column-gap: 10px;
  color: white;
}
.set-temperature-container {
  height: 75%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.inc-dec-button {
  width: 40%;
  margin-left: 30%;
  display: flex;
  justify-content: space-between;
}
.el-icon-cloudy {
  color: white;
}
.set-temperature {
  width: 60%;
  height: 50px;
  font-size: 18px;
  font-weight: 500;
  background-color: #2553e6;
  margin-left: 20%;
  margin-top: 60px;
  color: white;
  border-radius: 10px;
  display: flex;
  justify-content: center;
}
.inc-dec-icon {
  font-weight: bold;
  color: #000;
  font-size: 20px;
}
</style>
