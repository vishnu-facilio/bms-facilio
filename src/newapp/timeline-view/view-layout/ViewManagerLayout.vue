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
      <el-tab-pane label="Timeline Views" name="timelineViews">
        <TimeLineViewList
          v-if="activeTabName === 'timelineViews'"
          :moduleName="moduleName"
        ></TimeLineViewList>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script>
import ViewManagerLayout from 'src/newapp/viewmanager/ViewManagerLayout.vue'
import TimeLineViewList from './TimeLineViewList.vue'
import { isEmpty } from '@facilio/utils/validation'
import { findRouteForTab, pageTypes, isWebTabsEnabled } from '@facilio/router'
export default {
  extends: ViewManagerLayout,
  components: { TimeLineViewList },
  data() {
    return {
      activeTabName: 'timelineViews',
    }
  },
  methods: {
    goBack() {
      let { moduleName } = this
      let routePath

      if (isWebTabsEnabled()) {
        let {
          name: resourceSchedulerName,
        } = findRouteForTab(pageTypes.TIMELINE_LIST, { moduleName })

        routePath = !isEmpty(resourceSchedulerName)
          ? { name: resourceSchedulerName }
          : {}
        this.$router.push(routePath)
      } else {
        this.$router.push({ path: '/app/wo/timeline' })
      }
    },
  },
}
</script>
