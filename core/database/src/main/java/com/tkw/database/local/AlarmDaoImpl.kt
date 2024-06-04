package com.tkw.database.local

import com.tkw.database.AlarmDao
import com.tkw.database.model.AlarmSettingsEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

class AlarmDaoImpl @Inject constructor(): AlarmDao {
    override val realm: Realm = Realm.open(getRealmConfiguration())
    override val clazz: KClass<AlarmSettingsEntity> = AlarmSettingsEntity::class

    override fun updateSetting(setting: AlarmSettingsEntity) {
        TODO("Not yet implemented")
    }

    override fun getSetting(): AlarmSettingsEntity {
        TODO("Not yet implemented")
    }
}