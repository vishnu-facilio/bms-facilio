<template>
  <InspectionBuilder :isV2="true" @ruleEvent="handleEvents" />
</template>

<script>
import InspectionBuilder from './InspectionBuilder.vue'
import { facilioMicro } from 'util/microEmbed'
export default {
  components: { InspectionBuilder },
  data() {
    return {
      isV2: true,
    }
  },
  methods: {
    handleEvents(obj) {
      facilioMicro.emit('ruleEvents', obj)
    },
  },
}
</script>
