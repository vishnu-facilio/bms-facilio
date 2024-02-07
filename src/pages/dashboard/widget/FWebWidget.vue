<template>
  <div class="dragabale-card height100 webcard">
    <!-- <i class="el-icon-menu webcard-overlay-hide-icon"></i> -->
    <div class="overlay" v-if="mode" v-bind:class="{ act: edit }">
      <i
        class="el-icon-edit webcard-overlay-hide-icon"
        @click="edit = !edit"
      ></i>
    </div>
    <div v-show="edit" class="edit-url">
      <el-input v-model="tempurl" placeholder="Enter the Url">
        <template slot="append"
          ><span @click="closeeditUrl()">GO</span></template
        >
      </el-input>
    </div>
    <iframe
      v-if="embedUrl"
      :src="embedUrl"
      class="dashboard-web-widget"
      @error="error"
    >
    </iframe>
  </div>
</template>
<script>
export default {
  props: ['widget'],
  data() {
    return {
      embedUrl: null,
      tempurl: null,
      edit: false,
    }
  },
  computed: {
    mode() {
      if (
        this.$route.query &&
        this.$route.query.create &&
        this.$route.query.create === 'edit'
      ) {
        return true
      } else if (
        this.$route.query &&
        this.$route.query.create &&
        this.$route.query.create === 'new'
      ) {
        return true
      } else {
        return false
      }
    },
  },
  mounted() {
    this.getCardData()
  },
  methods: {
    getCardData() {
      if (this.widget && this.widget.dataOptions.metaJson) {
        this.embedUrl = this.widget.dataOptions.metaJson
        this.tempurl = this.widget.dataOptions.metaJson
      }
    },
    error(msg) {
      console.log('******* error', msg)
    },
    closeeditUrl() {
      this.edit = false
      this.embedUrl = this.tempurl
      this.widget.dataOptions.metaJson = this.embedUrl
    },
  },
}
</script>
<style>
.dashboard-web-widget {
  width: 100%;
  height: 100%;
  border: 0px;
}
.webcard .overlay {
  position: absolute;
  width: 100%;
  height: 100%;
}
.webcard-overlay-hide-icon {
  position: absolute;
  top: 18px;
  right: 35px;
  z-index: 10;
  cursor: pointer;
  color: #000;
  font-size: 15px;
}
.edit-url {
  position: absolute;
  width: 50%;
  z-index: 10;
  text-align: center;
  align-items: center;
  left: 25%;
  top: 50%;
  margin: auto;
}
.overlay.act {
  background: #000;
  opacity: 0.5;
}
.edit-url .el-input .el-input__inner {
  padding: 5px;
}
.edit-url .el-input-group__append {
  cursor: pointer;
}
</style>
