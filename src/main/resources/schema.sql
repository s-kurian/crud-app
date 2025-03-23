CREATE TABLE client (
    client_id integer IDENTITY,
    name varchar(100) NOT NULL,
    website_url varchar(400) NOT NULL,
    phone_number varchar(10) NOT NULL,
    street_address varchar(50) NOT NULL,
    city varchar(50) NOT NULL,
    state varchar(2) NOT NULL,
    zip_code varchar(5) NOT NULL
);
CREATE TABLE person (
    person_id integer IDENTITY,
    first_name varchar(50) NOT NULL,
    last_name varchar(50) NOT NULL,
    email_address varchar(50) NOT NULL,
    street_address varchar(50) NOT NULL,
    city varchar(50) NOT NULL,
    state varchar(2) NOT NULL,
    zip_code varchar(5) NOT NULL
);
CREATE TABLE client_person (
    client_id integer,
    person_id integer,
    PRIMARY KEY (client_id, person_id),
    FOREIGN KEY (client_id) REFERENCES client(client_id),
    FOREIGN KEY (person_id) REFERENCES person(person_id)
);
