package com.tkw.ui.chart

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DayMarker

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeekMarker

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MonthMarker

@Module
@InstallIn(ViewComponent::class)
object ChartMarkerModule {

    @DayMarker
    @Provides
    fun provideDayMarker(@ApplicationContext appContext: Context): MarkerView {
        return DayMarkerView(appContext)
    }

    @WeekMarker
    @Provides
    fun provideWeekMarker(@ApplicationContext appContext: Context): MarkerView {
        return WeekMarkerView(appContext)
    }

    @MonthMarker
    @Provides
    fun provideMonthMarker(@ApplicationContext appContext: Context): MarkerView {
        return MonthMarkerView(appContext)
    }
}