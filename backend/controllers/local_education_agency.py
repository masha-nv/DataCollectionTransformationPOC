from fastapi import APIRouter, HTTPException, File, UploadFile, Depends, Query
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
    
    
    