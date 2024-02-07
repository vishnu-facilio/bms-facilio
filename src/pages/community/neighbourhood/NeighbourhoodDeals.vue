<template>
  <div class="pL30 pR30 d-flex flex-direction-column">
    <div class="widget-topbar">
      <div class="widget-title mL0">Deals and Offers</div>
    </div>
    <div class="mB30">
      <div v-if="loading" class="flex-middle justify-content-center">
        <spinner :show="true" size="50"></spinner>
      </div>
      <div
        v-else-if="$validation.isEmpty(recordsList)"
        class="flex-middle justify-content-center flex-col"
      >
        <inline-svg
          src="svgs/community-empty-state/deals"
          iconClass="icon text-center icon-xxxlg"
        ></inline-svg>
        <div class="label-txt-black bold">
          {{ $t('tenant.dealsandoffers.no_data') }}
        </div>
      </div>
      <div v-else class="d-flex flex-row flex-wrap">
        <div
          v-for="(deal, index) in recordsList"
          class="neigh-deals-container cursor-pointer"
          :key="index"
          @click="openDealSummary(deal.id)"
        >
          <div class="d-flex flex-col">
            <ListAttachments
              :record="deal"
              :module="'dealsandoffersattachments'"
              customClass="deals-image"
            ></ListAttachments>
            <div v-tippy :content="deal.title" class="deals-text">
              {{ deal.title }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import ListAttachments from '@/relatedlist/ListAttachmentPreview'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
export default {
  props: ['details'],
  components: {
    ListAttachments,
  },
  data() {
    return {
      recordsList: [],
      loading: true,
    }
  },
  mounted() {
    this.loadNeighbourhoodDeals()
  },
  methods: {
    async loadNeighbourhoodDeals() {
      let filters = {
        neighbourhood: {
          operatorId: 36,
          value: [String((this.details || {}).id)],
        },
        active: {
          operatorId: 15,
          value: [String(true)],
        },
      }
      let params = {
        filters: JSON.stringify(filters),
        withCount: false,
      }
      let { list = [], error } = await API.fetchAll('dealsandoffers', params)
      this.loading = true
      if (error) {
        let {
          message = 'Error Occurred while fetching Neighbourhood deals',
        } = error
        this.$message.error(message)
      } else {
        if (!isEmpty(list)) {
          this.recordsList = list
        }
      }
      this.loading = false
    },

    openDealSummary(id) {
      const moduleName = 'dealsandoffers'
      const viewname = 'all'

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({ name, params: { viewname, id } })
        }
      } else {
        this.$router.push({
          name: 'dealsandoffersSummary',
          params: { viewname, id },
        })
      }
    },
  },
}
</script>
