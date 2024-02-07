<template>
  <div>
    <form v-on:submit.prevent="sendMapping">
      <div
        class="row oddEven"
        v-for="(field, index) in importProcessContext.columnHeadings"
        :key="index"
      >
        <div class="Cell col-md-5">
          <span>{{ field }}</span>
        </div>
        <div class="Cell col-md-2">
          <span> : </span>
        </div>
        <div class="Cell col-md-5">
          <!-- <select v-model="importProcessContext.fieldMapping[key]">
                              <option v-for="field in importProcessContext.columnHeadings">{{field}}</option>
                          </select> -->
          <el-select
            v-model="mappedValues[field]"
            filterable
            clearable
            @change="test"
          >
            <el-option
              v-for="(value, index2) in importProcessContext.fields"
              :label="
                importProcessContext.facilioFieldMapping[value]
                  ? importProcessContext.facilioFieldMapping[value].displayName
                  : value
              "
              :value="value"
              :key="index2"
            ></el-option>
          </el-select>
        </div>
      </div>
      <div class="col-lg-6 col-md-6 fc-form-footer">
        <q-btn loader color="primary">Submit </q-btn>
      </div>
    </form>
  </div>
</template>
<script>
import { QBtn } from 'quasar'

export default {
  props: ['importProcessContext', 'asset'],
  data() {
    return {
      show: true,
      mappedValues: {},
    }
  },
  components: {
    QBtn,
  },

  created() {
    if (this.importProcessContext.fieldMapping) {
      for (let key in this.importProcessContext.fieldMapping) {
        if (this.importProcessContext.fieldMapping[key]) {
          this.$set(
            this.mappedValues,
            this.importProcessContext.fieldMapping[key],
            key
          )
        }
      }
    }
  },

  methods: {
    sendMapping() {
      this.importProcessContext.fieldMapping = {}
      for (let key in this.mappedValues) {
        if (this.mappedValues[key]) {
          this.importProcessContext.fieldMapping[this.mappedValues[key]] = key
        }
      }
      this.importProcessContext.fieldMappingString = JSON.stringify(
        this.importProcessContext.fieldMapping
      )

      let self = this
      if (this.asset === '') {
        this.asset = null
      }
      this.$http
        .post('/import/processImport', {
          importProcessContext: this.importProcessContext,
          assetId: this.asset,
        })
        .then(function(response) {
          self.$emit('mappingResponse', response.data)
        })
        .catch(function(error) {
          const alert2 = Alert.create({
            html: ' Data import failed! [ ' + error + ']',
            color: 'negative',
            position: 'top-center',
          })
          setTimeout(function() {
            alert2.dismiss()
          }, 1500)
        })
    },
    test() {
      this.$forceUpdate()
    },
  },
}
</script>
<style type="text/css">
.oddEven:nth-child(odd) {
  background: rgb(248, 250, 250);
}
</style>
