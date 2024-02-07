<template>
  <div class="control-home-container">
    <div class="header">
      <i class="el-icon-menu"></i>
      <p class="heading">{{ $t('controlmobileui.control-home.title') }}</p>
    </div>
    <div>
      <Spinner v-if="loading" class="mT40" :show="loading"></Spinner>
      <el-table width="100%" v-else class="newtable" :data="assets">
        <el-table-column width="60px">
          <template>
            <i class="el-icon-cpu"></i>
          </template>
        </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <router-link
              :to="'/webview/sc/' + scope.row.id + '/lighting'"
              class="route-title"
            >
              {{ scope.row.name }}
            </router-link>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
export default {
  components: { Spinner },
  mounted() {
    this.init()
  },
  data() {
    return {
      assets: [],
      loading: false,
    }
  },
  methods: {
    async init() {
      this.loading = true
      let { data, error } = await API.post('/v2/workflow/runWorkflow', {
        nameSpace: 'controls',
        functionName: 'getControlAssets',
      })
      if (error) {
        this.$message.error(error.message || 'Error occured')
      } else if (data && data.workflow) {
        let { workflow } = data
        let { returnValue = [] } = workflow
        this.assets = returnValue
      }
      this.loading = false
    },
  },
}
</script>
<style scoped>
.el-icon-menu {
  font-size: 30px;
  margin-left: 15px;
}
.el-icon-cpu {
  margin-left: 16px;
  font-size: 30px;
}
.header {
  width: 100%;
  height: 75px;
  display: flex;
  align-items: center;
  border-block-end: 2px solid rgba(182, 179, 179, 0.6);
}
.heading {
  font-size: 16px;
  font-weight: 700;
  margin-top: 13px;
  margin-left: 15px;
}
.route-title {
  font-size: 16px;
  letter-spacing: 1px;
  color: black;
}
</style>
<style lang="scss">
.control-home-container {
  .newtable thead {
    display: none;
  }
  .newtable td {
    height: 70px;
  }
  .newtable table {
    width: 100%;
  }
  el-table {
    width: 100%;
  }
}
</style>
