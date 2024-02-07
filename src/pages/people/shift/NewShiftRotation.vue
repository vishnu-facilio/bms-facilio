<template>
  <div>
    <div v-if="visibility">
      <el-dialog
        :visible="visibility"
        :fullscreen="true"
        :before-close="cancel"
        :title="$t('common.products.shift_rotation')"
        custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog fc-item-type-summary-dialog"
        style="z-index: 999999"
      >
        <div class="fc-pm-main-content-H">
          {{ $t('people.shift.shift_rotation') }}
          <div class="fc-heading-border-width43 mT15"></div>
        </div>
        <div v-if="loading" class="mT50 fc-webform-loading">
          <spinner :show="loading" size="80"></spinner>
        </div>
        <div v-else class="new-body-modal">
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('people.shift.scheduler_name') }}
              </p>
              <el-input
                :placeholder="$t('common.products.name')"
                v-model="formData.schedularName"
                class="width100"
              ></el-input>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('people.shift.scheduler_frequency') }}
              </p>
              <el-select
                v-model="formData.schedularFrequency"
                placeholder="Scheduler Frequency"
                @change="
                  schedularFrequencyChangeActions(formData.schedularFrequency)
                "
                class="width100"
              >
                <el-option
                  v-for="data in schedulerFrequencyList"
                  :key="'freq_' + data.id"
                  :label="data.label"
                  :value="data.id"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('common.wo_report.time_of_schedule') }}
              </p>
              <el-time-picker
                format="HH:mm"
                value-format="timestamp"
                v-model="formData.timeOfSchedule"
                :placeholder="$t('common.wo_report.time_of_schedule')"
                class="width100"
              ></el-time-picker>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('common.header.day_of_schedule') }}
              </p>
              <el-select
                v-model="formData.schedularDay"
                :disabled="schedulerDayList.length == 0"
                placeholder="Select"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="(objList, index) in schedulerDayList"
                  :key="'shday_' + index"
                  :label="objList.label"
                  :value="objList.id"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
          <p class="fc-input-label-txt mT20 p0">
            {{ $t('common._common.applicable_for') }}
          </p>
          <el-row
            v-for="(data, key) in formData.applicableFor"
            class="criteria-condition-block"
            :key="'app_for_' + key"
            style="margin-left: -10px;"
          >
            <el-col :span="6">
              <el-select
                v-model="data.applicableForType"
                @change="getFieldDrop(data)"
                :placeholder="$t('common._common.select')"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="(field, index) in applicableForTypes"
                  :key="'ap_tp_' + index"
                  :label="field.name"
                  :value="field.id"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="14" class="pL20">
              <el-select
                v-model="data.applicableForId"
                :placeholder="$t('common._common.select')"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="(objList, index) in data.applicableForDataType"
                  :key="'ap_id_' + index"
                  :label="objList.name"
                  :value="objList.id"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="3" class="mT5 mL5 border-left">
              <img
                src="~assets/add-icon.svg"
                v-if="key + 1 === formData.applicableFor.length"
                style="height:18px;width:18px;"
                class="delete-icon"
                @click="addApplicableFor"
              />
              <img
                src="~assets/remove-icon.svg"
                v-if="!(formData.applicableFor.length === 1)"
                style="height:18px;width:18px;margin-right: 3px;"
                class="delete-icon"
                @click="deleteApplicableFor(key)"
              />
            </el-col>
          </el-row>
          <p class="fc-input-label-txt p0 mT10">
            {{ $t('common.products.shift_rotations') }}
          </p>
          <el-row
            v-for="(data, key) in formData.shiftRotations"
            class="criteria-condition-block"
            :key="'sr_' + key"
            style="margin-left: -10px;"
          >
            <el-col :span="10">
              <el-select
                v-model="data.fromShiftId"
                clearable
                :placeholder="$t('common._common.select')"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="(shift, index) in shifts"
                  :key="'sh_' + index"
                  :label="shift.name"
                  :value="shift.id"
                  v-if="shift.id !== data.toShiftId"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="10" class="pL20">
              <el-select
                v-model="data.toShiftId"
                clearable
                :placeholder="$t('common._common.select')"
                class="fc-input-full-border-select2 width100"
              >
                <el-option
                  v-for="(shift, index) in shifts"
                  :key="'shift_' + index"
                  :label="shift.name"
                  :value="shift.id"
                  v-if="shift.id !== data.fromShiftId"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="4" class="mT5 mL5 border-left">
              <img
                src="~assets/add-icon.svg"
                v-if="key + 1 === formData.shiftRotations.length"
                style="height:18px;width:18px;"
                class="delete-icon"
                @click="addShiftRotation"
              />
              <img
                src="~assets/remove-icon.svg"
                v-if="!(formData.shiftRotations.length === 1)"
                style="height:18px;width:18px;margin-right: 3px;"
                class="delete-icon"
                @click="deleteShiftRotation(key)"
              />
            </el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancel()">{{
            $t('people.shift.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="saveShiftRotation()"
            :loading="saveLoading"
            >{{
              saveLoading
                ? $t('common._common._saving')
                : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import moment from 'moment-timezone'
import { mapState } from 'vuex'
export default {
  props: ['visibility', 'editData'],
  data() {
    return {
      formData: {
        schedularName: null,
        schedularFrequency: null,
        timeOfSchedule: 0,
        schedularDay: null,
        shiftRotations: [
          {
            fromShiftId: null,
            toShiftId: null,
          },
        ],
        applicableFor: [
          {
            applicableForId: null,
            applicableForType: null,
          },
        ],
      },
      editObj: null,
      loading: false,
      schedulerDayList: [],
      schedulerFrequencyList: [
        {
          id: 1,
          label: this.$t('common._common.daily'),
        },
        {
          id: 2,
          label: this.$t('common._common.weekly'),
        },
        {
          id: 3,
          label: this.$t('common._common.monthly'),
        },
      ],
      saveLoading: false,
      shifts: null,
      applicableForTypes: [
        {
          id: 1,
          name: 'User',
        },
        {
          id: 2,
          name: 'Group',
        },
        {
          id: 3,
          name: 'Role',
        },
      ],
      requesterField: [
        {
          name: 'Requester',
          id: 'requester',
        },
      ],
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
      teams: state => state.groups,
      roles: state => state.roles,
    }),
  },
  created() {
    this.$store.dispatch('loadShifts')
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadGroups')
  },
  mounted() {
    this.loadShifts()
    if (this.editData) {
      this.getDetails()
    } else {
      this.formData.timeOfSchedule = moment(0, 'h')
    }
  },
  methods: {
    getDetails() {
      this.loading = true
      this.$http
        .get('v2/shiftRotation/' + this.editData.id)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.editObj = response.data.result.shiftRotation
            this.loading = false
            this.fillEditObject()
          } else {
            this.$message.error(response.data.message)
            this.loading = false
          }
        })
        .catch(error => {
          this.$message.error(error)
          this.loading = false
        })
    },
    fillEditObject() {
      this.formData.schedularName = this.editObj.schedularName
      // this.formData.applicableFor = this.editObj.applicableFor
      this.formData.shiftRotations = this.editObj.shiftRotations
      this.formData.schedularFrequency = this.editObj.schedularFrequency
      this.schedularFrequencyChangeActions(this.editObj.schedularFrequency)
      this.formData.schedularDay = parseInt(this.editObj.schedularDay)
      if (this.editObj.schedularFrequency === 1) {
        this.formData.schedularDay = null
      }
      if (this.editObj.timeOfSchedule > 0) {
        this.formData.timeOfSchedule = moment()
          .startOf('day')
          .seconds(this.editObj.timeOfSchedule)
          .valueOf()
      }
      if (this.editObj.applicableFor) {
        this.editObj.applicableFor.forEach(appFor => {
          let tempAppforId = appFor.applicableForId
          this.getFieldDrop(appFor)
          appFor.applicableForId = parseInt(tempAppforId)
          // if (appFor.type === 1) {
          //   if (appFor.applicableForId > 0) {
          //     appFor.applicableForId = appFor.applicableForId
          //   } else if (appFor.fieldId > 0) {
          //     appFor.applicableForId = 'requester'
          //   }
          // } else if (appFor.type === 2) {
          //   appFor.applicableForId = parseInt(appFor.applicableForId)
          // } else if (appFor.type === 3) {
          //   appFor.applicableForId = parseInt(appFor.applicableForId)
          // }
        })
        this.formData.applicableFor = this.editObj.applicableFor
      }
    },
    cancel() {
      this.$emit('update:visibility', false)
    },
    saveShiftRotation() {
      if (!this.isValid(this.formData.schedularName)) {
        this.$message.error(
          this.$t('common.header.please_input_scheduler_name')
        )
        return
      }
      if (!this.isValid(this.formData.timeOfSchedule)) {
        this.$message.error(
          this.$t('common.header.please_input_time_of_schedule')
        )
        return
      }
      if (
        (this.schedularFrequency === 2 || this.schedularFrequency === 3) &&
        !this.isValid(this.formData.schedularDay)
      ) {
        this.$message.error(
          this.$t('common.header.please_input_day_of_schedule')
        )
        return
      }
      if (this.formData.shiftRotations.length === 0) {
        this.$message.error(
          this.$t('common.header.please_input_atleast_one_shift_rotation')
        )
        return
      }
      let tempData = this.$helpers.cloneObject(this.formData)
      tempData.timeOfSchedule = this.getTimeinSeconds(tempData.timeOfSchedule)
      let url
      if (this.editData) {
        url = '/v2/shiftRotation/update'
        tempData['id'] = this.editData.id
      } else {
        url = '/v2/shiftRotation/add'
      }
      this.saveLoading = true
      this.$http
        .post(url, { shiftRotation: tempData })
        .then(response => {
          if (response.data.responseCode === 0) {
            if (this.editData) {
              this.$message.success(
                this.$t('common.products.shift_rotation_edited_successfully')
              )
            } else {
              this.$message.success(
                this.$t('common.products.shift_rotation_added_successfully')
              )
            }
            this.$emit('saved')
            this.cancel()
            this.saveLoading = false
          } else {
            this.$message.error(response.data.message)
            this.saveLoading = false
          }
        })
        .catch(error => {
          console.log(error)
          this.saveLoading = false
        })
    },
    getTimeinSeconds(val) {
      let dt = new Date(val)
      let secs = 60 * (dt.getMinutes() + 60 * dt.getHours())
      return secs
    },
    isValid(val) {
      if (val !== null && val !== '' && val !== undefined) {
        return true
      } else {
        return false
      }
    },
    loadShifts() {
      this.$http
        .get('/v2/shift/list')
        .then(response => {
          if (response.data.responseCode === 0) {
            this.shifts = response.data.result.shifts
          }
        })
        .catch(error => {
          console.log(error)
        })
    },
    deleteApplicableFor(index) {
      this.$delete(this.formData.applicableFor, index)
    },
    addApplicableFor() {
      this.formData.applicableFor.push({
        applicableForId: null,
        applicableForType: null,
      })
    },
    deleteShiftRotation(index) {
      this.$delete(this.formData.shiftRotations, index)
    },
    addShiftRotation() {
      this.formData.shiftRotations.push({
        fromShiftId: null,
        toShiftId: null,
      })
    },
    getFieldDrop(applicableFor) {
      applicableFor.applicableForId = null
      applicableFor.applicableForDataType = []
      if (applicableFor.applicableForType === 1) {
        applicableFor.applicableForDataType = [
          ...this.requesterField,
          ...this.users,
        ]
      } else if (applicableFor.applicableForType === 2) {
        applicableFor.applicableForDataType = this.teams
      } else if (applicableFor.applicableForType === 3) {
        applicableFor.applicableForDataType = this.roles
      }
    },
    schedularFrequencyChangeActions(val) {
      this.formData.schedularDay = null
      if (val === 2) {
        this.schedulerDayList = [
          {
            id: 1,
            label: this.$t('common._common.monday'),
          },
          {
            id: 2,
            label: this.$t('common._common.tuesday'),
          },
          {
            id: 3,
            label: this.$t('common._common.wednesday'),
          },
          {
            id: 4,
            label: this.$t('common._common._thursday'),
          },
          {
            id: 5,
            label: this.$t('common._common.friday'),
          },
          {
            id: 6,
            label: this.$t('common._common.saturday'),
          },
          {
            id: 7,
            label: this.$t('common._common.sunday'),
          },
        ]
      } else if (val === 3) {
        let tempList = []
        for (let i = 1; i <= 28; i++) {
          tempList.push({ id: i, label: i })
        }
        this.schedulerDayList = tempList
      } else {
        this.schedulerDayList = []
      }
    },
  },
}
</script>
<style scoped>
.criteria-condition-block {
  width: 100%;
  display: flex;
  flex-direction: row;
  flex-wrap: nowrap;
  align-items: center;
  background: transparent;
  padding: 15px 20px 15px 10px;
  cursor: pointer;
  border-radius: 3px;
  transition: 0.3s all ease-in-out;
  -webkit-transition: 0.3s all ease-in-out;
}
.criteria-condition-block:hover {
  background-color: #f1f8fa;
  transition: 0.3s all ease-in-out;
  -webkit-transition: 0.3s all ease-in-out;
}
.criteria-condition-block .el-input.is-disabled .el-input__inner {
  background: none;
}
.criteria-condition-block .creteria-delete-icon {
  min-width: 45px;
}
.criteria-condition-block .delete-icon {
  position: relative;
  top: 0;
  left: 10px;
  margin-right: 5px;
}
.criteria-condition-block:hover .delete-icon {
  cursor: pointer;
  position: relative;
  visibility: visible;
}
.criteria-condition-block .el-input__inner {
  background: transparent;
  margin-bottom: 0;
}
</style>
