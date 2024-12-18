```
Table User {
  id int PK
  first_name text [not null]
  last_name text [not null]
  email text [unique, not null]
  phone text [unique, not null]
  address text [not null]
  job_title text [not null]
  start_date date [not null]
  password text [not null]
  holiday_allowance int [default: 0]
  role roles [not null]
}

Enum roles {
  Employee
  Admin
}

Table PtoRequest {
  id int PK
  requestor_id int [ref: < User.id, not null]
  start_date datetime [not null]
  end_date datetime [not null]
  status statuses [not null]
  request_comment text
}

Enum statuses {
  Approved
  Waiting
  Denied
}

Table Line {
  manager_id int [ref: <> User.id, not null]
  subordinate_id int [ref: <> User.id, not null]
}
```