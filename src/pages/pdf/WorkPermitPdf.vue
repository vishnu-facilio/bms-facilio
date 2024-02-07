<template>
  <div class="height100vh">
    <div
      class="scrollable header-sidebar-hide quotation-pdf-con workpermit-pdf-con"
      :class="{
        'header-sidebar-hide': $route.path === 'pdf/workpermit',
      }"
    >
      <div v-if="isLoading" class="flex-middle fc-empty-white">
        <spinner :show="isLoading" size="80"></spinner>
      </div>
      <div class="pL25 pR25">
        <div class="print-break-page">
          <WorkPermitPreview
            v-if="!isLoading"
            :details="recordDetails"
            :moduleName="moduleName"
            :primaryFields="['name']"
          ></WorkPermitPreview>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import WorkPermitPreview from 'src/pages/workorder/workpermit/v3/WorkPermitPreview'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Spinner from '@/Spinner'
export default {
  data() {
    return {
      isLoading: true,
      recordDetails: null,
    }
  },
  mounted() {
    Promise.all([
      this.$store.dispatch('view/loadModuleMeta', this.moduleName),
      this.loadData(),
    ])
  },
  components: {
    WorkPermitPreview,
    Spinner,
  },
  computed: {
    id() {
      return this.$route.query.id ? parseInt(this.$route.query.id) : ''
    },
    moduleName() {
      return 'workpermit'
    },
  },
  methods: {
    async loadData() {
      this.isLoading = true
      let { workpermit, error } = await API.fetchRecord('workpermit', {
        id: this.id,
      })
      if (!isEmpty(error)) {
        let { message = 'Error Occured while fetching Work Permit' } = error
        this.$message.error(message)
      } else {
        this.recordDetails = workpermit
      }
      this.isLoading = false
    },
  },
}
</script>
<!-- Don't remove this style !-->
<style lang="scss">
.fc-workpermit-sum-con {
  max-width: 1100px;
  margin-left: auto;
  margin-right: auto;
}
@media print {
  @page {
    orphans: 4;
    widows: 2;
    margin-bottom: 0.7cm;
    margin-top: 0.7cm;
  }
  html {
    min-height: 100%;
    text-rendering: optimizeLegibility;
  }

  .fc-white-theme .layout-page-container {
    background: #fff;
  }

  .normal main {
    height: 100vh !important;
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
  }
  .fc-quotation-con {
    max-width: 1000px;
    width: 100%;
    padding: 20px 50px;
    margin-left: auto;
    height: 100%;
    margin-right: auto;
    position: relative;
    overflow: visible;
    display: block;
    margin-top: 0;
  }
  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  td {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  .fc-black-12,
  .fc-black-11 {
    font-size: 10px !important;
  }
  .el-table thead {
    display: table-header-group;
  }
  .work-permit-table .setting-list-view-table td {
    font-size: 11px !important;
    padding: 7px 15px !important;
    border-right: 1px solid #e4eaed !important;
  }
  .work-permit-table {
    border: 1px solid #e4eaed !important;
  }
  .setting-list-view-table tr {
    border: 1px solid transparent !important;
  }
  .fc-workpermit-sum-con {
    margin-top: 0;
  }
  .print-break-page .setting-list-view-table .setting-table-th {
    padding: 10px 15px !important;
  }
  .label-txt-black {
    font-size: 12px !important;
  }
  .work-permit-table .setting-list-view-table .setting-th-text {
    height: 30px !important;
  }
  .workpermit-pdf-con {
    .f12 {
      font-size: 11px !important;
    }
    .pT20 {
      padding-top: 15px !important;
    }
    .pB20 {
      padding-bottom: 15px !important;
    }
    .pT15 {
      padding-top: 10px !important;
    }
    .pB15 {
      padding-bottom: 10px !important;
    }
    .pT30 {
      padding-top: 20px !important;
    }
    .pB30 {
      padding-bottom: 20px !important;
    }
  }
}
</style>
