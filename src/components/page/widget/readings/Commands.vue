<template>
  <div class="command-card">
    <div class="f14 bold">{{ reading.field.displayName }}</div>
    <div class="mT30 bold f18">
      <div
        v-html="$fieldUtils.getDisplayValue(reading.field, reading.value)"
      ></div>
    </div>
    <el-button
      size="small"
      class="text-uppercase fc__border__btn mT35 mB0 pR30 pL30"
      style="max-width: 90px;"
      @click="showSetReadingDialog(reading.field, reading.value)"
      >Set</el-button
    >

    <SetReadingPopup
      v-if="showSetDialog"
      :key="newReading.field.id"
      :reading="newReading"
      :saveAction="closePopup"
      :closeAction="closePopup"
      :recordId="details.id"
      :recordName="details.name"
    ></SetReadingPopup>
  </div>
</template>
<script>
import SetReadingPopup from '@/readings/SetReadingValue'

export default {
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'sectionKey',
    'widget',
    'resizeWidget',
  ],
  components: { SetReadingPopup },
  data() {
    return {
      loading: false,
      reading: null,
      showSetDialog: false,
      newReading: {
        value: null,
        ttime: null,
      },
      readingSaving: false,
    }
  },
  computed: {
    setValue() {
      let data = this.$store.state.publishdata.setValue[this.details.id]
      return data ? data[this.reading.field.id] : null
    },
  },
  watch: {
    widget: {
      immediate: true,
      handler: 'loadData',
    },
    setValue: {
      handler: 'setFieldValue',
    },
  },
  methods: {
    loadData() {
      this.reading = this.widget.widgetParams.data
    },
    showSetReadingDialog(field, value) {
      this.newReading.field = field
      this.newReading.value = value
      this.showSetDialog = true
    },
    setFieldValue() {
      if (this.setValue) {
        this.reading.value = this.setValue
      }
    },
    closePopup() {
      this.showSetDialog = false
      this.resetEditObj()
    },
    resetEditObj() {
      this.newReading = {
        ttime: null,
        value: null,
      }
    },
  },
}
</script>
<style scoped>
.command-card {
  display: flex;
  flex-direction: column;
  padding: 40px 30px;
  align-items: center;
}
.set-button {
  max-width: 90px;
}
</style>
