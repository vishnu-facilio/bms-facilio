<template>
  <el-table
    :data="attributeListData"
    style="width: 100%"
    :header-cell-style="{ background: '#f3f1fc' }"
    class="form-list-table-attributes"
  >
    <el-table-column prop="name" label="Name"> </el-table-column>
    <el-table-column prop="fieldTypeDisplayName" label="Field Type">
    </el-table-column>
    <el-table-column width="100">
      <template v-if="!isExisting" v-slot="attributes">
        <div
          @click="handleClose(attributes.row.id)"
          class="visibility-hide-actions hover-icon-delete"
        >
          <inline-svg
            src="svgs/delete-red-minus"
            iconClass="icon icon-md"
            class="d-flex"
          ></inline-svg>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script>
export default {
  props: ['attributeList', 'isExisting'],
  data() {
    return {
      attributeListData: [],
    }
  },
  watch: {
    attributeList: {
      handler() {
        this.attributeListData = [...(this.attributeList || [])]
      },
      immediate: true,
    },
  },
  methods: {
    handleClose(item) {
      let index = this.attributeListData.findIndex(list => list.id === item)

      this.attributeListData.splice(index, 1)
      this.$emit('update:attributeList', this.attributeListData)
    },
  },
}
</script>
<style lang="scss">
.form-list-table-attributes {
  .el-table__cell {
    padding-left: 32px !important;
  }
}
</style>
