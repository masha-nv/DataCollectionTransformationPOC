from fastapi import APIRouter, HTTPException, File, UploadFile, Depends, Query, Body
from sqlmodel import Session, select, func
from model.local_education_agency import LEA 
from database import engine, get_session
from pydantic import ValidationError
from typing import List, Dict, Any
from core.constants import ALLOWED_FILE_EXTENSIONS
from services.file_importer import import_file_to_db
from services.auth.service import UserService, get_current_user

router = APIRouter()

@router.get('/lea')
async def get_lea_data(
    session: Session = Depends(get_session),
    current_user=Depends(get_current_user),
    offset: int = Query(0, ge=0),
    limit: int = Query(10, ge=1, le=100)):
    # total count of records 
    count_stmt = select(func.count()).select_from(LEA)
    total = session.exec(count_stmt).one()
    # paginated records
    statement = select(LEA).offset(offset).limit(limit)
    result = session.exec(statement)
    leas = result.all()
    return {"total": total, "items": leas}
    
    
    
@router.post("/lea")
async def upload_file(
    file: UploadFile = File(...), 
    session: Session = Depends(get_session),
    current_user=Depends(get_current_user)
):
    if not file.filename.endswith(ALLOWED_FILE_EXTENSIONS):
        raise HTTPException(status_code=400, details='Invalid file type')
    
    contents = await file.read() 
    delimiter = '\t' if file.filename.endswith('.tab') or file.filename.endswith('.tsv') else ','
   
    result = import_file_to_db(
        file_bytes=contents,
        model_class=LEA,
        session=session,
        delimiter=delimiter,
        name=file.filename,
        size=len(contents)
    )
    return result
    
    
    
@router.put("/lea")
async def update_lea_record(
    data: dict = Body(...),
    session: Session = Depends(get_session),
    current_user=Depends(get_current_user),
): 
    district_id = data.get('district_nces_id')
    if not district_id: 
        raise HTTPException(status_code=400, detail='district_nces_id is required')
    
    lea = session.get(LEA, district_id)
    if not lea: 
        raise HTTPException(status_code=404, detail="Record not found") 
    
    for key, value in data.items():
        if hasattr(lea, key):
            setattr(lea, key, value)
            
    session.add(lea)
    session.commit()
    session.refresh(lea) 
    return lea

@router.delete('/lea')
async def delete_records(
    district_ids: List[str] = Body(...),
    current_user=Depends(get_current_user),
    session: Session = Depends(get_session)
):
    # Query all records matching the provided IDs
    statement = select(LEA).where(LEA.district_nces_id.in_(district_ids))
    results = session.exec(statement).all()
    
    if not results:
        raise HTTPException(status_code=404, detail='No records found for the provided IDs')

    # Delete each record
    for lea in results:
        session.delete(lea)
        
    session.commit()
    return {"message": f"Deleted {len(results)} records", "deleted_ids": district_ids}


@router.get('/lea/{file_id}')
async def get_lea_for_file(
    file_id: str,
    current_user=Depends(get_current_user),
    session:Session=Depends(get_session)
):
    results = session.exec(select(LEA).where(LEA.file_id == file_id)).all() 
    return results