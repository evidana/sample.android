package com.evidana.sample.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    data class Product(
            val name: String,
            val sku: String,
            val price: Double,
            val quantity: Int,
            val currency: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        // initial products

        val guitar = Product("guitar 1", "SKU_123", 199.99, 1, "USD")
        val cymbal = Product("cymbal 1", "SKU_124", 125.99, 1, "USD")

        var guitarBundle = Bundle()
        guitarBundle.putString(FirebaseAnalytics.Param.ITEM_ID, guitar.name)
        guitarBundle.putDouble(FirebaseAnalytics.Param.PRICE, guitar.price)
        guitarBundle.putInt(FirebaseAnalytics.Param.QUANTITY, guitar.quantity)
        guitarBundle.putString(FirebaseAnalytics.Param.CURRENCY, guitar.currency)

        var cymbalBundle = Bundle()
        cymbalBundle.putString(FirebaseAnalytics.Param.ITEM_ID, cymbal.name)
        cymbalBundle.putDouble(FirebaseAnalytics.Param.PRICE, cymbal.price)
        cymbalBundle.putInt(FirebaseAnalytics.Param.QUANTITY, cymbal.quantity)
        cymbalBundle.putString(FirebaseAnalytics.Param.CURRENCY, cymbal.currency)

        var cartValue = guitar.price + cymbal.price
        var transactionID = (0..10).random()

        firebaseAnalytics = Firebase.analytics

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        firebaseAnalytics.logEvent("share_image") {
            param("image_name", "test")
            param("full_text", "test1")
        }

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "Home Screen")
        }

        var viewItemBundle = Bundle()
        viewItemBundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
        viewItemBundle.putDouble(FirebaseAnalytics.Param.VALUE, cartValue)
        viewItemBundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, arrayListOf(guitarBundle, cymbalBundle))
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, viewItemBundle)

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, viewItemBundle)

        // remember that begin_checkout always sets checkstep of 1
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, viewItemBundle)

        // dev requirements for mapping checkout_step2 to the actual in app event is important
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.CHECKOUT_PROGRESS) {
            param(FirebaseAnalytics.Param.CURRENCY, "USD")
            param(FirebaseAnalytics.Param.VALUE, cartValue)
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(guitarBundle, cymbalBundle))
            param(FirebaseAnalytics.Param.CHECKOUT_STEP, 2)
        }
        // this is just for ga4
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_SHIPPING_INFO, viewItemBundle)


        // dev requirements for mapping checkout_step2 to the actual in app event is important
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.CHECKOUT_PROGRESS) {
            param(FirebaseAnalytics.Param.CURRENCY, "USD")
            param(FirebaseAnalytics.Param.VALUE, cartValue)
            param(FirebaseAnalytics.Param.ITEMS, arrayOf(guitarBundle, cymbalBundle))
            param(FirebaseAnalytics.Param.CHECKOUT_STEP, 3)
        }
        // this is just for ga4
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_PAYMENT_INFO, viewItemBundle)

        // the event HAS TO BE ecommerce_purchase for ga360 reports to work
        // ok that it is deprecated
        // configure this one for GTM
        // add a rule to drop value
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, viewItemBundle)

        // the event HAS TO BE purchase for ga4 to work
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, viewItemBundle)

    }
}