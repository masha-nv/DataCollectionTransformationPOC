from fastapi import APIRouter, HTTPException, File, UploadFile, Depends, Query, Body 
from sqlmodel import Session, select, func
from model.school import School 
from database import engine, get_session 
import csv 
import io
from core.constants import ALLOWED_FILE_EXTENSIONS
import pandas as pd
from services.file_importer import import_file_to_db
from services.auth.service import UserService, get_current_user

router = APIRouter() 

@router.get('/school')
async def get_school_data(session:Session = Depends(get_session),
                          current_user=Depends(get_current_user),
                          offset:int = Query(0, ge=0),
                          limit: int = Query(10, ge=1, le=100)):
    # count
    count_statement =select(func.count()).select_from(School)
    count = session.exec(count_statement).one()
    statement = select(School).offset(offset).limit(limit)
    result = session.exec(statement)
    schools = result.all() 
    return {"total": count, "items": schools}


@router.post('/school')
async def upload_file(
    file: UploadFile = File(...),
    current_user=Depends(get_current_user),
    session:Session = Depends(get_session)):
    if not file.filename.endswith(ALLOWED_FILE_EXTENSIONS):
        raise HTTPException(status_code=400, details='Invalid file type')
    
    contents = await file.read() 
    delimiter = '\t' if file.filename.endswith('.tab') or file.filename.endswith('.tsv') else ','
    
    result = import_file_to_db(
        file_bytes=contents,
        model_class=School,
        session=session,
        delimiter=delimiter
    )
    return result
    

@router.get('/school/{file_id}')
async def get_schools_for_file(
    file_id: str,
    current_user=Depends(get_current_user),
    session:Session=Depends(get_session)
):
    results = session.exec(select(School).where(School.file_id == file_id)).all() 
    return results
    
    
    
    