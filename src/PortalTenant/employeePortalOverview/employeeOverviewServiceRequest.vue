<template>
  <div class="fc-tenant-wo-page pB0 p20">
    <div class="flex-center-row-space pB10">
      <div class="fc-black-14 bold">
        {{ $t('tenant.occupant.service_request') }}
      </div>
      <div
        class="fc-tenant-view-more-txt pointer bold"
        @click="goToServiceList"
      >
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
      v-else-if="$validation.isEmpty(serviceData)"
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
              @click="goToServiceCreate()"
            >
            </el-button>
          </div>
        </el-col>
        <el-col :span="8" class="text-right">
          <inline-svg
            src="svgs/work-request"
            iconClass="icon-xxxxlg icon"
            class="heightwoEmpty"
          ></inline-svg>
        </el-col>
      </el-col>
    </div>

    <div v-else>
      <div class="mB10">
        <el-row class="width100 flex-middle flex-no-wrap">
          <el-col
            :span="16"
            style="height: 160px;width: -webkit-fill-available;"
            class="white-bg-block bR10 p20 overflow-hidden flex-middle pL30 pR30"
          >
            <el-col :span="12" class="width100">
              <div class="fc-black-18 bold"></div>
              <div class="mT10">
                <el-button
                  class="fc-occupant-add-btn"
                  round
                  @click="goToServiceCreate()"
                >
                </el-button>
              </div>
            </el-col>
            <el-col :span="12" class="text-right width100">
              <inline-svg
                src="svgs/work-request"
                iconClass="icon-xxxxlg icon"
                class="heightwoEmpty"
              ></inline-svg>
            </el-col>
          </el-col>
          <el-col
            class="white-bg-block bR10 p20 mL10 pointer"
            :span="8"
            v-for="service in serviceData"
            :key="service.id"
            style="height: 160px;width: -webkit-fill-available;"
          >
            <div @click="openRecordSummary(service.id)">
              <div class="flex-middle justify-content-space">
                <div class="fc-id-tag">#{{ service.id }}</div>
                <div>
                  <el-tag class="fc-wo-tag">
                    {{ $getProperty(service, 'moduleState.displayName') }}
                  </el-tag>
                </div>
              </div>
              <div class="pL10 pT10">
                <div class="fc-mobile-black14 fwBold text-left">
                  {{ service.subject }}
                </div>
                <div class="fc-mobile-grey-txt12 pT10">
                  {{ service.currentTime | formatDate() }}
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import { findRouteForModule, pageTypes } from '@facilio/router'
export default {
  props: ['serviceData', 'loading'],
  methods: {
    goToServiceList() {
      let route = findRouteForModule('serviceRequest', pageTypes.LIST)
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
    goToServiceCreate() {
      let route = findRouteForModule('serviceRequest', pageTypes.CREATE)
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
        findRouteForModule('serviceRequest', pageTypes.OVERVIEW) || {}
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
<style lang="scss">
.heightwoEmpty {
  svg {
    z-index: 100;
    height: 280px !important;
    width: 280px !important;
    position: relative;
    top: 90px;
  }
}
</style>
