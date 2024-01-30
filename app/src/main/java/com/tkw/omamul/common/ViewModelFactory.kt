package com.tkw.omamul.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.tkw.omamul.data.MainRepositoryImpl
import com.tkw.omamul.data.local.CountDaoImpl
import com.tkw.omamul.data.local.LocalDataSource
import com.tkw.omamul.data.model.CountEntity
import com.tkw.omamul.ui.viewmodel.WaterViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
val ViewModelFactory = object: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()

        val conf = RealmConfiguration.create(setOf(CountEntity::class))
        val realm = Realm.open(conf)

        return when(modelClass) {
            WaterViewModel::class.java -> WaterViewModel(
                MainRepositoryImpl(
                    LocalDataSource(CountDaoImpl(realm)), null)
                , handle)
            else -> throw IllegalArgumentException("Unknown Class")
        } as T
    }
}