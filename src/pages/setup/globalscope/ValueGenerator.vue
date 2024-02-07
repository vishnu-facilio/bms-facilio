<template>
  <div class="global-scope-variable-valgen-container">
    <div class="sub-title-desc mT10">
      {{ $t('setup.globalscoping.specifyvalgen') }}
    </div>
    <spinner v-if="loading" :show="loading" size="80"></spinner>
    <el-form v-else ref="valuegen-form" :rules="rules" :model="valGenSection">
      <el-form-item
        prop="valueGeneratorId"
        :label="$t('setup.globalscoping.variable')"
        class="mB10 width100"
      >
        <el-select
          v-model="valGenSection.valueGeneratorId"
          :placeholder="$t('setup.globalscoping.selectvalgen')"
          class="fc-input-full-border-select2 width100"
          clearable
          filterable
          popper-class="dropdown-selection-popper"
        >
          <el-option
            v-for="(valGen, index) in valueGenerators"
            :key="'valGen-' + valGen.linkname + '-' + index"
            :label="valGen.displayName"
            :value="valGen.id"
          ></el-option>
        </el-select>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'

export default {
  props: ['valueGenerators', 'loading', 'scopeVariableDetail', 'valGenSection'],
  data() {
    return {
      rules: {
        valueGeneratorId: [
          {
            required: true,
            message: 'Please select variable',
            trigger: 'change',
          },
        ],
      },
      formData: {},
    }
  },
  mounted() {
    eventBus.$on('resetValueGen', this.resetValGen)
  },
  methods: {
    resetValGen() {
      this.valGenSection = { valueGeneratorId: null }
    },
    serializeData() {
      this.$refs['valuegen-form'].validate(valid => {
        if (valid) {
          let valueGenObj = {}
          let { valGenSection } = this
          if (isEmpty(valGenSection.valueGeneratorId)) {
            valueGenObj.valueGeneratorId = null
            valueGenObj.type = 2
          } else {
            valueGenObj.valueGeneratorId = valGenSection.valueGeneratorId
            valueGenObj.type = 1
          }
          this.formData = valueGenObj
        } else {
          return null
        }
      })
      return this.formData
    },
  },
}
</script>
<style scoped lang="scss">
.global-scope-variable-valgen-container {
  .sub-title-desc {
    font-size: 12px;
    letter-spacing: 0.6px;
    color: #999999;
  }
}
.dropdown-selection-popper {
  .el-select-dropdown__list {
    padding-bottom: 42px;
    width: 100%;
  }
}
</style>
