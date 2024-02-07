<template>
  <div>
    <div class="weightage-sub-header mT20">
      <div>
        <div class="weightage-sub-header-text">{{ title }}</div>
        <div class="weightage-sub-header-descrip">
          {{
            $t('common._common.here_are_the_list_of_title__and_its_weightage', {
              title,
            })
          }}
        </div>
      </div>

      <div
        v-if="!$validation.isEmpty(recordList)"
        @click="addNew"
        class="d-flex pointer"
      >
        <inline-svg src="svgs/plus-button" class="fill-green"></inline-svg>
        <div class="add-weightage-sub-score">Add {{ recordTitle }}</div>
      </div>
    </div>

    <div v-if="$validation.isEmpty(recordList)" class="empty-state">
      <inline-svg
        src="svgs/emptystate/readings-empty"
        iconClass="icon text-center icon-60"
        class="margin-auto"
      >
      </inline-svg>
      <div class="margin-auto primary-font">
        {{ $t('common.dashboard.there_is_no_title_available', { title }) }}
      </div>
      <el-button @click="addNew" class="weightage-add-btn">
        <span class="weightage-add-btn-text"
          >{{ $t('common._common.add') }} {{ recordTitle }}</span
        >
      </el-button>
    </div>

    <el-table
      v-else
      :data="recordList"
      :summary-method="getTotalWeightage"
      show-summary
      border
      class="mB25"
    >
      <el-table-column :label="`${recordTitle} Name`">
        <template v-slot="data">
          <div @click="edit(data.row)">
            {{ $getProperty(data.row, tableNameCol, '') }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        :label="$t('common.products.weightage')"
        prop="weightage"
        width="200px"
        align="right"
      ></el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  props: ['title', 'recordTitle', 'tableNameCol', 'score', 'scoreType'],

  computed: {
    recordList() {
      let { score, scoreType } = this
      let { baseScoringContexts } = score || {}
      return (baseScoringContexts || []).filter(d => d.type === scoreType)
    },
  },

  methods: {
    getTotalWeightage(params) {
      let { columns, data } = params || {}
      let totalWeightage = []

      columns.forEach((column, index) => {
        if (index === 0) {
          totalWeightage[index] = `Total ${this.recordTitle} Weightage`
        } else {
          let values = data.map(item => Number(item[column.property]))

          if (!values.every(value => Number.isNaN(value))) {
            totalWeightage[index] =
              values.reduce((prev, curr) => {
                return prev + curr
              }, 0) + '%'
          } else {
            totalWeightage[index] = ''
          }
        }
      })

      return totalWeightage
    },
    addNew() {
      this.$emit('add')
    },
    edit(data) {
      this.$emit('edit', data)
    },
  },
}
</script>
