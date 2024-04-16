package planner

import (
	"errors"

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

func (service *IngredientService) Update(ingredient Ingredient, ID uint) (Ingredient, error) {
	var findIngredient Ingredient
	service.Database.First(&findIngredient, ID)

	var err error
	if findIngredient.ID == 0 {
		err = errors.New("Ingredient does not exists")
	}

	if err != nil {
		return ingredient, err
	}

	result := service.Database.Save(&ingredient)

	return ingredient, result.Error
}

func (service *IngredientService) Delete(ID uint) (Ingredient, error) {
	var findIngredient Ingredient
	service.Database.First(&findIngredient, ID)

	var err error
	if findIngredient.ID == 0 {
		err = errors.New("Ingredient does not exists")
	}

	if err != nil {
		return findIngredient, err
	}

	result := service.Database.Delete(&findIngredient)

	return findIngredient, result.Error
}
