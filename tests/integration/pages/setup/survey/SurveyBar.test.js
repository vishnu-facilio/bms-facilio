import { render, screen, fireEvent } from '@testing-library/vue'
import SurveyBar from 'src/pages/setup/survey/SurveyBar.vue'
import * as FacilioRouter from '@facilio/router'
import { fetchSurvey } from 'fixtures/survey'
import Factory from 'utils/factory'

jest.mock('src/router', () => {
  return {
    resolve: () => {
      return Promise.resolve('Redirected to route')
    },
    push: () => {
      return Promise.resolve('Pushed to route')
    },
  }
})

const i18nKeys = {
  'survey.take_survey': 'Take Survey',
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
  },
}

describe('SurveyBar', () => {
  beforeEach(() => {
    jest
      .spyOn(FacilioRouter, 'getApp')
      .mockImplementation(() => ({ linkName: 'test' }))
    jest.spyOn(window, 'open').mockImplementation(() => jest.fn('Open new tab'))
  })

  afterEach(() => {
    jest.clearAllMocks()
  })

  it('Should render workorder survey bar component', async () => {
    render(SurveyBar, {
      ...setup,
      props: {
        moduleId: '7129163',
        moduleName: 'workorder',
        ...Factory.create(fetchSurvey(), {
          surveyDetails: {
            response: [{ expiryDate: 4102425000000, name: 'Next Survey' }],
          },
        }),
      },
    })
    let surveyBarElement = await screen.findByTestId('survey-bar-container')
    expect(surveyBarElement).toBeInTheDocument()
    let surveyBarHelper = await screen.findByTestId('survey-bar-helper')
    expect(surveyBarHelper).toBeInTheDocument()
    expect(surveyBarHelper.textContent.trim()).toBe('Next Survey')
  })

  it('Take survey link for workorder should open page in new tab', async () => {
    render(SurveyBar, {
      ...setup,
      props: {
        moduleId: '7129163',
        moduleName: 'workorder',
        ...Factory.create(fetchSurvey(), {
          surveyDetails: { response: [{ expiryDate: 4102425000000 }] },
        }),
      },
    })
    let takeSurveyLink = await screen.findByTestId('take-survey-btn')
    expect(takeSurveyLink).toBeInTheDocument()
    expect(takeSurveyLink.textContent.trim()).toEqual('Take Survey')
    await fireEvent.click(takeSurveyLink)
    expect(window.open).toHaveBeenCalledTimes(1)
  })

  it('Should render service request survey bar component', async () => {
    render(SurveyBar, {
      ...setup,
      props: {
        moduleId: '3429165',
        moduleName: 'serviceRequest',
        ...Factory.create(fetchSurvey(), {
          surveyDetails: { response: [{ expiryDate: 4102425000000 }] },
        }),
      },
    })
    let surveyBarElement = await screen.findByTestId('survey-bar-container')
    expect(surveyBarElement).toBeInTheDocument()
  })

  it('Take survey link for service request should open page in new tab', async () => {
    render(SurveyBar, {
      ...setup,
      props: {
        moduleId: '3429165',
        moduleName: 'serviceRequest',
        ...Factory.create(fetchSurvey(), {
          surveyDetails: { response: [{ expiryDate: 4102425000000 }] },
        }),
      },
    })
    let takeSurveyLink = await screen.findByTestId('take-survey-btn')
    expect(takeSurveyLink).toBeInTheDocument()
    expect(takeSurveyLink.textContent.trim()).toEqual('Take Survey')
    await fireEvent.click(takeSurveyLink)
    expect(window.open).toHaveBeenCalledTimes(1)
  })

  it('Survey bar should not be shown if survey is already completed or partially answered by user', async () => {
    render(SurveyBar, {
      ...setup,
      props: {
        moduleId: '3429165',
        moduleName: 'serviceRequest',
        ...Factory.create(fetchSurvey(), {
          surveyDetails: {
            response: [{ responseStatus: 4, expiryDate: 4102425000000 }],
          },
        }),
      },
    })
    let surveyBarElement = screen.queryByTestId('survey-bar-container')
    expect(surveyBarElement).not.toBeInTheDocument()
  })
})
