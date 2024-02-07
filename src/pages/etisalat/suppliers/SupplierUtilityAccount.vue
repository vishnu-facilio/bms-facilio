<template>
  <div>
    <el-row>
      <el-col :span="24" class>
        <div class="p30 height159 pT38">
          <div class="fc-black3-16 line-height22 bold">
            {{ $t('etisalat.etisalat.total') }}
          </div>
          <div class="fc-black3-16 line-height22 bold nowrap">
            {{ $t('etisalat.etisalat.utility_accounts') }}
          </div>
          <div class="fc-black-24 bold pT10">{{ result.total }}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  props: ['details', 'moduleName'],
  data() {
    return {
      pdfopen: false,
      currentPage: 0,
      pageCount: 0,
      result: { energy: 0, gas: 0, swarage: 0, total: 0, water: 0 },
      params: {
        nameSpace: 'SupplierSummary',
        functionName: 'getSummaryData',
        paramList: [],
      },
    }
  },
  mounted() {
    this.getData()
  },
  methods: {
    getData() {
      let supplierrid = this.details.id
      this.params.paramList.push(supplierrid)
      this.$http.post(`/v2/workflow/runWorkflow`, this.params).then(resp => {
        if (
          resp.data.result &&
          resp.data.result.workflow &&
          resp.data.result.workflow.returnValue
        ) {
          this.result = resp.data.result.workflow.returnValue
        }
      })
    },
  },
}
</script>
