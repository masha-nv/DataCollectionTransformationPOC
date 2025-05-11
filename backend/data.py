import pandas as pd 
from faker import Faker
import random 

fake = Faker() 
Faker.seed(0)

def generate_fake_school(index):
    return {
        "DistrictNCESID": str(1800046 + index),
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


if __name__ == '__main__':
    data = [generate_fake_school(i) for i in range(100)]
    df = pd.DataFrame(data)
    df.to_csv("lea_directory.tab", sep='\t', index=False)