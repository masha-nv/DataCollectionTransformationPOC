import { Label } from '@elis/react-beacon';
import { ContactBlock } from './contact-block';
import { ContactDataType } from '../i131-rp-rtd-render-decision';

export interface ApplicantOrRepAddressProps {
  contactData: ContactDataType;
}

export const ApplicantOrRepAddress = ({ contactData }: ApplicantOrRepAddressProps) => {
  return (
    <div>
      <Label required>Travel Document Mailing Address</Label>
      {contactData && <ContactBlock contactData={contactData} />}
    </div>
  );
};
