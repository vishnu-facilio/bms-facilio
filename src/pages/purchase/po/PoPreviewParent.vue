<template>
  <BrightFMPreview
    :details="details"
    v-if="$org.id === 274"
    :resizeWidget="resizeWidget"
  ></BrightFMPreview>
  <AsterPreview
    :details="details"
    v-else-if="$org.id === 583"
    :resizeWidget="resizeWidget"
  ></AsterPreview>
  <QfmPoPreview
    v-else-if="$org.id === 320"
    :details="details"
    :resizeWidget="resizeWidget"
  ></QfmPoPreview>
  <DefaultPreview
    :details="details"
    :resizeWidget="resizeWidget"
    v-else
  ></DefaultPreview>
</template>
<script>
import DefaultPreview from './PoSummaryPreview'
import BrightFMPreview from './orgpreview/BrightFMPoPreview'
import AsterPreview from './orgpreview/AsterPOPreview'
import QfmPoPreview from './orgpreview/QfmPoPreview'
export default {
  props: ['details', 'resizeWidget'],
  components: {
    DefaultPreview,
    BrightFMPreview,
    AsterPreview,
    QfmPoPreview,
  },
}
</script>
