<template>
  <div class="layout-padding">
    <div
      v-if="
        $account.org.id === 336 ||
          $account.org.id === 337 ||
          $account.org.id === 155
      "
      class="fc-form"
    >
      <div class="form-header">
        <div class="new-body-modal" style="padding-bottom: 20px;">
          <div id="container" style="width:100%;display:inline-block;">
            <el-row class="flex-middle">
              <el-col :span="3">
                <div class="label-txt-black">
                  Site Name
                </div>
              </el-col>
              <el-col :span="8" class="">
                <el-input
                  class="fc-input-full-border2 width100"
                  v-model="siteName"
                  type="text"
                  autoComplete="off"
                  :autofocus="true"
                  placeholder="Enter Site Name"
                />
              </el-col>

              <el-col class="pL40" :span="3">
                <div class="label-txt-black">
                  Building Name
                </div>
              </el-col>
              <el-col class="pL40" :span="10">
                <el-input
                  class="fc-input-full-border2 width100"
                  v-model="buildingName"
                  type="text"
                  autoComplete="off"
                  :autofocus="true"
                  placeholder="Enter Building Name"
                />
              </el-col>
            </el-row>

            <el-row class="pT20 flex-middle">
              <el-col :span="3">
                <div class="label-txt-black">
                  URL
                </div>
              </el-col>
              <el-col :span="21" class="">
                <el-input
                  class="fc-input-full-border2 width100"
                  v-model="assetURL"
                  type="text"
                  autoComplete="off"
                  :autofocus="true"
                  placeholder="Enter Url"
                />
              </el-col>
            </el-row>

            <el-row class="pT20 flex-middle">
              <el-col :span="3">
                <div class="label-txt-black">
                  User Name
                </div>
              </el-col>
              <el-col :span="8" class="">
                <el-input
                  class="fc-input-full-border2 width100"
                  v-model="userName"
                  type="text"
                  autoComplete="off"
                  :autofocus="true"
                  placeholder="Enter User Name"
                />
              </el-col>

              <el-col class="pL40" :span="3">
                <div class="label-txt-black">
                  Password
                </div>
              </el-col>
              <el-col class="pL40" :span="10">
                <el-input
                  class="fc-input-full-border2 width100"
                  v-model="password"
                  type="password"
                  autoComplete="off"
                  :autofocus="true"
                  placeholder="Enter Password"
                />
              </el-col>
            </el-row>

            <el-row class="pR80 pT20 flex-middle">
              <el-col :span="17" class="">
                <el-button
                  class="modal-btn-save"
                  type="primary"
                  :loading="loading"
                  @click="importBimForThirdParty"
                  :disabled="disableImportBtn"
                  >{{ loading ? 'IMPORTING' : 'IMPORT BIM' }}
                </el-button>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="fc-form">
      <div class="fc-form-title setting-form-title">
        SITE DETAILS
      </div>
      <el-form :model="site" :label-position="'top'" ref="ruleForm">
        <div class="form-header">
          <div class="new-body-modal" style="padding-bottom: 20px;">
            <div id="container" style="width:100%;display:inline-block;">
              <el-row align="middle" style="margin:0px;" :gutter="50">
                <el-col style="padding-left:0px;" :span="24">
                  <div class="fc-input-label-txt">
                    {{ $t('space.sites.name') }}
                  </div>
                  <div class="form-input">
                    <el-form-item prop="name">
                      <el-input
                        class="text required header fc-input-full-border2"
                        v-model="site.name"
                        type="text"
                        autoComplete="off"
                        :autofocus="true"
                        :placeholder="$t('space.sites.enter_site_name')"
                      />
                    </el-form-item>
                  </div>
                </el-col>
              </el-row>
              <el-row
                align="middle"
                style="margin:0px;padding-bottom: 30px;"
                :gutter="50"
              >
                <el-col style="padding-left:0px;" :span="24">
                  <div class="fc-input-label-txt">
                    {{ $t('space.sites.site_description') }}
                  </div>
                  <div class="form-input">
                    <el-input
                      v-model="site.description"
                      autoComplete="off"
                      type="textarea"
                      :autosize="{ minRows: 3, maxRows: 4 }"
                      class="fc-input-txt fc-input-full-border-textarea"
                      resize="none"
                      :placeholder="$t('common._common.enter_desc')"
                    ></el-input>
                  </div>
                </el-col>
              </el-row>
              <el-row> </el-row>

              <el-row
                align="middle"
                style="margin:0px;padding-bottom: 10px;"
                :gutter="50"
              >
                <el-col
                  style="padding-left:0px;padding-right: 20px;"
                  :span="12"
                >
                  <div class="fc-input-label-txt">
                    {{ $t('space.sites.site_location') }}
                  </div>
                  <el-input
                    style="padding-left: 0px;"
                    class="fc-input-full-border2"
                    v-model="site.location.lat"
                    autoComplete="off"
                    v-tippy
                    data-arrow="true"
                    :title="$t('space.sites.site_lat')"
                    :placeholder="$t('space.sites.site_lat')"
                  ></el-input>
                </el-col>
                <el-col
                  style="padding-left:0px;padding-right: 20px;"
                  :span="12"
                >
                  <div class="fc-input-label-txt"></div>
                  <el-input
                    style="padding-left: 0px;padding-right: 0px;padding-top: 16px;"
                    class="fc-input-full-border2"
                    v-model="site.location.lng"
                    autoComplete="off"
                    v-tippy
                    data-arrow="true"
                    :title="$t('space.sites.site_long')"
                    :placeholder="$t('space.sites.site_long')"
                  ></el-input>
                  <span class="mapMarker"
                    ><i
                      @click="dialogVisible = true"
                      :title="$t('space.sites.pick_location')"
                      data-arrow="true"
                      v-tippy
                      class="fa fa-map-marker fa-lg"
                      aria-hidden="true"
                    ></i
                  ></span>
                </el-col>
              </el-row>

              <el-row
                align="middle"
                style="margin:0px;padding-bottom: 30px;"
                :gutter="50"
              >
                <el-col style="padding-left:0px;padding-right:20px;" :span="12">
                  <div class="fc-input-label-txt">
                    {{ $t('space.sites.managed_by') }}
                  </div>
                  <div class="form-input">
                    <el-select
                      v-model="site.managedBy.id"
                      filterable
                      clearable
                      :placeholder="$t('space.sites.select_assignee')"
                      style="width: 100%;"
                    >
                      <el-option
                        v-for="user in users"
                        :key="user.id"
                        :label="user.name"
                        :value="parseInt(user.id)"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
                <el-col style="padding-left:0px;padding-right:20px;" :span="12">
                  <div class="fc-input-label-txt">
                    {{ $t('space.sites._site_type') }}
                  </div>
                  <div class="form-input">
                    <el-select
                      v-model="site.siteType"
                      filterable
                      clearable
                      :placeholder="$t('space.sites.select_type')"
                      style="width: 100%;"
                    >
                      <el-option
                        v-for="(label, value) in siteType"
                        :key="value"
                        :label="label"
                        :value="parseInt(value)"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
              </el-row>

              <div class="import-file-body border-bottom6">
                <div v-if="loading">
                  <div
                    class="flex-middle flex-col justify-content-center height300"
                  >
                    <div>
                      <spinner show="true" size="90"></spinner>
                    </div>
                  </div>
                </div>

                <div v-else-if="importRen">
                  <div v-if="importingSheets && importingSheets.length > 0">
                    <table class=" pT15 fc-list-view-table border-right-table">
                      <thead>
                        <tr
                          class="tablerow pT30"
                          style="background-color:#ffffff;padding:15px"
                        >
                          <td class="headerrow width30">INFORMATION</td>
                          <td class="headerrow width30">COBIE SHEET</td>
                          <td class="headerrow ">STATUS</td>
                        </tr>
                      </thead>

                      <tbody>
                        <tr
                          v-for="sheet in importingSheets"
                          :key="sheet.moduleName"
                          class="tablerow"
                        >
                          <td>
                            {{ sheet.moduleName }}
                          </td>
                          <td>
                            {{ sheet.sheetName }}
                          </td>
                          <td class="contentrow">
                            <img
                              v-if="sheet.status == 1"
                              class="svg-icon"
                              src="~assets/bim/progress.svg"
                            />
                            <img
                              v-else-if="sheet.status == 2"
                              class="svg-icon"
                              src="~assets/bim/success.svg"
                            />
                            <img
                              v-else
                              class="svg-icon"
                              src="~assets/bim/failed.svg"
                            />
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div v-else>
                    <table class=" pT15 fc-list-view-table border-right-table">
                      <thead>
                        <tr
                          class="tablerow pT30"
                          style="background-color:#ffffff;padding:15px"
                        >
                          <td class="headerrow ">
                            <label class="form-checkbox">
                              <input
                                type="checkbox"
                                v-model="selectAll"
                                @click="onSelectAllChange"
                              />
                              <i class="form-icon"></i>
                            </label>
                          </td>
                          <td class="headerrow width30">INFORMATION</td>
                          <td class="headerrow width30">COBIE SHEET</td>
                        </tr>
                      </thead>

                      <tbody>
                        <tr
                          v-if="validSheets && validSheets.length > 0"
                          v-for="sheet in validSheets"
                          :key="sheet.sheetName"
                          class="tablerow"
                        >
                          <td class="contentrow">
                            <label class="form-checkbox">
                              <input
                                type="checkbox"
                                :value="sheet.sheetName"
                                v-model="selectedSheetNames"
                              />
                              <i class="form-icon"></i>
                            </label>
                          </td>
                          <td>
                            {{ sheet.moduleName }}
                          </td>
                          <td>
                            {{ sheet.sheetName }}
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
                <div v-else>
                  <div
                    v-if="attachments && attachments.length > 0"
                    class="pT40 pB40"
                  >
                    <el-row
                      v-for="attachment in attachments"
                      :key="attachment.attachmentId"
                    >
                      <el-col :span="20">
                        <el-col :span="1" class>
                          <InlineSvg
                            src="svgs/fileupload-xls"
                            iconClass="icon icon-xlg vertical-middle"
                          ></InlineSvg>
                        </el-col>
                        <el-col :span="19">
                          <div class="pL20">
                            <div
                              class="this-center show text-left fc-black-16 textoverflow-ellipsis max-width300px"
                            >
                              {{ attachment.fileName }}
                            </div>
                            <div class="fc-grey3-text14">
                              {{ attachment.fileSize | prettyBytes }}
                            </div>
                          </div>
                        </el-col>
                      </el-col>
                      <el-col :span="4" class="text-right">
                        <div
                          class="fc-warning2-12 pointer"
                          @click="deleteAttachment()"
                        >
                          Remove
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                  <form
                    v-else
                    enctype="multipart/form-data"
                    novalidate
                    class="pT20 pB40"
                  >
                    <div class="dropbox">
                      <input
                        type="file"
                        accept=".xls, .xlsx"
                        class="input-file z-10"
                        @change="filesChange($event.target.files[0])"
                      />
                      <div class="upload-parent">
                        <div class="upload-folder-box">
                          <InlineSvg
                            src="svgs/folder-open"
                            iconClass="icon z-index1 icon-xxxl vertical-middle"
                          ></InlineSvg>
                        </div>
                      </div>
                      <p>
                        {{ $t('common.attachment_form.drag_and_drop_files') }}
                        <br />
                        {{ $t('common.attachment_form.click_to_browse') }}
                      </p>
                    </div>
                  </form>
                </div>
              </div>

              <div>
                <el-button
                  v-if="importRen"
                  class="modal-btn-save"
                  type="primary"
                  :loading="loading"
                  @click="importBim()"
                  :disabled="disableImportBtn"
                  >{{ loading ? 'IMPORTING' : 'IMPORT BIM DETAILS' }}
                </el-button>
                <el-button
                  v-else
                  class="modal-btn-save"
                  type="primary"
                  @click="saveAndUpload()"
                  :disabled="disableUploadBtn"
                >
                  SAVE AND UPLOAD
                  <span slot="loading"> UPLOADING </span>
                </el-button>
                <el-button
                  style="margin-left: 0px;"
                  class="modal-btn-cancel"
                  @click="dialogVisible = false"
                  >{{ $t('space.sites.site_cancel') }}</el-button
                >
              </div>
              <FLocationPickerDialog
                :model.sync="site.location"
                :dialogVisibility.sync="dialogVisible"
              ></FLocationPickerDialog>
            </div>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import FLocationPickerDialog from '@/FLocationPickerDialog'
import ImportHelper from 'src/pages/assets/import/v1/ImportHelper'
import { prettyBytes } from '@facilio/utils/filters'

export default {
  mixins: [ImportHelper],
  data() {
    return {
      disableImportBtn: false,
      disableUploadBtn: true,
      loading: false,
      selectAll: false,
      flocationData: null,
      dialogVisible: false,
      uploadFile: null,
      fileName: null,
      attachments: [],
      submit: false,
      uploadurl: '',
      siteType: {
        1: this.$t('setup.bim.common'),
        3: this.$t('setup.bim.residential'),
        4: this.$t('setup.bim.office'),
        5: this.$t('setup.bim.commercial'),
        6: this.$t('setup.bim.compound'),
        7: this.$t('setup.bim.university'),
        8: this.$t('setup.bim.retail'),
        9: this.$t('setup.bim.residentialNcommercial'),
      },
      site: {
        name: '',
        description: '',
        location: {
          lat: null,
          lng: null,
        },
        siteType: null,
        managedBy: {
          id: null,
        },
      },
      bimImportId: null,
      validSheets: null,
      importingSheets: null,
      error: null,
      importRen: false,
      interval: null,
      bimIntegration: null,
      selectedSheetNames: [],
      assetURL: null,
      userName: null,
      password: null,
      siteName: null,
      buildingName: null,
    }
  },
  destroyed() {
    clearInterval(this.interval)
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },
  components: {
    FLocationPickerDialog,
  },
  methods: {
    onSelectAllChange() {
      this.selectedSheetNames = []
      if (!this.selectAll) {
        for (let i in this.validSheets) {
          this.selectedSheetNames.push(this.validSheets[i].sheetName)
        }
      }
    },
    selectLocation() {
      if (this.flocationData) {
        this.site.location.lat = this.flocationData.lat
        this.site.location.lng = this.flocationData.lng
        this.dialogVisible = false
      }
    },
    getData(data) {
      if (data) {
        this.flocationData = data
      }
    },
    filesChange(selectedFile) {
      this.fileName = selectedFile.name
      this.uploadFile = selectedFile
      let fileEntry = {
        attachmentId: -1,
        fileName: selectedFile.name,
        fileSize: selectedFile.size,
        prettyFileSize: prettyBytes(selectedFile.size),
        contentType: selectedFile.type,
        status: null,
        error: null,
      }
      this.attachments.push(fileEntry)
      this.addAttachment()
    },
    importBimForThirdParty() {
      const formData = new FormData()

      if (this.$account.org.orgId === 336) {
        formData.append('thirdParty', 'INVICARA')
      } else if (
        this.$account.org.orgId === 337 ||
        this.$account.org.id === 155
      ) {
        formData.append('thirdParty', 'YOUBIM')
      }
      formData.append('siteName', this.siteName)
      formData.append('buildingName', this.buildingName)
      formData.append('assetURL', this.assetURL)
      formData.append('userName', this.userName)
      formData.append('password', this.password)
      this.loading = true
      this.$http
        .post('/v2/bimIntegration/bimJsonImport', formData)
        .then(response => {
          if (response.data.responseCode !== 0) {
            this.$message.error(response.data.message)
          } else {
            this.$message.success('Bim imported Successfully')
            this.disableImportBtn = true
          }
          this.loading = false
        })
        .catch(error => {
          this.error = error
          this.loading = false
        })
    },
    addAttachment() {
      const formData = new FormData()
      formData.append('fileUpload', this.uploadFile)
      formData.append('fileUploadContentType', this.uploadFile.type)
      formData.append('fileUploadFileName', this.fileName)
      this.loading = true
      this.$http
        .post('/v2/bimIntegration/uploadBim', formData)
        .then(response => {
          if (response.data) {
            this.loading = false
            if (response.data.responseCode !== 0) {
              this.error = response.data.message
            } else {
              this.bimImportId = response.data.result.bimImportId
              this.validSheets = response.data.result.validSheets
              for (let i in this.validSheets) {
                this.validSheets[i].selected = false
              }
              this.disableUploadBtn = false
            }
          }
        })
        .catch(error => {
          this.error = error
          this.loading = false
        })
    },
    deleteAttachment() {
      this.attachments.splice(0, 1)
      this.bimImportId = null
      this.validSheets = null
      this.disableUploadBtn = true
    },
    saveAndUpload() {
      if (this.bimImportId) {
        this.importRen = true
        this.$message.success('File Uploaded Successfully')
      } else if (this.error) {
        this.$message.error(error)
        this.deleteAttachment()
      }
    },
    checkStatus() {
      const formData = new FormData()
      formData.append('bimImportId', this.bimImportId)
      this.$http
        .post('/v2/bimIntegration/checkStatus', formData)
        .then(response => {
          if (response.data) {
            if (response.data.responseCode !== 0) {
              this.$message.error(response.data.message)
              clearInterval(this.interval)
            } else {
              let lastModuleStatus = 1
              let anyFail = false
              let bimImportProcessList =
                response.data.result.bimImportProcessList
              for (let i in this.importingSheets) {
                let module = this.importingSheets[i].moduleName
                this.importingSheets[i].status = bimImportProcessList.find(
                  m => m.moduleName === module
                ).status
                if (this.importingSheets[i].status === 3) {
                  anyFail = true
                }
                lastModuleStatus = this.importingSheets[i].status
              }

              if (lastModuleStatus === 2) {
                this.$message.success('BIM imported Successfully')
                clearInterval(this.interval)
              } else if (anyFail) {
                this.$message.error('Error in BIM import')
                clearInterval(this.interval)
              }
            }
          }
        })
        .catch(error => {
          this.$message.error(response.data.message)
          clearInterval(this.interval)
        })
    },
    importBim() {
      if (this.selectedSheetNames.length > 0) {
        let site = this.$helpers.cloneObject(this.site)
        if (site.location.lat === null) {
          site.location.lat = -1
        }
        if (site.location.lng === null) {
          site.location.lng = -1
        }
        if (site.managedBy.id === '') site.managedBy.id = -99

        let formData = {
          bimImportId: this.bimImportId,
          selectedSheetNames: this.selectedSheetNames,
          site: site,
        }
        this.disableImportBtn = true
        this.loading = true
        this.$http
          .post('/v2/bimIntegration/importBim', formData)
          .then(response => {
            if (response.data) {
              if (response.data.responseCode !== 0) {
                this.$message.error(response.data.message)
                this.loading = false
              } else {
                this.$message.success('BIM import scheduled Successfully')
                this.importingSheets = []
                for (let i in this.validSheets) {
                  if (
                    this.selectedSheetNames.indexOf(
                      this.validSheets[i].sheetName
                    ) > -1
                  ) {
                    let entry = this.validSheets[i]
                    let isMultipleModules = entry.moduleName.includes(' , ')
                    if (!isMultipleModules) {
                      entry.status = 1
                      this.importingSheets.push(entry)
                    } else {
                      let modules = entry.moduleName.split(' , ')
                      for (let j in modules) {
                        let en = {}
                        en.status = 1
                        en.sheetName = entry.sheetName
                        en.moduleName = modules[j]
                        this.importingSheets.push(en)
                      }
                    }
                  }
                }
                this.interval = setInterval(this.checkStatus, 40000)
                this.loading = false
              }
            }
          })
          .catch(error => {
            this.error = error
            this.loading = false
          })
      } else {
        this.$message.error('Please select Sheet')
      }
    },
  },
}
</script>
<style>
.layout-padding {
  padding: 1.5rem 2.4rem 40px !important;
}
</style>
