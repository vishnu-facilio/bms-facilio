<template>
  <div>
    <div>
      <portal-target :name="widget.key + '-title-section'"></portal-target>
    </div>
    <WidgetCard
      v-if="widget.widgets && widget.widgets.length > 0"
      class="widget-card"
      :hideBg="
        hideBgWidgetsList.includes(widget.name) ||
          $getProperty(widgetParams, 'hideBg')
      "
      :isSidebarView="isSidebarView"
    >
      <el-tabs v-model="activeTab" :before-leave="resetWidgetSize">
        <el-tab-pane
          v-for="(item, index) in widget.widgets"
          :key="`section-${sectionKey}-item-${widget.key}-widget-${index}`"
          :name="getWidgetName(item)"
          :label="
            item.title
              ? item.title + 's'
              : $t(moduleNameHash[getWidgetName(item)])
          "
          lazy
        >
          <page-widget
            :widget="item"
            :groupKey="widget.key"
            :layoutParams="layoutParams"
            :resizeWidget="resizeWidget"
            :sectionKey="sectionKey"
            :hideTitleSection="true"
            :activeTab="activeTab"
            :isSidebarView="isSidebarView"
            v-bind="$attrs"
            class="page-widget-container widget-container"
          ></page-widget>
        </el-tab-pane>
      </el-tabs>
      <div style="position: absolute; right: 15px; top: 20px;">
        <portal-target :name="widget.key + '-topbar'"></portal-target>
      </div>
    </WidgetCard>
    <WidgetCard
      v-else
      class="widget-card"
      :hideBg="
        hideBgWidgetsList.includes(widget.name) ||
          $getProperty(widgetParams, 'hideBg')
      "
      :isSidebarView="isSidebarView"
      :key="`section-${sectionKey}-item-${widget.key}-widget`"
    >
      <page-widget
        :widget="widget"
        :layoutParams="layoutParams"
        :resizeWidget="resizeWidget"
        :sectionKey="sectionKey"
        :isSidebarView="isSidebarView"
        v-bind="$attrs"
        class="page-widget-container widget-container"
      ></page-widget>
    </WidgetCard>
  </div>
</template>
<script>
import PageWidget from 'pageWidgets/Widget'
import WidgetCard from 'pageWidgets/utils/WidgetCard'

export default {
  props: [
    'widget',
    'layoutParams',
    'resizeWidget',
    'sectionKey',
    'isSidebarView',
  ],
  components: { PageWidget, WidgetCard },

  data() {
    return {
      activeTab: this.getWidgetName(
        this.widget.widgets && this.widget.widgets.length > 0
          ? this.widget.widgets[0]
          : this.widget
      ),
      moduleNameHash: {
        reading: 'asset.assets.asset_readings',
        workorder: 'asset.assets.asset_wo',
        alarm: 'asset.assets.asset_alarms',
        preventivemaintenance: 'asset.assets.asset_pm',
        attachment: 'asset.assets.documents',
        comment: 'asset.assets.notes',
        history: 'asset.assets.history',
        relatedList: 'Related List',
      },
      hideBgWidgetsList: [
        'siteBuildings',
        'floorSpaces',
        'relatedSubSpaces',
        'utilityConnections',
      ],
    }
  },
  computed: {
    widgetParams() {
      if (this.widget.widgetParams) {
        return this.widget.widgetParams
      }
      return null
    },
  },
  methods: {
    getWidgetName(widget) {
      let { module } = widget.widgetParams || {}
      return module ? module : widget.widgetTypeObj.name
    },
    resetWidgetSize() {
      this.resizeWidget({ h: this.layoutParams.h, w: this.layoutParams.w })
    },
  },
}
</script>
