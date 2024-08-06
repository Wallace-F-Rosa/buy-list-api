package api

import (
	"buylist/api/middleware"
	"buylist/internal"
	"net/http"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

// CreateIngredient godoc
// @Summary Create ingredient
// @Description Receives post data that creates an ingredient
// @Accepts json
// @Produces json
// @Sucess 201 {object} internal.Ingredient
// @Failure 400
// @Failure 500
// @Router /ingredient [post]
func CreateIngredient(c *gin.Context, service *internal.IngredientService) {
	ingredient := c.MustGet("ingredient").(internal.Ingredient)
	ingredient, err := service.Create(ingredient.Name, ingredient.OriginType)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
	}

	c.JSON(http.StatusCreated, ingredient)
}

// UpdateIngredient godoc
// @Summary Update an ingredient
// @Description Receives the identifier of ingredient and data to update it.
// @Accepts json
// @Produces json
// @Sucess 200 {object} internal.Ingredient
// @Failure 400
// @Failure 500
// @Router /ingredient [put]
func UpdateIngredient(c *gin.Context, service *internal.IngredientService) {
	ingredient := c.MustGet("ingredient").(internal.Ingredient)
	idNum := c.MustGet("idNum").(uint64)

	if idNum != uint64(ingredient.ID) {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "Ingredient data and identifier passed don't match",
		})
	}

	ingredient, err := service.Update(ingredient, uint(idNum))

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
	}

	c.JSON(http.StatusOK, ingredient)
}

// DeleteIngredient godoc
// @Summary Deletes an ingredient
// @Description Receives the identifier of an ingredient and deletes it.
// @Accepts json
// @Produces json
// @Sucess 200 {object} internal.Ingredient
// @Failure 400
// @Failure 500
// @Router /ingredient [delete]
func DeleteIngredient(c *gin.Context, service *internal.IngredientService) {
	idNum := c.MustGet("idNum").(uint64)
	ingredient, err := service.Delete(uint(idNum))

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
	} else {
		c.JSON(http.StatusOK, ingredient)
	}
}

// FindIngredient godoc
// @Summary Find ingredients
// @Description Search ingredients, by default returns all ingredients on database.
// Using query params will search for ingredients that match them.
// @Produces json
// @Sucess 200 {array} []internal.Ingredient
// @Failure 400
// @Failure 500
// @Router /ingredient [get]
// @Param name query string false "name of ingredient"
// @Param originType query string false "type of ingredient"
func FindIngredient(c *gin.Context, service *internal.IngredientService) {
	name := c.Query("name")
	originType := c.Query("originType")

	var ingredients []internal.Ingredient = nil
	var err error = nil
	if name != "" || originType != "" {
		ingredients, err = service.FindByParams(name, originType)
	} else {
		ingredients, err = service.Find()
	}

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, ingredients)
}

func GetIngredientRoutes(group *gin.RouterGroup, db *gorm.DB) {
	ingredientService := internal.IngredientService{Database: db}

	ingredient := group.Group("ingredient")
	{
		ingredient.GET("", func(c *gin.Context) {
			FindIngredient(c, &ingredientService)
		})

		ingredient.POST("", middleware.ValidateIngredient(), func(c *gin.Context) {
			CreateIngredient(c, &ingredientService)
		})

		ingredient.PUT("/:id", middleware.ValidateIngredient(), middleware.ValidateId(), func(c *gin.Context) {
			UpdateIngredient(c, &ingredientService)
		})

		ingredient.DELETE("/:id", middleware.ValidateId(), func(c *gin.Context) {
			DeleteIngredient(c, &ingredientService)
		})
	}
}
