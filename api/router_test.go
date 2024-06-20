package api

import (
	"buylist/internal"
	"buylist/internal/database"
	"bytes"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"net/http/httptest"
	"strconv"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
	"github.com/stretchr/testify/assert"
	"gorm.io/gorm"
)

func LoadEnv() {
	// load .env file
	err := godotenv.Load("..\\.env")
	if err != nil {
		log.Fatal("Error loading .env file")
	}
}

func setup() (*gin.Engine, *httptest.ResponseRecorder, *gorm.DB) {
	LoadEnv()
	db := database.GetDatabaseConnection()
	router := GetRouter(db)
	recorder := httptest.NewRecorder()
	return router, recorder, db
}

var router, recorder, db = setup()

func TestIngredientCreate(t *testing.T) {
	recorder := httptest.NewRecorder()
	ingredient := &internal.Ingredient{Name: "bread", OriginType: "plant"}
	ingredientJson, _ := json.Marshal(ingredient)
	body := bytes.NewBuffer(ingredientJson)

	req, _ := http.NewRequest("POST", "/api/ingredient", body)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusCreated, recorder.Code)
	var result internal.Ingredient
	json.Unmarshal(recorder.Body.Bytes(), &result)
	assert.Equal(t, ingredient.Name, result.Name)
	assert.Equal(t, ingredient.OriginType, result.OriginType)
}

func TestIngredientUpdate(t *testing.T) {
	recorder := httptest.NewRecorder()
	service := &internal.IngredientService{Database: db}
	ingredient, _ := service.Create("test", "testing")
	ingredientJson, _ := json.Marshal(ingredient)
	jsonBody := bytes.NewBuffer(ingredientJson)

	req, _ := http.NewRequest("PUT", "/api/ingredient/"+strconv.FormatUint(uint64(ingredient.ID), 10), jsonBody)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusOK, recorder.Code)
	var result internal.Ingredient
	json.Unmarshal(recorder.Body.Bytes(), &result)

	assert.Equal(t, ingredient.ID, result.ID)
	assert.Equal(t, ingredient.Name, result.Name)
	assert.Equal(t, ingredient.OriginType, result.OriginType)
}

func TestIngredientDelete(t *testing.T) {
	recorder := httptest.NewRecorder()
	service := &internal.IngredientService{Database: db}
	ingredient, _ := service.Create("test delete", "testing")

	req, _ := http.NewRequest("DELETE", "/api/ingredient/"+strconv.FormatUint(uint64(ingredient.ID), 10), nil)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusOK, recorder.Code)
	var result internal.Ingredient
	json.Unmarshal(recorder.Body.Bytes(), &result)

	assert.Equal(t, ingredient.ID, result.ID)
	assert.Equal(t, ingredient.Name, result.Name)
	assert.Equal(t, ingredient.OriginType, result.OriginType)
}

func TestIngredientFind(t *testing.T) {
	recorder := httptest.NewRecorder()
	service := &internal.IngredientService{Database: db}
	ingredient, _ := service.Create("test find", "testing")

	req, _ := http.NewRequest("GET", "/api/ingredient", nil)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusOK, recorder.Code)

	var result []internal.Ingredient
	json.Unmarshal(recorder.Body.Bytes(), &result)
	assert.NotEmpty(t, result)
	resultMap := make(map[uint]internal.Ingredient, len(result))
	for _, ingredient := range result {
		resultMap[ingredient.ID] = ingredient
	}

	value, exists := resultMap[ingredient.ID]
	assert.True(t, exists)
	assert.Equal(t, ingredient.ID, value.ID)
	assert.Equal(t, ingredient.Name, value.Name)
	assert.Equal(t, ingredient.OriginType, value.OriginType)
}

func TestIngredientFindByParams(t *testing.T) {
	service := &internal.IngredientService{Database: db}
	ingredient, _ := service.Create("test find", "testing")

	query := []string{
		"name=find",
		"originType=testing",
		"name=find&originType=testing",
	}

	for _, param := range query {
		recorder := httptest.NewRecorder()
		req, _ := http.NewRequest("GET", fmt.Sprintf("/api/ingredient?%s", param), nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusOK, recorder.Code)

		var result []internal.Ingredient
		json.Unmarshal(recorder.Body.Bytes(), &result)
		assert.NotEmpty(t, result)
		resultMap := make(map[uint]internal.Ingredient, len(result))
		for _, ingredient := range result {
			resultMap[ingredient.ID] = ingredient
		}

		value, exists := resultMap[ingredient.ID]
		assert.True(t, exists)
		assert.Equal(t, ingredient.ID, value.ID)
		assert.Equal(t, ingredient.Name, value.Name)
		assert.Equal(t, ingredient.OriginType, value.OriginType)
	}

}
