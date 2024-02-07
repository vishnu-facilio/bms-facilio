<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  data() {
    return {
      canvasStates: [],
      currentCanvasState: -1,
      skipHistoryPush: false,
    }
  },
  computed: {
    canShowUndo() {
      return this.currentCanvasState >= 1
    },
    canShowRedo() {
      return this.currentCanvasState < this.canvasStates.length - 1
    },
  },
  methods: {
    resetHistory() {
      this.canvasStates = []
      this.currentCanvasState = -1
    },

    addToHistory(appendToCurrent) {
      if (this.skipHistoryPush) {
        this.skipHistoryPush = false
      } else {
        let data = JSON.stringify(this.serializeCanvas())
        if (!appendToCurrent) {
          this.currentCanvasState = this.currentCanvasState + 1
        }

        let canvasStates = this.canvasStates
        canvasStates[this.currentCanvasState] = data

        if (canvasStates.length - 1 > this.currentCanvasState) {
          let validStates = canvasStates.slice(0, this.currentCanvasState + 1)
          this.canvasStates = validStates
        }
      }
    },

    undo() {
      this.currentCanvasState = this.currentCanvasState - 1
      let json = this.canvasStates[this.currentCanvasState]
      this.stateFlow.diagramJson = json

      this.skipHistoryPush = true
      this.drawStateFlow()
      this.autoSave()
    },

    redo() {
      this.currentCanvasState = this.currentCanvasState + 1
      let json = this.canvasStates[this.currentCanvasState]
      this.stateFlow.diagramJson = json

      this.skipHistoryPush = true
      this.drawStateFlow()
      this.autoSave()
    },

    updateDiagram(data = null) {
      let json = !isEmpty(data)
        ? JSON.stringify(data)
        : JSON.stringify(this.serializeCanvas())
      this.stateFlow.diagramJson = json
    },
  },
}
</script>
