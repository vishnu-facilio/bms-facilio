<template>
  <div>
    <div v-if="this.importProcessContext.status === 2">
      <div
        class="text-center mT20"
        v-if="this.importProcessContext.status === 2"
      >
        <div class="flex-middle flex-col justify-content-center height57vh">
          <div>
            <spinner show="true" size="90"></spinner>
          </div>
          <div class="fc-grey2-text12 pT20">Data Validation in Progress</div>
        </div>
      </div>
    </div>
    <div v-if="loading">
      <div class="flex-middle flex-col justify-content-center height57vh">
        <div>
          <spinner show="true" size="90"></spinner>
        </div>
        <div class="fc-grey2-text12 pT20">Fetching Data for Validation</div>
      </div>
    </div>
    <div class v-if="showParseErrorMessage">
      <div class="flex-middle flex-col justify-content-center height57vh f14">
        <div class="red-txt-small">Import Failed.</div>
        <div>{{ parseErrorMessage }}</div>
        <div class="text-center mT20">
          <el-button @click="goBacktoBeginning" class="plain"
            >Restart Import</el-button
          >
        </div>
      </div>
    </div>
    <div v-if="importProcessContext.status === 5 && sheetDataValidation">
      <div class="fc-black-12 text-left bold text-uppercase letter-spacing1">
        Data Validation
      </div>
      <div class="fc-heading-border-width43 mT10 height2px"></div>
      <div class="fR">
        <div class="fc__layout__align">
          <div class="fc-warning2-12">
            {{ sheetDuplicates.length }}
            {{ sheetDuplicates.length === 1 ? 'Duplicate' : 'Duplicates' }}
            Found
          </div>
        </div>
      </div>
      <div class="import-duplicate-scroll clearboth mT40">
        <table class="import-duplicate-table table-fixed">
          <thead>
            <tr>
              <th
                style="width: 230px;padding-left: 15px;padding-right: 15px;"
                v-for="(columnHeading,
                columnHeadingIndex) in computedColumnHeadings"
                :key="columnHeadingIndex"
              >
                {{ columnHeading }}
              </th>
            </tr>
          </thead>
        </table>
        <el-collapse class="fc-import-collapse">
          <el-collapse-item
            v-for="(logContext, logIndex) in sheetDuplicates"
            :key="logIndex"
          >
            <template slot="title">
              <div class="fc__layout__align width100 pL10 pR10">
                <div>
                  {{ computeTitleFromContext(logContext) }}
                  <i
                    v-if="
                      logContext.error_resolved &&
                        logContext.error_resolved === 3
                    "
                    title="Found in Site"
                    v-tippy="{
                      placement: 'top',
                      animation: 'shift-away',
                      arrow: true,
                    }"
                    class="el-icon-warning-outline mL10"
                  ></i>
                </div>
                <div class="fc-warning2-12 fR">
                  Count: {{ unmarkedRows(logContext) }}
                </div>
              </div>
            </template>
            <div class="clearboth">
              <table class="import-child-table table-fixed">
                <thead>
                  <tr
                    v-for="(rowContext,
                    rowContextIndex) in logContext.rowContexts"
                    :key="rowContextIndex"
                  >
                    <template
                      v-for="(column, columnIndex) in computedColumnHeadings"
                    >
                      <td
                        style="width: 230px;"
                        v-bind:style="{ disabled: rowContext.delete }"
                        v-if="column !== 'delete'"
                        :key="columnIndex"
                      >
                        <el-input
                          readonly
                          class="fc-input-border-remove import-fc-input-border-remove"
                          :disabled="rowContext.delete"
                          v-model="rowContext.colVal[column]"
                        ></el-input>
                      </td>
                    </template>
                    <td class="import-asset-last-sticky">
                      <i
                        v-if="!rowContext.delete"
                        class="pointer el-icon-close asset-table-close"
                        @click="$set(rowContext, 'delete', true)"
                      ></i>
                      <span
                        v-if="rowContext.delete"
                        class="fR import-add-txt pointer"
                        @click="$set(rowContext, 'delete', false)"
                        >Add</span
                      >
                    </td>
                  </tr>
                </thead>
              </table>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
      <div class="pT20 pB30">
        <div>
          <el-checkbox
            @change="skipChangeActions"
            v-model="skipAllSheetDuplicate"
            >Skip All Duplicate in Sheet</el-checkbox
          >
        </div>
        <div class="mT10">
          <el-checkbox
            :disabled="skipAllSheetDuplicate"
            v-model="removeFirstRowInSheetDuplicate"
            >Remove All Duplicate entires except first row</el-checkbox
          >
        </div>
      </div>
    </div>
    <div v-else-if="importProcessContext.status === 5 && siteDataValidation">
      <div class="fc-black-12 text-left bold text-uppercase letter-spacing1">
        Data Validation
      </div>
      <div class="fc-heading-border-width43 mT10 height2px"></div>
      <div class="import-duplicate-scroll2 clearboth mT40">
        <div class="import-border-bottom duplicates-sticky">
          <span class="fc-warning2-12 fwBold f14">{{
            siteDuplicates.length
          }}</span>
          <span class="mL10">{{
            printableModuleName() +
              (siteDuplicates.length === 1 ? ' is' : 's are') +
              ' already present in the site'
          }}</span>
          <div class="fR">
            <el-checkbox disabled v-model="skipAllSiteValidation"
              >Skip All Duplicates</el-checkbox
            >
          </div>
        </div>
        <div
          v-for="(logContext, logIndex) in siteDuplicatesViewList"
          class="mT20"
          :key="logIndex"
        >
          <div class="import-border-bottom">
            <span>{{ computeTitleFromContext(logContext) }}</span>
            <span class="fc-warning2-12 mL10">{{
              'Count: ' + logContext.rowContexts.length
            }}</span>
          </div>
        </div>
        <div
          v-if="siteValidationViewCount < siteDuplicates.length"
          class="pointer text-center mT20 import-add-txt"
          @click="siteValidationViewCount = siteDuplicates.length"
        >
          View More
        </div>
        <div
          v-else-if="
            siteValidationViewCount === siteDuplicates.length &&
              siteDuplicates.length !== 5
          "
          class="pointer text-center mT20 import-add-txt"
          @click="siteValidationViewCount = 5"
        >
          View Less
        </div>
      </div>
      <div></div>
    </div>
    <div v-if="importProcessContext.status === 6">
      <div class="fc-black-12 text-left bold text-uppercase letter-spacing1">
        Validation Completed
      </div>
      <div class="fc-heading-border-width43 mT10 height2px"></div>
      <div class="pT40">
        <div class="label-txt-black">
          Are you sure to continue with the import?
        </div>
        <div class="fc-black3-16 bold pT15">
          Total count of validated rows - {{ importProcessContext.totalRows }}
        </div>
      </div>
    </div>
    <div class="modal-dialog-footer" v-if="!inProgress && !loading">
      <el-button @click="goBacktoBeginning" class="modal-btn-cancel"
        >CANCEL</el-button
      >
      <el-button
        :loading="saving"
        type="primary"
        @click="statusVsNextClick"
        class="modal-btn-save"
      >
        Proceed to next
        <img
          src="~assets/arrow-pointing-white-right.svg"
          width="17px"
          class="fR"
        />
      </el-button>
    </div>
  </div>
</template>
<script>
import importHelper from './ImportHelper'
export default {
  props: [
    'importProcessContext',
    'parseError',
    'dataValidation',
    'timeColumnDateFormat',
  ],
  mixins: [importHelper],
  data() {
    return {
      duplicateEntries: null,
      loading: false,
      saving: false,
      sheetDataValidation: false,
      siteDataValidation: false,
      sheetDuplicates: [],
      siteDuplicates: [],
      skipAllSheetDuplicate: false,
      removeFirstRowInSheetDuplicate: false,
      skipAllSiteValidation: true,
      siteValidationViewCount: 5,
    }
  },
  watch: {
    importProcessContext() {
      if (this.importProcessContext.status === 5) {
        this.fetchDataForValidation()
      }
    },
  },
  computed: {
    siteDuplicatesViewList() {
      return this.siteDuplicates.filter(
        (i, index) => index < this.siteValidationViewCount
      )
    },
    inProgress() {
      if (this.importProcessContext.status < 5) {
        return true
      } else {
        return false
      }
    },
    showParseErrorMessage() {
      if (this.parseError && this.parseError.visibility) {
        return true
      } else {
        return false
      }
    },
    parseErrorMessage() {
      if (this.parseError && this.parseError.errorMessage) {
        return this.parseError.errorMessage
      } else {
        return ''
      }
    },
    computedColumnHeadings() {
      let columnHeadings = []
      for (let heading of this.importProcessContext.columnHeadings) {
        if (heading !== null) {
          columnHeadings.push(heading)
        }
      }
      return columnHeadings
    },
  },
  mounted() {
    if (this.importProcessContext.status === 5) {
      this.fetchDataForValidation()
    }
  },
  methods: {
    skipChangeActions(val) {
      if (val) {
        this.removeFirstRowInSheetDuplicate = false
      }
    },
    fetchDataForValidation() {
      this.loading = true
      this.$http
        .post('/import/fetchdataforValidation', {
          importProcessContext: this.importProcessContext,
        })
        .then(response => {
          this.duplicateEntries = response.data.unvalidatedRecords
          this.sheetDuplicates = []
          this.siteDuplicates = []
          for (let logContext of this.duplicateEntries) {
            this.$set(logContext, 'showRows', false)
            for (let rowContext of logContext.rowContexts) {
              this.$set(rowContext, 'delete', false)
            }
            if (logContext.error_resolved === 2) {
              this.sheetDuplicates.push(logContext)
            } else if (logContext.error_resolved === 3) {
              this.siteDuplicates.push(logContext)
            }
          }
          this.toggleValidationPage()
          this.$emit('update:validationInProgress', false)
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    toggleValidationPage() {
      if (this.sheetDuplicates.length > 0) {
        this.sheetDataValidation = true
      } else if (this.siteDuplicates.length > 0) {
        this.siteDataValidation = true
      }
    },
    goBacktoBeginning() {
      this.$emit('begin', 0)
    },
    removeAllDuplicates() {
      for (let logContext of this.sheetDuplicates) {
        for (let rowContext of logContext.rowContexts) {
          if (logContext.rowContexts.indexOf(rowContext) === 0) {
            continue
          } else {
            this.$set(rowContext, 'delete', true)
          }
        }
      }
    },
    unmarkedRows(logContext) {
      let unmarkedRows = 0
      let logIndex = this.sheetDuplicates.indexOf(logContext)
      for (let rowContext of this.sheetDuplicates[logIndex].rowContexts) {
        if (rowContext.delete === false) {
          unmarkedRows = unmarkedRows + 1
        }
      }
      return unmarkedRows
    },
    checkForSingleRows(logContextLength) {
      for (let key of Object.keys(logContextLength)) {
        if (logContextLength[key] > 1) {
          return false
        }
      }
      return true
    },
    computeTitleFromContext(logContext) {
      if (this.importProcessContext.importMode === 2) {
        return (
          (logContext.assetName ? logContext.assetName + '-' : '') +
          (this.timeColumnDateFormat.toLowerCase() === 'timestamp'
            ? logContext.ttime
            : this.formatTo(
                logContext.ttime,
                this.upperCaseDateYearfromFormat(this.timeColumnDateFormat)
              ))
        )
      } else {
        return logContext.assetName
      }
    },
    beginDataValidationv2() {
      if (this.sheetDataValidation) {
        if (this.skipAllSheetDuplicate) {
          for (let logContext of this.sheetDuplicates) {
            for (let rowContext of logContext.rowContexts) {
              this.$set(rowContext, 'delete', true)
            }
          }
        } else if (this.removeFirstRowInSheetDuplicate) {
          for (let logContext of this.sheetDuplicates) {
            for (let rowContext of logContext.rowContexts) {
              if (logContext.rowContexts.indexOf(rowContext) === 0) {
                continue
              } else {
                this.$set(rowContext, 'delete', true)
              }
            }
          }
        }
        if (this.validateForRemoveCheck(this.sheetDuplicates)) {
          this.sheetDataValidation = false
          if (this.siteDuplicates.length > 0) {
            this.siteDataValidation = true
          } else {
            this.prepareValidationUpdate()
          }
        }
      } else if (this.siteDataValidation) {
        if (!this.skipAllSiteValidation) {
          this.$message.error('Data cannot be added with same Name in a Site')
        } else {
          for (let logContext of this.siteDuplicates) {
            for (let rowContext of logContext.rowContexts) {
              this.$set(rowContext, 'delete', true)
            }
          }
          if (this.validateForRemoveCheck(this.siteDuplicates)) {
            this.prepareValidationUpdate()
          }
        }
      }
    },
    validateForRemoveCheck(duplicateEntries) {
      let logContextLength = {}
      for (let logContext of duplicateEntries) {
        for (let rowContext of logContext.rowContexts) {
          if (logContextLength[logContext.id]) {
            if (rowContext.delete !== true) {
              logContextLength[logContext.id] =
                logContextLength[logContext.id] + 1
            }
          } else {
            if (rowContext.delete !== true) {
              logContextLength[logContext.id] = 1
            }
          }
        }
      }
      if (this.checkForSingleRows(logContextLength)) {
        return true
      } else {
        this.$message.error('All duplicate entires need to be validated')
        return false
      }
    },
    beginDataValidation() {
      let logContextLength = {}
      for (let logContext of this.sheetDuplicates) {
        for (let rowContext of logContext.rowContexts) {
          if (logContextLength[logContext.id]) {
            if (rowContext.delete !== true) {
              logContextLength[logContext.id] =
                logContextLength[logContext.id] + 1
            }
          } else {
            if (rowContext.delete !== true) {
              logContextLength[logContext.id] = 1
            }
          }
        }
      }
      if (this.checkForSingleRows(logContextLength)) {
        this.prepareValidationUpdate()
      } else {
        this.$message.error('All duplicate entires need to be validated')
      }
    },
    fetchUnmarked(logContext) {
      for (let rowContext of logContext.rowContexts) {
        if (rowContext.delete === false) {
          return rowContext
        }
      }
      return false
    },
    statusVsNextClick() {
      if (this.importProcessContext.status === 6) {
        this.finishValidation()
      } else if (this.importProcessContext.status === 5) {
        this.beginDataValidationv2()
      }
    },
    prepareValidationUpdate() {
      let duplicateRecords = []
      for (let logContext of this.sheetDuplicates) {
        let unmarkedRowContext = this.fetchUnmarked(logContext)
        if (unmarkedRowContext) {
          logContext.correctedRow = unmarkedRowContext
          logContext.error_resolved = 4
        } else {
          logContext.error_resolved = 5
        }
        duplicateRecords.push(logContext)
      }
      for (let logContext of this.siteDuplicates) {
        duplicateRecords.push(logContext)
      }

      this.saving = true
      this.$http
        .post('/import/validateddata', {
          validatedRecords: duplicateRecords,
          importProcessContext: this.importProcessContext,
        })
        .then(response => {
          this.$emit(
            'update:importProcessContext',
            response.data.importProcessContext
          )
          this.saving = false
        })
        .catch(() => {
          this.saving = false
          this.$emit('finishValidation', false)
        })
    },
    finishValidation() {
      this.saving = true
      let url =
        this.importProcessContext.importMode === 1
          ? '/import/importData'
          : '/import/importreading'
      this.$http
        .post(url, {
          importProcessContext: this.importProcessContext,
        })
        .then(response => {
          this.$emit(
            'update:importProcessContext',
            response.data.importProcessContext
          )
          this.$emit('finishValidation', true)
          this.saving = false
        })
        .catch(() => {
          this.$message.error('Error Occured')
          this.saving = false
        })
    },
  },
}
</script>
<style scoped lang="scss">
.import-child-table tr td:first-child {
  left: 0;
  background: #f7fdfe;
  z-index: 200;
}

.import-duplicate-table tr th:first-child {
  left: 0;
  z-index: 200;
}

.import-child-table tr {
  background: #f7fdfe;
}

.import-child-table tr .import-asset-last-sticky {
  width: 40px;
  position: sticky;
  text-align: right;
  height: 38px;
  cursor: pointer;
  background: #f7fdfe;
  right: 0px;
  padding-right: 10px;
}
.duplicates-sticky {
  position: sticky;
  top: 0;
  z-index: 200;
  background-color: #fff;
}
</style>
