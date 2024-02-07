<template>
  <div class="custom-modules-details">
    <div class="user-layout">
      <portal to="module-summary-actions" slim>
        <div class="action-btn">
          <el-dropdown class="setup-el-btn" @command="addNewForm">
            <el-button type="primary" class="setup-el-btn">
              New Custom Field
              <i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="addField">Add field</el-dropdown-item>
              <el-dropdown-item command="customField">
                Add field in template
              </el-dropdown-item>
              <el-dropdown-item command="rollup">
                Add Rollup Field
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <NewCustomField
            v-if="showDialog"
            :visibility.sync="showDialog"
            :isNew="isNew"
            @onsave="loadFields"
            :module="moduleName"
            :field="selectedField"
          ></NewCustomField>
        </div>
      </portal>

      <div class="container-scroll">
        <div class="row setting-Rlayout mT30">
          <div class="col-lg-12 col-md-12">
            <table class="setting-list-view-table">
              <thead>
                <tr>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('panel.dashboard.label') }}
                  </th>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('setup.setupLabel.field_type') }}
                  </th>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('maintenance.wr_list.type') }}
                  </th>
                  <th class="setting-table-th setting-th-text">
                    {{ $t('setup.setupLabel.link_name') }}
                  </th>
                  <th class="setting-table-th setting-th-text"></th>
                </tr>
              </thead>
              <tbody v-if="loading">
                <tr>
                  <td colspan="100%" class="text-center">
                    <spinner :show="loading" size="80"></spinner>
                  </td>
                </tr>
              </tbody>
              <tbody v-else>
                <tr
                  class="tablerow"
                  v-for="(field, index) in filteredFields"
                  :key="index"
                >
                  <td style="padding-top: 18px;padding-bottom: 18px;">
                    {{ field.displayName }}
                  </td>
                  <td>
                    {{
                      field.displayType && displayTypeMap[field.displayType]
                        ? displayTypeMap[field.displayType]
                        : field.dataTypeEnum
                        ? field.dataTypeEnum
                        : null
                    }}
                  </td>
                  <td>{{ field.default ? 'System' : 'Custom' }}</td>
                  <td>{{ field.name }}</td>
                  <td>
                    <div class="actions pointer flex-row-space min-width100px">
                      <template v-if="!(field.default || field.locked)">
                        <i
                          class="el-icon-edit pointer"
                          @click="editField(field)"
                        ></i>
                        <i
                          class="el-icon-delete pointer mL30"
                          @click="deleteField(field)"
                        ></i>
                      </template>
                      <el-dropdown
                        v-if="showFieldPermissionDropDown(field)"
                        trigger="click"
                        class="mL-auto"
                      >
                        <span class="el-dropdown-link ">
                          <InlineSvg
                            src="svgs/menu"
                            iconClass="icon icon-xs mR10"
                          ></InlineSvg>
                        </span>
                        <el-dropdown-menu slot="dropdown">
                          <el-dropdown-item>
                            <div @click="showFieldPermission(field)">
                              {{ $t('setup.setupLabel.field_permission') }}
                            </div>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </el-dropdown>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <FieldPermissionEditForm
        v-if="showFormCreation"
        :selectedField="selectedField"
        @save="loadFields"
        @close="showFormCreation = false"
      ></FieldPermissionEditForm>
      <NewRollupField
        v-if="showRollupField"
        @saved="loadFields"
        :visibility.sync="showRollupField"
        :isNew="isNew"
        :model="rollupFieldGet"
        :module="moduleName"
      ></NewRollupField>
      <!-- Goto form builder dialog start-->
      <el-dialog
        title="List of Form Templates"
        :visible.sync="dialogVisible"
        width="40%"
        :before-close="closeDialog"
        class="fc-dialog-center-container fc-dialog-center-body-p0 scale-up-center"
      >
        <div v-if="loading" class="flex-middle height450">
          <spinner :show="loading" size="80"></spinner>
        </div>

        <div
          v-if="$validation.isEmpty(formTemplateData) && !loading"
          class="height450 flex-middle justify-content-center flex-direction-column"
        >
          <inline-svg
            src="svgs/emptystate/readings-empty"
            iconClass="icon text-center icon-xxxxlg"
          ></inline-svg>
          <div class="nowo-label">
            No Template available
          </div>
        </div>

        <el-table
          :data="formTemplateData"
          stripe
          style="width: 100%"
          class="fc-table-th-pLalign-reduce mB60"
          height="450"
          v-if="!loading && !$validation.isEmpty(formTemplateData)"
        >
          <el-table-column prop="Name" label="Name">
            <template v-slot="formList">
              <el-radio
                :label="formList.row"
                v-model="selectedRow"
                class="fc-radio-btn"
              >
                {{ formList.row.displayName }}
              </el-radio>
            </template>
          </el-table-column>
          <el-table-column prop="Type" label="Type">
            <template v-slot="formList">
              {{ formList.row.formTypeVal }}
            </template>
          </el-table-column>
        </el-table>

        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">
            CANCEL
          </el-button>
          <el-button type="primary" @click="editForm" class="modal-btn-save">
            Ok
          </el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import Constants from 'util/constant'
import NewCustomField from '@/NewCustomField'
import NewRollupField from '@/NewRollupFields'
import { API } from '@facilio/api'
import { serializeProps } from '@facilio/utils/utility-methods'
import { isEmpty } from '@facilio/utils/validation'
import FieldPermissionEditForm from 'src/pages/setup/FieldPermissionEditForm.vue'

export default {
  props: ['moduleName'],
  components: { NewCustomField, NewRollupField, FieldPermissionEditForm },
  data() {
    return {
      selectedRow: null,
      loading: true,
      fields: [],
      selectedField: null,
      isNew: false,
      dialogVisible: false,
      showDialog: false,
      showRollupField: false,
      formList: null,
      rollupFieldGet: [],
      formTemplateData: [],
      showFormCreation: false,
      displayTypeMap: {
        LOOKUP_POPUP: 'Picklist',
        LOOKUP_SIMPLE: 'Picklist',
        DATETIME: 'Datetime',
        DATE: 'Date',
        TEXTAREA: 'Multi Line Text',
        TEXTBOX: 'Single Line Text',
        NUMBER: 'Number',
        ROLL_UP_FIELD: 'Rollup Field',
        CURRENCY: 'Currency',
        MULTI_CURRENCY: 'Multi Currency',
      },
    }
  },

  created() {
    this.loadFields()
  },

  computed: {
    filteredFields() {
      let { fields, $constants, moduleName } = this
      let { HideFieldsSpecificToModule } = $constants
      let hiddenFields = HideFieldsSpecificToModule[moduleName] || []

      return fields.filter(f => !hiddenFields.includes(f.name))
    },
  },

  watch: {
    moduleName() {
      this.loadFields()
    },
  },

  methods: {
    async loadFields() {
      this.loading = true

      let { moduleName } = this
      let { error, data } = await API.get('/v2/modules/fields/fields', {
        moduleName,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.fields = data.fields
      }
      this.loading = false
    },
    showRollUpForm() {
      this.selectedField = null
      this.isNew = true
      this.showRollupField = true
    },
    editField(field) {
      let { displayType } = field
      if (displayType && this.displayTypeMap[displayType] === 'Rollup Field') {
        this.isNew = false
        this.getRollupFieldData(field)
      } else {
        this.showDialog = true
        this.isNew = false
        this.selectedField = field
      }
    },
    showFieldPermissionDropDown(field) {
      let { dataTypeEnum } = field || {}
      return dataTypeEnum !== 'LARGE_TEXT'
    },
    editForm() {
      let { moduleName, selectedRow } = this
      let { id } = selectedRow

      if (id === -1) {
        /* Have to add the default forms to the DB, before editing them */
        let formData = serializeProps(
          selectedRow,
          Constants.FORM_RESOURCE_PROPS
        )
        let data = {
          form: formData,
        }

        this.saveRecord(data)
      } else {
        let currentPath = this.$router.resolve({
          name: 'modules-details',
          params: { moduleName },
        }).href

        this.$router.push({
          path: `${currentPath}/layouts/${id}/edit`,
        })
      }
    },
    async saveRecord(data) {
      if (!isEmpty(data.form) && isEmpty(data.form.stateFlowId)) {
        data.form.stateFlowId = -99
      }

      let { moduleName } = this
      let url = 'v2/forms/add'
      data.moduleName = moduleName
      let { error, data: dataObj } = await API.post(url, data)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        let {
          form: { id },
        } = dataObj
        let currentPath = this.$router.resolve({
          name: 'modules-details',
          params: { moduleName },
        }).href

        this.$router.push({
          path: `${currentPath}/layouts/${id}/edit`,
        })
      }
    },
    deleteField(customField) {
      let { id, displayName } = customField
      let formData = {
        fieldIds: [id],
      }
      this.$dialog
        .confirm({
          title: 'Delete Field',
          message:
            'Are you sure you want to delete this Custom Field?' +
            displayName +
            '?',
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(async value => {
          if (value) {
            let { error } = await API.post('/module/deletefields', formData)

            if (error) {
              this.$message.error('Error in deleting')
            } else {
              this.fields = this.fields.filter(field => id !== field.id)
              this.$message.success('Successfully deleted the field')
            }
          }
        })
    },
    showFieldPermission(field) {
      this.selectedField = field
      this.showFormCreation = true
    },
    addNewForm(cmd) {
      if (cmd === 'rollup') {
        this.showRollUpForm()
      } else if (cmd === 'customField') {
        this.dialogVisible = true
        this.getFormTemplateList()
      } else if (cmd === 'addField') {
        this.openCustomFields()
      }
    },
    openCustomFields() {
      this.showDialog = true
      this.isNew = true
      this.selectedField = null
    },
    async getRollupFieldData(field) {
      let { error, data } = await API.get(
        `v2/rollUpField/getRollUpFields?fieldIds=${field.id}`
      )
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.rollupFieldGet = data.rollUpFields[0]
        this.showRollupField = true
      }
    },
    closeDialog() {
      this.dialogVisible = false
    },
    async getFormTemplateList() {
      this.loading = true
      let { error, data } = await API.get(
        `/v2/forms?moduleName=${this.moduleName}`
      )
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.formTemplateData = data.forms
        this.loading = false
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.setting-list-view-table th {
  background-color: #f3f1fc;
}

.flex-row-space {
  display: flex;
  align-items: center;
  flex-direction: row;
}
</style>
