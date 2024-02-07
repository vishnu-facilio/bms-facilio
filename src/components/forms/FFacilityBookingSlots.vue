<template>
  <div class="f-booking-slots-container">
    <div class="d-flex">
      <el-form-item
        :required="true"
        prop="bookingDate"
        label="Date"
        class="section-items form-two-column"
      >
        <FDatePicker
          v-model="model.bookingDate"
          :type="'date'"
          @change="fetchFacilitySlots(true)"
          :picker-options="pickerOptions"
          class="fc-input-full-border2 form-date-picker"
          @initialized="fetchFacilitySlots(false)"
        ></FDatePicker>
      </el-form-item>
      <el-form-item
        prop="bookingSlots"
        label="Slots"
        class="section-items form-two-column"
      >
        <div
          v-if="$validation.isEmpty(facilitySlots)"
          disabled
          class="fc-input-div-full-border width fc-black-14 text-left bold"
        >
          {{ $t('forms.booking.no_slot_available') }}
        </div>
        <div
          v-else-if="!$validation.isEmpty(facilitySlots)"
          disabled
          class="fc-input-div-full-border width fc-black-14 text-left bold"
        >
          {{ formattedBookingstartEndTime }}
        </div>
      </el-form-item>
    </div>
    <div class="fc-black-13 text-left mB10 mL10 fc-available-slot">
      {{ $t('forms.booking.time_slot') }}
    </div>
    <div class="d-flex flex-row mL10">
      <div class="f-slot-c1">
        <el-row :gutter="20">
          <spinner :show="loading" v-if="loading" :size="50"></spinner>
          <el-col v-else-if="$validation.isEmpty(facilitySlots)" :span="24">
            <div class="d-flex flex-direction-column">
              <div class="fc-black-14 text-left fw6">
                {{ $t('forms.booking.no_available_slot') }}
              </div>
            </div>
          </el-col>
          <el-col
            v-for="slot in facilitySlots"
            :key="'slot-' + slot.id"
            :span="4"
            class="slot-available-width"
            ><div
              class="time-slot-container"
              :class="[
                isSelectedSlot(slot) && 'active',
                !isSelectedSlot(slot) &&
                  isDisabledSlot(slot) &&
                  'disabled pointer-events-none',
              ]"
              @click="toggleSlotSelection(slot)"
            >
              {{
                `${$helpers.formatMillistoHHMM(
                  slot.slotStartTime
                )} - ${$helpers.formatMillistoHHMM(slot.slotEndTime)}`
              }}
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <div>
      <el-row v-if="formattedBookingstartEndTime && facility.chargeable">
        <el-col :span="24">
          <el-alert
            type="success"
            :closable="false"
            effect="dark"
            class="booking-slot-alret"
          >
            <div class="fc-black-14 text-left bold flex-middle">
              <i class="el-icon-warning-outline fc-warning-icon-booking"></i>
              {{ $t('forms.booking.external_Attendees_msg') }}

              <el-popover
                placement="top"
                :title="$t('forms.booking.slotHeading')"
                width="300"
                trigger="hover"
                popper-class="fc-booking-popup"
              >
                <!-- slot amount -->
                <div v-if="$validation.isEmpty(bookingslot)">
                  <div
                    class="d-flex justify-content-center flex-direction-column align-center fc-black-14 text-left bold pT10 pB10"
                  >
                    {{ $t('forms.booking.no_cost_breakdown') }}
                  </div>
                </div>
                <div v-else>
                  <div
                    v-for="selectedSlot in bookingslot"
                    :key="`selected-slot-` + selectedSlot.id"
                    class="time-slot-data flex-middle vertical-middle justify-content-space pB10"
                  >
                    <div class="time-slot-text">
                      <InlineSvg
                        class="mR5"
                        iconClass="fc-color icon-xs icon"
                        src="svgs/clock"
                      ></InlineSvg>
                      {{
                        `${$helpers.formatMillistoHHMM(
                          selectedSlot.slotStartTime
                        )} - ${$helpers.formatMillistoHHMM(
                          selectedSlot.slotEndTime
                        )}`
                      }}
                    </div>
                    <div class="time-slot-text">
                      <currency :value="selectedSlot.slotCost || 0"></currency>
                    </div>
                  </div>
                </div>
                <div
                  v-if="!$validation.isEmpty(bookingslot)"
                  class="time-slot-data2 flex-middle pT5 justify-content-space border-top3"
                >
                  <div class="time-slot-total-txt fc-black-14 text-left bold ">
                    {{ $t('common.header.total') }}
                  </div>
                  <div class="time-slot-total-txt bold">
                    <currency :value="getTotalCost"></currency>
                  </div>
                </div>
                <div slot="reference" class="underline fwBold pointer f16 pL5">
                  <currency :value="getTotalCost"></currency>
                </div>
              </el-popover>
            </div>
          </el-alert>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  props: ['model', 'field', 'facilityId', 'bookingslot'],
  components: {
    FDatePicker,
  },
  data() {
    return {
      facilitySlots: [],
      loading: false,
      facility: {},
    }
  },
  watch: {
    facilityId(newVal, oldVal) {
      if (newVal != oldVal) {
        this.fetchFacilitySlots(true)
      }
    },
  },
  created() {
    // this.fetchFacilitySlots(false)
  },
  computed: {
    getTotalCost() {
      let { bookingslot = [] } = this
      let totalCost = 0
      bookingslot.forEach(slot => {
        if (!isEmpty(slot.slotCost)) {
          totalCost += slot.slotCost
        }
      })
      return totalCost
    },
    selectedSlotIds() {
      let { bookingslot = [] } = this
      return bookingslot.map(slot => slot.id)
    },
    pickerOptions() {
      let { $helpers } = this
      let todayDate = $helpers
        .getOrgMoment()
        .startOf('date')
        .valueOf()
      return {
        disabledDate(time) {
          return $helpers.getDateInOrg(time.getTime()) < todayDate
        },
      }
    },
    formattedBookingstartEndTime() {
      if (this.bookingslot.length) {
        return `${this.$helpers.formatMillistoHHMM(
          this.slotStartTime
        )} - ${this.$helpers.formatMillistoHHMM(this.slotEndTime)}`
      }
      return ''
    },
    slotStartTime() {
      return Math.min.apply(
        Math,
        this.bookingslot.map(function(o) {
          return o.slotStartTime
        })
      )
    },
    slotEndTime() {
      return Math.max.apply(
        Math,
        this.bookingslot.map(function(o) {
          return o.slotEndTime
        })
      )
    },
  },
  methods: {
    async fetchFacilitySlots(reset) {
      if (reset) {
        this.facilitySlots = []
        this.facility = {}
        this.$emit('update:bookingslot', [])
      }
      if (
        !isEmpty(this.facilityId) &&
        !isEmpty(this.model.bookingDate) &&
        !isNaN(this.model.bookingDate)
      ) {
        let params = {
          id: this.facilityId,
          startTime: this.model.bookingDate,
          endTime: this.model.bookingDate + 86400000,
          // Adding One day
        }
        let currentTime = this.$helpers.getOrgMoment().valueOf()
        if (this.model.bookingDate <= currentTime) {
          params.startTime = currentTime
        }
        this.loading = true
        let { facility, error } = await API.fetchRecord('facility', params)
        if (error) {
          let {
            message = 'Error Occured while fetching Facility slots',
          } = error
          this.$message.error(message)
        } else {
          this.facilitySlots = (facility || {}).slots || []
          this.facility = facility || {}
        }
      }
      this.loading = false
    },
    isSelectedSlot(slot = {}) {
      let { selectedSlotIds = [] } = this
      if (selectedSlotIds.includes(slot.id)) {
        return true
      }
      return false
    },
    isDisabledSlot(slot = {}) {
      let { selectedSlotIds = [], facilitySlots = [], facility = {} } = this
      if (isEmpty(selectedSlotIds)) {
        return false
      } else if (selectedSlotIds.length < facility.maxSlotBookingAllowed) {
        let fSlot = facilitySlots.find(record => record.id === slot.id)
        let slotIndex = facilitySlots.indexOf(fSlot)
        let slotLeft = facilitySlots[slotIndex - 1]
        let slotRight = facilitySlots[slotIndex + 1]
        if (
          selectedSlotIds.includes(slotLeft?.id) ||
          selectedSlotIds.includes(slotRight?.id)
        ) {
          return false
        }
      }
      return true
    },
    toggleSlotSelection(slot = {}) {
      let { selectedSlotIds = [], bookingslot = [] } = this
      if (selectedSlotIds.includes(slot?.id)) {
        let bSlot = bookingslot.find(record => record.id === slot?.id)
        if (this.isLeafNode(bSlot)) {
          bookingslot.splice(bookingslot.indexOf(bSlot), 1)
        } else {
          this.$message.error('Cannot deselect this slot')
        }
      } else {
        bookingslot.push(slot)
      }
    },
    isLeafNode(slot = {}) {
      let { facilitySlots = [], selectedSlotIds = [] } = this
      let fSlot = facilitySlots.find(record => slot.id === record.id)
      let slotIndex = facilitySlots.indexOf(fSlot)
      if (slotIndex > -1) {
        if (
          selectedSlotIds.includes(facilitySlots[slotIndex + 1]?.id) &&
          selectedSlotIds.includes(facilitySlots[slotIndex - 1]?.id)
        ) {
          return false
        }
      }
      return true
    },
  },
}
</script>
