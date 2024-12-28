```
Table User {
  id int PK
  first_name text [not null]
  last_name text [not null]
  email text [unique, not null]
  department text [not null]
  salary float [not null]
  start_date text [not null]
  holiday_allowance int [not null]
  password text [not null]
  role roles [not null]
}

Table UserSettings {
  user_id int PK [ref: < User.id]
  pto_notifications int [not null]
  details_notifications int [not null]
  dark_theme int [not null]
  red_green_theme int [not null]
}

Enum roles {
  Admin
  Employee
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

```