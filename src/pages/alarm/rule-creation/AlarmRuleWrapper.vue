<template>
  <AlarmRuleCreation :isV2="true" @ruleEvent="handleEvents" />
</template>

<script>
import AlarmRuleCreation from './AlarmRuleCreation.vue'
import { facilioMicro } from 'util/microEmbed'
export default {
  components: { AlarmRuleCreation },
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
