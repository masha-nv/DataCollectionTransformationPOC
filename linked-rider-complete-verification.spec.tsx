import { AngularJSScope, AngularJSScopeContext } from '@uscis/elis/angularjs-scope';
import { CASE_ACCESS_LEVELS, IUserSecurity, MY_CASES_URL, WorkTypes } from '@uscis/elis/domain';
import { cisCaseReducer, initSecurity, setCISCase, userSecurityReducer } from '@uscis/elis/state';
import { Provider } from 'react-redux';
import { LinkedRiderCompleteVerification } from './linked-rider-complete-verification';
import { MOCK_USER_SECURITY, server } from '@uscis/elis/testing';
import { render, screen, waitFor } from '@uscis/elis/testing/library';

import userEvent from '@testing-library/user-event';
import {
  GET_RENDER_DECISION_ECHO_TASK_NOTICES,
  GET_RENDER_DECISION_ECHO_TASK_SELECT_OPTIONS,
} from '@uscis/elis/render-decision';
import { MOCK_I829_CASE } from '../__test-data__/elis-linked-rider-verification.test-data';
import {
  COMPLETE_RIDER_VERIFICATION,
  COMPLETE_VALIDATION_FAILURE,
  ESCALATE_TO_ADJUDICATOR_FAILURE,
  ESCALATE_TO_ADJUDICATOR_SUCCESS,
  MOCK_I829_CASE_DETAILS_RESPONSE_WITH_RIDER_ERRORS,
  MOCK_I829_CASE_DETAILS_RESPONSE_WITH_SPOUSE,
  MOCK_I829_CASE_DETAILS_RESPONSE_WITH_SPOUSE_INCORRECT_ANUMBER_ERRORS,
  MOCK_I829_CASE_DETAILS_RESPONSE_WITH_SPOUSE_MISSING_DATA_ERRORS,
} from '../__test-data__/linked-rider-complete-verification.mocks';
import { configureStore } from '@reduxjs/toolkit';
import { ElisContainer } from '@uscis/elis/storybook';
import { MOCK_GET_ATTACH_DETACH_INFO, MOCK_GET_ATTACH_DETACH_INFO_WITH_ATTACHED_CHILD, MOCK_GET_ATTACH_DETACH_INFO_WITH_DETACHED_CHILD, MOCK_GET_ATTACH_DETACH_INFO_WITH_DETACHED_SPOUSE } from '../__test-data__/elis-linked-rider-verification.mocks';

const MOCK_STORE = configureStore({
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      immutableCheck: false,
      serializableCheck: false,
    }),
  reducer: {
    cisCase: cisCaseReducer.reducer,
    userSecurity: userSecurityReducer.reducer,
  },
});

const TestComponent = () => {
  const mockScope: AngularJSScope = {
    $root: null,
    $broadcast: (eventType, eventData) => null,
    $on: (_eventType, _callback) => () => null,
  };
  return (
    <AngularJSScopeContext.Provider value={mockScope}>
      <Provider store={MOCK_STORE}>
        <ElisContainer>
          <LinkedRiderCompleteVerification
            workType={WorkTypes.LINKED_CHILD_VERIFICATION}
            cisCase={MOCK_I829_CASE}
            id={'linked-rider'}
            receiptNumber={MOCK_I829_CASE.receiptNumber}
            userTaskId={123}
          />
        </ElisContainer>
      </Provider>
    </AngularJSScopeContext.Provider>
  );
};

const mockUserSecurityDetails: IUserSecurity = {
  ...MOCK_USER_SECURITY,
  ...MOCK_USER_SECURITY.permissions,
  userPermissions: {},
  usersRole: CASE_ACCESS_LEVELS.CASE_ACCESS_2,
};

const { location } = window;

describe('elisLinkedRiderVerificationCompleteNotice', () => {
  beforeEach(() => {
    MOCK_STORE.dispatch(setCISCase(MOCK_I829_CASE));
    MOCK_STORE.dispatch(
      initSecurity(
        mockUserSecurityDetails.details,
        mockUserSecurityDetails.supervisors,
        mockUserSecurityDetails.caseLifeCyclePermissions,
        mockUserSecurityDetails.skills,
        mockUserSecurityDetails
      )
    );
    server.use(
      MOCK_I829_CASE_DETAILS_RESPONSE_WITH_SPOUSE,
      GET_RENDER_DECISION_ECHO_TASK_SELECT_OPTIONS,
      GET_RENDER_DECISION_ECHO_TASK_NOTICES,
      COMPLETE_RIDER_VERIFICATION,
      ESCALATE_TO_ADJUDICATOR_SUCCESS,
      MOCK_GET_ATTACH_DETACH_INFO,
    );
    Reflect.deleteProperty(global.window, 'location');
    window.location = { ...location } as string & Location;
  });

  afterAll(() => {
    Reflect.deleteProperty(global.window, 'location');
    window.location = { ...location } as string & Location;
  });

  it('should render Complete Notice Options', async () => {
    render(<TestComponent />);
    const linkedRiders = await screen.findByElementId('linked-riders-list');
    expect(linkedRiders).toBeTruthy();
  });

  it('select Attach Correspondence and show upload options', async () => {
    render(<TestComponent />);
    userEvent.click(await screen.findByElementId('attach-correspondence-switch'));
    expect(await screen.findByText('Select a Correspondence Option:')).toBeTruthy();
    const radioOptions = await screen.findByElementId('linked-rider-upload-options-radio');
    expect(radioOptions).toBeTruthy();
  });

  it('should complete validation', async () => {
    render(<TestComponent />);
    userEvent.click(await screen.findByElementId('linked-rider-submit-button'));
    const confirmationButton = screen.getByElementId('linked-rider-complete-confirm');
    userEvent.click(confirmationButton);
    await waitFor(() => {
      expect(screen.queryByElementId('linked-rider-submit-button')).toBeFalsy();
    });
  });

  it('should complete validation, disabled for rider errors', async () => {
    render(<TestComponent />);
    server.use(MOCK_I829_CASE_DETAILS_RESPONSE_WITH_RIDER_ERRORS);
    const missingANumberErrorMessage = await screen.findByText(
      'All riders must have an A-Number before completing this task'
    );
    expect(missingANumberErrorMessage).toBeTruthy();
    const incorrectANumberErrorMessage = await screen.findByText(
      'Confirm all rider A-numbers are in correct format before completing task'
    );
    expect(incorrectANumberErrorMessage).toBeTruthy();
    const dobErrorMessage = await screen.findByText(
      'All riders must have a Date of Birth before completing this task'
    );
    expect(dobErrorMessage).toBeTruthy();
    userEvent.click(screen.getByElementId('linked-rider-submit-button'));
  });

  it('should complete validation, disabled for spouse missing data errors', async () => {
    render(<TestComponent />);
    server.use(MOCK_I829_CASE_DETAILS_RESPONSE_WITH_SPOUSE_MISSING_DATA_ERRORS);
    const missingANumberErrorMessage = await screen.findByText(
      'All riders must have an A-Number before completing this task'
    );
    expect(missingANumberErrorMessage).toBeTruthy();
    const dobErrorMessage = await screen.findByText(
      'All riders must have a Date of Birth before completing this task'
    );
    expect(dobErrorMessage).toBeTruthy();
    userEvent.click(screen.getByElementId('linked-rider-submit-button'));
  });

  it('should complete validation, disabled for spouse incorrect a number error', async () => {
    server.use(MOCK_I829_CASE_DETAILS_RESPONSE_WITH_SPOUSE_INCORRECT_ANUMBER_ERRORS);
    render(<TestComponent />);
    const incorrectANumberErrorMessage = await screen.findByText(
      'Confirm all rider A-numbers are in correct format before completing task'
    );
    expect(incorrectANumberErrorMessage).toBeTruthy();
    userEvent.click(screen.getByElementId('linked-rider-submit-button'));
    expect(await screen.findByText('Complete or Escalate Case')).toBeTruthy();
  });

  it('should complete validation, returns failure', async () => {
    server.use(COMPLETE_VALIDATION_FAILURE);
    render(<TestComponent />);
    userEvent.click(await screen.findByElementId('linked-rider-submit-button'));
    const confirmationButton = screen.getByElementId('linked-rider-complete-confirm');
    userEvent.click(confirmationButton);
    const errorMessage = await screen.findByText('Failed to complete verification.');
    expect(errorMessage).toBeTruthy();
  });

  it('should not escalate task when canceling in confirmation window', async () => {
    render(<TestComponent />);
    userEvent.click(await screen.findByElementId('escalate-btn'));
    const cancelButton = await screen.findByElementId('linked-rider-escalate-cancel');
    userEvent.click(cancelButton);
    const escalateButton = await screen.findByElementId('escalate-btn');
    expect(escalateButton).toBeTruthy();
  });

  it('should escalate task on confirmation', async () => {
    render(<TestComponent />);
    global.window = Object.create(window);
    Object.assign(window, {
      location: {} as Location,
    });
    global.window.location.href = 'http://localhost/InternalApp/app/';
    userEvent.click(await screen.findByElementId('escalate-btn'));
    const confirmationButton = await screen.findByElementId('linked-rider-escalate-confirm');
    userEvent.click(confirmationButton);
    const escalateButton = await screen.findByElementId('escalate-btn');
    expect(window.location.href).toEqual(MY_CASES_URL);
  });

  it('should escalate task on confirmation, returns failure to escalate', async () => {
    server.use(ESCALATE_TO_ADJUDICATOR_FAILURE);
    render(<TestComponent />);
    userEvent.click(await screen.findByElementId('escalate-btn'));
    const confirmationButton = screen.getByElementId('linked-rider-escalate-confirm');
    userEvent.click(confirmationButton);
    const errorMessage = await screen.findByText(`Failed to escalate to adjudicator.`);
    expect(errorMessage).toBeTruthy();
  });

  it('should complete task, returns failure to completion', async () => {
    server.use(ESCALATE_TO_ADJUDICATOR_FAILURE);
    render(<TestComponent />);
    userEvent.click(await screen.findByElementId('escalate-btn'));
    const confirmationButton = screen.getByElementId('linked-rider-escalate-confirm');
    userEvent.click(confirmationButton);
    const errorMessage = await screen.findByText(`Failed to escalate to adjudicator.`);
    expect(errorMessage).toBeTruthy();
  });

  it('should render Complete verification and list only co-applying riders', async () => {
    render(<TestComponent />);
    const linkedRiders = await screen.findByElementId('linked-riders-list');
    expect(linkedRiders).toBeTruthy();
    expect(await screen.findByText('MAR, ROBERTS NMN')).toBeTruthy();
    expect(await screen.findByText('LEE, ABBY NMN')).toBeTruthy();
    expect(screen.queryByText('LEE, JONES NMN')).toBeFalsy();
  });

  it('should render Complete verification and list co-applying and attached riders', async () => {
    server.use(MOCK_GET_ATTACH_DETACH_INFO_WITH_ATTACHED_CHILD);
    render(<TestComponent />);
    const linkedRiders = await screen.findByElementId('linked-riders-list');
    expect(linkedRiders).toBeTruthy();
    expect(await screen.findByText('MAR, ROBERTS NMN')).toBeTruthy();
    expect(await screen.findByText('LEE, ABBY NMN')).toBeTruthy();
    expect(await screen.findByText('LEE, JONES NMN')).toBeTruthy();
  });

  it('should render Complete verification and not list detached child', async () => {
    server.use(MOCK_GET_ATTACH_DETACH_INFO_WITH_DETACHED_CHILD);
    render(<TestComponent />);
    const linkedRiders = await screen.findByElementId('linked-riders-list');
    expect(linkedRiders).toBeTruthy();
    expect(await screen.findByText('MAR, ROBERTS NMN')).toBeTruthy();
    expect(screen.queryByText('LEE, ABBY NMN')).toBeFalsy();
    expect(screen.queryByText('LEE, JONES NMN')).toBeFalsy();
  });

  it('should render Complete verification and not list detached spouse', async () => {
    server.use(MOCK_GET_ATTACH_DETACH_INFO_WITH_DETACHED_SPOUSE);
    render(<TestComponent />);
    const linkedRiders = await screen.findByElementId('linked-riders-list');
    expect(linkedRiders).toBeTruthy();
    expect(screen.queryByText('MAR, ROBERTS NMN')).toBeFalsy();
    expect(await screen.findByText('LEE, ABBY NMN')).toBeTruthy();
    expect(screen.queryByText('LEE, JONES NMN')).toBeFalsy();
  });
});
