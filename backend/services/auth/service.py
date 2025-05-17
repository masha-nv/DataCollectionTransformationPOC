from model.auth import User ,UserModelRequest
from sqlmodel import select, Session
from .utils import generate_password_hash
from fastapi.security import OAuth2PasswordBearer 
from jose import jwt, JWTError 
from fastapi import Depends

class UserService:
    oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/login")
    def get_user_by_email(self, email:str, session: Session):
        statement=select(User).where(User.email == email)
        result = session.exec(statement)
        user = result.first()
        return user
    
    def user_exists(self, email: str, session:Session):
        user = self.get_user_by_email(email, session)
        return True if user is not None else False
    
    def create_user(self, user_data: UserModelRequest, session: Session):
        user_data_dict = user_data.model_dump()
        print(user_data_dict) 
        new_user = User(**user_data_dict)
        new_user.password_hash = generate_password_hash(user_data_dict['password'])
        session.add(new_user)
        session.commit()
        return new_user
    
    def get_current_user(self, token: str=Depends(oauth2_scheme)):
        secret = os.getenv("JWT_SECRET")
        algorithm = os.getenv("JWR_ALGORYTHM")
        
        try: 
            payload = jwt.decode(token, secret, algorithms=[algorithm])
            user_id:str = payload.get('user_id')
            if user_id is None:
                raise HTTPException(status_code=401, detail="Invalid token payload")
            return payload 
        except JWTError:
            raise HTTPException(status_code=401, detail="Invald token")