<template>
  <div class="jp-banner-container">
    <div :class="['sidebar-loader', isLoading && 'on']"></div>

    <div v-if="isLoading" class="d-flex jp-banner">
      {{ $t('panel.loading_load') }}
    </div>

    <div v-else class="d-flex jp-banner">
      <i class="fa fa-exclamation-triangle pR5 pL10" aria-hidden="true"></i>
      <div class="survey-bar-helper mR20 pL5">
        {{ bannerText }}
      </div>
      <el-button class="jp-bnnr-btn" @click="openWarningDialog">
        {{ buttonText }}
      </el-button>
    </div>
    <JPWarningDialog
      v-if="showDialog"
      :showDialog="showDialog"
      :dialogType="$t('jobplan.publish')"
      :moduleName="moduleName"
      :stateUpdating="stateUpdating"
      @closeDialog="closeDialog"
      @saveAction="saveAction"
    />
  </div>
</template>
<script>
import JPWarningDialog from './JPWarningDialog'
import { PUBLISHED_STATUS } from 'pages/workorder/pm/create/utils/pm-utils.js'

export default {
  name: 'JPBanner',
  props: ['record', 'moduleName', 'isLoading', 'stateUpdating'],
  components: { JPWarningDialog },
  data: () => ({
    showDialog: false,
    isDisableDialog: false,
  }),
  computed: {
    recordStatus() {
      let { record, moduleName } = this
      let publishStatus
      if (moduleName === 'jobplan') {
        publishStatus = this.$getProperty(record, 'jpStatus', 1)
      } else {
        publishStatus = this.$getProperty(record, 'pmStatus', 1)
      }
      return PUBLISHED_STATUS[publishStatus]
    },
    isRecordDisabled() {
      let { recordStatus } = this
      return recordStatus === 'Disabled'
    },
    bannerText() {
      let { moduleName, isRecordDisabled } = this
      let bannerText = ''

      if (moduleName === 'jobplan') {
        bannerText = isRecordDisabled
          ? this.$t('jobplan.clone_jp')
          : this.$t('jobplan.jobplan_not_published')
      } else {
        bannerText = isRecordDisabled
          ? this.$t('maintenance.pm.clone_pm')
          : this.$t('maintenance.pm.pm_not_published')
      }
      return bannerText
    },
    buttonText() {
      let { isRecordDisabled } = this
      return isRecordDisabled
        ? this.$t('jobplan.clone')
        : this.$t('jobplan.publish')
    },
  },
  methods: {
    openWarningDialog() {
      let { isRecordDisabled } = this
      if (isRecordDisabled) {
        this.$emit('saveAction', 'Clone')
      } else {
        this.showDialog = true
      }
    },
    closeDialog() {
      this.showDialog = false
    },
    saveAction(event) {
      this.$emit('saveAction', event)
    },
  },
}
</script>
<style lang="scss" scoped>
.jp-banner {
  height: 40px;
  background-color: #0078b9;
  padding: 20px;
  justify-content: center;
  align-items: center;
  border: 0.5px solid #0078b9;
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  .fa-exclamation-triangle {
    font-size: 16px;
  }
}

.sidebar-loader.on {
  background: repeating-linear-gradient(
    to right,
    #f6f7f8 0%,
    #0078b9 25%,
    #0078b9 50%,
    #0078b9 75%,
    #0078b9 100%
  );
  background-size: 200% auto;
  background-position: 0 100%;
  animation-timing-function: cubic-bezier(0.4, 0, 1, 1);
}
.jp-bnnr-btn {
  height: 24px;
  width: 75px;
  padding: 4px 12px;
  border-radius: 2px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background-color: rgba(255, 255, 255, 0.2);
  text-decoration: underline;
  color: #fff;

  &:hover,
  &:active,
  &:focus {
    background-color: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: #fff;
    text-decoration: underline;
  }
}
</style>
