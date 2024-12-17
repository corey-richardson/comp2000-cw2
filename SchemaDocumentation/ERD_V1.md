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
  password text [unique, not null]
  role roles [not null]
}

Enum roles {
  Employee
  Admin
}

Table PtoRequest {
  id int PK
  requestor_id int [ref: < User.id, not null]
  approver_id int [ref: < User.id, not null]
  status statuses [not null]
  start_date datetime [not null]
  end_date datetime [not null]
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