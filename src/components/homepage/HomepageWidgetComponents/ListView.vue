<template>
  <div class="portal-service-request-card h100">
    <el-card
      class="box-card fc-recently-received-card fc-service-request-card h100"
    >
      <div class="flex-middle p20">
        <inline-svg
          src="svgs/employeePortal/service-request"
          iconClass="icon text-center icon-md vertical-bottom"
        ></inline-svg>
        <div class="fc-portal-title-txt-14 fw5 pL10">
          Service Requests
        </div>
      </div>
      <div class="flex-middle mB20" :gutter="10" style="padding: 0px 20px">
        <div class="mR20">
          <div class="service-request-add ">
            <div class="fc-add-circle">
              <inline-svg
                src="svgs/employeePortal/plus"
                iconClass="icon text-center icon-md vertical-bottom"
              ></inline-svg>
            </div>
            <div class="pT5 add-request" @click="redirectTo('add-request')">
              Add Request
            </div>
          </div>
        </div>

        <template v-if="serviceCatalogs.length">
          <div
            v-for="(serviceCatalog, index) in serviceCatalogs"
            :key="index"
            class="flex-middle flex-direction-column service-request-cat-add pointer"
            v-if="index < 3"
            @click="redirectTosServiceCategories(serviceCatalog.id)"
          >
            <div class="fc-circle  pointer">
              <inline-svg
                v-if="$validation.isEmpty(serviceCatalog.photoUrl)"
                src="vehicle-delivery"
                class="vertical-middle icon-catalog"
                iconClass="icon icon-38"
                style="height:100%;width:100%;"
              ></inline-svg>
              <img
                v-else
                :src="serviceCatalog.photoUrl"
                style="height:100%;width:100%;"
              />
            </div>
            <div class="fc-grey11 pT5">
              {{ serviceCatalog.name }}
            </div>
          </div>
          <div
            class="flex-middle flex-direction-column service-request-cat-add"
            v-if="serviceCatalogs[5]"
            style="position: absolute;right: 10px;"
          >
            <div class="w100 h100" style="display: contents;">
              <div
                class="fc-circle pointer"
                style="color: #483db6;"
                @click="redirectTosServiceCategories()"
              >
                <inline-svg
                  src="svgs/employeePortal/ic-more-horiz"
                ></inline-svg>
              </div>
              <div class="fc-grey11 text-center pT5">
                More
              </div>
            </div>
          </div>
        </template>
      </div>
      <template v-if="reports.length">
        <el-table :data="tableData" style="width: 100%" :show-header="false">
          <el-table-column prop="id" width="100">
            <template slot-scope="scope">
              <div
                style="padding-left:20px"
                class="fc-portal-blue-txt14 inline"
                @click="redirectToSRSummary(scope.row.id)"
              >
                {{ `# ${scope.row.id}` }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="subject">
            <template slot-scope="scope">
              <div
                class="fc-portal-black14"
                @click="redirectToSRSummary(scope.row.id)"
              >
                {{ `${scope.row.subject}` }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="moduleState">
            <template slot-scope="scope">
              <div
                v-if="scope.row.moduleState"
                style="display: flex;
                width: 100%;"
              >
                <div
                  class="fc-status-dot-green mR5"
                  v-if="scope.row.moduleState.type === 'OPEN'"
                ></div>
                <div
                  class="fc-status-dot-red mR5"
                  v-else-if="scope.row.moduleState.type === 'CLOSED'"
                ></div>
                <div>
                  {{ scope.row.moduleState.displayName }}
                </div>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <!-- <div
          v-for="(data, index) in reports"
          :key="index"
          v-if="index < 2"
          @click="redirectToSRSummary(data.id)"
        >
          <div :gutter="10" class="fc-request-list-block pointer">
            <div :span="4" class="">
              <div class="fc-portal-blue-txt14 ">#{{ data.id }}</div>
            </div>
            <div :span="10">
              <div class="fc-portal-black14">
                {{ data.subject }}
              </div>
            </div>
            <div :span="10">
              <div class="fc-portal-black2-14 flex-middle">
                <div
                  class="fc-status-dot-green mR5"
                  v-if="data.moduleState.type === 'OPEN'"
                ></div>
                <div
                  class="fc-status-dot-red mR5"
                  v-else-if="data.moduleState.type === 'CLOSED'"
                ></div>
                {{ data.moduleState.displayName }}
              </div>
            </div>
          </div>
        </div> -->
        <div
          v-if="reports.length >= 3"
          class="service-request-footer pointer"
          @click="redirectTo('show-all')"
        >
          Show all
        </div>
      </template>
      <template v-else>
        <div class="fc-fourth-sec-body">
          <inline-svg
            src="svgs/employeePortal/EmptyState/Emptystate-Servicerequest"
            iconClass="text-center vertical-bottom icon-sm-md"
          ></inline-svg>
        </div>
      </template>
    </el-card>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import {
  findRouteForTab,
  tabTypes,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

import baseWidget from 'src/components/homepage/HomepageWidgetComponents/BaseWidget.vue'
export default {
  extends: baseWidget,
  mounted() {
    this.getServiceReports()
    this.getServiceCatalogs()
  },
  data() {
    return {
      moduleName: 'serviceRequest',
      reports: [],
      serviceCatalogs: [],
      dropdownOptions: [],
    }
  },
  computed: {
    tableData() {
      let data = []
      if (this.reports.length > 2) {
        data.push(this.reports[0])
        data.push(this.reports[1])
        return data
      }
      return this.reports
    },
  },
  methods: {
    redirectToSRSummary(id) {
      let { moduleName } = this
      let currentView = 'all_employee_portal'
      let route = findRouteForModule(moduleName, pageTypes.OVERVIEW)
      if (route) {
        this.$router.push({
          name: route.name,
          params: { viewname: currentView, id },
        })
      } else {
        console.warn('Could not resolve route')
      }
    },
    redirectTosServiceCategories(id) {
      let pathObj = findRouteForTab(tabTypes.SERVICE_CATALOG)

      if (id) {
        this.$router.push({
          path: `/employee/${pathObj.path}/${id}`,
        })
      } else {
        this.$router.push({
          path: `/employee/${pathObj.path}`,
        })
      }
    },
    async getServiceCatalogs() {
      // let url = 'v2/servicecatalog/list?page=1&perPage=50'
      let url = 'v2/servicecatalog/list?page=1&perPage=10'
      let { error, data } = await API.get(url)
      if (!error) {
        this.serviceCatalogs = data.serviceCatalogs
      }
      this.serviceCatalogs.forEach(cat => {
        let option = {
          name: cat.form.displayName,
          id: cat.id,
          img: cat.photoUrl,
        }
        this.dropdownOptions.push(option)
      })
    },
    async getServiceReports() {
      let url =
        'v3/modules/data/list?viewName=myallrequests&page=1&perPage=10&withCount=true&moduleName=serviceRequest'

      let { error, data } = await API.get(url)
      if (!error) {
        this.reports = data.serviceRequest
      }
    },
    redirectTo(val) {
      if (val == 'add-request') {
        let pathObj = findRouteForModule(this.moduleName, pageTypes.CREATE)
        this.$router.push({
          path: `/employee/${pathObj.path}`,
        })
      } else if (val == 'show-all') {
        let pathObj = findRouteForModule(this.moduleName, pageTypes.LIST)
        let path = pathObj.path.replaceAll(':viewname', 'all_employee_portal')

        this.$router.push({
          path: `/employee/${path}`,
        })
      }
    },
  },
}
</script>
<style lang="scss">
.portal-service-request-card {
  .el-table td.el-table__cell {
    border: none;
  }
  .el-table--enable-row-transition .el-table__body td {
    padding-top: 20px;
    padding-bottom: 20px;
    background-color: #fbfaff;
  }
  .el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell {
    background-color: #f7f5ff;
  }
}
.service-request-cat-add {
  border: 1px dotted transparent;
  display: flex;
  align-items: center;
  flex-direction: column;
  width: 65px;
  height: 80px;
  margin-right: 20px;
  border-radius: 4px;

  &:hover {
    background-color: #fbfaff;

    // .fc-circle {
    //   width: 40px;
    //   height: 40px;
    //   border-radius: 100%;
    //   background-color: #f4f4f4;
    //   display: flex;
    //   align-items: center;
    //   justify-content: center;
    //   cursor: pointer;
    // }
  }
}
.h100 {
  height: 100%;
}
.more-options {
  cursor: pointer;
}
.service-request-add:hover {
  background: #fbfaff;
}
.fc-request-list-block:hover {
  background: #f7f5ff;
}
.service-request-add {
  width: 65px;
  height: 80px;
  border: 1px dashed #cfcaff;
  border-radius: 4px;
  display: flex;
  align-items: center;
  cursor: pointer;
  flex-direction: column;
}
.fc-service-request-card {
  .el-card__body {
    padding: 0;
    height: 100%;
    position: relative;
  }
}
.fc-circle {
  width: 40px;
  height: 40px;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-add-circle {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-grey11 {
  font-size: 11px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #464646;
}
.fc-portal-blue-txt14 {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #483db6;
}
.fc-portal-black14 {
  font-size: 14px;
  font-weight: normal;
  line-height: 1.29;
  letter-spacing: normal;
  color: #12324a;
}
.fc-portal-black2-14 {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #12324a;
}
.fc-status-dot-green {
  width: 6px;
  height: 6px;
  background-color: #16aa00;
  border-radius: 10px;
  margin-top: auto;
  margin-bottom: auto;
}
.fc-status-dot-red {
  width: 6px;
  height: 6px;
  background-color: #c03000;
  border-radius: 10px;
}
.fc-request-list-block {
  background-color: #fbfaff;
  padding: 20px;
  display: flex;
  width: 100%;
}
.service-request-footer {
  padding: 30px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #0053cc;
  position: absolute;
  bottom: -10px;
  width: 100%;
}
.add-request {
  font-size: 11px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #324056;
}
</style>
