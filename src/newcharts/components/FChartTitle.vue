<template>
  <div class="f-new-chart-title">
    <div
      v-if="titeList && titeList && titeList.length && !loading"
      class="flex-middle justify-center"
    >
      <div
        v-for="(data, index) in titeList"
        :key="index"
        :style="getStyle(data)"
        class="f-chart-mandv-title"
      >
        {{ data.title }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['chartContext', 'config'],
  data() {
    return {
      titeList: [],
      loading: false,
    }
  },
  mounted() {
    let self = this
    this.loading = true
    setTimeout(function() {
      self.titeList = self.chartContext.regionsTitles
      self.loading = false
    }, 1000)
  },
  methods: {
    getStyle(data) {
      return 'width:' + data.diff + 'px'
    },
  },
}
</script>

<style>
.f-chart-mandv-title {
  font-size: 11px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.98px;
  color: #324056;
  padding-top: 20px;
  z-index: 10;
}
.f-new-chart-title {
  position: relative;
  top: 35px;
}
</style>
