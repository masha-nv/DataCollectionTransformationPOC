from fastapi import APIRouter, File, UploadFile
import pandas as pd 
from uuid import uuid4

router = APIRouter() 


@router.post("/preview") 
async def preview_file(file: UploadFile = File(...)):
    print(file)
    df = pd.read_csv(file.file) 
    # store parsed full data temporarily 
    file_id = str(uuid4())
    return {"file_id": file_id,  
            "file_name": file.filename, 
            "size": file.size, 
            "preview_data": df.to_dict()}