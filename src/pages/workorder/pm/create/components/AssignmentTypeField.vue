<template>
  <el-select
    class="fc-input-full-border-select2 width100"
    v-model="modelValue"
    @change="onScopeChange"
    :disabled="disabled"
  >
    <el-option
      v-for="option in options"
      :key="option.value"
      :label="option.label"
      :value="option.value"
    >
    </el-option>
  </el-select>
</template>

<script>
export default {
  props: ['value', 'options', 'disabled'],
  computed: {
    modelValue: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      },
    },
  },
  methods: {
    onScopeChange() {
      let value = this || {}
      this.$emit('onScopeChange', value)
    },
  },
}
</script>

<style></style>
