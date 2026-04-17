import { Label, Select, SelectOption, SelectSize } from '@elis/react-beacon';
import { ModifiableContactBlock } from './modifiable-contact-block';
import styles from '../i131-rp-rtd-rd.module.scss';
import { useRefData } from '@uscis/elis/state';
import { I131RpRtdRdMailingAddressType } from '../i131-rp-rtd-render-decision';

export interface ConsulateOrEmbassyAddressProps {
  currentState: I131RpRtdRdMailingAddressType;
  onUpdate: (data: I131RpRtdRdMailingAddressType) => void;
  readOnly: boolean;
}

export const ConsulateOrEmbassyAddress = ({currentState, onUpdate, readOnly}: ConsulateOrEmbassyAddressProps) => {
  const refData = useRefData();

  const consulates = refData?.consulate || [];

  function handleConsulateUpdate(value: string) {
    onUpdate({...currentState, consulate: value})
  }

  return (
    <div className={`${styles['normalize-select-size']}`}>
      <div className="mb-4">
        <Label required htmlFor="consulate-office-selection">
          Select Consulate Office
        </Label>
        <Select value={currentState?.consulate} disabled={readOnly}
        id={'consulate-office-selection'} size={SelectSize.fill}
        onChange={evt => handleConsulateUpdate(evt.target.value)}>
          {consulates.map((c) => (
            <SelectOption key={c.consulateCode} value={c.consulateCode}>
              {c.consulateDesc}
            </SelectOption>
          ))}
        </Select>
      </div>
      <Label required>Travel Document Mailing Address</Label>
      <ModifiableContactBlock readOnly={readOnly}
        currentState={currentState}
        onUpdate={onUpdate} />
    </div>
  );
};
