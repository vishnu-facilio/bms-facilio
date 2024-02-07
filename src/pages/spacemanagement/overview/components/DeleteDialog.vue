<template>
  <el-dialog
    :visible="true"
    :show-close="false"
    :title="title"
    width="40%"
    :append-to-body="true"
    style="padding:0px;margin:0px;"
    class="delete-dialog"
    :before-close="closeDeleteDialog"
  >
    <div>
      <template>
        <span v-if="type == 3">{{
          $t(`common._common.space_delete_error`, {
            moduleName: moduleName,
          })
        }}</span>
        <span v-else>{{
          $t(`common._common.space_delete_warning`, {
            moduleName: moduleName,
          })
        }}</span>
        <table class="fc-border4 width100 mT20">
          <tr>
            <th>{{ $t(`common._common.dependency`) }}</th>
            <th>{{ $t(`common._common.count`) }}</th>
          </tr>
          <template v-if="errorMap">
            <tr v-for="(value, key) in errorMap" :key="key">
              <td>{{ key }}</td>
              <td>{{ value }}</td>
            </tr>
          </template>
        </table>

        <div v-if="type == 3" slot="footer" class="f-dialog-footer">
          <el-button
            type="primary"
            class="form-btn f13 bold secondary text-center text-uppercase cancel-btn-focus"
            @click="closeDeleteDialog()"
          >
            {{ $t('common._common.close') }}
          </el-button>
        </div>
        <div v-else class="f-dialog-footer">
          <el-button
            type="primary"
            class="modal-btn-cancel mR0"
            @click="closeDeleteDialog()"
          >
            {{ $t('common._common.cancel') }}
          </el-button>
          <el-button
            type="primary"
            class="mL0 btn--danger"
            @click="deleteRecord()"
          >
            {{ $t('common._common.delete') }}
          </el-button>
        </div>
      </template>
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'

export default {
  props: ['moduleName', 'errorMap', 'type', 'id'],
  computed: {
    title() {
      return this.type === 3
        ? this.$t('common._common.error')
        : this.$t('common.header.warning')
    },
  },
  methods: {
    closeDeleteDialog() {
      this.$emit('onClose')
    },
    async deleteRecord() {
      let { id } = this
      let { error } = await API.deleteRecord(this.moduleName, [id])

      if (!error) {
        this.$message.success(this.$t('space.sites.delete_success'))
        this.$emit('onDelete')
        this.$emit('refresh')
        this.closeDeleteDialog()
      } else {
        this.$message.error(error)
      }
    },
  },
}
</script>
<style scoped>
th,
td {
  padding: 10px;
}
.f-dialog-footer {
  position: absolute;
  margin: 0 -20px 0 -20px;
  margin-top: 20px;
  width: 100%;
  display: flex;
  align-items: center;
  flex-direction: row;
  text-align: right;
  user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  -webkit-user-select: none;
}
.delete-dialog {
  padding: -100px;
  margin: 100px;
}
.btn--danger {
  width: 50%;
  padding-top: 18px;
  padding-bottom: 18px;
  cursor: pointer;
  background-color: #e47676;
  border: transparent;
  color: #fff;
  font-size: 13px;
  text-transform: uppercase;
  font-weight: 500;
  float: left;
  border-radius: 0;
  line-height: 16px;
  cursor: pointer;
}
.btn--danger:hover {
  background-color: #e47676;
  border-color: #e47676;
}
</style>
