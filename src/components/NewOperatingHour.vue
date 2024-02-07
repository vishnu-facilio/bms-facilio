<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <div id="newworkordercategory">
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              !isNew
                ? $t('common.wo_report.edit_operating_hour')
                : $t('common.wo_report.new_operating_hour')
            }}
          </div>
        </div>
      </div>
      <div class="new-body-modal">
        <el-row :gutter="20">
          <el-col :span="24">
            <p class="fc-input-label-txt pB10">
              {{ $t('common.products.name') }}
            </p>
            <el-input
              v-model="name"
              :placeholder="$t('common._common.enter_the_name')"
              class="fc-input-full-border-select2"
            ></el-input>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="mT20">
          <el-col :span="24">
            <p class="fc-input-label-txt">
              {{ $t('common._common.operating_hour_type') }}
            </p>
            <el-select
              class="fc-input-full-border-select2 width100"
              v-model="businessHourType"
              :placeholder="$t('common._common.choose_operating_hour_type')"
            >
              <el-option
                :label="$t('common.date_picker.24_hours_7_days')"
                value="DAYS_24_7"
              ></el-option>
              <el-option
                :label="$t('common.date_picker.24_hours_5_days')"
                value="DAYS_24_5"
              ></el-option>
              <el-option
                :label="$t('common._common.custom_hours')"
                value="CUSTOM"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
        <div v-if="businessHourType !== 'DAYS_24_7'">
          <div v-if="businessHourType === 'CUSTOM'">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="setup-input-block mT20">
                  <p class="fc-input-label-txt">
                    {{ $t('common._common.custom_hour_type') }}
                  </p>
                  <el-select
                    class="fc-input-full-border-select2 width100"
                    v-model="customHourType"
                    :placeholder="$t('common._common.choose_custom_hour_type')"
                  >
                    <el-option
                      :label="$t('common._common.same_timing_all_day')"
                      value="SAME_TIMING_ALLDAY"
                    ></el-option>
                    <el-option
                      :label="$t('common._common.different_timing_all_day')"
                      value="DIFFERENT_TIMING_ALLDAY"
                    ></el-option>
                  </el-select>
                </div>
              </el-col>
              <el-col :span="12">
                <div v-if="customHourType === 'SAME_TIMING_ALLDAY'">
                  <div class="setup-input-block mT20">
                    <p class="fc-input-label-txt">
                      {{ $t('common._common.timing') }}
                    </p>
                    <el-row class="operating-hours-checkbox">
                      <el-col :span="12">
                        <el-time-select
                          v-model="same_starttime"
                          :picker-options="{
                            start: '00:00',
                            step: '00:15',
                            end: '23:45',
                          }"
                          :placeholder="$t('common._common.from')"
                          class="width110px"
                        ></el-time-select>
                      </el-col>
                      <el-col :span="12" class="pL10">
                        <el-time-select
                          v-model="same_endtime"
                          :picker-options="{
                            start: '00:00',
                            step: '00:15',
                            end: '23:45',
                          }"
                          :placeholder="$t('common.products.to')"
                          class="width110px"
                        ></el-time-select>
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <div class="setup-input-block mT30">
            <p class="fc-input-label-txt">
              {{ $t('common.wo_report.operating_days') }}
            </p>
            <el-checkbox-group
              v-model="operatingDaysList"
              :min="1"
              :max="7"
              class="mT10 operating-hours-checkbox"
            >
              <el-checkbox class="mB20" label="1">
                <div class="flex-middle">
                  <div class="width75px">{{ $t('common._common.monday') }}</div>
                  <div
                    v-if="
                      businessHourType === 'CUSTOM' &&
                        customHourType === 'DIFFERENT_TIMING_ALLDAY'
                    "
                    class="mL20"
                  >
                    <el-time-select
                      v-model="mon_starttime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common._common.from')"
                      class="fc-input-full-border-h35 mR10 "
                    ></el-time-select>
                    <el-time-select
                      v-model="mon_endtime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common.wo_report.to')"
                      class="fc-input-full-border-h35 "
                    ></el-time-select>
                  </div>
                </div> </el-checkbox
              ><br />
              <el-checkbox class="mB20" label="2">
                <div class="flex-middle">
                  <div class="width75px">
                    {{ $t('common._common.tuesday') }}
                  </div>
                  <div
                    v-if="
                      businessHourType === 'CUSTOM' &&
                        customHourType === 'DIFFERENT_TIMING_ALLDAY'
                    "
                    class="mL20"
                  >
                    <el-time-select
                      v-model="tue_starttime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common._common.from')"
                      class="fc-input-full-border-h35 mR10 "
                    ></el-time-select>
                    <el-time-select
                      v-model="tue_endtime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common.wo_report.to')"
                      class="fc-input-full-border-h35 "
                    ></el-time-select>
                  </div>
                </div> </el-checkbox
              ><br />
              <el-checkbox class="mB20" label="3">
                <div class="flex-middle">
                  <div class="width75px">
                    {{ $t('common._common.wednesday') }}
                  </div>
                  <div
                    v-if="
                      businessHourType === 'CUSTOM' &&
                        customHourType === 'DIFFERENT_TIMING_ALLDAY'
                    "
                    class="mL20"
                  >
                    <el-time-select
                      v-model="wed_starttime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common._common.from')"
                      class="fc-input-full-border-h35 mR10 "
                    ></el-time-select>
                    <el-time-select
                      v-model="wed_endtime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common.wo_report.to')"
                      class="fc-input-full-border-h35 "
                    ></el-time-select>
                  </div>
                </div> </el-checkbox
              ><br />
              <el-checkbox class="mB20" label="4">
                <div class="flex-middle">
                  <div class="width75px">
                    {{ $t('common._common.thursday') }}
                  </div>
                  <div
                    v-if="
                      businessHourType === 'CUSTOM' &&
                        customHourType === 'DIFFERENT_TIMING_ALLDAY'
                    "
                    class="mL20"
                  >
                    <el-time-select
                      v-model="thu_starttime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common._common.from')"
                      class="fc-input-full-border-h35 mR10 "
                    ></el-time-select>
                    <el-time-select
                      v-model="thu_endtime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common.wo_report.to')"
                      class="fc-input-full-border-h35 "
                    ></el-time-select>
                  </div>
                </div> </el-checkbox
              ><br />
              <el-checkbox class="mB20" label="5">
                <div class="flex-middle">
                  <div class="width75px">{{ $t('common._common.friday') }}</div>
                  <div
                    v-if="
                      businessHourType === 'CUSTOM' &&
                        customHourType === 'DIFFERENT_TIMING_ALLDAY'
                    "
                    class="mL20"
                  >
                    <el-time-select
                      v-model="fri_starttime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common._common.from')"
                      class="fc-input-full-border-h35 mR10 "
                    ></el-time-select>
                    <el-time-select
                      v-model="fri_endtime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common.wo_report.to')"
                      class="fc-input-full-border-h35 "
                    ></el-time-select>
                  </div>
                </div> </el-checkbox
              ><br />
              <el-checkbox class="mB20" label="6">
                <div class="flex-middle">
                  <div class="width75px">
                    {{ $t('common._common.saturday') }}
                  </div>
                  <div
                    v-if="
                      businessHourType === 'CUSTOM' &&
                        customHourType === 'DIFFERENT_TIMING_ALLDAY'
                    "
                    class="mL20"
                  >
                    <el-time-select
                      v-model="sat_starttime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common._common.from')"
                      class="fc-input-full-border-h35 mR10 "
                    ></el-time-select>
                    <el-time-select
                      v-model="sat_endtime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common.wo_report.to')"
                      class="fc-input-full-border-h35 "
                    ></el-time-select>
                  </div>
                </div> </el-checkbox
              ><br />
              <el-checkbox class="mB20" label="7">
                <div class="flex-middle">
                  <div class="width75px">{{ $t('common._common.sunday') }}</div>
                  <div
                    v-if="
                      businessHourType === 'CUSTOM' &&
                        customHourType === 'DIFFERENT_TIMING_ALLDAY'
                    "
                    class="mL20"
                  >
                    <el-time-select
                      v-model="sun_starttime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common._common.from')"
                      class="fc-input-full-border-h35 mR10 "
                    ></el-time-select>
                    <el-time-select
                      v-model="sun_endtime"
                      :picker-options="{
                        start: '00:00',
                        step: '00:15',
                        end: '23:45',
                      }"
                      :placeholder="$t('common.wo_report.to')"
                      class="fc-input-full-border-h35 "
                    ></el-time-select>
                  </div>
                </div> </el-checkbox
              ><br />
            </el-checkbox-group>
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="saveOperatingHour"
          :loading="operatingHourValueSaving"
          class="modal-btn-save"
          >{{
            operatingHourValueSaving
              ? $t('common._common._saving')
              : $t('common._common._save')
          }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
export default {
  props: ['businessHour', 'isNew', 'visibility', 'resourceid'],
  components: {
    ErrorBanner,
  },
  data() {
    return {
      name: '',
      businessHourType: '',
      customHourType: '',
      same_starttime: '',
      same_endtime: '',
      mon_starttime: '',
      mon_endtime: '',
      tue_starttime: '',
      tue_endtime: '',
      wed_starttime: '',
      wed_endtime: '',
      thu_starttime: '',
      thu_endtime: '',
      fri_starttime: '',
      fri_endtime: '',
      sat_starttime: '',
      sat_endtime: '',
      sun_starttime: '',
      sun_endtime: '',
      operatingDaysList: [],
      singleDaybusinessHoursList: [],
      error: false,
      errorText: '',
      operatingHourValueSaving: false,
    }
  },
  mounted() {
    this.initOperatingHour()
  },
  methods: {
    initOperatingHour() {
      if (this.isNew) {
        this.name = ''
        this.businessHourType = 'DAYS_24_7'
        this.customHourType = 'SAME_TIMING_ALLDAY'
        this.same_starttime = ''
        this.same_endtime = ''
        this.mon_starttime = ''
        this.mon_endtime = ''
        this.tue_starttime = ''
        this.tue_endtime = ''
        this.wed_starttime = ''
        this.wed_endtime = ''
        this.thu_starttime = ''
        this.thu_endtime = ''
        this.fri_starttime = ''
        this.fri_endtime = ''
        this.sat_starttime = ''
        this.sat_endtime = ''
        this.sun_starttime = ''
        this.sun_endtime = ''
        this.operatingDaysList = ['1', '2', '3', '4', '5']
        this.singleDaybusinessHoursList = []
      } else {
        this.name = this.businessHour.value.name
        this.businessHourType = this.businessHour.value.businessHourTypeVal
        this.customHourType = this.businessHour.value.customHourTypeVal
        if (this.customHourType === null) {
          this.customHourType = 'SAME_TIMING_ALLDAY'
        }
        this.singleDaybusinessHoursList = this.businessHour.value.singleDaybusinessHoursList
        if (this.businessHourType === 'DAYS_24_7') {
          this.operatingDaysList = ['1', '2', '3', '4', '5']
        }
        for (let i in this.singleDaybusinessHoursList) {
          this.singleDaybusinessHoursList[
            i
          ].dayOfWeek = this.singleDaybusinessHoursList[i].dayOfWeek.toString()
          this.operatingDaysList.push(
            this.singleDaybusinessHoursList[i].dayOfWeek
          )
          if (this.singleDaybusinessHoursList[i].dayOfWeek === '1') {
            this.mon_starttime = this.singleDaybusinessHoursList[i].startTime
            this.mon_endtime = this.singleDaybusinessHoursList[i].endTime
          } else if (this.singleDaybusinessHoursList[i].dayOfWeek === '2') {
            this.tue_starttime = this.singleDaybusinessHoursList[i].startTime
            this.tue_endtime = this.singleDaybusinessHoursList[i].endTime
          } else if (this.singleDaybusinessHoursList[i].dayOfWeek === '3') {
            this.wed_starttime = this.singleDaybusinessHoursList[i].startTime
            this.wed_endtime = this.singleDaybusinessHoursList[i].endTime
          } else if (this.singleDaybusinessHoursList[i].dayOfWeek === '4') {
            this.thu_starttime = this.singleDaybusinessHoursList[i].startTime
            this.thu_endtime = this.singleDaybusinessHoursList[i].endTime
          } else if (this.singleDaybusinessHoursList[i].dayOfWeek === '5') {
            this.fri_starttime = this.singleDaybusinessHoursList[i].startTime
            this.fri_endtime = this.singleDaybusinessHoursList[i].endTime
          } else if (this.singleDaybusinessHoursList[i].dayOfWeek === '6') {
            this.sat_starttime = this.singleDaybusinessHoursList[i].startTime
            this.sat_endtime = this.singleDaybusinessHoursList[i].endTime
          } else if (this.singleDaybusinessHoursList[i].dayOfWeek === '7') {
            this.sun_starttime = this.singleDaybusinessHoursList[i].startTime
            this.sun_endtime = this.singleDaybusinessHoursList[i].endTime
          }
        }
        if (
          this.businessHourType === 'CUSTOM' &&
          this.customHourType === 'SAME_TIMING_ALLDAY' &&
          this.singleDaybusinessHoursList.length > 0
        ) {
          this.same_starttime = this.singleDaybusinessHoursList[0].startTime
          this.same_endtime = this.singleDaybusinessHoursList[0].endTime
        }
      }
    },
    validation() {
      this.error = false
      this.errorText = ''
      if (this.name === null || this.name === '') {
        this.errorText = this.$t('common.header.please_enter_name')
        this.error = true
        return false
      } else if (
        this.businessHourType === 'DAYS_24_5' &&
        this.operatingDaysList.length != 5
      ) {
        this.errorText = this.$t(
          'common._common.please_choose_five_operating_days'
        )
        this.error = true
        return false
      } else if (
        this.businessHourType === 'CUSTOM' &&
        this.customHourType === 'SAME_TIMING_ALLDAY' &&
        (this.same_starttime === null ||
          this.same_starttime === '' ||
          this.same_endtime === null ||
          this.same_endtime === '')
      ) {
        this.errorText = this.$t('common._common.please_choose_timing')
        this.error = true
        return false
      } else if (
        this.businessHourType === 'CUSTOM' &&
        this.customHourType === 'DIFFERENT_TIMING_ALLDAY' &&
        ((this.operatingDaysList.indexOf('1') > -1 &&
          (this.mon_starttime === null ||
            this.mon_starttime === '' ||
            this.mon_endtime === null ||
            this.mon_endtime === '')) ||
          (this.operatingDaysList.indexOf('2') > -1 &&
            (this.tue_starttime === null ||
              this.tue_starttime === '' ||
              this.tue_endtime === null ||
              this.tue_endtime === '')) ||
          (this.operatingDaysList.indexOf('3') > -1 &&
            (this.wed_starttime === null ||
              this.wed_starttime === '' ||
              this.wed_endtime === null ||
              this.wed_endtime === '')) ||
          (this.operatingDaysList.indexOf('4') > -1 &&
            (this.thu_starttime === null ||
              this.thu_starttime === '' ||
              this.thu_endtime === null ||
              this.thu_endtime === '')) ||
          (this.operatingDaysList.indexOf('5') > -1 &&
            (this.fri_starttime === null ||
              this.fri_starttime === '' ||
              this.fri_endtime === null ||
              this.fri_endtime === '')) ||
          (this.operatingDaysList.indexOf('6') > -1 &&
            (this.sat_starttime === null ||
              this.sat_starttime === '' ||
              this.sat_endtime === null ||
              this.sat_endtime === '')) ||
          (this.operatingDaysList.indexOf('7') > -1 &&
            (this.sun_starttime === null ||
              this.sun_starttime === '' ||
              this.sun_endtime === null ||
              this.sun_endtime === '')))
      ) {
        this.errorText = this.$t(
          'common._common.please_choose_timing_selected_operating_days'
        )
        this.error = true
        return false
      } else {
        this.errorText = ''
        this.error = false
      }
    },
    addOperatingHour() {
      this.validation()
      if (this.error) {
        return
      }
      this.preparingDatasToSave()
      let resourceid = this.resourceid
      let businessHour = {
        name: this.name,
        businessHourType: this.businessHourType,
        customHourType: this.customHourType,
        singleDaybusinessHoursList: this.singleDaybusinessHoursList,
      }
      this.$http
        .post('/v2/businesshours/add', { businessHour, resourceid })
        .then(response => {
          // this.$account.data.operatingHour.push(response.data.result.businessHour);
          this.$message.success(
            this.$t('common.wo_report.operating_hour_saved_success')
          )
          businessHour.businessHourTypeVal = businessHour.businessHourType
          businessHour.customHourTypeVal = businessHour.customHourType
          businessHour.id = response.data.result.businessHour.id
          if (resourceid !== undefined) {
            this.$emit('businessHourChange', businessHour)
          } else {
            this.$emit('add', businessHour)
          }
          this.$emit('update:visibility', false)
        })
        .catch(() => {
          this.$message.error(
            this.$t('common._common.failed_to_save_operating_hour')
          )
          this.$emit('update:visibility', false)
        })
    },
    editOperatingHour() {
      this.validation()
      if (this.error) {
        return
      }
      this.preparingDatasToSave()
      let businessHour = Object.assign({}, this.businessHour.value)
      businessHour.name = this.name
      businessHour.businessHourType = this.businessHourType
      businessHour.customHourType = this.customHourType
      businessHour.businessHourTypeVal = businessHour.businessHourType
      businessHour.customHourTypeVal = businessHour.customHourType
      businessHour.singleDaybusinessHoursList = this.singleDaybusinessHoursList
      this.$http
        .post('/v2/businesshours/edit', { businessHour })
        .then(() => {
          this.$emit('edit', businessHour)
          this.$message.success(
            this.$t('common._common.operating_hour_updated_success')
          )
          this.$emit('update:visibility', false)
        })
        .catch(() => {
          this.$message.success(
            this.$t('common._common.failed_to_update_operating_hour')
          )
          this.$emit('update:visibility', false)
        })
    },
    saveOperatingHour() {
      if (this.isNew) {
        this.addOperatingHour()
      } else {
        this.editOperatingHour()
      }
    },
    closeDialog() {
      this.$emit('close')
      this.$emit('update:visibility', false)
    },
    clearDifferentTimingValues() {
      this.mon_starttime = null
      this.mon_endtime = null
      this.tue_starttime = null
      this.tue_endtime = null
      this.wed_starttime = null
      this.wed_endtime = null
      this.thu_starttime = null
      this.thu_endtime = null
      this.fri_starttime = null
      this.fri_endtime = null
      this.sat_starttime = null
      this.sat_endtime = null
      this.sun_starttime = null
      this.sun_endtime = null
    },
    preparingDatasToSave() {
      if (this.businessHourType === 'DAYS_24_7') {
        this.customHourType = null
        this.same_starttime = null
        this.same_endtime = null
        this.operatingDaysList = null
        this.clearDifferentTimingValues()
        this.singleDaybusinessHoursList = null
      } else if (this.businessHourType === 'DAYS_24_5') {
        this.customHourType = null
        this.same_starttime = null
        this.same_endtime = null
        this.clearDifferentTimingValues()
        this.singleDaybusinessHoursList = []
        for (let i in this.operatingDaysList) {
          let singleDayBusinessHour = {
            dayOfWeek: '',
            startTime: '',
            endTime: '',
          }
          singleDayBusinessHour.dayOfWeek = this.operatingDaysList[i]
          this.singleDaybusinessHoursList.push(singleDayBusinessHour)
        }
      } else if (this.businessHourType === 'CUSTOM') {
        if (this.customHourType === 'SAME_TIMING_ALLDAY') {
          this.mon_starttime = this.same_starttime
          this.mon_endtime = this.same_endtime
          this.tue_starttime = this.same_starttime
          this.tue_endtime = this.same_endtime
          this.wed_starttime = this.same_starttime
          this.wed_endtime = this.same_endtime
          this.thu_starttime = this.same_starttime
          this.thu_endtime = this.same_endtime
          this.fri_starttime = this.same_starttime
          this.fri_endtime = this.same_endtime
          this.sat_starttime = this.same_starttime
          this.sat_endtime = this.same_endtime
          this.sun_starttime = this.same_starttime
          this.sun_endtime = this.same_endtime
        } else {
          this.same_starttime = null
          this.same_endtime = null
        }
        this.singleDaybusinessHoursList = []
        for (let j in this.operatingDaysList) {
          let singleDayBusinessHours = {
            dayOfWeek: '',
            startTime: '',
            endTime: '',
          }
          singleDayBusinessHours.dayOfWeek = this.operatingDaysList[j]
          singleDayBusinessHours.startTime = this.getTimeByDayOfWeek(
            'start',
            singleDayBusinessHours.dayOfWeek
          )
          singleDayBusinessHours.endTime = this.getTimeByDayOfWeek(
            'end',
            singleDayBusinessHours.dayOfWeek
          )
          this.singleDaybusinessHoursList.push(singleDayBusinessHours)
        }
      }
    },
    getTimeByDayOfWeek(type, dayofweek) {
      if (type === 'start') {
        if (dayofweek === '1') {
          return this.mon_starttime
        } else if (dayofweek === '2') {
          return this.tue_starttime
        } else if (dayofweek === '3') {
          return this.wed_starttime
        } else if (dayofweek === '4') {
          return this.thu_starttime
        } else if (dayofweek === '5') {
          return this.fri_starttime
        } else if (dayofweek === '6') {
          return this.sat_starttime
        } else if (dayofweek === '7') {
          return this.sun_starttime
        }
      } else if (type === 'end') {
        if (dayofweek === '1') {
          return this.mon_endtime
        } else if (dayofweek === '2') {
          return this.tue_endtime
        } else if (dayofweek === '3') {
          return this.wed_endtime
        } else if (dayofweek === '4') {
          return this.thu_endtime
        } else if (dayofweek === '5') {
          return this.fri_endtime
        } else if (dayofweek === '6') {
          return this.sat_endtime
        } else if (dayofweek === '7') {
          return this.sun_endtime
        }
      }
    },
  },
}
</script>
<style>
.field-hint {
  padding: 8px 0;
  font-size: 12px;
  border-radius: 2px;
}

#newworkordercategory .el-textarea .el-textarea__inner {
  min-height: 50px !important;
  width: 350px;
  resize: none;
}
</style>
