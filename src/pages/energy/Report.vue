<template>
  <div class="db-content">
    <div class="row">
      <!-- <div class="col-3 tree-left">
        <div class="fc-widget tree-left">
          <el-tree :data="data" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
        </div>
      </div> -->
      <!-- <div class="col-9 tree-left">
        <div class="fc-widget">
          <el-button @click="resizeLayout">Resize</el-button>
          <el-button @click="addData">Change Data</el-button>
          <pie :width="defaultLayout.width" :height="defaultLayout.height" :data="defaultData" :options="{type: 'doughnut'}"></pie>
        </div>
      </div>
    </div> -->
      <div class="col-6 engery-contanier-second-row">
        <div class="fc-widget fc-widget-400 en-border">
          <div class="row p10 fc-widget-header">
            <div class="col-6 pull-left">
              <div class="f18 fc-widget-label">Energy Consumption Ranking</div>
              <div>
                <el-button @click="resizeLayout">Resize</el-button>
                <el-button @click="addData">Change Data</el-button>
              </div>
            </div>
            <div class="col-6 text-right">
              <div class="pull-right">
                <div class="row fc-widget-option energy-title-wiget-filter">
                  <!-- <div class="filterButton" @click="energyConsumptionRanking.period='year'" v-bind:class="{active: energyConsumptionRanking.period === 'year'}">This Year</div>
                <div class="filterButton" @click="energyConsumptionRanking.period='lastYear'" v-bind:class="{active: energyConsumptionRanking.period === 'lastYear'}">Last Year </div> -->
                </div>
              </div>
            </div>
          </div>
          <div class="en-divider"></div>
          <pie
            :width="defaultLayout.width"
            :height="defaultLayout.height"
            :data="defaultData"
            :options="{ type: 'doughnut' }"
          ></pie>
        </div>
      </div>
    </div>
    <div class="row">
      <div class="col"></div>
    </div>
  </div>
</template>
<script>
import Pie from 'charts/components/Pie'
export default {
  mounted() {
    this.sliders()
  },
  components: {
    Pie,
  },
  data() {
    return {
      defaultLayout: {
        width: 300,
        height: 300,
      },
      defaultData: [
        {
          value: 67,
          label: 'Life Safety',
        },
        {
          value: 54,
          label: 'Critical',
        },
        {
          value: 23,
          label: 'Maintenance is the best solution',
        },
        {
          value: 11,
          label: 'Lifes',
        },
        {
          value: 20,
          label: 'LifeK',
        },
        {
          value: 31,
          label: 'LifeCri',
        },
        {
          value: 43,
          label: 'LifeNew',
        },
      ],
      data: [
        {
          label: 'Energy Consumpation',
          children: [
            {
              label: 'Energy Break Down',
            },
            { label: 'Service Usage' },
          ],
        },
        {
          label: 'Energy Cost',
          children: [
            {
              label: 'Company Usage Cost',
            },
            { label: 'Service Usage Cost ' },
          ],
        },
        {
          label: 'Comparsion',
          children: [
            {
              label: 'Building',
            },
            { label: 'Period' },
            {
              label: 'Space /Floor',
            },
          ],
        },
        {
          label: 'Trends',
          children: [
            {
              label: 'Service',
            },
            { label: 'Floor' },
          ],
        },
      ],
      defaultProps: {
        children: 'children',
        label: 'label',
      },
    }
  },
  methods: {
    resizeLayout() {
      this.defaultLayout.width = 600
      this.defaultLayout.height = 600
    },
    addData() {
      this.defaultData.push({
        label: 'New Data',
        value: 0,
      })
    },
    handleNodeClick(data) {
      console.log(data)
    },
    sliders() {
      '.carousel[data-type="multi"] .item'.each(function() {
        let next = this.next()
        if (!next.length) {
          next = this.siblings(':first')
        }
        next
          .children(':first-child')
          .clone()
          .appendTo(this)

        for (let i = 0; i < 2; i++) {
          next = next.next()
          if (!next.length) {
            next = this.siblings(':first')
          }

          next
            .children(':first-child')
            .clone()
            .appendTo(this)
        }
      })
    },
  },
}
</script>
<style></style>
