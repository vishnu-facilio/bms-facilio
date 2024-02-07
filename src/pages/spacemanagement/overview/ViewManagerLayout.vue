<template>
  <div class="views-manager-container">
    <div class="header-container">
      <div class="d-flex flex-direction-column">
        <div class="d-flex pointer" @click="goBack">
          <InlineSvg
            src="arrow-pointing-to-left2"
            iconClass="icon icon-xs self-center mR5"
          ></InlineSvg>
          <div class="text-fc-blue f11 letter-spacing0_5">
            {{ $t('viewsmanager.list.back') }}
          </div>
        </div>
        <div class="heading-black22 pT10">
          {{ $t('viewsmanager.list.views_manager') }}
        </div>
      </div>
      <portal-target
        name="view-manager-actions"
        class="mL-auto"
      ></portal-target>
    </div>
    <el-tabs class="manager-tabs-container" v-model="activeTabName">
      <el-tab-pane label="List Views" name="listViews">
        <ModuleListView
          v-if="activeTabName === 'listViews'"
          :moduleName="moduleName"
        ></ModuleListView>
      </el-tab-pane>
      <el-tab-pane label="Scheduled Views" name="scheduledViews">
        <ViewScheduledList
          v-if="activeTabName === 'scheduledViews'"
          :moduleName="moduleName"
        ></ViewScheduledList>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import ViewManagerLayout from 'src/newapp/viewmanager/ViewManagerLayout'
import { isWebTabsEnabled } from '@facilio/router'
import ModuleListView from 'src/pages/spacemanagement/overview/ModuleListView.vue'

export default {
  extends: ViewManagerLayout,
  components: {
    ModuleListView,
  },
  methods: {
    goBack() {
      let { moduleName } = this

      if (isWebTabsEnabled()) {
        this.$router.push({
          name:
            moduleName === 'building'
              ? 'building-portfolio-module'
              : 'site-portfolio-module',
          params: { moduleName },
        })
      } else {
        this.$router.push({
          name:
            moduleName === 'building'
              ? 'buildings-portfolio-home'
              : 'sites-portfolio-home',
          params: { moduleName },
        })
      }
    },
  },
}
</script>
