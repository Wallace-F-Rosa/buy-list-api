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

func (service *IngredientService) Create(name string, originType string) Ingredient {
	ingredient := Ingredient{Name: name, OriginType: originType}
	service.Database.Create(&ingredient)
	return ingredient
}
