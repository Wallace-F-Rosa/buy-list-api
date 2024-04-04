package server

import (
	"log"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"gorm.io/gorm"
)

func GetRouter(databaseConnection *gorm.DB) *gin.Engine {

	// load .env file
	err := godotenv.Load("../.env")
	if err != nil {
		log.Fatal("Error loading .env file")
	}

	// init router
	gin.SetMode(os.Getenv("GIN_MODE"))
	router := gin.Default()
	router.SetTrustedProxies(nil)
	api := router.Group("/api")
	{
		GetIngredientRoutes(api, databaseConnection)
	}

	router.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	return router
}
