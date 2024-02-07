<template>
  <div>
    <div v-if="visibility">
      <el-dialog
        :visible.sync="visibility"
        title="NOTIFICATION PREFRENCES"
        :before-close="cancelForm"
        :key="storeRoom.id"
        custom-class="fc-dialog-center-container"
      >
        <div v-if="loading" class="height300">
          <spinner :show="loading" class="mT20" size="80"></spinner>
        </div>
        <div v-else class="height300">
          <el-row class="mB20" :gutter="20">
            <el-col :span="24">
              <el-checkbox
                v-model="formData.outOfStockNotification"
                @change="outOfStockCheckboxActions"
                >Notify me when item is out of stock</el-checkbox
              >
            </el-col>
          </el-row>
          <el-row
            v-if="formData.outOfStockNotification"
            class="mB20"
            :gutter="20"
          >
            <el-col class="el-select-block" :span="12">
              <el-select
                v-model="formData.outOfStockNotificationList"
                class="fc-input-full-border-select2 width100"
                multiple
                filterable
                collapse-tags
              >
                <el-option
                  v-for="user in users"
                  :key="user.name"
                  :label="user.name"
                  :value="user.id"
                  class="subject"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
          <el-row class="mB20" :gutter="20">
            <el-col :span="24">
              <el-checkbox
                v-model="formData.minQuantityNotification"
                @change="minCheckboxActions"
                >Notify me when Item nears Minimum Limit</el-checkbox
              >
            </el-col>
          </el-row>
          <el-row
            v-if="formData.minQuantityNotification"
            class="mB20"
            :gutter="20"
          >
            <el-col class="el-select-block" :span="12">
              <el-select
                v-model="formData.minQuantityNotificationList"
                class="fc-input-full-border-select2 width100"
                multiple
                filterable
                collapse-tags
              >
                <el-option
                  v-for="user in users"
                  :key="user.id"
                  :label="user.name"
                  :value="user.id"
                  class="subject"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()"
            >CANCEL</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="saveNotificationPrefrences()"
            :loading="saving"
            >{{ saving ? 'Submitting...' : 'SAVE' }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
// import { Promise } from 'q'
export default {
  props: ['visibility', 'storeRoom'],
  data() {
    return {
      saving: false,
      loading: false,
      formData: {
        outOfStockNotification: false,
        outOfStockNotificationList: [],
        minQuantityNotificationList: [],
        minQuantityNotification: false,
      },
      paramData: [],
      editData: null,
      rulesForUpdate: {
        minimum: {
          hasValue: false,
          list: {
            item: null,
            tool: null,
          },
        },
        outOfStock: {
          hasValue: false,
          list: {
            item: null,
            tool: null,
          },
        },
      },
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },
  mounted() {
    this.loadDetails()
  },
  methods: {
    loadDetails() {
      let param = {
        storeRoomId: this.storeRoom.id,
      }
      this.loading = true
      this.$http
        .post('v2/storeroom/rules/fetchRule', param)
        .then(response => {
          if (response.data.responseCode == 0) {
            this.editData = response.data.result.workflowRuleList
            this.mapEditData()
            this.loading = false
          } else {
            this.$message.error(response.data.message)
            this.loading = false
          }
        })
        .catch(() => {
          this.loading = false
        })
    },
    mapEditData() {
      for (let data of this.editData) {
        if (data.ruleType === 33) {
          this.formData.minQuantityNotification = true
          if (this.formData.minQuantityNotificationList.length === 0) {
            this.formData.minQuantityNotificationList = this.$common.getUsersFromTemplate(
              data.actions[0].template
            )
          }
          this.rulesForUpdate.minimum.hasValue = true
          if (data.event.moduleName === 'item') {
            this.rulesForUpdate.minimum.list.item = data.id
          }
          if (data.event.moduleName === 'tool') {
            this.rulesForUpdate.minimum.list.tool = data.id
          }
        }
        if (data.ruleType === 32) {
          this.formData.outOfStockNotification = true
          if (this.formData.outOfStockNotificationList.length === 0) {
            this.formData.outOfStockNotificationList = this.$common.getUsersFromTemplate(
              data.actions[0].template
            )
          }
          this.rulesForUpdate.outOfStock.hasValue = true
          if (data.event.moduleName === 'item') {
            this.rulesForUpdate.outOfStock.list.item = data.id
          }
          if (data.event.moduleName === 'tool') {
            this.rulesForUpdate.outOfStock.list.tool = data.id
          }
        }
      }
    },
    cancelForm() {
      this.$emit('update:visibility', false)
    },
    saveNotificationPrefrences() {
      this.validateData()
    },
    validateData() {
      if (this.formData.outOfStockNotification) {
        if (this.formData.outOfStockNotificationList.length === 0) {
          this.$message.error('Enter Atleast One User to send Notification')
          return
        }
      }
      if (this.formData.minQuantityNotification) {
        if (this.formData.minQuantityNotificationList.length === 0) {
          this.$message.error('Enter Atleast One User to send Notification')
          return
        }
      }
      this.dataFormatConstruct()
    },
    dataFormatConstruct() {
      let paramItemMin = {
        rule: {
          name: 'item reaching minimum quantity',
          scheduleType: 3,
          description: 'testing',
          ruleType: 33,
          event: {
            moduleName: 'item',
            activityType: 1048576,
          },
          criteria: {
            pattern: '(1 and 2)',
            conditions: {
              1: {
                fieldName: 'quantity',
                value: 'item.minimumQuantity',
                columnName: 'Item.QUANTITY',
                operatorId: 77,
                isResourceOperator: false,
                parseLabel: null,
                active: true,
                isSpacePicker: false,
              },
              2: {
                fieldName: 'storeRoom',
                isResourceOperator: false,
                parseLabel: null,
                value: this.storeRoom.id,
                active: true,
                isSpacePicker: false,
                operatorId: 36,
                columnName: 'Item.STORE_ROOM_ID',
              },
            },
            resourceOperator: false,
          },
          siteId: this.storeRoom.site ? this.storeRoom.site.id : null,
        },
        actions: [
          {
            actionType: 3,
            templateJson: {
              type: 1,
              message:
                'Hi,\nThis is to notify you that #${item.id} ${item.itemType.name} in ${item.storeRoom.name} is nearing lowest inventory limit.\nPlease click the link https://app.facilio.com/app/purchase/po/new to initiate new stock request. \nRegards,\nFacilio',
              name: 'Storeroom Notification Template',
              workflow: {
                parameters: [
                  {
                    name: 'org.domain',
                    typeString: 'String',
                  },
                  {
                    name: 'item.quantity',
                    typeString: 'String',
                  },
                  {
                    name: 'item.itemType.name',
                    typeString: 'String',
                  },
                  {
                    name: 'item.storeRoom.name',
                    typeString: 'String',
                  },
                  {
                    name: 'item.id',
                    typeString: 'String',
                  },
                ],
                expressions: [
                  {
                    name: 'org.domain',
                    constant: '${org.domain}',
                  },
                  {
                    name: 'item.quantity',
                    constant: '${item.quantity}',
                  },
                  {
                    name: 'item.itemType.name',
                    constant: '${item.itemType.name}',
                  },
                  {
                    name: 'item.storeRoom.name',
                    constant: '${item.storeRoom.name}',
                  },
                  {
                    name: 'item.id',
                    constant: '${item.id}',
                  },
                ],
                workflowString: null,
              },
              ftl: false,
              to: '',
              subject:
                'Alert: ${item.itemType.name} - About to Reach Out of Stock',
            },
          },
        ],
      }
      let paramItemOos = {
        rule: {
          name: 'item Out of Stock quantity',
          scheduleType: 3,
          description: 'testing',
          ruleType: 32,
          event: {
            moduleName: 'item',
            activityType: 1048576,
          },
          criteria: {
            pattern: '(1 and 2)',
            conditions: {
              1: {
                fieldName: 'quantity',
                value: '0',
                columnName: 'Item.QUANTITY',
                operatorId: 12,
                isResourceOperator: false,
                parseLabel: null,
                active: true,
                isSpacePicker: false,
              },
              2: {
                fieldName: 'storeRoom',
                isResourceOperator: false,
                parseLabel: null,
                value: this.storeRoom.id,
                active: true,
                isSpacePicker: false,
                operatorId: 36,
                columnName: 'Item.STORE_ROOM_ID',
              },
            },
            resourceOperator: false,
          },
          siteId: this.storeRoom.site ? this.storeRoom.site.id : null,
        },
        actions: [
          {
            actionType: 3,
            templateJson: {
              type: 1,
              message:
                'Hi,\nThis is to Notify you that #{item.id} ${item.itemType.name} in ${item.storeRoom.name} is currently Out of Stock.\nPlease click the link https://app.facilio.com/app/purchase/po/new to initiate new stock request.\nRegards,\n ' +
                this.$getProperty(window.brandConfig, 'name', 'Facilio'),
              name: 'Storeroom Notification Template',
              workflow: {
                parameters: [
                  {
                    name: 'org.domain',
                    typeString: 'String',
                  },
                  {
                    name: 'item.quantity',
                    typeString: 'String',
                  },
                  {
                    name: 'item.itemType.name',
                    typeString: 'String',
                  },
                  {
                    name: 'item.storeRoom.name',
                    typeString: 'String',
                  },
                  {
                    name: 'item.id',
                    typeString: 'String',
                  },
                ],
                expressions: [
                  {
                    name: 'org.domain',
                    constant: '${org.domain}',
                  },
                  {
                    name: 'item.quantity',
                    constant: '${item.quantity}',
                  },
                  {
                    name: 'item.itemType.name',
                    constant: '${item.itemType.name}',
                  },
                  {
                    name: 'item.storeRoom.name',
                    constant: '${item.storeRoom.name}',
                  },
                  {
                    name: 'item.id',
                    constant: '${item.id}',
                  },
                ],
                workflowString: null,
              },
              ftl: false,
              to: '',
              subject: 'Alert: ${item.itemType.name} - Out of Stock',
            },
          },
        ],
      }
      let paramToolMin = {
        rule: {
          name: 'tool reaching minimum quantity',
          scheduleType: 3,
          description: 'testing',
          ruleType: 33,
          event: {
            moduleName: 'tool',
            activityType: 1048576,
          },
          criteria: {
            pattern: '(1 and 2)',
            conditions: {
              1: {
                fieldName: 'currentQuantity',
                value: 'tool.minimumQuantity',
                columnName: 'Tool.CURRENT_QUANTITY',
                operatorId: 77,
                isResourceOperator: false,
                parseLabel: null,
                active: true,
                isSpacePicker: false,
              },
              2: {
                fieldName: 'storeRoom',
                isResourceOperator: false,
                parseLabel: null,
                value: this.storeRoom.id,
                active: true,
                isSpacePicker: false,
                operatorId: 36,
                columnName: 'Tool.STORE_ROOM_ID',
              },
            },
            resourceOperator: false,
          },
          siteId: this.storeRoom.site ? this.storeRoom.site.id : null,
        },
        actions: [
          {
            actionType: 3,
            templateJson: {
              type: 1,
              message:
                'Hi,\nThis is to notify you that #${tool.id} ${tool.toolType.name} in ${tool.storeRoom.name} is nearing lowest inventory limit.\nPlease click the link https://app.facilio.com/app/purchase/po/new to initiate new stock request.\n Regards,\nFacilio',
              name: 'Storeroom Notification Template',
              workflow: {
                parameters: [
                  {
                    name: 'org.domain',
                    typeString: 'String',
                  },
                  {
                    name: 'tool.currentQuantity',
                    typeString: 'String',
                  },
                  {
                    name: 'tool.toolType.name',
                    typeString: 'String',
                  },
                  {
                    name: 'tool.storeRoom.name',
                    typeString: 'String',
                  },
                  {
                    name: 'tool.id',
                    typeString: 'String',
                  },
                ],
                expressions: [
                  {
                    name: 'org.domain',
                    constant: '${org.domain}',
                  },
                  {
                    name: 'tool.currentQuantity',
                    constant: '${tool.currentQuantity}',
                  },
                  {
                    name: 'tool.toolType.name',
                    constant: '${tool.toolType.name}',
                  },
                  {
                    name: 'tool.storeRoom.name',
                    constant: '${tool.storeRoom.name}',
                  },
                  {
                    name: 'tool.id',
                    constant: '${tool.id}',
                  },
                ],
                workflowString: null,
              },
              ftl: false,
              to: '',
              subject:
                'Alert:${tool.toolType.name} - About to Reach Out of Stock',
            },
          },
        ],
      }
      let paramToolOos = {
        rule: {
          name: 'tool out of stock quantity',
          scheduleType: 3,
          description: 'testing',
          ruleType: 32,
          event: {
            moduleName: 'tool',
            activityType: 1048576,
          },
          criteria: {
            pattern: '(1 and 2)',
            conditions: {
              1: {
                fieldName: 'currentQuantity',
                value: '0',
                columnName: 'Tool.CURRENT_QUANTITY',
                operatorId: 12,
                isResourceOperator: false,
                parseLabel: null,
                active: true,
                isSpacePicker: false,
              },
              2: {
                fieldName: 'storeRoom',
                isResourceOperator: false,
                parseLabel: null,
                value: this.storeRoom.id,
                active: true,
                isSpacePicker: false,
                operatorId: 36,
                columnName: 'Tool.STORE_ROOM_ID',
              },
            },
            resourceOperator: false,
          },
          siteId: this.storeRoom.site ? this.storeRoom.site.id : null,
        },
        actions: [
          {
            actionType: 3,
            templateJson: {
              type: 1,
              message:
                'Hi,\nThis is to notify you that #${tool.id} ${tool.toolType.name} in ${tool.storeRoom.name} is currently out of stock.\nPlease click the link https://app.facilio.com/app/purchase/po/new to initiate new stock request.\nRegards,\nFacilio',
              name: 'Storeroom Notification Template',
              workflow: {
                parameters: [
                  {
                    name: 'org.domain',
                    typeString: 'String',
                  },
                  {
                    name: 'tool.currentQuantity',
                    typeString: 'String',
                  },
                  {
                    name: 'tool.toolType.name',
                    typeString: 'String',
                  },
                  {
                    name: 'tool.storeRoom.name',
                    typeString: 'String',
                  },
                  {
                    name: 'tool.id',
                    typeString: 'String',
                  },
                ],
                expressions: [
                  {
                    name: 'org.domain',
                    constant: '${org.domain}',
                  },
                  {
                    name: 'tool.currentQuantity',
                    constant: '${tool.currentQuantity}',
                  },
                  {
                    name: 'tool.toolType.name',
                    constant: '${tool.toolType.name}',
                  },
                  {
                    name: 'tool.storeRoom.name',
                    constant: '${tool.storeRoom.name}',
                  },
                  {
                    name: 'tool.id',
                    constant: '${tool.id}',
                  },
                ],
                workflowString: null,
              },
              ftl: false,
              to: '',
              subject: 'Alert: ${tool.toolType.name} - Out of Stock',
            },
          },
        ],
      }
      for (let i in this.formData.outOfStockNotificationList) {
        paramItemOos.actions[0].templateJson.workflow.expressions.push({
          name: 'user_email1',
          fieldName: 'email',
          moduleName: 'users',
          aggregateString: '[0]',
          criteria: {
            pattern: 1,
            conditions: {
              1: {
                fieldName: 'ouid',
                operatorId: 9,
                sequence: 1,
                value: this.formData.outOfStockNotificationList[i],
              },
            },
          },
        })
        paramToolOos.actions[0].templateJson.workflow.expressions.push({
          name: 'user_email1',
          fieldName: 'email',
          moduleName: 'users',
          aggregateString: '[0]',
          criteria: {
            pattern: 1,
            conditions: {
              1: {
                fieldName: 'ouid',
                operatorId: 9,
                sequence: 1,
                value: this.formData.outOfStockNotificationList[i],
              },
            },
          },
        })
        let l = parseInt(i) + 1
        if (
          parseInt(i) ===
          this.formData.outOfStockNotificationList.length - 1
        ) {
          paramItemOos.actions[0].templateJson.to =
            paramItemOos.actions[0].templateJson.to + '${user_email' + l + ':-}'
          paramToolOos.actions[0].templateJson.to =
            paramToolOos.actions[0].templateJson.to + '${user_email' + l + ':-}'
        } else {
          paramItemOos.actions[0].templateJson.to =
            paramItemOos.actions[0].templateJson.to +
            '${user_email' +
            l +
            ':-}, '
          paramToolOos.actions[0].templateJson.to =
            paramToolOos.actions[0].templateJson.to +
            '${user_email' +
            l +
            ':-}, '
        }
      }
      for (let j in this.formData.minQuantityNotificationList) {
        paramItemMin.actions[0].templateJson.workflow.expressions.push({
          name: 'user_email1',
          fieldName: 'email',
          moduleName: 'users',
          aggregateString: '[0]',
          criteria: {
            pattern: 1,
            conditions: {
              1: {
                fieldName: 'ouid',
                operatorId: 9,
                sequence: 1,
                value: this.formData.minQuantityNotificationList[j],
              },
            },
          },
        })
        paramToolMin.actions[0].templateJson.workflow.expressions.push({
          name: 'user_email1',
          fieldName: 'email',
          moduleName: 'users',
          aggregateString: '[0]',
          criteria: {
            pattern: 1,
            conditions: {
              1: {
                fieldName: 'ouid',
                operatorId: 9,
                sequence: 1,
                value: this.formData.minQuantityNotificationList[i],
              },
            },
          },
        })
        let k = parseInt(j) + 1
        if (
          parseInt(j) ===
          parseInt(this.formData.minQuantityNotificationList.length - 1)
        ) {
          paramItemMin.actions[0].templateJson.to =
            paramItemMin.actions[0].templateJson.to + '${user_email' + k + ':-}'
          paramToolMin.actions[0].templateJson.to =
            paramToolMin.actions[0].templateJson.to + '${user_email' + k + ':-}'
        } else {
          paramItemMin.actions[0].templateJson.to =
            paramItemMin.actions[0].templateJson.to +
            '${user_email' +
            k +
            ':-}, '
          paramToolMin.actions[0].templateJson.to =
            paramToolMin.actions[0].templateJson.to +
            '${user_email' +
            k +
            ':-}, '
        }
      }
      let deleteIdsList = []
      if (
        this.rulesForUpdate.minimum.hasValue &&
        !this.formData.minQuantityNotification
      ) {
        deleteIdsList.push(this.rulesForUpdate.minimum.list.item)
        deleteIdsList.push(this.rulesForUpdate.minimum.list.tool)
      } else if (
        this.rulesForUpdate.minimum.hasValue &&
        this.formData.minQuantityNotification
      ) {
        paramItemMin.rule['id'] = this.rulesForUpdate.minimum.list.item
        paramToolMin.rule['id'] = this.rulesForUpdate.minimum.list.tool
      }
      if (
        this.rulesForUpdate.outOfStock.hasValue &&
        !this.formData.outOfStockNotification
      ) {
        deleteIdsList.push(this.rulesForUpdate.outOfStock.list.item)
        deleteIdsList.push(this.rulesForUpdate.outOfStock.list.tool)
      } else if (
        this.rulesForUpdate.outOfStock.hasValue &&
        this.formData.outOfStockNotification
      ) {
        paramItemOos.rule['id'] = this.rulesForUpdate.outOfStock.list.item
        paramToolOos.rule['id'] = this.rulesForUpdate.outOfStock.list.tool
      }

      this.paramData = []
      if (this.formData.minQuantityNotification) {
        this.paramData.push(paramItemMin)
        this.paramData.push(paramToolMin)
      }
      if (this.formData.outOfStockNotification) {
        this.paramData.push(paramItemOos)
        this.paramData.push(paramToolOos)
      }
      let promises = []
      if (deleteIdsList.length > 0) {
        promises.push(
          this.$http.post('v2/storeroom/rules/deleteRule', {
            ruleIds: deleteIdsList,
          })
        )
      }
      if (this.paramData.length > 0) {
        promises.push(
          this.$http.post('v2/storeroom/rules/addOrUpdateRule', {
            storeRoomId: this.storeRoom.id,
            rules: this.paramData,
          })
        )
      }
      if (promises.length > 0) {
        Promise.all(promises).then(response => {
          if (promises.length === 1) {
            if (response[0].data.responseCode === 0) {
              this.$message.success('Rules Configured')
              this.$emit('update:visibility', false)
            } else {
              this.$message.error(response.data.message)
            }
          } else if (promises.length === 2) {
            if (
              response[0].data.responseCode === 0 &&
              response[1].data.responseCode === 0
            ) {
              this.$message.success('Rules Configured')
              this.$emit('update:visibility', false)
            } else {
              this.$message.error(response.data.message)
            }
          }
        })
      }
    },
    deleteApiCall(ids) {
      let param = {
        ruleIds: ids,
      }
      this.$http.post('v2/storeroom/rules/deleteRule', param).then(response => {
        if (response.data.responseCode === 0) {
          this.$message.success('Rules Configured')
        } else {
          this.$message.error(response.data.message)
        }
      })
    },
    apiCallMethod() {
      let param = {
        storeRoomId: this.storeRoom.id,
        rules: this.paramData,
      }
      this.$http
        .post('v2/storeroom/rules/addOrUpdateRule', param)
        .then(response => {
          if (response.data.responseCode == 0) {
            this.$message.success('Rules Configured')
            this.$emit('update:visibility', false)
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(() => {})
    },
    outOfStockCheckboxActions(val) {
      if (!val) {
        this.formData.outOfStockNotificationList = []
      }
    },
    minCheckboxActions(val) {
      if (!val) {
        this.formData.minQuantityNotificationList = []
      }
    },
  },
}
</script>
