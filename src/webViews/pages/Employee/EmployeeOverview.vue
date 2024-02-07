<template>
  <div class="fc-mobile-tenant-home-con fc-occupant-homepage">
    <div class="width100">
      <div class="text-center">
        <img
          v-if="$getProperty($account, 'org.logoUrl')"
          :src="getLogoUrl($account.org)"
          height="50"
          width="100%"
          class="object-scale-down"
        />
      </div>
      <div
        class="fc-occupant-profile mT20"
        v-if="!$validation.isEmpty(employee)"
      >
        <el-row class="flex-middle">
          <el-col :span="5">
            <avatar size="xxlg" :user="$portaluser"></avatar>
          </el-col>
          <el-col :span="19" class="pL10">
            <div class="fc-black-16 fwBold pT5" v-if="employee.name">
              {{ employee.name }}
            </div>
            <div class="fc-black-14 text-left fw4 pT5" v-if="employee.email">
              {{ employee.email }}
            </div>
            <div class="fc-black-14 text-left fw4 pT5" v-if="employee.phone">
              {{ employee.phone }}
            </div>
            <div
              class="fc-black-14 text-left fw4 pT5"
              v-if="$getProperty(employee.data, 'department.name')"
            >
              {{ $getProperty(employee.data, 'department.name') }}
            </div>
          </el-col>
        </el-row>
      </div>

      <template v-if="$validation.isEmpty(desks)">
        <el-card
          :body-style="{ padding: '20px 20px 20px 20px' }"
          class="fc-mobile-tenant-card mT10"
        >
          <div class="flex-middle">
            <div class="fc-empty-desk-round">
              <inline-svg src="svgs/desk-outline" iconClass="icon icon-lg">
              </inline-svg>
            </div>
            <div class="pL15 bold">
              {{ $t('tenant.occupant.desk_empty') }}
            </div>
          </div>
        </el-card>
      </template>

      <template v-if="!$validation.isEmpty(desks)">
        <div class="mT20">
          <div class="fc-occupant-heading">
            {{ $t('tenant.occupant.desk') }}
          </div>
          <el-card
            :body-style="{ padding: '20px 20px 20px 20px' }"
            class="fc-mobile-tenant-card mT10"
          >
            <div class="flex-middle">
              <div>
                <inline-svg src="svgs/desk-outline" iconClass="icon icon-lg">
                </inline-svg>
              </div>
              <div class="pL15">
                <div
                  class="fc-mobile-black14 fwBold"
                  @click="goToFloorPlan(desks)"
                >
                  {{ desks.name }}
                </div>
              </div>
            </div>

            <div class="flex-middle pT10">
              <div>
                <i
                  class="el-icon-office-building pR10 f20 fc-occupant-icon"
                ></i>
              </div>
              <div class="pL5">
                <div
                  class="fc-mobile-black14 pT5"
                  v-if="$getProperty(desks, 'building.name')"
                >
                  {{ $getProperty(desks, 'building.name') }}
                </div>
                <div
                  class="fc-mobile-black14 pT5"
                  v-if="$getProperty(desks, 'floor.name')"
                >
                  {{ $getProperty(desks, 'floor.name') }}
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </template>

      <service
        :serviceRequestData="serviceRequestData"
        :loading="loading"
        class="pB0"
      >
      </service>
      <!-- <visitor
        :visitorData="visitorData"
        :loading="loading"
        :getFormattedDay="getFormattedDay"
      ></visitor> -->
      <booking
        :bookingData="bookingData"
        :loading="loading"
        :getFormattedDay="getFormattedDay"
      ></booking>
    </div>
  </div>
</template>
<script>
import service from 'src/webViews/pages/Occupant/OccupantWoRequest'
// import visitor from 'src/webViews/pages/Occupant/OccupantVisitor'
import booking from 'src/webViews/pages/Occupant/OccupantBooking'
import Avatar from '@/Avatar'
import serviceMixin from 'src/PortalTenant/servicePortalOverview/mixin/serviceMixin'
import { pageTypes } from '@facilio/router'
import { emitEvent } from 'src/webViews/utils/mobileapps'
export default {
  mixins: [serviceMixin],
  components: {
    service,
    // visitor,
    booking,
    Avatar,
  },
  methods: {
    getLogoUrl(org = {}) {
      let { logoUrl } = org || {}
      if (logoUrl) {
        return this.$prependBaseUrl(logoUrl)
      }
      return null
    },
    goToFloorPlan(desks) {
      let routeData = {
        pageType: pageTypes.OVERVIEW,
        moduleName: 'floormap',
        floor: desks.floorId,
        indoorFloorPlanId: this.$getProperty(
          desks.floor,
          'data.indoorFloorPlanId'
        ),
        buildingId: desks.buildingId,
        userDeskId: desks.id,
      }
      emitEvent('navigate', routeData)
    },
  },
}
</script>
