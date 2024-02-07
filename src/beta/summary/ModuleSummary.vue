<script>
import {
  FContainer,
  FButton,
  FPageHeader,
  FSpinner,
  FDivider,
} from '@facilio/design-system'
import CustomModuleSummary from 'src/pages/custom-module/CustomModuleSummary.vue'
import StateflowButton from './buttons/StateflowButton.vue'
import CustomButton from './buttons/CustomButton.vue'
import Page from './PageBuilder.vue'
import { isEmpty, isFunction } from '@facilio/utils/validation'

export default {
  extends: CustomModuleSummary,
  name: 'ModuleSummary',
  components: {
    FContainer,
    FButton,
    FPageHeader,
    FSpinner,
    StateflowButton,
    FDivider,
    CustomButton,
    Page,
  },
  data() {
    return {
      hasTransitionBtns: false,
      hasCustomButtons: false,
      notesModuleName: 'cmdnotes',
      attachmentsModuleName: 'cmdattachments',
    }
  },
  computed: {
    tagProps() {
      let { record } = this || {}
      if (record.isStateFlowEnabled() && record.currentModuleState()) {
        return {
          text: record.currentModuleState(),
          variant: 'simple',
          statusType: 'information',
        }
      } else {
        return {}
      }
    },
  },
  methods: {
    buttons() {
      let { record, POSITION } = this || {}
      return (
        <FContainer display="flex" alignItems="center">
          {!isEmpty(record) && (
            <CustomButton
              record={this.record}
              moduleName={this.moduleName}
              position={POSITION.SUMMARY}
              vOn:refresh={this.refreshData}
              vOn:onError={() => {}}
              vOn:hasCustomButtons={val => (this.hasCustomButtons = val)}
            />
          )}
          {this.hasCustomButtons && (
            <FDivider height="14px" margin="containerNone containerLarge" />
          )}
          {record.isStateFlowEnabled() && (
            <StateflowButton
              key={`${record.id}transitions`}
              moduleName={this.moduleName}
              record={record}
              disabled={record.isApprovalEnabled()}
              buttonClass="asset-el-btn"
              vOn:currentState={() => {}}
              vOn:transitionSuccess={this.refreshData}
              vOn:transitionFailure={() => {}}
              vOn:hasTransitionBtns={val => (this.hasTransitionBtns = val)}
            />
          )}
          {this.hasTransitionBtns && (
            <FDivider height="14px" margin="containerNone containerLarge" />
          )}
          {this.systemButtons()}
        </FContainer>
      )
    },
    systemButtons() {
      return (
        <FButton
          appearance="tertiary"
          iconGroup="default"
          iconName="edit"
          iconButton={true}
          vOn:click={this.editRecord}
        />
      )
    },
    slot(type) {
      let { $scopedSlots } = this
      let { [type]: component } = $scopedSlots || {}
      let validSlots = ['buttons', 'tags']

      if (isFunction(component) && validSlots.includes(type)) {
        return component && component()
      } else {
        return null
      }
    },
  },
  render() {
    let { record, mainFieldKey } = this || {}
    return (
      <FContainer height="100%">
        {this.isLoading ? (
          <FContainer
            height="100%"
            display="flex"
            alignItems="center"
            justifyContent="center"
          >
            <FSpinner size={30} />
          </FContainer>
        ) : (
          <FContainer class="page-summary-container-ms">
            <FContainer
              class="page-header-ms"
              padding="containerNone containerXLarge"
            >
              <FPageHeader
                heading={record[mainFieldKey]}
                breadCrumbProps={{ appearance: 'back' }}
                tagProps={this.tagProps}
                captionText={record ? `#${record.id}` : ''}
                withTabs={true}
                vOn:path={this.back}
                {...{
                  scopedSlots: {
                    tags: () => {
                      return this.slot('tags')
                    },
                    buttons: () => {
                      return this.slot('buttons') || this.buttons()
                    },
                  },
                }}
              ></FPageHeader>
            </FContainer>
            <FContainer
              class="page-builder-ms"
              padding="containerNone containerMedium"
            >
              <Page
                key={record.id}
                module={this.moduleName}
                id={record.id}
                details={record}
                notesModuleName={this.notesModuleName}
                isV3Api={true}
                attachmentsModuleName={this.attachmentsModuleName}
              ></Page>
            </FContainer>
          </FContainer>
        )}
      </FContainer>
    )
  },
}
</script>
<style scoped lang="scss">
.page-summary-container-ms {
  display: flex;
  flex-direction: column;
  height: 100%;
  .page-header-ms {
    flex-shrink: 0;
  }
  .page-builder-ms {
    flex-grow: 1;
    overflow: hidden;
  }
}
</style>
