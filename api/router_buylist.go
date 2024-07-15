package api

import (
	"buylist/internal"
	"net/http"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func CreateBuyList(c *gin.Context, service *internal.BuyListService) {
	var buylist internal.BuyList

	err := c.BindJSON(&buylist)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": err.Error(),
		})
	}
	buylist, err = service.Create(buylist)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, buylist)
}

func UpdateBuyList(c *gin.Context, service *internal.BuyListService) {
	var buylist internal.BuyList

	err := c.BindJSON(&buylist)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": err.Error(),
		})
	}
	buylist, err = service.Update(buylist, buylist.ID)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, buylist)
}

func GetBuyListRoutes(group *gin.RouterGroup, db *gorm.DB) {
	service := internal.BuyListService{Database: db}
	buylist := group.Group("buylist")
	{
		buylist.POST("", func(c *gin.Context) {
			CreateBuyList(c, &service)
		})
		buylist.PUT("", func(c *gin.Context) {
			UpdateBuyList(c, &service)
		})
	}
}
