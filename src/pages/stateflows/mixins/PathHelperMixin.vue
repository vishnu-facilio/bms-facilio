<script>
import createPathFinder from 'l1-path-finder'
import ndarray from 'ndarray'
import Router from '../utils/PathFinding'
import { isValidAnchor } from '../utils/Common'

export default {
  created() {
    this.maze = null
    this.l1PathFinder = null
    this.router = null
  },
  methods: {
    constructMaze() {
      let allowedCanvasPanSize = this.allowedCanvasPanSize
      let cellSize = this.gridUnit

      let h = Math.ceil(allowedCanvasPanSize / cellSize)
      let w = Math.ceil(allowedCanvasPanSize / cellSize)
      let maze = ndarray([new Float32Array(h * w)], [h, w])

      return maze
    },

    initRouter() {
      let maze = this.constructMaze()

      this.l1PathFinder = Object.freeze(createPathFinder(maze))

      this.router = new Router(
        this.canvas,
        {
          allowedCanvasPanSize: this.allowedCanvasPanSize,
          gridUnit: this.gridUnit,
          excludeTypes: ['connector'],
        },
        this.l1PathFinder
      )
    },

    reinitRouter() {
      this.router = new Router(
        this.canvas,
        {
          allowedCanvasPanSize: this.allowedCanvasPanSize,
          gridUnit: this.gridUnit,
          excludeTypes: ['connector'],
        },
        this.l1PathFinder
      )
    },

    getPathCoords(transition, src, dest, options) {
      let isCycle =
        transition && transition.fromStateId === transition.toStateId

      let { isDragging = false, draggedObject = null } = options || {}

      let sourceProps = {
        fromPt: src.point,
        fromAnchor: isValidAnchor(src.anchor) ? src.anchor : -1,
        fromObject: src.element || null,
      }

      let destProps = {
        toPt: dest.point,
        toAnchor: isValidAnchor(dest.anchor) ? dest.anchor : -1,
        toObject: dest.element || null,
      }

      return this.router.search(sourceProps, destProps, {
        isDragging,
        draggedObject,
        isCycle,
      })
    },
  },
}
</script>
