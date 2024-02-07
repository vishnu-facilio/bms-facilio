<template>
  <div class="fc-tenant-wo-page pB0 p20 pT10">
    <div class="flex-center-row-space pB10">
      <div class="fc-black-14 bold">
        {{ $t('tenant.occupant.visitor') }}
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
    <div
      v-else-if="$validation.isEmpty(visitorData)"
      class="flex-middle flex-center-hH justify-content-center height200 flex-col white-bg-block"
    >
      <el-col
        :span="24"
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
            src="svgs/visitor-empty"
            iconClass="icon-xxxxlg icon"
            class="fc-visitor-empty"
          ></inline-svg>
        </el-col>
      </el-col>
    </div>

    <div v-else>
      <el-row class="width100 flex-middle flex-no-wrap">
        <el-col
          :span="14"
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
              src="svgs/visitor-empty"
              iconClass="icon-xxxxlg icon"
              class="fc-visitor-empty"
            ></inline-svg>
          </el-col>
        </el-col>

        <el-col
          class="white-bg-block bR10 mL10 overflow-hidden pointer"
          :span="10"
          v-for="visitor in visitorData"
          :key="visitor.id"
          style="height: 160px;width: -webkit-fill-available;"
        >
          <div @click="openRecordSummary(visitor.id)">
            <div class="flex-middle justify-content-space pL20 pR20 pT20">
              <div
                class="fc-black-14 f16 text-left fwBold truncate-text max-width70"
              >
                {{ $getProperty(visitor, 'visitor.name') }}
              </div>
              <div>
                <el-tag class="fc-visitor-not-tag">
                  {{ $getProperty(visitor, 'moduleState.displayName') }}
                </el-tag>
              </div>
            </div>
            <div class="p20 pT10">
              <div class="fc-black-14 text-left flex-wrap-align-center">
                <i class="el-icon-mobile-phone pR10 fc-occupant-icon"></i>
                {{ $getProperty(visitor, 'visitor.phone') }}
              </div>
              <div class="fc-black-14 pT10 text-left flex-wrap-align-center">
                <i class="el-icon-message pR10 fc-occupant-icon"></i>
                {{ $getProperty(visitor, 'visitor.email') }}
              </div>
            </div>
            <div
              class="flex-center-row-space justify-content-center fc-visiotor-today-bg"
            >
              <div class="pR10">
                {{ getFormattedDay(visitor.expectedCheckInTime) }}
              </div>
              <div>
                {{ $helpers.formatTimeFull(visitor.expectedCheckInTime) }}
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { findRouteForModule, pageTypes } from '@facilio/router'
export default {
  props: ['getFormattedDay', 'visitorData', 'loading'],
  methods: {
    openRecordSummary(id) {
      let { name } =
        findRouteForModule('invitevisitor', pageTypes.OVERVIEW) || {}
      if (name) {
        this.$router.push({
          name,
          params: { id, viewname: 'all' },
        })
      }
    },
    goToVisitCreate() {
      let route = findRouteForModule('invitevisitor', pageTypes.CREATE)
      if (route) {
        this.$router.push({
          name: route.name,
        })
      } else {
        console.warn('Could not resolve route')
      }
    },
    goToVisitList() {
      let route = findRouteForModule('invitevisitor', pageTypes.LIST)
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
  },
}
</script>
<style lang="scss">
.fc-visitor-empty {
  margin-top: 50px;
  position: relative;
  right: 20px;
  svg {
    height: 250px !important;
    width: 250px !important;
  }
}
</style>
