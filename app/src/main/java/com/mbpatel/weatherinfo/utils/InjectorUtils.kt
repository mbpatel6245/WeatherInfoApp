package com.mbpatel.weatherinfo.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.mbpatel.weatherinfo.db.AppDatabase
import com.mbpatel.weatherinfo.db.HistoryRepository
import com.mbpatel.weatherinfo.ui.history.HistoryViewModelFactory

//import com.mbpatel.weatherinfo.api.WeatherRepository
//import com.mbpatel.weatherinfo.db.AppDatabase
//import com.mbpatel.weatherinfo.db.BookmarkRepository
//import com.mbpatel.weatherinfo.ui.city.CityViewModelFactory
//import com.mbpatel.weatherinfo.ui.home.HomeViewModelFactory
//import com.mbpatel.weatherinfo.ui.map.MapViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    // var itemCode: String=""

  /*  private fun getCityRepository(context: Context): WeatherRepository {
        return WeatherRepository.getInstance()
    }

    private fun getBookmarkRepository(context: Context): BookmarkRepository {
        return BookmarkRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).bookmarkDao()
        )
    }
*/
    private fun getHomeRepository(context: Context): HistoryRepository {
        return HistoryRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext).historyDao()
        )
    }
/*
    fun provideCityViewModelFactory(fragment: Fragment, mLatLng: LatLng): CityViewModelFactory {
        return CityViewModelFactory(
            getCityRepository(fragment.requireContext()), mLatLng
        )
    }

    fun provideMapViewModelFactory(fragment: Fragment): MapViewModelFactory {
        return MapViewModelFactory(getBookmarkRepository(fragment.requireContext()))
    }

   */ fun provideHomeListViewModelFactory(fragment: Fragment): HistoryViewModelFactory {
        return HistoryViewModelFactory(
            getHomeRepository(fragment.requireContext()),
            fragment
        )
    }
    /* fun provideAddProductsViewModelFactory(
         context: Context, productId: String
     ): AddProductsViewModelFactory {
         return AddProductsViewModelFactory(
             getProductsRepository(context), productId,
             context.applicationContext as Application
         )
     }*/

    /* fun provideAddCustomerViewModelFactory(context: Context): CustomerViewModelFactory {
         return CustomerViewModelFactory(
             getCustomersRepository(context),
             context.applicationContext as Application
         )
     }*/
    /*fun provideGalleryViewModelFactory(): GalleryViewModelFactory {
        val repository = UnsplashRepository(UnsplashService.create())
        return GalleryViewModelFactory(repository)
    }*/
}