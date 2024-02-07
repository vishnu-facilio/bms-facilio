<template>
  <div class="height100 user-layout">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">
          {{ $t('common.wo_report.operating_hours') }}
        </div>
        <div class="heading-description">
          {{ $t('common._common.list_of_all_operating_hour') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          @click="newOperatingHour"
          class="setup-el-btn"
          >{{ $t('common._common.add_operating_hour') }}</el-button
        >
        <new-operating-hour
          v-if="showDialog"
          :isNew="isNew"
          :businessHour="selectedOperatingHour"
          :visibility.sync="showDialog"
          @edit="editBH"
          @add="addBH"
        ></new-operating-hour>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12">
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common.products._name') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common._common.operating_hour_type') }}
                </th>
                <th class="setting-table-th setting-th-text">
                  {{ $t('common._common.custom_hour_type') }}
                </th>
                <th class="setting-table-th setting-th-text"></th>
              </tr>
            </thead>
            <tbody v-if="businessHoursList.length === 0">
              <tr>
                <td colspan="100%" class="text-center">
                  {{ $t('common._common.no_operating_hours_created') }}
                </td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="(operatingHour, index) in businessHoursList"
                :key="index"
              >
                <td style="padding-top: 18px;padding-bottom: 18px;">
                  {{ operatingHour.name }}
                </td>
                <td>
                  {{
                    businessHourtypeMap.get(operatingHour.businessHourTypeVal)
                  }}
                </td>
                <td>
                  {{ customHourtypeMap.get(operatingHour.customHourTypeVal) }}
                </td>
                <td>
                  <div
                    class="text-left actions"
                    style="margin-top:-3px;margin-right: 15px;text-align:center;"
                  >
                    <i
                      class="el-icon-edit pointer"
                      @click="editOperatingHour(index, operatingHour)"
                    ></i>
                    &nbsp;&nbsp;
                    <i
                      class="el-icon-delete pointer"
                      @click="deleteOperatingHour(index, operatingHour)"
                    ></i>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import NewOperatingHour from '@/NewOperatingHour'
export default {
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
      businessHourtypeMap: businessHourtypeMap,
      customHourtypeMap: customHourtypeMap,
      selectedType: null,
      isNew: false,
      showDialog: false,
      associatedModules: null,
      showCannotDeleteDialog: false,
    }
  },
  mounted() {
    this.getOperatingHour()
  },
  methods: {
    addBH(businessHour) {
      this.businessHoursList.push(businessHour)
    },
    editBH(businessHour) {
      let idx = this.businessHoursList.findIndex(
        bh => bh.id === businessHour.id
      )
      this.businessHoursList.splice(idx, 0, businessHour)
      this.businessHoursList.splice(idx + 1, 1)
    },
    getOperatingHour() {
      let self = this
      self.$http.get('/v2/businesshours/list').then(function(response) {
        self.businessHoursList = response.data.result.list
      })
    },
    newOperatingHour() {
      this.selectedOperatingHour = null
      this.isNew = true
      this.showDialog = true
    },
    editOperatingHour(index, operatingHour) {
      this.selectedOperatingHour = { index, value: operatingHour }
      this.isNew = false
      this.showDialog = true
    },
    openCannotDelDialog(modules) {
      this.associatedModules = modules
      this.showCannotDeleteDialog = true
    },
    deleteOperatingHour(index, operatingHour) {
      this.$dialog
        .confirm({
          title: this.$t('common.wo_report.delete_operating_hour_title'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_operating_hour'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/v2/businesshours/delete', { id: operatingHour.id })
              .then(response => {
                if (response.status === 200) {
                  this.businessHoursList.splice(index, 1)
                  this.$message.success(
                    this.$t('common._common.operating_hour_deleted_success')
                  )
                }
              })
              .catch(error => {
                if (error.response.status === 400) {
                  this.openCannotDelDialog(error.response.data.relatedModules)
                } else {
                  this.$message.error(
                    this.$t('common._common.failed_to_delete_operating_hour')
                  )
                }
              })
          }
        })
    },
  },
}
</script>
<style></style>
