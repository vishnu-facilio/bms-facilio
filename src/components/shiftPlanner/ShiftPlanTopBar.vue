<template>
  <div class="d-flex shift-plan-top-bar">
    <div class="d-flex shift-plan-top-bar-left-child">
      <el-radio-group v-model="viewMode" size="small" @change="updateViewMode">
        <el-radio-button label="Calendar View"></el-radio-button>
        <el-radio-button label="List View"></el-radio-button>
      </el-radio-group>

      <new-date-picker
        class="picker"
        :zone="$timezone"
        :dateObj="dateFilter"
        :tabs="dateRange.enabledTabs"
        @date="setDateFilter"
      ></new-date-picker>
    </div>
    <div>
      <div class="d-flex">
        <el-dropdown
          trigger="click"
          @command="exportPlanner"
          placement="bottom"
          v-if="hasExportPermission"
        >
          <fc-icon
            class="export-button"
            group="default"
            name="download"
            size="20"
          ></fc-icon>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="xlsx">{{
              $t('common.shift_planner.export_excel')
            }}</el-dropdown-item>
            <el-dropdown-item command="csv">{{
              $t('common.shift_planner.export_csv')
            }}</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

        <!-- CTA Button -->
        <button
          v-if="hasUpdatePermission"
          class="fc-create-btn create-btn mL20"
          @click="toggleCTA"
        >
          {{ $t('common.shift_planner.associate_people') }}
        </button>
        <el-dialog
          v-if="showCTADialog"
          :title="$t('common.shift_planner.associate_people')"
          :visible.sync="showCTADialog"
          :append-to-body="true"
          width="40%"
          @close="closeCTA"
        >
          <div class="d-flex justify-content-space">
            <div>
              <div class="label">
                {{ $t('common.shift_planner.startDate') }}
              </div>
              <el-date-picker
                v-model="cta.startDate"
                type="date"
                :format="dateFormat"
                placeholder="Select Start Date"
                class="p5"
                :picker-options="{ disabledDate: disablePast }"
              >
              </el-date-picker>
            </div>
            <div>
              <div class="label">{{ $t('common.shift_planner.endDate') }}</div>
              <el-date-picker
                v-model="cta.endDate"
                type="date"
                :format="dateFormat"
                placeholder="Select End Date"
                class="p5"
                :picker-options="{ disabledDate: disablePast }"
              >
              </el-date-picker>
            </div>
          </div>
          <div>
            <div class="label">{{ $t('common.shift_planner.people') }}</div>
            <FLookupField
              :model.sync="cta.employee"
              :field="cta.employeeField"
              :hideLookupIcon="true"
              class="p5"
            />
          </div>
          <div>
            <div class="label">{{ $t('common.shift_planner.shift') }}</div>
            <FLookupField
              :model.sync="cta.shift"
              :field="cta.shiftField"
              :hideLookupIcon="true"
              class="p5"
            />
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="closeCTA">{{
              $t('common.shift_planner.cancel')
            }}</el-button>
            <el-button
              type="primary"
              class="modal-btn-save"
              :loading="cta.updatingShift"
              :disabled="disableButton"
              @click="updateShift()"
              >{{ $t('common.shift_planner.update') }}</el-button
            >
          </div>
        </el-dialog>
      </div>
    </div>
  </div>
</template>
<script>
import FLookupField from '@/forms/FLookupField'
import NewDateHelper from 'src/components/mixins/NewDateHelper'
import NewDatePicker from '@/NewDatePicker'
import { API } from '@facilio/api'
import { mapGetters, mapState } from 'vuex'
import PeopleMixin from 'src/pages/peopleV2/peopleMixin.vue'
function computeDefaultRange() {
  const date = new Date()
  const firstDay = new Date(date.getFullYear(), date.getMonth(), 1)
  const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0)
  return [firstDay, lastDay]
}

export default {
  components: {
    FLookupField,
    NewDatePicker,
  },
  mixins: [PeopleMixin],
  created() {
    this.$root.$on('edit-day-record', rec => {
      this.togglePrefilledCTA(rec)
    })
  },
  data() {
    return {
      disablePast: function(date) {
        return date < new Date().setHours(0, 0, 0, 0).valueOf()
      },
      dateRange: {
        enabledTabs: {
          enabledTabs: ['W', 'M'],
        },
      },
      dateFilter: NewDateHelper.getDatePickerObject(
        64, // month
        computeDefaultRange()
      ),
      showCTADialog: false,
      timelineRange: null,
      viewMode: 'Calendar View',

      cta: {
        shiftField: {
          isDataLoading: false,
          lookupModuleName: 'shift',
          field: {
            lookupModule: {
              name: 'shift',
              displayName: 'Shift',
            },
          },
          filters: { isActive: { operatorId: 15, value: ['true'] } },

          multiple: false,
        },
        updatingShift: false,
        startDate: null,
        endDate: null,
        employeeField: {
          isDataLoading: false,
          lookupModuleName: 'people',
          field: {
            lookupModule: {
              name: 'people',
              displayName: 'People',
            },
          },
          filters: {
            peopleType: {
              operatorId: 54,
              value: ['2', '3'],
            },
          },
          multiple: true,
        },
        employee: null,
        shift: null,
      },
    }
  },
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
    }),
    ...mapGetters('webtabs', ['tabHasPermission']),
    hasUpdatePermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('UPDATE', currentTab)
    },
    hasExportPermission() {
      let { currentTab, tabHasPermission } = this
      return tabHasPermission('EXPORT', currentTab)
    },
    dateFormat() {
      let { $dateformat } = this
      let dateformat = ($dateformat || '').replaceAll('Y', 'y')
      dateformat = (dateformat || '').replaceAll('D', 'd')
      return dateformat ? dateformat : 'dd/MMM/yyyy'
    },

    disableButton() {
      const { shift, startDate, endDate, employee } = this.cta
      return !(
        shift != null &&
        startDate != null &&
        endDate != null &&
        employee != null &&
        employee.length > 0 &&
        startDate <= endDate
      )
    },
  },
  methods: {
    updateViewMode() {
      this.$emit('viewModeUpdate', this.viewMode)
    },
    setTimelineRange(from, to) {
      this.timelineRange = { from, to }
      this.$emit('timelineRangeUpdate', this.timelineRange)
    },
    toggleCTA() {
      this.showCTADialog = !this.showCTADialog
    },
    closeCTA() {
      this.cta.startDate = null
      this.cta.endDate = null
      this.cta.employee = null
      this.cta.shift = null
      this.showCTADialog = false
    },
    togglePrefilledCTA(details) {
      const employeeID = details?.employee?.id
      if (employeeID) {
        this.cta.employee = [employeeID]
      }

      const shiftID = details?.shift?.id
      if (shiftID) {
        this.cta.shift = shiftID
      }

      const startDate = details?.startDate
      if (startDate) {
        this.cta.startDate = new Date(startDate).setHours(0, 0, 0, 0)
      }

      const today = this.getTodayDate()
      if (startDate < today) {
        this.cta.startDate = today
      }

      const endDate = details?.endDate
      if (endDate) {
        this.cta.endDate = new Date(endDate).setHours(0, 0, 0, 0)
      }

      this.toggleCTA()
    },
    setDateFilter(data) {
      const range = data?.value
      if (!range) {
        return
      }
      const from = range[0]
      const to = range[1]

      this.setTimelineRange(from, to)
      this.dateFilter = data
    },
    updateShift() {
      this.cta.updatingShift = true
      const SHIFT_ID = this.cta?.shift || null
      const EMPLOYEE_ID = this.cta?.employee || null
      const SHIFT_START = this.cta?.startDate?.valueOf() || null
      const SHIFT_END = this.cta?.endDate?.valueOf() || null
      if (!(SHIFT_ID && EMPLOYEE_ID && SHIFT_START && SHIFT_END)) {
        console.warn('updateShift preconditon failure')
        return
      }
      if (SHIFT_END < SHIFT_START) {
        console.warn('updateShift preconditon failure')
        return
      }
      this.updateUserShift(SHIFT_ID, EMPLOYEE_ID, SHIFT_START, SHIFT_END)
      this.cta.updatingShift = false
    },
    async updateUserShift(shiftID, employees, shiftStart, shiftEnd) {
      const route = `/v3/shiftplanner/update`
      const payload = {
        shiftID,
        people: employees,
        shiftStart,
        shiftEnd,
      }
      let { error } = await API.post(route, payload)
      if (error) {
        this.$message.error('shift update failed')
      } else {
        this.$message.success('Shift update success')
        this.$root.$emit('reload-employee-shift')
      }
      this.resetCTAForm()
      this.toggleCTA()
    },
    resetCTAForm() {
      this.cta.startDate = null
      this.cta.endDate = null
      this.cta.shift = null
      this.cta.employee = null
    },
    exportPlanner(cmd) {
      if (this.viewMode === 'Calendar View') {
        this.$root.$emit('export-shift-planner-cal-' + cmd)
      } else {
        this.$root.$emit('export-shift-planner-list-' + cmd)
      }
    },
  },
}
</script>
<style scoped>
.shift-plan-top-bar {
  background-color: white;
  justify-content: space-between;
  height: 56px;
  font-size: 14px;
  padding-top: 19px;
  padding-left: 15px;
  padding-bottom: 18px;
  padding-right: 8px;
}
.create-btn {
  margin-top: -10px;
}
.label {
  padding: 5px;
  font-weight: 500;
}

.export-button:hover {
  cursor: pointer;
}
</style>
<style lang="scss">
.shift-plan-top-bar {
  .el-input__icon {
    display: flex;
    align-items: center;
  }
  .modal-dialog-footer {
    min-height: 300px;
  }
  .picker {
    margin-top: -2px;
    margin-left: 20px;
  }
}
.shift-plan-top-bar-left-child {
  margin-top: -8px;
}
.el-dialog__body {
  min-height: 320px;
}
</style>
