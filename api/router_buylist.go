package api

import (
	"buylist/api/middleware"
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
	buyList := c.MustGet("buyList").(internal.BuyList)

	buyList, err := service.Create(buyList)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, buyList)
}

func UpdateBuyList(c *gin.Context, service *internal.BuyListService) {
	buyList := c.MustGet("buyList").(internal.BuyList)
	idNum := c.MustGet("idNum").(uint64)

	if idNum != uint64(buyList.ID) {
		c.JSON(http.StatusBadRequest, gin.H{
			"error": "List identifier doesn't match the data sent",
		})
		return
	}

	buyList, err := service.Update(buyList, idNum)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, buyList)
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
		buylist.POST("", middleware.ValidateBuyList(), func(c *gin.Context) {
			CreateBuyList(c, &service)
		})
		buylist.PUT("/:id", middleware.ValidateBuyList(), middleware.ValidateId(), func(c *gin.Context) {
			UpdateBuyList(c, &service)
		})
		buylist.DELETE("/:id", middleware.ValidateId(), func(c *gin.Context) {
			DeleteBuyList(c, &service)
		})
	}
}
