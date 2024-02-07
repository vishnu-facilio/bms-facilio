<template>
  <div class="height100vh">
    <div
      class="scrollable header-sidebar-hide quotation-pdf-con"
      :class="{
        'header-sidebar-hide': $route.path === 'pdf/inspectionpdf',
      }"
    >
      <div v-if="isLoading" class="flex-middle fc-empty-white">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <div>
        <div class="print-break-page quotation-print-con">
          <InspectionPdfWidget
            v-if="!isLoading"
            :details="record"
            :moduleName="moduleName"
          ></InspectionPdfWidget>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import InspectionPdfWidget from 'src/pages/inspection/individual-inspection/InspectionPdfWidget'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
export default {
  props: ['moduleName'],
  data() {
    return {
      isLoading: true,
      record: null,
    }
  },
  mounted() {
    this.loadInspectionRecord()
  },
  components: {
    InspectionPdfWidget,
    Spinner,
  },
  computed: {
    id() {
      let id = this.$getProperty(this, '$route.query.id', '')
      return id
    },
  },
  methods: {
    async loadInspectionRecord() {
      let { moduleName, id } = this
      this.isLoading = true
      let { [moduleName]: data, error } = await API.fetchRecord(moduleName, {
        id,
      })
      if (error) {
        this.$message.error('Error Occured' || error.message)
      } else {
        this.record = data
      }
      this.isLoading = false
    },
  },
}
</script>
<!-- Don't remove this style !-->
<style lang="scss">
.quotation-print-con {
  max-width: 1100px;
  margin-left: auto;
  margin-right: auto;
}
@media print {
  @page {
    z-index: 900;
    margin-bottom: 0.7cm !important;
    margin-top: 0.7cm !important;
    overflow: scroll;
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
    height: auto;
    overflow: visible;
    min-height: 100%;
    text-rendering: optimizeLegibility;
    counter-reset: page;
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
    page-break-inside: avoid !important;
    page-break-after: auto;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  .qanda-response-widget .qandacontainer {
    page-break-inside: avoid;
  }
  .qanda-response-widget .field-details .field-label,
  .qanda-response-widget .page-description,
  .qanda-response-widget .question-description,
  .qanda-response-widget .answer {
    font-size: 11px;
  }
  .qanda-response-widget .field-details .field-value,
  .qanda-response-widget .page-name,
  .qanda-response-widget .question,
  .qanda-response-widget .remarks-field {
    font-size: 12px;
  }
  .page-name {
    font-size: 12px !important;
  }
  .question-description {
    font-size: 11px !important;
  }
  .page-details-header {
    padding: 10px 15px !important;
    border-bottom: none !important;
    page-break-after: avoid;
  }
  .qanda-response-widget {
    padding: 0 20px !important;
    height: auto;
    .field-details {
      padding: 10px 20px;
    }
  }
  .answer-heading-text {
    font-size: 12px;
  }
  .question {
    font-size: 12px;
  }
  tr {
    page-break-inside: auto;
    page-break-after: auto;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  table {
    page-break-inside: auto;
    page-break-before: auto;
  }
  .fc-tr-border {
    border: solid 1px #dee7ef;
    border-bottom: solid 1px #dee7ef !important;
    border-top: solid 1px #dee7ef !important;
  }
  .qandacontainer,
  .qanda-response-widget .border-bottom {
    border-bottom: none !important;
  }
}
</style>
