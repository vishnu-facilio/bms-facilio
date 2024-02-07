<template>
  <div class="pB50">
    <div v-if="loading">
      <el-card
        :body-style="{ padding: '40px' }"
        class="fc-mobile-tenant-card mT20"
      >
        <spinner :show="loading" size="80"></spinner>
      </el-card>
    </div>

    <template v-else-if="!loading && $validation.isEmpty(bookingData)">
      <div class="pT20 pB10 flex-middle justify-content-space">
        <div class="fc-black3-16">
          {{ $t('tenant.occupant.booking') }}
        </div>
      </div>

      <el-card
        class="fc-mobile-tenant-card fc-visitor-card width100"
        style="height: 120px;"
      >
        <el-row>
          <el-col :span="14" class="mT20">
            <div class="fc-black-14 text-left bold"></div>
            <el-button
              class="fc-occupant-add-btn fc-occupant-mobile-btn"
              round
              @click="goToCreate()"
            >
            </el-button>
          </el-col>
          <el-col :span="10">
            <inline-svg
              src="svgs/desk-empty"
              iconClass="icon-80 icon"
              class="booking-empty-mobile-state"
            ></inline-svg>
          </el-col>
        </el-row>
      </el-card>
    </template>
    <template v-else>
      <div class="flex-middle justify-content-space pT10 pB10">
        <div class="fc-occupant-heading">
          {{ $t('tenant.occupant.booking') }}
        </div>
        <div class="fc-tenant-view-more-txt f13 pointer bold" @click="goToList">
          {{ $t('tenant.tenants.view_more') }}
        </div>
      </div>
      <div class="overflow-x width100">
        <div class="flex-middle flex-no-wrap width-max-content">
          <el-card class="fc-mobile-tenant-card fc-visitor-card mR10">
            <div class="fc-black-14 bold"></div>
            <div class="mT10 text-center">
              <el-button
                class="fc-occupant-add-btn fc-occupant-mobile-btn"
                round
                @click="goToCreate()"
              >
              </el-button>
            </div>
            <inline-svg
              src="svgs/desk-empty"
              iconClass="icon-80 icon"
              class="mT20 visitor-empty-mobile"
            ></inline-svg>
          </el-card>

          <el-card
            :body-style="{ padding: '20px 0 0 0' }"
            class="fc-mobile-tenant-card fc-visitor-card mR10"
            v-for="booking in bookingData"
            :key="booking.id"
          >
            <div @click.native="goToSummary(booking)">
              <div class="text-center fc-booking-center">
                <div class="fc-mobile-black14 fwBold">
                  {{ $getProperty(booking, 'facility.name') }}
                </div>
                <div class="fc-count-txt pT5">
                  {{ booking.noOfAttendees }}
                </div>
                <div class="fc-mobile-grey-txt12"></div>
              </div>
              <el-row class="mT15 fc-booking-bg">
                <el-col :span="8" class="pT10 pB10 pL20">
                  <div class="fc-white-11 nowrap bold">
                    {{ getFormattedDay(booking.currentTime) }}
                  </div>
                </el-col>
                <el-col :span="14" class="pT10 pB10 text-right">
                  <div class="fc-white-11 nowrap bold">
                    {{ $helpers.formatTimeFull(booking.currentTime) }}
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-card>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { pageTypes } from '@facilio/router'
import { emitEvent } from 'src/webViews/utils/mobileapps'
export default {
  props: ['bookingData', 'loading', 'getFormattedDay'],
  methods: {
    goToList() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'facilitybooking',
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
    goToSummary(booking) {
      let routeData = {
        pageType: pageTypes.OVERVIEW,
        moduleName: 'facilitybooking',
        recordId: booking.id,
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
    goToCreate() {
      let routeData = {
        pageType: pageTypes.CREATE,
        moduleName: 'facilitybooking',
      }
      emitEvent('navigate', routeData)
    },
  },
}
</script>
