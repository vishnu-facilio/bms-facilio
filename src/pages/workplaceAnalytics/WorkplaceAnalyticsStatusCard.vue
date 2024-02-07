<template>
  <div>
    <cardLoading class="status-card-loading" v-if="loading"></cardLoading>
    <el-card
      shadow="hover"
      class="fc-workplace-analytics-card p0 flex"
      @click="clickAction()"
      v-else
    >
      <div class="flex" style="border-right: 1px solid #f4f4f4;">
        <img
          v-if="
            cardData.building &&
              cardData.building[0] &&
              cardData.building[0].photoId
          "
          :src="getImage(cardData.building[0].photoId)"
          class="wp-building-img-container pointer "
        />

        <InlineSvg
          v-else
          :src="`svgs/spacemanagement/building`"
          class="pointer wp-building-conteriner wp-building-img-container"
          iconClass="icon icon-xxxxlg "
        ></InlineSvg>
      </div>
      <div class="d-flex fc-card-item-vertical-align p15 pL20">
        <div class="flex">
          <div class="f20  fwBold">
            {{ building.name }}
          </div>
        </div>
        <div class="flex-middle ">
          <div class="mR20 border-right-sep pR20">
            <inline-svg
              src="svgs/Sq-feet"
              class="wp-secondary-icons"
              iconClass="icon text-center icon-xl"
            ></inline-svg>
            <div
              class="text-center wp-secodary-text"
              v-if="
                cardData.building &&
                  cardData.building[0] &&
                  cardData.building[0].area
              "
            >
              {{ `${formatCurrency(cardData.building[0].area)} sq.ft` }}
            </div>
            <div class="text-center wp-secodary-text" v-else>
              {{ `0 sq.ft` }}
            </div>
          </div>
          <div class="mR20 border-right-sep pR20">
            <inline-svg
              src="svgs/Desk3"
              class="wp-secondary-icons"
              iconClass="icon text-center icon-xl"
            ></inline-svg>
            <div class="text-center wp-secodary-text">
              {{ `${cardData.deskCount || 0} Desks` }}
            </div>
          </div>
          <div class="mR20">
            <inline-svg
              src="svgs/floor"
              class="wp-secondary-icons"
              iconClass="icon text-center icon-xl"
            ></inline-svg>
            <div class="text-center wp-secodary-text">
              {{ `${cardData.floorCount} Floors` }}
            </div>
          </div>
        </div>
        <div class="flex-middle">
          <div class="mR20">
            <div class=" f14 wp-text3 pB5">{{ $t('floorplan.vacant') }}</div>
            <div class="f22 fwBold text-center">
              {{ cardData.vacant }}
            </div>
          </div>
          <div class="mR20">
            <div class=" f14 wp-text3 pB5">{{ $t('floorplan.occupied') }}</div>
            <div class="f22 fwBold text-center">
              {{ cardData.occupied }}
            </div>
          </div>
          <div class="mR20">
            <div class=" f14 wp-text3 pB5">
              {{ $t('floorplan.occupiedPercentage') }}
            </div>
            <div class="f22 fwBold text-center">
              {{ cardData.occupied | percentage(cardData.deskCount) }}
            </div>
          </div>
          <div class="mR20">
            <div class=" f14 wp-text3 pB5">
              {{ $t('floorplan.reservable') }}
            </div>
            <div class="f22 fwBold text-center">
              {{ cardData.reservable }}
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import isEqual from 'lodash/isEqual'
import cardLoading from 'pages/card-builder/components/CardLoading'
import { getBaseURL } from 'util/baseUrl'
import { formatCurrency } from 'charts/helpers/formatter'
export default {
  props: ['building', 'floor', 'departments'],
  components: { cardLoading },
  created() {
    this.init()
  },
  filters: {
    percentage(value, total) {
      if (value === 0) {
        return '0%'
      }
      if (!value) {
        return value
      }
      return `${((value / total) * 100).toFixed(1)}%`
    },
  },
  data() {
    return {
      loading: false,
      cardData: null,
    }
  },
  watch: {
    departments(newVal, oldVal) {
      if (!isEqual(newVal, oldVal)) {
        this.init()
      }
    },
  },
  methods: {
    formatCurrency(value) {
      return formatCurrency(value, 0)
    },
    getImage(photoId) {
      return `${getBaseURL()}/v2/files/preview/${photoId}`
    },
    clickAction() {
      this.$emit('cardClicked', this.building)
    },
    async init() {
      this.loading = true
      let params = {
        cardContext: {
          cardLayout: 'deskstatus_layout_1',
          cardParams: {
            title: this.building.name,
            buildingId: this.building.id,
            floorId: this.floor?.id ? this.floor.id : null,
            departments: this.departments || [],
          },
        },
      }
      let { data, error } = await API.post(
        `v2/dashboard/cards/getCardData`,
        params
      )
      if (data?.data?.valueMap) {
        this.cardData = data.data.valueMap
      } else if (error) {
        this.$message.error(`${error.message}`)
      }
      this.loading = false
      this.$emit('dataLoaded')
    },
  },
}
</script>
<style lang="scss">
.fc-workplace-analytics-card {
  border-radius: 10px;
  background: #fff;
  height: 225px;
}
.fc-workplace-analytics-card .el-card__body {
  padding: 0px;
  display: flex;
}
.fc-workplace-analytics-card.is-hover-shadow:hover {
}
.fc-card-item-vertical-align {
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;
  height: 100%;
}
.status-card-loading {
  height: 225px;
  border-radius: 10px;
}
.wp-building-icon {
  font-size: 40px;
  padding-right: 10px;
  font-weight: 400;
}
.fw300 {
  font-weight: 300;
}
.fwhite {
  color: #fff;
}
.wp-building-conteriner {
  width: 200px;
  align-items: center;
  margin: auto;
  display: flex;
  flex-direction: column;
  opacity: 0.5;
}
.border-right-sep {
  border-right: solid 1px #e2e9eb;
}
.wp-secodary-text {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324056;
}
.wp-text3 {
  color: #666;
}
.wp-secondary-icons {
  display: flex;
  align-items: center;
  flex-direction: column;
  opacity: 0.4;
  padding-bottom: 10px;
}
.wp-building-img-container {
  width: 200px;
}
</style>
