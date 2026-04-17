import { Card, CardVariant, Label, Select, SelectOption, SelectSize, TextInput } from '@elis/react-beacon';
import styles from '../i131-rp-rtd-rd.module.scss';
import {  ContactBlockType } from './i131-rp-rtd-rd-mailing-address';
import { I131RpRtdRdMailingAddressType } from '../i131-rp-rtd-render-decision';
import { useRefData } from '@uscis/elis/state';

type ModifiableContactBlockProps = {
  currentState: I131RpRtdRdMailingAddressType;
  onUpdate: (data: I131RpRtdRdMailingAddressType) => void;
  readOnly: boolean;
}

export const ModifiableContactBlock = ({currentState, onUpdate, readOnly}: ModifiableContactBlockProps) => {
  const refData = useRefData();
  const countries = refData?.countryCodes || [];


  function updateContact(value: string, name: keyof ContactBlockType) {
    const updated = {...currentState, contact: {...currentState.contact, [name]: value}}
    onUpdate(updated);
  }

  return (
    <div className="mt-3 mb-3">
      <Card id="address-block" variant={CardVariant.gray}>
        <div className="mb-3">
          <Label htmlFor="care-of">C/O</Label>
          <TextInput value={currentState.contact?.CO} disabled={readOnly}
          onChange={evt => updateContact(evt.target.value, 'CO')} id="care-of"></TextInput>
        </div>
        <div className="mb-3">
          <Label htmlFor="street-address-1">Street Address</Label>
          <TextInput value={currentState?.contact?.street} disabled={readOnly}
          onChange={evt => updateContact(evt.target.value, 'street')}  id="street-address-1"></TextInput>
        </div>
        <div className="mb-3">
          <Label htmlFor="street-address-2">Stress Address Line 2</Label>
          <TextInput id="street-address-2" value={currentState?.contact?.street2} disabled={readOnly}
          onChange={evt => updateContact(evt.target.value, 'street2')}></TextInput>
        </div>
        <div className="d-flex justify-content-between mb-3">
          <div>
            <Label htmlFor="city">City</Label>
            <TextInput id="city" value={currentState?.contact?.city} disabled={readOnly}
          onChange={evt => updateContact(evt.target.value, 'city')}></TextInput>
          </div>
          <div>
            <Label htmlFor="state-province-region">State / Province / Region</Label>
            <TextInput value={currentState?.contact?.state} disabled={readOnly}
          onChange={evt => updateContact(evt.target.value, 'state')} id="state-province-region"></TextInput>
          </div>
          <div>
            <Label htmlFor="zip-postal">Zip / Postal Code</Label>
            <TextInput value={currentState?.contact?.zipCode} disabled={readOnly}
          onChange={evt => updateContact(evt.target.value, 'zipCode')} id="zip-postal"></TextInput>
          </div>
        </div>
        <div className={`mb-3 ${styles['normalize-select-size']}`}>
          <Label htmlFor="country-selection" >Country</Label >
          <Select id="country-selection" disabled={readOnly}
          value={currentState?.contact?.country}
          onChange={evt => updateContact(evt.target.value, 'country')} size={SelectSize.fill}>
            {countries.map(country => <SelectOption key={country.countryCode} value={country.countryCode}>{country.countryName}</SelectOption>)}
          </Select>
        </div>
        <div className="mb-3">
          <Label htmlFor="phone">Phone</Label>
          <TextInput value={currentState?.contact?.phone} disabled={readOnly}
          onChange={evt => updateContact(evt.target.value, 'phone')}  id="phone"></TextInput>
        </div>
        <div className="mb-3">
          <Label htmlFor="email">Email</Label>
          <TextInput value={currentState?.contact?.email} disabled={readOnly}
          onChange={evt => updateContact(evt.target.value, 'email')}  id="email"></TextInput>
        </div>
      </Card>
    </div>
  );
};
