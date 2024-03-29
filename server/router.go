package server

import (
	"log"
	"meal-planner/planner"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
	"gorm.io/gorm"
)

func GetRouter(databaseConnection *gorm.DB) *gin.Engine {
	ingredientService := planner.IngredientService{databaseConnection}

	// load .env file
	err := godotenv.Load("../.env")
	if err != nil {
		log.Fatal("Error loading .env file")
	}

	// init router
	gin.SetMode(os.Getenv("GIN_MODE"))
	router := gin.Default()
	router.SetTrustedProxies(nil)

	router.GET("/ping", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"message": "pong",
		})
	})

	router.POST("/ingredient", func(c *gin.Context) {
		var ingredient planner.Ingredient

		err := c.BindJSON(&ingredient)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": err.Error(),
			})
		}
		ingredient = ingredientService.Create(ingredient.Name, ingredient.OriginType)

		c.JSON(http.StatusCreated, ingredient)
	})

	return router
}
