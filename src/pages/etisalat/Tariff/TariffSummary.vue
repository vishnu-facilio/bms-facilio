<template>
  <!-- OverWiriting For Header -->
  <div
    class="custom-modules-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="!isLoading" class="header pT10 pB15 pL20 pR20 border-bottom18">
      <div v-if="!$validation.isEmpty(customModuleData)" class="asset-details">
        <div class="d-flex flex-middle">
          <div class="mL5">
            <div class="asset-id mT10">#{{ customModuleData.id }}</div>
            <div class="asset-name pT5">
              {{ customModuleData[mainFieldKey] }}
            </div>
          </div>
        </div>
      </div>
      <div class="d-flex flex-direction-row" style="margin-left: auto;">
        <template v-if="isStateFlowEnabled">
          <TransitionButtons
            class="mR10"
            :key="customModuleData.id"
            :moduleName="moduleName"
            :record="customModuleData"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="refreshObj(true)"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>

        <el-dropdown
          class="mL10 self-center fc-btn-ico-lg pT3 pB3 pL8 pR8 pointer"
          trigger="click"
          @command="action => summaryDropDownAction(action)"
        >
          <span class="el-dropdown-link">
            <inline-svg
              src="svgs/menu"
              class="vertical-middle"
              iconClass="icon icon-md"
            >
            </inline-svg>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item :key="1" :command="'edit'">Edit</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <page
      v-if="!isLoading && customModuleData && customModuleData.id"
      :key="customModuleData.id"
      :module="moduleName"
      :id="customModuleData.id"
      :details="customModuleData"
      :primaryFields="primaryFields"
      :notesModuleName="notesModuleName"
      :attachmentsModuleName="attachmentsModuleName"
    ></page>
  </div>
</template>
<script>
import CustomModuleOverview from 'pages/base-module-v2/Overview'
export default {
  extends: CustomModuleOverview,
}
</script>
