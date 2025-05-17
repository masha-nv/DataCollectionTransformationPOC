from fastapi import APIRouter, HTTPException, Depends
from sqlmodel import Session, select 
from model.file import File 
from database import engine, get_session 
from services.auth.service import UserService 

router = APIRouter() 
user_service = UserService()


@router.get('/files')
async def get_files(session: Session = Depends(get_session), 
                    current_user=Depends(user_service.get_current_user)):
    statement = select(File)
    result = session.exec(statement)
    files = result.all() 
    return files
    
    
