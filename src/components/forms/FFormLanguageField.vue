<template>
  <el-select
    v-model="valueBinder"
    @change="updateValue"
    filterable
    clearable
    class="fc-input-full-border-select2 width100"
  >
    <el-option
      v-for="(language, index) in languagesList"
      :key="index"
      :label="language.label"
      :value="language.value"
      :disabled="field.isDisabled"
    >
    </el-option>
  </el-select>
</template>
<script>
import languages from 'util/data/languages'
export default {
  props: ['value', 'field'],
  data() {
    return {
      valueBinder: this.value,
    }
  },
  computed: {
    languagesList() {
      return languages
    },
  },
  methods: {
    updateValue(value) {
      this.$emit('input', value)
    },
  },
}
</script>
