import { render, screen } from '@testing-library/vue'
import ThankYouScreen from 'src/moduleSurveys/pages/ThankYouScreen.vue'

const i18nKeys = {
  'survey.copyright': 'Copyright',
  'survey.close': 'Close',
  'survey.thankyou_message': 'Thank you for your response',
}

const setup = {
  mocks: {
    $t(key) {
      return i18nKeys[key]
    },
  },
  stubs: {
    InlineSvg: {
      template: '<svg></svg>',
    },
  },
}

describe('ThankYouScreen', () => {
  it('Should render thank you screen and inspect all the texts', async () => {
    render(ThankYouScreen, {
      ...setup,
      props: {
        companyLogoUrl: 'https://cdn.test.com/logo.svg',
      },
    })
    let containerElement = await screen.findByTestId('thankyouscreen')
    let closeButton = await screen.findByTestId('closebutton')
    let responseText = await screen.findByTestId('tq-text')
    let surveyFooter = await screen.findByTestId('survey-footer-section')
    expect(containerElement).toBeInTheDocument()
    expect(closeButton.textContent.trim()).toEqual('Close')
    expect(responseText.textContent.trim()).toEqual(
      'Thank you for your response'
    )
    expect(surveyFooter.textContent.trim()).toEqual('Copyright')
  })
})
