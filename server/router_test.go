package server

import (
	"bytes"
	"encoding/json"
	"meal-planner/planner"
	"meal-planner/planner/database"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/gin-gonic/gin"
	"github.com/stretchr/testify/assert"
)

func setup() (*gin.Engine, *httptest.ResponseRecorder) {
	db := database.GetDatabaseConnection()
	router := GetRouter(db)
	recorder := httptest.NewRecorder()
	return router, recorder
}

func TestPingRoute(t *testing.T) {
	router, w := setup()

	req, _ := http.NewRequest("GET", "/ping", nil)
	router.ServeHTTP(w, req)

	assert.Equal(t, 200, w.Code)
	var result map[string]any
	json.Unmarshal(w.Body.Bytes(), &result)
	assert.Equal(t, "pong", result["message"])
}

func TestIngredientCreate(t *testing.T) {
	router, recorder := setup()

	ingredient := &planner.Ingredient{Name: "bread", OriginType: "plant"}
	ingredientJson, _ := json.Marshal(ingredient)
	body := bytes.NewBuffer(ingredientJson)

	req, _ := http.NewRequest("POST", "/ingredient", body)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusCreated, recorder.Code)
	var result planner.Ingredient
	json.Unmarshal(recorder.Body.Bytes(), &result)
	assert.Equal(t, ingredient.ID, result.ID)
	assert.Equal(t, ingredient.Name, result.Name)
	assert.Equal(t, ingredient.OriginType, result.OriginType)
}
