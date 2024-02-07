<template>
  <div class="fp-leagend-container">
    <!-- <div class="fp-leagend-header">
      <el-select
        @change="handleLeagnd()"
        v-model="leagend.active"
        placeholder="Select"
        class="fc-input-select fc-input-full-border2 width100"
      >
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        >
        </el-option>
      </el-select>
    </div> -->
    <div class="fp-leagend-body">
      <div
        class="row p10 pointer fp-leagend-row"
        v-for="(leagend, index) in leagends"
        :key="index"
        @mouseover="action(leagend)"
        @mouseout="action('CLEAR')"
        @click="setActive(leagend)"
        v-bind:class="{ inactive: !leagend.active, active: leagend.active }"
      >
        <div class="col-2">
          <div
            class="fc-leagend-icon"
            :style="{ background: leagend.color }"
          ></div>
        </div>
        <div class="col-10">{{ leagend.name }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['floorPlan', 'mappedSpaceId', 'controlCategoryList', 'leagend'],
  data() {
    return {
      spaceControllableCategoriesMap: null,
      controlTypes: [],
      leagends: [],
      options: [
        {
          value: 'CONTROL',
          label: 'Control Types',
        },
        {
          value: 'SPACE',
          label: 'Space Types',
        },
        {
          value: 'NONE',
          label: 'None',
        },
      ],
      value: '',
    }
  },
  watch: {
    leagend: {
      handler: function(newVal, oldVal) {
        this.handleLeagnd()
      },
      deep: true,
    },
  },
  methods: {
    action(data) {
      this.$emit('action', data)
    },
    type() {
      this.$emit('type', this.value)
    },
    setActive(data) {
      data.active = !data.active
      this.$emit('active', data)
    },
    getColor() {
      return (
        '#' +
        Math.random()
          .toString(16)
          .substr(2, 6)
      )
    },
    setLeagend() {
      this.leagends = []
      this.controlTypes.forEach(rt => {
        let leagend = {
          name: rt,
          color: this.getColor(),
          active: true,
          type: 'CONTROL',
        }
        this.leagends.push(leagend)
      })
    },
    handleLeagnd() {
      this.leagends = []
      if (this.leagend.active === 'CONTROL') {
        this.getFloorControlCategory()
      }
      this.type()
    },
    getFloorControlCategory() {
      let { id } = this.floorPlan
      let params = {
        floorId: id,
        spaceIncludeIds: this.mappedSpaceId,
      }
      return this.$http
        .post(`/v2/controlAction/getControllableCategories`, params)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.spaceControllableCategoriesMap =
              data.result.spaceControllableCategoriesMap
            this.findControlTypes()
          }
        })
    },
    findControlTypes() {
      this.controlTypes = []
      let self = this
      let name = null
      Object.values(this.spaceControllableCategoriesMap).forEach(rt => {
        Object.values(rt).forEach(rx => {
          name = this.controlCategoryList.find(
            rl => rl.categoryId === rx.controlType
          )._name
          if (self.controlTypes.indexOf(name) === -1) {
            self.controlTypes.push(name)
          }
        })
      })
      this.setLeagend()
    },
  },
}
</script>

<style>
.fp-leagend-header .fc-input-full-border2 .el-input__inner {
  width: 230px;
}
</style>
