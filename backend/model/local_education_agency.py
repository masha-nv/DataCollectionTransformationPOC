from sqlmodel import SQLModel, Field, Relationship
from typing import Optional
from model.file import File
import uuid 

class LEA(SQLModel, table=True):
    __tablename__ = "lea"
    id:str = Field(default_factory=lambda: str(uuid.uuid4()), primary_key=True)
    district_nces_id: str = Field(..., alias="DistrictNCESID", index=True, unique=False)
    fips_state_code: str = Field(..., alias="FIPSStateCode")
    lea_name: str = Field(..., alias="LEANAME")
    state_lea_id_number: str = Field(..., alias="StateLEAIDNumber")
    county: Optional[str] = Field(default=None, alias="County")
    loc_address1: Optional[str] = Field(default=None, alias="LOC_ADDRESS1")
    loc_city: Optional[str] = Field(default=None, alias="LOC_CITY")
    loc_state: Optional[str] = Field(default=None, alias="LOC_STATE")
    loc_zipcode: Optional[str] = Field(default=None, alias="LOC_ZIPCODE")
    mail_address1: Optional[str] = Field(default=None, alias="MAIL_ADDRESS1")
    mail_city: Optional[str] = Field(default=None, alias="MAIL_CITY")
    mail_state: Optional[str] = Field(default=None, alias="MAIL_STATE")
    mail_zipcode: Optional[str] = Field(default=None, alias="MAIL_ZIPCODE")
    phone_number: Optional[str] = Field(default=None, alias="PhoneNumber")
    school_year: Optional[str] = Field(default=None, alias="SCHOOLYEAR")
    operational_status: Optional[str] = Field(default=None, alias="OPERATIONSTATUS")
    file_id: Optional[str] = Field(default=None, foreign_key='file.id')
    file: Optional[File] = Relationship(back_populates='leas')