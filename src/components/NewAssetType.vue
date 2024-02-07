<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div id="newworkordercategory">
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              !isNew
                ? $t('common.wo_report.edit_asset_type')
                : $t('common.wo_report.new_asset_type')
            }}
          </div>
        </div>
      </div>

      <div class="new-body-modal">
        <div class="setup-input-block">
          <p class="fc-input-label-txt">{{ $t('common._common.type') }}</p>
          <el-input
            v-model="name"
            :placeholder="$t('common._common.enter_the_type')"
            class="fc-input-full-border-select2"
          ></el-input>
        </div>
        <div class="setup-input-block mT20">
          <el-checkbox v-model="movable" />
          <label style="padding-left:10px;">{{
            $t('common._common.is_movable')
          }}</label>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('common.roles.cancel')
        }}</el-button>
        <el-button type="primary" @click="saveType" class="modal-btn-save">{{
          $t('common._common._save')
        }}</el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
export default {
  props: ['assetType', 'isNew', 'visibility'],
  data() {
    return {
      name: '',
      movable: false,
      currentModuleName: 'assettype',
    }
  },
  mounted() {
    this.initAssetType()
  },
  created() {
    this.$store.dispatch('loadAssetType')
  },
  methods: {
    initAssetType() {
      if (this.isNew) {
        this.name = ''
        this.movable = false
      } else {
        this.name = this.assetType?.value?.name
        this.movable = this.assetType?.value?.movable
        this.assetID = this.assetType?.value?.id
      }
    },
    createType(self, assetType) {
      API.createRecord(this.currentModuleName, { data: assetType }).then(
        (assetType, error) => {
          if (!error) {
            self.$message({
              message: self.$t('common.wo_report.asset_type_saved_success'),
              type: 'success',
            })

            self.closeDialog()
            // reload the list while closing the closing the dialog.
            this.$emit('reloadList')
          } else {
            self.$message({
              message: self.$t('common._common.failed_to_save_asset_type'),
              type: 'error',
            })
          }
        }
      )
    },
    updateType(self, assetType) {
      API.updateRecord(this.currentModuleName, {
        id: this.assetID,
        data: assetType,
      }).then((assetType, error) => {
        if (!error) {
          self.$message({
            message: self.$t('common.wo_report.assets_type_updated_sucess'),
            type: 'success',
          })

          self.closeDialog()
          // reload the list while closing the closing the dialog.
          this.$emit('reloadList')
        } else {
          self.$message({
            message: self.$t('common._common.failed_to_update_asset_type'),
            type: 'error',
          })
        }
      })
    },
    saveType() {
      let self = this
      let assetType = { name: this.name, movable: this.movable }

      if (this.isNew) {
        this.createType(self, assetType)
      } else {
        this.updateType(self, assetType)
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
<style>
.field-hint {
  padding: 8px 0;
  font-size: 12px;
  border-radius: 2px;
}
.dialogsize {
  width: 40% !important;
}
.custom-field-modal .fc-dialog-form .el-dialog__header {
  padding: 0 !important;
}
.custom-field-modal .el-dialog__header {
  padding: 0 !important;
}
/* .new-body-modal .fc-form input:not(.q-input-target):not(.el-input__inner):not(.el-select__input):not(.btn), .fc-form textarea:not(.q-input-target):not(.el-textarea__inner):not(.el-input__inner):not(.el-select__input), .fc-form .fselect{
    width: 300px;
  } */
.new-body-modal .check-required {
  display: block;
}
.new-body-modal .select-height {
  height: 40px;
}
.v-modal {
  z-index: 101 !important;
}
#newworkordercategory .el-textarea .el-textarea__inner {
  min-height: 50px !important;
  width: 350px;
  resize: none;
}
</style>
