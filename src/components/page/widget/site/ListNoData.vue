<template>
  <div
    class="flex-middle justify-content-center wo-flex-col height100 flex-direction-column"
  >
    <inline-svg
      :src="`svgs/emptystate/readings-empty`"
      iconClass="icon text-center icon-130"
    ></inline-svg>
    <div class="fc-black-dark f18 bold">
      No {{ moduleDisplayName ? moduleDisplayName : moduleName }} available
    </div>
  </div>
</template>
<script>
import { eventBus } from '@/page/widget/utils/eventBus'
export default {
  props: ['moduleDisplayName', 'iconPath', 'moduleName'],
  mounted() {
    eventBus.$emit('autoResizeContainer', this.moduleName)
  },
}
</script>
