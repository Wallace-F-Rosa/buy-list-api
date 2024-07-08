package internal

import "gorm.io/gorm"

type BuyItem struct {
	gorm.Model
	Ingredient   Ingredient
	IngredientID int
	Quantity     uint
	BuyListID    int
}

type BuyList struct {
	gorm.Model
	Title string
	Items []BuyItem
}

type BuyListService struct {
	Database *gorm.DB
}

func (service *BuyListService) Create(list BuyList) (BuyList, error) {
	result := service.Database.Create(&list)
	return list, result.Error
}
