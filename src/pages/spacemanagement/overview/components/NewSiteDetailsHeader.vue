<template>
  <div class="fc-v1-site-page wrapper" id="site-header-wrapper">
    <!-- <div class="banner"></div> -->
    <el-header
      height="101px"
      class="site-header-scroll-show-header scroll-to-active"
      id="scroll-to-active"
    >
      <div class="site-header-scroll-show slideInDown scroll-to-active">
        <div class="site-header-main-content">
          <div>
            <div @click="openFilePreview()" v-if="record.avatarUrl">
              <img :src="record.avatarUrl" class="site-img-container pointer" />
            </div>
            <div v-else class="site-icon-container">
              <fc-icon group="default" name="site" size="30"></fc-icon>
            </div>
          </div>
          <div>
            <div class="fc-black-13 text-left">{{ `#${record.id}` }}</div>
            <div class="fc-black2-20 bold flex flex-direction-row align-center">
              <span
                v-if="decommission"
                v-tippy
                :title="$t('setup.decommission.decommissioned')"
                class="align-center mR5 flex pointer"
                ><fc-icon
                  group="alert"
                  class="pR5"
                  name="decommissioning"
                  size="20"
                ></fc-icon
              ></span>
              <span>{{ record.name }}</span>
              <span v-if="status" class="fc-newsummary-chip vertical-middle">
                {{ status }}
              </span>
            </div>
            <div class="flex-middle pT10">
              <div class="pR10 border-right1">
                <div class="grey-text">
                  {{ $t('space.sites.buildings') }} {{ count['buildings'] }}
                </div>
              </div>
              <div class="pL10 pR10 border-right1">
                <div class="grey-text">
                  {{ $t('space.sites.spaces') }} {{ count['allSpaces'] }}
                </div>
              </div>
              <div class="pL10 pR10">
                <div class="grey-text">
                  {{ area }}
                  <!-- {{ count['independent_spaces'] }}
                  {{ $t('space.sites.independent_spaces') }} -->
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="flex-middle justify-content-end p30 flex-shrink-0">
          <div
            :class="[
              'z-10 d-flex',
              this.$helpers.isPortalUser() && !$hasPermission('space:CREATE')
                ? 'margin-right-60'
                : '',
            ]"
          >
            <TransitionButtons
              class="mL10"
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

            <el-dropdown
              class="mL10 pointer site-header-button mR10"
              trigger="click"
              v-if="$hasPermission('space:CREATE')"
              @command="moduleName => openNewForm(moduleName)"
              :disabled="decommission"
            >
              <el-button type="primary" class="" style="width: inherit;">
                {{ $t('common._common.add')
                }}<i class="el-icon-arrow-down pL10 fc-white-12"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :key="1" :command="'building'">{{
                  $t('space.sites.new_building')
                }}</el-dropdown-item>
                <el-dropdown-item :key="2" :command="'space'">{{
                  $t('space.sites.new_space')
                }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>

            <el-button-group
              v-if="$hasPermission('space:UPDATE')"
              class="fc-btn-group-white site-action-btn"
            >
              <el-button
                v-if="canEdit"
                @click="openSiteEditForm"
                type="primary"
              >
                <fc-icon group="default" name="edit-solid" size="25"></fc-icon>
              </el-button>
              <el-button type="primary">
                <el-dropdown @command="action => dropDownAction(action)">
                  <span class="el-dropdown-link">
                    <fc-icon
                      group="default"
                      name="ellipsis-horizontal"
                      size="25"
                      class="bold"
                    ></fc-icon>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item :key="2" :command="'changePhoto'">
                      {{ $t('space.sites.add_photo') }}
                    </el-dropdown-item>
                    <el-dropdown-item :key="3" :command="'siteQrDownload'">
                      {{ $t('space.sites.downlaod_qr') }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </el-button>
            </el-button-group>
          </div>
        </div>
      </div>
    </el-header>
    <SpacePhotoUpdater
      ref="space-photos-updater"
      :record="record"
      :photosModuleName="'basespacephotos'"
      @photosList="data => setPhotos(data)"
    ></SpacePhotoUpdater>
    <PreviewFile
      :visibility.sync="imagePreviewVisibility"
      v-if="imagePreviewVisibility"
      :previewFile="getFormattedFile[photoPreviewIndex]"
      :files="getFormattedFile"
    ></PreviewFile>
    <iframe
      v-if="downloadQRurl"
      :src="downloadQRurl"
      style="display: none;"
    ></iframe>
  </div>
</template>
<script>
import SiteDetailsHeader from './SiteDetailsHeader.vue'

export default {
  extends: SiteDetailsHeader,
  computed: {
    area() {
      return `${
        this.record?.grossFloorArea > 0 ? this.record?.grossFloorArea : '---'
      } ${this.getUnit('grossFloorArea')} / ${
        this.record?.area > 0 ? this.record?.area : '---'
      } ${this.getUnit('area')}`
    },
    decommission() {
      return this.$getProperty(this, 'record.decommission', false)
    },
  },
  methids: {
    clientPortalUser() {
      if (this.$helpers.isPortalUser()) {
        return true
      } else {
        return false
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.fc-v1-site-page {
  .site-header-main-content {
    display: flex;
    .site-img-container {
      width: 70px;
      height: 70px;
      border: 1px solid #f9f9f9;
      border-radius: 50%;
      margin-right: 15px;
    }
    .site-icon-container {
      width: 60px;
      height: 60px;
      display: flex;
      justify-content: center;
      align-items: center;
      border: solid 1px #dbdbf3;
      background-color: #ffffff;
      border-radius: 50%;
      margin-right: 15px;
    }
    .grey-text {
      font-size: 14px;
      color: #9ba3ad;
      letter-spacing: 0.16px;
    }
  }
  .margin-right-60 {
    margin-right: 60%;
  }
}
.banner {
  width: 100% !important;
  height: 50px !important;
  background-color: blue !important;
}
</style>

<style lang="scss">
.portfolio-transition-button {
  max-width: 184px !important;
}
</style>
