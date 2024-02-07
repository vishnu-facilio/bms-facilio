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

    <template v-else-if="!loading && $validation.isEmpty(serviceRequestData)">
      <div class="pT20 pB20 flex-middle justify-content-space pT10">
        <div class="fc-black3-16">
          Service Requests
        </div>
        <div class="fc-tenant-view-more-txt f13 pointer bold" @click="goToList">
          {{ $t('tenant.tenants.view_more') }}
        </div>
      </div>
      <el-card class="fc-mobile-tenant-card width100" style="height: 120px;">
        <el-row class="mB10 flex-middle align-inherit">
          <el-col :span="14">
            <div class="fc-black-14 text-left bold">
              Have a pesky leak or a faulty bulb etc. etc.?
            </div>
            <div class="mT10">
              <el-button
                class="fc-occupant-add-btn fc-occupant-mobile-btn pL0"
                round
                @click="goToCreate()"
              >
                Raise work request
              </el-button>
            </div>
          </el-col>
          <el-col :span="10">
            <inline-svg
              src="svgs/work-request"
              iconClass="icon-xxxxlg icon"
              class="heightwoEmptyMobile"
            ></inline-svg>
          </el-col>
        </el-row>
      </el-card>
    </template>
    <template v-else>
      <div class="">
        <div class="flex-middle justify-content-space pT20 pB10">
          <div class="fc-occupant-heading">
            {{ $t('tenant.occupant.service_request') }}
          </div>
          <div
            class="fc-tenant-view-more-txt f13 pointer bold"
            @click="goToList"
          >
            {{ $t('tenant.tenants.view_more') }}
          </div>
        </div>
        <div class="fc-mobile-tenant-card ">
          <el-card
            :body-style="{ padding: '15px 20px 15px 20px' }"
            class="fc-mobile-tenant-card mB10"
            style="height: 120px;"
          >
            <el-row class="mB10 flex-middle align-inherit">
              <el-col :span="14" class="mT10">
                <div class="fc-black-14 text-left bold">
                  Have a pesky leak or a faulty bulb etc. etc.?
                </div>
                <div class="mT10">
                  <el-button
                    class="fc-occupant-add-btn fc-occupant-mobile-btn pL0"
                    round
                    @click="goToCreate()"
                  >
                    Raise work request
                  </el-button>
                </div>
              </el-col>
              <el-col :span="10">
                <inline-svg
                  src="svgs/work-request"
                  iconClass="icon-xxxxlg icon"
                  class="heightwoEmptyMobile"
                ></inline-svg>
              </el-col>
            </el-row>
          </el-card>
        </div>

        <el-card
          v-for="service in serviceRequestData"
          :key="service.id"
          :body-style="{ padding: '15px 20px 15px 20px' }"
          class="fc-mobile-tenant-card mB10"
        >
          <el-row class="flex-middle" @click.native="goToSummary(service)">
            <el-col :span="24">
              <div class="flex-middle justify-content-space">
                <div class="fc-id-tag">#{{ service.id }}</div>
                <el-tag class="fc-wo-tag">
                  {{
                    service.moduleState
                      ? service.moduleState && service.moduleState.displayName
                      : ''
                  }}
                </el-tag>
              </div>
              <div class="fc-mobile-black14 fwBold text-left pT5">
                {{ service.subject }}
              </div>
              <div
                class="fc-mobile-grey-txt12 pT5"
                v-if="
                  !$validation.isEmpty($getProperty(service, 'currentTime'))
                "
              >
                {{ service.currentTime | formatDate() }}
              </div>
            </el-col>
          </el-row>
        </el-card>
      </div>
    </template>
  </div>
</template>
<script>
import { pageTypes } from '@facilio/router'
import { emitEvent } from 'src/webViews/utils/mobileapps'
export default {
  props: ['serviceRequestData', 'loading'],
  methods: {
    goToList() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'serviceRequest',
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
    goToSummary(service) {
      let routeData = {
        pageType: pageTypes.OVERVIEW,
        moduleName: 'serviceRequest',
        recordId: service.id,
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
    goToCreate() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'service-catalog',
      }
      emitEvent('navigate', routeData)
    },
  },
}
</script>
<style lang="scss">
.heightwoEmptyMobile {
  position: relative;
  top: 10px;
  svg {
    height: 180px;
    width: 180px;
  }
}
</style>
