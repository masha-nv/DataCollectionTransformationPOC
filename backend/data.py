import pandas as pd 
from faker import Faker
import random 
import uuid

fake = Faker() 
Faker.seed(0)

def generate_fake_lea(index):
    return {
        "DistrictNCESID": str(1809000046 + index),
        "FIPSStateCode": str(random.randint(10, 99)),
        "LEANAME": fake.company() + " School",
        "StateLEAIDNumber": str(random.randint(9000, 9999)),
        "County": fake.city(),
        "LOC_ADDRESS1": fake.street_address(),
        "LOC_CITY": fake.city(),
        "LOC_STATE": fake.state_abbr(),
        "LOC_ZIPCODE": fake.zipcode(),
        "MAIL_ADDRESS1": fake.street_address(),
        "MAIL_CITY": fake.city(),
        "MAIL_STATE": fake.state_abbr(),
        "MAIL_ZIPCODE": fake.zipcode(),
        "PhoneNumber": fake.msisdn()[:10],
        "SCHOOLYEAR": random.choice(["2021-2022", "2022-2023", "2023-2024"]),
        "OPERATIONSTATUS": random.choice(["Open", "Closed", "Pending"])
    }

from faker import Faker
from random import randint, choice

fake = Faker()

def generate_fake_school(index: int) -> dict:
    return {
        "LEA Ide": str(1390046 + index),
        "LEA Identifier (Stat": str(randint(100000, 999999)),
        "FI": str(randint(1, 99)),
        "Education Entity Name": f"{fake.last_name()} Elementary School",
        "School Ident": str(100000000000 + index),
        "Mailing Address1": fake.street_address(),
        "Mailing Address2": "",
        "Mailing Address3": "",
        "City": fake.city().upper(),
        "St": fake.state_abbr(),
        "Zip": fake.zipcode(),
        "Telephone Education ": fake.msisdn()[0:10],
        "School Ye": "2022-2023",
        "School Operational S": choice(["Open", "Closed", "Pending"])
    }



if __name__ == '__main__':
    data = [generate_fake_lea(i) for i in range(100)]
    df = pd.DataFrame(data)
    df.to_csv("lea_directory_may14.csv", sep=',', index=False)
    
    data = [generate_fake_school(i) for i in range(100)]
    df = pd.DataFrame(data)
    df.to_csv("school_directory_may14.csv", sep=',', index=False)