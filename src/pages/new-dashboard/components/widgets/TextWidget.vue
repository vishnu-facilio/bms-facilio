<template>
  <div class="f-quill ql-snow dragabale-card imagecard readonly">
    <div class="f-quill-view ql-editor text-data-container">
      <div class="ql-container p0" v-html="widget.dataOptions.metaJson"></div>
    </div>
    <text-card-editer
      v-if="showtextcard"
      :widget="item"
      :visibility.sync="showtextcard"
      @update="updateTextCard"
      @close="showtextcard = false"
    />
  </div>
</template>
<script>
import textCardEditer from '@/TextCardPopup'
import { cloneDeep } from 'lodash'
import BaseWidgetMixin from 'src/pages/new-dashboard/components/widgets/BaseWidgetMixin.js'
export default {
  components: {
    textCardEditer,
  },
  mixins: [BaseWidgetMixin],
  props: {
    updateWidget: {
      type: Function,
    },
    item: {
      required: true,
      type: Object,
    },
  },
  data() {
    return {
      showtextcard: false,
      content: null,
    }
  },
  created() {
    this.initWidget(this.widgetConfig)
  },
  mounted() {
    this.getCardData()
  },
  watch: {
    content: {
      handler(newData, oldData) {
        this.updatecontehnt(false)
      },
    },
    widget: {
      handler(newData, oldData) {
        this.getCardData()
      },
      deep: true,
    },
  },
  methods: {
    updateTextCard(textData) {
      const { item } = this
      const clonedCard = cloneDeep(item)
      clonedCard.widget.dataOptions.metaJson = textData
      const { updateWidget } = this
      updateWidget(clonedCard)
      this.showtextcard = false
    },
    getCardData() {
      if (this.widget && this.widget.dataOptions.metaJson) {
        this.content = this.widget.dataOptions.metaJson
      }
    },
    updatecontehnt() {
      this.widget.dataOptions.metaJson = this.content
    },
  },
  computed: {
    widget() {
      return this.item.widget
    },
    id() {
      const {
        item: { id },
      } = this
      return id
    },
    widgetConfig() {
      const { id } = this ?? {}
      return {
        id: id,
        minW: 25,
        maxW: 96,
        minH: 10,
        maxH: 50,
        showHeader: false,
        showExpand: true,
        noResize: false,
        showDropDown: true,
        editMenu: [
          {
            label: 'Edit',
            action: () => {
              this.showtextcard = true
            },
            icon: 'el-icon-edit',
          },
        ],
        borderAroundWidget: true,
        viewMenu: [],
      }
    },
  },
}
</script>
<style>
.fquillidel {
  overflow: hidden;
  width: 0px;
  height: 0px !important;
  margin: 0;
  position: absolute;
  right: 0;
  top: 0;
}
.f-quill,
.editr {
  height: 100%;
  overflow: auto;
}
.readonly .ql-toolbar {
  display: none;
}
/* .readonly {
    padding: 20px;
  } */
p {
  font-size: 100%;
}
.dragabale-card.dragpoint {
  position: absolute;
  left: 0;
  top: 0;
  border-radius: 5px;
  width: 10px;
  height: 10px;
  background: #ff3e90;
}
h1,
h2,
h3,
h4,
h5,
h6 {
  line-height: normal;
  font-weight: unset;
}
</style>
<style scoped lang="scss">
.text-data-container {
  width: 100%;
}
</style>
