<template>
  <div>
    <f-dialog
      v-if="visibility"
      :visible="visibility"
      :width="'30%'"
      maxHeight="350px"
      title="Select Columns"
      @save="onSave"
      @close="close"
      confirmTitle="OK"
    >
      <spinner v-if="loading" :show="loading" size="80"></spinner>
      <el-checkbox-group :max="MAX_COLUMNS" v-else v-model="selectedNames">
        <div v-for="(field, index) in selectableFields" :key="index">
          <el-checkbox class="pT10 pB10" :label="field.name">{{
            field.displayName || field.name
          }}</el-checkbox>
        </div>
      </el-checkbox-group>
    </f-dialog>
  </div>
</template>
<script>
import FDialog from '@/FDialogNew'
export default {
  props: ['moduleName', 'visibility', 'fields'],
  components: { FDialog },
  data() {
    return {
      loading: false,
      metaFields: [],
      selectableFields: [],
      selectedNames: [],
      MAX_COLUMNS: 12,
    }
  },
  mounted() {
    if (this.fields) {
      this.selectableFields = [
        { name: 'id', displayName: 'ID' },
        ...this.fields,
      ]
      this.selectedNames = this.selectableFields.map(field => field.name)
      if (this.selectedNames.length > 7) {
        this.selectedNames.splice(7, this.selectedNames.length)
      }
    }
    this.loadMetaFields()
  },
  methods: {
    loadMetaFields() {
      this.loading = true
      this.$http
        .get(`/module/metafields?moduleName=${this.moduleName}`)
        .then(response => {
          this.metaFields = response.data.meta.fields || []
          for (
            let i = 0, j = this.selectedNames.length;
            i < this.metaFields.length;
            i++
          ) {
            let field = this.metaFields[i]
            if (!this.selectedNames.includes(field.name)) {
              this.selectableFields.push(field)
              if (j < 7) {
                this.selectedNames.push(field.name)
                j++
              }
            }
          }
          this.loading = false
        })
    },
    onSave() {
      this.$emit('save', this.selectedNames)
    },
    close() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
<style scoped></style>
