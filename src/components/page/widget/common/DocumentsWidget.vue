<template>
  <div class="widget-content">
    <template v-if="!hideTitleSection">
      <div class="widget-topbar">
        <div class="widget-title">{{ widgetTitle }}</div>
        <div class="flex-end">
          <portal-target :name="widget.key + '-topbar'"></portal-target>
        </div>
      </div>
    </template>
    <documents
      :module="attachmentsModuleName"
      :parentModule="moduleName"
      :record="details"
      :layoutParams="layoutParams"
      :resizeWidget="resizeWidget"
      :groupKey="groupKey"
      :isActive="isActive"
      :widget="widget"
    >
    </documents>
  </div>
</template>
<script>
import Documents from '@/widget/Documents'

export default {
  props: [
    'details',
    'layoutParams',
    'resizeWidget',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'moduleName',
    'attachmentsModuleName',
    'widget',
  ],
  components: { Documents },
  computed: {
    isActive() {
      return (
        this.activeTab ===
        (this.widget.widgetParams
          ? this.widget.widgetParams.module
          : this.widget.widgetTypeObj.name)
      )
    },
    widgetTitle() {
      let { widget } = this
      return (widget || {}).title
        ? widget.title
        : this.$t('asset.assets.documents')
    },
  },
}
</script>
<style scoped></style>
