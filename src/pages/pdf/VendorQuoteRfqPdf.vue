<template>
  <div class="height100vh">
    <div
      class="scrollable header-sidebar-hide pr-pdf-con"
      :class="{
        'header-sidebar-hide': $route.path === 'pdf/vendorQuoteRfqPdf',
      }"
    >
      <div v-if="isLoading" class="flex-middle fc-empty-white">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <div>
        <div class="print-break-page pr-print-con">
          <RfqPreview
            v-if="!isLoading"
            :details="rfqDetails"
            :pdfSource="'vendorQuotes'"
          ></RfqPreview>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import RfqPreview from 'src/pages/purchase/rfq/RfqPreviewParent'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
import PoPdf from './PoPdf'
export default {
  extends: PoPdf,
  props: ['details'],
  data() {
    return {
      isLoading: true,
      rfqDetails: [],
    }
  },
  mounted() {
    this.loadData()
  },
  components: {
    RfqPreview,
    Spinner,
  },
  computed: {
    id() {
      return this.$route.query.rfqId ? parseInt(this.$route.query.rfqId) : ''
    },
  },
  methods: {
    async loadData() {
      this.isLoading = true
      let { requestForQuotation, error } = await API.fetchRecord(
        'requestForQuotation',
        {
          id: this.id,
        }
      )
      if (!isEmpty(error)) {
        let {
          message = this.$t('common._common.error_occured_while_fetching_rfq'),
        } = error
        this.$message.error(message)
      } else {
        this.rfqDetails = requestForQuotation
        if (this.$route.query.pdfDetails) {
          this.rfqDetails = JSON.parse(this.$route.query.pdfDetails)
        }
      }
      this.isLoading = false
    },
  },
}
</script>
