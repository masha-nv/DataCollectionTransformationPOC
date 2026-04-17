import { ICISCase } from '@uscis/elis/domain';
import { setCISCase } from '@uscis/elis/state';
import { ElisContainer } from '@uscis/elis/storybook';
import { CISCASE_FROM_STORE } from '@uscis/elis/testing';
import { render, screen } from '@uscis/elis/testing/library';
import { Provider } from 'react-redux';
import { TEST_STORE } from '../../mocks/related-cases.mocks';
import { CaseSummaryDataView, ICaseSummaryDataView } from './case-summary-data-view';

const TestComponent: React.FC<ICaseSummaryDataView> = (props) => {
  return (
    <Provider store={TEST_STORE}>
      <ElisContainer>
        <CaseSummaryDataView {...props} />
      </ElisContainer>
    </Provider>
  );
};

describe('CaseSummaryDataView Component', () => {
  beforeEach(() => {
    TEST_STORE.dispatch(setCISCase(CISCASE_FROM_STORE));
  });

  it('renders with empty strings when CIS case has missing data', async () => {
    const adjudicationCisCase: ICISCase = {
      ...CISCASE_FROM_STORE,
      firstName: null,
      lastName: null,
      alienRegistrationNumber: null,
      dob: null,
      beneficiaryPersonNameList: [],
      beneficiaryAlienNumberList: [],
    };
    render(<TestComponent adjudicationCisCase={adjudicationCisCase} />);

    const petitionerDataView = await screen.findByElementId('Petitioner-case-summary-data-view');
    expect(petitionerDataView).toBeTruthy();

    const beneficiaryDataView = screen.getByElementId('Beneficiary-case-summary-data-view');
    expect(beneficiaryDataView).toBeTruthy();
  });
});
