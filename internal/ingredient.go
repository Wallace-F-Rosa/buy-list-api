package internal

import (
	"errors"

	"gorm.io/gorm"
)

type Ingredient struct {
	gorm.Model
	Name       string `json::"name"`
	OriginType string `json::"originType"` // animal, plant, condiment, spice, chemical
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

func (service *IngredientService) Find() ([]Ingredient, error) {
	findIngredient := []Ingredient{}
	result := service.Database.Model(&Ingredient{}).Find(&findIngredient)

	return findIngredient, result.Error
}

// godoc
// Search ingredients with a name (or) and originType that is similar to param name provided.
func (service *IngredientService) FindByParams(name string, originType string) ([]Ingredient, error) {
	findIngredient := []Ingredient{}
	query := service.Database.Model(&Ingredient{})
	if name != "" {
		query = query.Where("name like ?", "%"+name+"%")
	}
	if originType != "" {
		query = query.Where("originType like ?", "%"+originType+"%")
	}
	result := query.Find(&findIngredient)

	return findIngredient, result.Error
}
