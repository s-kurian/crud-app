
INSERT INTO client (
    name,
    website_url,
    phone_number,
    street_address,
    city,
    state,
    zip_code
) VALUES (
    'Company1',
    'www.company1.com',
    '9999999999',
    '123 Any St.',
    'Raleigh',
    'NC',
    '25603'
), (
    'Company2',
    'www.company2.com',
    '9999999999',
    '564 Any St.',
    'Raleigh',
    'NC',
    '25606'
), (
    'Company3',
    'www.company3.com',
    '9999999999',
    '345 Any St.',
    'Raleigh',
    'NC',
    '25606'
);

INSERT INTO person (
    first_name,
    last_name,
    email_address,
    street_address,
    city,
    state,
    zip_code
) VALUES (
    'John',
    'Smith',
    'fake1@aquent.com',
    '123 Any St.',
    'Asheville',
    'NC',
    '28801'
), (
    'Jane',
    'Smith',
    'fake2@aquent.com',
    '123 Any St.',
    'Asheville',
    'NC',
    '28801'
), (
    'ddfd',
    'Smitdfdsh',
    'fake2@aquent.com',
    '123 Any St.',
    'Asheville',
    'NC',
    '28801'
);

INSERT INTO client_person (
	client_id,
	person_id
	) VALUES (
	1,
	1
), (
	1,
	2
);