<template>
  <transition name="fade">
    <div class="view-panel">
      <div class="view-list-container">
        <div class="header-container">
          <div class="label-txt-black fwBold text-uppercase">
            {{ $t('common._common.views') }}
          </div>
          <inline-svg
            src="svgs/arrow"
            class="rotate-90 pointer d-flex back-icon mL-auto"
            iconClass="icon icon-xs"
            @click.native="closeViewSidePanel()"
          ></inline-svg>
        </div>
        <div class="search-container mT20 mb25">
          <el-input
            placeholder="Search"
            v-model="searchText"
            type="search"
            :prefix-icon="'el-icon-search'"
            class="fc-input-full-border2"
          >
          </el-input>
        </div>
        <div class="views-group">
          <div v-for="group in filteredList" :key="group.id">
            <template v-if="!$validation.isEmpty(group.views)">
              <div
                v-if="$validation.isEmpty(searchText)"
                class="folder-name pB10 mT20 text-uppercase"
              >
                {{ group.displayName || group.name }}
              </div>
              <div
                v-for="(view, index) in group.views"
                :key="index"
                class="view-item pT10 pB15 pointer"
                :class="isActiveView(view) ? 'active' : ''"
                @click="changeView(view, group)"
              >
                <div class="view-name ellipsis">
                  {{ view.displayName }}
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
      <div
        v-if="isWebTabsEnabled"
        class="view-manager-btn"
        @click="loadViewManager"
      >
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex"
          iconClass="icon icon-sm"
        ></inline-svg>
        <span class="label mL10 text-uppercase">
          {{ $t('viewsmanager.list.views_manager') }}
        </span>
      </div>
      <portal-target v-else name="view-manager-link"></portal-target>
    </div>
  </transition>
</template>

<script>
import ViewsList from 'src/pages/views/ViewsSidePanel'
import { findRouteForTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
export default {
  extends: ViewsList,
  methods: {
    loadViewManager() {
      let parentPath = this.findRoute()
      if (!isEmpty(parentPath)) {
        this.$router.push({
          path: parentPath + '/' + this.moduleName + '/viewmanager',
        })
      }
    },
    findRoute() {
      let tabType = tabTypes.CUSTOM
      let config = { type: 'portfolio' }
      let { name } = findRouteForTab(tabType, { config }) || {}

      return name ? this.$router.resolve({ name }).href : null
    },
  },
}
</script>
<style lang="scss" scoped>
.view-panel {
  display: flex;
  flex-direction: column;
  .view-manager-btn {
    margin-bottom: 60px;
    display: flex;
    margin-top: auto;
    border: 1px solid #f4f4f4;
    background: #fff;
    border-top: none;
    padding: 15px;
    cursor: pointer;
    .label {
      font-size: 12px;
      font-weight: bold;
      letter-spacing: 0.92px;
      color: #324056;
    }
  }
}
</style>
