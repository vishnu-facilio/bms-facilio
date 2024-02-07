<template>
  <QFMQuotationPreview
    :details="details"
    v-if="$org.id === 320"
    :resizeWidget="resizeWidget"
  ></QFMQuotationPreview>
  <BrightFMQuotationPreview
    :details="details"
    v-else-if="$org.id === 274"
    :resizeWidget="resizeWidget"
  ></BrightFMQuotationPreview>
  <RenaissancePreview
    :details="details"
    v-else-if="$org.id === 559"
    :resizeWidget="resizeWidget"
  >
  </RenaissancePreview>
  <DefaultQuotationPreview
    :details="details"
    :resizeWidget="resizeWidget"
    v-else
  ></DefaultQuotationPreview>
</template>
<script>
import DefaultQuotationPreview from './QuotationPreview'
import QFMQuotationPreview from './orgpreview/QuotationSummaryQfm'
import BrightFMQuotationPreview from './orgpreview/QuotationBrightFM'
import RenaissancePreview from './orgpreview/QuotationRenaissanceSummary'
export default {
  props: ['details', 'resizeWidget'],
  components: {
    DefaultQuotationPreview,
    QFMQuotationPreview,
    BrightFMQuotationPreview,
    RenaissancePreview,
  },
}
</script>
