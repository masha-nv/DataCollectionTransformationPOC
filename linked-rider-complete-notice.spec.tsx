import { AngularJSScope, AngularJSScopeContext } from '@uscis/elis/angularjs-scope';
import { setCISCase, store } from '@uscis/elis/state';
import { Provider } from 'react-redux';

import { server } from '@uscis/elis/testing';
import { render, screen } from '@uscis/elis/testing/library';
import { LinkedRiderCompleteNotice } from './linked-rider-complete-notice';
import {
  GET_RENDER_DECISION_ECHO_TASK_SELECT_OPTIONS,
  POST_RENDER_DECISION_ECHO_CREATE_NOTICE,
  POST_RENDER_DECISION_ECHO_FILE_UPLOAD,
  GET_RENDER_DECISION_ECHO_TASK_NOTICES,
  GET_RENDER_DECISION_ECHO_FILE_UPLOAD_NOTICES,
} from '@uscis/elis/render-decision';
import userEvent from '@testing-library/user-event';
import { WorkTypes } from '@uscis/elis/domain';
import { MOCK_I829_CASE } from '../__test-data__/elis-linked-rider-verification.test-data';
import { MOCK_I829_CASE_DETAILS_RESPONSE_WITH_SPOUSE } from '../__test-data__/linked-rider-complete-verification.mocks';

const TestComponent = () => {
  const mockScope: AngularJSScope = {
    $root: null,
    $broadcast: (eventType, eventData) => null,
    $on: (_eventType, _callback) => () => null,
  };
  return (
    <AngularJSScopeContext.Provider value={mockScope}>
      <Provider store={store}>
        <LinkedRiderCompleteNotice
          workTypeCode={WorkTypes.LINKED_CHILD_VERIFICATION}
          cisCase={MOCK_I829_CASE}
          id={'linked-rider'}
          onChange={() => null}
        ></LinkedRiderCompleteNotice>
      </Provider>
    </AngularJSScopeContext.Provider>
  );
};

describe('elisLinkedRiderVerificationCompleteNotice', () => {
  beforeEach(() => {
    store.dispatch(setCISCase(MOCK_I829_CASE));
    server.use(
      GET_RENDER_DECISION_ECHO_TASK_SELECT_OPTIONS,
      POST_RENDER_DECISION_ECHO_CREATE_NOTICE,
      POST_RENDER_DECISION_ECHO_FILE_UPLOAD,
      GET_RENDER_DECISION_ECHO_TASK_NOTICES,
      MOCK_I829_CASE_DETAILS_RESPONSE_WITH_SPOUSE,
      GET_RENDER_DECISION_ECHO_FILE_UPLOAD_NOTICES
    );
  });

  it('should render Complete Notice Options', async () => {
    render(<TestComponent />);
    const radioOptions = await screen.findByElementId('linked-rider-radio');
    expect(radioOptions).toBeTruthy();
  });

  it('select and display echo file upload correspondence', async () => {
    render(<TestComponent />);
    userEvent.click(await screen.findByElementId('linked-rider-manual-option'));
    expect(await screen.findByText('Upload Correspondence')).toBeTruthy();
    const uploadNotices = await screen.findByElementId('linked-rider-echo-upload-notices');
    expect(uploadNotices).toBeTruthy();
  });

  it('select and display echo task correspondence notices', async () => {
    render(<TestComponent />);
    userEvent.click(await screen.findByElementId('linked-rider-echo-option'));
    expect(await screen.findByText('Created Correspondence')).toBeTruthy();
    const taskNotices = await screen.findByElementId('linked-rider-echo-task-decision-notices');
    expect(taskNotices).toBeTruthy();
  });
});
