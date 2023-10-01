package com.pilot.noiapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RepoProvidesModule {

   /* @Provides
    @Singleton
    fun provideSharedPrefRepo(sharedPreferences: SharedPreferences): SharedPreferencesRepository {
        return SharedPreferencesRepositoryImpl(sharedPreferences)
    }*/

    @Provides
    @Singleton
    fun provideSecureStorageProvider(
        @ApplicationContext context: Context
    ): SharedPreferences {
        val alias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            "enc_sp_noiapp",
            alias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

}