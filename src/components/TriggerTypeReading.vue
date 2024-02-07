<template>
  <div>
    <el-row class="mT25">
      <el-col :span="24">
        <div class="fc-input-label-txt mb5">
          {{ $t('maintenance._workorder.reading_field') }}
        </div>
        <el-select
          filterable
          v-model="trigger.readingFieldId"
          style="width:100%"
          @change="forceUpdate"
          class="form-item fc-input-full-border-select2"
          placeholder=" "
        >
          <el-option
            v-for="readingField in readingFields"
            :key="readingField.id"
            :label="readingField.displayName"
            :value="readingField.id"
          ></el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-row class="mT25">
      <el-col :span="24">
        <div class="fc-input-label-txt mb5">
          {{ $t('maintenance._workorder.every') }}
        </div>
        <el-input
          v-model="trigger.readingInterval"
          class="fc-input-full-border2"
          type="number"
          placeholder
        ></el-input>
      </el-col>
    </el-row>
    <el-row class="mT25">
      <el-col :span="24">
        <div class="fc-input-label-txt mb5">
          {{ $t('maintenance._workorder.start_reading') }}
        </div>
        <el-input
          v-model="trigger.startReading"
          class="fc-input-full-border2"
          type="number"
          placeholder
        ></el-input>
      </el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  props: ['trigger', 'resource'],
  data() {
    return {
      readingFields: [],
    }
  },
  created() {
    if (!this.resource) {
      return
    }
    if (this.resource.resourceTypeEnum === 'SPACE') {
      this.$util
        .loadSpaceReadingFields(this.resource.id, false)
        .then(fields => {
          this.readingFields = fields
        })
    } else if (this.resource.resourceTypeEnum === 'ASSET') {
      this.$util
        .loadAssetReadingFields(this.resource.id, -1, false)
        .then(fields => {
          this.readingFields = fields
        })
    }
  },
  methods: {
    forceUpdate() {
      this.$forceUpdate()
    },
  },
}
</script>
<style></style>
