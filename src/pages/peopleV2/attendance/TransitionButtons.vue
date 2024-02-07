<template>
  <div class="transition-buttons">
    <el-skeleton v-if="transition.isLoading" :rows="1" animated />
    <div v-else>
      <el-button
        v-for="(button, index) in transition.buttons"
        :key="index"
        size="small"
        @click="processTransition(button)"
      >
        {{ getDisplayNameForTransition(button) }}
      </el-button>
      <!-- attendance transition dialogue -->
      <el-dialog
        v-if="transition.showTransitionDialogue"
        :title="transition.title"
        :visible.sync="transition.showTransitionDialogue"
        :append-to-body="true"
        width="30%"
        :custom-class="[
          allowDateTimeSelector ? 'for-attendance' : 'for-my-attendance',
        ]"
      >
        <!-- 1st row -->
        <div class="d-flex justify-content-space">
          <div v-if="allowDateTimeSelector" style="width: 100%">
            <div class="label">
              {{ $t('common.products.date') }}
            </div>
            <div class="mT10">
              <FDatePicker
                style="width: 100%"
                v-model="transition.selectedDate"
                :type="'datetime'"
                class="fc-input-full-border2 form-date-picker"
              ></FDatePicker>
            </div>
          </div>
          <div
            v-if="showBreakPicker()"
            :class="allowDateTimeSelector ? 'mL10' : ''"
            style="width: 100%"
          >
            <div class="label">
              {{ $t('common.products.break') }}
            </div>
            <div class="mT10">
              <FLookupField
                style="width: 100%"
                :model.sync="transition.selectedBreak"
                :field="breakFieldWithFilter"
                :hideLookupIcon="true"
              />
            </div>
          </div>
        </div>

        <!-- 2nd row -->
        <div :class="allowDateTimeSelector ? 'mT20' : ''">
          <div class="label">
            {{ $t('common.products.notes') }}
          </div>
          <div class="mT10">
            <el-input
              type="textarea"
              :autosize="{ minRows: 4, maxRows: 6 }"
              resize="none"
              v-model="transition.notes"
              :placeholder="`Add notes for ${transition.title}`"
              class="fc-input-full-border-textarea"
            ></el-input>
          </div>
        </div>

        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-cancel"
            @click="toggleShowTransitionDialogue"
            >{{ $t('common.shift_planner.cancel') }}</el-button
          >
          <el-button
            v-if="updatingTransaction"
            type="primary"
            class="modal-btn-save"
            :loading="transition.updatingState"
            :disabled="disableUpdateButton()"
            @click="updateTransaction"
            >{{ $t('common.shift_planner.update') }}</el-button
          >
          <el-button
            v-else
            type="primary"
            class="modal-btn-save"
            :loading="transition.updatingState"
            :disabled="disableUpdateButton()"
            @click="addTransaction"
            >{{ $t('common.shift_planner.update') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { API } from '@facilio/api'
import PeopleMixin from 'src/pages/peopleV2/peopleMixin.vue'
import FLookupField from '@/forms/FLookupField'
export default {
  name: 'TransitionButtons',
  props: {
    employeeID: {
      required: true,
      type: Number,
    },
    allowDateTimeSelector: {
      required: true,
      type: Boolean,
    },
    showAllButtons: {
      required: false,
      type: Boolean,
    },
  },
  components: {
    FLookupField,
    FDatePicker,
  },
  mixins: [PeopleMixin],

  data() {
    return {
      employeeShift: null,
      breakField: {
        isDataLoading: false,
        lookupModuleName: 'break',
        field: {
          lookupModule: {
            name: 'break',
            displayName: 'Break',
          },
        },
        filters: {},
      },
      updatingTransaction: false,
      transition: {
        id: null,
        title: null,
        name: null,

        isLoading: true,
        buttons: null,
        showTransitionDialogue: false,

        selectedDate: null,
        selectedBreak: null,
        notes: null,

        updatingState: false,
      },
    }
  },
  created() {
    this.fetchAttendanceTransitionButtons(this.employeeID)
    this.fetchEmployeeShift()
    this.$root.$on('edit-attendance-transaction', tx => {
      this.setDataForTransactionUpdate(tx)
    })
  },
  computed: {
    breakFieldWithFilter() {
      let shiftField = {
        isDataLoading: false,
        lookupModuleName: 'break',
        field: {
          lookupModule: {
            name: 'break',
            displayName: 'Break',
          },
        },
        filters: {},
      }
      const shiftId = this.employeeShift?.shiftId
      if (shiftId) {
        shiftField['filters'] = {
          shifts: {
            operatorId: 90,
            value: [shiftId + ''],
          },
        }
      }
      return shiftField
    },
  },
  methods: {
    async updateTransaction() {
      let { transition } = this || {}
      const transactionTime = transition.selectedDate.valueOf() || null
      const transactionType = transition.name || null
      const id = transition.id || null

      const transaction = {
        transactionTime,
        transactionType,
      }

      if (transition.selectedBreak) {
        transaction.shiftBreak = { id: transition.selectedBreak }
      }

      if (transition.notes) {
        transaction.notes = transition.notes
      }

      await API.updateRecord('attendanceTransaction', {
        id: id,
        data: transaction,
      })

      this.clearTransitionDialogue()
      this.toggleShowTransitionDialogue()
      this.$root.$emit('refresh-attendance')
    },
    setDataForTransactionUpdate(tx) {
      this.updatingTransaction = true
      this.transition.showTransitionDialogue = true

      const transitionDisplayName = this.getDisplayNameForTransition(
        tx.transactionType
      )
      this.transition.name = tx.transactionType
      this.transition.title = `Update ${transitionDisplayName}`
      this.transition.selectedDate = tx.transactionTime
      this.transition.id = tx.id
      this.transition.notes = tx.notes
    },
    async fetchEmployeeShift() {
      const today = new Date().setHours(0, 0, 0, 0).valueOf()
      const from = today
      const to = today
      await this.fetchShift(from, to, this.employeeID)
    },
    async fetchShift(rangeFrom, rangeTo, employeeID) {
      if (!(rangeFrom || rangeTo || employeeID)) {
        console.warn('precondition failure in fetching shift')
        return
      }
      this.transition.isLoading = true
      let route = `/v3/shiftplanner/view?rangeFrom=${rangeFrom}&rangeTo=${rangeTo}&peopleId=${employeeID}`
      let { error, data } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        this.employeeShift = data.shifts[0]
      }
      this.transition.isLoading = false
    },

    showBreakPicker() {
      if (this.updatingTransaction) {
        return false
      }
      return this.transition.name === 'BREAK'
    },
    disableUpdateButton() {
      if (this.showBreakPicker()) {
        if (this.transition.selectedBreak === null) {
          return true
        }
      }
      return false
    },
    async fetchAttendanceTransitionButtons(peopleID) {
      if (this.showAllButtons) {
        this.transition.buttons = [
          'CHECK_IN',
          'BREAK',
          'RESUME_WORK',
          'CHECK_OUT',
        ]
        return
      }
      this.transition.isLoading = true
      let route = `/v3/attendance/transition?peopleID=${peopleID}`
      let { error, data } = await API.get(route)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        this.transition.buttons = data.attendanceTransitions
      }
      this.transition.isLoading = false
    },
    processTransition(name) {
      this.transition.name = name
      this.transition.title = this.getDisplayNameForTransition(name)
      this.transition.selectedDate = Date.now()
      this.toggleShowTransitionDialogue()
    },
    toggleShowTransitionDialogue() {
      this.transition.showTransitionDialogue = !this.transition
        .showTransitionDialogue
    },
    clearTransitionDialogue() {
      this.transition.selectedDate = null
      this.transition.selectedBreak = null
      this.transition.notes = null
    },
    async addTransaction() {
      const people = this.employeeID
      const transactionTime = this.transition?.selectedDate.valueOf() || null
      const transactionType = this.transition?.name || null
      const sourceType = 'WEB'

      if (!(transactionTime && transactionType && sourceType && people)) {
        console.warn('create attendance transaction preconditon failure')
        return
      }

      const transaction = {
        transactionTime,
        transactionType,
        sourceType,
        people: { id: people },
      }

      if (this.transition?.selectedBreak) {
        transaction.shiftBreak = { id: this.transition.selectedBreak }
      }

      if (this.transition?.notes) {
        transaction.notes = this.transition.notes
      }
      this.transition.updatingState = true
      const { error } = await API.createRecord('attendanceTransaction', {
        data: transaction,
      })
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      }

      this.clearTransitionDialogue()
      this.toggleShowTransitionDialogue()
      await this.fetchAttendanceTransitionButtons(this.employeeID)

      this.transition.updatingState = false

      if (transactionType === 'CHECK_OUT' || transactionType === 'CHECK_IN') {
        this.$root.$emit('refresh-attendance')
      }
    },
  },
}
</script>

<style lang="scss">
.transition-buttons {
  .el-button {
    border: 1px solid #38b2c2;
  }
}
</style>

<style lang="scss">
.for-attendance {
  .el-dialog__body {
    min-height: 320px;
  }
}

.for-my-attendance {
  .el-dialog__body {
    min-height: 240px;
  }
}
</style>
