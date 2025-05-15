from fastapi import APIRouter, HTTPException, Depends
from sqlmodel import Session, select 
from model.file import File 
from database import engine, get_session 

router = APIRouter() 

@router.get('/files')
async def get_files(session: Session = Depends(get_session)):
    statement = select(File)
    result = session.exec(statement)
    files = result.all() 
    return files
    
    
