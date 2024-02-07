<template>
  <div>
    <el-form>
      <el-row>
        <div>
          <el-col :span="24" class="el-select-block">
            <el-select
              v-model="fileFields"
              filterable
              clearable
              multiple
              placeholder="Select File Fields"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(fileField, index) in fileFieldsList"
                :key="index"
                :label="fileField.displayName || fileField.name"
                :value="fileField.id"
              ></el-option>
            </el-select>
          </el-col>
        </div>
      </el-row>
    </el-form>
  </div>
</template>

<script>
export default {
  props: ['fileFieldIds', 'fileFieldsList'],
  computed: {
    fileFields: {
      get() {
        return this.fileFieldIds
      },
      set(value) {
        this.$emit('update:fileFieldIds', value)
      },
    },
  },
}
</script>
