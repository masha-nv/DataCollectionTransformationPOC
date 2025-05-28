from fastapi import FastAPI
from contextlib import asynccontextmanager
from fastapi.middleware.cors import CORSMiddleware
from database import create_db_and_tables
from routes import include_routes 
from dotenv import load_dotenv 
load_dotenv()


@asynccontextmanager
async def lifespan(app: FastAPI):
    print('server is starting...')
    create_db_and_tables()
    yield
    

app = FastAPI(lifespan=lifespan, redirect_slashes=False)
include_routes(app)

app.add_middleware(
    CORSMiddleware, 
    allow_origins=['*'],
    allow_credentials=True,
    allow_methods=['*'],
    allow_headers=['*']
    )
    