<template>
  <div>
    <el-dialog
      v-if="visibility"
      :append-to-body="true"
      :title="title"
      :visible.sync="visibility"
      width="28%"
      :before-close="closeDialog"
      custom-class="fc-dialog-center-container scale-up-center"
    >
      <div class="height300">
        <el-checkbox v-model="checked">{{
          $t('common.wo_report.show_photo')
        }}</el-checkbox>
        <el-checkbox v-model="showAttachement" class="pT20">
          {{ $t('common.wo_report.show_attachement') }}
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.wo_report.prints_image_type')"
            placement="bottom"
          >
            <i class="el-icon-info capp-linkname"></i>
          </el-tooltip>
        </el-checkbox>
        <el-checkbox v-model="showComments" class="pT20">
          {{ $t('common.wo_report.show_comments') }}
        </el-checkbox>
        <el-checkbox v-model="showHistory" class="pT20 mR100">
          {{ $t('common.wo_report.show_history') }}
        </el-checkbox>
        <el-checkbox v-model="useSiteLogo" class="pT20">
          {{ $t('common.wo_report.use_site_logo') }}
        </el-checkbox>
        <el-checkbox v-model="showSignature" class="pT20">
          {{ $t('common.wo_report.show_signature') }}
        </el-checkbox>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          :loading="disableDownloadButton"
          @click="isDownloadOption ? downloadWorkOrder() : printWorkOrder()"
          >{{ confirmButtonText }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { getApp, isWebTabsEnabled } from '@facilio/router'
import { mapState } from 'vuex'

export default {
  //props: ['visibility', 'workorders', 'viewName', 'isDownloadOption', 'pdfUrl'],
  props: {
    visibility: {
      type: Boolean,
      default: false,
    },
    workorders: {
      type: Array,
      default: () => [],
    },
    viewName: {
      type: String,
      default: '',
    },
    isDownloadOption: {
      type: Boolean,
      default: false,
    },
    pdfUrl: {
      type: String,
      default: '',
    },
    isPdfDownload: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      checked: false,
      showAttachement: false,
      showComments: false,
      showHistory: false,
      useSiteLogo: false,
      showSignature: true,
    }
  },
  computed: {
    ...mapState({
      currentTab: state => state.webtabs.selectedTab,
      currentGroup: state => state.webtabs.selectedTabGroup,
    }),
    selected() {
      return this.data.checked
    },
    title() {
      let { isDownloadOption } = this
      if (isDownloadOption) {
        return this.$t('common.wo_report.download_options')
      } else {
        return this.$t('common.wo_report.print_options')
      }
    },
    confirmButtonText() {
      let { isDownloadOption } = this
      if (isDownloadOption) {
        return this.$t('common.wo_report.download')
      } else {
        return this.$t('common.wo_report.print')
      }
    },
    disableDownloadButton() {
      let { isDownloadOption, isPdfDownload } = this
      return isDownloadOption && isPdfDownload
    },
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    getFileUrl() {
      let {
        workorders,
        checked,
        showAttachement,
        showComments,
        showHistory,
        useSiteLogo,
        showSignature,
        viewName,
      } = this
      let { linkName } = getApp()
      let appName = linkName === 'newapp' ? 'app' : linkName
      let query = new URLSearchParams()

      query.append('workorderId', workorders)
      query.append('showImage', checked)
      query.append('showAttachements', showAttachement)
      query.append('showComments', showComments)
      query.append('showHistory', showHistory)
      query.append('useSiteLogo', useSiteLogo)
      query.append('showSignature', showSignature)
      query.append('viewName', viewName)

      if (isWebTabsEnabled()) {
        let { currentTab, currentGroup } = this
        query.append('currentTabId', currentTab.id)
        query.append('currentGroupId', currentGroup.id)
      }

      let url = `${
        window.location.origin
      }/${appName}/pdf/summarydownloadreport?${query.toString()}`
      return url
    },
    printWorkOrder() {
      let { getFileUrl } = this
      let url = getFileUrl()
      window.open(url)
    },
    downloadWorkOrder() {
      let { getFileUrl } = this
      let url = getFileUrl()
      this.$emit('update:pdfUrl', url)
    },
  },
}
</script>
<style scsslang="" scoped>
.el-checkbox {
  margin-right: 50% !important;
}
</style>
