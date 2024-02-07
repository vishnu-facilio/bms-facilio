<template>
  <el-dialog
    :visible="true"
    :append-to-body="true"
    :title="$t('common.products.add_kpi_category')"
    class="fc-dialog-center-container kpi-details"
    custom-class="setup-dialog35"
    :before-close="cancel"
    style="z-index: 999999"
  >
    <el-row>
      <el-col>
        <div class="fc-input-label-txt mb5">
          {{ $t('common._common.category_name') }}
        </div>
        <div>
          <el-input
            v-model="kpiCategory.name"
            class="fc-input-full-border2 pB100"
          ></el-input>
        </div>
      </el-col>
    </el-row>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="cancel()">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button class="modal-btn-save" type="primary" @click="save()">{{
        kpiCategory.id ? $t('common._common.update') : $t('common._common.add')
      }}</el-button>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['category'],
  data() {
    return {
      loading: false,
      kpiCategory: { name: '' },
    }
  },
  created() {
    this.kpiCategory = { ...this.kpiCategory, ...this.category }
  },
  methods: {
    save() {
      this.loading = true

      let params = {
        kpiCategory: {
          name: this.kpiCategory.name,
        },
      }

      this.$http
        .post('v2/kpi/category/add', params)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.$emit(
              'onCategoryCreate',
              response.data.result.kpiCategoryContext.id
            )
          }
        })
        .catch((error = {}) => {
          this.loading = false

          this.$message.error(
            error.message ||
              this.$t('common._common.error_while_creatingkpi_category')
          )
        })
    },
    cancel() {
      this.$emit('onClose')
    },
  },
}
</script>
