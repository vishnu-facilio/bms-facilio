<template>
  <el-row class="mB20">
    <div class="fc-dark-grey-txt14 pB10">Form Alignment</div>
    <el-button-group>
      <el-button
        :class="[position === 'left' ? 'active' : '']"
        @click="position = 'left'"
        class="align-btn"
        :disabled="isSaving"
        >Left</el-button
      >
      <el-button
        :class="[position === 'top' ? 'active' : '']"
        @click="position = 'top'"
        class="align-btn"
        :disabled="isSaving"
        >Top</el-button
      >
      <el-button
        :class="[position === 'right' ? 'active' : '']"
        @click="position = 'right'"
        class="align-btn"
        :disabled="isSaving"
        >Right</el-button
      >
    </el-button-group>
  </el-row>
</template>

<script>
export default {
  props: {
    labelPosition: {
      type: String,
      required: true,
    },
    isSaving: {
      type: Boolean,
    },
  },
  computed: {
    position: {
      get() {
        return this.labelPosition
      },
      set(value) {
        this.$emit('updatePosition', value)
      },
    },
  },
}
</script>
<style scoped>
.el-button.el-button--default.align-btn {
  padding: 12px 28px;
  font-size: 0.8125rem;
}
.el-button.el-button--default.align-btn:hover,
.el-button.el-button--default.align-btn.active {
  color: #fff;
  background: #39b2c2;
}
</style>
