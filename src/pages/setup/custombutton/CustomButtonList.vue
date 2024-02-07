<template>
  <div class="custombtn-setup-container">
    <portal to="module-summary-actions" slim>
      <div class="action-btn setting-page-btn">
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="
            showNewCustomButtonDialog = true
            isNew = true
            selectedCustomButton = {}
          "
        >
          {{ $t('setup.customButton.header_add') }}
        </el-button>
      </div>
    </portal>

    <div class="container-scroll mT30 pB30">
      <div class="setting-Rlayout setting-list-view-table">
        <el-row class="header-row">
          <el-col :span="5" class="setting-table-th setting-th-text">
            {{ $t('setup.customButton.name') }}
          </el-col>
          <el-col :span="4" class="setting-table-th setting-th-text">
            {{ $t('maintenance._workorder.description') }}
          </el-col>
          <el-col
            :span="5"
            class="setting-table-th setting-th-text text-center"
          >
            Position
          </el-col>
          <el-col :span="4" class="setting-table-th setting-th-text">
            Behaviour
          </el-col>
          <el-col
            :span="4"
            class="setting-table-th setting-th-text text-center"
          >
            Status
          </el-col>
          <el-col :span="2" class="setting-table-th setting-th-text">
            <div class="hide-v">Edit</div>
          </el-col>
        </el-row>
        <el-row v-if="loading">
          <el-col :span="24" class="text-center">
            <spinner :show="loading" size="80"></spinner>
          </el-col>
        </el-row>
        <el-row v-else-if="$validation.isEmpty(customButtons)">
          <el-col :span="24" class="body-row-cell text-center">
            {{ $t('setup.customButton.empty_custom_buttons') }}
          </el-col>
        </el-row>
        <draggable
          v-else
          v-model="customButtons"
          v-bind="dragOptions"
          draggable=".is-draggable"
          handle=".task-handle"
          @change="reorder()"
        >
          <el-row
            class="body-row is-draggable"
            v-for="button in customButtons"
            :key="button.id"
          >
            <el-col class="body-row-cell d-flex" :span="5">
              <div class="vertical-middle task-handle inline pR10">
                <img src="~assets/drag-grey.svg" />
              </div>
              {{ button.name }}
            </el-col>
            <el-col :span="4" class="body-row-cell">
              {{ button.description ? button.description : '---' }}
            </el-col>
            <el-col :span="5" class="body-row-cell text-center">
              {{ getButtonPosition(button) }}
            </el-col>
            <el-col :span="4" class="body-row-cell">
              {{ getActionTypes(button) }}
            </el-col>
            <el-col :span="4" class="body-row-cell text-center">
              <el-switch
                v-model="button.status"
                @change="changeStatus(button)"
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </el-col>
            <el-col :span="2" class="body-row-cell">
              <div
                v-if="!button.locked"
                class="text-left actions mT0 mR15 text-center"
              >
                <i
                  class="el-icon-edit pointer pR10"
                  @click="editButton(button)"
                />

                <i
                  class="el-icon-delete pointer"
                  @click="deleteButton(button)"
                />
              </div>
            </el-col>
          </el-row>
        </draggable>
      </div>
      <NewCustomButton
        v-if="showNewCustomButtonDialog"
        :selectedCustomButton="selectedCustomButton"
        :isNew="isNew"
        :closeDialog="closeDialog"
        :module="moduleName"
      />
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { mapGetters } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import {
  getActionType,
  POSITION_TYPE,
  POSITION_LABELS,
} from './CustomButtonUtil'
import cloneDeep from 'lodash/cloneDeep'
import draggable from 'vuedraggable'
import NewCustomButton from './NewCustomButton'

export default {
  name: 'CustomButtonList',
  props: ['moduleName'],
  components: { draggable, NewCustomButton },
  title() {
    return 'Custom Buttons'
  },
  data() {
    return {
      customButtons: [],
      loading: false,
      showNewCustomButtonDialog: false,
      isNew: false,
      selectedCustomButton: null,
      dragOptions: {
        animation: 150,
        easing: 'cubic-bezier(1, 0, 0, 1)',
        group: 'tasksection',
        sort: true,
      },
    }
  },
  computed: {
    ...mapGetters('automationSetup', ['getAutomationModulesList']),
    modulesList() {
      return this.getAutomationModulesList()
    },
  },
  watch: {
    moduleName: { handler: 'getCustomButtonList', immediate: true },
  },
  methods: {
    getCustomButtonList() {
      this.loading = true
      return API.post('/v2/custombutton/list', {
        moduleName: this.moduleName,
      })
        .then(({ data, error }) => {
          if (isEmpty(error)) {
            this.customButtons = data.workflowRuleList
          } else {
            this.$message.error(error.message || 'Error Occured !')
          }
        })
        .finally(() => (this.loading = false))
    },
    getActionTypes(button) {
      return getActionType(button, true)
    },
    changeStatus(button) {
      let status = this.$getProperty(button, 'status')
      let url = status ? 'setup/turnonrule' : 'setup/turnoffrule'
      return API.post(url, {
        workflowId: button.id,
      }).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error occured')
          button.status = !button.status
        }
      })
    },
    getButtonPosition(button) {
      let positionType = this.$getProperty(button, 'positionType')

      let position = Object.values(POSITION_TYPE).find(
        type => type === positionType
      )
      return POSITION_LABELS[position]
    },
    closeDialog(needsReload = false) {
      if (needsReload) {
        this.getCustomButtonList()

        if (this.isNew) this.$message.success('Button Created Succecfully')
        else this.$message.success('Button Updated Succecfully')
      }

      this.showNewCustomButtonDialog = false
      this.isNew = false
      this.selectedCustomButton = {}
    },
    async deleteButton(button) {
      let value = await this.$dialog.confirm({
        title: this.$t(`setup.customButton.header_delete`),
        message: this.$t(`setup.customButton.delete_confirmation`),
        rbDanger: true,
        rbLabel: this.$t('common._common.delete'),
      })
      if (value) {
        let id = this.$getProperty(button, 'id')
        let { error } = await API.post('/v2/custombutton/delete', {
          ruleId: id,
        })
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success('Button deleted successfully')
          this.getCustomButtonList()
        }
      }
    },
    editButton(button) {
      this.selectedCustomButton = cloneDeep(button)
      this.selectedCustomButton.selecetdActionType = this.getActionTypes(button)

      if (isEmpty(this.selectedCustomButton.formId)) {
        this.selectedCustomButton.formId = null
      }
      this.isNew = false
      this.showNewCustomButtonDialog = true
    },
    async reorder() {
      let { error } = await API.post('/v2/custombutton/reorder', {
        moduleName: this.moduleName,
        ids: this.customButtons.map(policy => policy.id),
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.$message.success('Reorder Successful')
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.custombtn-setup-container {
  .setting-list-view-table .setting-table-th {
    vertical-align: middle;
    background-color: #f3f1fc;
    border: none;
  }
  .body-row {
    border: 1px solid transparent;
    border-bottom: 1px solid #f2f5f6;
    background-color: #fff;
  }
  .body-row:hover {
    background-color: #fff;
    border: 1px solid #b0dbe1;
  }
  .body-row:not(.is-draggable),
  .body-row:hover:not(.is-draggable) {
    border-color: #d5efff;
    background-color: #eff5f9;
  }
  .body-row:not(.is-default):not(.is-draggable) {
    border: 1px solid transparent;
    border-bottom: 1px solid #f2f5f6;
    background-color: #f7f8f9;
  }
  .body-row:hover:not(.is-default):not(.is-draggable) {
    border: 1px solid #b0dbe1;
  }
  .body-row:first-of-type {
    border-top: 1px solid #f2f5f6;
  }
  .body-row:first-of-type:hover {
    border: 1px solid #b0dbe1;
  }
  .body-row-cell {
    border-top: none;
    border-left: none;
    border-right: none;
    color: #333;
    font-size: 14px;
    border-collapse: separate;
    padding: 15px 30px;
    letter-spacing: 0.6px;
    font-weight: 400;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .body-row-cell:first-of-type {
    padding-left: 15px;
  }
  .body-row.is-draggable .task-handle {
    cursor: move;
  }
  .body-row .actions {
    visibility: hidden;
  }
  .body-row:hover .actions {
    visibility: visible;
  }
}
</style>
