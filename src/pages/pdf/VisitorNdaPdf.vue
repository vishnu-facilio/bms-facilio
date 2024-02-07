<template>
  <div>
    <div class="header-sidebar-hide">
      <div class="height100 scrollable">
        <div v-if="loading">Loading...</div>
        <div v-else-if="!visitorLog">
          Invalid visitor !!!
        </div>
        <div class="visitornda-print-con" v-else>
          <div class="visitornda-org-logo text-center">
            <div v-show="$org.logoUrl" class="text-center">
              <img :src="$org.logoUrl" style="width: 100px;" />
            </div>
          </div>
          <div class="fc-check-in-f22 pT10 font-bold">
            Non-Disclosure Aggreement
          </div>
          <div class="fc-kiosk-prin-16 pT20">
            {{ ndaContent }}
          </div>

          <div style="padding-top: 80px;">
            <div class="flex-middle justify-content-space">
              <div class="">
                <div class="fc-kiosk-prin-16 bold">
                  {{
                    visitorLog.visitor.name
                      ? visitorLog.visitor.name
                      : 'Visitor'
                  }}
                </div>
                <div class="fc-kiosk-prin-16">
                  {{ $helpers.formatDateFull(visitorLog.sysCreatedTime) }}
                </div>
              </div>

              <div class="">
                <img
                  v-if="visitorLog.signatureUrl"
                  :src="visitorLog.signatureUrl"
                  width="150"
                />
                <div class="kiosk-sign-txt">Signature</div>
                <div class=""></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { getVisitorSetting } from 'src/devices/VisitorKiosk/VisitorApis'
import { API } from '@facilio/api'

export default {
  data() {
    return {
      loading: true,
      visitorLog: null,
      ndaContent: null,
    }
  },
  mounted() {
    this.loadDetails().then(() => {
      this.loading = false
    })
  },
  computed: {
    id() {
      return this.$route.params.id
    },
  },
  methods: {
    async loadDetails() {
      let { id } = this
      let { error, visitorlog } = await API.fetchRecord('visitorlog', { id })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.visitorLog = visitorlog
      }

      let { visitorSettings } = await getVisitorSetting(
        this.visitorLog.visitorType.id
      )

      this.ndaContent = visitorSettings.finalNdaContent
    },
  },
}
</script>
