from controllers.local_education_agency import router as lea_router 
from controllers.school import router as school_router
from controllers.file import router as file_router

def include_routes(app):
    app.include_router(lea_router, prefix='/api', tags=['LEA'])
    app.include_router(school_router, prefix='/api', tags=['SCHOOL'])
    app.include_router(file_router, prefix='/api', tags=['FILE'])
    
    