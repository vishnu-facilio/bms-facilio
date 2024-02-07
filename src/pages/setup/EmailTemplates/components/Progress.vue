<template>
  <div>
    <el-progress
      :text-inside="true"
      :stroke-width="4"
      :percentage="percent"
      :show-text="false"
      class="image-upload-progressbar"
    ></el-progress>
  </div>
</template>
<script>
export default {
  data() {
    return {
      percent: 0,
      step: 3,
    }
  },
  watch: {
    percentage: {
      deep: true,
      handler: function(newVal) {
        this.percent = this.percentage
      },
    },
  },
  props: {
    percentage: {
      type: Number,
      default: () => {
        return 0
      },
    },
    progressTime: {
      type: Number,
      default: () => {
        return 100
      },
    },
    progressStep: {
      type: Number,
      default: () => {
        return 3
      },
    },
  },
  computed: {
    estimatedTime() {
      return 0
    },
  },
  mounted() {
    this.percent = this.percentage
    this.step = this.progressStep
    this.loadProgress()
  },
  methods: {
    loadProgress() {
      let count = setInterval(() => {
        if (this.percent > 100) {
          clearInterval(count)
          this.percent = 100
        }
        if (this.percent < 100) {
          if (this.percent > 50 && this.percent < 70) {
            this.step = this.step + 2
          } else if (this.percent > 70 && this.percent < 90) {
            this.step = this.step + 3
          } else if (this.percent > 90) {
            this.step = this.step + 4
          }
          this.percent += this.step
        }
      }, this.progressTime)
    },
  },
}
</script>
<style lang="scss">
.image-upload-progressbar {
  margin-top: 10px;
  .el-progress-bar__outer {
    border-radius: 0;
  }
  .el-progress-bar__inner {
    background-color: #3ab2c1;
    border-radius: 0;
  }
}
</style>
