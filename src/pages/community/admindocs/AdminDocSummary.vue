<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog f-page-dialog-right"
    :before-close="goToList"
  >
    <div v-if="loading" class="height100 flex-middle">
      <spinner :show="true" size="80"></spinner>
    </div>
    <div v-else class="container pB50">
      <div class="header-section border-bottom1px">
        <div class="d-flex flex-col">
          <div class="record-id bold">#{{ record.id }}</div>
          <div class="record-name">
            {{ record.title }}
            <div
              v-if="moduleState"
              class="fc-badge text-uppercase inline vertical-middle mL15"
            >
              {{ moduleState }}
            </div>
          </div>
        </div>
        <div class="header-actions d-flex flex-row align-center">
          <template v-if="isStateFlowEnabled">
            <TransitionButtons
              class="mR10"
              :key="record.id"
              :moduleName="moduleName"
              :record="record"
              buttonClass="asset-el-btn"
              @currentState="() => {}"
              @transitionSuccess="loadData(true)"
              @transitionFailure="() => {}"
            ></TransitionButtons>
          </template>
          <el-dropdown
            v-if="canShowActionButtons"
            class="dialog-dropdown"
            @command="dropdownActionHandler"
          >
            <span class="el-dropdown-link">
              <InlineSvg src="menu" iconClass="icon icon-md"></InlineSvg>
            </span>
            <el-dropdown-menu slot="dropdown">
              <template v-if="hasUpdatePermission">
                <el-dropdown-item command="edit">
                  {{ $t('common._common.edit') }}
                </el-dropdown-item>
              </template>
              <el-dropdown-item v-if="hasDeletePermission" command="delete">
                {{ $t('common._common.delete') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <page
        v-if="record && record.id"
        :key="record.id"
        :module="moduleName"
        :id="record.id"
        :details="record"
        :primaryFields="primaryFields"
        :isSidebarView="true"
        :skipMargins="true"
        :hideScroll="true"
        :isV3Api="true"
      ></page>
    </div>
  </el-dialog>
</template>
<script>
import Summary from 'pages/community/announcements/AnnouncementSummary'
export default {
  extends: Summary,
  props: {
    moduleName: {
      type: String,
      default: 'admindocuments',
    },
  },
  computed: {
    listRouteName() {
      return 'list-admindocs'
    },
    editRouteName() {
      return 'edit-admindocs'
    },
    primaryFields() {
      return ['title', 'description']
    },
  },
}
</script>
``
