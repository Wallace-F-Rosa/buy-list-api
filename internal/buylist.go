package internal

import (
	"database/sql"
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

// Search lists with similar title to parameter title and created at the date passed
// if title is empty string "" it will not be used
// createdAt will not be used if date is null
func (service *BuyListService) FindByParams(title string, createdAt sql.NullTime) ([]BuyList, error) {
	lists := []BuyList{}
	query := service.Database.Model(&BuyList{}).Preload("Items.Ingredient")
	if title != "" {
		query = query.Where("title like ?", "%"+title+"%")
	}
	if createdAt.Valid {
		query = query.Where("created_at >= ?", createdAt.Time)
		query = query.Where("created_at < ?", createdAt.Time.AddDate(0, 0, 1))
	}

	result := query.Find(&lists)
	return lists, result.Error
}

func (service *BuyListService) Find() ([]BuyList, error) {
	lists := []BuyList{}
	query := service.Database.Model(&BuyList{}).Preload("Items.Ingredient")

	result := query.Find(&lists)
	return lists, result.Error
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
