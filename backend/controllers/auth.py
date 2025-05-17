from fastapi import APIRouter, Depends, status
from model.auth import User, UserModelResponse, UserModelRequest, UserLoginModel
from services.auth.service import UserService
from database import get_session
from fastapi.exceptions import HTTPException
from sqlmodel import Session
from services.auth.utils import create_access_token, decode_token, verify_password
from datetime import timedelta
from fastapi.responses import JSONResponse

router = APIRouter() 
user_service = UserService()


@router.post('/signup', response_model=UserModelResponse, status_code=status.HTTP_201_CREATED)
async def create_user(user: UserModelRequest, session: Session=Depends(get_session)):
    email = user.email
    user_exists =  user_service.user_exists(email, session)
    
    if user_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail='User with email already exists')
    
    new_user = user_service.create_user(user, session)
    
    return new_user
    

@router.post('/login')
async def login_users(login_data: UserLoginModel, session:Session = Depends(get_session)):
    email = login_data.email
    password = login_data.password
    
    user =  user_service.get_user_by_email(email=email, session=session)
    if user is not None:
        password_valid = verify_password(password, user.password_hash)
        
        if password_valid:
            access_token = create_access_token(user_data={'email': user.email,
                                                          'user_id': str(user.id)})
            
            refresh_token=create_access_token(user_data={'email': user.email,
                                                          'user_id': str(user.id)},
                                              refresh=True, expiry=timedelta(days=2))
            
            return JSONResponse(
                content={
                    "message": "Loggin successfull",
                    "access_token": access_token,
                    "refresh_token": refresh_token,
                    "user": {
                        "email": user.email,
                        "id": str(user.id)
                    }
                }
            )
    raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="invalid email or password")