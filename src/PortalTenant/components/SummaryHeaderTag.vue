<template>
  <el-tag
    v-if="label"
    effect="dark"
    class="emp-summary-tag mL15"
    :style="{ height: `${setHeight}px` }"
  >
    <div class="mL10 mR10">{{ label }}{{ height }}</div>
  </el-tag>
</template>
<script>
export default {
  props: {
    label: {
      type: String,
      require: true,
    },
    size: {
      type: String,
    },
  },
  computed: {
    setHeight() {
      let { size } = this
      if (size == 'small') {
        return '19'
      }
      return '24'
    },
  },
}
</script>
<style>
.emp-summary-tag.el-tag {
  color: #fff;
  background-color: #23b096;
  border-radius: 15px;
  font-size: 10px;
  font-weight: 600;
  overflow: hidden;
  text-align: center;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  text-transform: uppercase;
  border-color: #23b096;
}
</style>
