from fastapi import APIRouter, HTTPException, File, UploadFile, Depends, Query, Body
import io 
import pandas as pd
from sqlmodel import Session, select
from model.local_education_agency import LocalEducationAgency 
from database import engine, get_session
import csv
from pydantic import ValidationError
from typing import List, Dict, Any

router = APIRouter()

@router.get('/lea-data')
async def get_lea_data(session: Session = Depends(get_session),
    offset: int = Query(0, ge=0),
    limit: int = Query(10, ge=1, le=100)):
    statement = select(LocalEducationAgency).offset(offset).limit(limit)
    result = session.exec(statement)
    leas = result.all()
    return leas
    
    
    
@router.post("/upload-lea-data")
async def upload_file(
    file: UploadFile = File(...), 
    session: Session = Depends(get_session)
):
    if not file.filename.endswith('.tab'):
        raise HTTPException(status_code=400, details='Invalid file type')
    
    contents = await file.read() 
    decoded = contents.decode('utf-8')
    reader = csv.DictReader(io.StringIO(decoded), delimiter='\t')
    errors: List[Dict[str, Any]] = []
    
    inserted = 0
    updated = 0
    
    for line_num, row in enumerate(reader, start=2): #header = line 1
        try: 
            validated = LocalEducationAgency.model_validate(row)
            existing = session.get(LocalEducationAgency, validated.DistrictNCESID)
            if existing: 
                updated += 1
                for key, value in validated.model_dump().items():
                    setattr(existing, key, value)
            else:
                lea = LocalEducationAgency(**validated.model_dump()) 
                session.add(lea)
                inserted+=1
                
        except ValidationError as ve:
            errors.append({
                "line":line_num,
                "errors": ve.errors()
            })
            
        except Exception as e:
            errors.append({
                "line": line_num, 
                "errors": [str(e)]
            })
            
    session.commit()
    
    return {
        "inserted":inserted,
        "updated": updated,
        "invalid_rows": errors
    }
    
    
@router.post("/update-lea-record")
async def update_lea_record(
    data: dict = Body(...),
    session: Session = Depends(get_session)
): 
    district_id = data.get('DistrictNCESID')
    if not district_id: 
        raise HTTPException(status_code=400, detail='DistrictNCESID is required')
    
    lea = session.get(LocalEducationAgency, district_id)
    if not lea: 
        raise HTTPException(status_code=404, detail="Record not found") 
    
    for key, value in data.items():
        if hasattr(lea, key):
            setattr(lea, key, value)
            
    session.add(lea)
    session.commit()
    session.refresh(lea) 
    return lea


@router.delete('/lea-record/{district_id}')
async def delete_record(district_id: str, session: Session = Depends(get_session)):
    lea = session.get(LocalEducationAgency, district_id)
    if not lea:
        raise HTTPException(status_code=404, detail='Record not found')
    
    session.delete(lea)
    session.commit()
    return {"message": district_id}


@router.delete('/lea-records')
async def delete_records(
    district_ids: List[str] = Body(...),
    session: Session = Depends(get_session)
):
    # Query all records matching the provided IDs
    statement = select(LocalEducationAgency).where(LocalEducationAgency.DistrictNCESID.in_(district_ids))
    results = session.exec(statement).all()
    
    if not results:
        raise HTTPException(status_code=404, detail='No records found for the provided IDs')

    # Delete each record
    for lea in results:
        session.delete(lea)
        
    session.commit()
    return {"message": f"Deleted {len(results)} records", "deleted_ids": district_ids}