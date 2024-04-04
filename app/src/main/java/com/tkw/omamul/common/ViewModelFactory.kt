package com.tkw.omamul.common

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.tkw.omamul.data.local.CupDaoImpl
import com.tkw.omamul.data.local.CupRepositoryImpl
import com.tkw.omamul.data.local.WaterDaoImpl
import com.tkw.omamul.data.local.WaterRepositoryImpl
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.CupListEntity
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
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
                WaterRepositoryImpl(WaterDaoImpl(realm)),
                CupRepositoryImpl(CupDaoImpl(realm)),
                handle
            )
            InitViewModel::class.java -> InitViewModel(
                WaterRepositoryImpl(WaterDaoImpl(realm))
            )
            CupViewModel::class.java -> CupViewModel(
                CupRepositoryImpl(CupDaoImpl(realm)),
                params as? Cup ?: Cup()
            )
            LogViewModel::class.java -> LogViewModel(
                WaterRepositoryImpl(WaterDaoImpl(realm))
            )
            else -> throw IllegalArgumentException("Unknown Class")
        } as T
    }
}