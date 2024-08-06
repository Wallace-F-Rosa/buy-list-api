package middleware

import (
	"buylist/internal"
	"net/http"

	"github.com/gin-gonic/gin"
)

func ValidateBuyList() gin.HandlerFunc {
	return func(c *gin.Context) {
		var buyList internal.BuyList

		err := c.BindJSON(&buyList)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": err.Error(),
			})
			return
		}

		c.Set("buyList", buyList)
	}
}
