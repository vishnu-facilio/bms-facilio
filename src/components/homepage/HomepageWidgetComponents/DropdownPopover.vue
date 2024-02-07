<template>
  <el-dropdown
    @command="redirectTo($event)"
    trigger="click"
    :hide-on-click="false"
  >
    <span class="el-dropdown-link">
      More <br />
      Options
    </span>
    <el-dropdown-menu slot="dropdown" style="overflow:auto;">
      <el-dropdown-item style="position:sticky;top:0;"
        ><div style="background-color:yellow;height:30px;">
          <el-input
            placeholder="Search"
            v-model="searchTerm"
            style="height:30px"
          >
          </el-input></div
      ></el-dropdown-item>
      <el-dropdown-item
        v-for="(option, index) in filteredCatalogs"
        :key="index"
        :command="option.id"
      >
        <div class="dropdown-option" style="display:flex;">
          <img
            src="~statics/icons/icon-ep.png"
            style="height:25px;width:25px;"
          />
          <p style="margin-left:20px">{{ option.name }}</p>
        </div>
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>
<script>
export default {
  mounted() {
    console.log('options : ', this.options)
  },
  props: ['options'],
  data() {
    return {
      searchTerm: '',
    }
  },
  methods: {
    redirectTo(id) {
      if (!id) {
        return
      }
      this.$router.push(
        `/service/service-catalog/service-catalog/requests/${id}`
      )
    },
  },
  computed: {
    filteredCatalogs() {
      let filteredCatalog = this.options.filter(option => {
        if (this.searchTerm.toLowerCase() == 'all') {
          return option
        } else if (
          option.name.toLowerCase().includes(this.searchTerm.toLowerCase())
        ) {
          return option
        }
      })
      return filteredCatalog
    },
  },
}
</script>
<style scoped></style>
