<script>
import {
  FContainer,
  FButton,
  FPageHeader,
  FSpinner,
  FDivider,
  FButtonGroup,
} from '@facilio/design-system'
import ModuleSummary from 'src/beta/summary/ModuleSummary.vue'
import StateflowButton from 'src/beta/summary/buttons/StateflowButton.vue'
import CustomButton from 'src/beta/summary/buttons/CustomButton.vue'
import Page from 'src/beta/summary/PageBuilder.vue'
import { isEmpty } from '@facilio/utils/validation'
import WOButton from 'src/beta/summary/widgets/WorkorderWidget/WorkorderButton.vue'

export default {
  extends: ModuleSummary,
  components: {
    FContainer,
    FButton,
    FPageHeader,
    FSpinner,
    StateflowButton,
    FDivider,
    CustomButton,
    Page,
    FButtonGroup,
    WOButton,
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
          {this.sysButtons()}
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
    sysButtons() {
      return <WOButton moreOptions="true" />
    },
  },
  render() {
    let { record } = this || {}
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
                heading={record.subject}
                breadCrumbProps={{ appearance: 'back' }}
                tagProps={this.tagProps}
                captionText={record ? `#${record.id}` : ''}
                withTabs={true}
                vOn:path={this.back}
                {...{
                  scopedSlots: {
                    buttons: () => {
                      return this.buttons()
                    },
                  },
                }}
              />
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
