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
      <!-- header -->
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              !isNew
                ? $t('common.wo_report.edit_asset_category')
                : $t('common.wo_report.new_asset_category')
            }}
          </div>
          <div class="fc-setup-modal-desc">
            {{ $t('common._common.list_of_all_asset_category') }}
          </div>
        </div>
      </div>

      <div class="new-body-modal">
        <div class="setup-input-block">
          <p class="fc-input-label-txt">
            {{ $t('common.products.name') }}
          </p>
          <el-input
            v-model="displayName"
            :placeholder="$t('common._common.enter_category')"
            class="fc-input-txt fc-input-full-border-select2  width100"
          ></el-input>
        </div>

        <div class="setup-input-block mT20">
          <p class="fc-input-label-txt">{{ $t('common._common.type') }}</p>
          <el-select
            class="fc-input-full-border-select2 width50"
            v-model="type"
            :disabled="isDefault"
            :placeholder="$t('common.wo_report.choose_type')"
          >
            <el-option :key="2" label="Energy" :value="2"></el-option>
            <el-option :key="1" label="HVAC" :value="1"></el-option>
            <el-option :key="3" label="Fire" :value="3"></el-option>
            <el-option :key="0" label="Others" :value="0"></el-option>
          </el-select>
        </div>

        <div class="setup-input-block mT20">
          <p class="fc-input-label-txt">
            {{ $t('common._common.parent_category') }}
          </p>
          <el-select
            v-model="parentCategoryId"
            clearable
            :disabled="isDefault"
            class="fc-input-full-border-select2 width50"
            :placeholder="$t('common._common.choose_parent_category')"
          >
            <el-option
              v-for="(cat, index) in assetCategory"
              v-if="isNew || cat.id !== currentCategoryId"
              :key="index"
              :label="cat.displayName"
              :value="cat.id"
            ></el-option>
          </el-select>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          @click="saveCategory"
          class="modal-btn-save"
          >{{ $t('common.roles.save') }}</el-button
        >
      </div>
    </div>
  </el-dialog>
</template>
<script>
import { mapState } from 'vuex'
import { API } from '@facilio/api'

export default {
  props: ['category', 'isNew', 'visibility'],
  data() {
    return {
      displayName: '',
      type: null,
      parentCategoryId: null,
      currentCategoryId: null,
      isDefault: false,
    }
  },
  mounted() {
    this.initCategory()
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },

  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
  },

  methods: {
    initCategory() {
      if (this.isNew) {
        this.displayName = ''
        this.type = null
        this.parentCategoryId = null
        this.isDefault = false
      } else {
        this.displayName = this.category.value.displayName
        this.type = this.category.value.type
        this.parentCategoryId =
          this.category.value.parentCategoryId === 0
            ? null
            : this.category.value.parentCategoryId
        this.currentCategoryId = this.category.value.id
        this.isDefault = this.category.value.isDefault
      }
    },
    saveCategory() {
      let self = this
      let assetCategory = {
        displayName: this.displayName,
        type: this.type,
        parentCategoryId: this.parentCategoryId,
      }

      if (this.isNew) {
        API.createRecord('assetcategory', { data: assetCategory }).then(
          (assetCategory, error) => {
            if (!error) {
              assetCategory.isDefault = self.isDefault

              self.$message({
                message: self.$t(
                  'common.wo_report.asset_category_saved_successfully'
                ),
                type: 'success',
              })

              self.closeDialog()
              // reload the list while closing the closing the dialog.
              this.$emit('reloadList')
            } else {
              self.$message({
                message: self.$t('common.wo_report.unable_to_add_record'),
                type: 'error',
              })
            }
          }
        )
      } else {
        API.updateRecord('assetcategory', {
          id: this.currentCategoryId,
          data: assetCategory,
        }).then((assetCategory, error) => {
          if (!error) {
            assetCategory.isDefault = self.isDefault

            self.$message({
              message: self.$t(
                'common._common.asset_category_edited_successfully'
              ),
              type: 'success',
            })

            self.closeDialog()
            // reload the list while closing the closing the dialog.
            this.$emit('reloadList')
          } else {
            self.$message({
              message: self.$t('common.wo_report.unable_to_update'),
              type: 'error',
            })
          }
        })
      }
    },
    cancel() {
      this.$emit('canceled')
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
