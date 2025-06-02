from fastapi import APIRouter, HTTPException, Depends
from sqlmodel import Session, select, func
from model.file import File 
from database import engine, get_session 
from services.auth.service import UserService , get_current_user

router = APIRouter() 


@router.get('/files')
async def get_files(session: Session = Depends(get_session), 
                    current_user=Depends(get_current_user)):
    # count all files
    count_stm = select(func.count()).select_from(File)
    count = session.exec(count_stm).one()
    statement = select(File)
    result = session.exec(statement)
    files = result.all() 
    return {"items": files, "total": count}
    
    
