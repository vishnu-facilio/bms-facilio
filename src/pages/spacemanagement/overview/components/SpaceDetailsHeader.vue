<template>
  <div
    class="fc-space-overview-header space-detail-header fc-v1-building-slider-content"
  >
    <div class="d-flex">
      <div style="margin-top: -5px;">
        <div @click="openFilePreview()" v-if="record.avatarUrl">
          <img :src="record.avatarUrl" class="floor-img-container pointer" />
        </div>
        <div v-else-if="newSiteSummary" class="new-space-icon-container">
          <fc-icon group="default" name="workspace" size="50"></fc-icon>
        </div>
        <div class="space-icon-container" v-else>
          <InlineSvg
            :src="`svgs/spacemanagement/space`"
            iconClass="icon icon-xxllg"
          ></InlineSvg>
        </div>
      </div>
      <div class="pL20">
        <div class="d-flex flex-direction-row label-txt-black">
          <SpaceHierarchyBreadcrumb :record="record"></SpaceHierarchyBreadcrumb>
        </div>
        <div class="fc-id text-left pT10">
          {{ `#${record.id}` }}
        </div>
        <div class="fc-black2-22 flex flex-direction-row align-center">
          <span
            v-if="decommission"
            v-tippy
            :title="$t('setup.decommission.decommissioned')"
            class="align-center mR5 flex pointer"
            ><fc-icon
              group="alert"
              class="pR5"
              name="decommissioning"
              size="18"
            ></fc-icon
          ></span>
          <span>{{ record.name }}</span>
          <span v-if="status" class="fc-newsummary-chip vertical-middle">
            {{ status }}
          </span>
        </div>
      </div>
    </div>

    <div class="d-flex flex-shrink-0">
      <TransitionButtons
        class="mR10 flex-align-start"
        ref="TransitionButtons"
        :moduleName="moduleName"
        :record="record"
        :transformFn="transformFn"
        :updateUrl="updateUrl"
        :disabled="isApprovalEnabled"
        buttonClass="portfolio-transition-button"
        @transitionSuccess="onTransitionSuccess"
        @transitionFailure="() => {}"
      ></TransitionButtons>
      <div
        v-if="$hasPermission('space:CREATE') && !record.space5"
        class="site-header-button mR15"
      >
        <el-button
          type="primary"
          @click="openNewForm('space')"
          :disabled="decommission"
        >
          {{ $t('space.sites.new_sub_space') }}
        </el-button>
      </div>
      <el-button-group
        v-if="$hasPermission('space:UPDATE')"
        class="fc-btn-group-white site-action-btn"
      >
        <el-button
          v-if="canEdit"
          @click="openEditForm"
          type="primary"
          icon="el-icon-edit"
        ></el-button>
        <el-button type="primary">
          <el-dropdown @command="action => dropDownAction(action)">
            <span class="el-dropdown-link">
              <inline-svg
                src="svgs/menu"
                class="vertical-middle"
                iconClass="icon icon-sm"
              >
              </inline-svg>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :key="2" :command="'changePhoto'">{{
                $t('space.sites.add_photo')
              }}</el-dropdown-item>
            </el-dropdown-menu>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :key="3" :command="'downloadQr'">
                Download Qr
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-button>
      </el-button-group>
    </div>

    <SpacePhotoUpdater
      ref="space-photos-updater"
      :record="record"
      :photosModuleName="'basespacephotos'"
      @photosList="data => setPhotos(data)"
    ></SpacePhotoUpdater>
    <PreviewFile
      :visibility.sync="imagePreviewVisibility"
      v-if="imagePreviewVisibility"
      :previewFile="getFormattedFile[0]"
      :files="getFormattedFile"
    ></PreviewFile>
  </div>
</template>
<script>
import { eventBus } from '@/page/widget/utils/eventBus'
import SpacePhotoUpdater from './PhotosUpdater'
import SpaceHierarchyBreadcrumb from './SpaceHierarchyBreadcrumb'
import PreviewFile from '@/PreviewFile'
import SpaceMixin from '../helpers/SpaceHelper'
import TransitionButtons from '@/stateflow/TransitionButtons'
import { mapGetters } from 'vuex'
import { getApp, isWebTabsEnabled } from '@facilio/router'

export default {
  props: [
    'moduleName',
    'record',
    'transformFn',
    'updateUrl',
    'isApprovalEnabled',
    'onTransitionSuccess',
    'canEdit',
  ],
  mixins: [SpaceMixin],
  components: {
    SpaceHierarchyBreadcrumb,
    SpacePhotoUpdater,
    PreviewFile,
    TransitionButtons,
  },
  data() {
    return {
      photos: [],
      imagePreviewVisibility: false,
    }
  },
  computed: {
    ...mapGetters(['getTicketStatus']),
    siteId() {
      return this.$route.params.siteid
    },
    status() {
      let { record, moduleName, $getProperty, getTicketStatus } = this
      let status =
        $getProperty(record, 'moduleState.id', null) &&
        getTicketStatus(record.moduleState.id, moduleName)

      return status ? status.displayName : null
    },
    decommission() {
      return this.$getProperty(this, 'record.decommission', false)
    },
    spaceQrPdfUrl() {
      let url
      let appName = getApp().linkName
      if (isWebTabsEnabled()) {
        url = `/${appName}/pdf/space/${this.record.id}`
      } else {
        url = `/app/pdf/space/${this.record.id}`
      }
      return window.location.protocol + '//' + window.location.host + url
    },
  },
  methods: {
    openNewForm(moduleName) {
      if (moduleName === 'space') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'space',
          spaceParent: this.record,
        })
      }
    },
    openEditForm() {
      let spaceParentObj =
        this.record.spaceParent && this.record.spaceParent.id
          ? this.record.spaceParent
          : this.record.space4 && this.record.space4.id
          ? this.record.space4
          : this.record.space3 && this.record.space3.id
          ? this.record.space3
          : this.record.space2 && this.record.space2.id
          ? this.record.space2
          : this.record.space1 && this.record.space1.id
          ? this.record.space1
          : null
      eventBus.$emit('openSpaceManagementForm', {
        isNew: false,
        spaceobj: this.record,
        module: 'space',
        visibility: true,
        site: this.record.site,
        building: this.record.building,
        floor: this.record.floor,
        spaceParent: spaceParentObj,
        isSummaryEdit: true,
      })
    },
    dropDownAction(action) {
      if (action === 'delete') {
        this.invokeDeleteDialog(this.siteData)
      } else if (action === 'changePhoto') {
        this.$refs['space-photos-updater'].open()
      } else if (action === 'downloadQr') {
        this.redirectToQrPage()
      }
    },
    redirectToQrPage() {
      window.open(this.spaceQrPdfUrl)
    },
    setPhotos(data) {
      this.$set(this, 'photos', data)
    },
    openFilePreview() {
      this.imagePreviewVisibility = true
    },
  },
}
</script>

<style lang="scss" scoped>
.space-detail-header {
  .new-space-icon-container {
    width: 75px;
    height: 75px;
    display: flex;
    justify-content: center;
    align-items: center;
    border: solid 1px #dbdbf3;
    background-color: #fff;
    border-radius: 50%;
  }
}
</style>
