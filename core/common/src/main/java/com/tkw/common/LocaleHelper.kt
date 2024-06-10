package com.tkw.common

import android.annotation.TargetApi
import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.*

/**
 * language 저장 후 호출
 */
object LocaleHelper {
    /**
     * appcompat 1.6.0 버전부터 set,getApplicationLocales 추가.
     * API 33부터 per-app language preferences 자동 저장.
     * 32 이하는 Manifest에 AppLocalesMetadataHolderService 서비스 등록을 통해
     * 프레임워크에서 저장 처리하도록 하거나, onCreate 이전에 직접 저장한 값을 가져와 세팅해야 함.
     *
     * API 32 이하는 반드시 AppCompatActivity 컨텍스트에서 호출해야 변경이 적용된다.
     *
     * hilt 라이브러리 사용 시 Fragment에서 context는 FragmentContextWrapper를 통해 가져오는데,
     * 이 context는 activity의 context가 아니므로 check에서 걸림.
     * 따라서 Activity로 파라미터를 받도록 함.
     */
    fun setApplicationLocales(context: Activity, storedLang: String?) {
        val localeCompat = if(storedLang.isNullOrEmpty()) {
            LocaleList.getDefault()
        } else {
            LocaleList.forLanguageTags(storedLang)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .applicationLocales = localeCompat
        } else {
            check(context is AppCompatActivity) { "This method must be called by AppCompatActivity." }
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.wrap(localeCompat)
            )
        }
    }

    fun getApplicationLocales(context: Activity): LocaleListCompat {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LocaleListCompat.wrap(
                context.getSystemService(LocaleManager::class.java)
                    .applicationLocales
            )
        } else {
            check(context is AppCompatActivity) { "This method must be called by AppCompatActivity." }
            AppCompatDelegate.getApplicationLocales()
        }
    }

    /**
     * application 또는 activity단의 context를 preference로 저장된 언어로 업데이트하여 리턴.
     *
     * super.attachBaseContext(LocaleHelper.getUpdateContext(base, lang)) 형태로 attachBaseContext(base: Context) 메소드 오버라이딩하여 사용한다.
     */
    fun getUpdateContext(context: Context, lang: String?): Context {
        return if(!lang.isNullOrEmpty()) {      //preference에 저장된 언어가 있으면 해당 언어로 context 변경
            updateBaseLanguage(context, lang)
        } else {                            //저장된 언어가 없으면 context가 가지고 있는 device 언어로 context 변경
            updateDeviceLanguage(context)
        }
    }

    private fun updateDeviceLanguage(context: Context): Context {
        val config = context.resources.configuration
        val locale = getLocale(config)
        return setLocale(context, locale.language)
    }

    private fun updateBaseLanguage(context: Context, lang: String): Context {
        return setLocale(context, lang)
    }

    /**
     * 2020.10.28 tkw
     * androidx appcompat:1.1.0 이슈
     * Nougat 아래 버전 (테스트 기기 : Galaxy S5, Marshmallow)에서 configuration 변경 시 nightmode 처리 중 로케일 설정이 안되는 현상
     * 아래 메소드 재정의하여 해결.
     *
     * activity에서 applyOverrideConfiguration 메소드 재정의.
     * super.applyOverrideConfiguration(LocaleHelper.applyConfigNightMode(baseContext, overrideConfiguration)) 형태로 사용한다.
     *
     * 출처 :
     * https://stackoverflow.com/questions/55265834/change-locale-not-work-after-migrate-to-androidx/58004553#58004553
     * https://qastack.kr/programming/4985805/set-locale-programmatically
     */
    fun applyConfigNightMode(baseContext: Context, overrideConfiguration: Configuration?): Configuration? {
        overrideConfiguration?.let {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(baseContext.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        return overrideConfiguration
    }

    fun <T> restartApplication(context: Context, restartActivity: Class<T>) {
        if(context is Activity) {
            context.finishAffinity()        //root 액티비티까지 종료
            val intent = Intent(context, restartActivity)
            context.startActivity(intent)
        }

        System.runFinalization()
        //현재 동작중인 스레드의 종료를 기다림.
        // 언어설정 preference 저장이 apply면 비동기, commit이면 동기인데 apply로 저장하는 경우 저장이 안된 채로 재시작됨.
        // 따라서 commit으로 저장하여 저장안되는 현상 해결

        System.exit(0)
    }

    private fun setLocale(context: Context, language: String): Context {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(
                context,
                language
            )
        }

        return updateResourcesLegacy(
            context,
            language
        )
    }

    private fun getLocale(config: Configuration): Locale {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return config.locales[0]
        } else {
            return config.locale
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    @SuppressWarnings("deprecation")
    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources

        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }
}