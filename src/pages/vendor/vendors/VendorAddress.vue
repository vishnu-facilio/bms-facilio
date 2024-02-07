<template>
  <div v-if="details.address">
    <div class="mT20 mB10 mL25 inventory-section-header">
      {{ $t('common._common.address') }}
    </div>
    <div class="fc__white__bg__info">
      <el-row class="border-bottom6 pB20">
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">{{ $t('common.products.street') }}</div>
          </el-col>
          <el-col :span="12" class="field-value">
            {{
              details.address && details.address.street
                ? details.address.street
                : ''
            }}
          </el-col>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">{{ $t('common.products.city') }}</div>
          </el-col>
          <el-col :span="12" class="field-value">
            {{
              details.address && details.address.city
                ? details.address.city
                : ''
            }}
          </el-col>
        </el-col>
      </el-row>
      <el-row class="border-bottom6 pB20 pT20">
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">
              {{ $t('common.products.state_province') }}
            </div>
          </el-col>
          <el-col :span="12" class="field-value">
            {{
              details.address && details.address.state
                ? details.address.state
                : ''
            }}
          </el-col>
        </el-col>
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">
              {{ $t('common.products.zip_postal_code') }}
            </div>
          </el-col>
          <el-col :span="12" class="field-value">
            {{
              details.address && details.address.zip ? details.address.zip : ''
            }}
          </el-col>
        </el-col>
      </el-row>
      <el-row class="border-bottom6 pB20 pT20">
        <el-col :span="12">
          <el-col :span="12">
            <div class="field-label">{{ $t('common.products.country') }}</div>
          </el-col>
          <el-col :span="12" class="field-value">
            {{
              details.address && details.address.country
                ? details.address.country
                : ''
            }}
          </el-col>
        </el-col>
        <el-col :span="12"> </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
export default {
  props: ['details'],
}
</script>
<style scoped>
.field-label {
  color: #324056;
  font-weight: 500;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.field-value {
  padding-left: 10px;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
</style>
