<template>
  <div>
    <el-form>
      <el-row>
        <div class="fc-fileField-url-select">
          <el-col :span="24">
            <el-select
              v-model="fileFields"
              filterable
              clearable
              multiple
              placeholder="Select file fields"
              class="fc-input-full-border-select2-template width100"
              @change="handleValuechange()"
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
  methods: {
    handleValuechange() {
      this.$emit('change', this.fileFields)
    },
  },
}
</script>
<style lang="scss">
.fc-fileField-url-select {
  .el-select__tags-text {
    color: #324056;
    font-weight: 500;
  }
  .el-tag {
    border-radius: 4px !important;
    background-color: #f9f9ff !important;
    border: solid 1px #dfdff3 !important;
  }
  .el-tag__close {
    background-color: transparent !important;
    font-size: 14px;
    color: #324056;
    font-weight: 500;
  }
  .el-select__tags {
    flex-wrap: wrap;
  }
}

.fc-input-full-border-select2-template .el-input__inner {
  min-height: 40px;
  padding-left: 15px !important;
  padding-right: 15px !important;
  border-radius: 3px !important;
  background-color: #ffffff;
  border: solid 1px #d0d9e2 !important;
  font-size: 14px;
  font-weight: normal;
  letter-spacing: 0.4px;
  color: #324056;
  text-overflow: ellipsis;
  font-weight: 400;
  padding-right: 30px;
  white-space: nowrap;
}
</style>
