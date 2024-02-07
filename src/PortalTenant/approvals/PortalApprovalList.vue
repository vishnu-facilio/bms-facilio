<template>
  <div class="layout container new-approval-list">
    <div v-if="isSummaryOpen" class="d-flex height100">
      <!-- HALF VIEW WHEN SUMMARY OPEN-->
      <div class="flex-grow">
        <ApprovalSummary
          :key="selectedRecordId"
          :id="selectedRecordId"
          :moduleName="moduleName"
          :details="list.find(({ id }) => id === selectedRecordId)"
          :updateUrl="moduleUpdateUrl"
          :transformFn="moduleTransformFn"
          @onTransitionSuccesss="handleTransitionSuccess"
        ></ApprovalSummary>
      </div>
    </div>
    <div v-else>
      <!-- LIST VIEW -->
      <PageLayout>
        <template #views>
          <ApprovalViews
            :moduleName="moduleName"
            :maxVisibleMenu="3"
            :canShowViewsSidePanel.sync="canShowViewsSidePanel"
            :pathPrefix="pathPrefix"
          ></ApprovalViews>
        </template>
        <template #header>
          <template v-if="sortConfigList">
            <pagination
              v-if="listcount"
              :total="listcount"
              :perPage="perPage"
              class="pL15 fc-black-small-txt-12"
            ></pagination>

            <!-- portal to insert elements in header from child components-->
            <portal-target name="approval-list-top"></portal-target>
            <sort
              :sortList="sortConfigList"
              :config="sortConfig"
              :excludeFields="excludedSortFields"
              @onchange="updateSort"
            ></sort>
          </template>
          <AdvancedSearch
            :key="`${moduleName}-search`"
            :moduleName="moduleName"
            :moduleDisplayName="moduleDisplayName"
          >
          </AdvancedSearch>
        </template>
        <div class="width100">
          <FTags :key="`ftags-list-${moduleName}`" :hideSaveView="true"></FTags>
        </div>

        <div :class="['f-list-view height100vh', { m10: loading }]">
          <div v-if="loading" class="full-layout-white height100vh text-center">
            <spinner :show="loading" size="80"></spinner>
          </div>

          <component
            v-else
            :is="listComponent"
            :moduleName="moduleName"
            :list="list"
            :setSortConfig="setSortConfig"
            :setTransitionConfig="setTransitionConfig"
            :getApprovalStates="getApprovalStates"
            :selectedRecordIds="selectedRecordIds"
            :getBulkApprovalStates="getBulkApprovalStates"
            :startBulkUpdate="startBulkUpdate"
            :selectRecords="selectRecords"
            :selectRecord="selectRecord"
            :onSelectionChange="onSelectionChange"
            :formattedSelectionCount="formattedSelectionCount"
            :refreshAction="reload"
            :openSummary="openSummary"
            :currentView="currentView"
          ></component>
        </div>
      </PageLayout>

      <ApprovalForm
        v-if="canShowBulkUpdateForm"
        :moduleName="moduleName"
        :transition="bulkUpdateData.transition"
        :formId="bulkUpdateData.transition.formId"
        :approvalRule="bulkUpdateData.approvalRule"
        :isV3="true"
        :saveAction="handleBulkFieldUpdate"
        :closeAction="cancelBulkUpdate"
      >
      </ApprovalForm>
    </div>
  </div>
</template>
<script>
import ApprovalList from 'pages/approvals/ApprovalList'
import PageLayout from '../components/PageLayout'
import ApprovalViews from './PortalApprovalViews'
export default {
  extends: ApprovalList,
  components: { PageLayout, ApprovalViews },
}
</script>
<style lang="scss">
.new-approval-list {
  overflow: hidden;
}
</style>
