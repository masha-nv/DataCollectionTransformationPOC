import { Label, Radio, RadioOption } from '@elis/react-beacon';
import { ApplicantOrRepAddress } from './applicant-or-rep-address';
import { ConsulateOrEmbassyAddress } from './consulate-or-embassy-address';
import { IAddress } from '@uscis/elis/domain';
import { ContactDataType, I131RpRtdRdMailingAddressType } from '../i131-rp-rtd-render-decision';

export interface I131RpRtdRdMailingAddressProps {
  caseId: number;
  currentState: I131RpRtdRdMailingAddressType;
  onUpdate: (data: I131RpRtdRdMailingAddressType) => void;
  USAddress: ContactDataType;
  readOnly: boolean;
}

export type ContactBlockType =
Pick<IAddress, 'apt' | 'street' | 'street2' | 'state' | 'zipCode' | 'country' | 'city'>& {
  CO?: string,
  // might not need phone and email
  phone?: string,
  email?: string,
}


export const I131RpRtdRdMailingAddress = ({ USAddress, currentState, onUpdate, readOnly }: I131RpRtdRdMailingAddressProps) => {

  const handleAddressChange = (sendToUSAddress: boolean) => {
    const clone = {...currentState};
    if(currentState.contact){
      clone.contact = {...currentState.contact};
    }
    if (sendToUSAddress) {
      delete clone.consulate;
      clone.contact = USAddress;
    }
    const updated = {...clone, sendToUSAddress}
    onUpdate(updated);
  };

  return (
    <div>
      <Label required htmlFor="send-to-address-type">
        Where is the Travel Document being sent?
      </Label>
      {/* radio group */}
      <Radio id={'send-to-address-type'} disabled={readOnly} required name="travelDocumentSendToAddressType">
        <RadioOption
          key="us-address"
          id={'send-to-us-address'}
          checked={currentState.sendToUSAddress}
          onChange={() => handleAddressChange(true)}
        >
          U.S. Address
        </RadioOption>
        <RadioOption
          key="foreign-address"
          id={'send-to-foreign-address'}
          checked={!currentState.sendToUSAddress}
          onChange={() => handleAddressChange(false)}
        >
          Foreign Address
        </RadioOption>
      </Radio>
      <hr />
      {currentState.sendToUSAddress && <ApplicantOrRepAddress contactData={USAddress} />}
      {!currentState.sendToUSAddress && <ConsulateOrEmbassyAddress readOnly={readOnly}
        currentState={currentState}
        onUpdate={onUpdate} />}
    </div>
  );
};
