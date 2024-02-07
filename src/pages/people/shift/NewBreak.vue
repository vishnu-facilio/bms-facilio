<template>
  <div>
    <div v-if="visibility">
      <el-dialog
        :fullscreen="true"
        :append-to-body="true"
        style="z-index: 1999"
        :visible="visibility"
        :title="$t('common.header.break')"
        custom-class="fc-dialog-form setup-dialog-right setup-dialog45 setup-dialog f-webform-right-dialog"
      >
        <div class="fc-pm-main-content-H">
          {{ $t('people.shift.break') }}
          <div class="fc-heading-border-width43 mT15"></div>
        </div>
        <div class="new-body-modal">
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">{{ $t('people.shift.break') }}</p>
              <el-input
                placeholder="Name"
                v-model="breakData.name"
                class="width100"
              ></el-input>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('people.shift.break_type') }}
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="breakData.breakType"
                  filterable
                  placeholder="Break Type"
                  class="width100"
                >
                  <el-option label="Paid" :value="1" :key="1"></el-option>
                  <el-option label="Unpaid" :value="2" :key="2"></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('people.shift.break_duration') }}
              </p>
              <el-time-picker
                format="HH:mm"
                value-format="timestamp"
                :picker-options="{ selectableRange: '00:00:00 - 10:00:00' }"
                v-model="breakData.breakTime"
                placeholder="Break Duration"
                class="width100"
              ></el-time-picker>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('people.shift.applicable_shifts') }}
              </p>
              <div
                class="el-select-block form-input fc-input-full-border-select2"
              >
                <el-select
                  multiple
                  collapse-tags
                  v-model="breakData.shifts"
                  filterable
                  placeholder="Shifts"
                  class="width100"
                >
                  <el-option
                    v-for="shift in shifts"
                    :label="shift.name"
                    :value="shift.id"
                    :key="shift.id"
                  ></el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('people.shift.break_type') }}
              </p>
              <div class="form-input fc-input-full-border-select2">
                <el-select
                  v-model="breakData.breakMode"
                  filterable
                  placeholder="Break Mode"
                  class="width100"
                >
                  <el-option label="Manual" :value="1" :key="1"></el-option>
                  <el-option label="Automatic" :value="2" :key="2"></el-option>
                </el-select>
              </div>
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
            @click="saveBreak()"
            :loading="saveLoading"
            >{{ saveLoading ? 'SAVING...' : 'SAVE' }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import moment from 'moment-timezone'
export default {
  props: ['visibility', 'breakEditData'],
  data() {
    return {
      breakData: {
        name: null,
        breakType: 1,
        breakTime: 0,
        breakMode: 1,
        shifts: [],
      },
      saveLoading: false,
      shifts: null,
    }
  },
  computed: {},
  created() {
    this.$store.dispatch('loadShifts')
  },
  mounted() {
    this.loadShifts()
    if (this.breakEditData) {
      this.fillBreakEditObject()
    } else {
      this.breakData.breakTime = moment(0, 'h')
    }
  },
  methods: {
    fillBreakEditObject() {
      this.breakData.name = this.breakEditData.name
      if (this.breakEditData.breakTime > 0) {
        this.breakData.breakTime = moment()
          .startOf('day')
          .seconds(this.breakEditData.breakTime)
          .valueOf()
      }
      if (this.breakEditData.breakType > 0) {
        this.breakData.breakType = this.breakEditData.breakType
      }
      if (this.breakEditData.shifts) {
        for (let shift of this.breakEditData.shifts) {
          this.breakData.shifts.push(shift.id)
        }
      }
    },
    cancel() {
      this.$emit('update:visibility', false)
    },
    saveBreak() {
      let self = this
      if (!this.isValid(this.breakData.name)) {
        this.$message.error('Enter Name')
        return
      }
      if (!this.isValid(this.breakData.breakTime)) {
        this.$message.error('Enter Break Time')
        return
      }
      if (this.breakData.shifts.length === 0) {
        this.$message.error('Enter Atleast One Shift')
        return
      }
      let tempBreakData = this.$helpers.cloneObject(this.breakData)
      delete tempBreakData.shifts
      tempBreakData['shifts'] = []
      for (let temp of this.breakData.shifts) {
        tempBreakData.shifts.push({ id: temp })
      }
      tempBreakData['breakTime'] = this.getTimeinSeconds(
        this.breakData.breakTime
      )
      if (this.breakEditData && this.breakEditData.id) {
        tempBreakData['id'] = this.breakEditData.id
      }
      this.saveLoading = true
      this.$http
        .post('/v2/break/addOrUpdate', { breakContext: tempBreakData })
        .then(response => {
          if (response.data.responseCode === 0) {
            if (this.breakEditData) {
              self.$message.success('Break Edited Successfully')
            } else {
              self.$message.success('Break Added Successfully')
            }
            self.$emit('saved')
            self.cancel()
            self.saveLoading = false
          } else {
            self.$message.error(response.data.message)
            self.saveLoading = false
          }
        })
        .catch(error => {
          console.log(error)
          self.saveLoading = false
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
      let self = this
      this.$http
        .get('/v2/shift/list')
        .then(response => {
          if (response.data.responseCode === 0) {
            self.shifts = response.data.result.shifts
          }
        })
        .catch(error => {
          console.log(error)
        })
    },
  },
}
</script>
