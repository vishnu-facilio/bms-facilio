<template>
  <BrightFMPreview
    :details="details"
    v-if="$org.id === 274"
    :resizeWidget="resizeWidget"
  ></BrightFMPreview>
  <QfmPrPreview
    :details="details"
    v-else-if="$org.id === 320"
    :resizeWidget="resizeWidget"
  ></QfmPrPreview>
  <DefaultPreview
    :details="details"
    :resizeWidget="resizeWidget"
    v-else
  ></DefaultPreview>
</template>
<script>
import DefaultPreview from './PrSummaryPreview'
import BrightFMPreview from './orgpreview/BrightFMPrPreview'
import QfmPrPreview from './orgpreview/QfmPrPreview'
export default {
  props: ['details', 'resizeWidget'],
  components: {
    DefaultPreview,
    BrightFMPreview,
    QfmPrPreview,
  },
}
</script>
