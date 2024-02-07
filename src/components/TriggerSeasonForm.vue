<template>
  <div>
    <el-row>
      <el-form ref="trigger-season-form" :model="formModel" :rules="rules">
        <el-col :span="2">
          <div class="season-list-number">{{ item }}</div>
        </el-col>
        <el-col :span="21">
          <el-row>
            <el-col :span="24">
              <el-form-item prop="name" label="Season Name">
                <el-input
                  @change="formChanged(false)"
                  class="fc-input-full-border2"
                  v-model="formModel.name"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row class="flex-middle justify-content-space">
            <el-col :span="12" class="flex-middle justify-content-space">
              <el-col :span="12">
                <el-form-item prop="startMonth" label="Start Month">
                  <el-select
                    v-model="formModel.startMonth"
                    @change="resetStartDateField"
                    collapse-tags
                    class="fc-input-full-border2 width95 fc-tag"
                  >
                    <el-option
                      v-for="f in months"
                      :label="f.label"
                      :value="f.value"
                      :key="f.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="startDate" label="Start Date">
                  <el-select
                    @change="formChanged(true)"
                    v-model="startDate"
                    collapse-tags
                    :disabled="$validation.isEmpty(formModel.startMonth)"
                    class="fc-input-full-border2 width95 fc-tag"
                  >
                    <el-option
                      v-for="f in getMaxDatesOfMonth(formModel.startMonth)"
                      :label="f"
                      :value="f"
                      :key="f + 'startdate'"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-col>
            <el-col :span="12">
              <el-col :span="12" class="end-config">
                <el-form-item prop="endMonth" label="End Month">
                  <el-select
                    @change="resetEndDateField"
                    v-model="formModel.endMonth"
                    collapse-tags
                    class="mL10 fc-input-full-border2 width95 fc-tag"
                  >
                    <el-option
                      v-for="f in months"
                      :label="f.label"
                      :value="f.value"
                      :key="f.value"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12" class="end-config">
                <el-form-item prop="endDate" label="End Date">
                  <el-select
                    v-model="endDate"
                    @change="formChanged(true)"
                    collapse-tags
                    :disabled="$validation.isEmpty(formModel.endMonth)"
                    class="mL10 fc-input-full-border2 width95 fc-tag"
                  >
                    <el-option
                      v-for="f in getMaxDatesOfMonth(formModel.endMonth)"
                      :label="f"
                      :value="f"
                      :key="f + 'endDate'"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-col>
          </el-row>
        </el-col>
        <el-col :span="1">
          <div
            v-if="canShowRemove"
            @click="removeLineItem()"
            class="season-list-minus"
          >
            <fc-icon group="action" name="circle-minus"></fc-icon>
          </div>
        </el-col>
      </el-form>
    </el-row>
  </div>
</template>
<script>
import Constants from 'util/constant'

export default {
  props: ['item', 'canShowRemove', 'formObj'],
  data() {
    return {
      formModel: {},
      months: Constants.MONTHS,
      startDate: null,
      endDate: null,
      rules: {
        name: [
          {
            required: true,
            message: this.$t('common.trigger.enter_season_name'),
            maxLength : 50,
            trigger: 'blur',
          },
        ],
        startMonth: [
          {
            required: true,
            message: this.$t('common.trigger.start_month_required'),
            trigger: 'change',
          },
        ],
        startDate: [
          {
            required: true,
            message: this.$t('common.trigger.start_date_required'),
            trigger: 'change',
          },
        ],
        endMonth: [
          {
            required: true,
            message: this.$t('common.trigger.end_month_required'),
            trigger: 'change',
          },
        ],
        endDate: [
          {
            required: true,
            message: this.$t('common.trigger.end_date_required'),
            trigger: 'change',
          },
        ],
      },
    }
  },
  created() {
    let { formObj } = this
    let { startDate, endDate } = formObj || {}
    this.formModel = formObj
    this.startDate = startDate
    this.endDate = endDate
  },
  mounted() {
    this.formChanged(true)
  },
  methods: {
    resetStartDateField() {
      this.startDate = 1
      this.formChanged(true)
    },
    resetEndDateField() {
      this.endDate = 1
      this.formChanged(true)
    },
    resetEndMonthField() {
      this.formChanged(true)
    },
    removeLineItem() {
      let { item } = this
      this.$emit('removeLineItem', item)
    },
    formChanged(fetchNextExecutionTimes) {
      let { formModel, item } = this
      formModel.startDate = this.startDate
      formModel.endDate = this.endDate
      this.$emit('onModelChange', { formModel, item, fetchNextExecutionTimes })
    },
    getMaxDatesOfMonth(val) {
      if (
        val === 1 ||
        val === 3 ||
        val === 5 ||
        val === 7 ||
        val === 8 ||
        val === 10 ||
        val === 12
      ) {
        return 31
      }
      if (val === 2) {
        return 29
      }
      return 30
    },
  },
}
</script>
<style lang="scss">
.end-config {
  .el-form-item__label {
    margin-left: 10px;
  }
  .el-form-item__error {
    margin-left: 10px;
  }
}
</style>
<style scoped lang="scss">
.enable-season-label {
  color: #324056;
  font-weight: 500;
  width: 100px;
  display: flex;
  align-items: center;
}
.season-list-number {
  display: flex;
  width: 25px;
  height: 25px;
  border-radius: 50px;
  align-items: center;
  justify-content: center;
  border: 1px solid #dfe5eb;
  margin-top: 50px;
}
.selector-column {
  width: 25px;
}
.width95 {
  width: 95%;
}
.season-list-minus {
  margin-top: 50px;
  margin-left: 10px;
  width: 30px;
  height: 30px;
  border-radius: 50px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.season-list-minus:hover {
  background-color: #f1f2f4;
}
</style>
