<template>
  <div class="x-field-select width40 d-flex">
    <el-select
      v-model="selectModel"
      placeholder="Select"
      filterable
      class="fc-input-full-border-h35   mB20 width100"
      popper-class="fc-group-select"
    >
      <el-option-group
        v-for="(dimension, dimensionIndex) in dimensions"
        :key="dimensionIndex"
        :label="dimension.displayName"
      >
        <el-option
          v-for="(field, fieldIndex) in dimension.subFields"
          :key="fieldIndex"
          :label="field.displayName"
          :value="field.fieldId_xAggr"
        >
        </el-option>
      </el-option-group>
    </el-select>
    <img
      src="~assets/add-icon.svg"
      class="add-remove-icons"
      @click="$emit('add')"
    />
    <img
      src="~assets/remove-icon.svg"
      @click="$emit('remove')"
      class="add-remove-icons"
    />
  </div>
</template>

<script>
export default {
  props: ['value', 'dimensions'],
  data() {
    return {}
  },
  computed: {
    //use xfield ID as v-model unique key
    selectModel: {
      get() {
        console.log('computed get', JSON.stringify(this.value))
        return this.value.fieldId_xAggr
      },
      set(fieldId_xAggr) {
        {
          console.log('computed set', fieldId_xAggr)

          let fieldObj = null
          this.dimensions.forEach(dimension => {
            dimension.subFields.forEach(subField => {
              if (subField.fieldId_xAggr == fieldId_xAggr) {
                fieldObj = subField
              }
            })
          })
          let [field_id, xAggr] = fieldId_xAggr.split('_')
          if (!isNaN(field_id)) {
            //field_id can also be string , formula fields , fieldName is stored as field_id
            field_id = parseInt(field_id)
          }
          xAggr = parseInt(xAggr)
          let drillStepMeta = {
            fieldId_xAggr,
            xAggr,

            xField: {
              field_id,
              module_id: fieldObj.moduleId,
            },
          }
          this.$emit('input', drillStepMeta)
        }
      },
    },
  },
}
</script>

<style lang="scss">
.x-field-select {
  .add-remove-icons {
    visibility: hidden;
    cursor: pointer;
    height: 25px;
    width: 30px;
    margin-top: 5px;
    margin-left: 10px;
  }
  &.is-last:hover {
    .add-remove-icons {
      visibility: visible;
    }
  }
}
</style>
