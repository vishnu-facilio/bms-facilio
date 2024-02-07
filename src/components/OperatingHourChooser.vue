<template>
  <div>
    <new-operating-hour
      @businessHourChange="changeBH"
      v-if="showDialog"
      :isNew="true"
      :resourceid="resourceid"
      :visibility.sync="showDialog"
      @close="handleclose"
    ></new-operating-hour>
    <el-dialog
      v-if="showOperatingHourDialog"
      :visible.sync="showOperatingHourDialog"
      :title="$t('asset.operating_hours.operating_hours')"
      :fullscreen="false"
      open="top"
      custom-class="fc-dialog-center-container fc-dialog-center-body-p0 operating-hours-dialog"
      :before-close="handleclose"
      :append-to-body="true"
    >
      <div class="fc-alert-msg-info" v-if="showScheduleNotification">
        <i class="fa fa-exclamation-triangle pR10" aria-hidden="true"></i>
        Out of Schedule alarm will be re-calculated with this operational hour
        for last one year.
      </div>
      <div id="operatinghourchooser">
        <div
          v-if="resourceid"
          @click="newOperatingHour"
          class="fc-dark-blue4-12 position-absolute pointer"
          style="right: 70px; top: 22px;"
        >
          {{ $t('space.sites.add_operating_hour') }}
        </div>
        <div class="clearboth operating-hour-dialog-scroll">
          <div>
            <div class="col-lg-12 col-md-12">
              <table class="setting-list-view-table width100 setting-table-p0">
                <thead>
                  <tr>
                    <th class="setting-table-th setting-th-text width200">
                      {{ $t('common._common.name') }}
                    </th>
                    <th class="setting-table-th setting-th-text">
                      {{ $t('asset.operating_hours.op_type') }}
                    </th>
                    <th class="setting-table-th setting-th-text">
                      {{ $t('asset.operating_hours.custom_type') }}
                    </th>
                  </tr>
                </thead>
                <tbody v-if="businessHoursList.length === 0">
                  <tr>
                    <td colspan="100%" class="text-center">
                      {{ $t('asset.operating_hours.no_data') }}
                    </td>
                  </tr>
                </tbody>
                <tbody v-else>
                  <tr
                    class="tablerow"
                    v-for="(operatingHour, index) in businessHoursList"
                    :key="index"
                    @click.stop="toggleSelect(operatingHour)"
                    v-bind:class="{ selectedrow: operatingHour.selected }"
                  >
                    <td class="width200">
                      {{ operatingHour.name }}
                    </td>
                    <td>
                      {{
                        businessHourtypeMap.get(
                          operatingHour.businessHourTypeVal
                        )
                      }}
                    </td>
                    <td>
                      {{
                        customHourtypeMap.get(operatingHour.customHourTypeVal)
                      }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div class="modal-dialog-footer">
          <el-button @click="handleclose()" class="modal-btn-cancel">{{
            $t('space.sites.site_cancel')
          }}</el-button>
          <el-button
            type="primary"
            @click="updateOperatingHour"
            :loading="operatingHourValueSaving"
            class="modal-btn-save"
            >{{
              operatingHourValueSaving
                ? $t('common._common._saving')
                : resourceid
                ? $t('common._common._save')
                : $t('common._common.select')
            }}</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import NewOperatingHour from '@/NewOperatingHour'
export default {
  props: ['resourceid', 'businessHour', 'showOperatingHourDialog', 'isAssetBh'],
  components: {
    NewOperatingHour,
  },
  data() {
    const businessHourtypeMap = new Map()
    businessHourtypeMap.set('DAYS_24_7', '24 Hours 7 Days')
    businessHourtypeMap.set('DAYS_24_5', '24 Hours 5 Days')
    businessHourtypeMap.set('CUSTOM', 'Custom Hours')
    const customHourtypeMap = new Map()
    customHourtypeMap.set('SAME_TIMING_ALLDAY', 'Same timing all day')
    customHourtypeMap.set('DIFFERENT_TIMING_ALLDAY', 'Different timing all day')
    return {
      businessHoursList: [],
      showDialog: false,
      selectedList: [],
      operatingHourValueSaving: false,
      businessHourtypeMap: businessHourtypeMap,
      customHourtypeMap: customHourtypeMap,
      showScheduleNotification: false,
    }
  },
  created() {
    this.checkRunStatus()
  },
  mounted() {
    this.getOperatingHour()
    this.initOperatingHour()
  },
  methods: {
    getOperatingHour() {
      let self = this
      self.$http.get('/v2/businesshours/list').then(function(response) {
        self.businessHoursList = response.data.result.list
        self.initOperatingHour()
      })
    },
    changeBH(businesshour) {
      this.$emit('businessHourChangeInChooser', businesshour)
    },
    initOperatingHour() {
      this.selectedList = []
      for (let i in this.businessHoursList) {
        if (this.businessHour != null) {
          if (this.businessHour.id === this.businessHoursList[i].id) {
            this.businessHoursList[i].selected = true
          } else {
            this.businessHoursList[i].selected = false
          }
        }
      }
    },
    handleclose() {
      this.showOperatingHourDialog = false
      this.$emit('update:showOperatingHourDialog', false)
      this.$emit('closeOperatingHourChooser')
    },
    checkRunStatus() {
      if (
        this.isAssetBh &&
        this.$helpers.isLicenseEnabled('OPERATIONAL_ALARM')
      ) {
        this.$http
          .get(`/v2/assets/checkrunstatus?assetId=${this.resourceid}`)
          .then(response => {
            this.showScheduleNotification =
              response.data.result.runStatusAvailable
          })
      }
    },
    updateOperatingHour() {
      if (this.selectedList.length > 0) {
        let id = this.selectedList[0].id
        this.operatingHourValueSaving = true
        let { resourceid } = this
        if (resourceid > 0) {
          return this.$http
            .post('/v2/businesshours/updateresource', { id, resourceid })
            .then(response => {
              this.operatingHourValueSaving = false
              this.$message.success('Operating Hour Saved Successfully.')
              this.onSelect()
            })
            .catch(() => {
              this.operatingHourValueSaving = false
              this.$message.error('Failed to save Operating Hour.')
            })
        } else {
          this.onSelect()
        }
      }
    },
    onSelect() {
      this.changeBH(this.selectedList[0])
      this.$emit('update:showOperatingHourDialog', false)
    },
    newOperatingHour() {
      this.showDialog = true
      this.showOperatingHourDialog = false
    },
    toggleSelect(resource, alreadyChanged) {
      this.selectedList = []
      for (let i in this.businessHoursList) {
        this.businessHoursList[i].selected = false
      }
      if (!alreadyChanged) {
        this.$set(resource, 'selected', !resource.selected)
      }
      this.setSelectedResource(resource)
    },
    setSelectedResource(resource) {
      if (resource.selected) {
        this.selectedList.push(this.$helpers.cloneObject(resource))
        let idx = this.businessHoursList.findIndex(bh => bh.id === resource.id)
        this.businessHoursList[idx].selected = true
        this.businessHoursList.splice(idx, 0, resource)
        this.businessHoursList.splice(idx + 1, 1)
      } else {
        let idx = this.selectedList.findIndex(sr => sr.id === resource.id)
        if (idx !== -1) {
          this.selectedList.splice(idx, 1)
        }
      }
    },
  },
}
</script>
<style lang="scss">
.selectedrow {
  background-color: #effdff !important;
}
.operating-hour-dialog-scroll {
  max-height: 400px;
  padding-bottom: 100px;
  overflow-y: scroll;
}
.tablerow.selected {
  background: #effdff;
  /* border-left: 3px solid #28b2a4; */
  border-right: 1px solid rgba(232, 232, 232, 0.35);
  border-top: 1px solid rgba(232, 232, 232, 0.35);
  border-bottom: 1px solid rgba(232, 232, 232, 0.35) !important;
}
#operatinghourchooser .setting-list-view-table tbody tr.tablerow:hover {
  background-color: #effdff !important;
}
.operating-hours-dialog .fc-alert-msg-info {
  border-radius: 0;
  margin-top: -3px;
  border-left: 0;
  padding-top: 13px;
  font-size: 13px;
  border-right: 0;
  padding-bottom: 14px;
}
</style>
