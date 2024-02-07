<template>
  <div class="pivot-row-height-customization">
    <div class="theme-text-color text-left line-height25 mB10">
      {{ $t('pivot.rowHeight') }}
    </div>
    <el-radio v-model="density" label="SMALL" class="theme-text-color">
      <span class="theme-text-color">{{ $t('pivot.small') }}</span>
    </el-radio>
    <el-radio v-model="density" label="MEDIUM" class="theme-text-color">
      <span class="theme-text-color">{{ $t('pivot.medium') }}</span>
    </el-radio>
    <el-radio v-model="density" label="LARGE" class="theme-text-color">
      <span class="theme-text-color">{{ $t('pivot.large') }}</span>
    </el-radio>
  </div>
</template>

<script>
import './../pivot.scss'

export default {
  props: ['initialRowHieght'],
  watch: {
    density(newVal) {
      this.$emit('rowHeightChanged', newVal)
    },
  },
  mounted() {
    if (this.initialRowHieght) {
      this.density = this.initialRowHieght
    }
  },
  data() {
    return {
      visible: false,
      density: 'SMALL',
    }
  },
}
</script>

<style scoped>
.row-label-container {
  padding: 5px 10px;
  cursor: pointer;
}
.theme-text-color{
  color: #324056;
  font-size:13px;
  letter-spacing: 0.5px;
}
.pivot-row-height-customization{
  padding:10px 0px;
}
</style>
