<template>
  <FContainer>
    <FTabs
      v-model="activeTab"
      :tabsList="widgetTabs"
      padding="containerMedium containerNone"
      :hideBorder="false"
      @change="tabPaneChanged"
    >
      <FTabPane
        v-for="section in widgetTabs"
        :activeKey="section.name"
        :key="section.id"
      >
        <FContainer :key="section.id" v-if="activeTab === section.name">
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
      </FTabPane>
    </FTabs>
  </FContainer>
</template>
<script>
import { FContainer, FTabs, FTabPane } from '@facilio/design-system'
import WidgetSupplier from './WidgetSupplier.vue'

export default {
  props: ['widget', 'resizeWidget'],
  components: {
    FContainer,
    WidgetSupplier,
    FTabs,
    FTabPane,
  },
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
  computed: {
    widgetList() {
      let { widgetTabs } = this
      return (widgetTabs || []).map(tab => {
        let { displayName: label, name: value } = tab
        return { label, value }
      })
    },
  },
  methods: {
    tabPaneChanged(newVal) {
      let h =
        this.$getProperty(this.tabsHeightList, `${newVal}`) ||
        this.widget?.height ||
        20
      this.resizeWidget({ h: h })
    },
  },
}
</script>
