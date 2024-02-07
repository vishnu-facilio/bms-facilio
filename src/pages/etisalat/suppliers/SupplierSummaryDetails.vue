<template>
  <div>
    <div>
      <el-row>
        <el-col :span="24">
          <div class>
            <el-row>
              <el-col
                :span="6"
                class="p30 flex-center-column border-right4 height159"
              >
                <InlineSvg
                  src="svgs/energy"
                  iconClass="icon icon-xxlll"
                ></InlineSvg>
                <div class="fc-black-14 text-left line-height22 pT10 bold">
                  {{ $t('etisalat.etisalat.electricity') }}
                </div>
                <div class="fc-black2-20 bold">{{ result.energy || 0 }}</div>
              </el-col>
              <el-col
                :span="6"
                class="p30 flex-center-column border-right4 height159"
              >
                <InlineSvg
                  src="svgs/water"
                  iconClass="icon icon-xxlll"
                ></InlineSvg>
                <div class="fc-black-14 text-left line-height22 pT10 bold">
                  {{ $t('etisalat.etisalat.water') }}
                </div>
                <div class="fc-black2-20 bold">{{ result.water || 0 }}</div>
              </el-col>
              <el-col
                :span="6"
                class="p30 flex-center-column border-right4 height159"
              >
                <InlineSvg
                  src="svgs/sewage"
                  iconClass="icon icon-xxlll"
                ></InlineSvg>
                <div class="fc-black-14 text-left line-height22 pT10 bold">
                  {{ 'Sewage' }}
                </div>
                <div class="fc-black2-20 bold">{{ result.sewage || 0 }}</div>
              </el-col>
              <el-col :span="6" class="p30 flex-center-column height159">
                <InlineSvg
                  src="svgs/gas"
                  iconClass="icon icon-xxxl"
                ></InlineSvg>
                <div class="fc-black-14 text-left line-height22 pT10 bold">
                  {{ $t('etisalat.etisalat.gas') }}
                </div>
                <div class="fc-black2-20 bold">{{ result.gas || 0 }}</div>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>

      <!-- second row -->
      <div class="pT30">
        <div class="fc-blue-label">{{ $t('etisalat.etisalat.tariffs') }}</div>
        <div class="mT10">
          <div
            class="flex-middle p20 justify-content-space flex-direction-row pB0"
          >
            <div class="fc-black-14 text-left bold">
              {{ $t('etisalat.etisalat.electricity') }}
            </div>
            <div>
              <i class="el-icon-edit fc-blue-txt4-12 f14 pointer"></i>
            </div>
          </div>
          <table class="width100 mT10 fc-suppier-table">
            <tr>
              <td colspan="50%" class="border-right10">
                <div class="d-flex justify-content-space">
                  <div class="fc-black-13 text-left">Upto 2,000 kWh</div>
                  <div class="fc-black-13 text-left">0.230 AED</div>
                </div>
              </td>
              <td colspan="50%">
                <div class="d-flex justify-content-space">
                  <div class="fc-black-13 text-left">Upto 2,000 kWh</div>
                  <div class="fc-black-13 text-left">0.230 AED</div>
                </div>
              </td>
            </tr>
            <tr>
              <td colspan="50%" class="border-right10">
                <div class="d-flex justify-content-space">
                  <div class="fc-black-13 text-left">2,000 kWh - 4000 kWh</div>
                  <div class="fc-black-13 text-left">2,000 kWh - 4000 kWh</div>
                </div>
              </td>
              <td colspan="50%"></td>
            </tr>
          </table>
        </div>
        <div class="mT20">
          <div
            class="flex-middle p20 justify-content-space flex-direction-row pB0"
          >
            <div class="fc-black-14 text-left bold">
              {{ $t('etisalat.etisalat.water') }}
            </div>
            <div>
              <i class="el-icon-edit fc-blue-txt4-12 f14 pointer"></i>
            </div>
          </div>
          <table class="width100 mT10 fc-suppier-table">
            <tr>
              <td colspan="50%" class="border-right10">
                <div class="d-flex justify-content-space">
                  <div class="fc-black-13 text-left">Upto 2,000 kWh</div>
                  <div class="fc-black-13 text-left">0.230 AED</div>
                </div>
              </td>
              <td colspan="50%">
                <div class="d-flex justify-content-space">
                  <div class="fc-black-13 text-left">Upto 2,000 kWh</div>
                  <div class="fc-black-13 text-left">0.230 AED</div>
                </div>
              </td>
            </tr>
            <tr>
              <td colspan="50%" class="border-right10">
                <div class="d-flex justify-content-space">
                  <div class="fc-black-13 text-left">2,000 kWh - 4000 kWh</div>
                  <div class="fc-black-13 text-left">2,000 kWh - 4000 kWh</div>
                </div>
              </td>
              <td colspan="50%">
                <div class="d-flex justify-content-space">
                  <div class="fc-black-13 text-left">2,000 kWh - 4000 kWh</div>
                  <div class="fc-black-13 text-left">2,000 kWh - 4000 kWh</div>
                </div>
              </td>
            </tr>
          </table>
        </div>

        <div class="mT20">
          <div
            class="flex-middle p20 justify-content-space flex-direction-row pB0"
          >
            <div class="fc-black-14 text-left bold">
              {{ $t('etisalat.etisalat.sewage') }}
            </div>
            <div>
              <i class="el-icon-edit fc-blue-txt4-12 f14 pointer"></i>
            </div>
          </div>
          <table class="width100 mT10 fc-suppier-table">
            <tr style="background: #fff;">
              <td colspan="50%">
                <div class="d-flex justify-content-space">
                  <div class="fc-black-13 text-left">Upto 2,000 kWh</div>
                  <div class="fc-black-13 text-left">0.230 AED</div>
                </div>
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
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
