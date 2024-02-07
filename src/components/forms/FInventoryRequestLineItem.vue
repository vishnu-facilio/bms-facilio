<template>
  <div>
    <div class="width100 pB50 position-relative">
      <table class="setting-list-view-table store-table">
        <thead class="setup-dialog-thead">
          <tr>
            <th class="setting-table-th setting-th-text" style="width: 240px;">
              {{ $t('common.inventory.item') }}
            </th>
            <!-- <th class="setting-table-th setting-th-text" style="width: 240px;">
              {{ $t('common.inventory.reservation_type') }}
            </th> -->
            <th class="setting-table-th setting-th-text" style="width: 240px;">
              {{ $t('common.inventory.quantity') }}
            </th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(lineItem, index) in lineItems" :key="index">
            <td>
              <el-form
                :ref="`lineItem-description-${index}`"
                :rules="descriptionValidationRules"
                :model="lineItem"
                class="table-row"
              >
                <el-form-item :required="true" class="pT20">
                  <FLookupFieldWrapper
                    v-if="showLookupField"
                    v-model="lineItem['itemType'].id"
                    :field="itemTypesField"
                    class="width100"
                  ></FLookupFieldWrapper>
                </el-form-item>
              </el-form>
            </td>
            <td>
              <el-form
                :ref="`lineItem-description-${index}`"
                :rules="quantityValidationRules"
                :model="lineItem"
                class="table-row"
              >
                <el-form-item :required="true" prop="quantity" class="pT20">
                  <el-input
                    :placeholder="$t('common._common.quantity')"
                    min="1"
                    type="number"
                    v-model="lineItem.quantity"
                    class="fc-input-full-border-select2 duration-input"
                  ></el-input>
                </el-form-item>
              </el-form>
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
      itemTypesField: {
        multiple: false,
        lookupModule: { name: 'itemTypes', displayName: 'Item Type' },
      },
    }
  },
  methods: {
    addLineItems() {
      let { lineItems } = this
      lineItems.push(deepCloneObject(Constants.INV_LINE_ITEM_DEFAULTS))
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
