package database

import (
	"buylist/internal"
	"os"
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
			instance, err = gorm.Open(sqlite.Open(os.Getenv("SQLITE_PATH")), &gorm.Config{})
			if err != nil {
				panic("Failed to connect database!")
			}
		}
	}
	instance.AutoMigrate(&internal.Ingredient{})
	return instance
}
