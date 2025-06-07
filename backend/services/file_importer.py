
from typing import Type, List, Dict, Any
from sqlmodel import SQLModel, Session, select
from sqlalchemy.exc import SQLAlchemyError
from model.file import File
from model.school import School
from model.local_education_agency import LEA
from core.utils import compute_file_hash
from fastapi import HTTPException

def import_file_to_db(
    *,
    file_bytes: bytes,
    model_class: Type[SQLModel],
    session: Session,
    delimiter: str = '\t',
    name: str,
    size: int
) -> Dict[str, Any]:
    """
    Reads file bytes, parses them using pandas, and inserts into DB using model_class.
    Returns a dict with row counts and error details.
    """
    file_hash=compute_file_hash(file_bytes)
    
    existing_file = session.exec(select(File).where(File.file_hash is not None and File.file_hash== file_hash)).first()
    
    if existing_file:
        return {
            "inserted": 0,
            "errors": [],
            "file_id": existing_file.id,
            "message": "Duplicate file upload skipped."
        }
        
    import pandas as pd
    import io
    decoded = file_bytes.decode('utf-8')
    df = pd.read_csv(io.StringIO(decoded), sep=delimiter)

    success_count = 0
    errors: List[Dict[str, Any]] = []
    
    model_type = ('Shool' if model_class == School else
                  'LEA' if model_class == LEA else 
                  'Unknown')
    
    try:
        file_instance=File(
            model_type=model_type,
            file_hash=file_hash, 
            name=name,
            size=size, 
            )
        session.add(file_instance)
        session.flush() #gets file_instance.id
    
        for index, row in df.iterrows():
            # convert row to a dict
            row_data = row.to_dict()
            # set the foreign key to establish the relationship 
            row_data['file_id'] = file_instance.id 
            # create the model instance 
            model_instance = model_class(**row_data)
            session.add(model_instance)
            success_count += 1
        
        session.commit()

    except Exception as e:
        session.rollback()
        raise HTTPException(status_code=400, detail={"inserted": success_count,
            "errors": errors,
            "message": str(e)})

    return {
        "inserted": success_count,
        "errors": errors,
        "file_id": file_instance.id,
        "message": f"Successfully inserted {success_count} records"
    }
