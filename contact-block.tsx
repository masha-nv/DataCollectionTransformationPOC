import { ContactDataType } from '../i131-rp-rtd-render-decision';

export interface ContactAddress {
  streetAddress1Text: string;
  streetAddress2Text: string;
  postalCode: string;
  postalZipCode: string;
  stateCode: string;
  cityName: string;
  countryCode: string;
  suiteApartmentNumber: string;
}
export interface ContactBlockProps {
  contactData: ContactDataType;
}

export const ContactBlock = ( {contactData: {email, mailingAddress, name, phone}}: ContactBlockProps) => {
  return (
    <div className="mb-3">
      {name && <p className="mb-0">{name}</p>}

      <p className="mb-0">{mailingAddress?.streetAddress1Text}</p>
      {mailingAddress?.streetAddress2Text && <p className="mb-0">{mailingAddress.streetAddress2Text}</p>}
      {mailingAddress.suiteApartmentNumber && <p className="mb-0">{mailingAddress.suiteApartmentNumber}</p>}
      <p className="mb-0">
        {mailingAddress.cityName && <span>{mailingAddress.cityName}</span>}&nbsp;
        {mailingAddress.stateCode && <span>{mailingAddress.stateCode}</span>}
        &nbsp;{mailingAddress.postalZipCode && <span>{mailingAddress.postalZipCode}</span>}
      </p>
      {mailingAddress.countryCode && <p className="mb-0">{mailingAddress.countryCode}</p>}
      {phone && <p className="mb-0">{phone}</p>}
      {email && <p className="mb-0">{email}</p>}
    </div>
  );
};
