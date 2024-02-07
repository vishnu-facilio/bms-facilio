<template>
  <div>
    <el-col
      v-if="$helpers.taxMode() === 2"
      :span="24"
      class="position-relative"
    >
      <el-col
        :span="16"
        class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border"
      >
        <div class="fc-black-12 text-right text-uppercase fw6">
          TAX
        </div>
      </el-col>
      <el-col
        :span="8"
        class="fc-form-tax-table-right-border fc-border-form-table-cal-td pT10"
      >
        <div class="fc-black-12 text-right bold">
          <el-select
            filterable
            clearable
            v-model="model.tax.id"
            class="width100 fc-input-full-border2 fc-form-select-grey-txt"
          >
            <el-option-group key="1" label="Tax Groups">
              <el-option
                v-for="(tax, index) in groupedTaxes"
                :key="index"
                :label="
                  tax.isActive
                    ? `${tax.name} (${tax.rate} %)`
                    : `${tax.name}* (${tax.rate} %)`
                "
                :value="tax.id"
              ></el-option>
            </el-option-group>
            <el-option-group
              key="2"
              :label="groupedTaxes.length > 0 ? 'Individual Taxes' : ''"
            >
              <el-option
                v-for="(tax, index) in individualTaxes"
                :key="index"
                :label="
                  tax.isActive
                    ? `${tax.name} (${tax.rate} %)`
                    : `${tax.name}* (${tax.rate} %)`
                "
                :value="tax.id"
              ></el-option>
            </el-option-group>
          </el-select>
        </div>
      </el-col>
    </el-col>

    <el-col :span="24" class="position-relative">
      <div v-for="(key, index) in Object.keys(taxSplitUp)" :key="index">
        <el-col
          :span="16"
          class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border"
        >
          <div class="fc-black-12 text-right fw6">
            {{
              `${$getProperty(taxSplitUp[key], 'tax.name')} (${$getProperty(
                taxSplitUp[key],
                'tax.rate'
              )} %)`
            }}
          </div>
        </el-col>
        <el-col
          :span="8"
          class="fc-form-tax-table-right-border fc-border-form-table-cal-td"
        >
          <div class="fc-black-12 text-right bold text-uppercase">
            {{ $d3.format(',.2f')($getProperty(taxSplitUp[key], 'taxAmount')) }}
          </div>
        </el-col>
      </div>
    </el-col>
    <el-col v-if="showTotalTax" :span="24" class="position-relative">
      <el-col
        :span="16"
        class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border"
      >
        <div class="fc-black-12 text-right fw6">
          TOTAL TAX
        </div>
      </el-col>
      <el-col
        :span="8"
        class="fc-form-tax-table-right-border fc-border-form-table-cal-td"
      >
        <div class="fc-black-12 text-right bold">
          {{ $d3.format(',.2f')(totalTax) }}
        </div>
      </el-col>
    </el-col>
  </div>
</template>
<script>
export default {
  props: [
    'model',
    'individualTaxes',
    'groupedTaxes',
    'taxSplitUp',
    'showTotalTax',
    'totalTax',
    '$helpers.taxMode()',
  ],
}
</script>
