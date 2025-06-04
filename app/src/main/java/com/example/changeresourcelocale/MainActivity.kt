package com.example.changeresourcelocale

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.changeresourcelocale.databinding.ActivityMainBinding
import java.util.Locale

/**
 * アプリから言語を切り替える実装の壁打ち
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {

            // resources.configurationを使った方法
            changeLocaleWithConfiguration()

            // AppCompatDelegate.getApplicationLocales()を使った方法
//            changeLocaleWithAppCompatDelegate()
        }
    }

    /**
     * resources.configurationを使用した言語切り替え
     * 画面が暗転しないが、一部APIが非推奨
     * こちらの方が体感パフォーマンスが良いように感じる
     */
    private fun changeLocaleWithConfiguration() {
        // 現在のlocaleを取得
        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API24以降
            resources.configuration.locales.get(0)
        } else {
            // API23以前
            // Deprecated
            resources.configuration.locale
        }

        // 日本語なら英語、そうでなければ日本語を参照する
        val newLocale = if (currentLocale.language == Locale.JAPANESE.language) {
            Locale.ENGLISH
        } else {
            Locale.JAPANESE
        }

        // アプリ全体のlocaleを更新
        val config = Configuration(resources.configuration)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(newLocale))
        } else {
            config.setLocale(newLocale)
        }
        // Deprecated
        resources.updateConfiguration(config, resources.displayMetrics)

        // Activityを再作成
        recreate()
    }

    /**
     * Per-app language preferences (アプリ単位の言語設定)を使用した言語切り替え
     * setApplicationLocalesの特性？で画面が一瞬暗転する
     * たまに切り替わらない時もある
     */
    private fun changeLocaleWithAppCompatDelegate() {
        // 現在のlocaleを取得
        val currentAppLocale = AppCompatDelegate.getApplicationLocales().get(0)
            ?: Locale.getDefault()

        // 日本語なら英語、そうでなければ日本語を参照する
        val newLocale = if (currentAppLocale.language == Locale.JAPANESE.language) {
            Locale.ENGLISH
        } else {
            Locale.JAPANESE
        }

        // アプリ全体のlocaleを更新
        val localeList = LocaleListCompat.forLanguageTags(newLocale.language)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}
