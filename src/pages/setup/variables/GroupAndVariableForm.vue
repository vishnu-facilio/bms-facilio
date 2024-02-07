<template>
  <el-dialog
    v-if="formType === 'group'"
    :title="
      isNew ? $t('common.header.add_group') : $t('common.header.edit_group')
    "
    :visible="true"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div class="height300">
      <el-form ref="groupForm" :rules="rules" :model="group">
        <el-form-item
          :label="$t('common.header.group_name')"
          prop="name"
          :required="true"
        >
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="group.name"
            @change="fillLinkName('group')"
            type="text"
            :placeholder="$t('common.header.group_name')"
          />
        </el-form-item>
        <el-form-item
          :label="$t('setup.setupLabel.link_name')"
          prop="linkName"
          :required="true"
        >
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="group.linkName"
            type="text"
            :placeholder="$t('setup.setupLabel.link_name')"
          />
        </el-form-item>
      </el-form>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}
        </el-button>

        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="submitGroupForm()"
        >
          {{ $t('panel.dashboard.confirm') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
  <el-dialog
    v-else
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="add-new-form-animated slideInRight fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog add-new-form"
    style="z-index: 999999"
    :before-close="closeDialog"
  >
    <el-form
      :model="variable"
      ref="variableForm"
      :rules="rules"
      :label-position="'top'"
      class="fc-form"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              isNew
                ? $t('common.products.add_variable')
                : $t('common.products.edit_variable')
            }}
          </div>
        </div>
      </div>
      <div class="new-body-modal enpi-body-modal">
        <el-form-item prop="name" :label="$t('common.products.name')">
          <el-input
            class="fc-input-full-border2 width100"
            autofocus
            v-model="variable.name"
            @change="fillLinkName('variable')"
            type="text"
            :placeholder="$t('common.header.variable_name')"
          />
        </el-form-item>
        <el-form-item prop="linkName" :label="$t('setup.setupLabel.link_name')">
          <el-input
            class="fc-input-full-border2 width100"
            autofocus
            v-model="variable.linkName"
            type="text"
            :placeholder="$t('setup.setupLabel.link_name')"
          />
        </el-form-item>
        <el-form-item prop="groupId" :label="$t('common.header.group_name')">
          <el-select
            filterable
            clearable
            allow-create
            default-first-option
            :loading="loading"
            :loading-text="loadingText"
            @change="createNewGroupOrSetGroup"
            v-model="variable.groupId"
            class="fc-input-full-border2 width100 fc-tag"
          >
            <el-option
              v-for="(groupItem, index) in groupList"
              :key="index"
              :label="groupItem.name"
              :value="groupItem.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <div class="d-flex">
          <el-form-item
            prop="type"
            :label="$t('common._common.type')"
            class="width50"
          >
            <el-select
              filterable
              v-model="variable.type"
              @change="resetValue"
              class="fc-input-full-border-select2 width100 pR30"
            >
              <el-option
                class="text-capitalize"
                v-for="(name, dataType) in variableTypes"
                :key="name"
                :label="name.toLowerCase()"
                :value="Number(dataType)"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            prop="valueString"
            :label="$t('common._common._input_value')"
            class="width50"
          >
            <template
              v-if="
                ['NUMBER', 'STRING', 'DECIMAL'].includes(
                  variableTypes[variable.type]
                )
              "
            >
              <el-input
                class="fc-input-full-border2 width100"
                autofocus
                v-model="variable.valueString"
                :type="getInputType(variableTypes[variable.type])"
                :placeholder="$t('common._common.enter_variable_value')"
              >
              </el-input>
            </template>
            <template v-if="['BOOLEAN'].includes(variableTypes[variable.type])">
              <el-checkbox v-model="variable.valueString"></el-checkbox>
            </template>
            <template
              v-if="['DATE', 'DATETIME'].includes(variableTypes[variable.type])"
            >
              <el-date-picker
                v-model="variable.valueString"
                :type="getInputType(variableTypes[variable.type])"
                class="fc-input-full-border2 width100"
              ></el-date-picker>
            </template>
          </el-form-item>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="submitVariable()"
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
import { API } from '@facilio/api'

const variableTypes = {
  1: 'NUMBER',
  2: 'STRING',
  3: 'DECIMAL',
  4: 'BOOLEAN',
  5: 'DATE',
  6: 'DATETIME',
}
const regex = /^[a-zA-Z_$]\w*/

export default {
  props: [
    'selectedVariable',
    'isNew',
    'selectedGroup',
    'selectedGroupId',
    'formType',
    'groupList',
  ],

  data() {
    return {
      saving: false,
      group: {
        name: '',
        linkName: '',
      },
      variable: {
        groupId: this.selectedGroupId ? this.selectedGroupId : null,
        name: '',
        linkName: '',
        type: 1,
        valueString: null,
      },
      variableTypes,
      loading: false,
      loadingText: null,
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
        linkName: {
          required: true,
          validator: function(rule, value, callback) {
            if (!regex.test(value)) {
              callback(new Error('Enter a valid name'))
            } else callback()
          },
          trigger: 'change',
        },
        groupId: {
          required: true,
          message: this.$t('common._common.please_select_group'),
          trigger: 'change',
        },
        type: {
          required: true,
          message: this.$t('common._common.please_select_variable_type'),
          trigger: 'change',
        },
        valueString: {
          required: true,
          message: this.$t('common._common.please_enter_value'),
          trigger: 'change',
        },
      },
    }
  },

  async created() {
    if (!this.isNew) {
      this.group = this.selectedGroup
      this.variable = this.selectedVariable
    }
  },

  methods: {
    async submitGroupForm() {
      let { group, formType } = this

      this.$refs['groupForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true

        let url = 'v3/globalVariable/addOrUpdateGroup'
        let params = { variableGroup: { ...group } }
        let { error, data } = await API.post(url, params)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          let { variableGroup } = data || {}

          this.$message.success(this.$t('common.header.group_added_or_updated'))
          this.$emit('onSave', formType, variableGroup)
          this.closeDialog()
        }
        this.saving = false
      })
    },
    async submitVariable() {
      let { variable, formType } = this
      let { valueString, type } = variable || {}

      if (['DATE', 'DATETIME'].includes(variableTypes[type])) {
        this.variable.valueString = valueString.getTime()
      }
      if (['BOOLEAN'].includes(variableTypes[type])) {
        this.variable.valueString = !!valueString
      }
      this.$refs['variableForm'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let url = 'v3/globalVariable/addOrUpdate'
        let params = {
          variable: {
            ...variable,
            valueString: this.variable.valueString.toString(),
          },
        }
        let { error, data } = await API.post(url, params)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          let { variable } = data || {}

          this.$message.success(
            this.$t('common._common.variable_added_or_updated_successfully')
          )
          this.$emit('onSave', formType, variable)
          this.closeDialog()
        }
        this.saving = false
      })
    },
    async createNewGroupOrSetGroup() {
      let { variable } = this
      let { groupId: groupName } = variable || {}

      if (typeof groupName === 'string') {
        this.loadingText = 'Adding new Group...'
        this.loading = true

        let url = 'v3/globalVariable/addOrUpdateGroup'
        let params = {
          variableGroup: {
            name: groupName,
            linkName: groupName.toLowerCase().replace(/ /g, '_'),
          },
        }
        let { error, data } = await API.post(url, params)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          let { variableGroup } = data || {}
          this.$emit('onSave', 'group', variableGroup)
          this.$nextTick(() => {
            this.$set(variable, 'groupId', variableGroup.id)
          })
        }
        this.loadingText = null
        this.loading = false
      }
    },
    fillLinkName(type) {
      if (type === 'group') {
        let { name } = this.group
        let linkName = name.toLowerCase().replace(/ /g, '_')

        this.group.linkName = linkName
      } else {
        let { name } = this.variable
        let linkName = name.toLowerCase().replace(/ /g, '_')

        this.variable.linkName = linkName
      }
    },
    getInputType(type) {
      let inputTypes = {
        NUMBER: 'number',
        DECIMAL: 'number',
        DATE: 'date',
        DATETIME: 'datetime',
      }
      return inputTypes[type] || 'text'
    },
    resetValue() {
      this.variable.valueString = null
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.add-new-form-animated {
  animation-duration: 0.3s;
  animation-fill-mode: both;
}
.el-select {
  .el-input__inner {
    text-transform: capitalize;
  }
}
</style>
