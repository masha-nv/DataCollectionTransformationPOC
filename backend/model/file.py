from sqlmodel import SQLModel, Field, Relationship
from typing import Optional, TYPE_CHECKING, List
import uuid
from datetime import datetime

if TYPE_CHECKING:
    from model.school import School
    from model.local_education_agency import LEA

class File(SQLModel, table=True):
    __tablename__ = 'file'
    id: str = Field(default_factory=lambda: str(uuid.uuid4()), primary_key=True)
    upload_datetime: datetime = Field(default_factory=datetime.utcnow)
    model_type: str = Field(..., description='The type of the associated mode (e.g., LEA, School)')
    schools: Optional[List['School']] = Relationship(back_populates='file')
    leas: Optional[List['LEA']] = Relationship(back_populates='file')
    file_hash: Optional[str] = Field(default=None,index=True) #validates that no two files contain identical information
    # this may be not needed, since we make sure on School and LEA that their unique ids must be unique
    # eg: lea_ide: str = Field(..., alias="LEA Ide", index=True, unique=True)