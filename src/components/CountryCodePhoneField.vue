<template>
  <vue-tel-input
    :dropdownOptions="dropdownOptions"
    :inputOptions="inputOptions"
    v-model="field"
    styleClasses="vue-tel-input-facilio"
  ></vue-tel-input>
</template>

<script>
import { VueTelInput } from 'vue-tel-input'

export default {
  components: { VueTelInput },
  props: ['fieldValue'],
  data() {
    return {
      dropdownOptions: {
        disabled: false,
        showDialCodeInList: true,
        showDialCodeInSelection: true,
        showFlags: true,
        showSearchBox: false,
        tabindex: 0,
        width: '400px',
      },
      inputOptions: {
        showDialCode: true,
      },
    }
  },
  computed: {
    field: {
      get() {
        return this.fieldValue
      },
      set(value) {
        this.$emit('update:fieldValue', value)
      },
    },
  },
}
</script>

<style src="vue-tel-input/dist/vue-tel-input.css"></style>
<style lang="scss">
.vue-tel-input-facilio {
  width: 100%;
  height: 40px;
  border: 1px solid #d0d9e2;
  &:focus-within {
    box-shadow: none;
    border-color: #d0d9e2;
  }
}
</style>
