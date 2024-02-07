<template>
  <div class="associatedDetails">
    <div class="details">
      {{ $t('common.failure_class.associate_problems') }}
    </div>
    <div class="card">
      <div class="flex-container">
        <FailureClassPCRCard
          :details="problemRecords"
          :header="'Problems'"
          :failureCodeName="'failurecodeproblems'"
          :displayName="'Failure Code Problems'"
          :currentId="currentId"
          :hideCreateNew="true"
          @openFailureCodeForm="openFailureCodeForm"
          @showFailureCodeDialog="addFailureCodeDialog"
          @activeRecordId="activeRecordId"
          @activeCauseRecordId="activeCauseRecordId"
          @deleteRecords="deleteRecords"
        ></FailureClassPCRCard>
        <FailureClassPCRCard
          :details="causesRecord"
          :header="'Causes'"
          :failureCodeName="'failurecodecauses'"
          :displayName="'Failure Code Causes'"
          :currentId="currentCauseId"
          :hideCreateNew="isProblemSelected"
          @openFailureCodeForm="openFailureCodeForm"
          @showFailureCodeDialog="addFailureCodeDialog"
          @activeRecordId="activeRecordId"
          @activeCauseRecordId="activeCauseRecordId"
          @deleteRecords="deleteRecords"
        ></FailureClassPCRCard>
        <FailureClassPCRCard
          :details="remediesRecord"
          :header="'Remedies'"
          :failureCodeName="'failurecoderemedies'"
          :displayName="'Failure Code Remedies'"
          :hideCreateNew="isCauseSelected"
          @openFailureCodeForm="openFailureCodeForm"
          @showFailureCodeDialog="addFailureCodeDialog"
          @activeRecordId="activeRecordId"
          @activeCauseRecordId="activeCauseRecordId"
          @deleteRecords="deleteRecords"
        ></FailureClassPCRCard>
      </div>
    </div>
    <new-failure-code
      v-if="showCreateNewDialog"
      ref="new-failure-code"
      :canShowFormCreation.sync="showCreateNewDialog"
      moduleName="failurecode"
      :dataId="codeId"
      :moduleDisplayName="this.codeDisplayName"
      @closeDialog="setSelectedFailureCodes"
    ></new-failure-code>
    <FailureCodeLookUpWizard
      v-if="canShowWizard"
      :selectedLookupField="failurecodeField"
      :canShowLookupWizard.sync="canShowWizard"
      :summaryId="this.details.id"
      @setLookupFieldValue="setSelectedFailureCodes"
    ></FailureCodeLookUpWizard>
  </div>
</template>

<script>
import FailureCodeLookUpWizard from 'src/pages/assets/failureclass/failureclassPage/FailureCodeLookUpWizard.vue'
import { API } from '@facilio/api'
import NewFailureCode from 'src/pages/assets/failureclass/forms/NewFailureCode.vue'
import { isEmpty } from '@facilio/utils/validation'
import FailureClassPCRCard from 'src/pages/assets/failureclass/failureclassPage/FailureClassPCRCard.vue'
import { eventBus } from '@/page/widget/utils/eventBus'

export default {
  props: ['details'],
  components: {
    NewFailureCode,
    FailureClassPCRCard,
    FailureCodeLookUpWizard,
  },
  data() {
    return {
      currentId: -1,
      currentCauseId: -1,
      problemRecords: [],
      causesRecord: [],
      remediesRecord: [],
      canShowWizard: false,
      failurecodeField: {
        displayName: this.$t('common.header.failure_code'),
        name: 'failurecode',
        lookupModule: {
          displayName: this.$t('common.header.failure_code'),
          name: 'failurecode',
        },
        multiple: true,
        selectedItems: [],
      },
      codeName: null,
      loading: false,
      showCreateNewDialog: false,
      codeId: null,
      codeDisplayName: null,
      isProblemSelected: false,
      isCauseSelected: false,
    }
  },
  created() {
    this.setInitialValues()
  },
  methods: {
    setInitialValues() {
      let data = this.details
      this.problemRecords = this.$getProperty(data, 'failureCodeProblems', [])
      let problemRecord = this.problemRecords && this.problemRecords[0]
      this.currentId = parseInt(this.$route.query.selectedProblemId)
      this.problemRecords.forEach(record => {
        if (record?.id == this.currentId) {
          this.causesRecord = this.$getProperty(record, 'failureCodeCauses')
        }
      })
      if (!this.causesRecord.length) {
        this.currentId = problemRecord?.id
        this.causesRecord = this.$getProperty(
          problemRecord,
          'failureCodeCauses',
          []
        )
      }

      let causeRecord = this.causesRecord && this.causesRecord[0]
      this.currentCauseId = parseInt(this.$route.query.selectedCauseId)
      this.causesRecord.forEach(record => {
        if (record?.id == this.currentCauseId) {
          this.remediesRecord = this.$getProperty(record, 'failureCodeRemedies')
        }
      })
      if (!this.remediesRecord.length) {
        this.currentCauseId = causeRecord?.id
        this.remediesRecord = this.$getProperty(
          causeRecord,
          'failureCodeRemedies',
          []
        )
      }
      this.isProblemSelected = !isEmpty(this.currentId)
      this.isCauseSelected =
        !isEmpty(this.currentCauseId) && this.isProblemSelected
    },
    openFailureCodeForm(codeName, displayName, data) {
      if (!isEmpty(data)) {
        let { code_id } = data
        this.$set(this, 'codeId', code_id)
      } else {
        this.$set(this, 'codeId', null)
      }
      this.codeDisplayName = displayName
      this.codeName = codeName
      this.showCreateNewDialog = true
    },
    activeRecordId(item) {
      this.currentId = this.$getProperty(item, 'id')
      this.causesRecord = this.$getProperty(item, 'failureCodeCauses')
      this.currentCauseId = this.$getProperty(this, 'causesRecord.0.id')
      this.isProblemSelected = true
      if (this.causesRecord.length) {
        this.remediesRecord = this.$getProperty(
          this,
          'causesRecord.0.failureCodeRemedies'
        )
      } else this.remediesRecord = []
      this.isProblemSelected = !isEmpty(this.currentId)
      this.isCauseSelected =
        !isEmpty(this.currentCauseId) && this.isProblemSelected
    },
    activeCauseRecordId(item) {
      this.currentCauseId = this.$getProperty(item, 'id')
      this.remediesRecord = this.$getProperty(item, 'failureCodeRemedies')
      this.isProblemSelected = !isEmpty(this.currentId)
      this.isCauseSelected =
        !isEmpty(this.currentCauseId) && this.isProblemSelected
    },

    async deleteRecords(id, codeName, displayName) {
      let value = await this.$dialog.confirm({
        title: this.$t(`common.header.delete_record`),
        message: this.$t(
          `common._common.are_you_sure_want_to_delete_this_record`
        ),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let { error } = await API.deleteRecord(codeName, [id], {
          force: true,
        })
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success(`${displayName} deleted successfully`)
          this.loadRecords()
        }
      }
    },

    addFailureCodeDialog(value, displayName) {
      this.canShowWizard = true
      this.codeName = value
      this.codeDisplayName = displayName
    },
    async setSelectedFailureCodes(failurecodes) {
      if (!isEmpty(failurecodes)) {
        let { codeName } = this
        this.saving = true
        let field = this.$getProperty(failurecodes, 'field')
        if (codeName === 'failurecodeproblems') {
          this.saveProblem(field, failurecodes)
        } else if (codeName === 'failurecodecauses') {
          this.saveCause(field, failurecodes)
        } else if (codeName === 'failurecoderemedies') {
          this.saveRemedy(field, failurecodes)
        }
        this.saving = false
      } else {
        this.loadRecords()
      }
    },
    saveProblem(field = null, failurecodes = null) {
      let data = []
      let failureClassId = this.$getProperty(this, 'details.id', null)
      if (!isEmpty(field)) {
        let selectedData = this.$getProperty(field, 'selectedItems')
        data = selectedData.map((code, index) => {
          return {
            failureCode: {
              id: code.value,
            },
            failureClass: {
              id: failureClassId,
            },
          }
        })
      } else {
        data.push({
          failureCode: {
            id: failurecodes.id,
          },
          failureClass: {
            id: failureClassId,
          },
        })
      }
      let problem = {
        failurecodeproblems: data,
      }
      this.savePCRRecords(problem, 'failurecodeproblems')
    },
    saveCause(field = null, failurecodes = null) {
      let data = []
      if (!isEmpty(field)) {
        let selectedData = this.$getProperty(field, 'selectedItems')
        data = selectedData.map((code, index) => {
          return {
            failureCode: {
              id: code.value,
            },
            failureCodeProblems: {
              id: this.currentId,
            },
          }
        })
      } else {
        data.push({
          failureCode: {
            id: failurecodes.id,
          },
          failureCodeProblems: {
            id: this.currentId,
          },
        })
      }
      let cause = {
        failurecodecauses: data,
      }
      this.savePCRRecords(cause, 'failurecodecauses')
    },
    saveRemedy(field = null, failurecodes = null) {
      let data = []
      if (!isEmpty(failurecodes.field)) {
        let selectedData = this.$getProperty(field, 'selectedItems')
        data = selectedData.map((code, index) => {
          return {
            failureCode: {
              id: code.value,
            },
            failureCodeCauses: {
              id: this.currentCauseId,
            },
          }
        })
      } else {
        data.push({
          failureCode: {
            id: failurecodes.id,
          },
          failureCodeCauses: {
            id: this.currentCauseId,
          },
        })
      }
      let remedy = {
        failurecoderemedies: data,
      }
      this.savePCRRecords(remedy, 'failurecoderemedies')
    },
    async savePCRRecords(data, moduleName) {
      let url = 'v3/modules/data/bulkCreate'
      let params = {
        moduleName: moduleName,
        params: {
          return: true,
        },
      }
      params['data'] = { ...data }

      let { error } = await API.post(url, params)
      if (error) {
        this.$message.error(
          error.message || this.$t('common.wo_report.unable_to_update')
        )
      } else {
        this.$message.success(` ${this.codeDisplayName} added successfully!`)
        this.loadRecords()
      }
    },
    async loadRecords() {
      eventBus.$emit(
        'refresh-failureclass',
        this.currentId,
        this.currentCauseId,
        this.details.id
      )
    },
  },
}
</script>

<style lang="scss" scoped>
.details {
  margin: 14px 0px 0px 16px;
  font-size: 15px;
  font-weight: 450;
  color: #324056;
}
.associatedDetails {
  background-color: #fff;
}
.flex-container {
  min-height: 350px;
  margin: 0 auto;
  display: flex;
}
.card {
  margin-top: 16px;
}
</style>
