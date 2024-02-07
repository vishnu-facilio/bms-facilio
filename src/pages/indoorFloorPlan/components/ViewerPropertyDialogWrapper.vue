<template>
  <div class="properties-section">
    <spinner :show="loading" size="80" v-if="loading"></spinner>
    <template v-else>
      <div class="new-header-container pL30">
        <div class="new-header-text relative">
          <div class="fc-setup-modal-title">Properties</div>
          <div class="flex-middle">
            <div
              content="View Summary"
              v-if="!isPortal && moduleName === 'desks'"
              v-tippy="{ arrow: true }"
              @click="openRecordSummary(record, moduleName)"
            >
              <inline-svg
                src="svgs/external-link2"
                iconClass="icon icon-sm-md fill-blue mR10 pointer"
              ></inline-svg>
            </div>
            <div
              content="View Summary"
              v-else-if="summaryUrl"
              v-tippy="{ arrow: true }"
              @click="openUrl(this.summaryUrl)"
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
      <template v-if="moduleName && moduleName === 'desks' && record">
        <div class="new-body-modal infp-left-section height100 pL20 pR20 mT10">
          <!-- start -->
          <el-form ref="form" :model="record" label-width="120px">
            <el-row class="mB10">
              <el-col :span="24">
                <el-form-item prop="name" class="mB10 m0">
                  <p class="fc-input-label-txt pB5">Name</p>
                  <el-input
                    :disabled="true"
                    v-model="record.name"
                    class="width100 fc-input-full-border2"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <el-row class="mB10 mT20">
            <el-col :span="24">
              <div class="fc-black-14 text-left text-capitalize mB10">
                {{ deskType }}
              </div>
              <el-button
                class="fc-input-div-full-border width100 text-left line-height20 fc-btn-desk-type"
                disabled
              >
                {{ record.deskTypeVal || '' }}
              </el-button>
            </el-col>
          </el-row>

          <el-row class="mB10" v-if="record.deskType === 1">
            <el-col :span="24">
              <div class="fc-black-14 text-left text-capitalize mB10 mT10">
                {{ department }}
              </div>
              <FLookupFieldWrapper
                v-model="record.department.id"
                :label="department"
                @recordSelected="value => setDepartmentValue(value)"
                :field="{
                  lookupModule: { name: 'department' },
                  multiple: false,
                }"
                class="fc-department-icon fc-department-input rm-arrow"
              ></FLookupFieldWrapper>
            </el-col>
          </el-row>

          <el-row class="mB10 pT10" v-if="record.deskType === 1">
            <el-col :span="24">
              <div class="fc-black-14 text-left text-capitalize mB10">
                {{ employee }}
              </div>
              <FLookupFieldWrapper
                v-if="rerenderEmplyWizard"
                class="rm-arrow"
                v-model="record.employee.id"
                :label="employee"
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
          <div class="text-center pT20"></div>

          <el-row class="text-center pT20">
            <el-col :span="20" v-if="showbtn">
              <el-button class="fc-pink-btn-small" size="small" @click="move">{{
                doneChanges
              }}</el-button>
            </el-col>
            <el-col :span="20" v-else-if="departmentChange">
              <el-button
                class="fc-pink-btn-small"
                size="small"
                @click="update"
                >{{ doneChanges }}</el-button
              >
            </el-col>
            <el-col :span="20" v-else>
              <el-button
                class="fc-pink-btn-small inactive"
                size="small"
                :disabled="true"
                >{{ doneChanges }}</el-button
              >
            </el-col>
            <el-col :span="4" v-if="showbtn">
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
        </div>
      </template>
      <template v-else-if="object">
        <div class="new-body-modal infp-left-section height100 pL20 pR20 mT10">
          <!-- start -->
          <el-form ref="form" :model="object" label-width="120px">
            <el-row class="mB10">
              <el-col :span="24">
                <el-form-item prop="name" class="mB10 m0">
                  <p class="fc-input-label-txt pB5">Name</p>
                  <el-input
                    :disabled="true"
                    v-model="object.label"
                    class="width100 fc-input-full-border2"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <!-- end -->
          <div class="text-center pT20"></div>

          <el-row class="text-center pT20">
            <el-col :span="20">
              <el-button
                class="fc-pink-btn-small inactive"
                size="small"
                :disabled="true"
                >{{ doneChanges }}</el-button
              >
            </el-col>
            <el-col :span="4">
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
        </div>
      </template>
    </template>
  </div>
</template>
<script>
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { API } from '@facilio/api'
import { isEqual, isEmpty } from 'lodash'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  components: { FLookupFieldWrapper },
  props: {
    objectId: {
      type: Number,
      required: true,
    },
    isPortal: {
      type: Boolean,
      required: false,
      default: false,
    },
    visible: {
      type: Boolean,
      required: true,
    },
    siteId: {
      type: Number,
      required: false,
    },
  },
  data() {
    return {
      rerenderEmplyWizard: true,
      deskType: 'Desk type',
      department: 'Department',
      employee: 'Employee',
      doneChanges: 'Done Changes',
      departmentChange: false,
      showdeskResetIcon: false,
      propertiesData: {},
      record: {},
      moduleName: null,
      moveText: '',
      moveData: {},
      fromDesk: null,
      loading: true,
      btnLoading: false,
      object: null,
      btnState: true,
    }
  },
  computed: {
    summaryUrl() {
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          return this.$router.resolve({
            name,
            params: {
              viewname: 'all',
              id: this.record.id,
            },
          }).href
        }
      }
      return null
    },
    showbtn() {
      if (this.btnLoading || !this.btnState) {
        return false
      }
      if (isEqual(this.record, this.formatRecord(this.propertiesData.record))) {
        return false
      }
      return true
    },
  },
  mounted() {
    this.init()
  },
  watch: {
    objectId: {
      handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.init()
        }
      },
    },
  },
  methods: {
    getModelData() {},
    reset() {
      this.moveText = ''
      this.record = this.$helpers.cloneObject(
        this.formatRecord(this.propertiesData.record) || {}
      )
    },
    move() {
      if (!isEmpty(this.moveData)) {
        this.updateMoveData(this.moveData).then(() => {
          this.$emit('refresh')
          this.getPropertiesData(true)
        })
      } else {
        this.updateRecord().then(() => {
          this.$emit('refresh')
          this.getPropertiesData(true)
        })
      }
    },
    prepareMoveData(move) {
      let timeOfMove = new Date().valueOf()
      let moveParmas = {}
      if (move.fromDesk && move.toDesk) {
        this.$set(moveParmas, 'to', { id: move.toDesk.id })
        this.$set(moveParmas, 'from', { id: move.fromDesk.id })
        this.$set(moveParmas, 'timeOfMove', timeOfMove)
        if (move.toDesk.employee) {
          if (move.toDesk?.department?.id) {
            this.$set(moveParmas, 'department', {
              id: move.toDesk.department.id,
            })
          }
          this.$set(moveParmas, 'employee', move.toDesk.employee)
        }
      } else if (move.fromDesk && !move.toDesk) {
        this.$set(moveParmas, 'from', { id: move.fromDesk.id })
        this.$set(moveParmas, 'timeOfMove', timeOfMove)
        if (move.fromDesk.employee) {
          this.$set(moveParmas, 'employee', { id: move.fromDesk.employee.id })
          if (move.fromDesk?.department?.id) {
            this.$set(moveParmas, 'department', {
              id: move.fromDesk.department.id,
            })
          }
        }
      } else if (move.toDesk && !move.fromDesk) {
        this.$set(moveParmas, 'to', { id: move.toDesk.id })
        this.$set(moveParmas, 'timeOfMove', timeOfMove)
        if (move.toDesk.employee) {
          this.$set(moveParmas, 'employee', move.toDesk.employee)
          if (move.toDesk?.department?.id) {
            this.$set(moveParmas, 'department', {
              id: move.toDesk.department.id,
            })
          }
        }
      }
      this.$set(moveParmas, 'scheduledTime', null)
      this.$set(moveParmas, 'moveType', 1)
      this.$set(moveParmas, 'siteId', this.siteId)
      return moveParmas
    },
    update() {
      this.updateRecord().then(() => {
        this.$emit('refresh')
        this.getPropertiesData(true)
      })
    },
    constructEmployeeFilter() {
      if (this.record?.department?.id) {
        return {
          department: {
            operatorId: 36,
            value: [`${this.record.department.id}`],
          },
        }
      }
      return {}
    },
    async setValue(value) {
      this.moveText = ''
      if (value?.value && value.value > 0) {
        await this.fetchEmployeeFulldetails(value.value)
        this.moveData = this.prepareMoveData(this.getMoveData(value))
      } else {
        this.unassign()
      }
    },
    unassign() {
      let record = this.$helpers.cloneObject(
        this.formatRecord(this.propertiesData.record)
      )
      if (record?.employee?.name) {
        this.moveText = `${this.record.employee.name} will be unassigned from this desk`
      } else {
        this.moveText = ''
      }
      this.fromDesk = record
      this.moveData = this.prepareMoveData(this.getMoveData())
    },
    getMoveData(toEmployee) {
      let toDesk = toEmployee ? this.record || null : null
      let fromDesk = this.fromDesk || null
      if (toEmployee?.value) {
        if (toDesk) {
          this.$set(toDesk, 'employee', { id: toEmployee.value })
        }
      }
      return {
        toDesk: toDesk,
        fromDesk: fromDesk,
      }
    },
    setDepartmentValue() {
      let { record } = this
      this.moveText = ''
      record.employee = {
        id: null,
      }
      this.rerenderEmplyWizard = false
      this.$nextTick(() => {
        this.rerenderEmplyWizard = true
      })
    },
    handleClose() {
      this.$emit('update:visible', false)
      this.$emit('close')
    },
    init() {
      this.getPropertiesData()
    },
    async getPropertiesData(loadingfalse) {
      this.moveText = ''
      let params = {
        objectId: this.objectId,
      }
      this.btnLoading = true

      if (!loadingfalse) {
        this.loading = true
      }
      await API.get(`/v3/floorplan/propertiesdata`, params)
        .then(({ data }) => {
          if (data?.properties) {
            this.propertiesData = this.$helpers.cloneObject(data.properties)
            if (data.properties.moduleName) {
              this.moduleName = this.$helpers.cloneObject(
                data.properties.moduleName
              )
            }
            if (data.properties?.record) {
              this.record = this.$helpers.cloneObject(
                this.formatRecord(data.properties.record) || {}
              )
            }

            this.object = this.$helpers.cloneObject(
              data.properties.object || {}
            )
          }
          this.loading = false
          this.btnLoading = false
        })
        .catch(error => {
          this.loading = false
          this.btnLoading = false
          if (error) {
            this.$message.error(`Error While fetching the data`)
          }
        })
    },
    formatRecord(record) {
      if (this.moduleName === 'desks') {
        if (!record?.department) {
          this.$set(record, 'department', { id: null })
        }
        if (!record?.employee) {
          this.$set(record, 'employee', { id: null })
        }
      } else if (this.moduleName === 'lockers') {
        if (!record?.employee) {
          this.$set(record, 'employee', { id: null })
        }
      }
      return record
    },
    async fetchEmployeeFulldetails(id) {
      this.btnState = false
      let { data } = await API.get(
        `v2/servicePortalHome?fetchOnlyDesk=true&count=1&recordId=${id}`
      )

      if (data && data.desks && data.desks.length) {
        this.loadMoveDetails(data.desks[0])
        this.setDepartment(data.desks[0])
      }
      this.btnState = true
    },
    setDepartment(desk) {
      if (desk?.department?.id && !this.record?.department?.id) {
        this.record.department = { id: desk.department.id }
      }
    },
    loadMoveDetails(desk) {
      this.fromDesk = null
      if (desk) {
        this.fromDesk = desk
        let floorName = desk.floor && desk.floor.name ? desk.floor.name : null
        this.moveText = `Employee will be moved from ${desk.name} ${
          floorName ? ', ' + floorName : ''
        } to this desk`
      }
    },
    async updateMoveData(move) {
      this.btnState = false
      let params = {
        data: move,
        moduleName: 'moves',
      }
      await API.post(`v3/modules/data/create`, params)
      this.btnState = true
    },
    async updateRecord() {
      this.btnState = false
      let params = {
        moduleName: this.moduleName,
        id: this.record.id,
        data: this.record,
      }
      let { data, error } = await API.post(`v3/modules/data/update`, params)
      if (error) {
        this.$message.error(`${error.message}`)
      } else if (data) {
        this.$message.success(`${this.record.name} updated`)
      }
      this.btnState = true
    },
    openUrl(url) {
      window.open(url, '_blank')
    },
    openRecordSummary({ id }) {
      // let { viewname } = this
      let viewname = 'all' // default view name should be dynamic
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
