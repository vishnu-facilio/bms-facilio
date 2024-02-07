<template>
  <el-radio-group
    v-model="isListView"
    class="d-flex visual-icons"
    size="mini"
    fill="#007adb"
    @change="onChangeVisualType"
  >
    <el-radio-button label="list">
      <fc-icon
        group="action"
        name="option"
        size="12"
        :color="[isListView === 'list' && '#007adb']"
      ></fc-icon>
    </el-radio-button>
    <el-radio-button label="calendar">
      <fc-icon
        group="dsm"
        name="calendar"
        size="12"
        :color="[isListView === 'calendar' && '#007adb']"
      ></fc-icon>
    </el-radio-button>
  </el-radio-group>
</template>
<script>
export default {
  data() {
    return {
      isListView: '',
    }
  },
  created() {
    let { startTime } = this.$route?.query || {}
    this.isListView = startTime ? 'calendar' : 'list'
  },
  watch: {
    isListView: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.$emit('onSwitchVisualize', newVal === 'list')
        }
      },
      immediate: true,
    },
  },
  methods: {
    onChangeVisualType(val) {
      if (val === 'list') {
        let { query } = this.$route || {}
        let { search, includeParentFilter } = query || {}

        this.$router
          .replace({ query: { search, includeParentFilter } })
          .catch(() => {})
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.visual-list-icon {
  color: #007adb;
}
</style>
<style lang="scss">
.visual-icons {
  .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    background-color: transparent !important;
  }
  .el-radio-button__inner:hover {
    color: #007adb;
  }
}
</style>
