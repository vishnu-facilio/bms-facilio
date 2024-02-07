<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog60 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <div class="new-header-container pL20 pR20">
        <div class="new-header-text flex-middle justify-content-space">
          <div class="fc-setup-modal-title">
            Setup
          </div>
          <div class="flex-middle">
            <div class="fc-black-12" v-if="syncData.lastSyncedTime > 0">
              Last Synced on
              {{ syncData.lastSyncedTime | formatDate() }}
            </div>
            <el-button class="setup-el-btn text-right mL10" @click="energySync">
              Sync
            </el-button>
            <div class="pL15 pointer" @click="closeDialog">
              <i class="el-icon-close fc-black-12 f20"></i>
            </div>
          </div>
        </div>
      </div>
      <div class="new-body-modal p0 mT0">
        <div v-if="loading" class="flex-middle fc-empty-white">
          <spinner :show="loading" size="80"></spinner>
        </div>
        <div
          v-if="$validation.isEmpty(energyDatas) && !loading"
          class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column"
        >
          <inline-svg
            src="svgs/emptystate/reportlist"
            iconClass="icon text-center icon-xxxlg"
          ></inline-svg>
          <div class="nowo-label">
            No Property available
          </div>
        </div>
        <el-table
          v-if="!loading && !$validation.isEmpty(energyDatas)"
          :data="energyDatas"
          style="width: 100%"
          class="fc-table-th-minus fc-energy-setup-main-table"
        >
          <el-table-column prop="property" label="PROPERTY" width="180">
            <template v-slot="energyData">
              <div>
                {{ parseProperty(energyData.row.meta) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="meters" label="NO. OF METERS" width="160">
            <template v-slot="energyData">
              <div>
                {{ energyData.row.meterContexts.length }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="mapped" label="MAPPED BUILDING" width="180">
            <template v-slot="energyData">
              <div v-if="energyData.row.buildingId > 0">
                {{
                  energyData.row.data.building.name
                    ? energyData.row.data.building.name
                    : '---'
                }}
              </div>
              <div v-else class="fc-warning2 f14">
                Unmapped
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="mapped" label="Status" width="160">
            <template v-slot="energyData">
              <div v-if="energyData.row.configured === true">
                <el-button class="success-green-btn pL7 pR7"
                  ><i class="el-icon-check pR5 fwBold"></i>Configured
                </el-button>
              </div>
              <div v-else-if="energyData.row.configured === false">
                <el-button
                  class="fc-add-border-green-btn"
                  @click="configurationDialog(energyData.row)"
                >
                  Configure</el-button
                >
              </div>
            </template>
          </el-table-column>
          <el-table-column>
            <template v-slot="energyData">
              <div
                class="fc-blue-txt4-12 f14 bold"
                @click="configurationDialog(energyData.row)"
              >
                Edit
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- configuration dialog -->
    <el-dialog
      title="Configuration"
      :visible.sync="dialogVisible"
      v-if="showSetupDialog && showSetupDialog.id"
      width="55%"
      :before-close="configClose"
      class="fc-dialog-center-container text-left energysetup-dialog"
    >
      <div class="height400 overflow-y-scroll">
        <el-row>
          <el-col :span="8">
            <div class="fc-black-13 text-left bold">
              Property Name
            </div>
          </el-col>
          <el-col :span="3">
            <div class="hide-v">
              <i class="el-icon-right"></i>
            </div>
          </el-col>
          <el-col :span="11">
            <div class="fc-black-13 text-left bold">Map Building</div>
          </el-col>
        </el-row>
        <el-row class="pT10 flex-middle">
          <el-col :span="8">
            <div
              class="label-txt-black text-left"
              v-if="$getProperty(showSetupDialog, 'meta')"
            >
              {{ parseProperty(showSetupDialog.meta) }}
            </div>
          </el-col>
          <el-col :span="3">
            <div>
              <InlineSvg
                src="svgs/black-arrow-right"
                iconClass="icon icon-sm fill-green3"
              ></InlineSvg>
            </div>
          </el-col>
          <el-col :span="11">
            <el-select
              v-model="buildingListData"
              placeholder="Select"
              class="fc-input-full-border2 width80"
              @change="loadMeters()"
              filterable
            >
              <el-option
                v-for="buildingData in buildingDatas"
                :key="buildingData.id"
                :label="buildingData.name"
                :value="buildingData.id"
              >
              </el-option>
            </el-select>
          </el-col>
        </el-row>
        <div class="fc-text-pink12 pT30 text-uppercase bold">
          Meters
        </div>
        <div class="fc-energy-setup-table-con">
          <table class="setting-list-view-table width100 fc-energy-setup-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text uppercase">
                  METER NAME
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  ENERGY TYPE
                </th>
                <th class="setting-table-th setting-th-text uppercase">
                  MAP METER
                </th>
              </tr>
            </thead>
            <tbody v-if="loading">
              <td colspan="100%" class="text-center">
                <div class="flex-middle pT30 pB30 width100">
                  <spinner :show="loading" size="50"></spinner>
                </div>
              </td>
            </tbody>

            <tbody
              v-if="
                $validation.isEmpty(showSetupDialog.meterContexts) && !loading
              "
            >
              <td colspan="100%" class="text-center">
                <div
                  class="flex-middle justify-content-center flex-direction-column height200"
                >
                  <inline-svg
                    src="svgs/emptystate/reportlist"
                    iconClass="icon text-center icon-xxxlg"
                  ></inline-svg>
                  <div class="nowo-label">
                    No Meters available
                  </div>
                </div>
              </td>
            </tbody>
            <tbody
              v-if="
                !loading && !$validation.isEmpty(showSetupDialog.meterContexts)
              "
            >
              <tr
                v-for="(meterTable, index) in showSetupDialog.meterContexts"
                :key="index"
              >
                <td>
                  <div v-if="$getProperty(meterTable, 'meta')">
                    {{
                      parseProperty(meterTable.meta)
                        ? parseProperty(meterTable.meta)
                        : ''
                    }}
                  </div>
                </td>
                <td>
                  <div v-if="$getProperty(meterTable, 'typeEnum')">
                    {{ $getProperty(meterTable, 'typeEnum') }}
                  </div>
                </td>
                <td>
                  <el-select
                    v-model="meterTable.meterId"
                    placeholder="Select"
                    class="fc-input-full-border2 width80"
                    filterable
                  >
                    <el-option
                      v-for="energyMeter in meterList"
                      :key="energyMeter.id"
                      :label="energyMeter.name"
                      :value="energyMeter.id"
                    >
                    </el-option>
                  </el-select>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="configClose" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          @click="energyMeterConfigureData"
          class="modal-btn-save"
          >Confirm</el-button
        >
      </div>
    </el-dialog>

    <el-dialog
      title="Data Sync"
      :visible.sync="syncDataVisible"
      width="35%"
      :before-close="syncCancel"
      class="fc-dialog-center-container text-left"
    >
      <div class="height150">
        <div class="flex-middle items-start">
          <div>
            <i class="fc-warning-14 bold f18 el-icon-warning-outline"></i>
          </div>
          <div class="fc-warning-14 line-height20 f16 break-word pL10">
            Sync will be scheduled at the background.
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="syncCancel" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          @click="dataSyncDialogVisible"
          class="modal-btn-save"
          >Sync Data</el-button
        >
      </div>
    </el-dialog>

    <el-dialog
      title="Data Sync"
      :visible.sync="nonsyncDataVisible"
      width="38%"
      :before-close="nonsyncCancel"
      class="fc-dialog-center-container text-left"
    >
      <div class="height150">
        <div class="flex-middle items-start">
          <div>
            <i class="fc-warning-14 bold f18 el-icon-warning-outline"></i>
          </div>
          <div class="fc-warning-14 line-height20 f16 break-word pL10">
            Sync is running in the background, please wait until it is
            completed.
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          @click="nonsyncDataVisible = false"
          class="modal-btn-cancel width100"
          >Cancel</el-button
        >
      </div>
    </el-dialog>

    <el-dialog
      title="Data Confirmation"
      :visible.sync="configurationDialogVisible"
      width="35%"
      :before-close="confrimationCancel"
      class="fc-dialog-center-container text-left"
    >
      <div class="height200 break-word line-height20">
        <div class="flex-middle items-start">
          <div>
            <i class="fc-warning-14 bold f18 el-icon-warning-outline"></i>
          </div>
          <div class="fc-warning-14 line-height20 f16 break-word pL10">
            If you want proceed value not changed map building and meter?
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          @click="configurationDialogVisible = false"
          class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="confrimationOk" class="modal-btn-save"
          >Confirm</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['visibility'],
  data() {
    return {
      energyDatas: [],
      energyData: null,
      energyMetricDatas: [],
      energyMetricData: null,
      myObj: {},
      dialogVisible: false,
      tableData: [],
      showSetupDialog: null,
      loading: true,
      meterList: [],
      energyMeter: null,
      buildingDatas: [],
      buildingData: null,
      buildingListData: null,
      syncData: [],
      metaMeterName: {},
      syncDataVisible: false,
      nonsyncDataVisible: false,
      getBuildingId: [],
      getMeterId: [],
      configurationDialogVisible: false,
    }
  },
  mounted() {
    this.energySetupDataFetch()
    this.lastSyncData()
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    configClose() {
      this.dialogVisible = false
    },
    async allBuildings() {
      this.loading = true
      let { error, data } = await API.get(
        `/v2/building/list?fetchCount=false&viewName=all`
      )
      if (error) {
        this.loading = false
        let { message } = error
        this.$message.error(message)
      } else {
        this.loading = false
        this.buildingDatas = data.buildings ? data.buildings : []
      }
    },
    async lastSyncData() {
      this.loading = true
      let { error, data } = await API.get(`/v2/energystar/fetchCustomer`)
      if (error) {
        this.loading = false
        let { message } = error
        this.$message.error(message)
      } else {
        this.loading = false
        this.syncData = data.energyStarCustomerContext
          ? data.energyStarCustomerContext
          : []
      }
    },
    energySetupDataFetch() {
      this.loading = true
      this.$http
        .get('/v2/energystar/fetchSetupData')
        .then(response => {
          this.loading = false
          this.energyDatas = response.data.result.energyStarPropertiesContext
            ? response.data.result.energyStarPropertiesContext
            : []

          this.energyMetricDatas = response.data.result
            .energyStarPropertiesContext.meterContexts
            ? response.data.result.energyStarPropertiesContext.meterContexts
            : []
          let energyStarPropertiesContext =
            response.data.result.energyStarPropertiesContext
          let property = energyStarPropertiesContext.find(
            rt => rt.id === this.showSetupDialog.id
          )
          let currentBuilding = property.data.building.buildingId
          this.buildingListData = currentBuilding
        })
        .catch(() => {
          this.loading = false
        })
    },
    configurationDialog(energyData) {
      this.dialogVisible = true
      this.showSetupDialog = energyData
      this.energySetupDataFetch()
      this.loadMeters()
      this.allBuildings()
    },
    async loadMeters() {
      this.loading = true
      let { buildingListData } = this
      let params = {}
      if (!isEmpty(buildingListData)) {
        params = {
          filters: JSON.stringify({
            buildingId: { operatorId: 9, value: [buildingListData] },
          }),
        }
      }

      let { data, error } = await API.get('/energymeter/all', params)
      if (error) this.$message.error(error.message || 'Error Occured')
      else {
        this.meterList = data || []
      }
      this.loading = false
    },
    loadaddMeter() {
      let that = this
      that.$http
        .get('/energymeter/all')
        .then(function(response) {
          that.loading = false
          if (response.status === 200) {
            that.meterList = response.data ? response.data : []
          }
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    energySync() {
      if (this.syncData.syncStatus === 1 || this.syncData.syncStatus === -1) {
        this.syncDataVisible = true
      } else if (this.syncData.syncStatus === 2) {
        this.nonsyncDataVisible = true
      }
    },
    syncCancel() {
      this.syncDataVisible = false
    },
    nonsyncCancel() {
      this.nonsyncDataVisible = false
    },
    async dataSyncDialogVisible() {
      let { error } = await API.get(`/v2/energystar/sync`)
      if (error) {
        let { message } = error
        this.$message.error(message)
        this.closeDialog()
      } else {
        this.$message.success('Energy Data sync started successfully.')
        this.syncCancel()
        this.closeDialog()
      }
    },
    energyMeterConfigure() {
      this.loading = true
      // let meterObj = this.showSetupDialog
      let data = this.$helpers.cloneObject(this.showSetupDialog)
      data.buildingId = this.buildingListData
      let url = {
        propertyContext: data,
      }
      // url.propertyContext.meterContexts.meterId = this.selectedEnergyMeter
      API.post('/v2/energystar/updateProperty', url).then(({ error }) => {
        if (error) {
          this.$message.error(error)
          this.loading = false
          this.dialogVisible = false
          this.closeDialog()
        } else {
          this.$message.success('Meter configure succeed')
          this.loading = false
          this.dialogVisible = false
          this.closeDialog()
        }
      })
    },
    parseProperty(string) {
      if (string) {
        let parsedData = JSON.parse(string)
        if (parsedData && parsedData.name) {
          return parsedData.name
        }
      }
      return ''
    },
    confrimationOk() {
      this.energyMeterConfigure()
    },
    confrimationCancel() {
      this.configurationDialogVisible = false
    },
    energyMeterConfigureData() {
      let { getBuildingId, buildingListData } = this
      let buildingId = this.$getProperty(getBuildingId, 'buildingId', -1)

      if (buildingListData === buildingId) {
        this.configurationDialogVisible = true
      } else {
        this.energyMeterConfigure()
      }
    },
  },
}
</script>
<style lang="scss">
.fc-energy-setup-main-table {
  table td {
    padding-left: 20px !important;
  }
}
.energysetup-dialog {
  .el-dialog__body {
    padding-bottom: 80px !important;
  }
}
</style>
