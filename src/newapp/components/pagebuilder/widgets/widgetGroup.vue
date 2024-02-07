<template>
  <FContainer>
    <portal :to="`builder-${widget.id}-${widget.name}`">
      <FContainer padding="containerXLarge">
        <FSegmentedControl v-model="activeTab" :tabsList="widgetTabs" />
      </FContainer>
    </portal>
    <template v-for="section in widgetTabs">
      <FContainer
        height="100%"
        v-if="activeTab == section.name"
        :key="section.id"
      >
        <widget-supplier
          class="widget-card page-widget-container widget-container "
          v-for="(widgetElement, index) in section.widgets"
          v-bind="$attrs"
          :groupKey="widgetKey"
          :activeTab="activeTab"
          :resizeWidgetId="widget.id"
          :resizeWidget="resizeWidget"
          :key="`${widgetElement.id} ${widgetElement.name} ${index}`"
          :widget="widgetElement"
          :hideTitleSection="true"
          :hideBorder="true"
        ></widget-supplier>
      </FContainer>
    </template>
  </FContainer>
</template>
<script>
import { FContainer, FSegmentedControl } from '@facilio/design-system'
import WidgetSupplier from '../WidgetSupplier.vue'

export default {
  props: ['widget', 'resizeWidget'],
  components: { FContainer, FSegmentedControl, WidgetSupplier },
  data() {
    return {
      activeTab: null,
      widgetTabs: [],
      tabsHeightList: {},
    }
  },
  created() {
    let { widgetDetail } = this.widget || {}
    let { sections } = widgetDetail
    this.widgetTabs = (sections || []).map(section => {
      return {
        ...section,
        widgets: (section.widgets || []).map(widget => {
          return { ...widget, height: widget.height + 3 }
        }),
        label: section.displayName,
        value: section.name,
      }
    })

    this.widgetKey = sections.reduce((acc, current) => {
      return `${acc} ${current.name}`
    }, '')
    this.tabsHeightList = sections.reduce((acc, section) => {
      let maxheight = section.widgets.reduce((prevMaxVal, widget) => {
        return Math.max(prevMaxVal, widget.height)
      }, new Number())
      acc[section.name] = maxheight
      return acc
    }, {})
    this.activeTab = this.$getProperty(sections, '0.name') || null
  },
  watch: {
    activeTab: {
      handler(newVal) {
        this.tabPaneChanged(newVal)
      },
      immediate: true,
    },
  },
  methods: {
    tabPaneChanged(newVal) {
      let h =
        this.$getProperty(this.tabsHeightList, `${newVal}`) ||
        this.widget?.height ||
        20
      this.resizeWidget({ h: h + 3 })
    },
  },
}
</script>
<style lang="scss">
.eltabs-flex.el-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
  .el-tabs__content {
    flex-grow: 1;
    .el-tab-pane {
      height: 100%;
    }
  }
}
</style>
