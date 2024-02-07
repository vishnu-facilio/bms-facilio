<template>
  <div>
    <div class="height100 flex-wrap">
      <div class="flex-middle justify-around pL20 pR20">
        <div class="flex justify-between mT30">
          <div class="width100 flex justify-center">
            <img
              src="~assets/svgs/tenant/tenantunit/area.svg"
              class="flLeft detail-card-icon"
            />
          </div>
          <div class="bold width100 flex justify-center mT20 f15">
            {{ getArea }}
          </div>
          <div class="width100 flex justify-center mT10 f13">
            {{ $t('common._common.area_sq_ft') }}
          </div>
        </div>
        <div class="seperator"></div>
        <div class="flex justify-between mT30">
          <div class="width100 flex justify-center">
            <img
              src="~assets/svgs/tenant/tenantunit/occupancy.svg"
              class="flLeft detail-card-icon"
            />
          </div>
          <div class="bold width100 flex justify-center mT20 f15">
            {{ getMaxOccupancy }}
          </div>
          <div class="width100 flex justify-center mT10 f13">
            {{ $t('common._common.max_occupancy') }}
          </div>
        </div>
        <div class="seperator"></div>
        <div class="flex justify-between mT30">
          <div class="width100 flex justify-center">
            <img
              src="~assets/svgs/tenant/tenantunit/asset.svg"
              class="flLeft detail-card-icon"
            />
          </div>
          <div
            class="bold width100 flex justify-center mT20 f15"
            v-bind:class="{ 'show-link': isValidCount(totalAssets) }"
            @click="redirectToList(totalAssets)"
          >
            {{ totalAssets }}
          </div>
          <div class="width100 flex justify-center mT10 f13">
            {{ $t('common._common.assets') }}
          </div>
        </div>
        <div class="seperator"></div>
        <div class="flex justify-between mT30">
          <div class="width100 flex justify-center">
            <img
              src="~assets/svgs/tenant/tenantunit/office.svg"
              class="flLeft detail-card-icon"
            />
          </div>
          <div class="bold width100 flex justify-center mT20 f15">
            {{ getSpaceCategory }}
          </div>
          <div class="width100 flex justify-center mT10 f13">
            {{ $t('common._common.category') }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  props: ['details'],
  data() {
    return {
      totalAssets: '---',
    }
  },
  async created() {
    await this.getAssetsCount()
  },
  computed: {
    getArea() {
      let { details } = this
      let { area } = details || {}
      return isEmpty(area) ? '---' : area
    },
    getMaxOccupancy() {
      let { details } = this
      let { maxOccupancy } = details || {}
      return isEmpty(maxOccupancy) ? '---' : maxOccupancy
    },
    getSpaceCategory() {
      let { details } = this
      let { spaceCategory } = details || {}
      let { name } = spaceCategory || {}
      return name || '---'
    },
  },
  methods: {
    async getAssetsCount() {
      let { details } = this
      let { id } = details
      if (!isEmpty(id)) {
        let queryParam = {}
        queryParam = {
          page: 1,
          moduleName: 'asset',
          viewName: 'all',
          withCount: true,
          filters: JSON.stringify({
            space: { operatorId: 36, value: [id.toString()] },
          }),
        }
        let { meta, error } = await API.get('/v3/modules/data/list', queryParam)
        if (!error) {
          let { pagination } = meta || {}
          let { totalCount } = pagination
          this.totalAssets = totalCount || '---'
        }
      }
    },
    isValidCount(value) {
      if (value && value !== null && value > 0) {
        return true
      }
      return false
    },
    async redirectToList(totalAssets) {
      if (!this.isValidCount(totalAssets)) {
        return
      }
      let { details } = this
      let { id } = details
      if (isEmpty(id)) {
        return
      }
      let filters = {
        space: { operatorId: 36, value: [id.toString()] },
      }
      let query = {}
      query.search = JSON.stringify(filters)
      query.includeParentFilter = true

      let { MODULE_TO_LIST_URL } = this.$constants
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('asset', pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: { viewname: 'all' },
            query: query,
          })
      } else {
        this.$router.push({
          path: MODULE_TO_LIST_URL['asset'],
          params: { viewname: 'all' },
          query,
        })
      }
    },
  },
}
</script>
<style scoped>
.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}
.seperator {
  margin-top: 30px;
  height: 50px;
  width: 1px;
  border: solid 1px #8ca1ad;
  opacity: 0.4;
}
.detail-card-icon {
  width: 27px;
  height: 27px;
}
.show-link:hover {
  color: #017aff;
  cursor: pointer;
  text-decoration: underline;
}
</style>
