<template>
  <div class="export-module pointer">
    <el-dropdown
      trigger="click"
      style="position: relative;font-size:17px;"
      @command="handleOptions"
      width="180px"
      :tabindex="-1"
    >
      <fc-icon group="action" name="export" size="18"></fc-icon>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item command="1" class="export-dropdown-menu">
          <div class="flLeft">
            <img class="f-export-icon" src="~statics/report/table-black.svg" />
          </div>
          <div class="pL10">{{ $t('common.wo_report.export_csv') }}</div>
        </el-dropdown-item>
        <el-dropdown-item command="2" class="export-dropdown-menu">
          <div class="flLeft">
            <img class="f-export-icon" src="~statics/report/table-black.svg" />
          </div>
          <div class="pL10">{{ $t('common.wo_report.export_xcl') }}</div>
        </el-dropdown-item>
        <el-dropdown-item
          command="mail"
          class="export-dropdown-menu"
          v-if="showMail"
        >
          <div class="flLeft">
            <img class="f-export-icon" src="~statics/report/email.svg" />
          </div>
          <div class="pL10">{{ $t('common._common.mail') }}</div>
        </el-dropdown-item>
        <div v-if="showViewScheduler" @click="showScheduleDialog = true">
          <el-dropdown-item class="export-dropdown-menu">
            <div class="flLeft">
              <img class="f-export-icon" src="~statics/report/calendar.svg" />
            </div>
            <div class="pL10">
              {{ $t('common._common.schedule') }}
            </div>
          </el-dropdown-item>
        </div>
      </el-dropdown-menu>
    </el-dropdown>
    <iframe
      v-if="exportDownloadUrl"
      :src="exportDownloadUrl"
      style="display: none;"
    ></iframe>
    <mail-module
      v-if="showEmailDialog"
      :module="module"
      :viewDetail="viewDetail"
      :viewName="viewDetail.name"
      :filters="filters"
      :visibility.sync="showEmailDialog"
    ></mail-module>
    <f-dialog
      v-if="showCommentsDialog"
      :visible.sync="showCommentsDialog"
      width="30%"
      @save="exportModuleList(commands)"
      @close="showCommentsDialog = false"
      confirmTitle="Export"
      customClass="qr-dialog"
    >
      <div class="fc-setup-modal-title fL">Export Workorders</div>
      <div style="padding: 25px 0px;">
        <el-checkbox
          class="fc-radio-btn pT15"
          color="secondary"
          v-model="checked"
          >Include Comments</el-checkbox
        >
        <!-- <el-radio class="fc-radio-btn pT15" color="secondary" v-model="qrParam" label="all" >{{'All assets (' + listcount + ')'}}</el-radio> -->
      </div>
    </f-dialog>
    <div v-if="showScheduleDialog">
      <NewScheduleReport
        :showScheduleDialog.sync="showScheduleDialog"
        :moduleName="module"
        :viewDisplayName="viewDetail.displayName"
        :viewName="viewDetail.name"
        :viewId="viewDetail.id"
        :exportFromView="true"
        @close="showScheduleDialog = false"
      ></NewScheduleReport>
    </div>
  </div>
</template>
<script>
import MailModule from '@/FMailModule'
import FDialog from '@/FDialogNew'
import NewScheduleReport from 'pages/report/forms/NewScheduleView'

export default {
  props: ['module', 'viewDetail', 'showMail', 'filters', 'showViewScheduler'],
  components: {
    MailModule,
    FDialog,
    NewScheduleReport,
  },
  data() {
    return {
      exportDownloadUrl: null,
      showEmailDialog: false,
      showCommentsDialog: false,
      commands: '',
      checked: false,
      showScheduleDialog: false,
    }
  },
  methods: {
    handleOptions(command) {
      if (this.module === 'workorder') {
        if (command === '1' || command === '2') {
          this.showCommentsDialog = true
          this.commands = command
        } else if (command === 'mail') {
          this.showEmailDialog = true
        }
      } else if (command === '1' || command === '2') {
        this.exportModuleList(command)
      } else if (command === 'mail') {
        this.showEmailDialog = true
      }
    },
    exportModuleList(type) {
      let params = null
      let url
      url =
        '/exportModule?type=' +
        type +
        '&moduleName=' +
        this.module +
        '&viewName=' +
        this.viewDetail.name +
        '&specialFields=' +
        this.checked
      if (this.filters) {
        params = '&filters=' + JSON.stringify(this.filters)
      }
      this.$message({
        showClose: true,
        message: 'Downloading...',
        type: 'success',
      })
      this.$http.post(url, params).then(response => {
        if (response.data && typeof response.data === 'object') {
          this.exportDownloadUrl = response.data.fileUrl
        } else {
          this.$error.message('Export failed')
        }
      })
      this.showCommentsDialog = false
      this.commands = ''
      this.checked = false
    },
  },
}
</script>
<style lang="scss">
.export-module,
.fc-list-view-filter .export-module div {
  padding-right: 0px;
  height: 18px;
}

.f-export-icon {
  width: 14px;
  height: 14px;
  z-index: 2000;
  position: relative;
  display: block;
}

/* .fc-list-view-filter .export-module > div {
  padding-right: 15px;
} */
.export-settings-icon {
  font-size: 17px;
}
.qr-dialog .el-radio + .el-radio {
  margin-left: 0px;
}
</style>
