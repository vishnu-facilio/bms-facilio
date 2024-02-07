<template>
  <div id="building-detail-header">
    <el-header
      height="114px"
      class="site-header-scroll-show-header scroll-to-active hide"
      id="scroll-to-active"
    >
      <div class="site-header-scroll-show pT20 pB20">
        <div>
          <div class="fc-black-13 text-left">{{ `#${record.id}` }}</div>
          <div class="fc-black2-20 bold flex flex-direction-row align-center">
            <span
              v-if="decommission"
              v-tippy
              :title="$t('setup.decommission.decommissioned')"
              class="align-center mR3 flex pointer"
              ><fc-icon
                group="alert"
                class="pR3"
                name="decommissioning"
                size="20"
              ></fc-icon
            ></span>
            <span>{{ record.name }}</span>
            <span v-if="status" class="fc-newsummary-chip vertical-middle">
              {{ status }}
            </span>
          </div>
          <div
            v-if="!$validation.isEmpty(count)"
            class="flex-middle justify-content-space mT10"
          >
            <div class="flex-middle pT5">
              <div class="pR20 border-right1">
                <div class="label-txt-black">{{ count['floors'] }} Floor</div>
              </div>
              <div class="pL20 pR20 border-right1">
                <div class="label-txt-black">{{ count['spaces'] }} Spaces</div>
              </div>
              <div class="pL20">
                <div class="label-txt-black">
                  {{ count['independent_spaces'] }}
                  Independent Spaces
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="pR20 d-flex flex-shrink-0">
          <CustomButton
            :record="record"
            :moduleName="moduleName"
            :position="POSITION.SUMMARY"
            @refresh="refreshDetails"
            @onError="() => {}"
          />
          <TransitionButtons
            class="mL8"
            ref="TransitionButtons"
            buttonClass="portfolio-transition-button"
            :moduleName="moduleName"
            :record="record"
            :transformFn="transformFn"
            :updateUrl="updateUrl"
            :disabled="isApprovalEnabled"
            @transitionSuccess="refreshDetails"
            @transitionFailure="() => {}"
          ></TransitionButtons>

          <el-dropdown
            v-if="$hasPermission('space:CREATE')"
            class="mL8 pointer site-header-button mR10"
            trigger="click"
            @command="moduleName => openNewForm(moduleName)"
            :disabled="decommission"
          >
            <el-button type="primary">
              {{ $t('common._common.add')
              }}<i class="el-icon-arrow-down fc-white-12 pL10"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item :key="1" :command="'floor'">{{
                $t('space.sites.newfloor')
              }}</el-dropdown-item>
              <el-dropdown-item :key="2" :command="'space'">{{
                $t('space.sites.new_space')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <el-dropdown
            v-if="$hasPermission('space:UPDATE')"
            @command="action => dropDownAction(action)"
            trigger="click"
            class="building-header-dropdown"
          >
            <span class="el-dropdown-link mT4 pointer">
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
              <el-dropdown-item :key="2" :command="'edit'">
                {{ $t('space.sites.edit_building') }}
              </el-dropdown-item>
              <el-dropdown-item :key="3" :command="'download'">
                Download Qr
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <div class="d-flex fc-building-overview-header">
      <div>
        <div @click="openFilePreview()" v-if="record.avatarUrl" class="">
          <img :src="record.avatarUrl" class="building-img-container pointer" />
        </div>
        <div v-else-if="newSiteSummary" class="new-building-icon-container">
          <fc-icon group="default" name="building" size="70"></fc-icon>
        </div>
        <div v-else class="building-icon-container">
          <InlineSvg
            :src="`svgs/spacemanagement/building`"
            class="pointer"
            iconClass="icon icon-80"
          ></InlineSvg>
        </div>
      </div>
      <div class="pL20 width100">
        <div class="flex flex-row width100 justify-content-space">
          <div class="flex flex-col">
            <div class="label-txt-black d-flex flex-direction-row">
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
          <div
            class="d-flex flex-direction-column justify-content-space flex-shrink-0"
          >
            <div class="d-flex items-center">
              <CustomButton
                :record="record"
                :moduleName="moduleName"
                :position="POSITION.SUMMARY"
                @refresh="refreshDetails"
                @onError="() => {}"
              />
              <TransitionButtons
                class="mL8"
                ref="TransitionButtons"
                buttonClass="portfolio-transition-button"
                :moduleName="moduleName"
                :record="record"
                :transformFn="transformFn"
                :updateUrl="updateUrl"
                :disabled="isApprovalEnabled"
                @transitionSuccess="refreshDetails"
                @transitionFailure="() => {}"
              ></TransitionButtons>

              <el-dropdown
                class="mL8 pointer site-header-button mR10"
                trigger="click"
                @command="moduleName => openNewForm(moduleName)"
                v-if="$hasPermission('space:CREATE')"
                :disabled="decommission"
              >
                <el-button type="primary">
                  {{ $t('common._common.add')
                  }}<i class="el-icon-arrow-down fc-white-12 pL10"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item :key="1" :command="'floor'">{{
                    $t('space.sites.newfloor')
                  }}</el-dropdown-item>
                  <el-dropdown-item :key="2" :command="'space'">{{
                    $t('space.sites.new_space')
                  }}</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>

              <el-dropdown
                v-if="$hasPermission('space:UPDATE')"
                @command="action => dropDownAction(action)"
                trigger="click"
                class="building-header-dropdown"
              >
                <span class="el-dropdown-link mT4">
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
                  <el-dropdown-item :key="2" :command="'edit'">
                    {{ $t('space.sites.edit_building') }}
                  </el-dropdown-item>
                  <el-dropdown-item :key="3" :command="'download'">
                    Download Qr
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </div>
        </div>
        <div
          class="flex flex-row justify-content-space"
          v-if="!$validation.isEmpty(count)"
        >
          <div class="flex-middle justify-content-space mT10">
            <div class="flex-middle pT5">
              <div class="pR40 border-right-sep">
                <div class="count-text">{{ count['floors'] }}</div>
                <div class="count-details pT5">Floors</div>
              </div>
              <div class="pL40 pR40 border-right-sep">
                <div class="count-text">{{ count['spaces'] }}</div>
                <div class="count-details pT5">Spaces</div>
              </div>
              <div class="pL40">
                <div class="count-text">
                  {{ count['independent_spaces'] }}
                </div>
                <div class="count-details pT10">Independent Spaces</div>
              </div>
            </div>
          </div>
          <div class="text-align-right d-flex flex-direction-column mT-auto">
            <div
              @click="
                $helpers.openInMap(record.location.lat, record.location.lng)
              "
              v-if="record.location"
              class="location-text bold pointer"
            >
              <inline-svg
                src="svgs/maps"
                class="vertical-middle"
                iconClass="icon icon-sm"
              ></inline-svg>
              {{ record.location.name }}
            </div>
            <div v-if="record.area > 0" class="location-text pT10">
              {{
                `${record.area > 0 ? record.area : '---'} ${getUnit('area')}`
              }}
            </div>
          </div>
        </div>
      </div>
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
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
import SpacePhotoUpdater from './PhotosUpdater'
import SpaceHierarchyBreadcrumb from './SpaceHierarchyBreadcrumb'
import PreviewFile from '@/PreviewFile'
import SpaceMixin from '../helpers/SpaceHelper'
import TransitionButtons from '@/stateflow/TransitionButtons'
import { mapGetters, mapState } from 'vuex'
import CustomButton from '@/custombutton/CustomButton'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import { API } from '@facilio/api'
import { getApp, isWebTabsEnabled } from '@facilio/router'

export default {
  props: [
    'moduleName',
    'record',
    'transformFn',
    'updateUrl',
    'isApprovalEnabled',
    'refreshDetails',
    'canEdit',
  ],
  mixins: [SpaceMixin],
  components: {
    SpacePhotoUpdater,
    SpaceHierarchyBreadcrumb,
    PreviewFile,
    TransitionButtons,
    CustomButton,
  },
  data() {
    return {
      count: {},
      photos: [],
      imagePreviewVisibility: false,
      POSITION: POSITION_TYPE,
    }
  },
  created() {
    this.loadCardDetails()
    this.$store.dispatch('view/loadModuleMeta', this.moduleName)
    window.addEventListener('scroll', this.scrollFunction, true)
    window.addEventListener('scroll', this.scrollToView, true)
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.scrollFunction, true)
    window.removeEventListener('scroll', this.scrollToView, true)
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
    buildingQrPdfUrl() {
      let url
      let appName = getApp().linkName
      if (isWebTabsEnabled()) {
        url = `/${appName}/pdf/building/${this.record.id}`
      } else {
        url = `/app/pdf/building/${this.record.id}`
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
    loadCardDetails() {
      let { record } = this
      if (!isEmpty((record || {}).id)) {
        let params = {
          buildingId: (record || {}).id,
          fetchReportCardsMeta: ['spaceCount'],
        }
        API.post(`building/reportcards`, params).then(
          ({ data: { reports }, error }) => {
            if (!error) {
              if (reports) {
                this.count = reports
              }
            } else {
              this.$message.error(error.message || 'Error Occurred')
            }
          }
        )
      }
    },
    openNewForm(moduleName) {
      if (moduleName === 'floor') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          building: this.record,
          module: 'floor',
        })
      } else if (moduleName === 'space') {
        eventBus.$emit('openSpaceManagementForm', {
          isNew: true,
          visibility: true,
          module: 'space',
          building: this.record,
        })
      }
    },
    openEditForm() {
      eventBus.$emit('openSpaceManagementForm', {
        isNew: false,
        buildingObj: this.record,
        module: 'building',
        visibility: true,
      })
    },
    dropDownAction(action) {
      if (action === 'delete') {
        this.invokeDeleteDialog(this.siteData)
      } else if (action === 'changePhoto') {
        this.$refs['space-photos-updater'].open()
      } else if (action === 'edit') {
        this.openEditForm()
      } else if (action === 'download') {
        this.redirectToQrPage()
      }
    },
    redirectToQrPage() {
      window.open(this.buildingQrPdfUrl)
    },
    setPhotos(data) {
      this.$set(this, 'photos', data)
    },
    openFilePreview() {
      this.imagePreviewVisibility = true
    },
    scrollFunction() {
      if (
        document.body.scrollTop > 250 ||
        document.getElementById('building-detail-header').parentElement
          .scrollTop > 100
      ) {
        document.querySelector('.navbar').style.display = 'none'
        document.querySelector('.navbar').style.transitionDelay = '5s'
        document.querySelector('.navbar').style.opacity = '0'
      } else {
        document.querySelector('.navbar').style.display = 'block'
        document.querySelector('.navbar').style.transitionDelay = '5s'
        document.querySelector('.navbar').style.opacity = '1'
      }
    },
    scrollToView() {
      if (
        document.body.scrollTop > 130 ||
        document.getElementById('building-detail-header').parentElement
          .scrollTop > 100
      ) {
        document.querySelector('#scroll-to-active').style.display = 'block'
        document.querySelector('#scroll-to-active').style.zIndex = '1'
        document.querySelector('#scroll-to-active').style.transitionDelay = '5s'
        document.querySelector('#scroll-to-active').style.opacity = '1'
      } else {
        document.querySelector('#scroll-to-active').style.display = 'none'
        document.querySelector('#scroll-to-active').style.zIndex = '0'
        document.querySelector('#scroll-to-active').style.transitionDelay = '5s'
        document.querySelector('#scroll-to-active').style.opacity = '0'
      }
    },
  },
}
</script>

<style lang="scss" scoped>
#building-detail-header {
  .new-building-icon-container {
    background: #fff;
    width: 130px;
    height: 130px;
    display: flex;
    justify-content: center;
    align-items: center;
    border: solid 1px #dbdbf3;
    border-radius: 50%;
  }
  .building-header-dropdown {
    cursor: pointer;
    width: 50px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
    border: 1px solid #d0d9e2;
  }
}
</style>
