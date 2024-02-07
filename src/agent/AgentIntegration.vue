<template>
  <div class="page-width-cal">
    <el-header class="fc-agent-main-header" height="80">
      <div class="flex-middle justify-content-space">
        <div class="fc-agent-black-26">
          Integration
        </div>
        <div class="">
          <el-button class="fc-agent-add-btn">
            <i class="el-icon-plus pR5 fwBold"></i>Add Integration
          </el-button>
        </div>
      </div>
    </el-header>
    <div class="agent-integration-con">
      <div class="agent-integration-card" @click="showWattsenseform()">
        <img src="~assets/svgs/agent/watt-sense.svg" width="200" />
        <el-button
          class="fc-agent-add-btn mT40 width100"
          @click="wattsenseFormDialog = true"
          >Integrate</el-button
        >
      </div>
      <div class="agent-integration-card">
        <img src="~assets/svgs/agent/altair.svg" width="200" />
        <el-button
          class="fc-agent-add-btn mT40 width100"
          @click="altairFormdialog = true"
          >Integrate</el-button
        >
      </div>
    </div>
    <watt-sense-form
      v-if="wattsenseFormDialog"
      :visibility.sync="wattsenseFormDialog"
    ></watt-sense-form>
    <altair-form
      v-if="altairFormdialog"
      :visibility.sync="altairFormdialog"
    ></altair-form>
  </div>
</template>
<script>
import wattSenseForm from 'agent/components/AgentWattSenseForm'
import altairForm from 'agent/components/AgentAltairForm'
export default {
  data() {
    return {
      wattsenseFormDialog: false,
      altairFormdialog: false,
    }
  },
  components: {
    wattSenseForm,
    altairForm,
  },
  methods: {
    showWattsenseform() {
      this.wattsenseFormDialog = true
    },
    showAltairform() {
      this.altairFormdialog = true
    },
  },
}
</script>

<style lang="scss">
.agent-integration-con {
  display: flex;
  align-items: center;
  width: 100%;
  flex-wrap: wrap;
  margin: 20px;
  .agent-integration-card {
    width: 260px;
    height: 180px;
    border-radius: 5px;
    box-shadow: 0 6px 23px 0 rgba(22, 18, 58, 0.03);
    border: solid 1px #edf3f3;
    background-color: #ffffff;
    text-align: center;
    padding: 20px;
    margin-right: 10px;
    position: relative;
  }
}
</style>
