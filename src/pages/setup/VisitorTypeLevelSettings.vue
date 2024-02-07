<template>
  <div>
    <div class="fc-setup-header" style="height: 125px;" v-if="!loading">
      <div class="flex-middle justify-content-space">
        <div class="">
          <div class="flex-middle visitor-back-btn pointer" @click="back">
            <i class="el-icon-back label-txt-black fwBold pR5"></i>
            Back
          </div>
          <div class="flex-middle pT10">
            <div class="setting-form-title flex-middle">
              {{ visitorTypeSettings.visitorType.name }}
              <div class="fc-newsummary-chip mL10">
                {{
                  visitorTypeSettings.visitorType.enabled
                    ? 'Enabled'
                    : 'Disabled'
                }}
              </div>
            </div>
          </div>
          <div class="heading-description">
            {{ visitorTypeSettings.visitorType.description }}
          </div>
        </div>
      </div>
      <!-- tabs -->
      <el-tabs v-model="activeName" class="vistor-setting-tab">
        <el-tab-pane label="Check-in Form" name="checkin">
          <VisitorCheckInFormEdit
            :formsList="formsList"
          ></VisitorCheckInFormEdit>
        </el-tab-pane>
        <el-tab-pane label="Legal Documents" name="nda">
          <visitor-legal
            :visitorTypeSettings="visitorTypeSettings"
          ></visitor-legal>
        </el-tab-pane>
        <el-tab-pane label="Host" name="host">
          <visitor-host
            :visitorTypeSettings="visitorTypeSettings"
          ></visitor-host>
        </el-tab-pane>
        <el-tab-pane label="Preferences" name="preference">
          <visitor-preference
            :visitorTypeSettings="visitorTypeSettings"
          ></visitor-preference>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>
<script>
import { getVisitorSetting } from 'src/devices/VisitorKiosk/VisitorApis'
import visitorLegal from 'src/pages/setup/VisitorLegalSettings'
import VisitorHost from 'src/pages/setup/VisitorHostsettings'
import VisitorPreference from 'src/pages/setup/VisitorPreference'
import VisitorCheckInFormEdit from 'src/pages/setup/VisitorCheckInFormEdit'
export default {
  created() {
    this.loadVistorTypeSettings()
  },
  data() {
    return {
      visitorTypeSettings: null,
      activeName: 'checkin',
      loading: true,
      formsList: [],
    }
  },
  components: {
    visitorLegal,
    VisitorHost,
    VisitorPreference,
    VisitorCheckInFormEdit,
  },
  methods: {
    loadVistorTypeSettings() {
      getVisitorSetting(this.$route.params.visitorTypeId, {
        visitorSettingPage: true,
      }).then(({ visitorSettings }) => {
        this.visitorTypeSettings = visitorSettings
        this.loading = false
        this.formsList.push(this.visitorTypeSettings.visitorLogForm)
      })
    },
    back() {
      window.history.go(-1)
    },
  },
}
</script>
<style lang="scss">
.vistor-setting-tab {
  margin-top: 9px;
  .el-tabs__nav-wrap::after {
    background-color: transparent !important;
  }
}
</style>
