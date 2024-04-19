package com.tkw.omamul.common

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.tkw.database.local.CupDaoImpl
import com.tkw.data.local.CupRepositoryImpl
import com.tkw.database.local.WaterDaoImpl
import com.tkw.data.local.WaterRepositoryImpl
import com.tkw.domain.Cup
import com.tkw.model.CupEntity
import com.tkw.model.CupListEntity
import com.tkw.model.DayOfWaterEntity
import com.tkw.model.WaterEntity
import com.tkw.omamul.ui.view.init.InitViewModel
import com.tkw.omamul.ui.view.water.cup.CupViewModel
import com.tkw.omamul.ui.view.water.home.WaterViewModel
import com.tkw.omamul.ui.view.water.log.LogViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
fun <V: Parcelable> getViewModelFactory(params: V?) = object: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()

        val conf = RealmConfiguration.Builder(setOf(
            DayOfWaterEntity::class,
            WaterEntity::class,
            CupListEntity::class,
            CupEntity::class
        ))
            .deleteRealmIfMigrationNeeded()
            .build()
        val realm = Realm.open(conf)
        Log.d("test", conf.path)

        return when(modelClass) {
            WaterViewModel::class.java -> WaterViewModel(
                com.tkw.data.local.WaterRepositoryImpl(com.tkw.database.local.WaterDaoImpl(realm)),
                com.tkw.data.local.CupRepositoryImpl(com.tkw.database.local.CupDaoImpl(realm)),
                handle
            )
            InitViewModel::class.java -> InitViewModel(
                com.tkw.data.local.WaterRepositoryImpl(com.tkw.database.local.WaterDaoImpl(realm))
            )
            CupViewModel::class.java -> CupViewModel(
                com.tkw.data.local.CupRepositoryImpl(com.tkw.database.local.CupDaoImpl(realm)),
                params as? com.tkw.domain.Cup ?: com.tkw.domain.Cup()
            )
            LogViewModel::class.java -> LogViewModel(
                com.tkw.data.local.WaterRepositoryImpl(com.tkw.database.local.WaterDaoImpl(realm))
            )
            else -> throw IllegalArgumentException("Unknown Class")
        } as T
    }
}