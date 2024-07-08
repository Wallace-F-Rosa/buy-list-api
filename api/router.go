package api

import (
	"os"

	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"gorm.io/gorm"
)

func GetRouter(databaseConnection *gorm.DB) *gin.Engine {
	// init router
	gin.SetMode(os.Getenv("GIN_MODE"))
	router := gin.Default()
	router.SetTrustedProxies(nil)
	api := router.Group("/api")
	{
		GetIngredientRoutes(api, databaseConnection)
		GetBuyListRoutes(api, databaseConnection)
	}

	router.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	return router
}
