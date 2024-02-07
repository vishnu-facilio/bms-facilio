<template>
  <div class="floorplan-builder-toolbar">
    <el-button-group>
      <!-- <el-tooltip class="item" effect="dark" content="Undo" placement="bottom">
        <el-button type="plain" icon="fa fa-undo"></el-button>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" content="Redo" placement="bottom">
        <el-button type="plain" icon="fa fa-repeat"></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Send to back"
        placement="bottom"
      >
        <el-button
          type="plain"
          icon="fa fa-object-group"
          @click="tool('sendToBack')"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Bring to front"
        placement="bottom"
      >
        <el-button
          type="plain"
          icon="fa fa-object-ungroup"
          @click="tool('bringToFront')"
        ></el-button>
      </el-tooltip>
      -->
      <!-- <el-tooltip
        class="item"
        effect="dark"
        content="Marker value"
        placement="bottom"
      >
        <el-button
          type="plain"
          icon="fa fa-square-o"
          @click="tool('enableMarkerValue')"
        ></el-button>
      </el-tooltip> -->
      <!-- <el-tooltip class="item" effect="dark" content="Lock" placement="bottom">
        <el-button
          type="plain"
          @click="tool('lock')"
          icon="fa fa-lock"
        ></el-button>
      </el-tooltip> -->
      <el-tooltip
        class="item"
        effect="dark"
        content="Unlock Floor and Spaces"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('unlock')"
          icon="fa fa-unlock"
        ></el-button>
      </el-tooltip>
    </el-button-group>

    <!-- <el-button-group>
      <el-tooltip
        class="item"
        effect="dark"
        content="Add Text"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addText')"
          icon="fa fa-font"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Add Button"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addButton')"
          icon="fa fa-stop"
        ></el-button>
      </el-tooltip>
      <el-tooltip
      class="item position-relative"
      effect="dark"
      content="Add Image"
      placement="bottom"
    >
      <el-button type="plain" icon="fa fa-image">
        <input
          type="file"
          class="graphics-add-image"
        />
      </el-button>
    </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Add Line"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addLine')"
          icon="fa fa-pencil"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Add Square"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addSquare')"
          icon="fa fa-square-o"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Add Circle"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addCircle')"
          icon="fa fa-circle-o"
        ></el-button>
      </el-tooltip>
    </el-button-group> -->

    <el-button-group>
      <el-tooltip
        class="item"
        effect="dark"
        content="Add Button"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addButton')"
          icon="fa fa-stop"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Add Pointer"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addPointer')"
          icon="fa fa-circle-o"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="ADD CCTV"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addUtilMarker', '1')"
          icon="fa el-icon-menu"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="ADD LIFT"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addUtilMarker', '2')"
          icon="fa el-icon-menu"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="ADD WIFI"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addUtilMarker', '3')"
          icon="fa el-icon-menu"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="ADD FIRE EXTINGUISHER"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addUtilMarker', '4')"
          icon="fa el-icon-menu"
        ></el-button>
      </el-tooltip>
      <!-- <el-tooltip
        class="item"
        effect="dark"
        content="Add Utils"
        placement="bottom"
      >
        <el-dropdown
          size="mini"
          split-button
          type="primary"
          @command="handleCommand"
        >
          element
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="1">CCTV</el-dropdown-item>
            <el-dropdown-item command="2">LIFT</el-dropdown-item>
            <el-dropdown-item command="3">WIFI</el-dropdown-item>
            <el-dropdown-item command="4">FIRE EXTINGUISHER</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-tooltip> -->
      <el-tooltip
        class="item"
        effect="dark"
        content="Floor Plan Zoning"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('addPolygon')"
          icon="fa fa-map-o"
        ></el-button>
      </el-tooltip>
    </el-button-group>

    <!-- <el-button-group class="fR">
      <el-tooltip
        class="item"
        effect="dark"
        :content="expand ? 'Minimize' : 'Maximize'"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('expand')"
          icon="fa fa-arrows-alt"
        ></el-button>
      </el-tooltip>
    </el-button-group> -->
    <!-- <el-button-group class="fR">
      <el-tooltip
        class="item"
        effect="dark"
        :content="expand ? 'Layer' : 'Layer'"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('layer')"
          icon="fa fa-cog"
        ></el-button>
      </el-tooltip>
    </el-button-group> -->
    <el-button-group class="fR">
      <el-tooltip
        class="item"
        effect="dark"
        content="Zoom In"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('zoomIn')"
          icon="fa fa-search-plus"
        ></el-button>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        content="Zoom Out"
        placement="bottom"
      >
        <el-button
          type="plain"
          @click="tool('zoomOut')"
          icon="fa fa-search-minus"
        ></el-button>
      </el-tooltip>
      <!-- <el-tooltip
        class="item"
        effect="dark"
        content="Fit To Screen"
        placement="bottom"
      >
        <el-button type="plain" icon="fa fa-compress"></el-button>
      </el-tooltip> -->
    </el-button-group>
  </div>
</template>

<script>
export default {
  data() {
    return {
      expand: false,
    }
  },
  methods: {
    tool(action, mode) {
      this.$emit('action', action, mode)
    },
    selectLayer() {
      this.$emit('layer', {})
    },
    handleCommand(command) {
      this.tool('addUtilMarker', command)
    },
    expandEditor() {},
  },
}
</script>
