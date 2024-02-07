<template>
  <RfqDefaultPreview
    :details="details"
    :resizeWidget="resizeWidget"
    :pdfSource="pdfSource"
  >
  </RfqDefaultPreview>
</template>
<script>
import RfqDefaultPreview from './RfqDefaultPreview.vue'
export default {
  props: ['details', 'resizeWidget', 'pdfSource'],
  components: {
    RfqDefaultPreview,
  },
}
</script>
