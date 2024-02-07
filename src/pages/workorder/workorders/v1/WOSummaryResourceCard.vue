<template>
  <div>
    <div v-if="loading" class="white-bg-block p20 wo-space-card">
      <spinner size="50" :show="true"></spinner>
    </div>
    <div
      class="white-bg-block p20 wo-space-card"
      v-else-if="record && record.resource"
    >
      <div
        class="fc-id11 text-uppercase"
        v-if="record.resource.resourceType === 2"
      >
        {{ $t('maintenance._workorder.asset') }}
      </div>
      <div class="fc-id11 text-uppercase" v-else>
        {{ $t('maintenance._workorder.space') }}
      </div>
      <div class="flex-middle label-txt-black pT10">
        <div class="flex-middle">
          <div>
            <div v-if="record.resource.resourceType === 2">
              <AssetAvatar
                size="lg"
                name="false"
                :asset="{ id: -1, name: '' }"
                class="asset-space-img"
              >
              </AssetAvatar>
            </div>
            <div v-else-if="record.resource.spaceType === 1">
              <InlineSvg
                :src="`svgs/spacemanagement/site`"
                class="pointer"
                iconClass="icon icon-xxxl vertical-middle"
              ></InlineSvg>
            </div>
            <div v-else-if="record.resource.spaceType === 2">
              <InlineSvg
                :src="`svgs/spacemanagement/building`"
                class="pointer"
                iconClass="icon icon-xxxl vertical-middle"
              ></InlineSvg>
            </div>
            <div v-else-if="record.resource.spaceType === 3">
              <InlineSvg
                :src="`svgs/spacemanagement/floor`"
                class="pointer"
                iconClass="icon icon-xxxl vertical-middle"
              ></InlineSvg>
            </div>
            <div v-else>
              <InlineSvg
                :src="`svgs/spacemanagement/space`"
                class="pointer"
                iconClass="icon icon-xxxl vertical-middle"
              ></InlineSvg>
            </div>
          </div>
          <div class="pL15 flex-col">
            <div
              class="label-txt-black bold width-fit "
              v-bind:class="
                !$validation.isEmpty(record.resource.sysDeletedTime) &&
                record.resource.sysDeletedTime > 0
                  ? 'action-disabled'
                  : 'pointer'
              "
              v-if="record.resource.resourceType === 2"
            >
              <span @click="openAsset(record.resource.id)">{{
                record.resource.name ? record.resource.name : '---'
              }}</span>
            </div>
            <div
              v-else
              class="label-txt-black bold width-fit "
              v-bind:class="
                !$validation.isEmpty(record.resource.sysDeletedTime) &&
                record.resource.sysDeletedTime > 0
                  ? 'action-disabled'
                  : 'pointer'
              "
            >
              <span @click="redirectToPortfolio()">
                {{ recordName }}
              </span>
              <span
                v-if="
                  record.resource &&
                    record.resource.floorId &&
                    hierarchyList &&
                    hierarchyList.length === 4
                "
                @click="openFloorplan(record.resource)"
              >
                <el-tooltip
                  class="item"
                  effect="dark"
                  content="View Floor Plan"
                  placement="bottom"
                >
                  <i class="fa fa-map-o  f11 pL5 pointer fc-black-11 op6"></i>
                </el-tooltip>
              </span>
            </div>
            <!-- child  -->
            <div class="d-flex pT5 flex-wrap">
              <div v-for="(item, index) in hierarchyList" :key="index">
                <div
                  class="d-flex flex-direction-row"
                  @click="redirect(item.route)"
                  v-if="
                    record.resource.resourceType === 2 ||
                      index != hierarchyList.length - 1
                  "
                >
                  <div class="fc-black-11 text-left pointer line-height15">
                    {{ item.displayName }}
                  </div>
                  <div
                    class="pL10 pR10"
                    v-if="
                      (record.resource.resourceType === 2 &&
                        index !== hierarchyList.length - 1) ||
                        (record.resource.resourceType !== 2 &&
                          hierarchyList.length > 1 &&
                          index !== hierarchyList.length - 2)
                    "
                  >
                    <el-divider direction="vertical"></el-divider>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="white-bg-block p20 wo-site-card" v-else>
      <div class="fc-id11 text-uppercase">
        {{ $t('maintenance._workorder.site') }}
      </div>
      <div>
        <div class="flex-middle">
          <div @click="siteRoute">
            <InlineSvg
              :src="`svgs/spacemanagement/site`"
              class="pointer"
              iconClass="icon icon-xxxl vertical-middle"
            >
            </InlineSvg>
          </div>
          <div class="label-txt-black pointer pL10" @click="siteRoute">
            {{ siteName }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import spaceCardMixin from 'src/components/mixins/SpaceCardMixin'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import AssetAvatar from '@/avatar/Asset'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import { getUnRelatedModuleSummary } from 'src/util/relatedFieldUtil'

export default {
  props: ['record', 'resourceFieldKey', 'moduleName'],
  mixins: [spaceCardMixin, FetchViewsMixin],
  components: {
    AssetAvatar,
  },
  data() {
    return {
      hierarchyList: [],
      loading: false,
      siteName: '---',
    }
  },
  computed: {
    recordName() {
      let { record } = this
      let { resource } = record || {}

      return this.$getProperty(resource, 'name', '---')
    },
  },
  created() {
    this.loadResourceDetails()
    this.getSiteName(this.$getProperty(this.record, 'siteId', null))
  },
  methods: {
    async loadResourceDetails() {
      this.hierarchyList = []
      let resource = this.$getProperty(
        this.record,
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
          }
          this.loading = false
        }
      }
    },
    siteRoute() {
      let parentPath = this.findRoute()
      if (parentPath) {
        this.$router.push({
          path: `${parentPath}/site/${this.record.siteId}/overview`,
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
    async getSiteName(siteId) {
      if (!isEmpty(siteId)) {
        let { moduleName } = this
        if (moduleName == 'serviceRequest') {
          let id = this.$getProperty(this.record, 'id', null)
          if (!isEmpty(id)) {
            let url = `v3/serviceRequest/${id}/site`
            let { data } = await API.get(url, {})
            if (!isEmpty(data)) {
              let { site } = data || {}
              this.siteName = this.$getProperty(site, 'name', '---')
            }
          }
        } else {
          let { site } = await getUnRelatedModuleSummary(
            moduleName,
            'site',
            siteId
          )
          this.siteName = this.$getProperty(site, 'name', '---')
        }
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
    redirectToPortfolio() {
      let hierarchyList = this.$getProperty(this, 'hierarchyList', [])
      let route = this.$getProperty(
        hierarchyList,
        `${hierarchyList.length - 1}.route`,
        null
      )

      if (!isEmpty(route)) {
        this.redirect(route)
      }
    },
  },
}
</script>
