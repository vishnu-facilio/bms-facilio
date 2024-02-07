<template>
  <div class="vm-reading-card" :class="readingContentClass">
    <div class="vm-reading-collapse">
      <div :class="titleClass" class="vm-reading-title">
        {{ $getProperty(activeReading, 'displayName', '') }}
      </div>
      <div class="vm-action-buttons">
        <div v-if="!canShowDelete" class="vm-delete-filler"></div>
        <div v-else class="delete-vm-container" @click="deleteReading">
          <fc-icon
            class="vm-action"
            group="default"
            name="trash-can"
            :size="16"
          ></fc-icon>
        </div>
        <div
          class="vm-button-wrapper"
          @click="toggleReadingContent"
          :class="disabledToggleClass"
        >
          <fc-icon
            class="vm-action"
            group="navigation"
            :name="accordionIconName"
            :size="16"
          ></fc-icon>
        </div>
      </div>
    </div>
    <div class="p20 vm-reading-content" :class="fullContentClass">
      <el-row>
        <el-col class="vm-reading-item" :span="12">
          <div class="vm-reading-title pT10">
            {{ $t('asset.virtual_meters.reading_name') }}
          </div>
          <el-select
            v-model="readingInfo.readingFieldId"
            :placeholder="$t('asset.virtual_meters.select_reading')"
            class="fc-input-full-border-select2 width80"
            :disabled="!isNew && !isNewReading(readingInfo)"
            clearable
            @change="fillReadingUnit"
          >
            <el-option
              v-for="reading in meterReadings"
              :key="reading.id"
              :label="reading.displayName"
              :value="reading.id"
            >
            </el-option>
          </el-select>
        </el-col>
        <el-col class="vm-reading-item left-align" :span="8">
          <div class="vm-reading-title pT10">
            {{ $t('asset.virtual_meters.unit') }}
          </div>
          <el-input
            class="fc-input-full-border2 width70"
            v-model="readingInfo.unit"
            disabled
          ></el-input>
        </el-col>
      </el-row>
      <div class="vm-reading-item mT7">
        <div class="vm-reading-title">
          {{ $t('asset.virtual_meters.select_interval') }}
        </div>
        <el-select
          v-model="readingInfo.frequency"
          class="fc-input-full-border-select2 width40"
          filterable
          :placeholder="$t('asset.virtual_meters.select_interval')"
        >
          <el-option
            v-for="(key, label) in frequencies"
            :key="key"
            :label="label"
            :value="key"
          ></el-option>
        </el-select>
      </div>
      <div class="vm-reading-item mT7">
        <div class="vm-reading-title-main">
          {{ $t('asset.virtual_meters.formula_builder') }}
        </div>
        <VMFormulaBuilder
          :isEditForm="!isNew"
          :ruleCondition="ruleCondition"
          :startDuration="readingInfo.frequency"
          @setCondition="setCondition"
          moduleName="virtualMeterTemplate"
          class="width100 pL0 mT7"
        />
      </div>
    </div>
  </div>
</template>
<script>
import VMFormulaBuilder from './VMFormulaBuilder'
import { isEmpty } from '@facilio/utils/validation'

const frequencies = {
  '10 Mins': 6,
  '15 Mins': 7,
  '20 Mins': 8,
  '30 Mins': 9,
  '1 Hr': 10,
  '2 Hr': 11,
  '3 Hr': 12,
  '4 Hr': 13,
  '8 Hr': 14,
  '12 Hr': 15,
  '1 Day': 16,
}

export default {
  components: { VMFormulaBuilder },
  props: [
    'isNew',
    'vmInfo',
    'meterReadings',
    'reading',
    'canShowDelete',
    'validation',
    'ruleCondition',
  ],
  data() {
    return {
      readingInfo: null,
      frequencies,
      readingContentClass: '',
    }
  },
  created() {
    this.readingInfo = this.reading
  },
  computed: {
    activeReading() {
      let { readingInfo, meterReadings } = this
      let { readingFieldId } = readingInfo || {}
      let reading = (meterReadings || []).find(
        reading => reading.id === readingFieldId
      )

      return reading || {}
    },

    fullContentClass() {
      let { readingContentClass } = this
      return isEmpty(readingContentClass) ? 'content-top' : ''
    },
    titleClass() {
      let { readingContentClass } = this
      return isEmpty(readingContentClass) ? 'hide-title' : ''
    },
    accordionIconName() {
      let { titleClass } = this
      return !isEmpty(titleClass) ? 'accordion-up' : 'accordion-down'
    },
    disabledToggleClass() {
      let { activeReading } = this
      return isEmpty(activeReading) ? 'vm-disable-reading-collapse' : ''
    },
  },
  watch: {
    readingInfo: {
      async handler(newVal) {
        if (!isEmpty(newVal)) {
          this.$emit('updateReading', newVal)
        }
      },
      deep: true,
    },
    reading: {
      async handler(newVal) {
        this.readingInfo = newVal || {}
      },
      deep: true,
    },
    activeReading: {
      async handler() {
        let { isNew } = this
        if (!isNew) {
          this.fillReadingUnit()
        }
      },
      deep: true,
    },
  },
  methods: {
    deleteReading() {
      let { readingInfo } = this
      let { uuid } = readingInfo || {}
      this.$emit('deleteReading', uuid)
    },
    setCondition(condition) {
      let { readingInfo } = this
      this.readingInfo = { ...readingInfo, ...condition }
    },
    toggleReadingContent() {
      let { readingContentClass } = this

      if (isEmpty(readingContentClass)) {
        this.readingContentClass = 'vm-reading-collapse-content'
      } else {
        this.readingContentClass = ''
      }
    },
    fillReadingUnit() {
      let { readingInfo, activeReading } = this
      let { readingFieldId } = readingInfo || {}

      if (isEmpty(readingFieldId)) {
        this.$set(this.readingInfo, 'unit', '')
      } else {
        let unit = this.$getProperty(activeReading, 'unit', '')
        this.$set(this.readingInfo, 'unit', unit)
      }
    },
    isNewReading(reading) {
      let { id } = reading || {}
      return isEmpty(id) ? true : false
    },
  },
}
</script>
<style lang="scss">
.vm-form-item {
  flex: 1 1 100%;
  width: 100%;
  .el-form-item {
    display: flex;
    flex-direction: column;
  }
  .el-form-item__content {
    margin-left: 0px !important;
  }
  .el-form-item__label {
    width: 160px !important;
  }
}
.vm-reading-content {
  .vm-reading-item {
    display: flex;
    flex-direction: column;
    margin-bottom: 10px;
  }
  .vm-reading-title {
    font-size: 14px;
    color: #324056;
    margin-bottom: 10px;
  }
  .vm-reading-title-main {
    font-size: 16px;
    color: #324056;
    font-weight: 500;
    margin-bottom: 5px;
    margin-top: 5px;
  }
  .left-align {
    margin-left: -5%;
  }
}

.vm-reading-card {
  border-radius: 4px;
  background-color: #f2f5fa;
  padding: 20px;
  height: auto;
  overflow: hidden;
  margin-bottom: 15px;
  border-radius: 6px;
}
.vm-reading-collapse {
  height: auto;
  display: flex;
  justify-content: space-between;
  z-index: 3;
}
.vm-reading-collapse-content {
  height: 55px;
}
.content-top {
  margin-top: -5%;
}
.hide-title {
  visibility: hidden;
}
.vm-action-buttons {
  display: flex;
  gap: 15px;
  z-index: 5;
}
.vm-button-wrapper,
.delete-vm-container {
  height: 25px;
  width: 25px;
  border-radius: 50%;
  background-color: #fff;
  color: #324056;
  cursor: pointer;
  display: flex;
  justify-content: center;
}
.vm-action {
  align-self: center;
}
.vm-reading-title {
  color: #0074d1;
  font-weight: 500;
}
.vm-delete-filler {
  width: 25px;
  height: 25px;
}
.vm-disable-reading-collapse {
  opacity: 0.5;
  pointer-events: none;
  cursor: not-allowed;
}
</style>
