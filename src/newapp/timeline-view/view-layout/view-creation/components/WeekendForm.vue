<template>
  <el-dialog
    :title="isNew ? `Create Weekend` : `Edit Weekend`"
    :visible="true"
    width="30%"
    class="fc-dialog-center-container"
    :append-to-body="true"
    :before-close="closeDialog"
  >
    <div style="height: 430px;">
      <el-form
        ref="weekendForm"
        label-position="top"
        :rules="rules"
        :model="weekendObj"
        class="weekend-form"
      >
        <el-form-item label="Name" prop="name" :required="true">
          <el-input
            class="width100 fc-input-full-border2"
            autofocus
            v-model="weekendObj.name"
            type="text"
            placeholder="Enter the name"
          />
        </el-form-item>
        <el-form-item label="Days" prop="value" :required="true">
          <el-checkbox-group
            v-model="weekendObj.value"
            class="checkbox-group fc__layout__has__row"
          >
            <el-checkbox
              v-for="(dayName, day) in weekDays"
              :key="day"
              :label="parseInt(day)"
              class="mB10"
            >
              {{ dayName }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('common._common.cancel') }}
        </el-button>

        <el-button
          type="primary"
          :loading="saving"
          class="modal-btn-save"
          @click="save()"
        >
          {{ $t('common._common._save') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>
<script>
import Constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  props: ['weekend'],
  data() {
    return {
      weekendObj: { name: null, value: [] },
      weekDays: Constants.WEEK_DAYS,
      saving: false,
      rules: {
        name: {
          required: true,
          message: this.$t('common._common.please_enter_a_name'),
          trigger: 'change',
        },
        value: {
          required: true,
          message: 'Atleast one day should be selected',
          trigger: 'change',
        },
      },
    }
  },
  created() {
    if (!this.isNew) {
      let { id, name, value } = this.weekend || {}
      this.weekendObj = { id, name, value }
    }
  },
  computed: {
    isNew() {
      return isEmpty(this.weekend)
    },
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    save() {
      this.$refs['weekendForm'].validate(async valid => {
        if (!valid) return false

        let { id, name, value } = this.weekendObj || {}
        let params = {
          weekend: {
            name,
            value: JSON.stringify({ All: value }),
          },
        }
        let url = ''

        if (this.isNew) {
          url = 'v2/weekends/add'
        } else {
          url = 'v2/weekends/update'
          params.weekend.id = id
        }

        let { error, data } = await API.post(url, params)

        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          this.$emit('onSave', data?.weekend)
          this.closeDialog()
        }
        this.saving = false
      })
    },
  },
}
</script>
<style lang="scss">
.weekend-form {
  .el-form-item {
    margin-bottom: 10px;
  }
}
</style>
