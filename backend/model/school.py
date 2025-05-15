from sqlmodel import SQLModel, Field, Relationship
from typing import Optional, TYPE_CHECKING, Optional
import uuid 

if TYPE_CHECKING:
    from model.file import File


class School(SQLModel, table=True):
    __tablename__ = "school"
    id: str = Field(default_factory=lambda: str(uuid.uuid4()), primary_key=True)
    lea_ide: str = Field(..., alias="LEA Ide", index=True, unique=True)
    lea_identifier_state: str = Field(..., alias="LEA Identifier (Stat")
    fi: str = Field(..., alias="FI")
    education_entity_name: str = Field(..., alias="Education Entity Name")
    school_ident: str = Field(..., alias="School Ident")
    mailing_address1: Optional[str] = Field(default=None, alias="Mailing Address1")
    mailing_address2: Optional[str] = Field(default=None, alias="Mailing Address2")
    mailing_address3: Optional[str] = Field(default=None, alias="Mailing Address3")
    city: Optional[str] = Field(default=None, alias="City")
    state: Optional[str] = Field(default=None, alias="St")
    zip_code: Optional[str] = Field(default=None, alias="Zip")
    telephone_education: Optional[str] = Field(default=None, alias="Telephone Education ")
    school_year: Optional[str] = Field(default=None, alias="School Ye")
    school_operational_status: Optional[str] = Field(default=None, alias="School Operational S")
    file_id: Optional[str] = Field(default=None, foreign_key='file.id')
    file: Optional['File'] = Relationship(back_populates='schools')
    
