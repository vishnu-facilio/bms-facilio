<template>
  <SummaryFieldsWidget
    ref="summaryFieldsWidget"
    v-bind="$attrs"
    :details="details"
    :moduleName="moduleName"
    :disableAutoResize="false"
    :detailsLayoutProp="detailsLayoutProp"
    class="pB20"
  ></SummaryFieldsWidget>
</template>

<script>
import SummaryFieldsWidget from 'src/components/page/widget/common/field-details/SummaryFieldsWidget'
import { isEmpty } from '@facilio/utils/validation'
import { getApp } from '@facilio/router'
import { API } from '@facilio/api'

export default {
  props: ['details'],
  data() {
    return {
      detailsLayoutProp: null,
    }
  },
  components: {
    SummaryFieldsWidget,
  },
  created() {
    this.loadSummaryDetailLayout()
  },
  computed: {
    moduleName() {
      return 'vendorQuotes'
    },
  },
  methods: {
    async loadSummaryDetailLayout() {
      let { moduleName } = this
      let summaryWidgetName = 'vendoQuotesRfqDetailsWidget'

      if (isEmpty(summaryWidgetName)) return
      this.summaryDetailsLoading = true

      let appId = (getApp() || {}).id
      let params = { moduleName, appId, widgetName: summaryWidgetName }
      let { data, error } = await API.get(
        'v2/customPage/summaryFieldWidget',
        params
      )

      if (!error) {
        this.detailsLayoutProp = data
      }
    },
  },
}
</script>
