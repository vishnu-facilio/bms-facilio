<template>
  <div v-if="showWidget">
    <el-dialog
      :visible.sync="showWidget"
      :width="width"
      :top="top"
      :append-to-body="true"
      :beforeClose="closeConnectedAppWidget"
      class="connected-app-dialog"
      :show-close="false"
    >
      <ConnectedAppViewWidget
        :style="{ height: height }"
        :ref="`ref-${widgetId}`"
        :key="widgetId"
        :widgetId="widgetId"
        v-bind="recordProps"
        :handlers="handlers"
      ></ConnectedAppViewWidget>
    </el-dialog>
  </div>
</template>

<script>
import ConnectedAppViewWidget from 'pages/connectedapps/ConnectedAppViewWidget'
export default {
  props: ['record', 'widgetId', 'showConnectedAppWidget'],
  components: { ConnectedAppViewWidget },
  data() {
    return {
      showWidget: this.showConnectedAppWidget,
      width: '80%',
      height: '70vh',
      top: '10vh',
      handlers: {},
    }
  },
  created() {
    this.initWidgetHandlers()
  },
  computed: {
    recordProps() {
      if (this.record) {
        return {
          recordId: this.record.id,
          context: this.record,
        }
      } else {
        return {}
      }
    },
  },
  watch: {
    showConnectedAppWidget(newValue) {
      this.showWidget = newValue
    },
  },
  methods: {
    initWidgetHandlers() {
      let resize = ({ width, height, top }) => {
        this.width = width || this.width
        this.height = height || this.height
        this.top = top || this.top
      }
      let hide = ({ showWidget }) => {
        this.showConnectedAppWidget = showWidget
      }
      let reloadData = () => {
        this.$emit('loadData')
      }
      this.handlers = {
        resize,
        hide,
        reloadData,
      }
    },
    closeConnectedAppWidget() {
      this.$emit('update:showConnectedAppWidget', false)
    },
  },
}
</script>

<style></style>
