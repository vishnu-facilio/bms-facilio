<template>
  <InductionBuilder :isV2="true" @ruleEvent="handleEvents" />
</template>

<script>
import InductionBuilder from './InductionBuilder.vue'
import { facilioMicro } from 'util/microEmbed'
export default {
  components: { InductionBuilder },
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
