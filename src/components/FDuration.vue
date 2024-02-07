<template>
  <div>
    <div :class="labelClass" class="fc-input-label-txt pB0">
      {{ label ? label : this.$t('maintenance.pm_list.duration') }}
    </div>
    <el-row :gutter="10" class="mT10">
      <slot name="prefix"></slot>
      <el-col :span="size == 'l' ? 10 : 7">
        <el-input
          type="number"
          v-model="duration.day"
          min="0"
          class="duration-days-input fc-input-full-border2"
        ></el-input>
        <span class="textcolor pL10">{{
          $tc('maintenance.pm_list.day_s')
        }}</span>
      </el-col>
      <el-col :span="11">
        <el-select
          v-model="duration.hour"
          style="width: 60px;"
          class="fc-input-full-border2"
        >
          <el-option
            v-for="hour in getNumberRange(23)"
            v-if="hour.value !== 0 || !minHour || duration.day > 0"
            :key="hour.value"
            :label="hour.label"
            :value="hour.value"
          ></el-option>
        </el-select>
        <span class="textcolor hours-txt-align">{{
          $t('maintenance.pm_list.hour_s')
        }}</span>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  props: ['value', 'label', 'labelClass', 'format', 'size', 'minHour'],
  data() {
    return {
      duration: {
        day: 0,
        hour: 0,
      },
    }
  },
  watch: {
    duration: {
      handler(newVal, oldVal) {
        this.onValueChange()
      },
      deep: true,
    },
  },
  mounted() {
    let unwatch = this.$watch(
      'value',
      function(value) {
        let duration = value
        if (duration && duration !== -1) {
          let factor = 1
          if (this.format === 'h') {
            factor = 1
          } else {
            factor = 3600 // In seconds
          }
          let days = Math.floor(duration / (24 * factor))
          duration -= days * 24 * factor
          let hours = Math.floor(duration / factor)
          this.duration.day = days
          this.duration.hour = hours
          if (unwatch) {
            unwatch()
          }
        }
      },
      { immediate: true }
    )
  },
  methods: {
    getNumberRange(no) {
      let list = []
      for (let i = 0; i <= no; i++) {
        list.push({
          label: i + '',
          value: i,
        })
      }
      return list
    },
    onValueChange() {
      let factor = 1
      if (this.format === 'h') {
        factor = 1
      } else {
        factor = 3600 // In seconds
      }
      let duration = this.duration.day * 24 * factor
      duration += this.duration.hour * factor
      this.$emit('input', duration || -1)
    },
  },
}
</script>
