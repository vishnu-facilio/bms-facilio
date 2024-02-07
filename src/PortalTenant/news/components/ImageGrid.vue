<script>
import { isEmpty } from '@facilio/utils/validation'
import PreviewFile from '@/PreviewFile'

export default {
  props: {
    images: {
      type: Array,
      required: true,
    },
  },
  data() {
    return { previewFile: null, canShowPreview: false }
  },
  computed: {
    showOne() {
      let { images } = this
      return [1, 4].includes(images.length)
    },
    showTwo() {
      let { images } = this
      return images.length >= 2 && ![3, 4].includes(images.length)
    },
    showThree() {
      let { images } = this
      return images.length >= 3
    },
  },
  render() {
    return (
      !isEmpty(this.images) && (
        <div class="grid">
          {this.showOne && this.renderOne()}
          {this.showTwo && this.renderTwo()}
          {this.showThree && this.renderThree()}
          {this.canShowPreview && this.renderPreview()}
        </div>
      )
    )
  },
  methods: {
    togglePreview(val) {
      this.canShowPreview = val
    },
    openPreview(file) {
      this.previewFile = file
      this.togglePreview(true)
    },
    renderOne() {
      const { images, imageCard } = this
      const [file] = images

      return (
        <div class="row">
          <div class="col">{imageCard(file)}</div>
        </div>
      )
    },
    renderTwo() {
      const { images, imageCard } = this
      const [file1, file2, file3] = images

      const wasFirstRendered = this.showOne
      const excessCount = wasFirstRendered
        ? images.length - 3
        : images.length - 2
      const showOverlay = excessCount > 0 && !this.showThree

      return (
        <div class="row">
          <div class="col">{imageCard(wasFirstRendered ? file2 : file1)}</div>
          <div class="col">
            {imageCard(wasFirstRendered ? file3 : file2, {
              showOverlay,
              count: excessCount,
            })}
          </div>
        </div>
      )
    },
    renderThree() {
      const { images, imageCard } = this

      const wasFirstRendered = this.showOne
      const wasSecondRendered = this.showTwo

      let filteredFiles = []

      if (wasSecondRendered) {
        // eslint-disable-next-line no-unused-vars
        let [first, second, ...rest] = images
        filteredFiles = rest
      } else if (wasFirstRendered) {
        // eslint-disable-next-line no-unused-vars
        let [first, ...rest] = images
        filteredFiles = rest
      } else {
        filteredFiles = images
      }

      const excessCount = filteredFiles.length - 3
      const showOverlay = excessCount > 0

      return (
        <div class="row">
          <div class="col">{imageCard(filteredFiles[0])}</div>
          <div class="col">{imageCard(filteredFiles[1])}</div>
          <div class="col">
            {imageCard(filteredFiles[2], {
              showOverlay,
              count: excessCount,
            })}
          </div>
        </div>
      )
    },
    renderPreview() {
      let { canShowPreview, previewFile, images } = this
      return (
        <el-dialog
          visible={canShowPreview}
          append-to-body={true}
          class="f-list-attachment-dialog"
        >
          <PreviewFile
            visibility={canShowPreview}
            {...{
              on: {
                'update:visibility': this.togglePreview,
              },
            }}
            previewFile={previewFile}
            files={images}
          ></PreviewFile>
        </el-dialog>
      )
    },
    imageCard(file, { showOverlay = false, count = null } = {}) {
      let { openPreview, $prependBaseUrl } = this
      let url = $prependBaseUrl(file.previewUrl)
      let style = { 'background-image': `url(${url})` }

      return (
        <div
          style={style}
          vOn:click={() => openPreview(file)}
          fit="fill"
          class="image-card"
        >
          {showOverlay && (
            <div class="overlay" vOn:click={() => openPreview(file)}>
              <span class="overlay-count">+{count + 1}</span>
            </div>
          )}
        </div>
      )
    },
  },
}
</script>
<style lang="scss" scoped>
.grid {
  display: flex;
  flex-direction: column;
  height: 285px;
  overflow: hidden;
}
.row {
  display: flex;
  flex-direction: row;
  flex-grow: 1;
  &:not(:first-of-type) {
    margin-top: 2px;
  }
}
.col {
  flex-grow: 1;
  &:not(:first-of-type) {
    margin-left: 2px;
  }
}
.image-card {
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  height: 100%;
  width: 100%;
}
.overlay {
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;

  .overlay-count {
    font-size: 3rem;
    line-height: 0;
  }
}
</style>
