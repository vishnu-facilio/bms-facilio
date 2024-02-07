<template>
  <div v-if="isLoading" class="flex-middle fc-empty-white">
    <spinner :show="isLoading" size="80"></spinner>
  </div>
  <div v-else class="details_fc" ref="failureReport-body">
    <div class="details_body">
      <div class="content">
        <div class="op5">
          {{ $t('common.failure_class.fc_name') }}
        </div>
        <span class="d-flex name">
          {{ getName(failureClass) }}
        </span>
      </div>
      <div class="content" style="margin-left:120px">
        <span style="opacity: 0.5;">{{
          $t('common.failure_class.description')
        }}</span>
        <span class="d-flex name">
          {{ getDescription(failureClass) }}
        </span>
      </div>
    </div>
    <div class="report-header-flx">
      <div class="report-header">
        {{ $t('common.failure_class.failure_report') }}
      </div>
      <div v-if="showTable" class="edit_delete">
        <span @click="showDialog()">
          <inline-svg
            class="edit"
            src="svgs/safetyPlan/edit"
            iconClass="icon icon-sm"
          ></inline-svg>
        </span>
        <span @click="deleteRecords()">
          <inline-svg
            class="edit"
            src="svgs/safetyPlan/delete"
            iconClass="icon icon-sm"
          ></inline-svg>
        </span>
      </div>
    </div>

    <div v-if="!showTable" class="report-noData">
      <div class="report-body flx">
        <inline-svg
          src="svgs/spacemanagement/ic-nodata"
          iconClass="text_center icon icon-xxxlg"
          class="mL35"
        ></inline-svg>
        <div class="mL60">
          <div class="mT2 label-txt-black f14 ">
            {{ $t('common.failure_class.report_not_added') }}
          </div>
          <div>
            <button class="btn mT15" @click="showDialog">
              <span class="add">
                {{ $t('common.failure_class.add_report') }}
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>
    <div v-else ref="table">
      <table>
        <tr>
          <th>{{ $t('common.failure_class.type') }}</th>
          <th>{{ $t('common.failure_class.name') }}</th>
          <th>{{ $t('common.failure_class.description') }}</th>
        </tr>
        <tr v-if="failureProblem">
          <td>{{ failureProblem.type }}</td>
          <td>{{ failureProblem.code }}</td>
          <td>{{ getDescription(failureProblem) }}</td>
        </tr>
        <tr v-else>
          <td class="no-data">
            {{ $t('common.failure_class.problem_not_added') }}
          </td>
        </tr>
        <tr v-if="failureCause">
          <td>{{ failureCause.type }}</td>
          <td>{{ failureCause.code }}</td>
          <td>{{ getDescription(failureCause) }}</td>
        </tr>
        <tr v-else>
          <td class="no-data">
            {{ $t('common.failure_class.cause_not_added') }}
          </td>
        </tr>
        <tr v-if="failureRemedy">
          <td>{{ failureRemedy.type }}</td>
          <td>{{ failureRemedy.code }}</td>
          <td>{{ getDescription(failureRemedy) }}</td>
        </tr>
        <tr v-else style="width:100%">
          <td class="no-data">
            {{ $t('common.failure_class.remedy_not_added') }}
          </td>
        </tr>
      </table>
    </div>
    <FailureReportDialog
      v-if="showMsgPopup"
      :key="renderDialogComponent"
      :failureClass="failureClass"
      :selectedProblem="selectedProblem"
      :selectedCause="selectedCause"
      :selectedRemedy="selectedRemedy"
      @showLookUpWizard="showLookUpWizard"
      @savePCRToRelModule="savePCRToRelModule"
      @closeDialog="closeDialog"
    ></FailureReportDialog>
    <div v-if="canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="failureCodeField"
        @setLookupFieldValue="closeLookUpWizard"
      ></FLookupFieldWizard>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import FLookupFieldWizard from 'src/components/FLookupFieldWizard.vue'
import FailureReportDialog from './FailureReportDialog.vue'
const EQUALS_OPERATOR = 9

export default {
  props: [
    'moduleName',
    'details',
    'resizeWidget',
    'layoutParams',
    'calculateDimensions',
  ],
  components: {
    FailureReportDialog,
    FLookupFieldWizard,
  },
  data() {
    return {
      isLoading: false,
      failureClass: null,
      failureClassId: null,
      showTable: false,
      relModuleId: null,
      failureProblem: null,
      failureCause: null,
      failureRemedy: null,
      showMsgPopup: false,
      selectedProblem: null,
      selectedCause: null,
      selectedRemedy: null,
      canShowLookupWizard: false,
      pcrModuleName: null,
      workorderFailureReport: null,
      problems: [],
      causes: [],
      remedies: [],
      codeIds: [],
      pcrDisplayName: null,
      pcrObj: {},
      isEdit: false,
      selectedField: null,
      renderDialogComponent: 0,
    }
  },
  created() {
    this.getAssociatedFailureClass()
  },
  computed: {
    problemField() {
      let { failureClass } = this || {}
      let currentField = this.getFailurefields(
        'failurecodeproblems',
        'Failure Code Problems',
        'Choose Problem'
      )
      if (failureClass) {
        let filters = {
          failureClass: {
            operatorId: EQUALS_OPERATOR,
            value: [`${this.failureClass?.id}`],
          },
        }
        currentField['filters'] = filters
      }
      return currentField
    },
    causeField() {
      let { selectedProblem } = this || {}
      let currentField = this.getFailurefields(
        'failurecodecauses',
        'Failure Code Causes',
        'Choose Cause'
      )
      if (selectedProblem) {
        let filters = {
          failureCodeProblems: {
            operatorId: EQUALS_OPERATOR,
            value: [`${this.selectedProblem}`],
          },
        }
        currentField['filters'] = filters
      }

      return currentField
    },
    remedyField() {
      let { selectedCause } = this || {}
      let currentField = this.getFailurefields(
        'failurecoderemedies',
        'Failure Code Remedies',
        'Choose Remedy'
      )
      if (selectedCause) {
        let filters = {
          failureCodeCauses: {
            operatorId: EQUALS_OPERATOR,
            value: [`${this.selectedCause}`],
          },
        }
        currentField['filters'] = filters
      }

      return currentField
    },
    failureCodeField() {
      let { codeIds, selectedField } = this || {}
      if (codeIds.length <= 0) codeIds.push('0')
      let currentField = this.getFailurefields(
        'failurecode',
        this.pcrDisplayName
      )
      if (codeIds) {
        let filters = {
          id: {
            operatorId: EQUALS_OPERATOR,
            value: codeIds,
          },
        }
        currentField['filters'] = filters
      }
      if (selectedField) currentField['selectedItems'] = [selectedField]
      return currentField
    },
  },
  methods: {
    init() {
      this.selectedProblem = null
      this.selectedCause = null
      this.selectedRemedy = null
      this.relModuleId = null
      this.workorderFailureReport = null
    },

    autoResize(reset) {
      this.$nextTick(() => {
        let { h: layoutHeight } = this.layoutParams || {}
        if (reset) {
          this.resizeWidget({ h: layoutHeight })
          return
        }
        let container = this.$refs['failureReport-body']
        if (!container) return
        let height = container.scrollHeight + 20
        let width = container.scrollWidth
        let { h: actualHeight } = this.calculateDimensions({ height, width })
        let h = actualHeight > layoutHeight ? actualHeight : layoutHeight
        if (actualHeight !== layoutHeight) {
          this.resizeWidget({ h })
        }
      })
    },

    closeDialog() {
      this.showMsgPopup = false
    },

    getProblem() {
      let fc_problems = this.$getProperty(
        this,
        'failureClass.failureCodeProblems',
        []
      )
      let problems = []
      let obj = {}
      fc_problems.forEach(problem => {
        obj[JSON.stringify(problem.code_id)] = problem.id
        problems.push(problem)
      })
      this.problems = problems
      this.pcrObj = { ...this.pcrObj, ...obj }
    },

    async showLookUpWizard(moduleName, selectedItem) {
      this.selectedField = null
      let ids = []
      let obj = {}
      if (moduleName === 'failurecodeproblems') {
        this.problems.forEach(prob => {
          if (prob.id === selectedItem) {
            this.selectedField = { label: prob.code, value: prob.code_id }
          }
          ids.push(JSON.stringify(prob.code_id))
        })
        this.pcrDisplayName = 'Failure Code Problems'
      } else if (moduleName === 'failurecodecauses') {
        this.problems.forEach(problem => {
          if (problem.id === this.selectedProblem) {
            let causes = this.$getProperty(problem, 'failureCodeCauses', [])
            causes.forEach(cause => {
              if (cause.id === selectedItem) {
                this.selectedField = { label: cause.code, value: cause.code_id }
              }
              obj[JSON.stringify(cause.code_id)] = cause.id
              ids.push(JSON.stringify(cause.code_id))
            })
          }
        })
        this.pcrDisplayName = 'Failure Code Causes'
      } else if (moduleName === 'failurecoderemedies') {
        this.problems.forEach(problem => {
          if (problem.id === this.selectedProblem) {
            let causes = this.$getProperty(problem, 'failureCodeCauses', [])
            causes.forEach(cause => {
              if (cause.id === this.selectedCause) {
                let remedies = this.$getProperty(
                  cause,
                  'failureCodeRemedies',
                  []
                )
                remedies.forEach(remedy => {
                  if (remedy.id === selectedItem) {
                    this.selectedField = {
                      label: remedy.code,
                      value: remedy.code_id,
                    }
                  }
                  obj[JSON.stringify(remedy.code_id)] = remedy.id
                  ids.push(JSON.stringify(remedy.code_id))
                })
              }
            })
          }
        })
        this.pcrDisplayName = 'Failure Code Remedies'
      }
      this.codeIds = [...ids]
      this.pcrObj = { ...this.pcrObj, ...obj }
      this.pcrModuleName = moduleName
      this.canShowLookupWizard = true
    },

    closeLookUpWizard(pcrLookUpdata) {
      let modName = this.pcrModuleName
      let { renderDialogComponent } = this
      if (pcrLookUpdata) {
        let field = this.$getProperty(pcrLookUpdata, 'field')
        if (!isEmpty(field)) {
          let selectedData = this.$getProperty(field, 'selectedItems')[0]
          if (modName === 'failurecodeproblems') {
            this.selectedProblem = this.pcrObj[
              JSON.stringify(selectedData.value)
            ]
            this.selectedCause = null
            this.selectedRemedy = null
          } else if (modName === 'failurecodecauses') {
            this.selectedCause = this.pcrObj[JSON.stringify(selectedData.value)]
            this.selectedRemedy = null
          } else if (modName === 'failurecoderemedies') {
            this.selectedRemedy = this.pcrObj[
              JSON.stringify(selectedData.value)
            ]
          }
          this.renderDialogComponent =
            selectedData.value + renderDialogComponent
        }
      }
    },

    getFailurefields(lookupModuleName, displayName, placeHolder) {
      let currentField = {
        isDataLoading: false,
        options: [],
        lookupModuleName,
        field: {
          lookupModule: {
            name: lookupModuleName,
            displayName,
          },
        },
        multiple: false,
        forceFetchAlways: true,
        isDisabled: true,
        placeHolderText: placeHolder,
      }
      return currentField
    },

    async showDialog() {
      if (!this.failureClass) {
        this.$message.error(this.$t('common.failure_class.no_failure_class'))
      } else {
        if (this.relModuleId) {
          await this.setPCRRecords(this.workorderFailureReport)
          this.isEdit = true
          this.showMsgPopup = true
        } else {
          this.isEdit = false
          this.showMsgPopup = true
        }
      }
    },

    async deleteRecords() {
      let { moduleName, relModuleId } = this
      let value = await this.$dialog.confirm({
        title: this.$t(`common.header.delete_record`),
        message: this.$t(
          `common._common.are_you_sure_want_to_delete_this_record`
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        this.isLoading = true
        let {
          error,
        } = await API.deleteRecord(`${moduleName}FailureClassRelationship`, [
          relModuleId,
        ])
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.init()
          await this.refreshWidget()
          this.$message.success(this.$t('common.failure_class.report_delete'))
          this.isLoading = false
        }
      }
    },

    getName(item) {
      return this.$getProperty(item, 'name', '---')
    },

    getDescription(item) {
      let description = this.$getProperty(item, 'description', 'NA')
      return description
    },

    async getAssociatedFailureClass() {
      this.isLoading = true
      let { moduleName } = this
      let id = this.$getProperty(this, 'details.id', null)
      let { workorder } = this.details
      let { failureClass } =
        moduleName === 'workorder' ? workorder : this.details
      let failureClassId = this.$getProperty(failureClass, 'id', null)
      let params = {
        filters: JSON.stringify({
          parent: {
            operatorId: EQUALS_OPERATOR,
            value: [`${id}`],
          },
        }),
      }
      let { list, error } = await API.fetchAll(
        `${moduleName}FailureClassRelationship`,
        params,
        {
          force: true,
        }
      )
      let data = list?.length && list[0]
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        if (data) {
          this.workorderFailureReport = data
          this.relModuleId = this.$getProperty(data, 'id', null)
          this.failureClass = this.$getProperty(data, 'failureClass', null)
          this.failureProblem = this.$getProperty(
            data,
            'failureProblem.failureCode',
            null
          )
          if (this.failureProblem) this.failureProblem['type'] = 'Problem'
          this.failureCause = this.$getProperty(
            data,
            'failureCause.failureCode',
            null
          )
          if (this.failureCause) this.failureCause['type'] = 'Cause'
          this.failureRemedy = this.$getProperty(
            data,
            'failureRemedy.failureCode',
            null
          )
          if (this.failureRemedy) this.failureRemedy['type'] = 'Remedy'
          this.setPCRRecords(data)
        }
        await this.getFailureClass(failureClassId)
      }
      this.showTable = this.selectedProblem ? true : false
      this.isLoading = false
      this.autoResize()
    },
    setPCRRecords(data) {
      let problem = this.$getProperty(data, 'failureProblem.id', null)
      let cause = this.$getProperty(data, 'failureCause.id', null)
      let remedy = this.$getProperty(data, 'failureRemedy.id', null)
      this.selectedRemedy = remedy
      this.selectedCause = cause
      this.selectedProblem = problem
    },
    async getFailureClass(failureClassId) {
      if (failureClassId) {
        let { failureclass, error } = await API.fetchRecord(
          'failureclass',
          {
            id: failureClassId,
          },
          {
            force: true,
          }
        )
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.failureClass = failureclass
          this.getProblem()
        }
      }
    },
    getFailureData(subModules, id) {
      if (subModules.length) {
        let woFailurevalue = {}
        subModules.forEach((data, index) => {
          if (data.id === id) {
            woFailurevalue = {
              data,
              index,
            }
          }
        })
        return woFailurevalue
      }
    },

    async savePCRToRelModule(
      selectedProblem,
      selectedCause,
      selectedRemedy,
      force = true
    ) {
      this.isLoading = true
      let { moduleName, details, isEdit, relModuleId, failureClass } = this
      let failureClassId = this.$getProperty(failureClass, 'id', null)
      let data = {
        parent: {
          id: details.id,
        },
        failureClass: {
          id: failureClassId,
        },
        failureProblem: {
          id: selectedProblem,
        },
        failureCause: {
          id: selectedCause,
        },
        failureRemedy: {
          id: selectedRemedy,
        },
      }
      let response = {}
      let successMsg = ''
      if (!isEdit) {
        successMsg = this.$t('common.failure_class.report_added')
        response = await API.createRecord(
          `${moduleName}FailureClassRelationship`,
          { data },
          force
        )
      } else {
        successMsg = this.$t('common.failure_class.report_updated')
        response = await API.updateRecord(
          `${moduleName}FailureClassRelationship`,
          {
            id: relModuleId,
            data,
          },
          force
        )
      }
      this.isLoading = false

      let { error } = response || {}
      if (error) {
        this.$message.error(error?.message || 'Error occured while saving')
      } else {
        await this.refreshWidget()
        this.$message.success(successMsg)
      }
    },
    async refreshWidget() {
      this.autoResize(true)
      await this.getAssociatedFailureClass()
    },
  },
}
</script>

<style scoped>
.details_fc {
  background-color: #fff;
}
.flx {
  display: flex;
  flex-direction: column;
  text-align: center;
}

.content {
  font-size: 12px;
  letter-spacing: 0.16px;
  color: #324056;
}

.details_body {
  margin: 10px 0;
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  padding: 24px 24px 16px;
}

.height364 {
  height: 385px;
}
.report-header {
  font-size: 16px;
  font-weight: 450;
  padding: 18px 24px 16px;
}

.name {
  font-size: 14px;
  font-weight: 450;
  margin: 11px 0 0;
  letter-spacing: 0.57px;
  color: #000;
}
.btn {
  height: 35px;
  margin: 10px 44px 0 45px;
  border-radius: 4px;
  background-color: #3ab2c2;
}

.add {
  font-size: 14px;
  font-weight: 450;
  letter-spacing: 0.5px;
}

table {
  width: 100%;
}

tr td:nth-child(1) {
  width: 20%;
}

tr:nth-child(2),
tr:nth-child(3) {
  border-bottom: 1px solid #efefef;
  border-collapse: collapse;
  border-spacing: 0;
}

th {
  font-size: 14px;
  font-weight: 450;
  background-color: #f3f1fc;
  padding: 14px 0 14px 24px;
}

td {
  padding: 14px 0 14px 24px;
}

th,
td {
  text-align: left;
  padding-left: 24px;
  padding-right: 24px;
  width: 20px;
}

.report-header-flx {
  display: flex;
  justify-content: space-between;
}

.edit_delete {
  display: flex;
  cursor: pointer;
  padding: 18px 24px 16px;
}

.edit {
  width: 20px;
  height: fit-content;
  border-radius: 50%;
  margin-right: 10px;
  display: flex;
  justify-content: center;
}

input {
  background-color: #fff;
}

.no-data {
  width: 30%;
  letter-spacing: 0.5px;
  color: rgba(50, 64, 86, 0.5);
}

.dialog-padding {
  padding: 5px 20px;
}
</style>

<style lang="scss">
.dialog-body {
  .el-dialog__body {
    padding: 0 20px;

    .modal-dialog-footer {
      .modal-btn-cancel {
        width: 50%;
        padding-top: 18px;
        padding-bottom: 18px;
        cursor: pointer;
        background-color: #f4f4f4;
        border: transparent;
        color: #8f8f8f;
        font-size: 13px;
        text-transform: uppercase;
        font-weight: 500;
        float: left;
        border-radius: 0;
        line-height: 16px;
        cursor: pointer;
        border-bottom-left-radius: 5px;
      }

      .modal-btn-save {
        width: 50%;
        padding-top: 18px;
        padding-bottom: 18px;
        cursor: pointer;
        background-color: #39b2c2;
        border: transparent;
        letter-spacing: 1.1px;
        text-align: center;
        color: #ffffff;
        text-transform: uppercase;
        font-weight: 500;
        border-radius: 0;
        float: right;
        line-height: 16px;
        border-bottom-right-radius: 5px;
        cursor: pointer;
      }
    }
  }
}
</style>
