<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog fc-tabs-layout-page"
    style="z-index: 999999"
    :before-close="closeDialog"
  >
    <el-form
      :model="groupDetails"
      :rules="rules"
      :label-position="'top'"
      ref="webtab-group"
      class="fc-form webtab-group"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              isNew
                ? $t('common._common.add_tab_group')
                : $t('common.products.edit_tab_group')
            }}
          </div>
        </div>
      </div>

      <div class="new-body-modal enpi-body-modal">
        <div class="body-scroll">
          <el-row>
            <el-col :span="24">
              <el-form-item prop="name">
                <p class="fc-input-label-txt">
                  {{ $t('common._common.name') }}
                </p>

                <el-input
                  class="width100 fc-input-full-border2"
                  autofocus
                  v-model="groupDetails.name"
                  type="text"
                  :placeholder="$t('common._common.enter_group_name')"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row v-if="!isNew">
            <el-col :span="24">
              <el-form-item prop="route">
                <p class="fc-input-label-txt">
                  {{ $t('common.wo_report.route') }}
                </p>

                <el-input
                  class="width100 fc-input-full-border2 fc-tab-route-input"
                  autofocus
                  v-model="groupDetails.route"
                  type="text"
                  :placeholder="$t('common._common.enter_group_route')"
                >
                  <template slot="prepend">{{ appUrl }}</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row class="mT10">
            <el-col :span="24">
              <el-form-item prop="iconType">
                <p class="icon-header">
                  {{ $t('common.visitor_forms.choose_icon') }}
                </p>

                <p class="icon-header-content">
                  {{ $t('common.products.select_an_icon_for_this_tab_group') }}
                </p>

                <div class="flex-container">
                  <div
                    v-for="(iconObj, iconType) in icons"
                    :key="iconType"
                    @click="setGroupIcon(iconType)"
                  >
                    <inline-svg
                      v-if="getIconType(iconType)"
                      :src="iconObj.icon"
                      :iconClass="`icon pointer ${iconObj.class || ''}`"
                      :class="getInlineClass(iconType)"
                    ></inline-svg>
                  </div>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="submitForm()"
          :loading="saving"
        >
          {{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import icons from 'newapp/webtab-icons.js'
import { API } from '@facilio/api'
import clone from 'lodash/clone'

const appLayoutTypes = {
  SINGLE: 1,
  DUAL: 2,
}

export default {
  props: [
    'activeGroup',
    'isNew',
    'linkName',
    'layoutId',
    'appLayoutType',
    'currentAppDomain',
  ],
  data() {
    return {
      groupDetails: {
        name: null,
        iconType: 0,
      },
      saving: false,
      icons,
      appLayoutTypes,
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
      },
    }
  },

  created() {
    if (!this.isNew) {
      this.groupDetails = clone(this.activeGroup)
    }
  },

  computed: {
    appUrl() {
      let { currentAppDomain, linkName } = this
      return `https://${currentAppDomain}/${linkName}/`
    },
  },

  methods: {
    getInlineClass(type) {
      let iconType = parseInt(type)
      let iconClass = 'icon-border  pointer'

      if (this.groupDetails.iconType === iconType) iconClass += ' active'

      return iconClass
    },
    setGroupIcon(iconType) {
      this.groupDetails.iconType = parseInt(iconType)
    },
    getIconType(iconType) {
      let { appLayoutType, appLayoutTypes } = this

      if (appLayoutType == appLayoutTypes.SINGLE) {
        return iconType > 99 && iconType < 300
      } else if (appLayoutType == appLayoutTypes.DUAL) {
        return iconType > 0 && iconType < 100
      }
    },
    submitForm() {
      this.$refs['webtab-group'].validate(valid => {
        if (!valid) return false

        let { layoutId, groupDetails, isNew } = this
        let { name, route, iconType, id } = groupDetails

        if (isNew) {
          route = name.replace(/\W+/g, '-').toLowerCase()
        }

        let params = {
          tabGroup: { name, route, iconType, layoutId, id },
        }

        this.saving = true
        API.post('/v2/tabGroup/addOrUpdate', params).then(({ error, data }) => {
          if (error) {
            this.saving = false
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.$message.success(
              this.$t('common.header.group_added_or_updated')
            )
            this.$emit('onSave', data.webTabGroup)
            this.$emit('reload')
            this.saving = false
            this.closeDialog()
          }
        })
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.webtab-group {
  .icon-header {
    font-weight: normal;
    letter-spacing: 1px;
    color: #ee508f;
    margin: 0;
    font-size: 12px;
    text-transform: uppercase;
    font-weight: bold;
  }
  .icon-header-content {
    font-size: 13px;
    letter-spacing: 0.46px;
    color: #6b7e91;
  }
  .icon-border {
    border-radius: 8px;
    border: solid 1px #d0d9e2;
    padding: 20px;
    height: 67px;
    width: 67px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 10px;
  }
  .icon-border.active {
    box-shadow: inset 0 0 5px 0 rgba(57, 178, 194, 0.5);
    border: solid 1px rgba(57, 178, 194, 0.5);
  }
}
.fc-tabs-layout-page {
  .fc-tab-route-input {
    .el-input__inner {
      border-left: none !important;
      border-top-left-radius: 0 !important;
      border-bottom-left-radius: 0 !important;
    }
  }
}
</style>
