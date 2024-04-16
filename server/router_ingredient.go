package server

import (
	"meal-planner/planner"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

// CreateIngredient godoc
// @Summary Create ingredient
// @Description Receives post data that creates an ingredient
// @Accepts json
// @Produces json
// @Sucess 201 {object} planner.Ingredient
// @Failure 400
// @Failure 500
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
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
	}

	c.JSON(http.StatusCreated, ingredient)
}

// UpdateIngredient godoc
// @Summary Update an ingredient
// @Description Receives the identifier of ingredient and data to update it.
// @Accepts json
// @Produces json
// @Sucess 200 {object} planner.Ingredient
// @Failure 400
// @Failure 500
// @Router /ingredient [put]
func UpdateIngredient(c *gin.Context, service *planner.IngredientService) {
	var ingredient planner.Ingredient

	err := c.BindJSON(&ingredient)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": err.Error(),
		})
	}

	idNum, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
	}

	ingredient, err = service.Update(ingredient, uint(idNum))

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
	}

	c.JSON(http.StatusOK, ingredient)
}

// DeleteIngredient godoc
// @Summary Update an ingredient
// @Description Receives the identifier of ingredient and deletes it.
// @Accepts json
// @Produces json
// @Sucess 200 {object} planner.Ingredient
// @Failure 400
// @Failure 500
// @Router /ingredient [put]
func DeleteIngredient(c *gin.Context, service *planner.IngredientService) {
	idNum, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
	}

	ingredient, err := service.Delete(uint(idNum))

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
	} else {
		c.JSON(http.StatusOK, ingredient)
	}
}

func GetIngredientRoutes(group *gin.RouterGroup, db *gorm.DB) {
	ingredientService := planner.IngredientService{Database: db}

	ingredient := group.Group("ingredient")
	{
		ingredient.POST("", func(c *gin.Context) {
			CreateIngredient(c, &ingredientService)
		})

		ingredient.PUT("/:id", func(c *gin.Context) {
			UpdateIngredient(c, &ingredientService)
		})

		ingredient.DELETE("/:id", func(c *gin.Context) {
			DeleteIngredient(c, &ingredientService)
		})
	}
}
