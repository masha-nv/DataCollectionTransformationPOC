from controllers.local_education_agency import router as lea_router 

def include_routes(app):
    app.include_router(lea_router, prefix='/api', tags=['LocalEducationAgency'])