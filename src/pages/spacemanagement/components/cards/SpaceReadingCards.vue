<template>
  <div
    class="space-readingcard h100"
    v-if="
      data &&
        data.label &&
        data.label !== 'Planned Maintenance' &&
        data.label !== 'ENERGY CONSUMED'
    "
  >
    <div class="space-readingcard-section">
      <div>
        <img
          v-if="data && (data.name === 'work_orders' || data.name === 'pm')"
          class="svg-icon space-readingcard-img"
          src="~assets/space/monitor-white.svg"
        />
        <img
          v-if="data && data.name === 'fire_alarms'"
          class="svg-icon space-readingcard-img"
          src="~assets/space/alarm.svg"
        />
        <img
          v-if="data && (data.name === 'assets' || data.name === 'spaces')"
          class="svg-icon space-readingcard-img"
          src="~assets/space/box-outline.svg"
        />
      </div>
      <div class="space-readingcard-data" @click="emitRedirect()">
        <span class="space-readingcard-value" v-if="data && data.data">{{
          data.data
        }}</span>
        <span class="space-readingcard-value" v-else>0</span>
        <span
          class="space-readingcard-unit"
          v-html="data.unit"
          v-if="data.unit && data.data"
        ></span>
      </div>
      <div class="space-readingcard-header">
        {{ (data && data.label) || '' }}
      </div>
    </div>
  </div>
  <div
    class="space-readingcard h100 energy-reading-card"
    v-else-if="data && data.label !== 'Planned Maintenance' && data.data"
  >
    <div class="space-readingcard-section">
      <div class="space-readingcard-header">
        {{ (data && data.label) || '' }}
      </div>
      <div class="space-readingcard-data" @click="emitRedirect()">
        <span class="space-readingcard-value" v-if="data.data">{{
          data.data
        }}</span>
        <span
          class="space-readingcard-unit"
          v-html="data.unit"
          v-if="data.unit && data.data"
        ></span>
      </div>
      <div class="space-readingcard-time">
        {{ $t('common.reports.rangeCategory.THIS_MONTH') }}
      </div>
    </div>
  </div>
  <div
    v-else-if="data && data.label && data.label !== 'Planned Maintenance'"
    class="space-readingcard-empty"
  >
    <div class="space-readingcard-header space-readingcard-header-empty">
      {{ $t('space.sites.energy_consumed') }}
    </div>
    <div class="space-readingcard--emptyvalue-1 " v-if="mainMeter">
      {{ '---' }}
    </div>
    <div class="space-readingcard--emptyvalue " v-else>
      {{ $t('space.sites.no_energy_meter') }}
    </div>
  </div>
</template>
<script>
export default {
  props: ['tempData', 'mainMeter'],
  watch: {
    tempData: function(newVal, oldVal) {
      this.data = newVal
    },
  },
  data() {
    return {
      data: null,
    }
  },
  mounted() {
    if (this.tempData) {
      this.data = this.tempData
    }
  },
  methods: {
    emitRedirect() {
      this.$emit('ValueClick', this.data.name)
    },
  },
}
</script>

<style>
.space-readingcard-header-empty {
  padding: 40px;
  padding-bottom: 0;
}
.space-readingcard-header {
  font-size: 12px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.2px;
  text-align: center;
  color: #999999;
  text-transform: uppercase;
}
.space-readingcard-value {
  font-size: 40px;
  font-weight: 300;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #000000;
}
.space-readingcard--emptyvalue-1 {
  font-weight: 300;
  font-style: normal;
  font-stretch: normal;
  color: #000000;
  letter-spacing: 0.6px;
  width: 100%;
  text-transform: capitalize;
  white-space: normal;
  padding: 45px;
  font-size: 35px;
  text-align: center;
  line-height: 31px;
  height: 100%;
  padding-top: 15px;
}
.space-readingcard--emptyvalue {
  font-weight: 300;
  font-style: normal;
  font-stretch: normal;
  color: #000000;
  letter-spacing: 0.6px;
  width: 100%;
  text-transform: capitalize;
  white-space: normal;
  padding: 45px;
  font-size: 17px;
  text-align: center;
  line-height: 31px;
  height: 100%;
  padding-top: 15px;
}
.space-readingcard-unit {
  font-size: 1.25vw;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.1px;
  color: #50516c;
}
.space-readingcard-time {
  font-size: 11px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.2px;
  text-align: center;
  color: #b9b9b9;
  text-transform: uppercase;
}
.space-readingcard-edit {
  position: absolute;
  left: 10px;
  top: 18px;
  font-size: 18px;
  z-index: 8;
  color: #868686;
  display: none;
  cursor: pointer;
}
.dragabale-card.space-readingcard:hover .space-readingcard-edit {
  display: block !important;
}
.space-readingcard .el-color-picker {
  position: absolute;
  left: 10px;
  top: 15px;
  display: none;
}
.dragabale-card.space-readingcard:hover .el-color-picker {
  display: block !important;
}
.card-theame .space-readingcard-header,
.card-theame .space-readingcard-unit,
.card-theame .space-readingcard-value {
  color: #fff;
}
.space-readingcard .el-switch {
  position: absolute;
  left: 10px;
  bottom: 10px;
}
.card-theame .space-readingcard-time {
  font-size: 12px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 1.3px;
  text-align: center;
  color: #ffffff;
}
.space-readingcard {
  height: 100%;
  padding: 40px 0;
}
.reading-card-color-picker {
  position: absolute;
  top: 16px;
  right: 33px;
  color: #000;
  opacity: 0.6;
  font-weight: 500;
  font-size: 18px;
  cursor: pointer;
}
.color-picker-conatiner {
  display: inline-grid;
}
.color-box {
  height: 20px;
  width: 20px;
  margin: 2px;
  border: 1px solid #f4f4f4;
  cursor: pointer;
}
.color-picker-section {
}
.reading-color-picker {
  padding: 20px;
}
.c-picker-label {
  font-size: 11px;
  font-weight: 400;
  text-transform: uppercase;
  padding: 10px;
  padding-left: 2px;
  letter-spacing: 1px;
}
.color-choose-icon {
  display: none;
}
.dragabale-card.space-readingcard:hover .color-choose-icon {
  display: block !important;
}
.editmode .dragabale-card.space-readingcard:hover .space-readingcard-header {
  max-width: 65%;
  margin: auto;
  overflow: hidden;
  white-space: nowrap;
}
.space-readingcard-data {
  max-width: 90%;
  margin: auto;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: center;
  padding: 5px 0 5px 0;
  cursor: pointer;
}

@media only screen and (min-width: 1800px) {
  .space-readingcard-section {
    padding-top: 25px;
  }
}
@media only screen and (min-width: 2000px) {
  .space-readingcard-section {
    padding-top: 40px;
  }
}
.space-readingcard-img {
  width: 24px;
}
.energy-reading-card .space-readingcard-data {
  padding: 13.5px 0 6px 0;
}
.energy-reading-card {
  padding: 43px 0 40px 0;
}
.space-readingcard,
.space-readingcard-empty {
  max-height: 180px;
  overflow: hidden;
}
.container.db-container.fc-position-relative.fc-border-radius.p0.text-center.fc-border-1 {
  height: 100%;
}
</style>
