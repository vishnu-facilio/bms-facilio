<template>
  <div>
    <div class="width100 pB50 position-relative">
      <table class="setting-list-view-table store-table">
        <thead class="setup-dialog-thead">
          <tr>
            <th class="setting-table-th setting-th-text" style="width: 240px;">
              {{ $t('common._common.type') }}
            </th>
            <th class="setting-table-th setting-th-text" style="width: 240px;">
              {{ $t('common.products.name') }}
            </th>
            <th class="setting-table-th setting-th-text" style="width: 240px;">
              {{ $t('common._common.quantity') }}
            </th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(lineItem, index) in lineItems" :key="index">
            <td>
              <el-select
                v-model="lineItem.inventoryType"
                class="fc-input-full-border-select2 width100"
                @change="lineItemChangeAction()"
              >
                <el-option
                  v-for="item in lineItemTypes"
                  :key="item.type"
                  :label="item.label"
                  :value="item.type"
                >
                  <div class="flex-middle">
                    <div class="pR40">
                      {{ item.label }}
                    </div>
                  </div>
                </el-option>
              </el-select>
            </td>
            <td>
              <el-form
                :ref="`lineItem-description-${index}`"
                :rules="descriptionValidationRules"
                :model="lineItem"
                class="table-row"
              >
                <el-form-item
                  :prop="lineItemTypesMap[lineItem.inventoryType].model"
                  :required="true"
                  class="hide-error pT20"
                >
                  <FLookupFieldWrapper
                    v-if="showLookupField"
                    v-model="
                      lineItem[
                        `${lineItemTypesMap[lineItem.inventoryType].model}`
                      ].id
                    "
                    :field="currentFieldObject(lineItem)"
                    class="width100"
                    :filterConstruction="constructFilter"
                  ></FLookupFieldWrapper>
                </el-form-item>
              </el-form>
            </td>
            <td>
              <el-input
                :placeholder="$t('common._common.quantity')"
                :min="0"
                type="number"
                v-model="lineItem.quantity"
                class="fc-input-full-border-select2 duration-input"
              ></el-input>
            </td>
            <td>
              <div class="flex-middle">
                <i
                  @click="addLineItems()"
                  v-if="lineItems.length - 1 === index"
                  class="el-icon-circle-plus-outline fc-row-add-icon f20 bold pL5"
                ></i>
                <i
                  @click="removeLineItems(index)"
                  v-if="lineItems.length > 1"
                  class="el-icon-remove-outline fc-row-delete-icon f20 bold pL10"
                ></i>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import { deepCloneObject } from 'util/utility-methods'
import Constants from 'util/constant'
import LineItemsMixin from '@/mixins/forms/LineItemsMixin'
export default {
  props: ['model', 'field', 'lineItems', 'isEdit', 'module'],
  mixins: [LineItemsMixin],
  components: {
    FLookupFieldWrapper,
  },
  data() {
    return {
      showLookupField: true,
      fields: {
        itemTypes: {
          multiple: false,
          lookupModule: { name: 'itemTypes', displayName: 'Item Type' },
        },
        toolTypes: {
          lookupModule: { name: 'toolTypes', displayName: 'Tool Type' },
          multiple: false,
        },
      },
      lineItemTypesMap: {
        1: {
          list: 'itemTypes',
          model: 'itemType',
        },
        2: {
          list: 'toolTypes',
          model: 'toolType',
        },
      },
      lineItemTypes: [
        {
          value: 'items',
          label: this.$t('common.products.items'),
          icon: 'svgs/items',
          iconClass: 'icon icon-sm-md vertical-sub mR10 stroke-grey',
          type: 1,
        },
        {
          value: 'tools',
          label: this.$t('common.header.tools'),
          icon: 'svgs/setting',
          iconClass: 'icon icon-md vertical-sub mR10 fill-grey',
          type: 2,
        },
      ],
    }
  },
  created() {
    this.init()
  },
  methods: {
    currentFieldObject(lineItem) {
      let { inventoryType } = lineItem || {}
      if (inventoryType !== null) {
        return this.fields[`${this.lineItemTypesMap[inventoryType].list}`]
      }
    },
    constructFilter() {
      if (this.module === 'transferrequest') {
        return {
          isRotating: { operatorId: 15, value: ['false'] },
        }
      }
      return {}
    },
    init() {
      this.loading = true
      Promise.all([this.loadLineItemsModuleMeta()]).then(() => {
        this.loading = false
      })
    },
    addLineItems() {
      let { lineItems } = this
      lineItems.push(deepCloneObject(Constants.INV_LINE_ITEM_DEFAULTS))
    },
    lineItemChangeAction() {
      this.showLookupField = false
      this.$nextTick(() => {
        this.showLookupField = true
      })
    },
    loadLineItemsModuleMeta() {
      return this.$store.dispatch(
        'view/loadModuleMeta',
        'transferrequestlineitems'
      )
    },
  },
}
</script>
<style lang="scss" scoped>
.setting-list-view-table td {
  padding: 0px 20px;
}
.setting-list-view-table .setting-table-th {
  padding: 23px 22px;
}
table {
  border-spacing: 0;
}
</style>
