<template>
  <div class="space-management-v1-tree">
    <div class="d-flex flex-direction-column">
      <div
        class="fc-black-13 text-left site-border-bottom-grey pL20 pR20 pT10 pB10"
        @click="back"
      >
        <i class="el-icon-back pR10 bold"></i
        ><a class="fc-black-12 bold">
          {{ $t('common._common.back_list') }}
        </a>
      </div>
      <div
        v-if="!isSiteLoading"
        class="side-bar-site-details flex-middle"
        :class="{ siteActive: isSiteOverview }"
      >
        <div>
          <img
            v-if="site.photoId > 0"
            :src="getImage(site.photoId)"
            class="site-img-container"
          />
          <fc-icon
            v-else-if="newSiteSummary"
            group="default"
            name="site"
            size="50"
          ></fc-icon>
          <InlineSvg
            v-else
            :src="`svgs/spacemanagement/site`"
            class="pointer"
            iconClass="icon icon-60"
          ></InlineSvg>
        </div>
        <div class="pL15 width100">
          <div class="display-flex-between-space">
            <div class="fc-id">{{ `#${site.value}` }}</div>
            <div v-show="!hideSwitchSite">
              <el-popover
                placement="bottom"
                width="240"
                v-model="siteSwitcher"
                trigger="click"
                popper-class="site-switch-popover"
                @after-leave="searchText = null"
              >
                <div slot="reference" class="popover-text">
                  {{ $t('maintenance._workorder.change_site') }}
                </div>
                <div class="popover-body d-flex flex-direction-column">
                  <el-input
                    class="fc-input-full-border2 width100 spacenav-search"
                    prefix-icon="el-icon-search"
                    v-model="searchText"
                    autofocus
                  ></el-input>
                  <div
                    class="d-flex overflow-y-scroll flex-direction-column mT10"
                  >
                    <div
                      v-for="(site, siteIndex) in filteredSiteList"
                      :key="siteIndex"
                      :class="[
                        'popover-content pointer',
                        site.value == siteId && 'current-site',
                      ]"
                      @click="switchSite(site.value)"
                    >
                      <div class="display-flex-between-space">
                        <div class="label">{{ site.label }}</div>
                        <div v-if="site.value == siteId">
                          <i class="el-icon-check"></i>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </el-popover>
            </div>
          </div>
          <div class="display-flex-between-space">
            <div @click="openSiteSummary" class="label-text pointer">
              {{ site.label }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="loading">
      <spinner :show="true"></spinner>
    </div>
    <div v-else class="space-tree-v1">
      <div v-show="initialDataLoading">
        <spinner :show="true"></spinner>
      </div>
      <el-tree
        v-show="!initialDataLoading && !isSiteLoading"
        ref="tree"
        :props="treeProps"
        node-key="key"
        :load="loadTree"
        :highlight-current="true"
        :default-expanded-keys="defaultExpandedKeys"
        :current-node-key="currentNodeKey"
        @node-click="handleNodeClick"
        accordion
        lazy
      >
        <template v-slot="{ node, data }">
          <div
            class="space-nav-tree-slot align-center"
            :class="[
              node.level === 1 && 'building-border-bottom',
              node.level === 2 && 'floor-border-bottom',
            ]"
          >
            <div class="d-flex align-center">
              <i
                class="fa fa-circle site-dot-icon mR10"
                v-if="node.level === 3"
              ></i>
              <img
                :src="getImage(data.data.photoId)"
                v-if="data.data.photoId > 0 && node.level < 3"
                class="mR10 nav-img-container"
              />
              <fc-icon
                v-else-if="newSiteSummary && node.level < 3"
                group="default"
                :name="getIconSrc(data)"
                size="25"
                class="mL5 mR10"
              ></fc-icon>
              <InlineSvg
                :src="getIconSrc(data)"
                v-else-if="node.level < 3"
                class="mR10"
                iconClass="icon icon-xxxll"
              ></InlineSvg>
              <div :class="['label-text', node.isCurrent && 'bold']">
                {{ data.name }}
              </div>
            </div>
            <div>
              <i
                v-if="node.loading && !node.isLeaf"
                class="el-icon-loading"
              ></i>
              <i
                v-else-if="node.expanded && !node.isLeaf"
                class="el-icon-arrow-up"
              ></i>
              <i v-else-if="!node.isLeaf" class="el-icon-arrow-down"></i>
            </div>
          </div>
        </template>
      </el-tree>
    </div>
  </div>
</template>
<script>
import { isEmpty, isArray } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
import { getBaseURL } from 'util/baseUrl'
import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'
import { getFieldOptions } from 'util/picklist'
import SpaceMixin from '../helpers/SpaceHelper'
export default {
  mixins: [SpaceMixin],
  data() {
    return {
      treeProps: {
        label: 'name',
        children: 'spaces',
        isLeaf: 'leaf',
      },
      loading: false,
      defaultExpandedKeys: [],
      currentNodeKey: null,
      searchText: null,
      initialDataLoading: false,
      siteSwitcher: false,
      isSiteHandled: false,
      isSiteLoading: true,
      siteList: [],
      filteredSiteList: [],
    }
  },
  computed: {
    viewname() {
      if (this.$route.params.viewname) {
        return this.$route.params.viewname
      }
      return 'all'
    },
    pathname() {
      if (this.$route.params.pathname) {
        return this.$route.params.pathname
      }
      return 'sites'
    },
    site() {
      return (
        (this.siteList || []).find(site => site.value === this.siteId) || {}
      )
    },
    siteId() {
      return Number(this.$route.params.siteid) || -1
    },
    isSiteScope() {
      let currentSiteId = Number(this.$cookie.get('fc.currentSite')) || -1
      return currentSiteId > 0
    },
    currentRoutePath() {
      let { $route } = this
      let { path } = $route
      return path
    },
    isSiteOverview() {
      let { siteId, currentRoutePath } = this
      return this.getSiteLink(siteId) === currentRoutePath
    },
    hideSwitchSite() {
      return (this.siteList || []).length < 2 || this.isSiteScope
    },
  },
  created() {
    this.getSiteList().then(() => {
      this.isSiteLoading = false
      eventBus.$on('overviewRecordDetails', space => {
        if (this.loading) {
          this.loading = false
        }
        this.expandTree(space)
      })
      eventBus.$on('reloadTree', space => {
        this.loading = true
        this.initialDataLoading = true
        this.$nextTick(() => {
          if (!isEmpty(space)) {
            this.expandTree(space)
          }
          this.loading = false
        })
      })
    })
  },
  watch: {
    searchText: {
      handler() {
        this.getFilteredSiteList()
      },
    },
  },
  methods: {
    async getSiteList() {
      let { siteId } = this
      let defaultIds = []
      defaultIds.push(siteId)
      let { error, options } = await getFieldOptions({
        field: {
          lookupModuleName: 'site',
          additionalParams: {
            isToFetchDecommissionedResource: true,
          },
        },
        defaultIds,
      })
      if (!error) {
        this.siteList = options
        this.filteredSiteList = this.siteList
      }
    },
    async getFilteredSiteList() {
      let { siteId, searchText } = this
      let defaultIds = []
      defaultIds.push(siteId)
      let { error, options } = await getFieldOptions({
        field: {
          lookupModuleName: 'site',
          additionalParams: {
            isToFetchDecommissionedResource: true,
          },
        },
        defaultIds,
        searchText,
      })
      if (!error) {
        this.filteredSiteList = options
      }
    },
    switchSite(val) {
      let siteUrl = this.getSiteLink(val)
      if (siteUrl != this.currentRoutePath) {
        this.initialDataLoading = true
        this.loading = true
        this.$router.push({ path: siteUrl })
        this.siteSwitcher = false
      }
    },
    getIconSrc({ data }) {
      let { newSiteSummary } = this
      let { spaceType } = data
      let src = 'svgs/spacemanagement/'
      if (spaceType === 2) {
        src = newSiteSummary ? 'building' : (src += 'building')
      }
      if (spaceType === 3) {
        src = newSiteSummary ? 'floorstack' : (src += 'floor')
      }
      if (spaceType === 4) {
        src = newSiteSummary ? 'workspace' : (src += 'space')
      }
      return src
    },
    loadTree(node, resolve) {
      let promise = []
      promise.push(this.getSiteList())
      promise.push(this.site)
      Promise.all(promise).finally(() => {
        this.loadChildren(node, resolve)
      })
    },
    loadChildren(node, resolve) {
      if (node.level === 0) {
        this.initialDataLoading = true
        this.isSiteHandled = true
        this.loadSubTree(
          {
            name: this.site.label,
            path: this.getSiteLink(this.site.value),
            data: this.site,
          },
          resolve
        )
      } else {
        this.isSiteHandled = false
        this.loadSubTree(node.data, resolve)
      }
    },
    handleNodeClick(node) {
      if (node.path != this.currentRoutePath) {
        this.$router.push({ path: node.path })
      }
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    getSiteLink(siteId) {
      let parentPath = this.findRoute()
      if (parentPath) {
        return `${parentPath}/${this.pathname}/${this.viewname}/site/${siteId}/overview`
      }
    },
    getBuildingLink(siteId, buildingId) {
      let parentPath = this.findRoute()
      if (parentPath) {
        return `${parentPath}/${this.pathname}/${this.viewname}/site/${siteId}/building/${buildingId}`
      }
    },
    getFloorLink(siteId, floorId) {
      let parentPath = this.findRoute()
      if (parentPath) {
        return `${parentPath}/${this.pathname}/${this.viewname}/site/${siteId}/floor/${floorId}`
      }
    },
    getSpaceLink(siteId, spaceId) {
      let parentPath = this.findRoute()
      if (parentPath) {
        return `${parentPath}/${this.pathname}/${this.viewname}/site/${siteId}/space/${spaceId}`
      }
    },
    back() {
      let parentPath = this.findRoute()
      if (parentPath) {
        this.$router.push({
          path: `${parentPath}/${this.pathname}/` + this.viewname,
        })
      }
    },
    loadSubTree(item, resolve) {
      let spaceKey
      let subparams
      let spaceType = [4]
      if (this.isSiteHandled) {
        if (item.data.value) {
          subparams = [
            { key: 'site', value: item.data.value },
            { key: 'building', operator: 'is empty' },
            { key: 'floor', operator: 'is empty' },
            { key: 'space1', operator: 'is empty' },
            { key: 'space2', operator: 'is empty' },
            { key: 'space3', operator: 'is empty' },
            { key: 'space4', operator: 'is empty' },
            { key: 'space5', operator: 'is empty' },
          ]
        }
      } else {
        if (item.data.spaceType === 2) {
          subparams = [
            { key: 'building', value: item.data.id },
            { key: 'floor', operator: 'is empty' },
            { key: 'space1', operator: 'is empty' },
            { key: 'space2', operator: 'is empty' },
            { key: 'space3', operator: 'is empty' },

            { key: 'space4', operator: 'is empty' },
            { key: 'space5', operator: 'is empty' },
          ]
        } else if (item.data.spaceType === 3) {
          spaceKey = 'floor'
          subparams = [
            { key: spaceKey, value: item.data.id },
            {
              key: 'space1',
              operator: item.data.spaceId1 > 0 ? 'is' : 'is empty',
              value: item.data.spaceId1,
            },
            {
              key: 'space2',
              operator: item.data.spaceId2 > 0 ? 'is' : 'is empty',
              value: item.data.spaceId2,
            },
            {
              key: 'space3',
              operator: item.data.spaceId3 > 0 ? 'is' : 'is empty',
              value: item.data.spaceId3,
            },
            {
              key: 'space4',
              operator: item.data.spaceId4 > 0 ? 'is' : 'is empty',
              value: item.data.spaceId4,
            },
            {
              key: 'space5',
              operator: item.data.spaceId5 > 0 ? 'is' : 'is empty',
              value: item.data.spaceId5,
            },
          ]
        } else if (item.data.spaceType === 4) {
          spaceKey = 'space'
          if (item.data.spaceId1 < 0) {
            subparams = [
              { key: 'space1', operator: 'is', value: item.data.spaceId },
              { key: 'space2', operator: 'is empty' },
              { key: 'space3', operator: 'is empty' },
              { key: 'space4', operator: 'is empty' },
              { key: 'space5', operator: 'is empty' },
            ]
          } else if (item.data.spaceId2 < 0) {
            subparams = [
              { key: 'space2', operator: 'is', value: item.data.spaceId },
              { key: 'space3', operator: 'is empty' },
              { key: 'space4', operator: 'is empty' },
              { key: 'space5', operator: 'is empty' },
            ]
          } else if (item.data.spaceId3 < 0) {
            subparams = [
              { key: 'space3', operator: 'is', value: item.data.spaceId },
              { key: 'space4', operator: 'is empty' },
              { key: 'space5', operator: 'is empty' },
            ]
          } else if (item.data.spaceId4 < 0) {
            subparams = [
              { key: 'space4', operator: 'is', value: item.data.spaceId },
              { key: 'space5', operator: 'is empty' },
            ]
          } else if (item.data.spaceId5 < 0) {
            subparams = [
              { key: 'space5', operator: 'is', value: item.data.spaceId },
            ]
          } else {
            resolve([])
            return
          }
        } else {
          resolve([])
          return
        }
      }
      if (this.isSiteHandled || item.data.spaceType === 2) {
        let promises = []
        let itemId
        if (this.isSiteHandled) {
          itemId = item.data.value
        } else {
          itemId = item.data.id
        }
        let url = '/v2/basespace/getBaseSpaceChildren?baseSpaceId=' + itemId
        promises.push(this.$http.get(url))
        Promise.all(promises)
          .then(([baseSpaceList]) => {
            let siteChilren = []
            if (!isEmpty(baseSpaceList)) {
              let {
                data: {
                  responseCode,
                  result: { basespaces = [] },
                },
              } = baseSpaceList
              if (responseCode === 0) {
                siteChilren.push(...basespaces)
              }
            }
            let chilren = this.getChildData(siteChilren)
            resolve(chilren)
          })
          .finally(() => {
            if (this.initialDataLoading) {
              this.initialDataLoading = false
            }
            this.setCurrentKey()
          })
      } else {
        this.$util
          .loadSpacesContext(spaceType, null, subparams)
          .then(response => {
            let spaces = response.records
            let children = this.getChildData(spaces)
            resolve(children)
            this.setCurrentKey()
          })
      }
    },
    getChildData(records) {
      let children = []
      if (isArray(records)) {
        for (let bs of records) {
          let path = ''
          if (bs.spaceTypeVal === 'Site') {
            path = this.getSiteLink(bs.id)
          } else if (bs.spaceTypeVal === 'Building') {
            path = this.getBuildingLink(this.siteId, bs.id)
          } else if (bs.spaceTypeVal === 'Floor') {
            path = this.getFloorLink(this.siteId, bs.id)
          } else if (bs.spaceTypeVal === 'Space') {
            path = this.getSpaceLink(this.siteId, bs.id)
          } else if (bs.spaceTypeVal === 'Zone') {
            let parentPath = this.findRoute()
            if (parentPath) {
              path = `${parentPath}/zone/${bs.id}/overview`
            }
          }
          let isLeaf = false
          if (bs.spaceType === 1) {
            if (bs.noOfBuildings < 1 && bs.noOfIndependentSpaces < 1) {
              isLeaf = true
            }
          } else if (bs.spaceType === 2) {
            if (bs.noOfFloors < 1 && bs.noOfIndependentSpaces < 1) {
              isLeaf = true
            }
          } else if (bs.spaceType === 3) {
            if (bs.noOfIndependentSpaces < 1) {
              isLeaf = true
            }
          } else {
            if (bs.noOfSubSpaces < 1) {
              isLeaf = true
            }
          }
          children.push({
            key: bs.id + '_' + bs.spaceTypeVal,
            name: bs.name,
            path: path,
            data: bs,
            leaf: isLeaf,
          })
        }
      }
      return children
    },
    expandTree(data) {
      let defaultExpandedKeys = []
      let { spaceTypeVal, spaceType } = data
      let key = `${data.id}_${spaceTypeVal}`
      let treeRef = this.$refs.tree
      let loaded = false
      let init = false
      if (spaceTypeVal === 'Site') {
        key = null
      }
      if (!isEmpty(key)) {
        if (!isEmpty(treeRef)) {
          let node = treeRef.getNode(key)
          loaded = (node || {}).loaded
        } else {
          this.currentNodeKey = key
          init = true
        }
        if (!loaded && !isEmpty(data.id) && !isEmpty(key)) {
          defaultExpandedKeys.push(key)
        }
      }
      if (!isEmpty(data.buildingId) && spaceType != 2) {
        defaultExpandedKeys.push(`${data.buildingId}_Building`)
      }
      if (!isEmpty(data.floorId) && spaceType != 3) {
        defaultExpandedKeys.push(`${data.floorId}_Floor`)
      }
      if (!isEmpty(data.spaceId1)) {
        defaultExpandedKeys.push(`${data.spaceId1}_Space`)
      }
      if (!isEmpty(data.spaceId2)) {
        defaultExpandedKeys.push(`${data.spaceId2}_Space`)
      }
      if (!isEmpty(data.spaceId3)) {
        defaultExpandedKeys.push(`${data.spaceId3}_Space`)
      }
      if (!isEmpty(data.spaceId4)) {
        defaultExpandedKeys.push(`${data.spaceId4}_Space`)
      }
      if (!isEmpty(data.spaceId5)) {
        defaultExpandedKeys.push(`${data.spaceId5}_Space`)
      }
      this.defaultExpandedKeys = defaultExpandedKeys

      if (!init && !isEmpty(treeRef)) {
        this.$nextTick(() => {
          this.currentNodeKey = key
          this.setCurrentKey()
        })
      }
    },
    setCurrentKey() {
      let treeRef = this.$refs.tree
      let { currentNodeKey } = this
      let currentKey
      if (!isEmpty(treeRef)) {
        currentKey = treeRef.getCurrentKey()
        if (currentKey != currentNodeKey) {
          treeRef.setCurrentKey(currentNodeKey)
        }
      }
    },
    openSiteSummary() {
      let url = this.getSiteLink(this.siteId)
      if (url != this.currentRoutePath) {
        this.$router.push({ path: url })
      }
    },
    getImage(photoId) {
      return `${getBaseURL()}/v2/files/preview/${photoId}`
    },
  },
}
</script>
