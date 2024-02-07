<template>
  <el-dialog
    :visible="true"
    :before-close="close"
    custom-class="section-settings-dialog"
    :show-close="false"
    :close-on-click-modal="false"
    :modal-append-to-body="false"
  >
    <span slot="title" class="section-dialog-title">Configure Group</span>
    <el-form :model="settings" ref="ruleForm" :label-position="'top'">
      <p class="dash-section-sub-title">
        Display options
      </p>
      <el-form-item>
        <p class="dash-section-input-label-txt mT10">
          Name
        </p>
        <el-input
          placeholder="Enter name"
          :autofocus="true"
          v-model="settings.name"
          class="width612 pR20 fc-input-full-border2"
        ></el-input>
      </el-form-item>
      <p class="dash-section-sub-title mT20">
        Theme
      </p>
      <el-form-item>
        <p class="dash-section-input-label-txt mT10">
          Background
        </p>
        <el-radio-group v-model="settings.banner_meta.type">
          <el-radio class="fc-radio-btn f13" label="color">Color</el-radio>
          <el-radio class="fc-radio-btn f13" label="banner">Banner</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="bannerType == 'banner'">
        <banner-picker v-model="settings.banner_meta.banner" />
      </el-form-item>
      <el-form-item v-if="bannerType == 'color'">
        <color-picker v-model="settings.banner_meta.color" />
      </el-form-item>
      <!-- <el-form-item class="mB10">
        <label class="dash-section-sub-title">
          Link to Dashboard
        </label>
        <el-switch
          v-model="settings.link.linkToDashboard"
          class="Notification-toggle mL30"
          active-color="rgba(57, 178, 194, 0.8)"
          inactive-color="#e5e5e5"
        ></el-switch>
      </el-form-item>
      <el-form-item v-if="settings.link.linkToDashboard == true">
        <p class="dash-section-input-label-txt">
          Select dashboard
        </p>
        <el-select
          v-model="settings.link.dashboard"
          placeholder="On Field Update"
          class="fc-input-full-border2 width100 pR20"
          filterable
        >
          <el-option
            v-for="(triggerTypeName, triggerType) in triggerTypeList"
            :key="`actionType-${triggerType}`"
            :label="triggerTypeName"
            :value="parseInt(triggerType)"
          ></el-option>
        </el-select>
      </el-form-item> -->
    </el-form>
    <span slot="footer" class="dash-section-footer-btn">
      <el-button
        size="medium"
        type="el-button  el-button--primary"
        class="mL10 dialog-btn confirm-btn"
        @click="save"
        >Save</el-button
      >

      <el-button
        size="medium"
        class="mR10 dialog-btn cancel-btn"
        @click="close"
      >
        Cancel
      </el-button>
    </span>
  </el-dialog>
</template>
<script>
import { cloneDeep } from 'lodash'
import ColorPicker from './ColorPicker.vue'
import BannerPicker from './BannerPicker.vue'

export default {
  components: {
    ColorPicker,
    BannerPicker,
  },
  props: {
    visibility: {
      type: Boolean,
      required: true,
    },
    section: {
      type: Object,
      required: true,
      default: () => ({
        name: 'Title',
        desc: 'Description',
        x: 0,
        y: 2,
        w: 96,
        h: 20,
        minW: 96,
        // link: {
        //   linkToDashboard: false,
        //   dashboard: '',
        // },
        maxW: 96,
        type: 'section',
        collapsed: false,
        banner_meta: {
          type: 'color',
          color: { label: 'blue', color: '#406dce', background: '#E9F0FF' },
          banner: {
            label: 'voilet',
            borderColor: '#bc5bb1',
            backgroundColor: '#FBF9FF',
          },
        },
        children: [],
      }),
    },
  },
  data() {
    return {
      triggerTypeList: [],
      settings: {},
    }
  },
  computed: {
    bannerType() {
      const {
        settings: {
          banner_meta: { type },
        },
      } = this
      return type ?? ''
    },
    isNewSection() {
      // New sections don't have id (Create mode), but the already existing sections have id (Edit mode).
      const {
        section: { id },
      } = this
      if (!id) {
        return true
      } else {
        return false
      }
    },
  },
  created() {
    this.settings = cloneDeep(this.section)
  },
  methods: {
    save() {
      const { isNewSection, settings } = this
      if (isNewSection) {
        this.$emit('createSection', settings)
      } else {
        this.$emit('updateSection', settings)
      }
      this.close()
    },
    close() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style lang="scss" scoped></style>
<style lang="scss">
.section-settings-dialog {
  border-radius: 5px;
  width: 714px;
  height: 612px;
}

.section-settings-dialog {
  .el-dialog__header {
    border-bottom: 1px solid #edeef0;
    padding-left: 32px;
    padding-top: 22px;
    height: 67px;
  }
  .el-dialog__footer {
    height: 76px;
    width: 714px;
    border-top: 1px solid #edeef0;
    padding-top: 20px;
  }
  .el-dialog__body {
    overflow: scroll;
    height: 469px;
    padding: 20px 30px 0px 30px !important;
  }
  .section-dialog-title {
    display: block;
    width: 149px;
    height: 18px;
    font-size: 16px;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: normal;
    letter-spacing: 0.5px;
    color: #324056;
  }
  .dash-section-sub-title {
    font-size: 14px;
    font-weight: 500;
    letter-spacing: 0.5px;
    /*color: #ef4f8f;*/
    color: var(--fc-theme-color);
    margin: 0;
  }
  .el-form-item {
    margin-bottom: 0px !important;
  }
  .dash-section-input-label-txt {
    font-size: 13px;
    font-weight: normal;
    letter-spacing: 0.5px;
    color: #324056 !important;
    margin: 0;
  }
  .el-radio__label {
    font-size: 13px;
    letter-spacing: 0.5px;
    color: #324056;
    font-weight: normal;
  }
  .fc-radio-btn .el-radio__inner {
    height: 16px !important;
    width: 16px !important;
  }
  .dash-section-footer-btn {
    float: right;
    padding-right: 10px;
  }
  .width612 {
    width: 612px;
  }
  .confirm-btn {
    background-color: rgb(57, 178, 194) !important;
    font-size: 13px;
    width: 84px;
  }
  .cancel-btn {
    width: 84px;
  }
}
</style>
