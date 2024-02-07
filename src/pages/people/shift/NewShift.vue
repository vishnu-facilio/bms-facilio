<template>
  <div>
    <div v-if="visibility">
      <el-dialog
        :visible="visibility"
        :fullscreen="true"
        :before-close="cancel"
        :title="$t('common.header.shift')"
        custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog fc-item-type-summary-dialog"
        style="z-index: 999999"
      >
        <div class="fc-pm-main-content-H">
          {{ $t('people.shift.shift') }}
          <div class="fc-heading-border-width43 mT15"></div>
        </div>
        <div class="new-body-modal">
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">{{ $t('people.shift.name') }}</p>
              <el-input
                :placeholder="$t('common.products.name')"
                v-model="shiftData.name"
                class="width100"
              ></el-input>
            </el-col>
            <el-col :span="12">
              <div class="fc-color-picker mT50">
                <el-color-picker
                  v-model="shiftData.colorCode"
                  popper-class="fc-color-pallete"
                ></el-color-picker>
              </div>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt">
                {{ $t('people.shift.from_time') }}
              </p>
              <el-time-picker
                format="HH:mm"
                value-format="timestamp"
                v-model="shiftData.startTime"
                :placeholder="$t('common.wo_report.start_time')"
                class="width100"
              ></el-time-picker>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt">{{ $t('people.shift.to_time') }}</p>
              <el-time-picker
                format="HH:mm"
                value-format="timestamp"
                :picker-options="{ minTime: shiftData.startTime }"
                v-model="shiftData.endTime"
                :placeholder="$t('common.wo_report.end_time')"
                class="width100"
              ></el-time-picker>
            </el-col>
          </el-row>
          <div class="fc-input-label-txt mT40">
            {{ $t('people.shift.weekend_definition') }}
          </div>
          <el-table
            :data="tableData"
            class="mT10 inventory-inner-table width100"
          >
            <el-table-column
              width="150"
              label="DAYS"
              prop="day"
            ></el-table-column>
            <el-table-column label="WEEK">
              <template v-slot:header>
                <div
                  class="width100"
                  style="text-align: center; line-height: 10px;"
                >
                  {{ $t('common.wo_report.weeks_') }}
                </div>
              </template>
              <el-table-column :label="$t('common._common._all')">
                <template v-slot="scope">
                  <el-checkbox
                    v-model="scope.row.all"
                    @change="allCheckBoxChangeActions(scope.row)"
                  ></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column label="1st">
                <template v-slot="scope">
                  <el-checkbox
                    v-model="scope.row.one"
                    @change="checkBoxChangeActions(scope.row)"
                  ></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column label="2nd">
                <template v-slot="scope">
                  <el-checkbox
                    v-model="scope.row.two"
                    @change="checkBoxChangeActions(scope.row)"
                  ></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column label="3rd">
                <template v-slot="scope">
                  <el-checkbox
                    v-model="scope.row.three"
                    @change="checkBoxChangeActions(scope.row)"
                  ></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column label="4th">
                <template v-slot="scope">
                  <el-checkbox
                    v-model="scope.row.four"
                    @change="checkBoxChangeActions(scope.row)"
                  ></el-checkbox>
                </template>
              </el-table-column>
              <el-table-column label="5th">
                <template v-slot="scope">
                  <el-checkbox
                    v-model="scope.row.five"
                    @change="checkBoxChangeActions(scope.row)"
                  ></el-checkbox>
                </template>
              </el-table-column>
            </el-table-column>
          </el-table>
          <div></div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancel()">{{
            $t('people.shift.cancel')
          }}</el-button>
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="saveShift()"
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
export default {
  props: ['visibility', 'shiftEditData'],
  data() {
    return {
      shiftData: {
        name: null,
        startTime: 0,
        endTime: 0,
        colorCode: '#39b2c2',
      },
      tableData: [
        {
          day: 'Monday',
          key: 1,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Tuesday',
          key: 2,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Wednesday',
          key: 3,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Thursday',
          key: 4,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Friday',
          key: 5,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Saturday',
          key: 6,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
        {
          day: 'Sunday',
          key: 7,
          all: false,
          one: false,
          two: false,
          three: false,
          four: false,
          five: false,
        },
      ],
      saveLoading: false,
    }
  },
  computed: {},
  mounted() {
    if (this.shiftEditData) {
      this.fillShiftEditData()
    } else {
      this.fillNewShiftData()
    }
  },
  methods: {
    fillNewShiftData() {
      this.shiftData.startTime = moment(9, 'HH')
      this.shiftData.endTime = moment(18, 'HH')
    },
    fillShiftEditData() {
      let self = this
      this.shiftData.name = this.shiftEditData.name
      if (this.shiftEditData.startTime > 0) {
        this.shiftData.startTime = moment()
          .startOf('day')
          .seconds(this.shiftEditData.startTime)
          .valueOf()
      }
      if (this.shiftEditData.endTime > 0) {
        this.shiftData.endTime = moment()
          .startOf('day')
          .seconds(this.shiftEditData.endTime)
          .valueOf()
      }
      if (this.shiftEditData.weekendJSON) {
        for (let week in self.shiftEditData.weekendJSON) {
          for (let day of self.shiftEditData.weekendJSON[week]) {
            self.tableData[day - 1][this.getNumberWord(week)] = true
          }
        }
        for (let key in this.tableData) {
          this.checkBoxChangeActions(this.tableData[key])
        }
      }
      if (this.shiftEditData.colorCode) {
        this.shiftData.colorCode = this.shiftEditData.colorCode
      }
    },
    getNumberWord(val) {
      switch (parseInt(val)) {
        case 1:
          return 'one'
        case 2:
          return 'two'
        case 3:
          return 'three'
        case 4:
          return 'four'
        case 5:
          return 'five'
      }
    },
    cancel() {
      this.$emit('update:visibility', false)
    },
    saveShift() {
      let self = this
      if (!this.isValid(this.shiftData.name)) {
        this.$message.error(this.$t('common._common.enter_name'))
        return
      }
      if (!this.isValid(this.shiftData.startTime)) {
        this.$message.error(this.$t('common._common.enter_start_time'))
        return
      }
      if (!this.isValid(this.shiftData.endTime)) {
        this.$message.error(this.$t('common._common.enter_end_time'))
        return
      }
      let tempShiftData = this.$helpers.cloneObject(this.shiftData)
      tempShiftData['weekend'] = this.generateWeekendJson()
      tempShiftData['startTime'] = this.getTimeinSeconds(
        this.shiftData.startTime
      )
      tempShiftData['endTime'] = this.getTimeinSeconds(this.shiftData.endTime)
      if (this.shiftEditData && this.shiftEditData.id) {
        tempShiftData['id'] = this.shiftEditData.id
        this.saveLoading = true
        this.$http
          .post('/v2/shift/update', { shift: tempShiftData })
          .then(response => {
            if (response.data.responseCode === 0) {
              self.$message.success(
                this.$t('common.products.shift_edited_successfully')
              )
              self.saveLoading = false
              self.$emit('saved')
              self.cancel()
            } else {
              self.$message.error(response.data.message)
              self.saveLoading = false
            }
          })
          .catch(error => {
            console.log(error)
            self.saveLoading = false
          })
      } else {
        this.saveLoading = true
        this.$http
          .post('/v2/shift/add', { shift: tempShiftData })
          .then(response => {
            if (response.data.responseCode === 0) {
              self.$message.success(
                this.$t('common.products.shift_added_successfully')
              )
              self.saveLoading = false
              self.$emit('saved')
              self.cancel()
            } else {
              self.$message.error(response.data.message)
              self.saveLoading = false
            }
          })
          .catch(error => {
            console.log(error)
            self.saveLoading = false
          })
      }
    },
    getTimeinSeconds(val) {
      let dt = new Date(val)
      let secs = 60 * (dt.getMinutes() + 60 * dt.getHours())
      return secs
    },
    generateWeekendJson() {
      let weekend = { 1: [], 2: [], 3: [], 4: [], 5: [] }
      for (let tab of this.tableData) {
        if (tab.one) {
          weekend['1'].push(tab.key)
        }
        if (tab.two) {
          weekend['2'].push(tab.key)
        }
        if (tab.three) {
          weekend['3'].push(tab.key)
        }
        if (tab.four) {
          weekend['4'].push(tab.key)
        }
        if (tab.five) {
          weekend['5'].push(tab.key)
        }
      }
      for (let key in weekend) {
        if (weekend[key].length === 0) {
          delete weekend[key]
        }
      }
      return JSON.stringify(weekend)
    },
    isValid(val) {
      if (val !== null && val !== '' && val !== undefined) {
        return true
      } else {
        return false
      }
    },
    allCheckBoxChangeActions(val) {
      val.one = val.all
      val.two = val.all
      val.three = val.all
      val.four = val.all
      val.five = val.all
    },
    checkBoxChangeActions(val) {
      if (val.one && val.two && val.three && val.four && val.five) {
        val.all = true
      } else {
        val.all = false
      }
    },
  },
}
</script>
