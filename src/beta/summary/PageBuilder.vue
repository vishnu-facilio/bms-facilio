<template lang="">
  <div ref="page-builder-wrapper" class="pb-container">
    <div v-if="loading" class="fc-empty-center height100vh">
      <FSpinner :size="40" />
    </div>

    <FTabs
      v-else
      v-model="currentTab"
      :tabsList="tabs"
      :hideBorder="true"
      backgroundColor="backgroundMidgroundSubtle"
      padding="containerMedium containerNone"
      margin="containerNone containerNone containerNone containerLarge"
      @change="pushCurrentTab"
    >
      <FTabPane
        ref="tab-comp"
        style="margin-top:8px"
        v-for="tab in tabs"
        :key="`${tab.id} ${tab.displayName}`"
        :activeKey="tab.name"
      >
        <FContainer
          v-if="loadingPage"
          height="100%"
          display="flex"
          alignItems="center"
          justifyContent="center"
        >
          <FSpinner :size="20" />
        </FContainer>
        <div
          style="height:100%;overflow:scroll"
          v-else-if="currentTab === tab.name"
        >
          <Page
            :grid="tab"
            :ref="`page-builder-${tab.name}`"
            class="page-builder-wrapping"
            :key="`${tab.id}_${tab.displayName}`"
            :cellHeight="cellHeight"
          >
            <template #sectionHeader="{ section }">
              <FContainer
                v-if="
                  !isEmpty(section.displayName) || !isEmpty(section.description)
                "
                padding="containerLarge containerXLarge"
                display="flex"
                flexDirection="column"
              >
                <FText
                  v-if="!isEmpty(section.displayName)"
                  appearance="headingMed20"
                >
                  {{ section.displayName }}
                </FText>
                <FText
                  v-if="!isEmpty(section.description)"
                  appearance="bodyReg14"
                  color="textCaption"
                >
                  {{ section.description }}
                </FText>
              </FContainer>
            </template>
            <template v-slot="{ widget }">
              <NewWidgetSupplier
                :widget="widget"
                :groupKey="widget.key"
                v-bind="$attrs"
                :details="details"
                :id="id"
                :module="module"
                :record="details"
                :reloadSummary="reloadSummary"
                :resizeWidgetOnPage="resizeWidgetOnPage"
                :cellHeight="cellHeight"
                :cellWidth="cellWidth"
                :moduleName="module"
                class="page-widget-container"
                @fitToViewArea="fitToViewArea"
              />
            </template>
          </Page>
        </div>
      </FTabPane>
    </FTabs>
  </div>
</template>
<script>
import NewPageBuilder from '../../newapp/components/pagebuilder/NewPageBuilder.vue'
import { Page } from '@facilio/ui/app'
import {
  FTabs,
  FTabPane,
  FText,
  FContainer,
  FSpinner,
} from '@facilio/design-system'
import NewWidgetSupplier from 'src/beta/summary/widget/WidgetSupplier.vue'
export default {
  extends: NewPageBuilder,
  name: 'PageBuilder',
  components: {
    FTabs,
    FTabPane,
    FText,
    FContainer,
    NewWidgetSupplier,
    Page,
    FSpinner,
  },
  methods: {
    deserializeTabs(rawTabs) {
      let tabs = rawTabs.map((tab, tabIndex) => {
        // remove this handling once text captial is fixed in server
        let { displayName } = tab || {}
        displayName = displayName
          .split(' ')
          .map(txt => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase())
          .join(' ')
        let columns = (tab.columns ?? []).map(
          (column, columnIndex, columns) => {
            let { width: columnWidth } = column

            if (columnIndex > 0) {
              const previousColumn = columns[columnIndex - 1]
              let { x, width } = previousColumn
              column.x = x + width
            } else {
              column.x = 0
            }
            return {
              ...column,
              sections: (column?.sections ?? []).map(section => {
                return {
                  ...section,
                  widgets: this.getSerializedWidgets(
                    section?.widgets,
                    columnWidth
                  ),
                }
              }),
            }
          }
        )
        return {
          ...tab,
          id: tab.id,
          key: `t${tabIndex}`,
          columns,
          value: tab.name,
          label: displayName,
        }
      })
      return tabs
    },
  },
}
</script>

<style lang="scss">
.pb-container {
  height: 100%;
  background-color: transparent;

  .npb-section-title {
    font-weight: normal;
    line-height: 18px;
    letter-spacing: 0.7px;
    font-size: 18px;
    color: #000;
  }

  .el-tabs__nav-wrap {
    padding-left: 20px;
  }

  .npb-section-desc {
    font-size: 13px;
    color: gray;
    line-height: 20px;
    letter-spacing: 0.3px;
    margin-top: 5px;
  }
}

.page-builder-wrapping {
  overflow: scroll;
  height: 100%;
  // height: calc(100vh - 170px);

  & > .grid-stack-item {
    & > .grid-stack-item-content {
      .grid-stack-item-content {
        .grid-stack-item-content {
          background: #fff;
        }
      }
    }
  }
}
</style>
