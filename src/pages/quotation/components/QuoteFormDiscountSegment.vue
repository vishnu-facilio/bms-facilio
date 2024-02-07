<template>
  <el-col v-if="discountVisibility" :span="24" class="position-relative">
    <el-col
      :span="16"
      class="fc-form-tax-table-right-border fc-border-form-table-cal-td fc-form-tax-table-left-border position-relative flex-middle"
    >
      <div class="fc-black-12 text-left fw6 pT5">
        DISCOUNT
      </div>
      <el-form-item prop="discountAmount" class="mB0 hide-error">
        <el-input
          v-if="Number(discountType) === 1"
          placeholder="Discount"
          type="number"
          v-model="model.discountAmount"
          class="fc-input-full-border2 width100 mL5 pL5 pR5 width135px"
        ></el-input>
      </el-form-item>
      <el-form-item prop="discountPercentage" class="mB0 hide-error">
        <el-input
          placeholder="Discount"
          v-if="Number(discountType) === 2"
          type="number"
          v-model="model.discountPercentage"
          class="fc-input-full-border2 width100 mL5 pL5 pR5 width135px"
        >
        </el-input>
      </el-form-item>
      <div class="fc-form-tax-discount-box d-flex">
        <div class="flex-middle">
          <el-select
            @change="resetDiscount"
            v-model="discountType"
            placeholder=""
            class="mL10"
          >
            <el-option key="1" :label="currency" :value="1"> </el-option>
            <el-option key="2" label="%" :value="2"> </el-option>
          </el-select>
        </div>
      </div>
    </el-col>
    <el-col
      :span="8"
      class="fc-form-tax-table-right-border fc-border-form-table-cal-td"
    >
      <div class="fc-black-12 text-right bold break-word-all">
        {{ $d3.format(',.2f')(discount) }}
      </div>
    </el-col>
    <div @click="removeDiscount()" class="fc-form-action-total-remove">
      <i class="el-icon-remove-outline fc-row-delete-icon bold f20"></i>
    </div>
  </el-col>
</template>
<script>
export default {
  props: ['model', 'discountVisibility', 'discount', 'currency'],
  data() {
    return {
      discountType: 1,
    }
  },
  created() {
    if ((this.model || {}).discountPercentage) {
      this.discountType = 2
    }
  },
  methods: {
    resetDiscount(val) {
      if (Number(val) === 1) {
        this.model.discountPercentage = null
      } else if (Number(val) === 2) {
        this.model.discountAmount = null
      }
    },
    removeDiscount() {
      this.$emit('update:discountVisibility', false)
      this.model.discountAmount = null
      this.model.discountPercentage = null
    },
  },
}
</script>
