<template>
  <div>
    <div v-if="isLoading" class="mT100">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <div v-else class="height100 flex-wrap">
      <el-row>
        <div class="flex-middle justify-content-space p20">
          <div class="f13 bold text-uppercase fc-black-13 text-left">
            {{ $t('common._common.facility_bookings') }}
          </div>
          <div class="f11 bold text-fc-grey">
            {{ $t('common.date_picker.this_month') }}
          </div>
        </div>
      </el-row>
      <el-row>
        <el-col :span="7">
          <el-row>
            <div class="justify-content-center">
              <div class="f60 text-center">
                {{ totalTillYesterdayBooking }}
              </div>
              <div class="f14 bold fc-black-13 text-center mT5">
                {{ $t('common._common.till_yesterday') }}
              </div>
            </div>
          </el-row>
          <el-row class="mT30">
            <div>
              <div class="f60 text-center">
                {{ totalTodayBooking }}
              </div>
              <div class="f14 bold fc-black-13 text-center mT5">
                {{ $t('common._common.today') }}
              </div>
            </div>
          </el-row>
        </el-col>
        <el-col :span="1">
          <div class="line-vr"></div>
        </el-col>
        <el-col :span="16">
          <el-row class="mT30 tenant-bk-url" style="height:110px">
            <div @click="redirectToOverview('facility', mostBookedFacilityId)">
              <div
                class="flex-middle f14 bold text-uppercase fc-black-13 text-center"
              >
                <img
                  src="~assets/svgs/tenant/success_red.svg"
                  class="mR10"
                  style="width:20px;height:20px"
                />
                {{ $t('common._common.most_booked_facility') }}
              </div>
              <div class="mT10 textoverflow-ellipsis pR20 mL30 f14">
                <span>{{ mostBookedFacility }}</span>
              </div>
              <div class="textoverflow-ellipsis pR20 mL30 f11 text-fc-grey mT5">
                <template v-if="totalHoursBooked">
                  {{ totalHoursBooked }}
                </template>
              </div>
            </div>
          </el-row>
          <el-row class="tenant-bk-url">
            <div
              @click="redirectToOverview('facilitybooking', upcomingBookingId)"
            >
              <div
                class="flex-middle f14 bold text-uppercase fc-black-13 text-center"
              >
                <img
                  src="~assets/svgs/tenant/clock_previous_green.svg"
                  class="mR10"
                  style="width:20px;height:20px"
                />
                {{ $t('common._common.upcoming_booking') }}
              </div>
              <div
                class="textoverflow-ellipsis pR20 mL30 pT10 fc-black-14 text-left"
              >
                {{ upcomingBookingName }}
              </div>
              <div
                class="textoverflow-ellipsis pR20 mL30 f11"
                v-if="upcomingBookingDate"
              >
                <span class="text-fc-grey">{{ upcomingBookingDate }}</span>
                <span class="separator pL10">|</span>
                <span class="text-fc-grey">{{ upcomingBookingSlot }}</span>
              </div>
            </div>
          </el-row>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import moment from 'moment-timezone'
export default {
  props: ['details'],
  data() {
    return {
      totalTillYesterdayBooking: '---',
      totalTodayBooking: '---',
      mostBookedFacility: '---',
      mostBookedFacilityId: null,
      totalHoursBooked: null,
      lastBooking: '---',
      upcomingBooking: '---',
      upcomingBookingName: '---',
      upcomingBookingDate: null,
      upcomingBookingSlot: null,
      lastBookingId: null,
      upcomingBookingId: null,
      isLoading: false,
    }
  },
  components: { Spinner },
  async created() {
    this.isLoading = true

    await Promise.all([this.getBookingsTillTodayCount()])
    this.isLoading = false
  },
  computed: {
    getCurrentTime() {
      return this.$helpers.getOrgMoment().valueOf()
    },
  },
  methods: {
    getStartTime(val) {
      return this.$helpers
        .getOrgMoment(this.getCurrentTime)
        .startOf(val)
        .valueOf()
    },
    getEndTime(val) {
      return this.$helpers
        .getOrgMoment(this.getCurrentTime)
        .endOf(val)
        .valueOf()
    },
    async getBookingsTillTodayCount() {
      let queryParam = {}
      queryParam = {
        withCount: true,
        viewName: 'all',
        includeParentFilter: true,
        moduleName: 'facilitybooking',
      }
      queryParam.filters = JSON.stringify({
        tenant: {
          operatorId: 36,
          value: [this.details.id.toString()],
        },
        bookingDate: {
          operatorId: 20,
          value: [
            this.getStartTime('month').toString(),
            this.getEndTime('month').toString(),
          ],
        },
      })
      let { data, meta, error } = await API.get(
        '/v3/modules/data/list',
        queryParam
      )
      if (!error) {
        let { facilitybooking } = data || {}
        this.getBookingsInsightCount(facilitybooking || [], meta)
      }
    },
    async getBookingsInsightCount(bookings, meta) {
      let startOfDay = this.getStartTime('day')
      let endOfDay = this.getEndTime('day')
      let bookingIds = []
      let totalTillYesterdayBooking = 0
      let totalTodayBooking = 0
      let mostBookedFacilityMap = new Map()
      let mostBookedFacilityIdCountVsCount = null
      let tenantFacilityVsBookingMap = {}
      let upcomingBookingIds = []
      bookings.forEach(booking => {
        let bookingId = booking.id
        bookingIds.push(bookingId.toString())
        if (booking.bookingDate < startOfDay) {
          totalTillYesterdayBooking = totalTillYesterdayBooking + 1
        } else if (
          booking.bookingDate >= startOfDay &&
          booking.bookingDate < endOfDay
        ) {
          totalTodayBooking = totalTodayBooking + 1
        }
        if (booking && booking.facility) {
          let facilityId = booking.facility.id
          if (mostBookedFacilityMap.get(facilityId) >= 0) {
            mostBookedFacilityMap.set(
              facilityId,
              mostBookedFacilityMap.get(facilityId) + 1
            )
          } else {
            mostBookedFacilityMap.set(facilityId, 1)
          }

          if (booking.bookingDate >= startOfDay) {
            upcomingBookingIds.push(bookingId.toString())
          }
          if (
            tenantFacilityVsBookingMap[facilityId] &&
            tenantFacilityVsBookingMap[facilityId].length > 0
          ) {
            tenantFacilityVsBookingMap[facilityId].push(bookingId.toString())
          } else {
            let bkIds = [bookingId.toString()]
            tenantFacilityVsBookingMap[facilityId] = bkIds
          }
        }
      })

      mostBookedFacilityIdCountVsCount = this.findMostBookedFacility(
        mostBookedFacilityMap
      )
      this.totalTillYesterdayBooking = totalTillYesterdayBooking
      this.totalTodayBooking = totalTodayBooking

      if (mostBookedFacilityIdCountVsCount) {
        this.mostBookedFacilityId = Object.keys(
          mostBookedFacilityIdCountVsCount
        )[0]
        this.mostBookedFacility = await this.getFacilityDetails(
          this.mostBookedFacilityId,
          meta
        )
      }

      let pastBookingSlots = await this.getBookingSlots(
        tenantFacilityVsBookingMap[this.mostBookedFacilityId] || []
      )
      let slotIds = []

      pastBookingSlots.forEach(bkslot => {
        let { slot } = bkslot || {}
        if (slot.id) {
          slotIds.push(slot.id.toString())
        }
      })
      let slots = await this.getSlots(slotIds)

      let totalTime = 0
      slots.forEach(s => {
        totalTime = totalTime + (s.slotEndTime - s.slotStartTime)
      })
      if (totalTime) {
        let momentTime = moment.duration(totalTime)
        this.totalHoursBooked =
          momentTime
            .hours()
            .toString()
            .padStart(2, '0') +
          ' Hrs ' +
          momentTime
            .minutes()
            .toString()
            .padStart(2, '0') +
          ' Mins'
      }
      let upcomingBookingSlots = await this.getBookingSlots(
        upcomingBookingIds || []
      )

      let upcomingSlotIds = []

      upcomingBookingSlots.forEach(bkslot => {
        let { slot } = bkslot || {}
        if (slot.id) {
          upcomingSlotIds.push(slot.id.toString())
        }
      })
      let upcomingSlots = await this.getSlots(upcomingSlotIds)

      let nextRecentStartTime = Number.MAX_SAFE_INTEGER
      let nextComingSlotId = -1
      upcomingSlots.forEach(slot => {
        if (nextRecentStartTime > slot.slotStartTime) {
          nextRecentStartTime = slot.slotStartTime
          nextComingSlotId = slot.id
        }
      })

      let upcomingBookingId = null
      upcomingBookingSlots.forEach(bookingslot => {
        if (
          bookingslot &&
          bookingslot.slot &&
          bookingslot.slot.id == nextComingSlotId
        ) {
          if (upcomingBookingId === null) {
            upcomingBookingId = bookingslot.booking
              ? bookingslot.booking.id
              : null
          }
        }
      })
      this.upcomingBookingId = upcomingBookingId
      let upcomingBooking = await this.getBookingDetails(upcomingBookingId)
      let upcomingFacilityBooking =
        upcomingBooking && upcomingBooking.facility
          ? upcomingBooking.facility
          : {}
      this.upcomingBookingName = upcomingFacilityBooking.name || '---'
      this.upcomingBookingDate = upcomingBooking
        ? this.$options.filters.formatDate(upcomingBooking.bookingDate, true)
        : null
      if (
        upcomingBooking &&
        upcomingBooking.slotList &&
        upcomingBooking.slotList.length
      ) {
        let bkSlotDetail = upcomingBooking.slotList[0]
        let upcomingSlot = bkSlotDetail.slot || {}
        if (upcomingSlot.slotStartTime && upcomingSlot.slotEndTime) {
          let startTime = moment(upcomingSlot.slotStartTime)
          let endTime = moment(upcomingSlot.slotEndTime)
          this.upcomingBookingSlot =
            startTime.format('HH:mm') + ' - ' + endTime.format('HH:mm')
        }
      }
    },
    findMostBookedFacility(facilityMap) {
      let mostBookedFacility = -1
      let maxBookingsCount = -1
      facilityMap.forEach(function(value, key) {
        if (value && value > maxBookingsCount) {
          mostBookedFacility = key
        }
      })
      let mostBookedFacilityMap = {}
      mostBookedFacilityMap[mostBookedFacility] = facilityMap.get(
        mostBookedFacility
      )
      return mostBookedFacilityMap
    },
    async getFacilityDetails(facilityId, meta) {
      let { supplements } = meta || {}
      let { facilitybooking } = supplements || {}
      let { facility } = facilitybooking || {}
      let { name: facilityName } = (facility || {})[facilityId] || {}
      return facilityName || '---'
    },
    async getBookingSlots(bookingIds) {
      if (bookingIds && bookingIds.length > 0) {
        let queryParam = {}
        queryParam = {
          viewName: 'all',
          includeParentFilter: true,
          moduleName: 'bookingslot',
        }
        queryParam.filters = JSON.stringify({
          booking: {
            operatorId: 36,
            value: bookingIds,
          },
        })
        let { data, error } = await API.get('/v3/modules/data/list', queryParam)
        if (!error) {
          let { bookingslot } = data || {}

          return bookingslot
        }
      }
      return []
    },
    async getSlots(slotIds) {
      if (slotIds && slotIds.length > 0) {
        let queryParam = {}
        queryParam = {
          viewName: 'all',
          includeParentFilter: true,
          moduleName: 'slot',
        }
        queryParam.filters = JSON.stringify({
          id: {
            operatorId: 9,
            value: slotIds,
          },
        })
        let { data, error } = await API.get('/v3/modules/data/list', queryParam)
        if (!error) {
          let { slot } = data || {}
          return slot
        }
      }
      return []
    },
    async getBookingDetails(bookingId) {
      if (bookingId && bookingId > 1) {
        let queryParam = {}
        queryParam = {
          id: bookingId,
          moduleName: 'facilitybooking',
        }
        let { data, error } = await API.get(
          '/v3/modules/data/summary',
          queryParam
        )
        if (!error) {
          let { facilitybooking } = data || {}
          return facilitybooking
        }
      }
    },
    redirectToOverview(moduleName, id) {
      if (!this.$validation.isEmpty(id)) {
        let route
        let params = { id: id, viewname: 'all' }
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

          if (name) {
            route = this.$router.resolve({
              name,
              params,
            }).href
          }
        } else {
          route = this.$router.resolve({
            name:
              moduleName === 'facility' ? 'facilitySummary' : 'bookingSummary',
            params,
          }).href
        }
        route && window.open(route, '_blank')
      }
    },
  },
}
</script>
<style scoped>
.line-hr {
  width: 45%;
  height: 1px;
  background: #8ca1ad;
  opacity: 0.3;
}
.mL55 {
  margin-left: 55px;
}

.tenant-bk-url:hover {
  cursor: pointer;
}

.line-vr {
  margin-top: 20px;
  width: 1px;
  height: 190px;
  background: #8ca1ad;
  opacity: 0.3;
}
.mT100 {
  margin-top: 100px;
}
.f60 {
  font-size: 60px;
}
.mT65 {
  margin-top: 65px;
}
.mT45 {
  margin-top: 45px;
}
</style>
