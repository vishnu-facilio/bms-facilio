<template>
  <div class="overflow-y-scroll">
    <template v-if="!hideTitleSection">
      <div class="widget-topbar">
        <div class="widget-title pL20">{{ widgetTitle }}</div>
      </div>
      <div class="flex-end">
        <portal-target :name="widget.key + '-topbar'"></portal-target>
      </div>
    </template>

    <notes
      :module="notesModuleName"
      :parentModule="moduleName"
      :isCustomModule="isCustomModule"
      class="assetsnotes flex-grow overflow-y-scroll"
      :record="details"
      :notify="canShowNotifyRequester"
      :title="widget ? widget.title : $t('asset.assets.notes')"
      :layoutParams="layoutParams"
      :resizeWidget="resizeWidget"
      :isActive="isActive"
      :groupKey="groupKey"
    ></notes>

    <el-dialog
      v-if="expand && renderNewComment"
      :visible="true"
      width="65%"
      custom-class="fc-dialofc-dialog-center-container scale-up-center ex-dialog"
      class="attribute-list-dialog"
      :isCustomModule="isCustomModule"
      :lock-scroll="false"
      :show-close="true"
      :append-to-body="true"
      :before-close="closeDialog"
      :close-on-press-escape="false"
      @close="
        () => {
          expand = false
        }
      "
      :close-on-click-modal="false"
    >
      <template slot="title">
        <div class="ex-dialog-header fW500 pL10">
          {{ 'Notes' }}
        </div>
      </template>
      <notes
        v-if="renderNewComment"
        :module="notesModuleName"
        :parentModule="moduleName"
        :isPopoverView="true"
        class="assetsnotes mT0 "
        style="height:556px;overflow: auto;"
        :record="details"
        :notify="canShowNotifyRequester"
        :title="widget ? widget.title : $t('asset.assets.notes')"
        :layoutParams="layoutParams"
        :resizeWidget="resizeWidget"
        :isActive="isActive"
        :groupKey="groupKey"
      ></notes>
    </el-dialog>
  </div>
</template>
<script>
import { eventBus } from '../utils/eventBus'

export default {
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'hideTitleSection',
    'groupKey',
    'activeTab',
    'widget',
    'resizeWidget',
    'notesModuleName',
  ],
  mounted() {
    eventBus.$on('expandNotesWidget', this.expandNotesWidget)
  },
  destroyed() {
    eventBus.$off('expandNotesWidget', this.expandNotesWidget)
  },
  data() {
    return {
      expand: false,
    }
  },
  components: {
    Notes: () => import('@/widget/Notes'),
  },
  computed: {
    isActive() {
      let { widgetParams = {}, widgetTypeObj = {} } = this.widget || {}

      return (
        this.activeTab ===
        (widgetParams ? widgetParams.module : widgetTypeObj.name)
      )
    },

    isCustomModule() {
      let { widget } = this
      return this.$getProperty(widget, 'relation.toModule.custom')
    },
    widgetTitle() {
      let { widget } = this
      return (widget || {}).title
        ? widget.title
        : this.$t('asset.assets.documents')
    },
    canShowNotifyRequester() {
      let { widget: { widgetParams } = {} } = this
      let { canShowNotifyRequestor = false } = widgetParams || {}
      return canShowNotifyRequestor || false
    },
    renderNewComment() {
      return this.$helpers.isLicenseEnabled('NEW_COMMENTS')
    },
    notesWidgetClass() {
      return this.renderNewComment ? 'overflow-y-scroll' : 'widget-content'
    },
  },
  methods: {
    closeDialog() {
      this.expand = false
    },
    expandNotesWidget() {
      this.expand = true
    },
  },
}
</script>
<style>
.expand-icon {
  width: 20px;
  height: 20px;
  color: #666666;
}
.ex-icon-container {
  position: relative;
  bottom: 10px;
  cursor: pointer;
  padding: 4px;
  height: 28px;
  width: 27px;
  border-radius: 2px;
}
.ex-icon-container:hover {
  background: rgba(202, 212, 216, 0.3);
  opacity: 0.8;
}
.ex-dialog .el-dialog__header {
  border-bottom: 1px solid #f1f1f1;
}
.ex-dialog-header {
  font-weight: 500;
}
.ex-dialog .el-dialog__body {
  padding: 0px;
}
</style>
