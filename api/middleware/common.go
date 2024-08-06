package middleware

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

func ValidateId() gin.HandlerFunc {
	return func(c *gin.Context) {
		idNum, err := strconv.ParseUint(c.Param("id"), 10, 32)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		}

		c.Set("idNum", idNum)
	}
}
