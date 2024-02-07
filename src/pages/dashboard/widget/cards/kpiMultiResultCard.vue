<template>
  <component
    ref="kpicards"
    v-if="layout"
    :is="layout"
    :widget="config ? config.widget : null"
    :config="config ? config : null"
  >
  </component>
</template>
<script>
import one from './KPILayouts/LayoutOne'
export default {
  props: ['config, widget'],
  components: {
    one: one,
  },
  computed: {
    layout() {
      if (
        this.config &&
        this.config.widget.dataOptions &&
        this.config.widget.dataOptions.metaJson
      ) {
        if (JSON.parse(this.config.widget.dataOptions.metaJson).layout) {
          return JSON.parse(this.config.widget.dataOptions.metaJson).layout
        } else {
          return 'one'
        }
      } else {
        return 'one'
      }
    },
  },
}
</script>

<style></style>
