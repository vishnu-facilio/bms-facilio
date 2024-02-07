import { render, screen } from '@testing-library/vue'
import ServiceRequestSummary from 'src/pages/servicerequest/ServiceRequestSummary.vue'

jest.mock('vue-router')
jest.mock('src/pages/workflow/router', () => {})
jest.mock('src/pages/base-module-v2/Overview', () => {})
jest.mock('src/pages/setup/survey/ViewAllSurveys', () => {})

const i18nKeys = {
  'survey.take_survey': 'Take Survey',
  'survey.take_other_surveys': 'Show my surveys',
}

const setup = {
  mocks: {
    $t(key) {
      return i18nKeys[key]
    },
    $account: {
      user: {
        peopleId: 13083,
      },
    },
    isLoading: false,
    $route: {
      params: {
        id: 1,
      },
    },
    moduleName: 'serviceRequest',
    showDeleteDialog: false,
    currentModuleState: true,
    POSITION: {
      SUMMARY: 1,
    },
    downloadUrl: '',
    isStateFlowEnabled: false,
    shouldHideApprovers: false,
    $hasPermission() {
      return true
    },
    surveyDetails: {},
  },
  stubs: {
    ViewAllSurveysDialog: {
      template: '<span>View all surveys dialog</span>',
    },
    CustomButton: {
      template: '<button>Custom button</button>',
    },
    page: {
      template: '<div>Page</div>',
    },
    InlineSvg: {
      template: '<svg>svg icon</svg>',
    },
    SurveyBar: {
      template: '<div>Survey bar</div>',
    },
  },
  computed: {
    customModuleData() {
      return {
        id: 1,
        mode: 'next',
      }
    },
    isSurveyEnabled() {
      return true
    },
    isApprovalEnabled() {
      return true
    },
    showViewAllSurvey() {
      return true
    },
  },
}

describe('ServiceRequestSummary', () => {
  afterAll(() => {
    jest.clearAllMocks()
  })

  it('Should render service request summary component', async () => {
    render(ServiceRequestSummary, {
      ...setup,
      props: {
        viewname: 'all',
      },
    })
    let surveyBarElement = await screen.findByTestId('service-request-summary')
    expect(surveyBarElement).toBeInTheDocument()
  })

  it('Check all the necessary components and portals have rendered on the screen', async () => {
    render(ServiceRequestSummary, {
      ...setup,
      props: {
        viewname: 'all',
      },
    })
    let customButtom = await screen.findByTestId('custom-button')
    let portalApprovalBar = await screen.findByTestId('pagebuilder-sticky-top')
    let summaryDropdown = await screen.findByTestId('summary-dropdown')
    expect(customButtom).toBeInTheDocument()
    expect(portalApprovalBar).toBeInTheDocument()
    expect(summaryDropdown.children.length).toEqual(2)
  })

  it('Summary dropdown options click actions', async () => {
    render(ServiceRequestSummary, {
      ...setup,
      props: {
        viewname: 'all',
      },
    })
    const firstOption = screen.getByText('Edit')
    expect(firstOption).toBeTruthy()
  })

  it('Should render footer elements on the screen', async () => {
    render(ServiceRequestSummary, {
      ...setup,
      props: {
        viewname: 'all',
      },
    })
    const setupDialog = screen.getByTestId('setup-dialog')
    expect(setupDialog).toBeInTheDocument()
    expect(screen.getByText('CANCEL')).toBeInTheDocument()
    expect(screen.getByText('DISSOCIATE')).toBeInTheDocument()
    expect(screen.getByText('MOVE TO RECYCLE BIN')).toBeInTheDocument()
  })
})
