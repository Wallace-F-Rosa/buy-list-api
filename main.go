package main

import (
	server "buylist/api"
	_ "buylist/docs"
	"buylist/internal/database"
	"log"

	"github.com/joho/godotenv"
)

// @title  Buy list API
// @version 1.0
// @description This is an web REST API made with the intent of learning Go for web backend web development.
// @termsOfService  http://swagger.io/terms/

// @contact.name   API Support
// @contact.url    http://www.swagger.io/support
// @contact.email  support@swagger.io

// @license.name MIT
// @license.url https://github.com/Wallace-F-Rosa/meal-planner?tab=MIT-1-ov-file

// @host localhost:8080
// @Base Path
// @securityDefinitions.basic  BasicAuth

// @externalDocs.description  OpenAPI
// @externalDocs.url          https://swagger.io/resources/open-api/

func LoadEnv() {
	// load .env file
	err := godotenv.Load(".env")
	if err != nil {
		log.Fatal("Error loading .env file")
	}
}

func main() {
	LoadEnv()
	db := database.GetDatabaseConnection()
	app := server.GetRouter(db)

	app.Run() // run on default port 8080
}
