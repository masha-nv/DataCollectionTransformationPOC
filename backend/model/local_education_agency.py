from sqlmodel import SQLModel, Field,  create_engine, Session, Relationship, select
from typing import Optional

# Items to consider:
    # add more robust validation, for example validate zip code (^[0-9]{5}(?:-[0-9]{4})?$), school year (yyyy-yyyy), phone number, etc
class LocalEducationAgency(SQLModel, table=True): 
    DistrictNCESID: str = Field(primary_key=True)
    FIPSStateCode: str
    LEANAME: str
    StateLEAIDNumber: str
    County: Optional[str]
    LOC_ADDRESS1: str
    LOC_CITY: str
    LOC_STATE: str
    LOC_ZIPCODE: str
    MAIL_ADDRESS1: str
    MAIL_CITY: str
    MAIL_STATE: str
    MAIL_ZIPCODE: str
    PhoneNumber: str
    SCHOOLYEAR: str
    OPERATIONSTATUS: str