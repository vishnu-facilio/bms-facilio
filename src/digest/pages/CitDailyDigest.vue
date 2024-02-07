<template>
  <div class="fc-digest-page-bg">
    <div class="digest-con">
      <div class="fc-digest-cus-logo">
        <div>
          <img
            src="~assets/customer-logo/cit-new-logo.png"
            style="width: 80px;height: 60px;"
          />
        </div>
        <div class="text-center fc-table-digest-head">
          NEOM - Daily Activity Digest
        </div>
        <div>
          <img src="~assets/customer-logo/nemo.svg" style="width: 50px;" />
        </div>
      </div>
      <div class="pT20">
        <div class="fc-digest-heading bold">
          Hi
        </div>
        <div class="fc-digest-desc">
          Here’s an overview of your daily maintenance activities for
          <span class="bold">NEOM.</span>
        </div>
      </div>
      <table class="fc-digest-table pT30">
        <tbody>
          <tr>
            <td
              colspan="100%"
              class="text-center fc-table-digest-td-head fc-digest-td-block-top-border"
            >
              Work Order Summary
            </td>
          </tr>
          <tr>
            <td colspan="33.3%" class="fc-digest-td-block">
              <div class="fc-digest-td-txt fw6">
                Total Number of Work-Orders Till
              </div>
              <div class="fc-digest-td-txt">
                Yesterday
              </div>
            </td>
            <td colspan="33.3%" class="fc-digest-td-block">
              <div class="fc-digest-td-txt">{{ woTillYesterday }} Nos</div>
            </td>
            <td
              colspan="33.3%"
              class="fc-digest-td-block fc-digest-td-block-right-border"
            >
              <div class="fc-digest-td-txt">
                {{ woOpenTillYesterdayPercentage }} % Open
              </div>
            </td>
          </tr>
          <tr>
            <td colspan="100%" class="text-center fc-table-digest-td-head">
              Work-Orders Summary Today
            </td>
          </tr>
          <tr>
            <td colspan="33.3%" class="fc-digest-td-block">
              <div class="fc-digest-td-txt fw6">
                Total Number of Work-Orders for NEOM
              </div>
            </td>
            <td colspan="33.3%" class="fc-digest-td-block">
              <div class="fc-digest-td-txt">{{ totalWo }} Nos</div>
            </td>
            <td
              colspan="33.3%"
              class="fc-digest-td-block fc-digest-td-block-right-border"
            >
              <div class="fc-digest-td-txt">
                {{ totalWoOpenPercentage }} % Open
              </div>
            </td>
          </tr>
          <!-- <tr>
            <td colspan="33.3%" class="fc-digest-td-block">
              <div class="fc-digest-td-txt fw6">
                Unplanned Vs Planned Maintenance WO Details
              </div>
            </td>
            <td colspan="33.3%" class="fc-digest-td-block">
              <div class="fc-digest-td-txt">
                {{ totalWoPlanned }} Nos of Planned Maintenance
              </div>
              <div class="fc-digest-td-txt">
                {{ totalWoUnplanned }} Nos of Unplanned Maintenance
              </div>
            </td>
            <td
              colspan="33.3%"
              class="fc-digest-td-block fc-digest-td-block-right-border"
            >
              <div class="fc-digest-td-txt">
                {{ totalWoPlannedOpenPercentage }} % of Open Planned Maintenance
              </div>
              <div class="fc-digest-td-txt">
                {{ totalWoUnplannedOpenPercentage }} % of Open Unplanned Maintenance
              </div>
            </td>
          </tr> -->
          <tr>
            <td colspan="33.3%" class="fc-digest-td-block">
              <div class="fc-digest-td-txt fw6">
                Work Orders Segregation by Area
              </div>
            </td>
            <td
              colspan="66.6%"
              class="fc-digest-td-block p0 fc-digest-td-block-right-border"
            >
              <table>
                <tr>
                  <td
                    colspan="22.2%"
                    class="fc-digest-td-block border-left-none"
                  >
                    <div class="fc-digest-td-txt fw6">
                      Area 1 Purple
                    </div>
                    <div class="fc-digest-td-txt">
                      Total - {{ totalWoArea1Purple }} nos of WO
                    </div>
                  </td>
                  <td colspan="22.2%" class="fc-digest-td-block">
                    <div class="fc-digest-td-txt fw6">
                      Area 2 Green
                    </div>
                    <div class="fc-digest-td-txt">
                      Total - {{ totalWoArea2Green }} nos of WO
                    </div>
                  </td>
                  <td colspan="22.2%" class="fc-digest-td-block">
                    <div class="fc-digest-td-txt fw6">
                      Area 3 Orange
                    </div>
                    <div class="fc-digest-td-txt">
                      Total - {{ totalWoArea3Orange }} nos of WO
                    </div>
                  </td>
                </tr>
                <tr>
                  <td colspan="100%" class="fc-digest-td-block border-none">
                    <div class="fc-digest-td-txt fw6">
                      Independent Areas
                    </div>
                    <div class="fc-digest-td-txt">
                      Total - {{ independentLoadData.value }} nos of WO
                    </div>
                  </td>
                </tr>
              </table>
            </td>
          </tr>

          <tr>
            <td colspan="100%" class="text-center fc-table-digest-td-head">
              Work-Order Categorisation/Status - Today
            </td>
          </tr>
          <tr>
            <td
              colspan="100%"
              class="fc-digest-td-block fc-digest-td-block-right-border"
            >
              <div class="fc-digest-td-txt text-left">
                Bar Chart - WOBS by Status
              </div>
              <div
                class="fc-digest-td-txt text-center fc-cit-digest-chart bold"
              >
                <module-new-report
                  :id="reportId"
                  hideTabs="true"
                  isWidget="true"
                >
                </module-new-report>
              </div>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="fc-digest-page-break">
        <div class="fc-digest-desc pT20">
          Here is a quick look, at the number of Work-orders per each service
          line
        </div>

        <table class="fc-digest-table2 mT20">
          <thead>
            <tr>
              <th
                class="fc-digest-table-head2 fw6 text-center fc-digest-td-block fc-digest-td-block-top-border"
                colspan="25%"
              >
                Description
              </th>
              <th
                class="fc-digest-table-head2 fw6 text-center fc-digest-td-block fc-digest-td-block-top-border"
                colspan="25%"
              >
                Total Number of WO
              </th>
              <th
                class="fc-digest-table-head2 fw6 text-center fc-digest-td-block fc-digest-td-block-top-border fc-digest-td-block-right-border"
                colspan="25%"
              >
                Closed Work - Order Count
              </th>
            </tr>
          </thead>
          <tbody v-if="services.length === 0">
            <tr>
              <td colspan="100%" class="text-center"></td>
            </tr>
          </tbody>
          <tbody v-else>
            <tr
              class="tablerow"
              v-for="(service, index) in services"
              :key="index"
            >
              <td colspan="25%" class="fc-digest-td-block fc-digest-td-txt">
                {{ service.name }}
              </td>
              <td colspan="25%" class="fc-digest-td-block fc-digest-td-txt">
                {{ service.totalWo }} Nos
              </td>
              <td
                colspan="25%"
                class="fc-digest-td-block  fc-digest-td-block-right-border  fc-digest-td-txt"
              >
                {{ service.totalWoClose }} Nos
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import ModuleNewReport from 'src/pages/report/ModuleNewReport'
import OuterApp from 'src/OuterAppUtil/OuterAppHelper'

export default {
  mixins: [OuterApp],
  components: {
    ModuleNewReport,
  },
  data() {
    return {
      isMobilePage: false,
      reportId: 65,
      woTillYesterday: 0,
      woOpenTillYesterdayPercentage: 0,
      totalWo: 0,
      totalWoOpenPercentage: 0,
      totalWoPlanned: 0,
      totalWoUnplanned: 0,
      totalWoPlannedOpenPercentage: 0,
      totalWoUnplannedOpenPercentage: 0,
      totalWoArea1Purple: 0,
      totalWoArea2Green: 0,
      totalWoArea3Orange: 0,
      services: [],
      independentLoadData: [],
      loading: true,
    }
  },
  created() {
    this.loadQData()
    this.loadData()
    this.independentArea()
  },
  methods: {
    loadData() {
      API.get('/v2/workflow/getDefaultWorkflowResult', {
        defaultWorkflowId: 201,
      }).then(({ data }) => {
        const result = data.workflow.returnValue
        this.woTillYesterday = result.woTillYesterday
        this.woOpenTillYesterdayPercentage =
          result.woOpenTillYesterdayPercentage
        this.totalWo = result.totalWo
        this.totalWoOpenPercentage = result.totalWoOpenPercentage
        this.totalWoPlanned = result.totalWoPlanned
        this.totalWoUnplanned = result.totalWoUnplanned
        this.totalWoPlannedOpenPercentage = result.totalWoPlannedOpenPercentage
        this.totalWoUnplannedOpenPercentage =
          result.totalWoUnplannedOpenPercentage
        this.totalWoArea1Purple = result.totalWoArea1Purple
        this.totalWoArea2Green = result.totalWoArea2Green
        this.totalWoArea3Orange = result.totalWoArea3Orange
        this.services = result.services
      })
    },
    async independentArea() {
      this.loading = true
      let { error, data } = await API.get('/v2/kpi/module/value?id=141')
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.independentLoadData = data || []
      }
      this.loading = false
    },
  },
}
</script>
