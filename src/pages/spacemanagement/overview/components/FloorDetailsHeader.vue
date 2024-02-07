<template>
  <div>
    <div
      class="fc-building-overview-header floor-detail-header"
      style="padding: 30px 20px 20px;"
    >
      <div class="fc-v1-building-slider-content">
        <div class="d-flex justify-content-space">
          <div class="d-flex">
            <div>
              <div @click="openFilePreview()" v-if="record.avatarUrl">
                <img
                  :src="record.avatarUrl"
                  class="floor-img-container pointer"
                />
              </div>
              <div v-else-if="newSiteSummary" class="new-floor-icon-container">
                <fc-icon group="default" name="floorstack" size="50"></fc-icon>
              </div>
              <div
                v-else
                class="space-icon-container"
                style="margin-top: -5px;"
              >
                <InlineSvg
                  :src="`svgs/spacemanagement/floor`"
                  iconClass="icon icon-xxllg"
                ></InlineSvg>
              </div>
            </div>
            <div class="pL20">
              <div class="d-flex flex-direction-row label-txt-black">
                <SpaceHierarchyBreadcrumb
                  :record="record"
                ></SpaceHierarchyBreadcrumb>
              </div>
              <div class="fc-id text-left pT10">
                {{ `#${record.id}` }}
              </div>
              <div class="fc-black2-22 flex flex-direction-row align-center">
                <span
                  v-if="decommission"
                  v-tippy
                  :title="$t('setup.decommission.decommissioned')"
                  class="align-center flex pointer"
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
          <div
            class="d-flex flex-direction-column justify-content-space flex-shrink-0"
          >
            <div class="d-flex">
              <TransitionButtons
                class="mR10"
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
                v-if="$hasPermission('space:CREATE')"
                class="site-header-button mR10"
              >
                <el-button
                  type="primary"
                  @click="openNewForm('space')"
                  :disabled="decommission"
                >
                  {{ $t('space.sites.new_space') }}
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
                      <el-dropdown-item :key="2" :command="'changePhoto'">
                        {{ $t('space.sites.add_photo') }}
                      </el-dropdown-item>
                      <el-dropdown-item
                        v-if="record.defaultFloorPlanId < 1"
                        :key="3"
                        :command="'addFloorPlan'"
                        >Add Floor Plan</el-dropdown-item
                      >
                      <el-dropdown-item
                        v-else
                        :key="4"
                        :command="'setDefaultFloorPlan'"
                        >Manage Floor Plan</el-dropdown-item
                      >
                      <el-dropdown-item :key="5" :command="'downloadQr'"
                        >Download Qr</el-dropdown-item
                      >
                    </el-dropdown-menu>
                  </el-dropdown>
                </el-button>
              </el-button-group>
            </div>
            <div v-if="record.area > 0" class="text-align-right">
              <div class="location-text">
                {{
                  `Floor Area ${
                    record.area > 0 ? record.area : '---'
                  } ${getUnit('area')}`
                }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      custom-class="f-image-editor fc-dialog-center-container fp-adder"
      v-if="floorPlanView"
      :visible.sync="floorPlanView"
      :modal-append-to-body="false"
      :width="'50%'"
      :title="'Floor Plan'"
    >
      <floorplanAdd
        @close="closedialog"
        @openFloorPlanEditor="openFPEditor"
        @makedefault="makedefaultFp"
      ></floorplanAdd>
    </el-dialog>
    <SpacePhotoUpdater
      ref="space-photos-updater"
      :record="record"
      :photosModuleName="'basespacephotos'"
      @photosList="data => setPhotos(data)"
    ></SpacePhotoUpdater>
    <floorplan-builder
      v-if="floorPlanEditor"
      @close="closeBuilder"
      :floorId="record.id"
      :floorplanId="floorplanId"
    >
    </floorplan-builder>
    <set-default-floor-plan
      @close="closedialog"
      @setDefaultFloorplan="setDefaultFloorplan"
      @openFPEdit="openFloorPlanEdit"
      @newfloorplan="newFloorPlan"
      @deleteFloorplan="deleteFloorPLan"
      v-if="defaultFloorPlanSet"
      :floorobj="record"
    >
    </set-default-floor-plan>
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
import { isEmpty } from '@facilio/utils/validation'
import SpacePhotoUpdater from './PhotosUpdater'
import FloorplanAdd from 'pages/floorPlan/components/FloorPlanAdd'
import setDefaultFloorPlan from 'pages/floorPlan/components/setDefaultFloorPlanDialog'
import SpaceHierarchyBreadcrumb from './SpaceHierarchyBreadcrumb'
import PreviewFile from '@/PreviewFile'
import SpaceMixin from '../helpers/SpaceHelper'
import TransitionButtons from '@/stateflow/TransitionButtons'
import { mapGetters, mapState } from 'vuex'
import { API } from '@facilio/api'
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
    SpacePhotoUpdater,
    FloorplanAdd,
    FloorplanBuilder: () => import('pages/floorPlan/FloorPlanBuilder'), // for bundlesize reduction
    setDefaultFloorPlan,
    SpaceHierarchyBreadcrumb,
    PreviewFile,
    TransitionButtons,
  },
  data() {
    return {
      floorPlanView: false,
      defaultFloorPlanSet: false,
      floorPlanEditor: false,
      floorplanId: null,
      photos: [],
      imagePreviewVisibility: false,
    }
  },
  created() {
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
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
    floorQrPdfUrl() {
      let url
      let appName = getApp().linkName
      if (isWebTabsEnabled()) {
        url = `/${appName}/pdf/floor/${this.record.id}`
      } else {
        url = `/app/pdf/floor/${this.record.id}`
      }
      return window.location.protocol + '//' + window.location.host + url
    },
  },
  methods: {
    getUnit(fieldName) {
      let { metaInfo } = this
      if (!isEmpty(metaInfo)) {
        let areaField = metaInfo.fields.find(field => field.name === fieldName)
        if (!isEmpty(areaField) && !isEmpty(areaField.unit)) {
          return areaField.unit
        }
      }
      return 'sq. ft'
    },
    openNewForm(moduleName) {
      if (moduleName === 'space') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'space',
          floor: this.record,
        })
      }
    },
    openEditForm() {
      eventBus.$emit('openSpaceManagementForm', {
        isNew: false,
        floorObj: this.record,
        module: 'floor',
        visibility: true,
      })
    },
    dropDownAction(action) {
      if (action === 'delete') {
        this.invokeDeleteDialog(this.siteData)
      } else if (action === 'changePhoto') {
        this.$refs['space-photos-updater'].open()
      } else if (action === 'addFloorPlan') {
        this.floorPlanView = true
      } else if (action === 'setDefaultFloorPlan') {
        this.defaultFloorPlanSet = true
      } else if (action === 'downloadQr') {
        this.redirectToQrPage()
      }
    },
    redirectToQrPage() {
      window.open(this.floorQrPdfUrl)
    },
    closedialog() {
      this.floorPlanView = false
      this.defaultFloorPlanSet = false
    },
    openFPEditor(floorplan) {
      this.floorplanId = floorplan.id
      this.floorPlanEditor = true
    },
    closeBuilder() {
      this.floorPlanEditor = false
    },
    setPhotos(data) {
      this.$set(this, 'photos', data)
    },
    openFilePreview() {
      this.imagePreviewVisibility = true
    },
    setDefaultFloorplan(id) {
      this.$set(this.record, 'defaultFloorPlanId', id)
      this.closedialog()
      this.updateFloor()
    },
    makedefaultFp(value, id) {
      if (value && id) {
        this.setDefaultFloorplan(id)
      }
    },
    openFloorPlanEdit(id) {
      this.floorplanId = id
      this.closedialog()
      this.floorPlanEditor = true
    },
    newFloorPlan() {
      this.closedialog()
      this.floorPlanView = true
    },
    deleteFloorPLan() {
      this.closedialog()
      this.$message.success('Floor Plan Deleted')
    },
    updateFloor() {
      API.post('/floor/update', { floor: this.record }).then(({ error }) => {
        if (!error) {
          this.$message.success(this.$t('space.sites.floor_updated_success'))
        } else {
          this.$message.error(this.$t('space.sites.floor_updated_failed'))
        }
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.floor-detail-header {
  .new-floor-icon-container {
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
