<template>
  <div>
    <div v-if="prop == 'number'">
      {{ value }}
    </div>
    <div v-else-if="getFormattedCellValue(value, prop) == '#VALUE!'">
      <el-popover
        placement="bottom"
        :title="$t('pivot.formulaError')"
        width="425"
        trigger="hover"
        content="Error calculating formula field. Please check the expression."
      >
        <div slot="reference">
          {{ getFormattedCellValue(value, prop) }}
        </div>
      </el-popover>
    </div>
    <div v-else>
      {{ getFormattedCellValue(value, prop) }}
    </div>
  </div>
</template>

<script>
import BaseView from './BaseView.vue'
export default {
  extends: BaseView,
}
</script>
