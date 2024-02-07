<template>
  <div class="asset-summary-card">
    <div class="asset-details" ref="content-container">
      <div class="flex width20">
        <asset-avatar
          size="widget"
          name="false"
          :asset="details"
        ></asset-avatar>
      </div>
      <div class="flex mT30 mL35 width70">
        <div class="setup-dialog60">
          <div class="d-flex f14 mB20 fc-black-com">
            <asset-location :asset="details"></asset-location>
          </div>
          <div class="d-flex">
            <div class="f12 mR30 d-flex flex-direction-column">
              <span class="fc-blue-label mb5 mR10">{{
                $t('asset.assets.asset_category')
              }}</span>
              <span class="f14 fc-black-com">
                {{ getAssetCategoryDisplayName(details) }}
              </span>
              <span
                v-if="getAssetParentCategory(details)"
                class="f11 fc-black-com pT5"
                >(
                {{ getAssetParentCategory(details) }}
                )</span
              >
            </div>
            <div class="f12 d-flex flex-direction-column mL10">
              <span class="fc-blue-label mb5 mR10">{{
                $t('asset.assets.asset_type')
              }}</span>
              <span class="f14 fc-black-com">
                {{ getAssetTypeName(details) }}
              </span>
            </div>
          </div>
          <div class="f13 mT30 d-flex flex-direction-column">
            <span class="fc-blue-label mb5 mR10">{{
              $t('asset.assets.asset_description')
            }}</span>
            <span class="f14 fc-black-com">
              {{ getAssetDescription }}
            </span>
          </div>
        </div>
        <div v-if="canShowOperatingHours" class="fc-border-left width40">
          <div class="f12 mR30 mL35 d-flex flex-direction-column">
            <div class="d-flex">
              <span class="fc-blue-label mB15 mR10">{{
                $t('asset.assets.operating_hours')
              }}</span>
              <div
                v-if="businessHour"
                class="fc-dark-blue4-12 pointer"
                @click="showOphoursChooser"
              >
                {{ $t('asset.assets.change') }}
              </div>
            </div>
            <operating-hours
              class="f14"
              ref="operating-hours-view"
              :model.sync="businessHour"
              :changeBH="changeBH"
              :resourceId="details.id"
              :hideChangeBtn="businessHour"
              :isAssetBh="true"
            ></operating-hours>
          </div>
        </div>
      </div>
    </div>
    <div
      v-if="needsShowMore"
      class="text-center pT10 pB15 mR20 white-background"
    >
      <a
        @click="toggleVisibility()"
        class="fc-link fc-link-animation text-capitalize letter-spacing0_3 f13"
        >{{ showMoreLinkText }}</a
      >
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import AssetAvatar from '@/avatar/Asset'
import OperatingHours from '@/widget/OperatingHoursView'
import AssetLocation from '@/fields/AssetLocation'
import { mapGetters, mapState } from 'vuex'

export default {
  components: { AssetAvatar, OperatingHours, AssetLocation },
  props: ['details', 'params'],
  data() {
    return {
      cardBodyStyle: {
        padding: '0px',
        height: '100%',
        display: 'flex',
        flexDirection: 'row',
      },
      needsShowMore: false,
      isAllVisible: false,
      canShowOperatingHours: true,
      businessHoursList: [],
      businessHour: null,
    }
  },
  created() {
    this.initCategory()
  },
  mounted() {
    if (this.details && this.details.operatingHour) {
      this.getOperatingHours()
    }
    this.canShowMore()
  },
  computed: {
    showMoreLinkText() {
      let { isAllVisible } = this
      return isAllVisible
        ? this.$t('common._common.view_less')
        : this.$t('common._common.view_more')
    },
    layoutParams() {
      let { $attrs } = this
      let { layoutParams } = $attrs || {}
      return layoutParams
    },
    currentHeight() {
      let { $refs } = this
      let { scrollHeight } = $refs['content-container'] || {}
      return !isEmpty(scrollHeight) ? scrollHeight : 200
    },
    getAssetDescription() {
      let { details } = this
      let { description } = details || {}
      return !isEmpty(description)
        ? description
        : this.$t('common._common.no_description')
    },
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    ...mapGetters(['getAssetType', 'getAssetCategory']),
  },
  methods: {
    getOperatingHours() {
      return this.$http.get('/v2/businesshours/list').then(response => {
        if (response.data.responseCode === 0) {
          let { result } = response.data
          let list = !isEmpty(result.list) ? result.list : []

          this.businessHour = list.find(
            businessHour => businessHour.id === this.details.operatingHour
          )
        }
      })
    },
    toggleVisibility() {
      this.isAllVisible = !this.isAllVisible
      let { isAllVisible, currentHeight, $refs, layoutParams } = this
      if (isAllVisible) {
        this.$nextTick(() => {
          this.resizeWidget({
            height: currentHeight + 100,
            width: $refs['content-container'].scrollWidth,
          })
        })
      } else {
        this.$nextTick(() => this.resizeWidget(layoutParams))
      }
    },
    canShowMore() {
      let { currentHeight } = this
      this.needsShowMore = currentHeight > 230
    },
    resizeWidget(params) {
      let { $attrs } = this
      return $attrs.resizeWidget(params)
    },
    changeBH(businesshour) {
      this.businessHour = businesshour
    },
    showOphoursChooser() {
      this.$refs['operating-hours-view'].showOperatingHoursDialog = true
    },
    getAssetCategoryDisplayName(data) {
      let { category } = data || {}
      let { id } = category || {}
      let { displayName } = this.getAssetCategory(id) || {}
      return !isEmpty(displayName) ? displayName : '- - -'
    },
    getAssetTypeName(data) {
      let { type } = data || {}
      let { id } = type || {}
      let { name } = this.getAssetType(id) || {}
      return !isEmpty(name) ? name : '- - -'
    },
    getAssetParentCategory(data) {
      let { category } = data || {}
      let { id } = category || {}
      let { parentCategoryId } = this.getAssetCategory(id) || {}

      if (parentCategoryId !== 0) {
        let { displayName } = this.assetCategory.find(
          assetCategory => assetCategory.id === parentCategoryId
        )
        return !isEmpty(displayName) ? displayName : '- - -'
      }
    },
    async initCategory() {
      await this.$store.dispatch('loadAssetCategory')
    },
  },
}
</script>
<style scoped>
.asset-summary-card {
  flex-direction: column;
}
.asset-details {
  flex-grow: 1;
  text-align: left;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  overflow: hidden;
}
</style>
