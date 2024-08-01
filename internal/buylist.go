package internal

import (
	"errors"

	"gorm.io/gorm"
)

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

func (service *BuyListService) Update(list BuyList, ID uint64) (BuyList, error) {
	var findBuyList BuyList
	service.Database.First(&findBuyList, ID)

	var err error
	if findBuyList.ID == 0 {
		err = errors.New("List does not exists")
	}

	if err != nil {
		return list, err
	}

	result := service.Database.Save(&list)

	return list, result.Error
}

func (service *BuyListService) Delete(ID uint64) (BuyList, error) {
	var findBuyList BuyList
	service.Database.Model(&findBuyList).Preload("Items.Ingredient").First(&findBuyList, ID)

	var err error
	if findBuyList.ID == 0 {
		err = errors.New("List does not exists")
	}

	if err != nil {
		return findBuyList, err
	}

	result := service.Database.Delete(&findBuyList)

	return findBuyList, result.Error
}
