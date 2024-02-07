<template>
  <el-dialog
    :visible="true"
    :title="title"
    width="40%"
    :show-close="false"
    :append-to-body="true"
    style="padding:0px;margin:0px;"
    class="delete-dialog"
  >
    <div>
      <template>
        <span>{{ $t(`common.failure_class.warning`) }}</span>
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

        <div slot="footer" class="f-dialog-footer">
          <el-button
            type="primary"
            class="form-btn f13 bold secondary text-center text-uppercase cancel-btn-focus btn--danger"
            @click="closeDeleteDialog()"
          >
            {{ $t('common._common.close') }}
          </el-button>
        </div>
      </template>
    </div>
  </el-dialog>
</template>
<script>
export default {
  props: ['moduleName', 'errorMap', 'id'],
  computed: {
    title() {
      return this.$t('common.header.warning')
    },
  },
  methods: {
    closeDeleteDialog() {
      this.$emit('onClose')
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
