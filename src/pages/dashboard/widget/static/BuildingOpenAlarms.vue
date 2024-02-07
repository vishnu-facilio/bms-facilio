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
  props: ['currentDashboard'],
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
  computed: {
    buildingId() {
      if (this.currentDashboard) {
        return this.currentDashboard.buildingId
      }
      if (this.$route.params.buildingid) {
        return this.$route.params.buildingid
      }
      return null
    },
  },
  methods: {
    initdata() {
      let self = this
      self.countSummary.loading = true
      let params = {
        staticKey: 'buildingopenalarms',
        paramsJson: {
          buildingId: parseInt(this.buildingId),
        },
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
          console.log(error)
          self.countSummary.loading = false
        })
    },
    filteralarms(type) {
      let filterObj = {
        resource: {
          operatorId: 38,
          value: [this.buildingId + ''],
        },
      }
      let path

      if (type === 'active') {
        path =
          '/app/fa/faults/active?includeParentFilter=true&search=' +
          encodeURIComponent(JSON.stringify(filterObj))
      } else {
        path =
          '/app/fa/faults/unacknowledged?includeParentFilter=true&search=' +
          encodeURIComponent(JSON.stringify(filterObj))
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
  font-size: 5rem;
}
</style>
