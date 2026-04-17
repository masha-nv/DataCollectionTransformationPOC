import { ISelectOption, Label, Select, SelectOption, SelectSize, TextInput } from '@elis/react-beacon';
import styles from '../i131-rp-rtd-rd.module.scss';

export const ADMITTANCE_TYPES = {
  PERMANENT_RESIDENT: 'Permanent Resident',
  REFUGEE: 'Refugee',
  ASYLEE: 'Asylee',
};

export interface I131RpRtdRdAdmittanceProps {
  onUpdate: (value: string) => void;
  currentState: string;
  readOnly: boolean;
}

export const I131RpRtdRdAdmittance = ({ onUpdate, currentState, readOnly }: I131RpRtdRdAdmittanceProps) => {

  function onAdmittanceUpdate(data: ISelectOption) {
    onUpdate(data.value as string);
  }

  return (
    <div className="d-flex">
      <div className={`mr-3 ${styles['normalize-select-size']}`}>
        <Label required htmlFor="admittance-selection">
          Admittance
        </Label>
        <Select
          id="admittance-selection"
          disabled={currentState === ADMITTANCE_TYPES.PERMANENT_RESIDENT || readOnly}
          value={currentState}
          size={SelectSize.small}
          onChange={(e, v) => onAdmittanceUpdate(v)}
        >
          {currentState === ADMITTANCE_TYPES.PERMANENT_RESIDENT ? (
            <SelectOption value={ADMITTANCE_TYPES.PERMANENT_RESIDENT}>Permanent Resident</SelectOption>
          ) : (
            <></>
          )}
          <SelectOption value={ADMITTANCE_TYPES.REFUGEE}>Refugee</SelectOption>
          <SelectOption value={ADMITTANCE_TYPES.ASYLEE}>Asylee</SelectOption>
        </Select>
      </div>
      <div className="mr-3">
        <Label htmlFor="restrictions">Restrictions</Label>
        <TextInput id="restrictions" disabled value={'NONE'} />
      </div>
      <div>
        <Label htmlFor="entries">Entries</Label>
        <TextInput id="entries" disabled value={'M'} />
      </div>
    </div>
  );
};
