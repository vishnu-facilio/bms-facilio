<template>
  <el-input
    v-model="modelValue"
    :placeholder="inputPlaceholder"
    :disabled="isDisabled"
    class="fc-input-full-border2"
    @change="handleChange"
  >
  </el-input>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['isDisabled', 'isEdit', 'value', 'field'],
  data() {
    return {}
  },
  computed: {
    modelValue: {
      get() {
        return this.value
      },
      set(value) {
        this.$emit('input', value)
      },
    },
    inputPlaceholder() {
      return `${this.$t('forms.field_permission.input_value')} ${
        this.field?.displayName
      }`
    },
  },
  methods: {
    handleChange() {
      let { value } = this || {}
      if (!isEmpty(value)) {
        this.$emit('change', value)
      }
    },
  },
}
</script>
