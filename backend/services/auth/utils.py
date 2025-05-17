from passlib.context import CryptContext 
from datetime import timedelta, datetime
import jwt
import os
import uuid
import logging
from fastapi.security import OAuth2PasswordBearer 
from jose import jwt, JWTError 

password_ctx = CryptContext(schemes=['bcrypt'])

ACCESS_TOKEN_EXP = 3600

def generate_password_hash(password: str)->str: 
    hash = password_ctx.hash(password) 
    return hash


def verify_password(password:str, hash: str) -> bool:
    return password_ctx.verify(password, hash)


def create_access_token(user_data:dict, expiry:timedelta = None, refresh: bool = False):
    payload={
        **user_data,
        "exp": datetime.now() + (expiry if expiry is not None else timedelta(seconds=ACCESS_TOKEN_EXP)),
        "iat": datetime.now(),
        "jti": str(uuid.uuid4()),
        "refresh": refresh
    }
    secret = os.getenv("JWT_SECRET")
    algorithm = os.getenv("JWR_ALGORYTHM")
    
    if not secret:
        raise ValueError("JWT_SECRET environment variable is not set.")
    if not algorithm:
        raise ValueError("JWR_ALGORYTHM environment variable is not set.")
    
    token=jwt.encode(
        payload=payload,
        key=secret,
        algorithm=algorithm
    )
    
    return token


def decode_token(token:str) -> dict:
    secret = os.getenv("JWT_SECRET")
    algorithm = os.getenv("JWR_ALGORYTHM")
    try:
        token_data = jwt.decode(jwt=token, key=secret, algorythms=[algorithm])
        return token_data 
    except jwt.PyJWTError as e:
        logging.exception(e)
        return None
    

