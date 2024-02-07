<template>
  <shimmer-loading v-if="loading" class="card-shimmer"> </shimmer-loading>
  <div class="dragabale-card mini-cards" v-else>
    <div class="fc-b-card pT5 mobile-building-card">
      <div class="row pT10 pB10" v-if="building">
        <div class="col-5 p10 text-center">
          <img
            class="fc-avatar profilemini-avatar"
            v-bind:src="building.avatar"
          />
        </div>
        <div class="col-7 self-center mobile-building-card-right">
          <div class="row pB10 en">
            <div class="fvw18 bold text-left mobile-building-name">
              {{ building.name }}
            </div>
          </div>
          <div
            class="fvw13 bold sb-secondary-color text-left mobile-buidling-year"
          >
            {{ $t('panel.tyre.this_month') }}
          </div>
          <div class="row building mobile-building-row-val">
            <span
              class="fvw20 bold en mobile-building-val"
              style="padding-top: 1px;"
              >{{
                (building.currentVal && building.currentVal.consumption) || '-'
              }}</span
            >
            <div
              class="fvw14 secondary-color mobile-current-val"
              style="padding-top: 5px;"
            >
              &nbsp; {{ building.currentVal && building.currentVal.unit }}
            </div>
          </div>
          <div class="row en-color-1">
            <div
              class="fvw16 bold row mobile-building-img-row"
              v-if="building.arrow"
              style="padding-top: 5px;"
            >
              <img
                style="background-color:transparent;height: 1vw;position: relative;top: 5px;"
                v-bind:src="building.arrow.direction"
              />
              <div
                class="fvw18 bold mobile-buiding-arrow-val"
                v-bind:style="{ color: building.arrow.color }"
              >
                &nbsp; {{ building.variance + '%' || '-' }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import shimmerLoading from '@/ShimmerLoading'
export default {
  props: ['widget'],
  data() {
    return {
      loading: false,
      building: null,
    }
  },
  components: {
    shimmerLoading,
  },
  mounted() {
    if (this.widget) {
      this.initCard()
    }
  },
  watch: {
    widget: {
      handler: function(newVal, oldVal) {
        this.initCard()
      },
    },
  },
  methods: {
    initCard() {
      let self = this
      self.loading = true
      let params = null
      if (this.widget.dataOptions.building) {
        params = {
          staticKey: 'profilemini',
          baseSpaceId: this.widget.dataOptions.building.id,
        }
      } else {
        params = {
          widgetId: self.widget.id,
        }
      }
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.building = response.data.cardResult.card
          if (
            response.data.cardResult.card &&
            response.data.cardResult.card.currentVal.consumption &&
            response.data.cardResult.card.currentVal
          ) {
            if (response.data.cardResult.card.currentVal.consumption > 1000) {
              self.building.currentVal.consumption = (
                response.data.cardResult.card.currentVal.consumption / 1000
              ).toFixed(2)
              self.building.currentVal.unit = ' MWh'
            } else {
              self.building.currentVal.consumption = response.data.cardResult.card.currentVal.consumption.toFixed(
                2
              )
            }
          }
          if (
            response.data.cardResult.card &&
            response.data.cardResult.card.variance
          ) {
            self.building.arrow = self.$helpers.arrowHandle(
              self.building.variance
            )
          }
          self.loading = false
        })
        .catch(function(error) {
          self.loading = false
        })
    },
  },
}
</script>
<style>
.mini-cards .fc-b-card {
  box-shadow: unset;
  border: none;
}
.profilemini-avatar {
  width: 6vw;
  height: 6vw;
}
@media only screen and (min-width: 1800px) {
  .mini-cards .fc-b-card {
    padding-top: 10px;
  }
}
@media only screen and (min-width: 2100px) {
  .mini-cards .fc-b-card {
    padding-top: 20px;
  }
}
</style>
