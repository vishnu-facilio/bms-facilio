<template>
  <div>
    <el-row v-for="(d, index) in data" :key="index" class="fc-st-row-border">
      <el-col :span="20">
        <slot :data="d" :index="index"></slot>
      </el-col>
      <el-col :span="4">
        <div class="addition-container pT20 pR10">
          <div
            @click="addData()"
            class="fc-st-add-label pointer"
            v-if="index === data.length - 1 && data.length <= limit"
          >
            <i class="el-icon-circle-plus-outline"></i>
          </div>
          <div
            @click="removeData(index)"
            v-if="data.length > 1"
            class="fc-st-remove-label pointer"
          >
            <i class="el-icon-remove-outline"></i>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { cloneDeep } from 'lodash'
export default {
  props: {
    value: {
      type: Array,
      required: true,
    },
    defaultdata: {
      type: Object,
      required: true,
    },
    limit: {
      type: Number,
      default: 5,
    },
  },
  data() {
    return {
      data: [],
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    sync() {
      this.$emit('update:value', this.data)
    },
    addData() {
      this.data.push(cloneDeep(this.defaultdata))
      this.sync()
    },
    removeData(index) {
      this.data.splice(index, 1)
      this.sync()
    },
    init() {
      this.data = this.value
    },
  },
}
</script>
<style>
.addition-container {
  font-size: 21px;
  display: flex;
  height: 100%;
  justify-content: space-evenly;
  align-items: center;
  height: 40px;
}
.fc-st-add-label {
  color: #31c583;
}
.fc-st-remove-label {
  color: #ec7c7c;
}
.fc-st-row-border {
  border: 1px solid #f1f1f1;
  padding: 0px 0px 5px 5px;
  border-radius: 4px;
}
</style>
