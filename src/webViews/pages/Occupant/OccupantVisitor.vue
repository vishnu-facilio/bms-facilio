<template>
  <div>
    <div v-if="loading">
      <el-card
        :body-style="{ padding: '40px' }"
        class="fc-mobile-tenant-card mT20"
      >
        <spinner :show="loading" size="80"></spinner>
      </el-card>
    </div>

    <template v-else-if="!loading && $validation.isEmpty(visitorData)">
      <div class="pT10 pB10 flex-middle justify-content-space">
        <div class="fc-occupant-heading">
          {{ $t('tenant.occupant.visitor') }}
        </div>
      </div>
      <el-card
        class="fc-mobile-tenant-card fc-visitor-card width100"
        style="height: 120px;"
      >
        <el-row>
          <el-col :span="14" class="mT20">
            <div class="fc-black-14 text-left bold">
              Expecting a visit?
            </div>
            <el-button
              class="fc-occupant-add-btn fc-occupant-mobile-btn"
              round
              @click="goToCreate()"
            >
              Send Invites
            </el-button>
          </el-col>
          <el-col :span="10">
            <inline-svg
              src="svgs/visitor-empty"
              iconClass="icon-80 icon"
              class="visitor-empty-mobile-state"
            ></inline-svg>
          </el-col>
        </el-row>
      </el-card>
    </template>

    <template v-else>
      <div>
        <div class="flex-middle justify-content-space pT10 pB10">
          <div class="fc-occupant-heading">
            {{ $t('tenant.occupant.visitor') }}
          </div>
          <div
            class="fc-tenant-view-more-txt f13 pointer bold"
            @click="goToList"
          >
            {{ $t('tenant.tenants.view_more') }}
          </div>
        </div>
        <div class="overflow-x width100">
          <div class="flex-middle flex-no-wrap width-max-content">
            <el-card class="fc-mobile-tenant-card fc-visitor-card mR10">
              <div class="fc-black-14 bold">
                Expecting a visit?
              </div>
              <div class="mT10 text-center">
                <el-button
                  class="fc-occupant-add-btn fc-occupant-mobile-btn"
                  round
                  @click="goToCreate()"
                >
                  Send Invites
                </el-button>
              </div>
              <inline-svg
                src="svgs/visitor-empty"
                iconClass="icon-80 icon"
                class="mT40 visitor-empty-mobile"
              ></inline-svg>
            </el-card>
            <el-card
              :body-style="{ padding: '0' }"
              class="fc-mobile-tenant-card fc-visitor-card mR10 overflow-hidden"
              v-for="visitor in visitorData"
              :key="visitor.id"
            >
              <div @click.native="goToSummary(visitor)">
                <div class="text-left">
                  <el-tag class="fc-visitor-not-tag mT10 mL15">
                    {{ $getProperty(visitor, 'moduleState.displayName') }}
                  </el-tag>
                </div>
                <div class="text-left pL20 pR20 mT10">
                  <div class="fc-mobile-black14 fwBold">
                    {{ $getProperty(visitor, 'visitor.name') }}
                  </div>
                  <div class="fc-mobile-black12 pT10">
                    <i class="el-icon-mobile-phone pR5 fc-occupant-icon"></i>
                    {{ $getProperty(visitor, 'visitor.phone') }}
                  </div>
                  <div class="fc-mobile-grey-txt12 flex-middle break-word pT10">
                    <i class="el-icon-message pR10 fc-occupant-icon"></i>
                    {{ $getProperty(visitor, 'visitor.email') }}
                  </div>
                </div>
                <el-row class="mT15 fc-visitor-bg width100">
                  <div class="flex-middle pT10 pB10 justify-center">
                    <div class="fc-white-11 bold nowrap pL10">
                      {{ getFormattedDay(visitor.expectedCheckInTime) }}
                    </div>
                    <div class="fc-white-11 bold text-center nowrap">
                      {{ $helpers.formatTimeFull(visitor.expectedCheckInTime) }}
                    </div>
                  </div>
                </el-row>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>
<script>
import { pageTypes } from '@facilio/router'
import { emitEvent } from 'src/webViews/utils/mobileapps'
export default {
  props: ['getFormattedDay', 'visitorData', 'loading'],
  methods: {
    goToList() {
      let routeData = {
        pageType: pageTypes.LIST,
        moduleName: 'visitor',
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
    goToSummary(visitor) {
      let routeData = {
        pageType: pageTypes.OVERVIEW,
        moduleName: 'visitor',
        recordId: visitor.id,
        viewName: this.viewName,
      }
      emitEvent('navigate', routeData)
    },
    goToCreate() {
      let routeData = {
        pageType: pageTypes.CREATE,
        moduleName: 'visitor',
      }
      emitEvent('navigate', routeData)
    },
  },
}
</script>
