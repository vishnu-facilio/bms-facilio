<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    :before-close="closeDialog"
    custom-class="fc-dialog-form fc-dialog-right  classification-dialog-right"
    style="z-index: 9"
  >
    <template slot="title">
      <div class="el-dialog__title self-center f16">
        {{ title }}
      </div>
    </template>
    <div v-if="isLoading" class="classification-shimmer-lines mT10">
      <span class="span-name loading-shimmer"></span>
      <span class="lines loading-shimmer"></span>
      <span class="span-name loading-shimmer"></span>
      <span class="rectangle loading-shimmer"></span>
      <span class="span-name loading-shimmer"></span>
      <span class="lines loading-shimmer"></span>
    </div>
    <div v-else class="classification-scroll-container pB50 pT40">
      <el-form :rules="rules" ref="attribute-form" :model="classificationData">
        <el-form-item
          :label="$t('setup.classification.name')"
          prop="name"
          class="pL32 pR32"
        >
          <el-input
            v-model="classificationData.name"
            :placeholder="
              $t('setup.classification.classification_name_placeholder')
            "
            class="fc-input-full-border-select2 width100 "
          >
          </el-input
        ></el-form-item>
        <el-form-item
          :label="$t('setup.classification.description')"
          prop="description"
          class="pL32 pR32"
        >
          <el-input
            v-model="classificationData.description"
            type="textarea"
            :min-rows="1"
            :autosize="{ minRows: 2, maxRows: 4 }"
            :placeholder="$t('setup.classification.enter_description')"
            class="fc-input-full-border-select2 width100"
          >
          </el-input
        ></el-form-item>
        <el-form-item
          :label="$t('setup.classification.classification_extends')"
          class="pL32 pR32"
        >
          <SpecificationLookup
            :classificationData.sync="specificModule"
            :isNew="isNew"
          ></SpecificationLookup>
        </el-form-item>
        <el-form-item
          :label="$t('setup.classification.applies_to')"
          prop="appliedModuleIds"
          class="pL32 pR32"
        >
          <el-select
            v-model="classificationData.appliedModuleIds"
            multiple
            filterable
            collapse-tags
            :disabled="!isNew"
            class="fc-input-full-border-select2 width100 fc-tag"
            :placeholder="$t('setup.classification.list_of_modules')"
          >
            <el-option
              v-for="list in getAppliedModulesList"
              :key="`moduleId-${list.moduleId}-${list.name}`"
              :label="list.displayName"
              :value="list.moduleId"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <div v-if="!$validation.isEmpty(getAttributeList)" class="mB40">
          <div class="pB8 pT15 pL32 pR32 sub-header">
            {{ $t('setup.classification.attributes.existing_attributes') }}
          </div>
          <AttributeListTable
            key="existing-attribute-list"
            :attributeList.sync="getAttributeList"
            :isExisting="true"
          ></AttributeListTable>
        </div>
        <div class="new-attribute-container pB8 pL32 pR32">
          <div>
            <div class="sub-header">
              {{ $t('setup.classification.attributes.new_attributes') }}
            </div>
          </div>
          <div
            @click="showAttributePopup = true"
            class="f14 fR pointer  d-flex add-attr-btn"
          >
            <i class="el-icon-plus f12 fwBold pR2"></i>
            {{ $t('setup.classification.add') }}
          </div>
        </div>
        <div v-if="!$validation.isEmpty(selectedAttributeList)">
          <AttributeListTable
            key="new-attribute-list"
            :attributeList.sync="selectedAttributeList"
          ></AttributeListTable>
        </div>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog" class="modal-btn-cancel">{{
        $t('setup.classification.cancel')
      }}</el-button>
      <el-button
        type="primary"
        class="modal-btn-save"
        @click="onSave"
        :loading="saving"
        >{{ $t('setup.classification.save') }}</el-button
      >
    </div>
    <AttributeWizard
      v-if="showAttributePopup"
      :selectedAttributeList="selectedAttributeList"
      :classificationId="selectedId"
      @onClose="showAttributePopup = false"
      @onSave="selectedList"
    >
    </AttributeWizard>
  </el-dialog>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import SpecificationLookup from 'src/newapp/components/classification/SpecificationLookup.vue'
import AttributeWizard from './ClassificationAttributeFieldWizard.vue'
import { ClassificationListModel } from './ClassificationModel'
import AttributeListTable from './AttributeListTable.vue'

export default {
  props: ['selectedId'],
  components: { SpecificationLookup, AttributeWizard, AttributeListTable },
  data() {
    return {
      isLoading: false,
      classificationData: {},
      attributeList: [],
      modulesList: [],
      saving: false,
      showAttributePopup: false,
      selectedAttributeList: [],
      dataTypes: {
        1: 'String',
        2: 'Number',
        3: 'Decimal',
        4: 'Boolean',
        6: 'DateTime',
      },
      rules: {
        name: [
          {
            required: true,
            message: this.$t('setup.classification.please_input_clss_name'),
            trigger: 'blur',
          },
        ],
        appliedModuleIds: [
          {
            required: true,
            message: this.$t(
              'setup.classification.attributes.please_select_field'
            ),
            trigger: 'change',
          },
        ],
      },
    }
  },
  created() {
    this.getModulesList()
    this.getClassificationData()
  },
  computed: {
    isNew() {
      return isEmpty(this.selectedId)
    },
    title() {
      return this.isNew
        ? this.$t('setup.classification.new_classification')
        : this.$t('setup.classification.edit_classification')
    },
    specificModule: {
      get() {
        return this.classificationData
      },
      set(value) {
        this.$set(this.classificationData, 'parentClassification', value)
        if (this.isNew) this.classificationData.appliedModuleIds = []
      },
    },
    getAttributeList() {
      if (!isEmpty(this.attributeList)) {
        return this.attributeList.map(item => {
          return {
            ...item,
            fieldTypeDisplayName: this.dataTypes[item.fieldType],
          }
        })
      }
      return []
    },
    getAppliedModulesList() {
      let { modulesList, classificationData } = this
      let appliedModuleIds = this.$getProperty(
        classificationData,
        'parentClassification.appliedModuleIds',
        []
      )
      let filteredModuleList = []
      if (!isEmpty(appliedModuleIds)) {
        filteredModuleList = modulesList.filter(moduleObjn =>
          appliedModuleIds.includes(moduleObjn.moduleId)
        )
      } else {
        filteredModuleList = modulesList
      }
      return filteredModuleList || []
    },
  },
  methods: {
    async getModulesList() {
      let { error, data } = await API.get('/v3/classifications/modules')
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.modulesList = data.modules
      }
    },
    async getClassificationData() {
      this.isLoading = true
      if (!this.isNew) {
        this.classificationData = await ClassificationListModel.fetch({
          id: this.selectedId,
          classificationResolvePath: true,
        })
        this.attributeList = this.classificationData.attributes
      } else {
        this.classificationData = new ClassificationListModel()
      }
      this.isLoading = false
    },
    closeDialog() {
      this.$emit('onClose')
    },
    serialize() {
      return this.selectedAttributeList.map(attr => ({ id: attr.id }))
    },
    selectedList(val) {
      this.selectedAttributeList = val
    },
    async validate() {
      try {
        return await this.$refs['attribute-form'].validate()
      } catch {
        return false
      }
    },
    async onSave() {
      let valid = await this.validate()
      if (!valid) return

      this.saving = true
      let { classificationData } = this
      let { attributes } = classificationData
      let attributesList = this.serialize()
      attributesList = [...(attributes || []), ...(attributesList || [])]
      this.$set(classificationData, 'attributes', attributesList)

      try {
        await this.classificationData.save()

        if (this.isNew)
          this.$message.success(this.$t('setup.classification.created_success'))
        else
          this.$message.success(this.$t('setup.classification.update_success'))
      } catch (error) {
        this.$message.error(error.message)
      }

      this.saving = false
      this.$emit('onSave')
      this.closeDialog()
    },
  },
}
</script>
<style lang="scss" scoped>
.classification-scroll-container {
  height: calc(100vh - 100px);
  overflow-y: scroll;
}
.new-attribute-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.add-attr-btn {
  padding: 4px 8px;
  align-items: center;
  color: #fff;
  background-color: #3ab2c1;
  border-radius: 2px;
  .pR2 {
    padding-right: 2px;
  }
}
.classification-shimmer-lines .lines {
  height: 30px;
  width: 90%;
  margin: 0px 20px 20px;
  border-radius: 5px;
}
.classification-shimmer-lines .rectangle {
  height: 90px;
  width: 90%;
  margin: 0px 20px 20px;
  border-radius: 5px;
}
.classification-shimmer-lines .span-name {
  height: 10px;
  width: 50%;
  margin: 20px 20px 5px;
  border-radius: 5px;
}
</style>
<style lang="scss">
.classification-dialog-right {
  width: 37% !important;
  .el-dialog__header {
    padding: 28px;
    border-bottom: solid 1px #d0d9e2;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .el-dialog__title {
      color: #4f4f4f;
    }
    .el-dialog__headerbtn {
      position: unset;

      .el-dialog__close {
        font-size: 24px;
        font-weight: 500;
      }
    }
  }
  .el-dialog__body {
    height: calc(100vh - 50px);
    overflow-y: scroll;
  }
  .el-form-item {
    margin-bottom: 40px;

    .el-form-item__content {
      line-height: normal;
    }
    .el-form-item__label {
      font-weight: 500;
      color: #2f4058;
      font-size: 14px;
      letter-spacing: 0.5px;
      line-height: normal;
      padding-bottom: 8px;
    }
  }

  .sub-header {
    font-weight: 500;
    color: #2f4058;
    font-size: 14px;
    letter-spacing: 0.5px;
  }
  .pR32 {
    padding-right: 32px;
  }
}
</style>
