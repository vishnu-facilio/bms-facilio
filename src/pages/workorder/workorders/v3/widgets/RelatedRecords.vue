<template>
  <div class="width100 height100 fc-summary-content-scroll">
    <div>
      <div v-if="invokeRelatedListWidget">
        <WoRelatedListWidget
          :widget="woWidget"
          :details="workorder"
          moduleName="workorder"
        ></WoRelatedListWidget>
      </div>
      <div v-if="showPermitListWidget" class="white-bg-block mT20">
        <WorkPermitListWidget
          :widget="permitWidget"
          :staticWidgetHeight="'342'"
          :details="workorder"
          moduleName="workorder"
        ></WorkPermitListWidget>
      </div>
    </div>
  </div>
</template>
<script>
import WoRelatedListWidget from '@/relatedlist/ChildRelationListWidget'
import WorkPermitListWidget from '@/page/widget/common/RelatedListWidget'
import { mapState } from 'vuex'
export default {
  name: 'RelatedRecords',
  props: ['moduleName', 'details'],
  created() {
    this.$store.dispatch('view/loadModuleMeta', 'workorder')
    this.woWidget.relatedList.module = (this.moduleMeta || {}).module
  },
  data() {
    return {
      invokeRelatedListWidget: true,
      showPermitListWidget: false,
      woWidget: {
        relatedList: {
          module: {},
        },
      },
    }
  },
  components: {
    WorkPermitListWidget,
    WoRelatedListWidget,
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    currModuleName() {
      return 'workorder'
    },
    widgetTitle() {
      return 'Related Records'
    },
    workorder() {
      return this.details.workorder
    },
  },
}
</script>
