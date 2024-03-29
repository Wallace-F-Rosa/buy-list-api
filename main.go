package main

import (
	"meal-planner/planner/database"
	"meal-planner/server"
)

func main() {
	db := database.GetDatabaseConnection()
	app := server.GetRouter(db)

	app.Run() // run on default port 8080
}
