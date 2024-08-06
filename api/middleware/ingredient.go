package middleware

import (
	"buylist/internal"
	"net/http"

	"github.com/gin-gonic/gin"
)

func ValidateIngredient() gin.HandlerFunc {
	return func(c *gin.Context) {
		var ingredient internal.Ingredient

		err := c.BindJSON(&ingredient)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": err.Error(),
			})
			return
		}

		c.Set("ingredient", ingredient)
	}
}
