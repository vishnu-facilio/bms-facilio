<template>
  <div class="qanda-pdf-layout quotation-pdf-con">
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <div v-else class="print-break-page">
      <InspectionPdfWidget
        :details="record"
        :moduleName="moduleName"
      ></InspectionPdfWidget>
    </div>
  </div>
</template>
<script>
import InspectionPdfWidget from 'src/pages/inspection/individual-inspection/InspectionPdfWidget'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
export default {
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
    moduleName() {
      return this.$getProperty(this, '$route.params.moduleName', '')
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
.qanda-pdf-layout {
  z-index: 100;
  background-color: #fff;
  height: 100vh;
  width: 100%;
  overflow-y: scroll;
  overflow-x: hidden;
  max-width: 1100px;
  margin-left: auto;
  margin-right: auto;
}
.fc-v1-portal-sidebar {
  display: none;
}
.portal-home-layout {
  background-color: #fff;
}
@media print {
  @page {
    z-index: 900;
    margin-bottom: 0.7cm !important;
    margin-top: 0.7cm !important;
  }
  html {
    min-height: 100%;
    text-rendering: optimizeLegibility;
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
  .portal-home-layout {
    background-color: #fff;
    height: 100%;
  }
  .portal-home-layout .fc-v1-portal-main {
    overflow: visible;
    height: 100%;
  }
  main {
    height: 100% !important;
    background: #fff;
  }
  .qanda-pdf-layout {
    height: auto;
  }

  .print-break-page {
    height: auto;
    overflow-y: scroll;
    display: block;
    page-break-after: always;
  }
  tr {
    page-break-inside: auto;
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
    page-break-inside: auto !important;
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
