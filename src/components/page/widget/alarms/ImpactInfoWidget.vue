<template>
  <div class="p30 d-flex flex-direction-column">
    <div class="bold text-uppercase pB10 fc-black-12 text-left letter-spacing1">
      {{ title }}
    </div>
    <div class="f18 featured pT20">
      {{ impactName }}
    </div>
  </div>
</template>
<script>
export default {
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
  ],
  computed: {
    title() {
      return 'Impact Template'
    },
    impact() {
      let { details } = this
      let { alarm } = details || {}
      let { rule } = alarm || {}
      return this.$getProperty(rule, 'impact', {})
    },
    impactName() {
      let { impact } = this
      return this.$getProperty(impact, 'name', '---')
    },
  },
}
</script>
