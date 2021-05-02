# 背景
因为公司开发新项目，之前用的框架比较陈旧，就重新搭建了一套。导航组件选择了Navigation，毕竟jetpack组件是google的正规军，但是用过之后才发现问题也不少，比较常见的是跳转页面返回时会重新走一遍生命周期，这个是因为在FragmentNavigator中使用了FragmentTransaction.replace方法导致的。

## 修改页面跳转方式（replace -> add hide）
1. 自定义FragmentNavigator
这里参考了网上找到的一些实现方式，大部分实现方式相同，但试用过程中发现会有页面重叠显示的问题，后面通过实践修复了这个问题，代码如下：
```
//      fix 2: replace换成show和hide
        Fragment fragment = mFragmentManager.getPrimaryNavigationFragment();
        if (fragment != null) {
            ft.hide(fragment);
        }
        Fragment frag;
        String tag = String.valueOf(destination.getId());
        frag = instantiateFragment(mContext, mFragmentManager,
                className, args);
        frag.setArguments(args);
        ft.add(mContainerId, frag, tag);
        ft.setPrimaryNavigationFragment(frag);
```
2. 自定义FixNavHostFragment
网上大部分的实现方式都是需要在使用navigation组件的容器activity中用代码设置graph，我这里给出的方法是通过自定义FixNavHostFragment的方式省略掉这个步骤，使用时只需要将FragmentContainerView里的 android:name字段赋值成自定义的FixNavHostFragment即可：
```
    <androidx.fragment.app.FragmentContainerView
        android:id="@+id/host_fragment"
        android:name="com.example.common.navi.FixNavHostFragment"//替换成自定义NavHostFragment
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:navGraph="@navigation/navi_main"
        app:defaultNavHost="true" />

```
但是目前这种实现方式还是有一些问题，如果跳转的过程中加入进出场动画时，页面会出现闪烁的情况，目前我的work around方法是屏蔽退场动画，如果有哪位大佬有更好的方法，还望不吝赐教！
## 多模块全局跳转方案
### Demo中Module依赖关系:
![](https://upload-images.jianshu.io/upload_images/3383786-49a4b764f4fc9241.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

多模块route网上有很多解决方案，ARouter、WMRouter等，这些主要是针对于Activity的跳转，不过ARouter从1.1.1版本也开始支持了Fragment跳转。
Navigation对于多模块之间的Fragment跳转没有做过多介绍，我目前想到的方式是通过在公共模块中添加graph中fragment的id，来达到此Fragment全局可见的效果：
![image.png](https://upload-images.jianshu.io/upload_images/3383786-c67f9c6aa24ee5bd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 跳转封装
```
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
```

使用方式：
直接调用 ` nav(R.id.fragmentB)`
传参调用 ` nav(R.id.fragmentB, bundleOf("from" to "From fragment A"))`

Demo地址：https://github.com/xinayida/FixNavigationDemo
