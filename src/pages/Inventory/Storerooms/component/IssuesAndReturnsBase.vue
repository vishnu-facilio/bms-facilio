<template>
  <div>
    <LineItemList
      v-bind="$attrs"
      ref="lineItemList"
      :config="listConfiguration"
      :moduleName="moduleName"
      :moduleDisplayName="moduleDisplayName"
      :widgetDetails="widgetDetails"
      :hideNotification="true"
      :additionalParams="additionalParams"
      @onCreateOrUpdate="issuedRecordData"
      @clickedActionBtn="btnValue"
      viewname="all"
      class="flex-grow"
    >
      <template #empty-state-icon-text>
        <inline-svg
          src="svgs/storeroom/issue-to-wo"
          iconClass="p10"
        ></inline-svg>
        <div class="line-item-list-empty-state-text">
          {{ $t('common.inventory.issues_empty_msg') }}
        </div>
      </template>
      <template #empty-state-btn>
        <div class="line-item-list-footer">
          <el-button
            v-for="(btnObj, index) in emptyStateBtnList"
            :key="`footer-btn-${index}`"
            type="primary"
            @click="clickAction(btnObj.value)"
            class="line-item-list-footer-add-btn"
          >
            <div class="d-flex" style="height: 14px;">
              {{ btnObj.label }}
            </div>
          </el-button>
          <el-button
            v-for="(btnObj, index) in additionalBtnList"
            :key="`additional-btn-${index}`"
            type="primary"
            @click="btnObj.action(btnObj)"
            class="line-item-list-footer-add-btn"
          >
            <div class="d-flex" style="height: 14px;">
              {{ btnObj.label }}
            </div>
          </el-button>
        </div>
      </template>
    </LineItemList>
    <portal to="pagebuilder-fixed-top">
      <div class="issuance-success-msg-container" v-if="showSuccessMsg">
        <div class="success-msg-container">
          <InlineSvg
            src="svgs/storeroom/white-check-circle"
            iconClass="icon icon-sm-md"
            class="success-icon"
          ></InlineSvg>
          <span class="item-name"> {{ typeName }}</span>
          <span class="issue-success-msg">
            {{ $t('common.inventory.has_been_issued_to') }}</span
          >
          <span class="work-order-name"> {{ workOrderName }}</span>
        </div>

        <span @click="showSuccessMsg = false" class="close-icon">
          <InlineSvg
            src="svgs/storeroom/ic-clear"
            iconClass="icon icon-sm-md"
          ></InlineSvg>
        </span>
      </div>
    </portal>
    <tool-issue-return
      v-if="showToolsIssue"
      :storeRoom="details"
      type="issue"
      :showToolIssueReturn.sync="showToolsIssue"
      @refreshInventory="refreshData"
    ></tool-issue-return>
    <tool-issue-return
      v-if="showToolsReturn"
      :storeRoom="details"
      type="return"
      :showToolIssueReturn.sync="showToolsReturn"
      @refreshInventory="refreshData"
    ></tool-issue-return>
  </div>
</template>
<script>
import LineItemList from 'src/components/page/widget/common/line-items/LineItemList.vue'
import { isEmpty } from '@facilio/utils/validation'
import InventoryMixin from 'pages/Inventory/InventoryMixin'
import ToolIssueReturn from 'src/pages/Inventory/Storerooms/ToolStoreIssueReturn.vue'

export default {
  mixins: [InventoryMixin],
  props: ['details'],
  components: {
    LineItemList,
    ToolIssueReturn,
  },
  data() {
    return {
      showSuccessMsg: false,
      issuedInventory: null,
      inventoryReservationId: null,
      actionButtonValue: null,
      recordsCount: null,
      isRotatingType: null,
      showToolsIssue: false,
      showToolsReturn: false,
    }
  },
  computed: {
    additionalParams() {
      let { inventoryReservationId, actionButtonValue } = this || {}
      let { lookupModuleName } = actionButtonValue || {}
      if (
        !isEmpty(inventoryReservationId) &&
        lookupModuleName === 'inventoryReservation'
      ) {
        return {
          inventoryReservation: { id: inventoryReservationId },
        }
      }
      return {}
    },
    additionalBtnList() {
      // to be added by extended components
      return []
    },

    formConfig() {
      let { actionButtonValue, isRotatingType } = this || {}
      let { lookupModuleName } = actionButtonValue || {}
      return {
        formType: 'POP_UP_FORM',
        formTitle: {
          defaultForm: this.$t('common.inventory.issue_to_workorder'),
        },
        modifyFieldPropsHook: field => {
          let { name } = field || {}
          if (name === 'storeRoom') {
            return {
              ...field,
              isDisabled: true,
              required: true,
              value: this.$getProperty(this.details, 'id'),
            }
          }
          // if (name === 'issueTo') {
          //   return {
          //     ...field,
          //     value: this.$getProperty(this.details, 'id'),
          //   }
          // }
          if (name === 'workorder') {
            return lookupModuleName === 'inventoryReservation'
              ? { ...field, required: true, isDisabled: true }
              : { ...field, required: true }
          }
          if (name === 'item') {
            return { ...field, isDisabled: true }
          }
          if (name === 'tool') {
            return { ...field, isDisabled: true }
          }
          if (name === 'quantity' && isRotatingType) {
            return { ...field, isDisabled: true }
          }
          if (name === 'asset') {
            return !isRotatingType
              ? { ...field, hideField: true }
              : { ...field, required: true }
          }
        },
      }
    },
    listConfiguration() {
      let { formConfig } = this
      return {
        getRecordList: () => {
          return []
        },
        ...(formConfig || {}),
        canHideHeader: true,
      }
    },
    widgetDetails() {
      let { emptyStateBtnList } = this
      return {
        canHideHeader: true,
        actionButtonList: emptyStateBtnList,
      }
    },
    // check if it needs to move to item component as tools must be issued only to a person
    // if yes, change the name to issuedTo and move it to specific child components
    workOrderName() {
      return this.$getProperty(
        this.issuedInventory,
        'workorder.subject',
        this.$t('common.inventory.the_mentioned_workorder')
      )
    },
  },
  methods: {
    btnValue(value) {
      this.actionButtonValue = value
    },
    clickAction(btnObjValue) {
      this.$refs['lineItemList']?.handleClickActionBtn(btnObjValue)
    },
    refreshData() {
      return
    },
    issuedRecordData(response) {
      let { error, workorderItem } = response || {}
      if (error) {
        this.$message.error(error || this.$t('common.inventory.error_occurred'))
      } else {
        this.issuedInventory = workorderItem
        this.showSuccessMsg = true
        setTimeout(() => {
          this.showSuccessMsg = false
        }, 5000)
      }
      this.inventoryReservationId = null
    },
  },
}
</script>
<style scoped lang="scss">
.line-item-list-footer {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  padding: 16px 24px;
  overflow: scroll;

  .line-item-list-footer-add-btn {
    border-radius: 4px;
    border: solid 1px #3ab2c2;
    background-color: #fff;
    font-size: 14px;
    font-weight: 500;
    color: #324056;
    padding: 10px 12px;

    &:hover {
      color: #fff;
      background-color: #3cbfd0;
    }
  }
  .el-button + .el-button {
    margin-left: 16px;
  }
}
.issuance-success-msg-container {
  border-left: 4px solid #29a360;
  background-color: #def7e9;
  text-align: center;
  padding: 10px 0px;
  margin-top: 2px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.item-name {
  color: #324056;
  font-weight: 500;
  padding-right: 4px;
}
.work-order-name {
  color: #324056;
  font-weight: 500;
  padding-left: 4px;
}
.issue-success-msg {
  color: #324056;
}
.success-icon {
  height: 16px;
  margin-right: 4px;
}
.close-icon {
  height: 16px;
  margin-right: 4px;
  cursor: pointer;
}
.success-msg-container {
  margin: auto;
  display: flex;
  align-items: center;
}
</style>
