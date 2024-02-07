<template>
  <FContainer>
    <FContainer
      display="flex"
      justifyContent="space-between"
      gap="sectionLarge"
      padding="containerXxLarge containerNone"
      marginRight="sectionLarge"
    >
      <FContainer width="200px">
        <FContainer padding="containerLarge containerXxLarge">
          <Glimpse
            :record="workorder"
            :field="fieldDetail"
            recordModuleName="workorder"
            :canShowLink="true"
            :config="config"
            @redirectToSummary="redirectToSummary"
          />
        </FContainer>
        <FContainer padding="containerLarge containerXxLarge" display="flex">
          <FContainer v-for="(item, index) in hierarchyList" :key="index">
            <FContainer
              @click="redirect(item.route)"
              v-if="
                workorder.resource.resourceType === 2 ||
                  index != hierarchyList.length - 1
              "
            >
              <FText appearance="bodyReg14" color="textMain">
                {{ item.displayName }}
                <FText
                  v-if="
                    (workorder.resource.resourceType === 2 &&
                      index !== hierarchyList.length - 1) ||
                      (workorder.resource.resourceType !== 2 &&
                        hierarchyList.length > 1 &&
                        index !== hierarchyList.length - 2)
                  "
                  >,
                </FText>
              </FText>
            </FContainer>
          </FContainer>
        </FContainer>
      </FContainer>
      <FContainer
        display="inline-flex"
        flex-direction="column"
        gap="containerMedium"
        marginTop="containerXxLarge"
        v-if="this.currentLocation"
      >
        <InlineSvg
          src="svgs/illustration-map"
          style="width:45px;height:20px"
          iconClass="icon-class-map"
        ></InlineSvg>
        <FContainer
          display="flex"
          marginTop="containerXLarge"
          justifyContent="center"
        >
          <FButton appearance="link" size="small">
            <a :href="getMapLink()" target="_blank">Map </a>
          </FButton>
        </FContainer>
      </FContainer>
    </FContainer>
  </FContainer>
</template>
<script>
import { FContainer, FText, FIcon, FButton } from '@facilio/design-system'
import spaceCardMixin from 'src/components/mixins/SpaceCardMixin'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import AssetAvatar from '@/avatar/Asset'
import { Glimpse } from '@facilio/ui/new-app'

import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { getUnRelatedModuleSummary } from 'src/util/relatedFieldUtil'
import { getBaseURL } from 'util/baseUrl'
export default {
  props: ['moduleName', 'details'],
  mixins: [spaceCardMixin, FetchViewsMixin],
  components: {
    AssetAvatar,
    FContainer,
    FIcon,
    FText,
    FButton,
    Glimpse,
  },
  data() {
    return {
      hierarchyList: [],
      loading: false,
      site: null,
      currentLocation: '',
      fieldDetail: {},
    }
  },
  computed: {
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Resource'
    },
    workorder() {
      return this.details
    },
    getSpaceorAsset() {
      return this.details.resource?.name ? this.details.resource.name : '___'
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.$account.data.currencyInfo || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
    config() {
      let { multiCurrency } = this
      return {
        dateformat: this.$dateformat,
        timezone: this.$timezone,
        timeformat: this.$timeformat,
        org: this.$account.org,
        multiCurrency,
        baseUrl: getBaseURL(),
      }
    },
  },
  created() {
    this.loadResourceDetails()
    this.resolveSiteName()
  },
  methods: {
    async loadResourceDetails() {
      this.loadWorkorderFields()
      this.hierarchyList = []
      let resource = this.$getProperty(
        this.workorder,
        `${this.resourceFieldKey || 'resource'}`
      )
      if (!isEmpty(resource)) {
        let resourceId = resource.id
        if (resource.resourceType === 2) {
          this.loading = true
          let { data, error } = await API.get(
            `/v2/assets/${resourceId}?fetchHierarchy=true`
          )
          if (error) {
            this.$message.error(error.message || 'Error Occurred')
          } else if (data?.asset?.space) {
            this.hierarchyList = this.initHierarchy(data?.asset?.space)
            this.currentLocation = data?.asset?.currentLocation
          }
          this.loading = false
        } else {
          this.loading = true
          let { data, error } = await API.get(
            `/v2/basespaces/${resourceId}?fetchDeleted=true`
          )
          if (error) {
            this.$message.error(error.message || 'Error Occurred')
          } else if (data?.basespace) {
            this.hierarchyList = this.initHierarchy(data?.basespace)
            this.currentLocation = data?.basespace?.currentLocation
          }
          this.loading = false
        }
      }
    },
    siteRoute() {
      let parentPath = this.findRoute()

      if (parentPath) {
        this.$router.push({
          path: `${parentPath}/site/${this.workorder.siteId}/overview`,
        })
      }
    },

    openFloorplan(resource) {
      if (resource && resource.floorId && resource.id) {
        this.$set(this.floorPlanViewMode, 'floorId', resource.floorId)
        this.$set(this.floorPlanViewMode, 'focus', {
          spaceId: resource.id,
        })
        this.floorplanVisible = true
      }
    },
    async resolveSiteName() {
      let { workorder } = this
      let { siteId } = workorder || {}

      if (!isEmpty(siteId)) {
        let { site } = await getUnRelatedModuleSummary(
          'workorder',
          'site',
          siteId
        )
        this.site = site || {}
      }
    },
    async openAsset(id) {
      let viewname = await this.fetchView('asset')

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('asset', pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({ name, params: { viewname, id } })
        }
      } else {
        this.$router.push({
          path: `/app/at/assets/${viewname}/${id}/overview`,
        })
      }
    },
    getMapLink() {
      let href = `https://www.google.com/maps/search/?api=1&query=${this.currentLocation}`
      return href
    },
    async loadWorkorderFields() {
      let params = {
        moduleName: 'workorder',
      }
      let { data, error } = await API.get(`v2/modules/fields/fields`, params)
      if (!error) {
        this.ticketFields = data?.fields || {}
        this.fieldDetail = this.ticketFields.find(
          item => item.name === 'resource'
        )
        this.fieldDetail.lookupModuleName = 'resource'
        //this.$set(this.fieldDetail, 'field', field)
        return this.fieldDetail
      }
      return this.fieldDetail
    },
    async redirectToSummary(field) {
      let { workorder } = this
      let id = workorder.resource.id || {}
      let moduleName = 'asset'
      let viewname = await this.fetchView(moduleName)
      let routerPath = null
      let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
      if (name) {
        routerPath = this.$router.resolve({ name, params: { viewname, id } })
      }
      !!routerPath && window.open(routerPath?.href, '_blank')
    },
  },
}
</script>
<style>
.icon-class-map {
  width: 47px;
  height: 36px;
}
</style>
