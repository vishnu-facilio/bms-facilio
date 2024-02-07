<template>
  <div>
    <el-dialog
      :visible.sync="dialogVisible"
      width="35%"
      :append-to-body="true"
      :before-close="handleClose"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog slideInRight fc-animated"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ $t('asset.import.import_history') }}
          </div>
        </div>
      </div>
      <div class="flex-middle justify-end pT25 pR40">
        <Pagination
          v-if="showPagination"
          :hasMoreList="hasMoreList"
          :currentPage.sync="page"
          :perPage="perPage"
          :currentCount="currentListCount"
        ></Pagination>
      </div>

      <div v-if="loading" class="mT50 fc-webform-loading">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-else
        class="new-body-modal mT0 pT25"
        style="height: calc(100vh - 50px);"
      >
        <div v-if="!importHistoryList || importHistoryList.length === 0">
          <el-row>
            <el-col :span="24">
              <div class="attendance-transaction-no-data">
                <img src="~statics/noData-light.png" width="100" height="100" />
                <div class="mT10 label-txt-black f14">
                  No Import History Available
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="position-relative">
          <div
            v-for="(importContext, index) in importHistoryList"
            :key="index"
            class="asset-history-card mB20"
          >
            <div class="asset-history-card-header">
              <div class="fc__layout__align">
                <div class="label-txt-black word-break max-width170px fwBold">
                  {{ importContext.fileName }}
                </div>

                <el-dialog
                  :visible.sync="showDialog"
                  v-if="!showProceed(importContext.status)"
                  width="40%"
                  :before-close="closeDialog"
                  :append-to-body="true"
                  style="z-index: 9999999999;"
                >
                  <div class="new-header-container">
                    <div class="new-header-text">
                      <div class="fc-setup-modal-title">
                        <div
                          class="fc-pdf-black-13"
                          style="margin-top:-40px;margin-left:-25px"
                        >
                          <b>FIELD MAPPINGS</b>
                        </div>
                      </div>
                    </div>
                  </div>

                  <el-table
                    ref="table"
                    :data="historyData"
                    :border="true"
                    style="width: 100%"
                  >
                    <el-table-column prop="filefields" label="FIELDS IN FILE">
                      <template v-slot="history">
                        <div class="mL20">{{ history.row.filefields }}</div>
                      </template>
                    </el-table-column>
                    <el-table-column
                      prop="faciliofields"
                      label="FIELDS IN FACILIO"
                    >
                      <template v-slot="history">
                        <div class="mL30">{{ history.row.faciliofields }}</div>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-dialog>

                <div
                  :content="getImportMessage(importContext)"
                  v-tippy="{
                    placement: 'top',
                    arrow: true,
                    animation: 'shift-away',
                    interactive: true,
                    onShow: () => {
                      return !$validation.isEmpty(
                        getImportMessage(importContext)
                      )
                    },
                  }"
                  :class="importStatusVsClass(importContext.status)"
                  class="word-break max-width170px pL20"
                >
                  {{
                    $constants.importProcessEnumDisplayNameMap[
                      importContext.status
                    ]
                  }}
                </div>
              </div>

              <div class="fc-grey2-text12 pT10">
                {{
                  $options.filters.formatDate(importContext.importTime, false)
                }}
              </div>
            </div>

            <div class="asset-history-card-body">
              <div class="row">
                <div class="pT10 col-6">
                  <div class="fc-grey2-text12 pT10">Site</div>
                  <div class="label-txt-black pT5">
                    {{
                      $store.getters.getSite(importContext.siteId)
                        ? $store.getters.getSite(importContext.siteId).name
                        : '---'
                    }}
                  </div>
                </div>
                <div class="pT10 col-6">
                  <div class="fc-grey2-text12 pT10">Imported by</div>
                  <div class="label-txt-black pT5">
                    {{ $store.getters.getUser(importContext.uploadedBy).name }}
                  </div>
                </div>
              </div>

              <div class="row">
                <div class="col-6">
                  <div v-if="!showProceed(importContext.status)">
                    <div class="fc-grey2-text12 pT20">Mapped fields</div>
                    <div class="pT5">
                      <a
                        @click="openImportFielddialog(importContext)"
                        style="font-size: 14.8px"
                      >
                        {{ Nooffields(importContext) }} fields</a
                      >
                    </div>
                  </div>
                  <div v-else>
                    <div class="fc-grey2-text12 pT20">Mapped fields</div>
                    <div class="label-txt-black pT5">
                      No fields mapped yet
                    </div>
                  </div>
                </div>
                <div class="col-6">
                  <div class="fc-grey2-text12 pT20">Imported file</div>
                  <div class="pT5">
                    <a
                      :href="url(importContext)"
                      :download="importContext.fileName"
                      style="font-size: 14.8px"
                    >
                      {{ importContext.fileName }}</a
                    >
                  </div>
                </div>
              </div>

              <div
                v-if="
                  !showProceed(importContext.status) &&
                    getImportMessage(importContext) !== null
                "
              >
                <div class="fc-grey2-text12 pT20">Failure reason</div>
                <div class="fc-black-11 pT5">
                  {{ getImportMessage(importContext) }}
                </div>
              </div>

              <div v-if="isInventoryModule" class="pT20">
                <div class="label-txt-black">
                  {{ getStoreName(importContext) }}
                </div>
                <div class="fc-grey2-text12 pT10">Storeroom</div>
              </div>
            </div>
            <div v-if="showProceed(importContext.status)">
              <el-button
                class="fc-btn-green-lite width100"
                @click="emitContext(importContext)"
                >Proceed to Next</el-button
              >
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import Pagination from 'src/newapp/components/FPagination.vue'

export default {
  props: ['visibility', 'historyMeta'],
  components: { Pagination },
  data() {
    return {
      importHistoryList: [],
      loading: false,
      dialogVisible: true,
      showDialog: false,
      historyData: [],
      page: 1,
      listCount: null,
      perPage: 10,
      currentListCount: 0,
      hasMoreList: false,
      showPagination: false,
    }
  },
  created() {
    if (!this.isInventoryModule) {
      this.$store.dispatch('loadSites')
    }
  },
  mounted() {
    this.loadImportHistory()
  },
  computed: {
    moduleName() {
      return this.$route.params.moduleName || null
    },
    isInventoryModule() {
      return ['purchasedItem', 'purchasedTool'].includes(this.moduleName)
    },
  },
  watch: {
    page() {
      this.loadImportHistory()
    },
  },
  methods: {
    url(importContext) {
      let url = `/api/v2/files/download/${importContext.fileId}`
      return url
    },
    openImportFielddialog(importContext) {
      this.showDialog = true
      this.historyData = []
      for (const [key, value] of Object.entries(
        importContext.fieldMappingJSON
      )) {
        importContext.fields.forEach(field => {
          if (key.split('__')[1] == field) {
            let data = {}
            data['faciliofields'] =
              importContext.facilioFieldMapping[field].displayName
            data['filefields'] = value
            this.historyData.push(data)
          }
        })
      }
    },
    Nooffields(importContext) {
      if (importContext.fieldMappingJSON !== null) {
        return Object.keys(importContext.fieldMappingJSON).length
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$emit('update:visibility', false)
    },
    closeDialog() {
      this.showDialog = false
    },
    importStatusVsClass(status) {
      if (status === 1 || status === 5 || status === 6 || status === 4) {
        return 'fc-dark-blue4-14'
      } else if (status === 2 || status === 7) {
        return 'fc-warning-14'
      } else if (status === 8) {
        return 'fc-success-14'
      } else if (status === 9 || status === 3) {
        return 'fc-failed-14'
      }
    },
    showProceed(status) {
      if (status === 1 || status === 5 || status === 6 || status === 4) {
        return true
      } else {
        return false
      }
    },
    loadImportHistory() {
      this.loading = true
      let { perPage } = this
      let param = {
        count: perPage,
        importMode: this.historyMeta.importMode,
        moduleName: this.moduleName,
        page: this.page,
        perPage,
      }
      this.$http
        .post('/v2/import/importHistoryList', param)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.importHistoryList =
              response.data.result.importProcessContextList
            this.currentListCount = this.importHistoryList?.length || 0
            this.showPagination =
              this.currentListCount === perPage || this.page != 1
            this.hasMoreList = this.currentListCount === perPage
          } else {
            this.$message.error('Unable to Fetch Import History')
          }
          this.loading = false
        })
        .catch(error => {
          this.loading = false
          this.$message.error(error)
        })
    },
    goBack() {
      this.$emit('showUpload')
    },
    statusVsAction(context) {
      switch (context.status) {
        case 1: {
          this.emitContext(context)
          break
        }
        case 5: {
          this.emitContext(context)
          break
        }
        case 6: {
          this.emitContext(context)
          break
        }
        default: {
          break
        }
      }
    },
    emitContext(context) {
      this.$emit('continueImport', context)
    },
    getStoreName(importContext) {
      return this.$getProperty(
        importContext,
        'importJobMetaJson.storeRoomName',
        '---'
      )
    },
    getImportMessage(importContext) {
      if (importContext.status === 9) {
        return this.$getProperty(
          importContext,
          'importJobMetaJson.errorMessage',
          null
        )
      } else if (importContext.status === 3) {
        return this.$getProperty(
          importContext,
          'importJobMetaJson.initialParseError',
          null
        )
      } else if (importContext.status === 8) {
        let msg = ''
        let inserted =
          this.$getProperty(
            importContext,
            'importJobMetaJson.Inserted',
            null
          ) || 0
        let updated =
          this.$getProperty(importContext, 'importJobMetaJson.Updated', null) ||
          0
        let skipped =
          this.$getProperty(importContext, 'importJobMetaJson.Skipped', null) ||
          0
        if (inserted) {
          msg += `Inserted: ${inserted} `
        }
        if (updated) {
          msg += `Updated: ${updated} `
        }
        if (skipped) {
          msg += `Skipped: ${skipped}`
        }
        return msg
      }
      return ''
    },
  },
}
</script>
