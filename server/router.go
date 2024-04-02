package server

import (
	"log"
	"meal-planner/planner"
	"net/http"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"gorm.io/gorm"
)

// CreateIngredient godoc
// @Summary Create ingredient
// @Description Receives post data that creates an ingredient
// @Accepts json
// @Produces json
// @Sucess 200 {object} planner.Ingredient
// @Failure 400
// @Router /ingredient [post]
func CreateIngredient(c *gin.Context, service *planner.IngredientService) {
	var ingredient planner.Ingredient

	err := c.BindJSON(&ingredient)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": err.Error(),
		})
	}
	ingredient, err = service.Create(ingredient.Name, ingredient.OriginType)

	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
	}

	c.JSON(http.StatusCreated, ingredient)
}

func GetRouter(databaseConnection *gorm.DB) *gin.Engine {
	ingredientService := planner.IngredientService{Database: databaseConnection}

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

		ingredient := api.Group("ingredient")
		{

			ingredient.POST("", func(c *gin.Context) {
				CreateIngredient(c, &ingredientService)
			})
		}
	}

	router.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	return router
}
