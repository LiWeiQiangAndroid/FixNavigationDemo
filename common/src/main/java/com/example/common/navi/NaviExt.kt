package com.example.common.navi

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.common.R

fun AppCompatActivity.nav(@IdRes resId: Int, @Nullable args: Bundle?, pInclusive: Boolean? = false) {
    nav()?.navigate(resId, args, navOptions {
        launchSingleTop = false
        anim {
            enter = R.anim.slide_in_right
//            exit = R.anim.slide_out_left//Fixme 有exit动画时会出现闪屏问题，暂时没有找到解决方案
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    })
}

fun Fragment.nav(@IdRes resId: Int, @Nullable args: Bundle? = null, withAnim: Boolean? = true) {
    nav().navigate(resId, args, navOptions {
        launchSingleTop = false
        if (withAnim == true) {
            anim {
                enter = R.anim.slide_in_right
//                exit = R.anim.slide_out_left//Fixme 有exit动画时会出现闪屏问题，暂时没有找到解决方案
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }
//        popUpTo(id) { inclusive = pInclusive!! }
    })
}

private fun AppCompatActivity.nav(): NavController? {
    var controller: NavController? = null
    supportFragmentManager.findFragmentById(R.id.host_fragment)?.apply {
        controller = FixNavHostFragment.findNavController(this)
        if (controller == null) {
            controller = findNavController()
        }
    }
    return controller
}


private fun Fragment.nav(): NavController {
    return NavHostFragment.findNavController(this)
}

fun Fragment.navBack() {
    nav().popBackStack()
}