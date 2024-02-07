<template>
  <div
    class="custom-module-overview"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="isLoading" class="flex-middle fc-empty-white">
      <spinner :show="isLoading" size="80"></spinner>
    </div>
    <template v-else-if="!$validation.isEmpty(record)">
      <div class="header p20 flex-center-row-space">
        <div class="fL mL10 custom-module-details flex-center-column">
          <div class="custom-module-id">#{{ record && record.id }}</div>
          <div class="custom-module-name mT5">
            {{ record['name'] }}
          </div>
        </div>
        <div class="marginL-auto flex-middle">
          <CustomButton
            class="pR10"
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshData()"
            @onError="() => {}"
          />
          <div class="marginL-auto flex-middle">
            <div>
              <el-button
                class="fc-wo-border-btn pL15 pR15 self-center"
                style="padding: 1em;margin-right: 20px;"
                @click="openFailureClassForm(record)"
              >
                <i class="el-icon-edit"></i>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <Page
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :notesModuleName="notesModuleName"
        :isV3Api="true"
        :attachmentsModuleName="attachmentsModuleName"
      ></Page>

      <new-failure-class
        v-if="showCreateNewDialog"
        ref="edit-failure-class"
        :canShowFormCreation="showCreateNewDialog"
        moduleName="failureclass"
        :dataId="failureClassId"
        :moduleDisplayName="'Failure Class'"
        @closeDialog="loadData"
      ></new-failure-class>
    </template>
  </div>
</template>
<script>
import CustomModuleSummary from 'pages/custom-module/CustomModuleSummary'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import NewFailureClass from 'src/pages/assets/failureclass/forms/NewFailureClass.vue'

export default {
  extends: CustomModuleSummary,
  components: {
    NewFailureClass,
  },
  data() {
    return {
      showSiteDialog: false,
      notesModuleName: 'failureclassnotes',
      attachmentsModuleName: 'failureclassattachments',
      saving: false,
      recordDetails: {},
      primaryFields: [
        'description',
        'sysCreatedBy',
        'sysCreatedTime',
        'sysModifiedBy',
        'sysModifiedTime',
      ],
      editedFailureClass: null,
      isNewFailureClass: true,
      showCreateNewDialog: false,
      failureClassId: null,
    }
  },
  computed: {
    moduleName() {
      return 'failureclass'
    },
  },
  mounted() {
    eventBus.$on(
      'refresh-failureclass',
      (selectedProblemId = null, selectedCauseId = null, id) => {
        this.loadRecord(selectedProblemId, selectedCauseId, id)
      }
    )
  },
  methods: {
    openFailureClassForm(data) {
      if (!isEmpty(data)) {
        this.$set(this, 'failureClassId', this.id)
      } else {
        this.$set(this, 'failureClassId', null)
      }
      this.showCreateNewDialog = true
    },
    loadData() {
      this.showCreateNewDialog = false
      this.refreshData()
    },
    async loadRecord(selectedProblemId, selectedCauseId, splId, force = true) {
      this.isLoading = true
      let selectedIds = { selectedProblemId, selectedCauseId }
      let { moduleName, id } = this
      id = splId ? splId : id
      this.record = await this.modelDataClass.fetch({ moduleName, id, force })
      await this.getFormMeta()
      let query = this.$route.query
      this.$router.push({
        path: '',
        query: { ...query, ...selectedIds },
      })
      this.isLoading = false
    },
  },
}
</script>
<style>
.description_fc {
  opacity: 0.5;
  font-size: 13px;
  letter-spacing: 0.16px;
  color: #324056;
}
</style>
