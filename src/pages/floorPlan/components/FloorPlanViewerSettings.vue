<template>
  <div class="fp-viwer-settings">
    <el-button
      icon="el-icon-refresh"
      size="small"
      class="p12"
      plain
      @click="action('resetPanAndZoom')"
      v-if="refresh"
    ></el-button>
    <el-popover
      placement="bottom"
      title="Views"
      width="200"
      trigger="click"
      v-model="popovervisible"
    >
      <div class="fp-leagend-header-settings">
        <el-select
          @change="action('viewMode', view)"
          v-model="view"
          placeholder="Select"
          class="fc-input-select fc-input-full-border2 width100"
        >
          <el-option
            v-for="item in viewModes"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
          <el-option
            :key="213"
            label="VAV readings"
            value="vavreadings"
            v-if="$account.org.id === 343 || $account.org.id === 1"
          ></el-option>
          <el-option
            :key="213"
            label="Custom script"
            value="customscript"
            v-if="$account.org.id === 410"
          ></el-option>
        </el-select>
      </div>
      <div class="fp-leagend-header-settings" v-if="view === 'readings'">
        <p class="fc-input-label-txt pB5 pT5">Asset Category</p>
        <el-select
          filterable
          @change="loadAssetFields()"
          v-model="heatmapData.assetCategoryId"
          placeholder="Select"
          class="fc-input-select fc-input-full-border2 width100"
        >
          <el-option
            v-for="(category, index) in assetCategory"
            :key="index"
            :label="category.name"
            :value="category.id"
          >
          </el-option>
        </el-select>
      </div>
      <div class="fp-leagend-header-settings" v-if="view === 'readings'">
        <p class="fc-input-label-txt pB5 pT5">Asset Field Name</p>
        <el-select
          filterable
          @change="setReadingFieldModuleName()"
          v-model="heatmapData.readingFieldName"
          placeholder="Select"
          class="fc-input-select fc-input-full-border2 width100"
        >
          <el-option
            v-for="(field, index) in assetFields"
            :key="index"
            :label="field.displayName"
            :value="field.name"
          >
          </el-option>
        </el-select>
      </div>
      <el-button
        slot="reference"
        class="p12"
        icon="el-icon-setting"
        size="small"
        plain
        v-if="!$mobile && viewMode && !hideSettings"
      >
      </el-button>
    </el-popover>
    <el-button-group class="fb-settings-block">
      <el-button
        icon="el-icon-plus"
        size="small"
        plain
        @click="action('zoomIn')"
      ></el-button>
      <el-button
        icon="el-icon-minus"
        size="small"
        plain
        @click="action('zoomOut')"
      ></el-button>
    </el-button-group>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  props: ['refresh', 'viewMode', 'viewParams', 'hideSettings'],
  data() {
    return {
      popovervisible: false,
      heatmapData: {
        assetCategoryId: null,
        readingFieldName: null,
        readingModule: null,
      },
      assetFields: [],
      view: 'default',
      viewModes: [
        {
          label: 'Default',
          value: 'default',
        },
        {
          label: 'Maintenance',
          value: 'maintenance',
        },
        {
          label: 'Control points',
          value: 'control_points',
        },
        {
          label: 'Readings',
          value: 'readings',
        },
      ],
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
  },
  watch: {
    viewMode: {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.view = this.viewMode
        }
      },
    },
  },
  mounted() {
    if (this.viewMode) {
      this.view = this.viewMode
    }
    if (this.viewParams && this.viewParams.assetCategoryId) {
      this.heatmapData = this.viewParams
      this.loadAssetFields(true)
    }
  },
  methods: {
    refreshPopover() {
      this.heatmapData = {
        assetCategoryId: null,
        readingFieldName: null,
        readingModule: null,
      }
      this.popovervisible = false
      setTimeout(() => {
        this.popovervisible = true
      }, 100)
    },
    openpopover() {
      this.popovervisible = !this.popovervisible
    },
    action(action, data) {
      if (action === 'viewMode' && data !== 'readings') {
        this.$emit('action', action, data)
      } else if (action === 'viewMode') {
        this.refreshPopover()
      } else {
        this.$emit('action', action)
      }
    },
    setReadingFieldModuleName() {
      if (this.heatmapData.readingFieldName) {
        let fieldname = this.heatmapData.readingFieldName
        let fieldobj = this.assetFields.find(rt => rt.name === fieldname)
        this.heatmapData.readingModule = fieldobj.module.name
        this.$emit('action', 'viewMode', 'readings', this.heatmapData)
      }
    },
    loadAssetFields(edit) {
      if (!edit) {
        this.heatmapData.readingFieldName = null
      }
      this.$util
        .loadAssetReadingFields(-1, this.heatmapData.assetCategoryId)
        .then(fields => {
          this.assetFields = fields
        })
    },
  },
}
</script>
<style>
.fp-leagend-header-settings .fc-input-full-border2 .el-input__inner {
  width: 100%;
}
</style>
