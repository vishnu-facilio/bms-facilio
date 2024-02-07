<template>
  <div>
    <div v-if="loading" class="mT50">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div class="facilio-inventory-web-form-body" v-else-if="toolBulkAddForm">
      <el-dialog
        :visible.sync="toolBulkAddForm"
        width="70%"
        title="TOOL BULK ADD"
        :before-close="cancelForm"
        :key="storeRoom.id"
        custom-class="fc-dialog-center-container inventory-store-dialog fc-web-form-dialog"
      >
        <table class="setting-list-view-table store-table">
          <thead>
            <tr>
              <th
                class="setting-table-th setting-th-text uppercase"
                style="width: 240px;"
              >
                {{ $t('common.header.tool_type') }}
              </th>
              <th
                class="setting-table-th setting-th-text uppercase"
                style="width: 240px;"
              >
                {{ $t('common._common.quantity') }}
              </th>
              <th
                :class="[
                  !checkForMultiCurrency('rate', metaFieldTypeMap) &&
                    'setting-table-th',
                  'setting-th-text uppercase',
                ]"
                style="width: 240px;"
              >
                {{ $t('common.header.rate_per_hour') }}
              </th>
              <th
                class="setting-table-th setting-th-text uppercase"
                style="width: 240px;"
              >
                {{ $t('common.tabs.cost') }}
              </th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="(addTool, index) in formData"
              :key="index"
              class="visibility-visible-actions"
            >
              <td class="module-builder-td">
                <el-select
                  filterable
                  clearable
                  v-model="addTool.toolType.id"
                  class="fc-input-full-border-select2 width250px"
                >
                  <el-option
                    v-for="(option, i) in toolTypesList"
                    v-if="!isSelected(option.id, index)"
                    :key="i"
                    :label="option.name"
                    :value="option.id"
                  ></el-option>
                </el-select>
              </td>
              <td class="module-builder-td">
                <el-input
                  placeholder="Quantity"
                  type="number"
                  v-model="addTool.quantity"
                  class="fc-input-full-border-select2 width110px"
                ></el-input>
              </td>
              <td
                v-if="checkForMultiCurrency('rate', metaFieldTypeMap)"
                class="item-unit-price-currency"
              >
                <FNewCurrencyField
                  :key="addTool.currencyCode"
                  v-model="addTool.rate"
                  :isSubform="true"
                  :moduleData="addTool"
                  @setCurrencyCodeInSubform="
                    (code, rate) => setCurrencyCodeInSubform(code, rate, index)
                  "
                  @calculateExchangeRate="
                    rateObj => calculateExchangeRate(rateObj, index)
                  "
                ></FNewCurrencyField>
              </td>
              <td v-else>
                <el-input
                  placeholder="Rate/Hour"
                  type="number"
                  v-model="addTool.rate"
                  class="fc-input-full-border-select2 width110px"
                ></el-input>
              </td>
              <td>
                <currency
                  :value="
                    addTool.quantity > 0 && addTool.rate > 0
                      ? addTool.quantity * addTool.rate
                      : '0'
                  "
                  :recordCurrency="getrecordCurrency(addTool.currencyCode)"
                ></currency>
              </td>
              <td>
                <div class="visibility-hide-actions export-dropdown-menu">
                  <span
                    @click="addToolFormDataEntry(formData, index)"
                    title="Add"
                    v-tippy
                  >
                    <img src="~assets/add-icon.svg" class="mR10" />
                  </span>
                  <span
                    v-if="formData.length > 1"
                    @click="removeToolFormDataEntry(formData, index)"
                    title="Remove"
                    v-tippy
                  >
                    <img src="~assets/remove-icon.svg" />
                  </span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="cancelForm()"
            >CLOSE</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="saveToolBulkAddForm()"
            :loading="saving"
            >{{ saving ? 'Submitting...' : 'SAVE' }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { loadNonIndTrackedInventory } from 'pages/Inventory/InventoryUtil'
import FNewCurrencyField from 'src/components/FNewCurrencyField.vue'
import {
  getCalculatedCurrencyValue,
  getCurrencyForCurrencyCode,
  getMetaFieldMapForModules,
  checkForMultiCurrency,
} from 'src/pages/setup/organizationSetting/currency/CurrencyUtil.js'

export default {
  props: ['toolList', 'storeRoom', 'toolBulkAddForm'],
  components: { FNewCurrencyField },
  data() {
    return {
      toolTypesList: null,
      formData: [],
      saving: false,
      loading: true,
      createTools: [],
      updateTools: [],
      toolId: null,
      metaFieldTypeMap: {},
      checkForMultiCurrency,
    }
  },
  async mounted() {
    this.loadNotIndTrackToolTypes()
    this.metaFieldTypeMap = await getMetaFieldMapForModules('tool')
  },
  computed: {
    tools() {
      return this.toolList.filter(
        i => i.toolType.isRotating === false || !i.toolType.isRotating
      )
    },
  },
  watch: {},
  methods: {
    async loadNotIndTrackToolTypes() {
      this.loading = true
      let toolTypes = await loadNonIndTrackedInventory('toolTypes')
      if (isEmpty(toolTypes)) {
        this.loading = false
      } else {
        this.toolTypesList = toolTypes
        this.fillFormData()
      }
    },
    isSelected(id, index) {
      for (let i in this.formData) {
        if (this.formData[i].toolType.id === id) {
          if (parseInt(i) === index) {
            return false
          }
          return true
        }
      }
      return false
    },
    cancelForm() {
      this.$emit('update:toolBulkAddForm', false)
    },
    async saveToolBulkAddForm() {
      this.saving = true

      for (let j = this.formData.length - 1; j >= 0; j--) {
        if (
          this.formData[j].hasOwnProperty('quantity') &&
          this.formData[j].quantity === null &&
          this.formData[j].hasOwnProperty('rate') &&
          this.formData[j].rate === null
        ) {
          this.formData.splice(j, 1)
          continue
        }
        let storeroomId = this.$getProperty(this, 'storeRoom.id')
        let toolTypeId = this.$getProperty(this.formData, `${j}.toolType.id`)

        let toolTypeObj = this.$getProperty(this.formData, `${j}.toolType`)
        if (toolTypeObj && toolTypeId) {
          this.formData[j]['storeRoom'] = { id: storeroomId }

          let status = await this.isAlreadyPresentInStore(
            toolTypeId,
            storeroomId
          )
          if (status) {
            this.formData[j]['id'] = this.toolId
            this.updateTools.push(this.formData[j])
          } else {
            this.createTools.push(this.formData[j])
          }
        } else {
          this.formData.splice(j, 1)
          continue
        }
      }
      let promise
      if (this.createTools.length > 0) {
        let url = 'v3/modules/data/bulkCreate'
        let params = {
          data: {
            tool: this.createTools,
          },
          moduleName: 'tool',
        }
        promise = await API.post(url, params)
      }
      if (this.updateTools.length > 0) {
        let url = 'v3/modules/data/bulkpatch'
        let params = {
          data: {
            tool: this.updateTools,
          },
          moduleName: 'tool',
        }
        promise = await API.post(url, params)
      }
      let { error } = (await promise) || {}
      if (error) {
        this.$message.error(
          error.message || 'Error Occurred while stocking tools'
        )
        this.saving = false
      } else {
        this.$message.success(`Added successfully`)
        this.saving = false
        this.cancelForm()
        this.$emit('refresh')
      }
    },
    async isAlreadyPresentInStore(toolTypeId, storeId) {
      let params = {
        filters: JSON.stringify({
          toolType: {
            operatorId: 36,
            value: [toolTypeId + ''],
          },
          storeRoom: {
            operatorId: 36,
            value: [storeId + ''],
          },
        }),
      }
      let { list, error } = await API.fetchAll('tool', params)
      if (error) {
        this.$message.error(
          error || 'Error ocurred while checking the availability'
        )
        return false
      } else {
        if (list.length > 0) {
          this.toolId = list[0].id
        }
        return list.length > 0
      }
    },
    removeToolFormDataEntry(list, index) {
      list.splice(index, 1)
    },
    addToolFormDataEntry(field) {
      field.push({ toolType: { id: null }, quantity: null, rate: null })
    },
    fillFormData() {
      if (this.tools.length === 0) {
        let t = {
          toolType: { id: null },
          quantity: null,
          rate: null,
        }
        this.formData.push(t)
      }
      for (let i in this.tools) {
        if (i === '10') {
          break
        } else {
          let temp = {
            toolType: { id: this.tools[i].toolType.id },
            quantity: null,
            rate: null,
          }
          this.formData.push(temp)
        }
      }
      this.loading = false
    },
    setCurrencyCodeInSubform(currencyCode, exchangeRate, index) {
      let tool = this.formData[index] || {}
      tool = {
        ...(tool || {}),
        currencyCode,
        exchangeRate,
      }
      this.$set(this.formData, `${index}`, tool)
    },
    getrecordCurrency(currencyCode) {
      return getCurrencyForCurrencyCode(currencyCode)
    },
    calculateExchangeRate(rateObj, index) {
      let tool = this.formData[index] || {}
      tool.rate = getCalculatedCurrencyValue(rateObj, tool.rate)
      this.formData[index] = tool
    },
  },
}
</script>
<style scoped>
.item-unit-price-currency {
  padding: 20px 0px 0px;
  width: 20%;
}
</style>
