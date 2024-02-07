<template>
  <div class="height100 width100">
    <div class="tools-dataloader-download pL0 pR0">
      <div class="col-lg-12 col-md-12">
        <div class="visitor-setting-con-width mT10">
          <el-row class="flex-middle">
            <el-col :span="12" class="dataloader-download-card flex-middle">
              <el-row :gutter="24" class="flex-middle dataloader-app-card">
                <el-col :span="3">
                  <inline-svg
                    src="svgs/windows_logo"
                    class="vertical-middle"
                    iconClass="icon icon-xxllg"
                  ></inline-svg>
                </el-col>
                <el-col :span="16" class="dataloader-app-image">
                  <div class="fc-black-15 fwBold">
                    {{ $t('setup.dataloader.windows64') }}
                  </div>
                  <div class="fc-grey4-13 pT5">
                    {{ $t('setup.dataloader.download_windows64') }}
                  </div>
                </el-col>
                <el-col :span="5" class="text-right p0">
                  <el-button
                    type="primary"
                    @click="downloadApp('windows64')"
                    class="setup-el-btn dataloader-float"
                  >
                    {{ 'Download' }}
                  </el-button>
                </el-col>
              </el-row>
            </el-col>
            <el-col :span="12" class="dataloader-download-card flex-middle">
              <el-row :gutter="24" class="flex-middle dataloader-app-card">
                <el-col :span="3">
                  <inline-svg
                    src="svgs/windows_logo"
                    class="vertical-middle"
                    iconClass="icon icon-xxllg"
                  ></inline-svg>
                </el-col>
                <el-col :span="16" class="dataloader-app-image">
                  <div class="fc-black-15 fwBold">
                    {{ $t('setup.dataloader.windows32') }}
                  </div>
                  <div class="fc-grey4-13 pT5">
                    {{ $t('setup.dataloader.download_windows32') }}
                  </div>
                </el-col>
                <el-col :span="5" class="text-right p0">
                  <el-button
                    type="primary"
                    @click="downloadApp('windows32')"
                    class="setup-el-btn dataloader-float"
                  >
                    {{ 'Download' }}
                  </el-button>
                </el-col>
              </el-row>
            </el-col>
          </el-row>
          <el-row class="flex-middle">
            <el-col :span="12" class="dataloader-download-card flex-middle">
              <el-row :gutter="24" class="flex-middle dataloader-app-card">
                <el-col :span="3">
                  <img
                    src="~assets/svgs/linux_logo.png"
                    width="40"
                    height="46"
                  />
                </el-col>
                <el-col :span="16" class="dataloader-app-image">
                  <div class="fc-black-15 fwBold">
                    {{ $t('setup.dataloader.linux') }}
                  </div>
                  <div class="fc-grey4-13 pT5">
                    {{ $t('setup.dataloader.download_linux') }}
                  </div>
                </el-col>
                <el-col :span="5" class="text-right p0">
                  <el-button
                    type="primary"
                    @click="downloadApp('linux')"
                    class="setup-el-btn dataloader-float"
                  >
                    {{ 'Download' }}
                  </el-button>
                </el-col>
              </el-row>
            </el-col>

            <el-col :span="12" class="dataloader-download-card flex-middle">
              <el-row :gutter="24" class="flex-middle dataloader-app-card">
                <el-col :span="3">
                  <inline-svg
                    src="svgs/apple_logo"
                    class="vertical-middle"
                    iconClass="icon icon-xxllg"
                  ></inline-svg>
                </el-col>
                <el-col :span="16" class="dataloader-app-image">
                  <div class="fc-black-15 fwBold">
                    {{ $t('setup.dataloader.macos') }}
                  </div>
                  <div class="fc-grey4-13 pT5">
                    {{ $t('setup.dataloader.download_mac') }}
                  </div>
                </el-col>
                <el-col :span="5" class="text-right p0">
                  <el-button
                    type="primary"
                    @click="downloadApp('mac')"
                    class="setup-el-btn dataloader-float"
                  >
                    {{ 'Download' }}
                  </el-button>
                </el-col>
              </el-row>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: 'Data Loader App List',
  title() {
    return 'Data Loader App List'
  },

  data() {
    return {
      loading: true,
      rolelist: [],
      isNew: false,
      role: {},
      showWebTabRolePermission: false,
      showRoleDetails: false,
    }
  },

  methods: {
    isStage() {
      return window.location.host.toLowerCase().indexOf('stage.facilio.in') > -1
        ? true
        : false
    },
    downloadApp(type) {
      if (this.isStage()) {
        this.downloadStage(type)
      } else {
        this.downloadProduction(type)
      }
    },
    downloadStage(type) {
      let url = null
      if (type === 'windows64') {
        url =
          'https://static.facilio.com/downloads/dataloader/stage/FacilioDataLoader-windows_x64-latest.exe'
      } else if (type === 'windows32') {
        url =
          'https://static.facilio.com/downloads/dataloader/stage/FacilioDataLoader-windows_x32-latest.exe'
      } else if (type === 'mac') {
        url =
          'https://static.facilio.com/downloads/dataloader/stage/FacilioDataLoader-mac-latest.dmg'
      } else if (type === 'linux') {
        url =
          'https://static.facilio.com/downloads/dataloader/stage/FacilioDataLoader-linux-latest.AppImage'
      }
      window.open(url)
    },
    downloadProduction(type) {
      let url = null
      if (type === 'windows64') {
        url =
          'https://static.facilio.com/downloads/dataloader/FacilioDataLoader-windows_x64-latest.exe'
      } else if (type === 'windows32') {
        url =
          'https://static.facilio.com/downloads/dataloader/FacilioDataLoader-windows_x32-latest.exe'
      } else if (type === 'mac') {
        url =
          'https://static.facilio.com/downloads/dataloader/FacilioDataLoader-mac-latest.dmg'
      } else if (type === 'linux') {
        url =
          'https://static.facilio.com/downloads/dataloader/FacilioDataLoader-linux-latest.AppImage'
      }
      window.open(url)
    },
  },
}
</script>
<style scoped lang="scss">
.dataloader-app-card {
  align-items: center;
  display: flex;
  width: 100%;
}
.dataloader-float {
  float: right;
}
.dataloader-app-image {
  padding-left: 15px !important;
}
</style>
