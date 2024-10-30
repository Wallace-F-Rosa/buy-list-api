package api

import (
	"buylist/api/auth"
	"buylist/internal"
	"buylist/internal/database"
	"bytes"
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"net/http/httptest"
	"net/url"
	"strconv"
	"testing"
	"time"

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
	auth, _ := auth.New()
	router := GetRouter(db, auth)
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

func TestBuyListCreate(t *testing.T) {
	recorder := httptest.NewRecorder()

	buylist := &internal.BuyList{Title: "Testing list",
		Items: []internal.BuyItem{
			{
				Ingredient: internal.Ingredient{
					Name:       "test",
					OriginType: "spice",
				},
				Quantity: 2,
			},
		},
	}
	buylistJson, _ := json.Marshal(buylist)
	body := bytes.NewBuffer(buylistJson)

	// println(string(buylistJson))

	req, _ := http.NewRequest("POST", "/api/buylist", body)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusCreated, recorder.Code)
	var result internal.BuyList
	json.Unmarshal(recorder.Body.Bytes(), &result)
	assert.Equal(t, buylist.Title, result.Title)
	assert.Equal(t, len(buylist.Items), len(result.Items))
	for i, item := range buylist.Items {
		assert.Equal(t, item.Quantity, result.Items[i].Quantity)
		assert.Equal(t, item.Ingredient.Name, result.Items[i].Ingredient.Name)
		assert.Equal(t, item.Ingredient.OriginType, result.Items[i].Ingredient.OriginType)
	}
}

func TestBuyListUpdate(t *testing.T) {
	recorder := httptest.NewRecorder()

	service := internal.BuyListService{Database: db}
	buylist := internal.BuyList{Title: "Testing list",
		Items: []internal.BuyItem{
			{
				Ingredient: internal.Ingredient{
					Name:       "test",
					OriginType: "condiment",
				},
				Quantity: 2,
			},
		},
	}

	buylist, _ = service.Create(buylist)

	buylist.Title = "new title"
	buylist.Items = []internal.BuyItem{
		{
			Ingredient: internal.Ingredient{
				Name:       "test 2",
				OriginType: "chemichal",
			},
			Quantity: 3,
		},
	}

	updateBuyList, _ := json.Marshal(buylist)
	body := bytes.NewBuffer(updateBuyList)

	req, _ := http.NewRequest("PUT", "/api/buylist/"+strconv.FormatUint(uint64(buylist.ID), 10), body)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusOK, recorder.Code)

	var result internal.BuyList
	json.Unmarshal(recorder.Body.Bytes(), &result)

	assert.Equal(t, buylist.ID, result.ID)
	assert.Equal(t, buylist.Title, result.Title)
	assert.Equal(t, len(buylist.Items), len(result.Items))
	for i, item := range buylist.Items {
		assert.Equal(t, item.Quantity, result.Items[i].Quantity)
		assert.Equal(t, item.Ingredient.Name, result.Items[i].Ingredient.Name)
		assert.Equal(t, item.Ingredient.OriginType, result.Items[i].Ingredient.OriginType)
	}
}

func TestBuyListDelete(t *testing.T) {
	recorder := httptest.NewRecorder()
	service := internal.BuyListService{Database: db}
	list, _ := service.Create(internal.BuyList{
		Title: "testing",
		Items: []internal.BuyItem{
			{
				Ingredient: internal.Ingredient{
					Name:       "test",
					OriginType: "testing",
				},
				Quantity: 2,
			},
		},
	})

	req, _ := http.NewRequest("DELETE", "/api/buylist/"+strconv.FormatUint(uint64(list.ID), 10), nil)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusOK, recorder.Code)
	var result internal.BuyList
	json.Unmarshal(recorder.Body.Bytes(), &result)

	assert.Equal(t, list.ID, result.ID)
	assert.Equal(t, list.Title, result.Title)
	assert.Equal(t, len(list.Items), len(result.Items))
	for i, item := range list.Items {
		assert.Equal(t, item.Quantity, result.Items[i].Quantity)
		assert.Equal(t, item.Ingredient.Name, result.Items[i].Ingredient.Name)
		assert.Equal(t, item.Ingredient.OriginType, result.Items[i].Ingredient.OriginType)
	}
}

func TestBuyListFindByParams(t *testing.T) {
	service := internal.BuyListService{Database: db}
	list, _ := service.Create(internal.BuyList{
		Title: "testing",
		Items: []internal.BuyItem{
			{
				Ingredient: internal.Ingredient{
					Name:       "test",
					OriginType: "testing",
				},
				Quantity: 2,
			},
		},
	})

	query1 := url.Values{}
	query1.Add("title", "test")

	query2 := url.Values{}
	now := time.Now()
	query2.Add("created_at", now.Format("02/01/2006"))

	query3 := url.Values{}
	query3.Add("title", "ing")
	query3.Add("created_at", now.Format("02/01/2006"))

	query := []url.Values{
		query1,
		query2,
		query3,
	}

	for _, param := range query {
		recorder := httptest.NewRecorder()
		req, _ := http.NewRequest("GET", "/api/buylist?"+param.Encode(), nil)
		router.ServeHTTP(recorder, req)

		assert.Equal(t, http.StatusOK, recorder.Code)

		var result []internal.BuyList
		json.Unmarshal(recorder.Body.Bytes(), &result)
		assert.NotEmpty(t, result)
		resultMap := make(map[uint]internal.BuyList, len(result))
		for _, ingredient := range result {
			resultMap[ingredient.ID] = ingredient
		}

		value, exists := resultMap[list.ID]
		assert.True(t, exists)
		assert.Equal(t, list.ID, value.ID)
		assert.Equal(t, list.Title, value.Title)
		assert.Equal(t, len(list.Items), len(value.Items))
		for i, item := range list.Items {
			assert.Equal(t, item.Quantity, value.Items[i].Quantity)
			assert.Equal(t, item.Ingredient.Name, value.Items[i].Ingredient.Name)
			assert.Equal(t, item.Ingredient.OriginType, value.Items[i].Ingredient.OriginType)
		}
	}
}

func TestBuyListFind(t *testing.T) {
	service := internal.BuyListService{Database: db}
	list, _ := service.Create(internal.BuyList{
		Title: "testing",
		Items: []internal.BuyItem{
			{
				Ingredient: internal.Ingredient{
					Name:       "test",
					OriginType: "testing",
				},
				Quantity: 2,
			},
		},
	})

	recorder := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/api/buylist", nil)
	router.ServeHTTP(recorder, req)

	assert.Equal(t, http.StatusOK, recorder.Code)

	var result []internal.BuyList
	json.Unmarshal(recorder.Body.Bytes(), &result)
	assert.NotEmpty(t, result)
	resultMap := make(map[uint]internal.BuyList, len(result))
	for _, ingredient := range result {
		resultMap[ingredient.ID] = ingredient
	}

	value, exists := resultMap[list.ID]
	assert.True(t, exists)
	assert.Equal(t, list.ID, value.ID)
	assert.Equal(t, list.Title, value.Title)
	assert.Equal(t, len(list.Items), len(value.Items))
	for i, item := range list.Items {
		assert.Equal(t, item.Quantity, value.Items[i].Quantity)
		assert.Equal(t, item.Ingredient.Name, value.Items[i].Ingredient.Name)
		assert.Equal(t, item.Ingredient.OriginType, value.Items[i].Ingredient.OriginType)
	}
}
