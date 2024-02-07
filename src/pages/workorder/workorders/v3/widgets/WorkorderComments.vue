<template>
  <div class="white-bg-block ">
    <div
      class="label-txt-black pT15 pB15 fw-bold  pL30 pR30 "
      style="border-bottom:1px solid #e5e5ea;"
    >
      {{ $t('maintenance.wr_list.comments') }}
    </div>
    <notes
      :module="'ticketnotes'"
      parentModule="workorder"
      class="assetsnotes  new-summary-block fc-visibility-gicon-visible flex-grow overflow-y-scroll"
      :record="details"
      :notify="showNotifyRequester"
      :layoutParams="layoutParams"
      :resizeWidget="resizeWidget"
      :isActive="false"
      :forWO="true"
    ></notes>
  </div>
</template>
<script>
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
export default {
  name: 'WorkorderComments',
  props: ['moduleName', 'details', 'layoutParams', 'widget', 'resizeWidget'],
  components: {
    Notes: () => import('@/widget/Notes'),
  },
  data() {
    return {
      woDescriptiontranslate: false,
    }
  },
  computed: {
    showNotifyRequester() {
      return !isEmpty(this.details?.workorder?.requester)
    },
  },
  methods: {
    handleTranslate() {
      this.woDescriptiontranslate = !this.woDescriptiontranslate
      eventBus.$emit('woDescriptiontranslate', this.woDescriptiontranslate)
    },
  },
}
</script>
<style lang="scss">
.comment-heading {
  border-bottom: 1px solid #e5e5ea;
  padding: 20px;
}
</style>
