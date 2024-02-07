<template>
  <div class="properties-section">
    <div class="new-header-container pL30">
      <div class="new-header-text relative">
        <div class="fc-setup-modal-title">Properties</div>
        <div class="flex-middle">
          <div
            content="View Summary"
            v-if="!isPortal && localProperties.desk && localProperties.desk.id"
            v-tippy="{ arrow: true }"
            @click="openRecordSummary(localProperties.desk.id)"
          >
            <inline-svg
              src="svgs/external-link2"
              iconClass="icon icon-sm-md fill-blue mR10 pointer"
            ></inline-svg>
          </div>
          <div class="fc-setup-modal-close f18 pointer" @click="handleClose">
            <i class="el-icon-close fwBold"></i>
          </div>
        </div>
      </div>
    </div>
    <template
      v-if="
        localProperties.markerId &&
          localProperties.markerId.indexOf('desk') > -1
      "
    >
      <div class="new-body-modal infp-left-section height100 pL20 pR20 mT10">
        <!-- start -->
        <el-form ref="form" :model="localProperties" label-width="120px">
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="name" class="mB10 m0">
                <p class="fc-input-label-txt pB5">Name</p>
                <el-input
                  :disabled="true"
                  v-model="localProperties.label"
                  class="width100 fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-row class="mB10 mT20">
          <el-col :span="24">
            <div class="fc-black-14 text-left text-capitalize mB10">
              Desk type
            </div>
            <el-button
              class="fc-input-div-full-border width100 text-left line-height20 fc-btn-desk-type"
              disabled
            >
              {{ deskTypes[desk.deskType] || '' }}
            </el-button>
          </el-col>
        </el-row>

        <el-row class="mB10">
          <el-col :span="24">
            <div class="fc-black-14 text-left text-capitalize mB10 mT10">
              Department
            </div>
            <FLookupFieldWrapper
              v-model="localProperties.departmentId"
              :label="'Department'"
              @recordSelected="value => setDepartmentValue(value)"
              :field="{
                lookupModule: { name: 'department' },
                multiple: false,
              }"
              class="fc-department-icon fc-department-input rm-arrow"
            ></FLookupFieldWrapper>
          </el-col>
        </el-row>

        <el-row class="mB10 pT10">
          <el-col :span="24">
            <div class="fc-black-14 text-left text-capitalize mB10">
              Employee
            </div>
            <FLookupFieldWrapper
              class="rm-arrow"
              v-if="rerenderEmplyWizard"
              v-model="localProperties.employeeId"
              :label="'Employee'"
              @recordSelected="value => setValue(value)"
              :field="{
                lookupModule: { name: 'employee' },
                multiple: false,
              }"
              :filterConstruction="constructEmployeeFilter"
            ></FLookupFieldWrapper>
          </el-col>
        </el-row>
        <div class="fc-black-14 text-left mB10 f10" v-if="moveText">
          {{ moveText }}
        </div>
        <!-- end -->
        <!--
        <el-radio-group v-model="viewerMode" class="fp-mode-switch">
          <el-radio-button label="Unassign">
            <div class="flex-middle">
              <div class="pL5">Unassign</div>
            </div>
          </el-radio-button>
          <el-radio-button label="Done">
            <div class="flex-middle">
              <div class="pL5">
                Done
              </div>
            </div>
          </el-radio-button>
        </el-radio-group> -->
        <div class="text-center pT20"></div>

        <el-row class="text-center pT20">
          <el-col :span="20" v-if="showbtn">
            <el-button class="fc-pink-btn-small" size="small" @click="move"
              >Done Changes</el-button
            >
          </el-col>
          <el-col :span="20" v-else-if="departmentChange">
            <el-button class="fc-pink-btn-small" size="small" @click="update"
              >Done Changes</el-button
            >
          </el-col>
          <el-col :span="20" v-else>
            <el-button
              class="fc-pink-btn-small inactive"
              size="small"
              :disabled="true"
              >Done Changes</el-button
            >
          </el-col>
          <el-col :span="4" v-if="showdeskResetIcon">
            <el-tooltip
              class="item"
              effect="dark"
              content="Reset"
              placement="bottom"
            >
              <el-button
                size="small"
                @click="reset"
                class="fp-reset-icon"
                icon="el-icon-refresh-left"
              ></el-button>
            </el-tooltip>
          </el-col>
          <el-col :span="4" v-else>
            <el-tooltip
              class="item"
              effect="dark"
              content="Reset"
              placement="bottom"
            >
              <el-button
                size="small"
                :disabled="true"
                class="fp-reset-icon"
                icon="el-icon-refresh-left"
              ></el-button>
            </el-tooltip>
          </el-col>
        </el-row>
        <!-- <div class="text-center pT20">
          <el-button
            class="pT10 pB10"
            size="small"
            @click="unAssignDesk"
            v-if="unassign"
            >Unassign</el-button
          >
          <el-button
            class="fc-pink-btn-small"
            size="small"
            @click="move"
            v-if="showbtn"
            >Done Changes</el-button
          >
          <el-button
            class="fc-pink-btn-small"
            size="small"
            @click="update"
            v-else-if="departmentChange"
            >Done Changes</el-button
          >
        </div> -->
      </div>
    </template>
    <template v-else>
      <div class="new-body-modal infp-left-section height100 pL20 pR20 mT10">
        <el-form ref="form" :model="localProperties" label-width="120px">
          <el-row class="mB10">
            <el-col :span="24">
              <el-form-item prop="name" class="mB10 m0">
                <p class="fc-input-label-txt pB5">Name</p>
                <el-input
                  :disabled="true"
                  @change="updateMarker()"
                  v-model="localProperties.label"
                  class="width100 fc-input-full-border2"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row
            class="mB10 pT10"
            v-if="markerName === 'Locker' && moduleData"
          >
            <el-col :span="24">
              <div class="fc-black-14 text-left text-capitalize mB10">
                Employee
              </div>
              <FLookupFieldWrapper
                class="rm-arrow"
                v-model="localProperties.employeeId"
                :label="'Employee'"
                @recordSelected="value => setLockerValue(value)"
                :field="{
                  lookupModule: { name: 'employee' },
                  multiple: false,
                }"
              ></FLookupFieldWrapper>
            </el-col>
          </el-row>

          <el-row class="text-center pT20">
            <el-col
              :span="20"
              v-if="markerName === 'Locker' && moduleData && showbtn"
            >
              <div>
                <el-button
                  class="fc-pink-btn-small"
                  size="small"
                  @click="doneLocker"
                  >Done Changes</el-button
                >
              </div>
            </el-col>
            <el-col :span="4">
              <el-col :span="4" v-if="showdeskResetIcon">
                <el-tooltip
                  class="item"
                  effect="dark"
                  content="Reset"
                  placement="bottom"
                >
                  <el-button
                    size="small"
                    @click="reset"
                    class="fp-reset-icon"
                    icon="el-icon-refresh-left"
                  ></el-button>
                </el-tooltip>
              </el-col>
              <el-col :span="4" v-else>
                <el-tooltip
                  class="item"
                  effect="dark"
                  content="Reset"
                  placement="bottom"
                >
                  <el-button
                    size="small"
                    :disabled="true"
                    class="fp-reset-icon"
                    icon="el-icon-refresh-left"
                  ></el-button>
                </el-tooltip>
              </el-col>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </template>
  </div>
</template>
<script>
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { API } from '@facilio/api'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  props: [
    'properties',
    'visible',
    'employeeList',
    'departments',
    'selectedFeature',
    'isPortal',
    'defaultMarkerIdVsName',
  ],
  components: { FLookupFieldWrapper },
  data() {
    return {
      rerenderEmplyWizard: true,
      unassign: true,
      viewerMode: 'unassign',
      defaultEmployee: null,
      departmentChange: false,
      moveText: null,
      viewname: 'alldesks',
      showbtn: false,
      deskType: { 1: 'Assignable', 2: 'Hoteling', 3: 'Hot' },
      element: {
        id: null,
      },
      localProperties: {},
      localSelectedFeature: {},
      deskFields: [],
    }
  },
  computed: {
    deskTypes() {
      if (this.deskFields.length) {
        let desktypefield = this.deskFields.find(rt => rt.name === 'deskType')
        if (desktypefield?.enumMap) {
          return desktypefield.enumMap
        }
      }
      return ''
    },
    showdeskResetIcon() {
      if (this.showbtn || this.departmentChange) {
        return true
      }
      return false
    },
    selectedDepartment() {
      if (this.localProperties?.departmentId) {
        return (
          this.departments.find(
            rt => rt.id === this.localProperties.departmentId
          ) || null
        )
      }
      return null
    },
    markerName() {
      if (this.localSelectedFeature) {
        let { markerType } = this.localSelectedFeature
        if (markerType && markerType.id) {
          return this.defaultMarkerIdVsName[markerType.id]
        }
      }
      return null
    },
    moduleData() {
      if (this.localSelectedFeature && this.localSelectedFeature.moduleData) {
        return this.localSelectedFeature.moduleData
      }
      return null
    },
    desk() {
      if (this.localSelectedFeature && this.localSelectedFeature.desk) {
        return this.localSelectedFeature.desk
      }
      return null
    },
    FormatedEmployeeList() {
      if (this.employeeList) {
        return [...this.employeeList, { name: 'None', id: -1 }]
      }
      return [{ name: 'none', id: -1 }]
    },
    formatedDepartmentList() {
      let data = []
      if (this.departments && this.departments.length) {
        data = this.departments
      }
      return [...data, { name: 'none', id: -1 }]
    },
    propertyEmployeeId() {
      if (this.localProperties && this.localProperties.employeeId) {
        return this.localProperties.employeeId
      }
      return -1
    },
    defaultEmployeeDepartMent() {
      if (
        this.defaultEmployee &&
        this.defaultEmployee.department &&
        this.defaultEmployee.department.id
      ) {
        return this.defaultEmployee.department.id
      }
      return -1
    },
  },
  watch: {
    propertyEmployeeId: {
      immediate: true,
      handler() {
        this.fetchDefaultEmployee()
      },
    },
  },
  mounted() {
    this.loadDeskFields()
    this.cloneProps()
    this.fetchDefaultEmployee()
  },
  methods: {
    freezePropertieDialog() {
      this.moveText = null
      this.showbtn = false
      this.departmentChange = false
      this.rerenderEmplyWizard = false
      this.$nextTick(() => {
        this.rerenderEmplyWizard = true
      })
    },
    updateProps() {
      Object.keys(this.localProperties).forEach(key => {
        this.$set(
          this.properties,
          key,
          this.$helpers.cloneObject(this.localProperties[key])
        )
      })

      Object.keys(this.localSelectedFeature).forEach(key => {
        this.$set(
          this.selectedFeature,
          key,
          this.$helpers.cloneObject(this.localSelectedFeature[key])
        )
      })
      this.selectedFeature.properties = this.$helpers.cloneObject(
        this.localProperties
      )
    },
    cloneProps() {
      this.$set(
        this,
        'localProperties',
        this.$helpers.cloneObject(this.properties)
      )
      this.$set(
        this,
        'localSelectedFeature',
        this.$helpers.cloneObject(this.selectedFeature)
      )
    },
    reset() {
      this.moveText = null
      this.cloneProps()
      this.showbtn = false
      this.departmentChange = false
      this.rerenderEmplyWizard = false
      this.$nextTick(() => {
        this.rerenderEmplyWizard = true
      })
    },
    constructEmployeeFilter() {
      if (this.localProperties?.departmentId) {
        return {
          department: {
            operatorId: 36,
            value: [`${this.localProperties.departmentId}`],
          },
        }
      }
      return {}
    },
    setLockerValue(value) {
      this.showbtn = true
      if (value.value) {
        this.localProperties.employeeId = value.value
        this.$set(this.localProperties, 'secondaryLabel', value.label)
        this.localSelectedFeature.moduleData.employee = {
          id: value.value,
          name: value.label,
        }
      } else {
        delete this.localSelectedFeature.moduleData.employee
        this.localProperties.employeeId = null
        this.$set(this.localProperties, 'secondaryLabel', '')
      }
    },
    doneLocker() {
      this.updateProps()
      this.setIcon()
      this.$emit('updatelocker', this.localProperties)
      this.freezePropertieDialog()
      this.updateLocker(this.localSelectedFeature.moduleData)
    },
    async updateLocker(marker) {
      let params = {
        moduleName: 'lockers',
        data: marker,
        id: marker.id,
      }
      let { data, error } = await API.post(`v3/modules/data/update`, params)
      if (data) {
        this.$message.success('Updated')
      }
      if (error) {
        this.$message.error('Failed')
      }
    },
    setIcon() {
      let { localSelectedFeature, localProperties } = this
      let name = this.defaultMarkerIdVsName[localSelectedFeature.markerType.id]
      if (
        localSelectedFeature.moduleData &&
        localSelectedFeature.moduleData.employee
      ) {
        this.$set(localProperties, 'normalClass', name)
        this.$set(localProperties, 'markerId', name)
        this.$set(localProperties, 'activeClass', `${name}_active`)
      } else {
        name += `_inactive`
        this.$set(localProperties, 'normalClass', name)
        this.$set(localProperties, 'markerId', name)
        this.$set(localProperties, 'activeClass', `${name}_active`)
      }
    },
    updateMarker() {
      this.updateProps()
      this.$emit('updatemarker', this.localProperties)
    },

    setValue(value) {
      this.departmentChange = false

      if (value.value) {
        this.localProperties.employeeId = value.value
        this.$set(this.localProperties, 'secondaryLabel', value.label)
        if (this.localSelectedFeature.desk) {
          this.$set(this.localSelectedFeature.desk, 'employee', {
            id: value.value,
            name: value.label,
          })
        } else {
          this.$set(this.localSelectedFeature, 'desk', {
            employee: { id: value.value, name: value.label },
          })
        }

        this.fetchEmployee(value.value)
        this.fetchEmployeeFulldetails(value.value)
      } else {
        this.unAssignDesk()
      }
    },
    unAssignDesk() {
      this.moveText = `${this.localProperties.secondaryLabel} will be unassigned from this desk`
      this.localProperties.employeeId = null
      this.$set(this.localProperties, 'secondaryLabel', '')
      if (this.localProperties?.desk?.employee) {
        delete this.localProperties.desk.employee
      }
      if (this.localProperties?.employee) {
        delete this.localProperties.employee
      }
      if (this.localSelectedFeature?.employee) {
        delete this.localSelectedFeature.employee
      }

      // delete this.localSelectedFeature.employee
      this.showbtn = true
      this.unassign = false
    },
    async fetchEmployeeFulldetails(id) {
      let { data } = await API.get(
        `v2/servicePortalHome?fetchOnlyDesk=true&count=1&recordId=${id}`
      )

      if (data && data.desks && data.desks.length) {
        this.loadMoveDetails(data.desks[0])
      }
    },
    loadMoveDetails(desks) {
      if (desks) {
        let floorName =
          desks.floor && desks.floor.name ? desks.floor.name : null
        this.moveText = `Employee will be moved from ${desks.name} ${
          floorName ? ', ' + floorName : ''
        } to this desk`
      }
    },
    async fetchEmployee(id) {
      let params = {
        moduleName: 'employee',
        id: id,
      }
      let { data } = await API.post(`v3/modules/data/summary`, params)
      if (data && data.employee) {
        this.setEmployeebackupProperties(data.employee)
        this.showbtn = true
      }
    },
    async fetchDefaultEmployee() {
      if (this.localProperties.employeeId) {
        let params = {
          moduleName: 'employee',
          id: this.localProperties.employeeId,
        }
        let { data } = await API.post(`v3/modules/data/summary`, params)
        if (data && data.employee) {
          this.defaultEmployee = data.employee
        }
      }
    },
    assignbackupProperties(employee) {
      this.localProperties.employeeId = employee.id
      this.$set(this.localSelectedFeature, 'employee', employee)
      this.$set(this.localProperties, 'secondaryLabel', employee.name)
      if (employee && employee.department && employee.department.id) {
        this.$set(
          this.localProperties,
          'departmentId',
          Number(employee.department.id)
        )
        this.$set(this.localSelectedFeature, 'department', employee.department)
      } else {
        if (this.localProperties.departmentId) {
          delete this.localProperties.departmentId
          delete this.localSelectedFeature.department
        }
      }
    },
    setEmployeebackupProperties(employee) {
      if (
        this.localProperties.departmentId &&
        employee.department &&
        employee.department.id
      ) {
        if (this.localProperties.departmentId !== employee.department.id) {
          this.$confirm(
            'The department of the employee not matching with the department of desk, are you sure want to assign?',
            {
              title: 'Confirm department change',
              confirmButtonText: 'OK',
              cancelButtonText: 'CANCEL',
              cancelButtonClass: 'msg-cancel-btn',
              confirmButtonClass: 'msg-confirm-btn',
              customClass: 'fc-el-msgBox',
              type: 'warning',
            }
          )
            .then(() => {
              this.unassign = true
              this.showbtn = true
              this.assignbackupProperties(employee)
            })
            .catch(() => {
              this.showbtn = false
              this.departmentChange = false
            })
        }
      } else {
        this.assignbackupProperties(employee)
      }
    },
    setDepartmentValue(value) {
      if (value.value) {
        this.rerenderEmplyWizard = false
        this.localProperties.departmentId = value.value

        this.$set(
          this.localSelectedFeature.desk,
          'department',
          this.selectedDepartment
        )
        this.departmentChange = true
        this.localProperties.employeeId = null
        this.$set(this.localProperties, 'secondaryLabel', '')
        this.$nextTick(() => {
          this.rerenderEmplyWizard = true
        })
      } else {
        this.departmentChange = true
        this.localProperties.departmentId = null
        if (this.localProperties.department) {
          delete this.localProperties.department
        }
        if (this.localProperties.desk?.department) {
          delete this.localProperties.desk.department
        }
      }
    },
    // setDepartmentValue(value) {
    //   this.rerenderEmplyWizard = false
    //   if (
    //     value.value &&
    //     this.defaultEmployeeDepartMent > -1 &&
    //     this.defaultEmployeeDepartMent !== value.value
    //   ) {
    //     this.$confirm(
    //       'The department of assigned employee is different, are you sure want to change the department?',
    //       {
    //         title: 'Confirm department change',
    //         confirmButtonText: 'OK',
    //         cancelButtonText: 'CANCEL',
    //         cancelButtonClass: 'msg-cancel-btn',
    //         confirmButtonClass: 'msg-confirm-btn',
    //         customClass: 'fc-el-msgBox',
    //         type: 'warning',
    //       }
    //     )
    //       .then(() => {
    //         this.localProperties.departmentId = value.value

    //         this.$set(
    //           this.localSelectedFeature.desk,
    //           'department',
    //           this.selectedDepartment
    //         )
    //         this.departmentChange = true
    //         this.localProperties.employeeId = null
    //         this.$set(this.localProperties, 'secondaryLabel', '')
    //       })
    //       .catch(() => {
    //         this.departmentChange = false
    //       })
    //   } else {
    //     this.departmentChange = true
    //   }
    //   this.$nextTick(() => {
    //     this.rerenderEmplyWizard = true
    //   })
    // },
    move() {
      this.updateProps()
      if (this.localProperties.employeeId) {
        this.$emit('move', this.localProperties)
      } else {
        this.$emit('unassignDesk', this.localProperties)
      }
      this.freezePropertieDialog()
    },
    handleClose() {
      this.showbtn = false
      this.departmentChange = false
      this.rerenderEmplyWizard = false
      this.$nextTick(() => {
        this.rerenderEmplyWizard = true
      })
      this.$emit('update:visible', false)
      this.$emit('close')
    },
    update() {
      this.updateProps()
      this.$emit('update', this.localProperties)
      if (
        !this.localProperties.employeeId &&
        this.defaultEmployee &&
        this.defaultEmployee.id
      ) {
        this.$emit('unassignDesk', this.localProperties)
      }
      this.freezePropertieDialog()
    },
    openRecordSummary({ id }) {
      let { viewname } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('desks', pageTypes.OVERVIEW) || {}

        if (name) {
          let routeData = this.$router.resolve({
            name,
            params: {
              viewname,
              id,
            },
          })
          window.open(routeData.href, '_blank')
        }
      } else {
        let routeData = this.$router.resolve({
          name: 'deskSummary',
          params: {
            viewname,
            id,
          },
        })
        window.open(routeData.href, '_blank')
      }
    },
    async loadDeskFields() {
      let params = {
        moduleName: 'desks',
      }
      let { data } = await API.get(`v2/modules/fields/fields`, params)
      if (data?.fields) {
        this.deskFields = data.fields
      }
    },
  },
}
</script>
<style scoped>
.fc-btn-desk-type {
  padding-top: 9px;
}
.fc-pink-btn-small {
  text-transform: unset;
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.36px;
  text-align: center;
  width: 100%;
}
.fc-pink-btn-small.inactive {
  text-transform: unset;
  letter-spacing: 0.2px;
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.36px;
  text-align: center;
  color: #8c8c8c;
  background: #8c8c8c;
  background-color: #ededed;
  border-color: #ededed;
}
</style>
<style lang="scss">
.rm-arrow {
  .el-select .el-input .el-icon-arrow-up,
  .el-select .el-input .el-icon-arrow-down {
    display: none;
  }
}
.fp-mode-switch {
  .el-radio-button__inner {
    padding: 7px 20px;
    border: 1px solid #efefef;
    color: #324056;
  }

  .el-radio-button__orig-radio:checked + .el-radio-button__inner {
    background: #ff3184 !important;
    border-color: #ff3184 !important;
    box-shadow: -1px 0 0 0 #ff3184;
  }
}
.fc-department-input {
  .el-input {
    width: 100%;
    border: 1px solid #d0d9e2 !important;
    border-radius: 4px;
    .el-input__inner {
      width: 80%;
      border: none !important;
    }
    .el-input__suffix {
      .el-icon-circle-close {
        position: relative;
        left: 20px;
        padding-left: 50px !important;
        font-size: 18px;
      }
    }
  }
}
.fp-reset-icon {
  padding: 10px;
  font-size: 15px;
  font-weight: 800;
  margin: 0px;
}
</style>
