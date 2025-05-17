from sqlmodel import SQLModel, Field, Column
import uuid
import sqlalchemy.dialects.sqlite as sl
from datetime import datetime
from pydantic import BaseModel

    
class User(SQLModel, table=True):
    __tablename__ = 'users'
    id: str = Field(default_factory=lambda: str(uuid.uuid4()), primary_key=True)
    username: str = Field(max_length=8)
    email: str = Field(max_length=40)
    first_name: str
    last_name: str
    created_at: datetime = Field(default_factory=lambda:datetime.utcnow(), nullable=False)
    password_hash:str = Field(exclude=True)
    def __repr__(self):
        return f"<User {self.username}>"
    
    
class UserModelResponse(BaseModel):
    username: str = Field(max_length=8)
    email: str = Field(max_length=40)
    first_name: str
    last_name: str
    
class UserModelRequest(BaseModel):
    username: str = Field(max_length=8)
    email: str = Field(max_length=40)
    first_name: str
    last_name: str
    password: str
    
    
class UserLoginModel(BaseModel):
    email: str = Field(max_length=40)
    password: str