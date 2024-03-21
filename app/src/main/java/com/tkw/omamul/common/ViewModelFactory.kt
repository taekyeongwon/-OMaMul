package com.tkw.omamul.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.tkw.omamul.data.local.CupDaoImpl
import com.tkw.omamul.data.local.CupRepositoryImpl
import com.tkw.omamul.data.local.WaterDaoImpl
import com.tkw.omamul.data.local.WaterRepositoryImpl
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.ui.view.init.InitViewModel
import com.tkw.omamul.ui.view.water.cup.CupViewModel
import com.tkw.omamul.ui.view.water.WaterViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
val ViewModelFactory = object: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()

        val conf = RealmConfiguration.Builder(setOf(
            DayOfWaterEntity::class,
            WaterEntity::class,
            CupEntity::class
        ))
//            .deleteRealmIfMigrationNeeded()
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
                CupRepositoryImpl(CupDaoImpl(realm))
            )
            else -> throw IllegalArgumentException("Unknown Class")
        } as T
    }
}