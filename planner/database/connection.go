package database

import (
	"meal-planner/planner"
	"sync"

	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

var lock = &sync.Mutex{}

var instance *gorm.DB

func GetDatabaseConnection() *gorm.DB {
	if instance == nil {
		lock.Lock()
		defer lock.Unlock()
		if instance == nil {
			var err error
			instance, err = gorm.Open(sqlite.Open("local.bd"), &gorm.Config{})
			if err != nil {
				panic("Failed to connect database!")
			}
		}
	}
	instance.AutoMigrate(&planner.Ingredient{})
	return instance
}
