<template>
  <div class="asset-details-widget pT0">
    <div class="container flex">
      <div class="field">
        <el-row class="border-bottom3 pB15 pT5">
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('common._common.asset') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div
                class="fc-black-13 text-left pointer"
                @click="openAsset(alarmDetails.resource.id)"
              >
                {{ resourceName }}
              </div>
            </el-col>
          </el-col>

          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('common._common.category') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div class="fc-black-13 text-left">
                {{
                  !$validation.isEmpty(assetCategory) ? assetCategory : '---'
                }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <!-- second row -->
        <el-row class="border-bottom3 pB15 pT15">
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('common.products.site') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div
                class="fc-black-13 text-left pointer"
                @click="openSite(spaceDetails.site.id)"
              >
                {{ siteName }}
              </div>
            </el-col>
          </el-col>
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('common._common.building') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div
                class="fc-black-13 text-left pointer"
                @click="
                  openBuilding(spaceDetails.site.id, spaceDetails.building.id)
                "
              >
                {{ buildingName }}
              </div>
            </el-col>
          </el-col>
        </el-row>
        <el-row class="pB15 pT15">
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('common._common.floor') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div
                class="fc-black-13 text-left pointer"
                @click="openFloor(spaceDetails.site.id, spaceDetails.floor.id)"
              >
                {{ floorName }}
              </div>
            </el-col>
          </el-col>
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                {{ $t('common.space_asset_chooser.space') }}
              </div>
            </el-col>
            <el-col :span="14">
              <div class="fc-dark-blue3-13 text-left">
                ---
              </div>
            </el-col>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import {
  isWebTabsEnabled,
  findRouteForTab,
  tabTypes,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
  ],
  data() {
    return {
      loading: false,
      lookupValue: null,
      isAllVisible: false,
      assetCategory: null,
    }
  },
  created() {
    this.getAssetCategory()
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    alarmDetails() {
      let { details } = this
      return this.$getProperty(details, 'alarm')
    },
    resourceDetails() {
      let { details } = this
      let { alarm } = details || {}
      return this.$getProperty(alarm, 'resource')
    },
    spaceDetails() {
      let { details } = this
      let { alarm } = details || {}
      return this.$getProperty(alarm, 'spaceDetails')
    },
    occurenceDetails() {
      let { details } = this
      return this.$getProperty(details, 'occurrence')
    },
    resourceName() {
      return this.$getProperty(this.resourceDetails, 'name', '---')
    },

    siteName() {
      return this.$getProperty(this, 'spaceDetails.site.name', '---')
    },
    buildingName() {
      return this.$getProperty(this, 'spaceDetails.building.name', '---')
    },
    floorName() {
      return this.$getProperty(this, 'spaceDetails.floor.name', '---')
    },
  },
  methods: {
    findRoute() {
      if (isWebTabsEnabled()) {
        let { $router } = this
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return $router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    openAsset(id) {
      if (id) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('asset', pageTypes.OVERVIEW) || {}
          if (name) {
            this.$router.replace({ name, params: { viewname: 'all', id } })
          }
        } else {
          let url = '/app/at/assets/all/' + id + '/overview'
          this.$router.replace({ path: url })
        }
      }
    },
    openSite(id) {
      if (id) {
        let parentPath = this.findRoute()

        if (parentPath) {
          let url = `${parentPath}/site/${id}/overview`
          this.$router.replace({ path: url })
        }
      }
    },
    openBuilding(siteId, id) {
      if (id) {
        let parentPath = this.findRoute()

        if (parentPath) {
          let url = `${parentPath}/site/${siteId}/building/${id}`
          this.$router.replace({ path: url })
        }
      }
    },
    openFloor(siteId, id) {
      if (id) {
        let parentPath = this.findRoute()

        if (parentPath) {
          let url = `${parentPath}/site/${siteId}/floor/${id}`
          this.$router.replace({ path: url })
        }
      }
    },
    async loadSpaceDetails() {
      if (this.resourceDetails) {
        let { spaceId, buildingId, siteId } = this.resourceDetails
        let locationId =
          spaceId > 0 ? spaceId : buildingId > 0 ? buildingId : siteId

        if (locationId > 0) {
          let url = `v2/basespaces/${spaceId}`
          let { data, error } = await API.get(url)
          if (isEmpty(error)) {
            this.spaceDetails = this.$getProperty(data, 'basespace')
          } else {
            this.$message.error(error.message)
          }
        }
      }
    },
    async getAssetCategory() {
      this.assetCategory = this.$getProperty(
        this,
        'details.alarm.readingAlarmAssetCategory.displayName'
      )
      if (!isEmpty(this.assetCategory)) {
        return
      } else {
        let assetCategoryId = this.$getProperty(
          this,
          'alarmDetails.rule.assetCategoryId'
        )
        let url = 'v2/module/data/list?moduleName=assetcategory'
        let { data, error } = await API.get(url)
        if (isEmpty(error)) {
          let assetCategory = (this.$getProperty(data, 'details') || []).find(
            assetCategory => assetCategory.id === assetCategoryId
          )
          this.assetCategory = this.$getProperty(assetCategory, 'displayName')
        } else {
          this.$message.error(error.message)
        }
      }
    },
  },
}
</script>
<style scoped>
.asset-details-widget {
  padding: 10px 30px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.field {
  flex: 0 100%;
  padding: 20px 0;
  border-bottom: 1px solid #edf4fa;
  transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
  -webkit-transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
  -moz-transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
}
.fc-link-animation {
  animation: linkanimation 0.85s linear infinite alternate;
  -webkit-animation: linkanimation 0.85s linear infinite alternate;
  -moz-animation: linkanimation 0.85s linear infinite alternate;
}
@keyframes linkanimation {
  from {
    -webkit-transform: translateY(-2px);
    transform: translateY(-2px);
  }
  to {
    -webkit-transform: translateY(2px);
    transform: translateY(2px);
  }
}
.field:last-child:not(:nth-child(even)) {
  border-bottom: none;
}
</style>
