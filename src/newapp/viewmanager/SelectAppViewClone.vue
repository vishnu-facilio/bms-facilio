<template>
  <el-dialog
    :title="$t('common._common.clone')"
    :visible="true"
    :show-close="false"
    :append-to-body="true"
    top="30vh"
    width="30%"
    class="fc-dialog-center-container clone_view-choose_app-dialog"
  >
    <el-form ref="selectAppForm" :rules="rules" :model="selectAppFormObj">
      <el-form-item :label="$t('common.header.application')" prop="cloneAppId">
        <el-select
          v-model="selectAppFormObj.cloneAppId"
          :placeholder="$t('common.placeholders.select_app')"
          filterable
          class="fc-input-full-border2 min_width100 pR15"
        >
          <el-option
            v-for="app in apps"
            :key="app.linkName"
            :label="app.name"
            :value="app.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button class="cancel-btn" @click="closeDialog">{{
        $t('common.roles.cancel')
      }}</el-button>
      <el-button type="primary" class="save-btn" @click="save">{{
        $t('common._common._save')
      }}</el-button>
    </span>
  </el-dialog>
</template>
<script>
export default {
  props: ['apps', 'currentAppId'],
  data() {
    return {
      selectAppFormObj: { cloneAppId: null },
      rules: {
        cloneAppId: {
          required: true,
          message: this.$t('viewsmanager.view_clone.select_app_to_clone_view'),
          trigger: 'change',
        },
      },
    }
  },
  created() {
    let { currentAppId } = this
    this.selectAppFormObj.cloneAppId = currentAppId
  },
  methods: {
    closeDialog() {
      this.$emit('close')
    },
    async save() {
      let validate = await this.$refs['selectAppForm'].validate()
      if (!validate) return
      let { cloneAppId } = this.selectAppFormObj
      this.$emit('save', cloneAppId)
    },
  },
}
</script>
<style lang="scss">
.clone_view-choose_app-dialog {
  .el-form-item__label {
    font-weight: 500;
  }
  .el-form-item.is-required .el-form-item__label:after {
    display: none;
  }
  .el-dialog__footer {
    padding: 20px 44px;
    .save-btn,
    .save-btn:active {
      border-radius: 4px;
      background-color: #3ab2c2 !important;
      border: solid 1px #38b2c2;
      color: #fff !important;
    }
    .cancel-btn {
      border-radius: 4px;
      background-color: #fff;
      color: #3ab2c2;
      border: 1px solid #3ab2c2;
    }
    .el-button {
      padding: 8px 20px;
      font-size: 14px;
    }
  }
}
</style>
