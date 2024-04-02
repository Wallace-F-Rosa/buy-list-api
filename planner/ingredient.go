package planner

import (
	"gorm.io/gorm"
)

type Ingredient struct {
	gorm.Model
	Name       string
	OriginType string // animal, plant, condiment, spice, chemical
}

type IngredientService struct {
	Database *gorm.DB
}

func (service *IngredientService) Create(name string, originType string) (Ingredient, error) {
	ingredient := Ingredient{Name: name, OriginType: originType}
	result := service.Database.Create(&ingredient)
	return ingredient, result.Error
}

func (service *IngredientService) Update(ingredient Ingredient) (Ingredient, error) {
	result := service.Database.Updates(&ingredient)
	return ingredient, result.Error
}
