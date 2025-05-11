from sqlmodel import create_engine, SQLModel, Session

# SQLite database URL
sqlite_file_name = 'data_collection.db'
sqlite_url = f'sqlite:///{sqlite_file_name}'

# Create the database engine
engine = create_engine(sqlite_url, echo=True)

# Function to create tables
def create_db_and_tables():
    SQLModel.metadata.create_all(engine)
    
# FastAPI dependency 
def get_session():
    with Session(engine) as session:
        yield session