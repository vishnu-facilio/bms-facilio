<template>
  <div class="emp-search-container ">
    <div class="position-relative">
      <div
        class="fc-search-bottom-border inline width100 inline-flex self-center"
      >
        <el-input
          size="medium"
          v-model="searchText"
          @input="searchContent()"
          @focus="
            () => {
              handleFocusSwicth()
            }
          "
          @blur="blurSearchBox()"
          class="width100 fc-input-full-border2 fc-input-full-border2-prefix"
          placeholder="Search"
          clearable
        >
          <i
            slot="prefix"
            class="el-input__icon pointer fc-grey8 f14 fwBold el-icon-search mR5"
          ></i>
        </el-input>
      </div>

      <div class="search-result-component z-10">
        <IndoorFloorplanObjectList
          :showSearchList="showSearchList"
          :searchText="searchText"
          :employeesList="employeesList"
          @actionEmployee="actionEmployee"
          :isLoading="isLoading"
        ></IndoorFloorplanObjectList>
      </div>
    </div>
    <el-dialog
      title="SWITCH FLOOR"
      :visible.sync="openFloorPlanDialog"
      width="30%"
      :before-close="closeDialog"
      class="fc-dialog-center-container"
      :append-to-body="true"
    >
      <div class="height150">
        <div class="label-txt-black" v-if="openfloorplanData">
          <span v-if="openfloorplanData.loading">
            <spinner :show="true" size="80"></spinner>
          </span>
          <span v-else style="    word-break: normal;">
            {{ openfloorplanData.message }}
          </span>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="openFloorPlanDialog = false" class="modal-btn-cancel"
          >{{ 'CANCEL' }}
        </el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="openFloorPlan()"
          >{{ 'SWITCH' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import IndoorFloorplanObjectList from 'src/pages/indoorFloorPlan/IndoorFloorplanObjectList.vue'
import { API } from '@facilio/api'
import debounce from 'lodash/debounce'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'

export default {
  props: ['floorId'],
  components: { IndoorFloorplanObjectList },

  data() {
    return {
      employeesList: [],
      showSearchList: false,
      searchText: null,
      isLoading: false,
      openFloorPlanDialog: false,
      openfloorplanData: null,
      employeLoading: false,
    }
  },
  mounted() {
    this.getEmployeeApi()
  },
  methods: {
    async handleFocusSwicth() {
      this.showSearchList = true
      await this.employeeSearchList(this.searchText)
    },
    blurSearchBox() {
      // its due to @blur trigger before the click action
      this.$helpers.delay(200).then(() => {
        this.showSearchList = false
      })
    },
    searchContent: debounce(function() {
      this.employeeSearchList(this.searchText)
    }, 100),

    async employeeSearchList(searchText) {
      this.isLoading = true
      this.employeesList = []
      let params = {}
      let filters = {}
      filters['name'] = { operatorId: 5, value: [searchText] }
      params = {
        moduleName: 'employee',
        page: 1,
        perPage: 20,
        includeParentFilter: true,
      }
      if (searchText) {
        params.filters = JSON.stringify(filters)
      }
      this.employeLoading = true

      await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.employeesList = data.moduleDatas
        }
        this.employeLoading = false
      })
      this.isLoading = false
    },
    async getEmployeeApi() {
      this.employeesList = []
      let params = {
        moduleName: 'employee',
        page: 1,
        perPage: 20,
        includeParentFilter: true,
      }
      this.employeLoading = true

      let { data } = await API.get(`v2/module/data/list`, params)
      if (data) {
        this.employeesList = data.moduleDatas
      }
      this.employeLoading = false
    },
    openFloorPlan() {
      if (this.openfloorplanData && this.openfloorplanData.indoorFloorPlanId) {
        this.$router.push({
          path: `/employee/floorplan/floor-map/indoorfloorplan/${this.openfloorplanData.floorId}/${this.openfloorplanData.indoorFloorPlanId}`,
          query: { deskId: this.openfloorplanData.deskId },
        })
        // window.open(routeData.href, '_blank')
        this.closeDialog()
      }
    },
    closeDialog() {
      this.openFloorPlanDialog = false
    },
    actionEmployee(employee) {
      this.openfloorplanData = {}
      if (employee && employee.space && employee.space.id) {
        let { floor } = employee.space
        if (floor && floor.id) {
          if (floor.id === this.floorId) {
            this.emitData('desks', employee.space)
          } else {
            let data = {
              deskId: employee.space.id,
              deskName: employee.space.name,
              employeeName: employee.name,
              floorId: floor.id,
              message: '',
              loading: true,
              indoorFloorPlanId: -1,
            }
            this.openfloorplanData = data

            this.openFloorPlanDialog = true
            this.getFloor()
          }
        }
      }
    },
    async getFloor() {
      let { floorId } = this.openfloorplanData

      let params = { floorId: floorId }

      let { data } = await API.post(`v2/floor/details`, params)
      if (data) {
        let { floor } = data
        let { building } = floor
        this.openfloorplanData.indoorFloorPlanId = floor.indoorFloorPlanId
        let { employeeName } = this.openfloorplanData
        this.openfloorplanData.message = `${employeeName} is located at ${
          floor.name
        } ${
          building && building.name ? ', ' + building.name : ''
        }. Do you want to switch the floor plan?`
      }
      this.openfloorplanData.loading = false
    },
    emitData(modulesName, data) {
      // this.$emit('focus', modulesName, data)
      eventBus.$emit('focusMarker', modulesName, data)
    },
  },
}
</script>
<style>
.emp-search-container .fc-input-full-border2 .el-input__inner:hover,
.emp-search-container .fc-input-full-border2 .el-input__inner:focus {
  border-color: #0053cc !important;
}
.emp-search-container .fc-input-full-border2 .el-input__inner {
  height: 36px !important;
  border-radius: 4px !important;
}
.emp-search-container {
  width: 250px;
  margin-right: 20px;
  height: 36px;
}
.search-result-component {
  position: absolute;
  left: -25px;
}
</style>
