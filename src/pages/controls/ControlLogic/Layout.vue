<template>
  <div class="layout container">
    <subheader
      :menu="subheaderMenu"
      newbtn="false"
      type="workorder"
      parent="/app/co/controls"
    >
      <div class="row">
        <div class="col-12">
          <div class="fR fc-subheader-right">
            <portal-target name="controllogicpagenation"></portal-target>
            <div class="block"></div>
          </div>
        </div>
      </div>
      <!-- <div class="fR">
          <pagination :total="listCount" :perPage="perPage" ref="f-page"></pagination>
      </div> -->
      <button
        class="fc-create-btn mR60 control-logic-list-btn"
        @click="addControlRule()"
      >
        NEW CONTROL LOGIC
      </button>
    </subheader>
    <router-view
      @syncCount="callbackMethod"
      class=""
      ref="controllist"
    ></router-view>
    <new-logic
      v-if="controlLogicVisible"
      :controlLogic="selectedControlLogic"
      :isNew="isNew"
      :visibility.sync="controlLogicVisible"
    ></new-logic>
  </div>
</template>
<script>
import Subheader from '@/Subheader'
import NewLogic from 'pages/controls/ControlLogic/NewControlLogicForm'

export default {
  data() {
    return {
      subheaderMenu: [
        {
          label: 'All Control Logic',
          path: { path: '/app/co/cl/controllogic' },
        },
      ],
      controlLogicVisible: false,
      selectedControlLogic: null,
    }
  },
  watch: {},
  components: {
    Subheader,
    NewLogic,
  },
  mounted() {
    this.$store.dispatch('view/loadModuleMeta', 'workflowrule')
  },
  methods: {
    addControlRule(controlData) {
      // this.$refs.controllist.addControllogic()
      this.controlLogicVisible = true
      this.isNew = true
      this.selectedControlLogic = controlData
    },
  },
}
</script>
<style lang="scss">
.control-logic-list-btn {
  position: absolute;
  right: 0;
  top: 10px;
}
</style>
