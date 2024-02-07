<template>
  <el-drawer
    :modal="false"
    size="31%"
    :visible="true"
    :with-header="false"
    :before-close="close"
    :modal-append-to-body="false"
    class="el-drawer-header"
  >
    <div class="el-drawer-body">
      <div class="comment-dialog-header" style="flex-shrink: 0;">
        <h3 class="comment-dialog-heading" style="text-transform: uppercase">
          {{ $t('maintenance.wr_list.attachments') }}
          <span>
            {{ noOfAttachments }}
          </span>
        </h3>
        <div class="comment-close">
          <i class="el-icon-close" aria-hidden="true" v-on:click="close()"></i>
        </div>
      </div>
      <div class="comment-dialog-body flex-grow overflow-scroll">
        <Attachments
          module="ticketattachments"
          :record="selectedWorkorder"
          :diasbleAttachment="true"
        ></Attachments>
      </div>
    </div>
  </el-drawer>
</template>
<script>
import Attachments from '@/relatedlist/SummaryAttachment'
export default {
  props: ['selectedWorkorder'],
  components: {
    Attachments,
  },
  computed: {
    noOfAttachments() {
      let { noOfAttachments } = this.selectedWorkorder || {}
      return noOfAttachments > 0 ? `( ${noOfAttachments} )` : ''
    },
  },
  methods: {
    close() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss" scoped>
.el-drawer-header {
  top: 50px;
  position: fixed;
}
.el-drawer-body {
  height: 100%;
  display: flex;
  flex-direction: column;
}
</style>
