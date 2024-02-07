<template>
  <div v-if="!$validation.isEmpty(widgets)">
    <div v-for="(widget, index) in widgets" :key="index">
      <div class="white-bg-block mT20">
        <RelatedListWidget
          :staticWidgetHeight="staticWidgetHeight"
          :details="details"
          :widget="widget"
          moduleName="workorder"
        ></RelatedListWidget>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import RelatedListWidget from '@/page/widget/common/RelatedListWidget'

export default {
  props: ['id'],
  components: {
    RelatedListWidget,
  },
  created() {
    this.fetchRelatedWidgetDetails()
  },
  data() {
    return {
      staticWidgetHeight: '342',
      details: null,
      widgets: null,
    }
  },
  methods: {
    fetchRelatedWidgetDetails() {
      let { id } = this
      let url = `/v2/pages/workorder`
      let params = {
        id,
      }

      API.get(url, params).then(({ data, error }) => {
        if (error) {
          this.$message.error(error || 'Error Occured')
        } else {
          if (!isEmpty(data)) {
            let { page, record } = data
            this.details = record || {}
            let filteredWidget = []
            let { tabs } = page || {}
            if (!isEmpty(tabs)) {
              const relatedRecordsTab = tabs.filter(
                t => t.name === 'related records'
              )
              let [{ sections }] =
                relatedRecordsTab.length > 0 ? relatedRecordsTab : [{}]
              if (!isEmpty(sections)) {
                let widgets = sections[0].widgets || []
                if (!isEmpty(widgets)) {
                  let sysWidget = []
                  let cusWidget = []
                  for (let i = 0; i < widgets.length; i++) {
                    if (widgets[i].widgetTypeEnum === 'RELATED_LIST') {
                      let { module } = widgets[i].relatedList || {}
                      let { field } = widgets[i].relatedList || {}
                      if (
                        !isEmpty(module) &&
                        !['workorderCost', 'workpermit'].includes(
                          module.name
                        ) &&
                        !['parentWO'].includes(field.name)
                      ) {
                        if (module.custom) {
                          cusWidget.push(widgets[i])
                        } else {
                          sysWidget.push(widgets[i])
                        }
                      }
                    }
                  }
                  filteredWidget = sysWidget.concat(cusWidget)
                }
              }
              this.widgets = filteredWidget
            }
          }
        }
      })
    },
  },
}
</script>
