<template>
  <div class="global-scope-variable-fieldmapping-container">
    <div class="sub-title-desc mT10">
      {{ $t('setup.globalscoping.fieldmappingdetails') }}
    </div>
    <spinner v-if="loading" :show="loading" size="80"></spinner>
    <template v-else>
      <div
        v-if="$validation.isEmpty(moduleFieldsMap)"
        class="mT40 mB40 text-center p30imp"
      >
        <InlineSvg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxlg emptystate-icon-size"
        ></InlineSvg>
        <div class="fc-black-dark f15 bold mT10">
          {{ $t('setup.globalscoping.nofieldsfound') }}
        </div>
      </div>
      <el-form
        v-else
        ref="fieldmapping-form"
        class="mT20"
        label-position="left"
        label-width="200px"
        :model="model"
      >
        <el-form-item
          v-for="(moduleObj, index) in moduleFieldsMap"
          :key="'fieldmap-' + index"
          prop="switchModule"
          :label="moduleObj.displayName"
          :title="moduleObj.displayName"
          class="mB10 textoverflow-ellipsis"
          v-tippy="{ placement: 'top-start' }"
        >
          <el-select
            v-model="model.moduleField[moduleObj.name]"
            :placeholder="$t('setup.globalscoping.selectfield')"
            class="fc-input-full-border-select2 width100"
            clearable
            filterable
          >
            <el-option
              v-for="(fieldObj, index) in moduleObj.fieldsList"
              :key="'field-' + moduleObj.moduleId + '-' + index"
              :label="fieldObj.displayName"
              :value="fieldObj.name"
            >
              <span class="fL">{{ fieldObj.displayName }}</span>
              <span class="field-type">{{
                fieldObj.dataType === multilookupDatatype
                  ? $t('setup.globalscoping.multilookup')
                  : $t('common.markdown.null')
              }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </template>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['moduleFieldsMap', 'loading', 'scopeVariableDetail', 'model'],
  computed: {
    multilookupDatatype() {
      return 13
    },
  },
  methods: {
    serializeData() {
      let scopeVariableModulesFieldsList = []
      let { model, moduleFieldsMap } = this
      if (model && !isEmpty(model.moduleField)) {
        moduleFieldsMap.map(module => {
          if (module && !isEmpty(model.moduleField[module.name])) {
            scopeVariableModulesFieldsList.push({
              moduleId: module.moduleId,
              fieldName: model.moduleField[module.name],
            })
          }
        })
      }
      return scopeVariableModulesFieldsList
    },
  },
}
</script>
<style scoped lang="scss">
.global-scope-variable-fieldmapping-container {
  .sub-title-desc {
    font-size: 12px;
    letter-spacing: 0.6px;
    color: #999999;
  }
}
</style>

<style lang="scss">
.global-scope-variable-fieldmapping-container {
  .el-form-item__label {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
.field-type {
  float: right;
  color: #8492a6;
  font-size: 13px;
}
</style>
