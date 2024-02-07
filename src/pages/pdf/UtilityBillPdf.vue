<template>
  <FContainer class="height100vh">
    <FContainer
      class="scrollable header-sidebar-hide pr-pdf-con"
      :class="{
        'header-sidebar-hide': $route.path === 'pdf/utilityBillspdf',
      }"
      width="100%;"
      height="100%;"
      position="inherit;"
      top="0;"
      left="0;"
      right="0:"
      page-break-after="always;"
      page-break-inside="avoid;"
    >
      <FContainer v-if="isLoading" class="flex-middle fc-empty-white">
        <FSpinner :show="isLoading" size="80"></FSpinner>
      </FContainer>
      <FContainer>
        <FContainer
          class="print-break-page pr-print-con"
          height="100%;"
          overflow-y="scroll;"
          display="block;"
          page-break-after="always;"
          max-width="1100px;"
          margin-left="auto;"
          margin-right="auto;"
        >
          <UtilityBillSummary
            v-if="!isLoading"
            :details="billDetails"
          ></UtilityBillSummary>
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import UtilityBillSummary from 'src/beta/summary/widget/utilityBills/UtilityBillSummary.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { FContainer, FSpinner } from '@facilio/design-system'

export default {
  props: ['details'],
  data() {
    return {
      isLoading: true,
      billDetails: null,
    }
  },
  mounted() {
    this.loadData()
  },
  components: {
    UtilityBillSummary,
    FContainer,
    FSpinner,
  },
  computed: {
    id() {
      return this.$route.query.billId ? parseInt(this.$route.query.billId) : ''
    },
  },
  methods: {
    async loadData() {
      this.isLoading = true
      let { utilityIntegrationBills, error } = await API.fetchRecord(
        'utilityIntegrationBills',
        {
          id: this.id,
        }
      )
      if (!isEmpty(error)) {
        let {
          message = this.$t(
            'common._common.error_occured_while_fetching_bills'
          ),
        } = error
        this.$message.error(message)
      } else {
        this.billDetails = utilityIntegrationBills
        if (this.$route.query.pdfDetails) {
          this.billDetails = JSON.parse(this.$route.query.pdfDetails)
        }
      }
      this.isLoading = false
    },
  },
}
</script>
