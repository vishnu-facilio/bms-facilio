<template>
  <div class="save-pivot-report">
    <el-button
      class="pivot-save-cancel-btn"
      type="el-button save-btn el-button--primary"
      :disabled="disableSaveButton"
      :class="disableSaveButton ? 'disable-pointer': ''"
      @click="saveReport"
    >
      {{ $t('pivot.save') }}
    </el-button>
    <el-button
      class="pivot-save-cancel-btn pivot-cancel-btn"
      @click="cancelBtnHandler"
    >
      {{ $t('pivot.cancel') }}
    </el-button>
    <UnsavedChangesErrorDialogBox
      v-if="showUnsavedChangesDialogBox"
      :visibility.sync="showUnsavedChangesDialogBox"
      @result="resultHander"
    >
    </UnsavedChangesErrorDialogBox>
  </div>
</template>

<script>
import UnsavedChangesErrorDialogBox from './UnsavedChangesErrorDialogBox'
import saveReportMixin from './saveReportMixin.vue'
import {pageTypes, isWebTabsEnabled, findRouteForTab} from '@facilio/router'

export default {
  props: ['report', 'config'],
  components: {
    // SavePivotReportDialogBox,
    UnsavedChangesErrorDialogBox,
  },
  watch: {
    config: {
      handler: function() {
        this.unsavedChanges = true
      },
      deep: true,
    },
    report: {
      handler() {
        this.init()
      },
      deep: true,
      immediate: true,
    },
  },
  mounted() {
    console.log('rn : ', this.report.name)
  },
  data() {
    return {
      // showSaveDialogBox: false,
      showUnsavedChangesDialogBox: false,
      unsavedChanges: false,
    }
  },
  mixins: [saveReportMixin],
  methods: {
    cancelBtnHandler() {
      if (this.unsavedChanges) {
        this.showUnsavedChangesDialogBox = true
      } else {
        this.goBackToPreviousRoute()
      }
    },
    resultHander(res) {
      if (res) this.goBackToPreviousRoute()
    },
    goBackToPreviousRoute() {

      if(isWebTabsEnabled()){
        let {name} = findRouteForTab(pageTypes.PIVOT_VIEW)
        let url = this.$router.resolve({name }).href
        if (this.$route.query.reportId) {
          url = this.$router.resolve({name , params:{reportId: this.$route.query.reportId}}).href
        }
        this.$router.push(url)
      }
      else
      {
        if (this.$route.query.reportId) {
          this.$router.push(`/app/em/pivot/view/${this.$route.query.reportId}`)
        } else {
          this.$router.push('/app/em/pivot/view')
        }
    }
    },
    reportSaved(reportObj) {
      this.$emit('reportSaved', reportObj)
    },
  },
}
</script>

<style scoped lang="scss">
.save-pivot-report {
  height: 35px;
  .el-button {
    padding: 0px;
    font-weight: 500;
    letter-spacing: 0.5px;
    height: 35px;
    font-size: 13px;
  }
}
.disable-pointer{
    pointer-events: none;
}

.pivot-save-cancel-btn {
  width: 91px;
  height: 36px;
  padding-right: 10px;
  text-transform: capitalize;
  font-size: 13px !important;
}

.setup-el-btn {
  font-weight: 500;
  width: 91px !important;
  height: 36px !important;
  padding: 0px 10px !important;
}
.save-btn {
  background-color: #38b2c2 !important;
  border: none !important;
  font-size: 13px;
}

.save-pivot-report > button:not(.pivot-cancel-btn):hover {
  box-shadow: 0 1px 2px 0 rgb(57, 178, 194) !important;
}
.save-pivot-report > button:not(.save-btn):hover {
  border-color: #409EFF !important;
}
</style>
