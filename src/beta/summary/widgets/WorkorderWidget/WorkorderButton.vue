<template
  ><FButtonGroup :moreOptions="moreOptions">
    <template #options>
      <div
        style="display:flex;flex-direction:column;gap:20px; align-items:flex-start ;"
      >
        <span>Print</span>
        <span>Download</span>
      </div>
    </template>
  </FButtonGroup></template
>
<script>
import { FButtonGroup } from '@facilio/design-system'
export default {
  components: {
    FButtonGroup,
  },
  props: ['moreOptions'],
}
</script>
