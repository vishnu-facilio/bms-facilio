<template>
  <div>
    <el-row class="mT20" :gutter="20">
      <el-col :span="5">
        <div class="category-blue-txt pB10">{{ $t('setup.setup.agent') }}</div>
        <el-select
          v-model="selectedAgent"
          filterable
          @change="loadControllers"
          placeholder="Select Agent"
          width="250px"
          class="fc-input-full-border-select2 flLeft"
        >
          <el-option
            v-for="(agent, index) in agents"
            :key="index"
            :label="agent.name"
            :value="agent.id"
            no-data-text="No agents available"
          ></el-option>
        </el-select>
      </el-col>
      <el-col :span="4">
        <div class="category-blue-txt pB10">Type</div>
        <el-select
          v-model="selectedType"
          filterable
          placeholder="Select Type"
          width="250px"
          class="fc-input-full-border-select2 flLeft"
          @change="onTypeChange"
        >
          <template v-for="(label, value) in $constants.ControllerTypes">
            <el-option
              v-if="availableTypes.includes(parseInt(value))"
              :key="value"
              :label="label"
              :value="parseInt(value)"
            ></el-option>
          </template>
        </el-select>
      </el-col>
      <el-col :span="5">
        <div class="category-blue-txt pB10">Controller</div>
        <el-select
          v-model="selectedControllerId"
          filterable
          @change="onControllerSelected()"
          placeholder="Select Controller"
          width="250px"
          class="fc-input-full-border-select2 flLeft"
        >
          <el-option
            v-for="(controller, index) in filteredControllers"
            :key="index"
            :label="controller.name"
            :value="controller.id"
            no-data-text="No controllers available"
          ></el-option>
        </el-select>
      </el-col>
      <slot></slot>
    </el-row>
  </div>
</template>
<script>
export default {
  props: ['loading'],
  data() {
    return {
      agents: [],
      selectedAgent: null,
      selectedType: null,
      controllers: [],
      selectedControllerId: null,
    }
  },
  computed: {
    availableTypes() {
      return this.controllers.map(controller => controller.controllerType)
    },
    filteredControllers() {
      return this.controllers.filter(
        controller => controller.controllerType === this.selectedType
      )
    },
    selectedController() {
      if (this.selectedControllerId) {
        return this.controllers.find(
          controller => controller.id === this.selectedControllerId
        )
      }
      return null
    },
  },
  mounted() {
    this.loadAgents()
  },
  methods: {
    loadAgents() {
      this.setLoading(true)
      this.$http
        .get('/v2/setup/agent/list')
        .then(response => {
          this.agents = response.data.result.agentDetails
            ? response.data.result.agentDetails
            : []
          if (this.agents.length) {
            this.selectedAgent =
              parseInt(this.$route.params.agentId) || this.agents[0].id
            this.loadControllers()
          } else {
            this.setLoading(false)
          }
        })
        .catch(() => {
          this.agents = []
          this.setLoading(false)
        })
    },
    loadControllers() {
      this.setLoading(true)
      this.selectedType = null
      this.selectedControllerId = null
      this.controllers = []
      this.$http
        .get(`/v2/setup/controllers?agentId=${this.selectedAgent}`)
        .then(response => {
          this.controllers = response.data.result.controllers
            ? response.data.result.controllers.filter(
                c => c.controllerType > -1
              )
            : []
          if (this.controllers.length) {
            this.selectedControllerId =
              parseInt(this.$route.params.controllerId) ||
              this.controllers[0].id
            this.selectedType = this.selectedController.controllerType
            this.onControllerSelected()
          } else {
            this.setLoading(false)
          }
        })
        .catch(_ => {
          this.controllers = []
          this.setLoading(false)
        })
    },
    onTypeChange() {
      this.selectedControllerId = this.filteredControllers[0].id
      this.onControllerSelected()
    },
    setLoading(isLoading) {
      this.$emit('update:loading', isLoading)
    },
    onControllerSelected() {
      this.$emit('onControllerSelected', this.selectedController)
    },
  },
}
</script>
