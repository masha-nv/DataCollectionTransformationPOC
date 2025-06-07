from controllers.local_education_agency import router as lea_router 
from controllers.school import router as school_router
from controllers.file import router as file_router
from controllers.auth import router as auth_router
from controllers.shared import router as shared_router

def include_routes(app):
    app.include_router(lea_router, prefix='/api', tags=['LEA'])
    app.include_router(school_router, prefix='/api', tags=['SCHOOL'])
    app.include_router(file_router, prefix='/api', tags=['FILE'])
    app.include_router(auth_router, prefix='/api', tags=['USER'])
    app.include_router(shared_router, prefix='/api', tags=['SHARED'])
    
    