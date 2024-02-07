<template>
  <div class="row">
    <div class="col-12">
      <div class="openalarms">
        <div class="row db-test-2 db-fire-test mobile-db-fire-test1">
          <div class="col-md-12 col-lg-12" style="text-align: center">
            <div class="q-item-main q-item-section">
              <div
                class="q-item-label clickable"
                v-bind:class="{
                  zoomout: countSummary.data.unacknowledged > 0,
                  countzero: countSummary.data.unacknowledged < 1,
                }"
                @click="filteralarms('unacknowledged')"
              >
                {{ countSummary.data.unacknowledged }}
              </div>
              <div class="q-item-sublabel">
                {{ $t('home.dashboard.unacknowledged') }}
              </div>
            </div>
          </div>
        </div>
        <div class="q-item-separator-component"></div>
        <div class="row db-test-2 db-fire-test mobile-db-fire-test2">
          <div class="col-md-12 col-lg-12" style="text-align: center">
            <div class="q-item-main q-item-section">
              <div
                class="q-item-label clickable"
                @click="filteralarms('active')"
                v-bind:class="{
                  unassigned: countSummary.data.active > 0,
                  countzero: countSummary.data.active < 1,
                }"
              >
                {{ countSummary.data.active }}
              </div>
              <div class="q-item-sublabel">
                {{ $t('home.dashboard.active') }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      countSummary: {
        loading: true,
        isdemodata: false,
        data: {
          unacknowledged: 0,
          active: 0,
        },
      },
    }
  },
  mounted() {
    this.initdata()
  },
  methods: {
    initdata() {
      let self = this
      self.countSummary.loading = true
      if (this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        this.$util.getdefaultWorkFlowResult(4).then(d => {
          self.countSummary.data['unacknowledged'] = d.unacknowledged
          self.countSummary.data['active'] = d.active
          self.countSummary.loading = false
        })
      } else {
        let params = {
          staticKey: 'openalarms',
        }
        self.$http
          .post('dashboard/getCardData', params)
          .then(function(response) {
            if (
              response.data &&
              response.data.cardResult &&
              response.data.cardResult.result
            ) {
              let result = response.data.cardResult.result
              self.countSummary.data['unacknowledged'] = result.unacknowledged
              self.countSummary.data['active'] = result.active
            }
            self.countSummary.loading = false
          })
          .catch(function(error) {
            self.countSummary.loading = false
          })
      }
    },
    filteralarms(type) {
      let path = '/app/fa/faults/unacknowledged'

      if (type === 'active') {
        path = '/app/fa/faults/active'
      } else if (type === 'unacknowledged') {
        path = '/app/fa/faults/unacknowledged'
      }
      if (!this.$mobile) {
        this.$router.push(path)
      }
    },
  },
}
</script>
<style>
.openalarms .zoomout {
  font-size: 5.2vw;
}
</style>
