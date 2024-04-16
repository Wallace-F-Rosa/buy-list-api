package server

import (
	"bytes"
	"encoding/json"
	"meal-planner/planner"
	"meal-planner/planner/database"
	"net/http"
	"net/http/httptest"
	"strconv"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
	"gorm.io/gorm"
)

func setup() (*gin.Engine, *httptest.ResponseRecorder, *gorm.DB) {
	db := database.GetDatabaseConnection()
	router := GetRouter(db)
	recorder := httptest.NewRecorder()
	return router, recorder, db
}

var router, recorder, db = setup()

func TestIngredientCreate(t *testing.T) {
	ingredient := &planner.Ingredient{Name: "bread", OriginType: "plant"}
	ingredientJson, _ := json.Marshal(ingredient)
	body := bytes.NewBuffer(ingredientJson)

	req, _ := http.NewRequest("POST", "/api/ingredient", body)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusCreated, recorder.Code)
	var result planner.Ingredient
	json.Unmarshal(recorder.Body.Bytes(), &result)
	assert.Equal(t, ingredient.Name, result.Name)
	assert.Equal(t, ingredient.OriginType, result.OriginType)
}

func TestIngredientUpdate(t *testing.T) {
	service := &planner.IngredientService{Database: db}
	ingredient, _ := service.Create("test", "testing")
	ingredientJson, _ := json.Marshal(ingredient)
	jsonBody := bytes.NewBuffer(ingredientJson)

	req, _ := http.NewRequest("PUT", "/api/ingredient/"+strconv.FormatUint(uint64(ingredient.ID), 10), jsonBody)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusOK, recorder.Code)
	var result planner.Ingredient
	json.Unmarshal(recorder.Body.Bytes(), &result)

	assert.Equal(t, ingredient.ID, result.ID)
	assert.Equal(t, ingredient.Name, result.Name)
	assert.Equal(t, ingredient.OriginType, result.OriginType)
}

func TestIngredientDelete(t *testing.T) {
	service := &planner.IngredientService{Database: db}
	ingredient, _ := service.Create("test delete", "testing")

	req, _ := http.NewRequest("DELETE", "/api/ingredient/"+strconv.FormatUint(uint64(ingredient.ID), 10), nil)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusOK, recorder.Code)
	var result planner.Ingredient
	json.Unmarshal(recorder.Body.Bytes(), &result)

	assert.Equal(t, ingredient.ID, result.ID)
	assert.Equal(t, ingredient.Name, result.Name)
	assert.Equal(t, ingredient.OriginType, result.OriginType)
}
