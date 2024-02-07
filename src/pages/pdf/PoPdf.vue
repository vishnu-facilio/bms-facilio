<template>
  <div class="height100vh">
    <div
      class="scrollable header-sidebar-hide pr-pdf-con"
      :class="{
        'header-sidebar-hide': $route.path === 'pdf/popdf',
      }"
    >
      <div v-if="isLoading" class="flex-middle fc-empty-white">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <div>
        <div class="print-break-page pr-print-con">
          <poPreview v-if="!isLoading" :details="poDetails"></poPreview>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import poPreview from 'src/pages/purchase/po/PoPreviewParent'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
export default {
  props: ['details'],
  data() {
    return {
      isLoading: true,
      poDetails: [],
    }
  },
  mounted() {
    this.loadData()
  },
  components: {
    poPreview,
    Spinner,
  },
  computed: {
    id() {
      return this.$route.query.poId ? parseInt(this.$route.query.poId) : ''
    },
  },
  methods: {
    async loadData() {
      this.isLoading = true
      let { purchaseorder, error } = await API.fetchRecord('purchaseorder', {
        id: this.id,
      })
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Purchase Order' } = error
        this.$message.error(message)
      } else {
        this.poDetails = purchaseorder
        if (this.$route.query.pdfDetails) {
          Object.assign(
            this.prDetails,
            JSON.parse(this.$route.query.pdfDetails)
          )
        }
      }
      this.isLoading = false
    },
  },
}
</script>
<!-- Don't remove this style !-->
<style lang="scss">
.pr-print-con {
  max-width: 1100px;
  margin-left: auto;
  margin-right: auto;
}

@media print {
  @page {
    // orphans: 2;
    // widows: 2;
    margin-top: 20px;
    z-index: 900;
  }

  html {
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  .normal main {
    height: 100vh !important;
    background: #fff;
    min-height: 100vh !important;
  }

  .header-sidebar-hide {
    width: 100%;
    height: 100%;
    position: inherit;
    top: 0;
    left: 0;
    right: 0;
    page-break-after: always;
    page-break-inside: avoid;
  }

  .layout-page .pL60 {
    padding-left: 0 !important;
    position: inherit !important;
    height: initial !important;
  }

  .print-break-page {
    height: 100%;
    overflow-y: scroll;
    display: block;
    page-break-after: always;
  }

  body {
    overflow: scroll;
    background: #fff;
    display: block;
    width: auto;
    height: 100%;
    overflow: visible;
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  .fc-quotation-con {
    max-width: 1000px;
    width: 100%;
    padding: 0 30px;
    margin-left: auto;
    height: 100%;
    margin-right: auto;
    position: relative;
    overflow: visible;
    display: flex;
    flex-direction: column;
    margin-top: 0 !important;
  }

  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }

  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }

  .el-table thead {
    display: table-header-group;
  }

  .fc-quotation-table-td-left {
    white-space: inherit !important;
  }

  .fc-quotation-table {
    th {
      white-space: nowrap;
    }
  }

  .fc-quotation-table-td-right,
  .fc-quotation-table-td-left {
    height: inherit;
  }

  .el-table__body,
  .el-table__header {
    border: none !important;
  }

  .fc-quotation-table-td-data {
    page-break-inside: avoid;
  }

  .fc-black-13 {
    font-size: 12px !important;
  }

  .fc-quotation-summary-table {
    border: none;
  }

  .fc-quotation-summary-table th {
    height: 34px;
    line-height: 34px;
    border-top: 1px solid #584a8f;
    font-size: 9px;
    padding: 0 10px !important;
  }

  .fc-quotation-table-td-left,
  .fc-quotation-table-td-right {
    height: 35px !important;
    padding: 10px 20px !important;
    font-size: 12px;
  }

  .fc-quotation-summary-table td {
    font-size: 12px !important;
    padding: 5px 10px !important;
  }

  .fc-black-15 {
    font-size: 13px !important;
  }

  .fc-black3-16 {
    font-size: 20px !important;
  }

  .fc-black-12 {
    font-size: 10px !important;
  }

  .widthreduce-pdf {
    width: 47.4% !important;
  }

  .width43 {
    width: 52.6% !important;
  }

  .label-txt-black {
    font-size: 12px !important;
  }

  .padding-remove-bottom-pdf {
    padding-bottom: 0 !important;
  }

  .fc-quotation-bill-con {
    margin-top: 10px !important;
  }

  .fc-qfm-print-footer {
    width: calc(100% - 55px);
    position: fixed;
    z-index: 200;
    max-width: 1100px;
    bottom: -4px;
    align-items: center;
    background: #fff;
    justify-content: space-between;
    border-top: 1px solid #e4eaed;
    padding: 2px 0 10px;
    display: flex;
    margin-left: 30px;
    margin-right: 30px;
  }

  .fc-bfm-print-footer {
    width: 100%;
    height: 80px;
    left: 0;
    position: fixed;
    right: 0;
    bottom: 0;
    background: #fff;
    display: block;
    z-index: 200;
  }

  .fc-print-footer-center {
    max-width: 900px;
    margin-left: auto;
    margin-right: auto;
    padding-right: 40px;
    padding-left: 40px;
  }

  .fc-print-page-break-inside-avoid {
    page-break-inside: avoid;
  }

  .pB15 {
    padding-bottom: 10px !important;
  }

  .fc-print-pT15 {
    padding-top: 7px !important;
  }

  .fc-black-14,
  .fc-black-15 {
    font-size: 12px !important;
    line-height: 20px !important;
  }

  .f12 {
    font-size: 10px !important;
  }

  .fc-print-pT20 {
    padding-top: 4px !important;
  }

  .fc-quotation-summary-table th:first-child {
    border-left: 1px solid #584a8f;
  }

  .fc-qfm-subject {
    margin-top: 10px !important;
  }

  .fc-qfm-desc {
    padding-top: 0px !important;
  }

  .fc-qfm-pt20 {
    padding-top: 10px !important;
  }

  .fc-qfm-mT0 {
    margin-top: 0 !important;
  }

  .fc-qfm-pt10 {
    padding-top: 5px !important;
  }

  .fc-qfm-pb10 {
    padding-bottom: 10px !important;
  }

  .fc-qfm-mB20 {
    margin-bottom: 10px !important;
  }

  .line-height30 {
    line-height: 20px !important;
  }

  .fc-signature-border {
    .qfm-sign-width {
      width: 250px;
      height: 30px;
    }
  }

  .pB5 {
    padding-bottom: 0 !important;
  }

  .print-reduced-padding {
    padding: 10px 10px 0 10px !important;
  }

  .fc-qfm-page-break {
    page-break-after: always;
    page-break-inside: avoid;
  }

  .quote-id-footer {
    position: relative;
    top: 7px;
  }

  thead {
    display: table-header-group;
  }

  tfoot {
    display: table-footer-group;
  }

  .fc-quotation-summary-table .fc-quotation-table {
    border: none !important;
  }
  .fc-brightfm-po-pdf-con {
    zoom: 90%;
  }
}
</style>
