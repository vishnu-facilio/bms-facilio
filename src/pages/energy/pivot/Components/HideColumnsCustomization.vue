<template>
  <el-popover placement="bottom" v-model="visible">
    <div class="hide-column-customize" >
      <div
        class="single-hide-column-container"
        v-for="(conf, index) in columnConfig"
        :key="index"
      >
        <div class="column-label-container">
          {{ conf.label }}
        </div>
        <el-switch v-model="conf.hideColumn"></el-switch>
      </div>
    </div>
    <div slot="reference" class="hide-label-container pivot-tab-hover" style="margin-left:16px;">
      <i class="el-icon-view" style="height:16px;width:16px; font-weight: 600 !important;transform: scale(1.1);"></i>
      {{ $t('pivot.hideColumns') }}
    </div>
  </el-popover>
</template>

<script>
import './../pivot.scss'

export default {
  props: ['columnConfig'],
  data() {
    return {
      visible: false,
    }
  },
}
</script>
<style scoped>
.hide-label-container {
  padding: 6.4px 10px;
  cursor: pointer;
  font-size:13px;
}
.single-hide-column-container {
  display: flex;
  align-content: space-between;
  justify-content: space-between;
}
.column-label-container {
  padding: 5px 15px;
}
</style>
