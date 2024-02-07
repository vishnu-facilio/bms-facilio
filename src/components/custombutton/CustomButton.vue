<template>
  <div>
    <div
      v-if="[POSITION.LIST_TOP, POSITION.SUMMARY].includes(position)"
      class="custombtn-container"
    >
      <template v-if="customButtons.length == 1">
        <AsyncButton
          v-for="(button, index) in customButtons"
          :key="`${button.name}-${index}`"
          :size="buttonSize"
          :class="[customButtonStyle, 'custombtn-name']"
          :clickAction="startButtonAction"
          :actionParams="[button]"
          :disabled="actionLoading"
          :title="`${button.name}`"
          v-tippy
        >
          {{ button.name }}
        </AsyncButton>
      </template>
      <template v-else>
        <el-dropdown
          v-if="!isEmpty(customButtons)"
          trigger="click"
          split-button
          :class="customButtonStyle"
          @command="startButtonAction"
          @click="startButtonAction(buttons.featured[0])"
          :size="buttonSize"
          :disabled="actionLoading"
          @visible-change="dropdownVisibilityChange"
        >
          <div class="display-flex-between-space">
            <div
              class="custombtn-name"
              :title="`${buttons.featured[0].name}`"
              v-tippy
            >
              <span v-if="loading"
                ><i class="el-icon-loading"></i>
                {{ $t('common._common.loading') }}</span
              >
              <span v-else>
                {{ buttons.featured[0].name }}
              </span>
            </div>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              class="pointer"
              v-for="button in buttons.dropdown"
              :key="button.id"
              :command="button"
              :title="`${button.name}`"
              v-tippy
              >{{ button.name }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </div>
    <div v-else class="custombtn-container">
      <el-dropdown
        trigger="click"
        :class="customButtonStyle"
        :size="buttonSize"
        :disabled="actionLoading"
        @command="handleCommandForDropdownButton"
        @visible-change="dropdownVisibilityChange"
      >
        <el-button
          v-if="position === POSITION.LIST_BAR"
          type="primary"
          :loading="actionLoading"
          class="custombtn-bulk-action"
          @click="fetchCustomBtnOnVisible"
        >
          {{
            actionLoading
              ? $t('common._common.loading')
              : $t('setup.customButton.more_actions')
          }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <fc-icon
          v-else
          group="default"
          name="ellipsis-vertical"
          size="16"
          class="pointer margin-auto more-icon"
          @click="fetchCustomBtnOnVisible"
        ></fc-icon>
        <el-dropdown-menu slot="dropdown" class="more-action-btns">
          <template v-if="isBtnLoading">
            <el-dropdown-item
              v-for="index in [1, 2]"
              :key="`loading-${index}`"
              class="lines loading-shimmer"
              :class="{ 'dropdown-f12 ': isMoreActionBtnSlotProvided }"
            ></el-dropdown-item>
          </template>
          <template v-else>
            <slot name="more-action-btns"></slot>
            <template v-if="!isEmpty(customButtons)">
              <el-dropdown-item
                v-for="button in customButtons"
                :key="button.id"
                :command="{ command: button, uniqueKey: 'customButton' }"
                class="pointer"
                :class="{ 'dropdown-f12 ': isMoreActionBtnSlotProvided }"
              >
                {{ button.name }}
              </el-dropdown-item>
            </template>
            <el-dropdown-item
              v-else-if="showDropDownInList"
              style="color: #999;"
              class="cursor-default pointer-events-none text-center "
              :class="{ 'dropdown-f12 ': isMoreActionBtnSlotProvided }"
            >
              {{ $t('setup.customButton.no_buttons_configured') }}
            </el-dropdown-item>
          </template>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <template v-if="showRecordActionForm">
      <CreateUpdateRecord
        :selectedButton="selectedButton"
        :record="record"
        :moduleName="moduleName"
        :updateUrl="updateUrl"
        :transformFn="transformFn"
        :actionType="currentActionType"
        :isPortalApp="isPortalApp"
        @response="responseHandler"
        @closed="
          () => {
            this.showRecordActionForm = false
          }
        "
      />
    </template>
    <template v-if="showBulkActionForm">
      <BulkUpdateRecord
        :selectedButton="selectedButton"
        :selectedRecords="selectedRecords"
        :moduleName="moduleName"
        @response="responseHandler"
        @closed="
          () => {
            this.showBulkActionForm = false
          }
        "
      />
    </template>
    <template v-if="showConnectedAppWidget">
      <ConnectedAppWidget
        :record="record"
        :widgetId="widgetId"
        :showConnectedAppWidget.sync="showConnectedAppWidget"
        @loadData="loadData"
      />
    </template>
  </div>
</template>

<script>
import intersection from 'lodash/intersection'
import { API } from '@facilio/api'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import {
  POSITION_TYPE,
  getActionType,
  ACTION_TYPES,
} from 'pages/setup/custombutton/CustomButtonUtil'
import {
  redirectUrlAction,
  redirectType,
  internalUrlAction,
} from './actions/RedirectUrl'
import CreateUpdateRecord from './actions/CreateUpdateRecord'
import BulkUpdateRecord from './actions/BulkUpdateRecord'
import ConnectedAppWidget from './actions/ConnectedAppWidget'
import AsyncButton from '../AsyncButton'
import { tabTypes, findTab, getApp } from '@facilio/router'
import { mapGetters } from 'vuex'
import { CustomModuleData } from 'src/pages/custom-module/CustomModuleData'

export default {
  props: [
    'record',
    'updateUrl',
    'moduleName',
    'transformFn',
    'hideNotifications',
    'position',
    'selectedRecords',
    'toggleVisibility',
  ],
  components: {
    ConnectedAppWidget,
    CreateUpdateRecord,
    BulkUpdateRecord,
    AsyncButton,
  },
  data() {
    return {
      customButtons: [],
      selectedButton: null,
      loading: false,
      showConnectedAppWidget: false,
      widgetId: null,
      showRecordActionForm: false,
      showBulkActionForm: false,
      currentActionType: null,
      POSITION: POSITION_TYPE,
      visibility: false,
      actionLoading: false,
      isBtnLoading: false,
      dropdownVisible: false,
      isEmpty,
    }
  },
  created() {
    let { LIST_ITEM, LIST_BAR } = POSITION_TYPE
    let { moduleName, position } = this
    if (!isEmpty(moduleName) && ![LIST_ITEM, LIST_BAR].includes(position)) {
      this.loadCustomButtonsList()
    }
  },
  watch: {
    moduleName: {
      handler() {
        this.loadCustomButtonsList()
      },
    },
    record(newValue, oldValue) {
      if (oldValue !== newValue && !isEmpty(newValue)) {
        this.loadCustomButtonsList()
      }
    },
    selectedRecords: {
      handler(records) {
        this.filterIntersectedButtons(records)
      },
      immediate: true,
    },
  },
  computed: {
    modelDataClass() {
      return this.$attrs.modelDataClass || CustomModuleData
    },
    isMoreActionBtnSlotProvided() {
      return !isEmpty(this.$slots['more-action-btns'])
    },
    isWoStateTransitionLicenseEnabled() {
      return this.$helpers.isLicenseEnabled('WO_STATE_TRANSITION_V3')
    },
    buttons() {
      let buttons = [...this.customButtons]
      return {
        featured: buttons.slice(0, 1) || [],
        dropdown: buttons.slice(1) || [],
      }
    },
    buttonSize() {
      let { position } = this
      if (
        [POSITION_TYPE.LIST_ITEM, POSITION_TYPE.LIST_BAR].includes(position)
      ) {
        return 'mini'
      } else if (position === POSITION_TYPE.LIST_TOP) {
        return 'medium'
      } else {
        return ''
      }
    },
    showDropDownInList() {
      let { customButtons, isMoreActionBtnSlotProvided } = this
      return !isMoreActionBtnSlotProvided && isEmpty(customButtons)
    },

    // Remove when v3 is enabled for workorders
    isPortalApp() {
      return ['tenant', 'occupant', 'client', 'vendor'].includes(
        getApp().linkName || null
      )
    },
    customButtonStyle() {
      let { position, customButtons } = this
      let style = ''

      if (customButtons.length == 1) {
        style += 'custombtn'
      } else {
        style += 'custombtn-dropdown'
      }

      if (POSITION_TYPE.LIST_TOP === position) {
        style += ' top-btn'
      } else {
        if (POSITION_TYPE.LIST_ITEM === position) {
          style += ' d-flex'
        } else {
          style += ' bulk-btn'
        }
      }

      return style
    },
    ...mapGetters({
      getTabByTabId: 'webtabs/getTabByTabId',
      tabHasPermission: 'webtabs/tabHasPermission',
    }),
  },
  methods: {
    async loadCustomButtonsList(force = false) {
      let { LIST_ITEM, LIST_BAR } = POSITION_TYPE
      let { moduleName, record, selectedRecords, position } = this
      let params = { moduleName, position, force }

      let errorHandling = error => {
        let errMsg =
          error?.message ||
          this.$t('custommodules.list.unable_to_fetch_custom_btn')

        this.$message.error(errMsg)
      }

      this.isBtnLoading = true

      if ([LIST_ITEM, LIST_BAR].includes(position)) {
        let recordList = position === LIST_ITEM ? [record] : selectedRecords
        let recordIds = (recordList || []).map(rec => rec.id)

        try {
          let response = await this.modelDataClass.loadCustomButtonsForRecords({
            ...params,
            recordIds,
          })

          if (!isEmpty(response)) {
            position === LIST_ITEM
              ? this.filterEachRecordButtons(response)
              : this.filterIntersectedButtons(response)
          }
        } catch (error) {
          errorHandling(error)
        }
      } else {
        try {
          let customButtons = await this.modelDataClass.loadCustomButtons({
            ...params,
            record,
          })

          this.customButtons = customButtons || []
        } catch (error) {
          errorHandling(error)
        }
      }

      this.isBtnLoading = false
    },
    fetchCustomBtnOnVisible() {
      if (!this.dropdownVisible) {
        this.loadCustomButtonsList()
      }
    },
    filterEachRecordButtons(response) {
      let { record, position } = this
      let { customButtons, customButtonsforRecords } = response || {}

      let { evaluatedButtonIds: buttonIds } =
        (customButtonsforRecords || []).find(r => r.id === record.id) || {}

      if (!isEmpty(buttonIds)) {
        this.customButtons = (customButtons || []).filter(
          button =>
            buttonIds.includes(button.id) && button.positionType === position
        )
      }
    },
    filterIntersectedButtons(response) {
      let { selectedRecords, position } = this
      let { customButtons, customButtonsforRecords } = response || {}

      if (!isEmpty(selectedRecords)) {
        let buttonIds = []

        selectedRecords.forEach(record => {
          let { evaluatedButtonIds } =
            (customButtonsforRecords || []).find(r => r.id === record.id) || {}
          let evaluatedBtnIds = isEmpty(evaluatedButtonIds)
            ? []
            : evaluatedButtonIds

          buttonIds.push(evaluatedBtnIds)
        })

        let customButtonsIds = intersection(...buttonIds)

        this.customButtons = (customButtons || []).filter(
          button =>
            customButtonsIds.includes(button.id) &&
            button.positionType === position
        )
      }
    },
    handleCommandForDropdownButton(cmdObj) {
      let { command, uniqueKey } = cmdObj || {}
      if (uniqueKey === 'customButton') {
        this.startButtonAction(command)
      } else {
        this.$emit('moreActionButtons', cmdObj)
      }
    },
    async startButtonAction(selectedButton) {
      this.freezeRecordInList(true)

      this.selectedButton = selectedButton
      this.actionLoading = true
      if (this.position !== POSITION_TYPE.LIST_BAR) {
        this.loading = true
      }
      await this.executeAction()
      this.loading = false
      this.actionLoading = false

      this.freezeRecordInList(false)
    },
    freezeRecordInList(loading) {
      if (this.position === POSITION_TYPE.LIST_ITEM)
        this.$emit('freezeRecord', loading)
    },
    async executeAction() {
      let { moduleName, record, selectedButton, position } = this
      let actionType = getActionType(this.selectedButton)
      this.currentActionType = actionType
      if (actionType === ACTION_TYPES.REDIRECT_URL) {
        await redirectUrlAction({
          url: this.$getProperty(this, 'selectedButton.config.url'),
          record: this.record,
          type: redirectType.DIFFERENT_TAB,
          moduleName,
        })
      } else if (
        [ACTION_TYPES.OPEN_FORM, ACTION_TYPES.OPEN_SUMMARY].includes(actionType)
      ) {
        let { config = {} } = selectedButton
        let { moduleName: targetModuleName } = config
        let canCreate = false
        let { tabId } = findTab(tabTypes.MODULE, {
          moduleName: targetModuleName,
        })
        if (!isEmpty(tabId)) {
          let webTabObj = this.getTabByTabId(tabId)
          canCreate = this.tabHasPermission('CREATE', webTabObj)
        }
        await internalUrlAction(
          {
            config: config,
            record: this.record,
            type: redirectType.DIFFERENT_TAB,
            moduleName,
          },
          canCreate
        )
      } else if (actionType === ACTION_TYPES.CONNECTED_APPS) {
        this.widgetId = this.$getProperty(
          this,
          'selectedButton.config.widgetId'
        )
        this.showConnectedAppWidget = true
      } else if (
        actionType === ACTION_TYPES.CREATE_RECORD ||
        actionType === ACTION_TYPES.UPDATE_RECORD
      ) {
        if (
          actionType === ACTION_TYPES.UPDATE_RECORD &&
          position === POSITION_TYPE.LIST_BAR
        ) {
          await this.startBulkUpdateAction()
        } else {
          await this.startRecordActionFlow()
        }
      } else if (actionType === ACTION_TYPES.OTHER_ACTIONS) {
        let url = this.getUrl(selectedButton)

        let params
        if (this.position === POSITION_TYPE.LIST_BAR) {
          params = {
            customButtonId: selectedButton.id,
            moduleName: moduleName,
            data: {
              [moduleName]: this.selectedRecords.map(record => {
                return { id: record.id }
              }),
            },
          }
          return API.post('v3/modules/data/bulkpatch', params).then(
            this.otherActionResponse
          )
        } else {
          if (
            !this.isPortalApp &&
            moduleName === 'workorder' &&
            !this.isWoStateTransitionLicenseEnabled
          ) {
            params = this.transformFn({
              moduleName: moduleName,
              id: record?.id,
              customButtonId: selectedButton.id,
            })
            return API.post(url, params).then(this.otherActionResponse)
          } else {
            params = {
              id: record?.id,
              customButtonId: selectedButton.id,
              data: {},
            }
            let { id: recordId } = record || {}
            let url = `v3/action/${moduleName}/${recordId}/customButton`

            return API.patch(url, params).then(this.otherActionResponse)
          }
        }
      }
    },
    startRecordActionFlow() {
      this.showRecordActionForm = true
    },
    startBulkUpdateAction() {
      this.showBulkActionForm = true
    },
    loadData() {
      this.$emit('onSuccess')
      this.$emit('refresh')
    },
    responseHandler(response) {
      if (!response) {
        this.$emit('onError')
      } else {
        this.$emit('refresh')
        this.$emit('onSuccess')
      }
    },
    getUrl(buttonItem) {
      if (isFunction(this.updateUrl)) {
        return this.updateUrl(buttonItem)
      } else {
        return this.updateUrl
      }
    },
    otherActionResponse({ error }) {
      if (error) {
        this.$message.error(error.message || 'Error occured')
        this.responseHandler(false)
      } else {
        this.$message.success('Action executed successfully')

        this.responseHandler(true)
      }
    },
    dropdownVisibilityChange(val) {
      let { toggleVisibility } = this
      this.dropdownVisible = val
      if (!isEmpty(toggleVisibility)) {
        let { record } = this || {}
        let { id } = record || {}
        this.toggleVisibility(id, val)
      }
    },
  },
}
</script>
<style lang="scss">
.custombtn-container {
  display: flex;
  align-items: center;

  .el-button {
    border: 1px solid #594de1;
    background-color: white;
    color: #594de1;
    cursor: pointer;
    border-radius: 2px;

    &:hover {
      border: 1px solid #594de1;
      background-color: #594de1;
      color: white;
      box-shadow: 0 2px 4px 0 #594de16b;
    }
  }
  .bulk-btn {
    min-height: 28px;
  }

  .custombtn-name {
    max-width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .el-button-group {
    max-width: 160px;
    display: flex;

    .el-button {
      max-width: 130px;
    }
  }
  .el-button-group:hover {
    .el-button {
      border: 1px solid #594de16b;
      background-color: #594de1;
      color: white;
      box-shadow: 0 2px 4px 0 #594de1;
    }
  }

  .el-dropdown__caret-button {
    width: 30px !important;
    border-left: none;
    background-color: #f1efff;
    color: #594de1;
    &:focus {
      background-color: #f1efff;
    }
    &:hover {
      border-left: none;
    }
  }
  .more-icon {
    border-radius: 20px;
    padding: 5px;

    &:hover {
      background-color: #f7f8f9;
    }
  }
}
.more-action-btns {
  .el-dropdown-menu__item {
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 32px !important;
    font-size: 12px !important;

    &:hover {
      color: #594de1;
    }
  }
}
.connected-app-dialog {
  .el-dialog__body {
    padding: 0px !important;
  }
  .el-dialog__header {
    padding: 0px !important;
  }
}
</style>
<style lang="scss" scoped>
.custombtn-bulk-action {
  padding: 5px 10px;
  border: 1px solid #dadfe3;
  color: #324056;
  letter-spacing: 0.3px;
  text-transform: uppercase;
  font-size: 12px;
  line-height: 16px;
}
.el-dropdown-menu {
  margin-top: 5px !important;
  min-width: 210px;

  .el-dropdown-menu__item {
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 32px !important;
    font-size: 12px !important;

    &:hover {
      color: #594de1;
    }
  }
}

.lines {
  height: 16px;
  width: 90%;
  margin: 12px 10px;
  border-radius: 8px;
}
</style>
