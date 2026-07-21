# BuyList API
This is an web REST API made with the intent of learning Spring boot for web backend web development.
Web framework used is [Spring boot](https://spring.io/projects/spring-boot).
The goal of this application is to manage groceries buylists. The API provided should be able to manage grocery items and provide a way to manage buy list's that contain these items.


## Goals
- [x] Setup project
    - [x] Swagger docs
- [x] Manage ingredients
- [x] Manage buy lists
- [x] Authentication by jwt
- [x] Make buylists be visible only to users that created them
- [] Make ingredients used have a initial payload
- [] Notification service
    - [] Generate an application event on buylist creation
    - [] On event capture send notification info (list data and date to notify) to notification service

TODO: create a new app to implement notification sending
- [ ] Notify the user in date selected to use the buy list
    - [ ] email notification
    - [ ] Whatsapp notification

## Run project
Install dependencies and run project locally:
.\gradlew bootRun --args="--spring.profiles.active=local"

Api is running on localhost:8080/api
To see swagger docs go to localhost:8080/docs