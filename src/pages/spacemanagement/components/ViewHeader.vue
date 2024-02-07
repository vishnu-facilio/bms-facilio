<template>
  <ul
    class="subheader-tabs pull-left"
    :class="{
      'has-more-views':
        !$validation.isEmpty(filteredViews.more) && !showCurrentViewOnly,
    }"
  >
    <div class="flex-middle">
      <div class="fL fc-subheader-left d-flex">
        <inline-svg
          v-if="!canShowViewsSidePanel && !clientPortalUser()"
          src="svgs/hamburger-menu"
          class="d-flex pointer hamburger-icon self-center"
          iconClass="icon icon-sm"
          @click.native="openViewsSidePanel"
        ></inline-svg>
      </div>

      <template v-for="(item, index) in filteredViews.list">
        <li
          :key="item.name + index"
          class="self-center"
          :class="[currentView === item.name && 'active']"
          @click="goToView(item, currentGroup)"
        >
          <a>{{ item.label }}</a>
        </li>
      </template>

      <template
        v-if="!$validation.isEmpty(filteredViews.more) && !showCurrentViewOnly"
      >
        <el-dropdown
          trigger="click"
          class="mL20 pointer more-views-dropdown"
          @command="goToView"
        >
          <span class="el-dropdown-link">
            <inline-svg src="svgs/header-more"></inline-svg>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(item, index) in filteredViews.more"
              :key="item.name + index"
              :command="item"
            >
              {{ item.label }}
            </el-dropdown-item>
            <el-dropdown-item
              v-if="showRearrange"
              class="rearrange-option"
              :command="{ name: 'rearrange-views' }"
              >{{ $t('setup.users_management.rearrange') }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </div>

    <ViewCustomization
      :visible.sync="showReorderPanel"
      :reload="true"
      :menu="currentViews"
      :moduleName="moduleName ? moduleName : ''"
      @onchange="loadViews()"
    ></ViewCustomization>
    <NewView :pathPrefix="pathPrefix"></NewView>
  </ul>
</template>
<script>
import ViewHeader from 'src/newapp/components/ViewHeader.vue'
import { isEmpty } from '@facilio/utils/validation'
import { isWebTabsEnabled } from '@facilio/router'

export default {
  extends: ViewHeader,
  methods: {
    goToView(view, group = null) {
      if (!isEmpty(view)) {
        let { name: viewname } = view
        let { moduleName } = this
        if (viewname === 'rearrange-views') {
          this.showReorderPanel = true
          return
        }

        let { query } = this.$route

        if (isWebTabsEnabled()) {
          this.$router
            .push({
              name:
                moduleName === 'building'
                  ? 'building-portfolio-module'
                  : 'site-portfolio-module',
              params: { moduleName, viewname },
            })
            .catch(error => console.warn('Could not switch view\n', error))
        } else {
          let path = this.getPathForView(view, group)
          this.$router.push({ path, query }).catch(() => {})
        }
      }
    },
    clientPortalUser() {
      if (
        this.$helpers.isLicenseEnabled('CLIENT_PORTAL') &&
        this.$helpers.isPortalUser()
      ) {
        return true
      } else {
        return false
      }
    },
  },
}
</script>
