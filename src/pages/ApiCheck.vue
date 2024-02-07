<template>
  <div>
    <el-row>
      <el-col :span="12">
        <el-button @click="get()">get</el-button>
        <el-button @click="post()">post</el-button>
        <el-input placeholder="Please input" v-model="url"></el-input>
        <el-input
          type="textarea"
          :rows="2"
          autosize
          placeholder="Please input"
          v-model="params"
        ></el-input>
      </el-col>
      <el-col :span="12">
        <el-input
          type="textarea"
          :rows="2"
          autosize
          placeholder="Please input"
          v-model="result"
        ></el-input>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { API } from '@facilio/api'
export default {
  data() {
    return {
      url: '',
      params: ``,
      result: ``,
    }
  },
  methods: {
    get() {
      API.get(this.url).then(response => {
        this.result = JSON.stringify(response)
      })
    },
    post() {
      API.post(this.url, JSON.parse(this.params)).then(response => {
        this.result = JSON.stringify(response)
      })
    },
  },
}
</script>
