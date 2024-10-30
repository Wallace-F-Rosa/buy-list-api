package api

import (
	"buylist/api/auth"
	"buylist/api/login"
	"os"

	"github.com/gin-contrib/sessions"
	"github.com/gin-contrib/sessions/cookie"
	"github.com/gin-gonic/gin"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"gorm.io/gorm"
)

func GetRouter(databaseConnection *gorm.DB, auth *auth.Authenticator) *gin.Engine {
	// init router
	gin.SetMode(os.Getenv("GIN_MODE"))
	router := gin.Default()
	router.SetTrustedProxies(nil)

	// init session
	store := cookie.NewStore([]byte("secret"))
	router.Use(sessions.Sessions("auth-session", store))

	api := router.Group("/api")
	{
		GetIngredientRoutes(api, databaseConnection)
		GetBuyListRoutes(api, databaseConnection)
		api.GET("/login", login.Handler(auth))
	}

	router.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	return router
}
