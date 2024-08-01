package api

import (
	"buylist/internal"
	"database/sql"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func GetBuyList(c *gin.Context, service *internal.BuyListService) {
	title := c.Query("title")
	createdAtStr := c.Query("created_at")

	var createdAt sql.NullTime
	if createdAtStr != "" {
		createdAtDate, err := time.Parse("02/01/2006", createdAtStr)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{
				"error": "Invalid date passed on created_at parameter",
			})
			return
		}

		createdAt.Scan(createdAtDate)
	}

	var lists []internal.BuyList
	var err error
	if title != "" || createdAtStr != "" {
		lists, err = service.FindByParams(title, createdAt)
	} else {
		lists, err = service.Find()
	}

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, lists)
}

func CreateBuyList(c *gin.Context, service *internal.BuyListService) {
	var buylist internal.BuyList

	err := c.BindJSON(&buylist)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": err.Error(),
		})
		return
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
		return
	}

	idNum, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid list identifier"})
		return
	}

	if idNum != uint64(buylist.ID) {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "List identifier doesn't match the data sent",
		})
		return
	}

	buylist, err = service.Update(buylist, idNum)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, buylist)
}

func DeleteBuyList(c *gin.Context, service *internal.BuyListService) {

	idNum, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "Invalid list identifier",
		})
		return
	}

	list, err := service.Delete(idNum)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"error": err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, list)
}

func GetBuyListRoutes(group *gin.RouterGroup, db *gorm.DB) {
	service := internal.BuyListService{Database: db}
	buylist := group.Group("buylist")
	{
		buylist.GET("", func(c *gin.Context) {
			GetBuyList(c, &service)
		})
		buylist.POST("", func(c *gin.Context) {
			CreateBuyList(c, &service)
		})
		buylist.PUT("/:id", func(c *gin.Context) {
			UpdateBuyList(c, &service)
		})
		buylist.DELETE("/:id", func(c *gin.Context) {
			DeleteBuyList(c, &service)
		})
	}
}
