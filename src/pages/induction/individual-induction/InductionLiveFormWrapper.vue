<template>
  <InductionLiveForm :isV2="true" @ruleEvent="handleEvents" />
</template>

<script>
import InductionLiveForm from './InductionLiveForm.vue'
import { facilioMicro } from 'util/microEmbed'
export default {
  components: { InductionLiveForm },
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
