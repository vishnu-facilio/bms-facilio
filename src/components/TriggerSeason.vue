<template>
  <div>
    <div class="mT20 enable-season-label">
      {{ 'Seasonal' }}
      <fc-icon
        v-tippy
        :title="'Configure season names, start and stop dates to trigger'"
        class="mL5 pointer"
        :size="14"
        group="action"
        name="info"
      ></fc-icon>
    </div>
    <el-switch
      v-model="showSeason"
      @change="setSeasonNull"
      class="mT10"
    ></el-switch>
    <template v-if="showSeason">
      <template v-for="(form, index) in forms">
        <TriggerSeasonForm
          :key="form.persistIndex + 'key'"
          :item="index + 1"
          :formObj="form"
          :canShowRemove="currentSeason > 1"
          @removeLineItem="removeTriggerSeasonLineItem"
          @onModelChange="onModelChange"
        >
        </TriggerSeasonForm>
        <div :key="index + 'line-key'" class="bottom-line-container">
          <div class="bottom-line"></div>
        </div>
      </template>
      <template v-if="currentSeason < maxAllowedSeason">
        <div @click="addLineItem()" class="add-season-button">
          {{ $t('common.trigger.add_season') }}
        </div>
      </template>
    </template>
  </div>
</template>
<script>
import TriggerSeasonForm from 'src/components/TriggerSeasonForm.vue'
import { eventBus } from './page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['triggerEdit'],
  data() {
    return {
      showSeason: false,
      maxAllowedSeason: 5,
      currentSeason: 1,
      name: '',
      forms: [],
      persistIndex: 1,
    }
  },
  computed: {},
  components: {
    TriggerSeasonForm,
  },
  created() {
    this.init()
  },
  destroyed() {
    eventBus.$destroy()
  },
  methods: {
    init() {
      let { triggerEdit } = this
      if (!isEmpty(triggerEdit)) {
        let { schedule } = triggerEdit || {}
        let { seasons } = schedule || {}
        if (!isEmpty(seasons)) {
          this.showSeason = true
          this.forms = seasons
          this.currentSeason = seasons.length
        }
      } else {
        this.initialiseItem(0)
      }
    },
    setSeasonNull() {
      this.forms = []
      this.currentSeason = 1
      let { triggerEdit } = this
      let { schedule } = triggerEdit || {}
      schedule.seasons = null
      if (this.showSeason) {
        this.initialiseItem(0)
      } else {
        eventBus.$emit('changedSeasonsTrigger', null)
      }
    },
    initialiseItem(index) {
      let { persistIndex } = this
      this.forms[index] = {
        persistIndex,
      }
      this.persistIndex = persistIndex + 1
    },
    addLineItem() {
      let { currentSeason } = this
      this.$setProperty(this, 'currentSeason', currentSeason + 1)
      this.initialiseItem(this.currentSeason - 1)
    },
    removeLineItem() {
      let { currentSeason } = this
      this.$setProperty(this, 'currentSeason', currentSeason - 1)
    },
    removeTriggerSeasonLineItem(item) {
      this.removeLineItem()
      let { forms } = this
      forms.splice(item - 1, 1)
      this.prepareData()
    },
    onModelChange(props) {
      let { formModel, item } = props || {}
      this.forms[item - 1] = formModel
      this.prepareData()
    },
    prepareData() {
      let { forms, triggerEdit } = this
      let { schedule } = triggerEdit || {}
      schedule.seasons = forms
      eventBus.$emit('changedSeasonsTrigger', forms)
    },
  },
}
</script>
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
.add-season-button {
  color: #38b2c3;
  font-weight: 500;
  margin-top: 24px;
  cursor: pointer;
}
.bottom-line {
  width: 100%;
  background-color: #f0f0f0;
  height: 1px;
}
.bottom-line-container {
  margin-top: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
