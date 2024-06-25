# BuyList
This is an web REST API made with the intent of learning Go for web backend web development.
Web framework used is [Gin](https://gin-gonic.com/).
The goal of this application is to manage food buylists. The API provided should be able to
manage to show food grocery items and provide a way to manage buy list's for those items.


## Goals
- [X] Setup project
- [ ] Manage ingredients
- [ ] Manage buy lists (each user has its own lists)
- [ ] Authentication by tokens
- [ ] Notify the user in date selected to use the buy list
    - [ ] email notification
    - [ ] Whatsapp notification

## Run project
Install dependencies and compile:
`go build`

Generate Swagger docs:
`swag init`

To run api server:
`go run main.go`

Api is running on localhost:8080/api
To see swagger docs go to localhost:8080/docs/index.html