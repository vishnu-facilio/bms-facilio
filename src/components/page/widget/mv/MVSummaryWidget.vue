<template>
  <div class="mv-summary-widget">
    <div class="mv-desc">
      <div class="fc-blue-label">{{ $t('mv.summary.description') }}</div>
      <div
        class="pm-summary-tab-subject mT10 textoverflow-height-ellipsis2"
        style="max-width: 830px;height:43px;"
      >
        <span v-if="!$validation.isEmpty(mvProject.description)">{{
          mvProject.description
        }}</span>
        <span v-else>{{ $t('mv.summary.nodescription') }}</span>
      </div>
      <div class="d-flex mT35">
        <div class="d-flex mR100">
          <asset-avatar
            :name="false"
            size="md"
            :asset="mvProject.meter"
          ></asset-avatar>
          <div class="mL10">
            <div class="avatar-title text-uppercase">
              {{ $t('mv.summary.asset_name') }}
            </div>
            <div class="mT5 avatar-content">
              {{ (mvProject.meter || {}).name }}
            </div>
          </div>
        </div>
        <div class="d-flex mR100">
          <space-avatar
            :name="false"
            size="md"
            :space="getCurrentSite(mvProject.siteId)"
          ></space-avatar>
          <div class="mL10">
            <div class="avatar-title text-uppercase">
              {{ $t('mv.summary.site_name') }}
            </div>
            <div class="mT5 avatar-content">
              {{ getSiteName(mvProject.siteId) }}
            </div>
          </div>
        </div>
        <div class="d-flex mR100">
          <avatar size="md" :user="mvProject.owner" color="#c3c3c3"></avatar>
          <div class="mL10">
            <div class="avatar-title text-uppercase">
              {{ $t('mv.summary.project_owner') }}
            </div>
            <div class="mT5 avatar-content">
              {{ getUserName(mvProject.owner) }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="mL-auto vertical-separator mR30" style="height: 150px;"></div>
    <div class="baseline-period">
      <div class="d-flex mT10">
        <div class="calender d-flex justify-content-center">
          <inline-svg
            src="svgs/calendar-time"
            class="vertical-middle self-center"
            iconClass="icon fill-white icon-md vertical-middle"
          ></inline-svg>
        </div>
        <div class>
          <div class="fc-blue-label pL10">
            {{ $t('mv.summary.baselineperiod') }}
          </div>
          <div class="flex-middle pT5">
            <div class="ecm-option date f13 letter-spacing0_6 mL10">
              {{ mvBaseLine.startTime | formatDate(true) }}
              <span class="mL15 mR15">-</span>
            </div>
            <div class="ecm-option date f13 letter-spacing0_6">
              {{ mvBaseLine.endTime | formatDate(true) }}
            </div>
          </div>
        </div>
      </div>
      <div class="pT50 d-flex">
        <!-- reporting card -->
        <div class="d-flex mT10">
          <div class="calender2 d-flex justify-content-center">
            <inline-svg
              src="svgs/calendar-time"
              class="vertical-middle self-center"
              iconClass="icon fill-white icon-md vertical-middle"
            ></inline-svg>
          </div>
          <div class>
            <div class="fc-blue-label pL10">
              {{ $t('mv.summary.reporting_period') }}
            </div>
            <div class="flex-middle pT5">
              <div class="ecm-option date f13 letter-spacing0_6 mL10">
                {{ mvProject.reportingPeriodStartTime | formatDate(true) }}
                <span class="mL15 mR15">-</span>
              </div>
              <div class="ecm-option date f13 letter-spacing0_6">
                {{ mvProject.reportingPeriodEndTime | formatDate(true) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Avatar from '@/Avatar'
import AssetAvatar from '@/avatar/Asset'
import SpaceAvatar from '@/avatar/Space'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details'],
  components: {
    Avatar,
    AssetAvatar,
    SpaceAvatar,
  },
  computed: {
    mvProject() {
      let { details } = this
      return details.mvProject
    },
    mvBaseLine() {
      let { details } = this
      let { baselines = [] } = details
      return baselines[0]
    },
  },
  methods: {
    getUserName(userObj) {
      if (!isEmpty(userObj)) {
        let { id } = userObj
        let currentUser = this.$store.getters.getUser(id)
        return currentUser.name === 'Unknown' ? '---' : currentUser.name
      }
      return '---'
    },
    getSiteName(siteId) {
      let site = this.$store.getters.getSite(siteId)
      return site.name ? site.name : '---'
    },
    getCurrentSite(siteId) {
      return this.$store.getters.getSite(siteId)
    },
  },
}
</script>

<style lang="scss">
.mv-summary-widget {
  padding: 30px;
  flex-direction: row;
  .fc-avatar.fc-avatar-md {
    height: 35px;
    width: 35px;
  }
  .avatar-title {
    font-size: 11px;
    font-weight: 500;
    letter-spacing: 0.5px;
    color: #8ca1ad;
  }
  .avatar-content {
    font-size: 13px;
    line-height: 1.29;
    letter-spacing: 0.5px;
    color: #324056;
  }
  .baseline-period {
    margin-right: 90px;
    .baseline-header {
      letter-spacing: 0.5px;
      color: #8ca1ad;
    }
  }
  .calender {
    width: 36px;
    height: 36px;
    border-radius: 50px;
    background-color: #efa25e;
  }
  .calender2 {
    width: 36px;
    height: 36px;
    border-radius: 50px;
    background-color: #83d2dd;
  }
}
</style>
