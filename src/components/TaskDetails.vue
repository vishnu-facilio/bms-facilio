<template>
  <el-dialog
    title="TASK DETAILS"
    :visible.sync="showTaskDetails"
    :append-to-body="true"
    width="40%"
    class="fc-dialog-center-container"
  >
    <div class="task-details">
      <div class="fc-dark-grey-txt14">
        {{ $t('maintenance._workorder.description') }}
        <el-input
          v-model="task.description"
          class="fc-input-full-border-textarea mT25"
          type="textarea"
          auto-complete="off"
          :rows="1"
          resize="none"
          placeholder="Enter Description"
          :autosize="{ minRows: 5, maxRows: 5 }"
        />
      </div>
      <div class="mT25">
        <el-checkbox
          v-model="task.enableInput"
          @change="task.inputType = !task.enableInput ? 1 : task.inputType"
          >{{ $t('maintenance._workorder.enable_input') }}</el-checkbox
        >
      </div>
      <div v-if="task.enableInput" class="mT25">
        <div class="fc-dark-grey-txt14">
          {{ $t('maintenance._workorder.task_type') }}
        </div>
        <el-radio
          @change="onSelectInput"
          :disabled="!canDisableInputType"
          v-model="task.inputType"
          class="fc-radio-btn pT15"
          color="secondary"
          label="2"
          >{{ $t('maintenance._workorder.reading') }}
          {{
            !canDisableInputType
              ? $t('maintenance._workorder.select_asset_space')
              : ''
          }}</el-radio
        >
        <el-radio
          @change="onSelectInput"
          v-model="task.inputType"
          class="fc-radio-btn pT15"
          color="secondary"
          label="3"
          >{{ $t('maintenance._workorder.text') }}</el-radio
        >
        <el-radio
          @change="onSelectInput"
          v-model="task.inputType"
          class="fc-radio-btn pT15"
          color="secondary"
          label="4"
          >{{ $t('maintenance._workorder.number') }}</el-radio
        >
        <el-radio
          @change="onSelectInput"
          v-model="task.inputType"
          class="fc-radio-btn pT15"
          color="secondary"
          label="5"
          >{{ $t('maintenance._workorder.option') }}</el-radio
        >
      </div>
      <div v-if="task.inputType === '2'" class="mT25">
        <div class="fc-dark-grey-txt14">
          {{ $t('maintenance._workorder.reading_field') }}
        </div>
        <div>
          <el-select
            v-model="task.readingFieldId"
            filterable
            style="width:100%"
            class="fc-input-full-border-select2 mT10"
            placeholder=" "
            :loading="isLoading"
          >
            <el-option
              v-for="option in task.taskReadings"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            ></el-option>
          </el-select>
        </div>
      </div>
      <!-- TODO: Have to open this once server changes are done -->
      <!-- <div v-if="task.inputType === '4'" class="mT25">
        <div class="fc-dark-grey-txt14">{{$t("maintenance.pm_list.validation")}}</div>
        <el-select
          v-model="task.validation"
          style="width:100%"
          class="form-item fc-input-full-border-select2 mT10"
          placeholder
        >
          <el-option
            v-for="{label, value} in numberTypeOptions"
            :key="value"
            :label="label"
            :value="value"
          ></el-option>
        </el-select>
        <div v-if="task.validation === 'safeLimit'" class="mT25">
          <div class="fc-dark-grey-txt14">{{$t("maintenance.pm_list.minimum_value")}}</div>
          <el-input type="number" v-model="task.minSafeLimit" class="fc-input-full-border2 mT10"></el-input>
        </div>
        <div v-if="task.validation === 'safeLimit'" class="mT25">
          <div class="fc-dark-grey-txt14">{{$t("maintenance.pm_list.max_value")}}</div>
          <el-input type="number" v-model="task.maxSafeLimit" class="fc-input-full-border2 mT10"></el-input>
        </div>
      </div>-->
      <div v-if="task.inputType === '5'" class="mT25">
        <div class="mT10 fc-dark-grey-txt14">
          {{ $t('maintenance._workorder.options') }}
        </div>
        <div
          v-for="(option, index) in task.options"
          :key="index"
          class="taskoption mT10 d-flex"
        >
          <el-input
            type="text"
            v-model="option.name"
            placeholder
            class="fc-input-full-border2 width200px task-option-input"
          >
            <template slot="prepend">{{ index + 1 }}</template>
          </el-input>
          <div class="add-remove-icons">
            <img src="~assets/add-icon.svg" @click="addTaskOption()" />
            <img
              v-if="canShowDeleteIcon()"
              src="~assets/remove-icon.svg"
              @click="removeTaskOption(index)"
            />
          </div>
        </div>
        <div>
          <div class="fc-dark-grey-txt13 mT25">Default Value</div>
          <el-select
            placeholder="Select"
            v-model="task.defaultValue"
            class="fc-input-full-border-select2 width100 mT10"
          >
            <el-option
              v-for="task in task.options"
              :key="task.name"
              :label="task.name"
              :value="task.name"
            ></el-option>
          </el-select>
        </div>
      </div>
      <div class="fc-dark-grey-txt14 mT20 mb5">Validation</div>
      <div class="mT20">
        <el-checkbox v-model="task.attachmentRequired">{{
          $t('maintenance._workorder.photo')
        }}</el-checkbox>
        <el-checkbox
          v-if="task.inputType && task.inputType === '5'"
          v-model="task.remarksRequired"
          >{{ $t('maintenance._workorder.remarks_required') }}</el-checkbox
        >
        <div v-if="task.remarksRequired">
          <el-row class="mT20">
            <el-col :span="12">
              <div class="fc-dark-grey-txt14 mb5">Remarks Options</div>
              <el-radio-group v-model="task.remarkOption" class="mT10">
                <el-radio label="all" class="fc-radio-btn">All</el-radio>
                <el-radio label="specific" class="fc-radio-btn"
                  >Specific</el-radio
                >
              </el-radio-group>
              <el-select
                v-if="task.remarkOption === 'specific'"
                v-model="task.remarkOptionValues"
                multiple
                collapse-tags
                placeholder="Select Values"
                class="fc-input-full-border-select2 fc-tag el-select-block width200px mT10"
              >
                <el-option
                  v-for="(option, index) in task.options"
                  :label="option.name"
                  :key="index"
                  :value="option.name"
                ></el-option>
              </el-select>
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="showTaskDetails = false"
        >CANCEL</el-button
      >
      <el-button type="primary" class="modal-btn-save" @click="saveTaskDetails"
        >SAVE</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { deepCloneObject } from 'util/utility-methods'
import Constants from 'util/constant'
import { API } from '@facilio/api'

export default {
  props: {
    canShowTaskDetails: {
      type: Boolean,
      required: true,
    },
    selectedTask: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      numberTypeOptions: Constants.NUMBER_TYPE_OPTIONS,
      task: {},
      isLoading: false,
      previousResourceId: null,
    }
  },
  mounted() {
    let { selectedTask } = this
    this.$set(this, 'task', deepCloneObject(selectedTask))
    this.fetchTaskReadings()
  },
  computed: {
    showTaskDetails: {
      get() {
        return this.canShowTaskDetails
      },
      set(value) {
        return this.$emit('update:canShowTaskDetails', value)
      },
    },
    canDisableInputType() {
      let { task = {} } = this
      let { resource } = task
      if (!isEmpty(resource)) {
        let {
          resource: { name, id },
        } = task
        return !isEmpty(name) || !isEmpty(id)
      }
      return false
    },
  },
  methods: {
    canShowDeleteIcon() {
      let {
        task: { options = [] },
      } = this
      return options.length > 2
    },
    onSelectInput() {
      this.$set(this.task, 'remarksRequired', false)
      this.$set(this.task, 'remarkOptionValues', [])
      this.$set(this.task, 'remarkOption', 'all')
      this.fetchTaskReadings()
    },
    async fetchTaskReadings() {
      let {
        task: { inputType, taskReadings },
      } = this
      let {
        task: { resource },
      } = this
      if (inputType === '2' && isEmpty(taskReadings)) {
        this.isLoading = true
        let options = await this.loadResourceReadingFields(resource)
        this.isLoading = false
        this.$set(this.task, 'taskReadings', options)
      }
    },
    async loadResourceReadingFields(resource) {
      let { id } = resource
      let url = `/v2/reading/latestdata/${id}?readingType=available`
      let { data, error } = await API.get(url)
      let options = []
      if (!isEmpty(resource)) {
        if (error) {
          let { message } = error || {}
          this.$message.error(message || {})
        } else {
          let { readingValues = [] } = data || {}
          if (!isEmpty(readingValues)) {
            readingValues.forEach(reading => {
              let { field } = reading || {}
              let { displayName: label, id: value } = field || {}
              if (!isEmpty(label) && !isEmpty(value)) {
                options.push({
                  label,
                  value,
                })
              }
            })
          }
        }
      }
      return options
    },
    addTaskOption() {
      let {
        task: { options },
      } = this
      options.push({
        name,
      })
    },
    removeTaskOption(index) {
      let {
        task: { options },
      } = this
      options.splice(index, 1)
    },
    validateTask(task) {
      let {
        enableInput,
        inputType,
        validation,
        maxSafeLimit,
        minSafeLimit,
      } = task
      if (enableInput) {
        if (Number(inputType) === 4 && validation === 'safeLimit') {
          if (isEmpty(minSafeLimit) && isEmpty(maxSafeLimit)) {
            this.showErrMsg('Please enter minimum and maximum value')
            return true
          }
        } else if (Number(inputType) === 5) {
          let {
            options,
            options: { length },
          } = task
          let isTaskOptionEmpty = options.some(option => isEmpty(option.name))
          if (length < 2) {
            this.showErrMsg('Please enter atleast two options')
            return true
          } else if (isTaskOptionEmpty) {
            this.showErrMsg('Option cannot be empty')
            return true
          }
        }
      }
    },
    showErrMsg(msg) {
      this.$message.error(msg)
    },
    saveTaskDetails() {
      let { task, validateTask } = this
      let canProceedSave = validateTask(task)
      if (!canProceedSave) {
        this.$emit('updateTaskDetails', task)
      }
    },
  },
}
</script>

<style scoped>
.task-details {
  height: 100%;
  padding-bottom: 100px;
  overflow: scroll;
}
.task-details .el-radio {
  margin: 0px 30px 0px 0px;
}
.task-details .add-remove-icons {
  display: none;
}
.task-details .add-remove-icons img {
  width: 15px;
  height: 15px;
  margin: 10px 0px 0px 10px;
}
.taskoption:hover .add-remove-icons {
  display: flex;
}
</style>
