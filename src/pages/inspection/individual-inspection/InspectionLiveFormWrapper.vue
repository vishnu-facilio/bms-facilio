<template>
  <InspectionLiveForm :isV2="isV2" @ruleEvent="handleEvents" />
</template>

<script>
import InspectionLiveForm from './InspectionLiveForm.vue'
import { facilioMicro } from 'util/microEmbed'
export default {
  components: { InspectionLiveForm },
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
