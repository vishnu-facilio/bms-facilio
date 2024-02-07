<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <el-form :model="value" :label-position="'top'">
      <div class="new-header-container">
        <div class="new-header-modal">
          <div class="new-header-text">
            <div v-if="isNew" class="setup-modal-title">New Shift</div>
            <div v-else class="setup-modal-title">Edit Shift Hours</div>
            <div class="heading-description">List of Shift Hours</div>
          </div>
        </div>
      </div>
      <div class="new-body-modal" style="margin-top: 0;">
        <div class="modal-form-input">
          <p class="new-label-text">Name</p>
          <el-input class="form-item" v-model="value.name"></el-input>
        </div>
        <div class="modal-form-input">
          <p class="new-label-text">Site</p>
          <el-select class="form-item" v-model="value.siteId">
            <el-option
              v-for="s in this.sites"
              :label="s.label"
              :value="s.id"
              :key="s.id"
            ></el-option>
          </el-select>
        </div>
        <div class="modal-form-input">
          <p class="new-label-text">Shift Hours</p>
          <el-row class="form-item">
            <el-radio
              v-model="shiftHoursType"
              label="same"
              class="form-item row"
            >
              <el-col :span="9">
                Same hours every day
              </el-col>
              <span v-show="!isDifferent">
                <el-col :span="6">
                  <el-select
                    class="form-item"
                    v-model="value.startTime"
                    style="width:100%"
                  >
                    <el-option
                      v-for="time in this.timesOption"
                      :label="time"
                      :value="time"
                      :key="time"
                    ></el-option>
                  </el-select>
                </el-col>
                <el-col :span="1">
                  to
                </el-col>
                <el-col :span="6">
                  <el-select
                    class="form-item"
                    v-model="value.endTime"
                    style="width:100%"
                  >
                    <el-option
                      v-for="time in this.timesOption"
                      :label="time"
                      :value="time"
                      :key="time"
                    ></el-option>
                  </el-select>
                </el-col>
              </span>
            </el-radio>
          </el-row>
          <el-row>
            <el-radio
              v-model="shiftHoursType"
              label="different"
              class="form-item"
            >
              Different hours every day
            </el-radio>
          </el-row>
        </div>
        <div class="modal-form-input">
          <p class="new-label-text">Shift Days</p>
        </div>
        <el-row v-for="d in weekdays" :key="d.label">
          <el-col :span="6">
            <el-checkbox v-model="d.checked"> {{ d.dayOfWeekVal }}</el-checkbox>
          </el-col>
          <div v-show="isDifferent && d.checked">
            <el-col :span="6">
              <el-select
                class="form-item"
                v-model="d.startTime"
                style="width:100%"
              >
                <el-option
                  v-for="time in timesOption"
                  :label="time"
                  :value="time"
                  :key="time"
                ></el-option>
              </el-select>
            </el-col>
            <el-col :span="1">
              to
            </el-col>
            <el-col :span="6">
              <el-select
                class="form-item"
                v-model="d.endTime"
                style="width:100%"
              >
                <el-option
                  v-for="time in timesOption"
                  :label="time"
                  :value="time"
                  :key="time"
                ></el-option>
              </el-select>
            </el-col>
          </div>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button type="primary" @click="save" class="modal-btn-save"
          >Save</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
export default {
  props: ['value', 'visibility', 'isNew'],
  created() {
    this.$store.dispatch('loadShifts')
  },
  mounted() {
    this.setHourOptions()
    this.getSites()
    this.setWeekdays()
  },
  computed: {
    isDifferent() {
      return !this.value.isSameTime
    },
    shiftHoursType: {
      get: function() {
        if (this.value.isSameTime) {
          return 'same'
        } else {
          return 'different'
        }
      },
      set: function(val) {
        if (val === 'same') {
          this.value.isSameTime = true
        } else if (val === 'different') {
          this.value.isSameTime = false
        }
      },
    },
  },
  data() {
    return {
      sites: [],
      timesOption: [],
      weekdays: [
        {
          checked: false,
          dayOfWeek: 1,
          dayOfWeekVal: 'MONDAY',
          startTime: '09:00',
          endTime: '17:00',
        },
        {
          checked: false,
          dayOfWeek: 2,
          dayOfWeekVal: 'TUESDAY',
          startTime: '09:00',
          endTime: '17:00',
        },
        {
          checked: false,
          dayOfWeek: 3,
          dayOfWeekVal: 'WEDNESDAY',
          startTime: '09:00',
          endTime: '17:00',
        },
        {
          checked: false,
          dayOfWeek: 4,
          dayOfWeekVal: 'THURSDAY',
          startTime: '09:00',
          endTime: '17:00',
        },
        {
          checked: false,
          dayOfWeek: 5,
          dayOfWeekVal: 'FRIDAY',
          startTime: '09:00',
          endTime: '17:00',
        },
        {
          checked: false,
          dayOfWeek: 6,
          dayOfWeekVal: 'SATURDAY',
          startTime: '09:00',
          endTime: '17:00',
        },
        {
          checked: false,
          dayOfWeek: 7,
          dayOfWeekVal: 'SUNDAY',
          startTime: '09:00',
          endTime: '17:00',
        },
      ],
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    save() {
      let param = this.value
      let days = this.weekdays.filter(e => e.checked)
      if (param.isSameTime) {
        days.forEach(e => {
          e.startTime = this.value.startTime
          e.endTime = this.value.endTime
        })
      } else {
        param.startTime = ''
        param.endTime = ''
      }
      param.days = days
      if (this.isNew) {
        this.$http.post('/shifts/add', { shift: param }).then(response => {
          this.$message.success('New Shift Added Successfully.')
          this.closeDialog()
          this.$emit('reset')
        })
      } else {
        this.$http.post('/shifts/update', { shift: param }).then(response => {
          this.$message.success('Shift Update Successfully.')
          this.closeDialog()
          this.$emit('reset')
        })
      }
    },
    setHourOptions() {
      for (let i = 0; i <= 23; i++) {
        let time = (i < 10 ? '0' + i : i) + ':'
        this.timesOption.push(time + '00')
        this.timesOption.push(time + '30')
      }
    },
    getSites() {
      this.$http.get('/campus').then(response => {
        if (response.data.records) {
          response.data.records.forEach(r => {
            this.sites.push({ label: r.name, id: r.id })
          })
        }
      })
    },
    isExist(val) {
      let res = this.findDay(val)
      if (res) {
        return true
      }
      return false
    },
    findDay(val) {
      return this.value.days.find(d => d.dayOfWeek === val)
    },
    setWeekdays() {
      this.weekdays.forEach(e => {
        let val = this.findDay(e.dayOfWeek)
        if (val) {
          e.label = val.label
          e.startTime = val.startTime
          e.endTime = val.endTime
          e.checked = true
        } else {
          e.checked = false
        }
      })
    },
  },
}
</script>
