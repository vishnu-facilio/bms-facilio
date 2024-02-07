<template>
  <div class="fc-tenant-wo-page pB0 p20">
    <div class="flex-center-row-space pB10">
      <div class="fc-black-14 bold">
        {{ $t('tenant.occupant.booking') }}
      </div>
      <div class="fc-tenant-view-more-txt pointer bold" @click="goToVisitList">
        {{ $t('tenant.tenants.view_more') }}
      </div>
    </div>
    <div
      v-if="loading"
      class="flex-middle flex-center-hH justify-content-center height200 flex-col white-bg-block"
    >
      <spinner :show="loading" size="80"></spinner>
    </div>
    <template v-else-if="$validation.isEmpty(bookingData)">
      <div
        class="flex-middle flex-center-hH justify-content-center height200 flex-col white-bg-block"
      >
        <el-col
          :span="16"
          style="height: 160px;"
          class="white-bg-block bR10 p20 overflow-hidden flex-middle pL30 pR30"
        >
          <el-col :span="16">
            <div class="fc-black-18 bold"></div>
            <div class="mT10">
              <el-button
                class="fc-occupant-add-btn"
                round
                @click="goToVisitCreate()"
              >
              </el-button>
            </div>
          </el-col>
          <el-col :span="8" class="text-right">
            <inline-svg
              src="svgs/desk-empty"
              iconClass="icon-xxxxlg icon"
              class="mT20"
            ></inline-svg>
          </el-col>
        </el-col>
      </div>
    </template>

    <template v-else>
      <el-row class="width100 flex-middle flex-no-wrap">
        <el-col
          :span="16"
          style="height: 160px;width: -webkit-fill-available;"
          class="white-bg-block bR10 p20 overflow-hidden flex-middle pL30 pR30 width100"
        >
          <el-col :span="14">
            <div class="fc-black-18 bold"></div>
            <div class="mT10">
              <el-button
                class="fc-occupant-add-btn"
                round
                @click="goToVisitCreate()"
              >
              </el-button>
            </div>
          </el-col>
          <el-col :span="8" class="text-right">
            <inline-svg
              src="svgs/desk-empty"
              iconClass="icon-xxxxlg icon"
              class="mT60"
            ></inline-svg>
          </el-col>
        </el-col>

        <el-col
          class="white-bg-block bR10 mL10 overflow-hidden pointer"
          v-for="booking in bookingData"
          :key="booking.id"
          :span="10"
          style="width: -webkit-fill-available;"
        >
          <div @click="openRecordSummary(booking.id)">
            <div class="p20">
              <div class="fc-black-14 f16 bold">
                {{ $getProperty(booking, 'facility.name') }}
              </div>
              <div class="fc-no-attened-txt pT5">
                {{ booking.noOfAttendees }}
              </div>
              <div class="fc-guest-txt text-center pT5"></div>
            </div>
            <div
              class="flex-center-row-space fc-desk-today-bg justify-content-center"
            >
              <div class="pR10">
                {{ getFormattedDay(booking.currentTime) }}
              </div>
              <div>
                {{ $helpers.formatTimeFull(booking.currentTime) }}
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </template>
  </div>
</template>
<script>
import { findRouteForModule, pageTypes } from '@facilio/router'
export default {
  props: ['bookingData', 'loading', 'getFormattedDay'],
  methods: {
    goToVisitList() {
      let route = findRouteForModule('facilitybooking', pageTypes.LIST)
      if (route) {
        this.$router.push({
          name: route.name,
          params: {
            viewname: 'all',
          },
        })
      } else {
        console.warn('Could not resolve route')
      }
    },
    goToVisitCreate() {
      let route = findRouteForModule('facilitybooking', pageTypes.CREATE)
      if (route) {
        this.$router.push({
          name: route.name,
        })
      } else {
        console.warn('Could not resolve route')
      }
    },
    openRecordSummary(id) {
      let { name } =
        findRouteForModule('facilitybooking', pageTypes.OVERVIEW) || {}
      if (name) {
        this.$router.push({
          name,
          params: { id, viewname: 'all' },
        })
      }
    },
  },
}
</script>
