<template>
  <div class="asset-details-widget pT0">
    <div class="container flex">
      <div class="field">
        <el-row class="border-bottom3 pB15 pT5">
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                Agent
              </div>
            </el-col>
            <el-col :span="14">
              <div class="fc-black-13 text-left pointer">
                {{ alarmDetails.agent.displayName }}
              </div>
            </el-col>
          </el-col>

          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                Type
              </div>
            </el-col>
            <el-col :span="14">
              <div class="fc-black-13 text-left">
                {{
                  alarmDetails
                    ? alarmDetails.agent
                      ? alarmDetails.agent.type
                      : '---'
                    : '--'
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
                Site
              </div>
            </el-col>
            <el-col :span="14">
              <div
                class="fc-black-13 text-left pointer"
                @click="openSite(spaceDetails.site.id)"
              >
                {{
                  spaceDetails
                    ? spaceDetails.site
                      ? spaceDetails.site.name
                      : '---'
                    : '--'
                }}
              </div>
            </el-col>
          </el-col>
          <el-col :span="12">
            <el-col :span="8">
              <div class="fc-black-13 text-left bold">
                Building
              </div>
            </el-col>
            <el-col :span="14">
              <div
                class="fc-black-13 text-left pointer"
                @click="
                  openBuilding(spaceDetails.site.id, spaceDetails.building.id)
                "
              >
                {{
                  spaceDetails
                    ? spaceDetails.building
                      ? spaceDetails.building.name
                      : '---'
                    : '--'
                }}
              </div>
            </el-col>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
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
      spaceDetails: null,
      alarmDetails: this.details.alarm,
      resourceDetails: this.details.alarm ? this.details.alarm.agent : null,
      occurenceDetails: this.details.occurrence,
      isAllVisible: false,
    }
  },
  mounted() {
    this.loadSpaceDetails()
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ...mapGetters(['getAssetCategory']),

    assetCategory() {
      if (this.alarmDetails && this.alarmDetails.resource) {
        let category = this.getAssetCategory(
          this.alarmDetails.resource.category.id
        )
        return category
      } else {
        return null
      }
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
    loadSpaceDetails() {
      if (this.resourceDetails) {
        let { spaceId, buildingId, siteId } = this.resourceDetails
        let locationId =
          spaceId > 0 ? spaceId : buildingId > 0 ? buildingId : siteId

        if (locationId > 0) {
          this.$util
            .loadBaseSpaceDetails(locationId)
            .then(result => {
              this.spaceDetails = result
            })
            .catch()
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
