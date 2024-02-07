<template>
  <div>
    <div class="mT20">
      <el-row :gutter="20">
        <el-col :span="12" class="">
          <div class="fc-input-label-txt pB0">Module</div>
          <el-select
            v-model="trigger.customTrigger.customModuleId"
            class="fc-input-full-border-select2 width100 pT10"
          >
            <el-option
              v-for="(mod, index) in modules"
              :key="index"
              :label="mod.displayName"
              :value="mod.moduleId"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="12" class="">
          <div class="fc-input-label-txt pB0">Field</div>
          <el-select
            v-model="trigger.customTrigger.fieldId"
            class="fc-input-full-border-select2 width100 pT10"
          >
            <el-option
              v-for="(field, index) in moduleFields[
                trigger.customTrigger.customModuleId
              ]"
              :key="index"
              :label="field.displayName"
              :value="field.fieldId"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
    </div>
    <div class="mT20">
      <div class="fc-input-label-txt pB0">
        {{ $t('setup.setupLabel.scheduled_type') }}
      </div>
      <el-radio-group v-model="trigger.customTrigger.executeOn" class="pT10">
        <el-radio class="fc-radio-btn" :label="1">On</el-radio>
        <el-radio class="fc-radio-btn" :label="2">After</el-radio>
        <el-radio class="fc-radio-btn" :label="3">Before</el-radio>
      </el-radio-group>
    </div>
    <template v-if="trigger.customTrigger.executeOn > 1">
      <div class="pT20">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="c-input-label-txt pB0">Days</div>
            <el-select
              v-model="trigger.customTrigger.days"
              clearable
              placeholder="Select"
              class="fc-input-full-border-select2 width100 pT10"
            >
              <el-option
                v-for="index in 10"
                :label="index"
                :key="index + 1"
                :value="index"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="12">
            <div class="c-input-label-txt pB0">Hours</div>
            <el-select
              v-model="trigger.customTrigger.hours"
              clearable
              class="fc-input-full-border-select2 width100 pT10"
            >
              <el-option
                v-for="(opt, key) in timeOptions"
                :label="opt.label"
                :value="opt.value"
                :key="key"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
      </div>
    </template>
  </div>
</template>
<script>
export default {
  props: ['trigger'],
  data() {
    return {
      modules: [],
      moduleFields: {},
      timeOptions: [],
    }
  },
  created() {
    this.$http.get(`/v2/pm/getModulesForTrigger`).then(resp => {
      if (resp.data && resp.data.result) {
        this.modules = resp.data.result.modules
        this.modules.forEach(i => (this.moduleFields[i.moduleId] = i.fields))
      }
    })
    for (let i = 0; i <= 23; i += 0.5) {
      this.timeOptions.push({ label: i, value: i * 60 * 60 })
    }
  },
}
</script>
