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

// GetBuyList godoc
// @Summary Find buylists
// @Description Search buylists, by default returns all lists on database.
// Using query params will search for buylists that match them.
// @Produces json
// @Sucess 200 {array} []internal.BuyList
// @Failure 400
// @Failure 500
// @Router /buylist [get]
// @Param title query string false "buylist title"
// @Param created_at query string false "buylist creation date in dd/mm/yyyy format"
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

// CreateBuyList godoc
// @Summary Create buylist with ingredients
// @Description Receives post data that creates a buylist
// @Accepts json
// @Produces json
// @Sucess 201 {object} internal.BuyList
// @Failure 400
// @Failure 500
// @Router /buylist [post]
func CreateBuyList(c *gin.Context, service *internal.BuyListService) {
	buyList := c.MustGet("buyList").(internal.BuyList)

	buyList, err := service.Create(buyList)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, buyList)
}

// UpdateBuyList godoc
// @Summary Update a buylist
// @Description Receives the identifier of buylist and data to update it.
// @Accepts json
// @Produces json
// @Sucess 200 {object} internal.BuyList
// @Failure 400
// @Failure 500
// @Router /buylist [put]
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

// DeleteBuyList godoc
// @Summary Deletes an buylist
// @Description Receives the identifier of an buylist and deletes it.
// @Accepts json
// @Produces json
// @Sucess 200 {object} internal.BuyList
// @Failure 400
// @Failure 500
// @Router /buylist [delete]
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
