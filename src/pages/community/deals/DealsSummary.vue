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
      <template v-if="!$validation.isEmpty(imageAttachments)">
        <el-carousel
          trigger="click"
          :autoplay="false"
          indicator-position="none"
          class="f-page-dialog-carousel"
        >
          <el-carousel-item v-for="image in imageAttachments" :key="image.id">
            <el-image
              :key="record.id + image.id"
              :src="image ? $prependBaseUrl(image.previewUrl) : ''"
              class="width100 height100"
              fit="cover"
            >
              <div slot="error">
                <div>
                  <InlineSvg
                    src="svgs/photo"
                    iconClass="icon fill-grey icon-xxlll op5"
                  ></InlineSvg>
                </div>
              </div>
            </el-image>
          </el-carousel-item>
        </el-carousel>
      </template>
      <div class="header-section border-bottom1px">
        <div class="d-flex flex-col">
          <div class="record-id bold">#{{ record.id }}</div>
          <div class="record-name">
            <div class="inline mR15">{{ record.title }}</div>
            <div
              v-if="moduleState"
              class="fc-badge text-uppercase inline vertical-middle"
            >
              {{ moduleState }}
            </div>
          </div>
          <div class="deals-summary-header mT5">
            <div v-if="record.expiryDate" class="expiry-date">
              Expires on
              {{ $options.filters.formatDate(record.expiryDate, true) }}
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
              <el-dropdown-item v-if="hasUpdatePermission" command="edit">
                Edit
              </el-dropdown-item>
              <el-dropdown-item v-if="hasDeletePermission" command="delete">
                Delete
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
        :notesModuleName="notesModuleName"
        :attachmentsModuleName="attachmentsModuleName"
        :isSidebarView="true"
        :skipMargins="true"
        :hideScroll="true"
      ></page>
    </div>
  </el-dialog>
</template>
<script>
import Summary from 'pages/community/announcements/AnnouncementSummary'

export default {
  extends: Summary,
  computed: {
    listRouteName() {
      return 'dealsandoffersList'
    },
    editRouteName() {
      return 'edit-dealsandoffers'
    },
  },
}
</script>
