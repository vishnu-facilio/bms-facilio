<template>
  <div
    class="custom-modules-overview overflow-y-scroll"
    :class="!$validation.isEmpty(customClass) ? customClass : ''"
  >
    <div v-if="!isLoading" class="header pT10 pB15 pL20 pR20">
      <div v-if="!$validation.isEmpty(customModuleData)" class="asset-details">
        <div class="d-flex flex-middle">
          <div v-if="showPhotoField">
            <div v-if="customModuleData[photoFieldName]">
              <img
                :src="getImage(customModuleData[photoFieldName])"
                class="img-container"
              />
            </div>
            <div v-else-if="showAvatar">
              <avatar
                size="lg"
                :user="{ name: customModuleData.name }"
              ></avatar>
            </div>
          </div>
          <div class="mL5">
            <div class="asset-id mT10">
              <i
                v-if="$account.portalInfo"
                class="el-icon-back fc-grey3-text14 fw6 pR10 pointer"
                content="back"
                arrow
                v-tippy="{ animateFill: false, animation: 'shift-toward' }"
                @click="back"
              ></i>
              #{{ customModuleData.id }}
            </div>
            <div class="asset-name mb5 d-flex">
              {{ customModuleData[mainFieldKey] }}
              <div
                v-if="currentModuleState"
                class="fc-badge text-uppercase inline vertical-middle mL15"
              >
                {{ currentModuleState }}
              </div>
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
            :updateUrl="updateUrl"
            :transformFn="transformFormData"
            :disabled="isApprovalEnabled"
            buttonClass="asset-el-btn"
            @currentState="() => {}"
            @transitionSuccess="refreshObj()"
            @transitionFailure="() => {}"
          ></TransitionButtons>
        </template>

        <portal to="pagebuilder-sticky-top" v-if="isApprovalEnabled">
          <ApprovalBar
            :moduleName="moduleName"
            :key="customModuleData.id + 'approval-bar'"
            :record="customModuleData"
            :updateUrl="updateUrl"
            :transformFn="transformFormData"
            :hideApprovers="shouldHideApprovers"
            @onSuccess="refreshObj()"
            @onFailure="() => {}"
            class="approval-bar-shadow"
          ></ApprovalBar>
        </portal>

        <el-button
          type="button"
          v-if="showEdit"
          class="fc-wo-border-btn pL15 pR15 self-center"
          @click="editCustomModuleData(customModuleData)"
        >
          <i class="el-icon-edit" title="Edit" v-tippy></i>
        </el-button>
        <el-button
          type="button"
          v-if="
            moduleName === 'tenantcontact' || moduleName === 'vendorcontact'
          "
          class="fc-wo-border-btn pL15 pR15 self-center"
          @click="showDialog(customModuleData)"
        >
          <i class="el-icon-setting" title="Application Access" v-tippy></i>
        </el-button>
        <el-dropdown
          trigger="click"
          v-if="moduleName === 'employee'"
          class="fc-btn-ico-lg mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 pointer height40 mT4"
        >
          <div class="flex-middle" ref>
            <i class="el-icon-more pointer overview-icon-more-btn"></i>
            <span class="pointer mL-auto text-fc-pink child-add"></span>
          </div>

          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <div @click="showEmployeeactionDialog(customModuleData)">
                Application Access
              </div>
            </el-dropdown-item>
            <el-dropdown-item>
              <div @click="showDialog(customModuleData)">Portal Access</div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-dropdown
          class="mL10 custom-module-dropdown self-center"
          trigger="hover"
          @command="moduleName => openNewForm(moduleName)"
          v-if="!$validation.isEmpty(subModulesList)"
        >
          <el-button class="fc-wo-border-btn pL15 pR15" type="primary">
            <i class="el-icon-plus"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(module, index) in subModulesList"
              :key="index"
              :command="module.name"
            >
              {{
                `Convert to ${
                  $constants.moduleSingularDisplayNameMap[module.name]
                    ? $constants.moduleSingularDisplayNameMap[module.name]
                    : module.displayName
                }`
              }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <el-dropdown
          v-if="moduleName === 'tenant'"
          class="mL15 fc-btn-ico-lg pT5 pB5 pL8 pR8 mtb5 pointer"
          @command="handleCommand"
        >
          <span class="el-dropdown-link">
            <img src="~assets/menu.svg" height="18" width="18" />
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>Create Work Order</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <el-dropdown
        class="mL10 dashboard-dropdown-right p10 pR10 pL10 self-center bl-more-btn"
        @command="dashboardCommand"
        trigger="click"
      >
        <span class="el-dropdown-link">
          <i class="el-icon-more rotate-90 pointer f14 fc-grey6"></i>
        </span>
        <el-dropdown-menu slot="dropdown" class="dashboard-subheader-dp">
          <el-dropdown-item command="edit">
            <div>{{ 'Create bill' }}</div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <page
      v-if="customModuleData && customModuleData.id"
      :key="customModuleData.id"
      :module="moduleName"
      :id="customModuleData.id"
      :details="customModuleData"
      :primaryFields="primaryFields"
      :notesModuleName="notesModuleName"
      :attachmentsModuleName="attachmentsModuleName"
    ></page>
    <column-customization
      :visible.sync="canShowColumnCustomization"
      :moduleName="relatedListModuleName"
      :columnConfig="relatedListColumnConfig"
      :relatedViewDetail="relatedViewDetail"
      :relatedMetaInfo="relatedMetaInfo"
      :viewName="relatedViewName"
      @refreshRelatedList="refreshRelatedList"
    ></column-customization>
    <el-dialog
      :visible.sync="showDeleteDialog"
      class="dialog-d"
      custom-class="setup-dialog45"
      :show-close="false"
    >
      <div class="text-center fc-black-20">
        Do you want to delete or dissociate from
        {{ moduleName ? moduleName : '' }} ?
      </div>
      <span
        slot="footer"
        class="fc-dialog-center-container delete-dialog-footer padding-px18"
      >
        <el-button @click="showDeleteDialog = false">CANCEL</el-button>
        <el-button class="delete-dissociate-buttons" @click="dissociate()"
          >DISSOCIATE</el-button
        >
        <el-button class="delete-dissociate-buttons" @click="deleteRecord()"
          >MOVE TO RECYCLE BIN</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>
<script>
import Vendorsummary from 'pages/vendor/vendors/VendorSummary'
export default {
  extends: Vendorsummary,
  computed: {
    isLandloard() {
      if (
        this.customModuleData &&
        this.customModuleData.data['picklist'] &&
        this.customModuleData.data.picklist === 2
      ) {
        return true
      }
      return false
    },
  },
  methods: {
    dashboardCommand(modeule) {
      let { customModuleData } = this
      if (modeule === 'edit') {
        let routeData = this.$router.resolve({
          path: `/app/al/custom_utilitybills/create?supplierLabel=${customModuleData.name}&supplier=${customModuleData.id}`,
        })
        window.open(routeData.href, '_blank')
      }
    },
  },
}
</script>
<style>
.bl-more-btn {
  background: #fff;
  border-radius: 3px;
  /* box-shadow: 0 2px 4px 0 rgba(230, 230, 230, 0.5); */
  border: 1px solid #d9e0e7;
  color: #605e88;
  font-size: 14px;
}
</style>
