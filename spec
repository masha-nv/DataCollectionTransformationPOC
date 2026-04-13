import React from 'react';
import { fireEvent, render, screen, waitFor } from '@uscis/elis/testing/library';
import { CaseFilingType } from './case-filing-type';
import { useCISCase } from '@uscis/elis/state';
import { useAxios } from '@uscis/elis/configuration';
import { CASE_FILING_TYPE_CODES } from '@uscis/elis/domain';

jest.mock('@uscis/elis/state', () => ({
  useCISCase: jest.fn(),
}));

jest.mock('@uscis/elis/configuration', () => ({
  useAxios: jest.fn(),
}));

jest.mock('@elis/react-beacon', () => {
  const React = require('react');

  return {
    Alert: ({ children }: { children: React.ReactNode }) => <div>{children}</div>,
    AlertVariant: {
      success: 'success',
      error: 'error',
    },
    Button: ({
      id,
      children,
      onClick,
      disabled,
    }: {
      id?: string;
      children: React.ReactNode;
      onClick?: () => void;
      disabled?: boolean;
    }) => (
      <button id={id} onClick={onClick} disabled={disabled}>
        {children}
      </button>
    ),
    ButtonType: {
      primary: 'primary',
      secondary: 'secondary',
      tertiary: 'tertiary',
    },
    DataView: ({
      title,
      toolbar,
      data,
      fields,
    }: {
      title: string;
      toolbar?: React.ReactNode;
      data: Record<string, string>;
      fields: Array<{ label: string; field: string }>;
    }) => (
      <div>
        <div>{title}</div>
        {toolbar}
        {fields.map((field) => (
          <div key={field.field}>
            <span>{field.label}</span>
            <span>{data[field.field]}</span>
          </div>
        ))}
      </div>
    ),
    DataViewLabelColumnWidth: {
      medium: 'medium',
    },
    Label: ({
      htmlFor,
      children,
    }: {
      htmlFor?: string;
      children: React.ReactNode;
    }) => <label htmlFor={htmlFor}>{children}</label>,
    Modal: ({
      title,
      children,
      footer,
    }: {
      title: string;
      children: React.ReactNode;
      footer?: React.ReactNode;
    }) => (
      <div>
        <div>{title}</div>
        {children}
        {footer}
      </div>
    ),
    Select: ({
      id,
      label,
      value,
      onChange,
      children,
      disabled,
    }: {
      id?: string;
      label?: string;
      value?: string;
      onChange?: (evt: React.ChangeEvent<HTMLSelectElement>) => void;
      children: React.ReactNode;
      disabled?: boolean;
    }) => (
      <div>
        {label && <label htmlFor={id}>{label}</label>}
        <select id={id} value={value} onChange={onChange} disabled={disabled}>
          {children}
        </select>
      </div>
    ),
    SelectOption: ({
      value,
      children,
    }: {
      value: string;
      children: React.ReactNode;
    }) => <option value={value}>{children}</option>,
    SelectSize: {
      fill: 'fill',
    },
    SelectType: {
      primary: 'primary',
    },
    TextArea: ({
      id,
      value,
      onChange,
    }: {
      id?: string;
      value?: string;
      onChange?: (evt: React.ChangeEvent<HTMLTextAreaElement>) => void;
    }) => <textarea id={id} value={value} onChange={onChange} />,
  };
});

describe('CaseFilingType component', () => {
  const mockGetHistory = jest.fn();
  const mockUpdateFilingType = jest.fn();
  const mockSaveComments = jest.fn();
  const mockOnCaseFilingTypeUpdate = jest.fn();
  const mockReload = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();

    (useCISCase as jest.Mock).mockReturnValue({
      caseId: 123456,
      caseFilingTypeCode: CASE_FILING_TYPE_CODES.I131RTD_LPR_US,
      caseFilingType: {
        description: 'Refugee Travel Document - LPR In US',
      },
    });

    Object.defineProperty(window, 'location', {
      writable: true,
      value: {
        ...window.location,
        reload: mockReload,
      },
    });

    (useAxios as jest.Mock)
      // history
      .mockReturnValueOnce([
        {
          data: [],
          error: null,
          loading: false,
        },
        mockGetHistory,
      ])
      // update filing type
      .mockReturnValueOnce([
        {
          error: null,
          loading: false,
        },
        mockUpdateFilingType,
      ])
      // save comments
      .mockReturnValueOnce([
        {
          error: null,
          loading: false,
        },
        mockSaveComments,
      ]);
  });

  test('renders current filing category', () => {
    render(<CaseFilingType onCaseFilingTypeUpdate={mockOnCaseFilingTypeUpdate} />);

    expect(screen.getByText('Filing Category')).toBeTruthy();
    expect(screen.getByText('Refugee Travel Document - LPR In US')).toBeTruthy();
    expect(screen.getByText('Edit')).toBeTruthy();
  });

  test('renders mount error alert when history request fails', () => {
    (useAxios as jest.Mock)
      .mockReturnValueOnce([
        {
          data: undefined,
          error: new Error('history failed'),
          loading: false,
        },
        mockGetHistory,
      ])
      .mockReturnValueOnce([
        {
          error: null,
          loading: false,
        },
        mockUpdateFilingType,
      ])
      .mockReturnValueOnce([
        {
          error: null,
          loading: false,
        },
        mockSaveComments,
      ]);

    render(<CaseFilingType onCaseFilingTypeUpdate={mockOnCaseFilingTypeUpdate} />);

    expect(screen.getByText('Unable to load filing category history.')).toBeTruthy();
  });

  test('opens modal when Edit is clicked', () => {
    render(<CaseFilingType onCaseFilingTypeUpdate={mockOnCaseFilingTypeUpdate} />);

    fireEvent.click(screen.getByText('Edit'));

    expect(
      screen.getByText('Update I-131 Refugee Travel Document Filing Category')
    ).toBeTruthy();
  });

  test('shows comments field only after selecting a category', () => {
    render(<CaseFilingType onCaseFilingTypeUpdate={mockOnCaseFilingTypeUpdate} />);

    fireEvent.click(screen.getByText('Edit'));

    expect(screen.queryByText('Comments')).toBeNull();

    fireEvent.change(screen.getByLabelText('Filing Category'), {
      target: {
        value: String(CASE_FILING_TYPE_CODES.I131RTD_Non_LPR_US),
      },
    });

    expect(screen.getByText('Comments')).toBeTruthy();
  });

  test('Confirm Changes is disabled until category selected and comments entered', () => {
    render(<CaseFilingType onCaseFilingTypeUpdate={mockOnCaseFilingTypeUpdate} />);

    fireEvent.click(screen.getByText('Edit'));

    const confirmButton = screen.getByText('Confirm Changes') as HTMLButtonElement;
    expect(confirmButton.disabled).toBe(true);

    fireEvent.change(screen.getByLabelText('Filing Category'), {
      target: {
        value: String(CASE_FILING_TYPE_CODES.I131RTD_Non_LPR_US),
      },
    });

    expect(confirmButton.disabled).toBe(true);

    fireEvent.change(screen.getByLabelText('Comments'), {
      target: { value: 'Updating filing category' },
    });

    expect(confirmButton.disabled).toBe(false);
  });

  test('saves updates, saves comments, refreshes history, calls callback, and hard refreshes page', async () => {
    mockUpdateFilingType.mockResolvedValue({});
    mockSaveComments.mockResolvedValue({});
    mockGetHistory.mockResolvedValue({});

    render(<CaseFilingType onCaseFilingTypeUpdate={mockOnCaseFilingTypeUpdate} />);

    fireEvent.click(screen.getByText('Edit'));

    fireEvent.change(screen.getByLabelText('Filing Category'), {
      target: {
        value: String(CASE_FILING_TYPE_CODES.I131RTD_Non_LPR_US),
      },
    });

    fireEvent.change(screen.getByLabelText('Comments'), {
      target: { value: 'Updating filing category' },
    });

    fireEvent.click(screen.getByText('Confirm Changes'));

    await waitFor(() => {
      expect(mockUpdateFilingType).toHaveBeenCalledWith({
        data: {
          caseFilingTypeCode: String(CASE_FILING_TYPE_CODES.I131RTD_Non_LPR_US),
        },
      });
    });

    expect(mockSaveComments).toHaveBeenCalledWith({
      data: {
        caseNoteText: 'Updating filing category',
        caseIdentifier: 123456,
      },
    });

    expect(mockGetHistory).toHaveBeenCalled();
    expect(mockOnCaseFilingTypeUpdate).toHaveBeenCalledWith(
      String(CASE_FILING_TYPE_CODES.I131RTD_Non_LPR_US)
    );
    expect(mockReload).toHaveBeenCalled();
  });

  test('shows error banner when update fails', async () => {
    mockUpdateFilingType.mockRejectedValue(new Error('save failed'));

    render(<CaseFilingType onCaseFilingTypeUpdate={mockOnCaseFilingTypeUpdate} />);

    fireEvent.click(screen.getByText('Edit'));

    fireEvent.change(screen.getByLabelText('Filing Category'), {
      target: {
        value: String(CASE_FILING_TYPE_CODES.I131RTD_Non_LPR_US),
      },
    });

    fireEvent.change(screen.getByLabelText('Comments'), {
      target: { value: 'Updating filing category' },
    });

    fireEvent.click(screen.getByText('Confirm Changes'));

    expect(await screen.findByText('Unable to update filing category. Please try again.')).toBeTruthy();
    expect(mockReload).not.toHaveBeenCalled();
  });
});
