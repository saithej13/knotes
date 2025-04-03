package com.vst.knotes.MVVM.View.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.vst.knotes.MVVM.Model.MenuItem
import com.vst.knotes.MVVM.View.BaseActivity
import com.vst.knotes.MVVM.View.BaseFragment
import com.vst.knotes.MVVM.View.DyamicUi.DynamicFragment
import com.vst.knotes.MVVM.View.Sale.addsale
import com.vst.knotes.MVVM.View.category.Category
import com.vst.knotes.MVVM.View.price.price
import com.vst.knotes.MVVM.View.product.product
import com.vst.knotes.RoomDataBase.SQLite.DatabaseHelper
import com.vst.knotes.databinding.Menu2Binding
import com.vst.knotes.databinding.MenuBinding

class menu : BaseFragment<Menu2Binding>() {
    private lateinit var MenuItemList: MutableList<MenuItem>
    override fun inflateBinding(inflater: LayoutInflater, parent: ViewGroup?): Menu2Binding {
        return Menu2Binding.inflate(inflater, parent, false)
    }
    override fun provideYourFragmentView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
        viewLifecycleOwner: LifecycleOwner
    ): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.category.setOnClickListener {
//            (activity as? BaseActivity)?.replaceFragment(Category::class.java.newInstance() as Fragment)
//        }

    }
    fun loadData(){
        MenuItemList = ArrayList()
        try {
            DatabaseHelper.openDatabase()?.let { sqLiteDatabase ->
                val query = "SELECT menuid,menucode,menuname,menuurl,menudescription,menucategory,display_order,isactive,storeid,zoneid FROM tblmenu"
                sqLiteDatabase.rawQuery(query, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val menuItem = MenuItem()
                            menuItem.Menuid = cursor.getString(0)
                            menuItem.Menucode = cursor.getString(1)
                            menuItem.Menuname = cursor.getString(2)
                            menuItem.menuurl = cursor.getString(3)
                            menuItem.Menudescription = cursor.getString(4)
                            menuItem.Menucategory = cursor.getInt(5)
                            menuItem.Display_Order = cursor.getInt(6)
                            menuItem.isactive = cursor.getInt(7)
                            menuItem.storeid = cursor.getInt(8)
                            menuItem.zoneid = cursor.getInt(9)
                            MenuItemList.add(menuItem)
                        } while (cursor.moveToNext());
                    } else {
                        Log.e("SQLITE", "tblmenu is EMPTY!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SQLITE", "ERROR: ${e.localizedMessage}", e)
        } finally {
            DatabaseHelper.closeDatabase()
            binding.recyclerview.layoutManager = LinearLayoutManager(context)
            if (!MenuItemList.isNullOrEmpty()) {
                binding.recyclerview.adapter = context?.let { MenuAdapter(it, MenuItemList) }
                (binding.recyclerview.adapter as MenuAdapter).setClickListener(object : MenuAdapter.ItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        // Handle item click here
                        val menuItem = MenuItemList[position].Menucode
                        // You can start a new fragment or activity here
                        if(menuItem.equals("MENU_ORDER")){
                            (activity as? BaseActivity)?.replaceFragment(Category::class.java.newInstance() as Fragment)
                        }
                        else if(menuItem.equals("MENU_ONLINE_ORDER")){
                            (activity as? BaseActivity)?.replaceFragment(product::class.java.newInstance() as Fragment)
                        }
                        else if(menuItem.equals("MENU_KOT")){
                            (activity as? BaseActivity)?.replaceFragment(price::class.java.newInstance() as Fragment)
                        }
                        else if(menuItem.equals("MENU_CASH_FLOW")){
                            (activity as? BaseActivity)?.replaceFragment(DynamicFragment::class.java.newInstance() as Fragment)
                        }
                        else if(menuItem.equals("MENU_EXPENSE")){
                            (activity as? BaseActivity)?.replaceFragment(addsale::class.java.newInstance() as Fragment)
                        }

//                        (activity as? BaseActivity)?.replaceFragment(DetailMenu::class.java.newInstance() as Fragment)
                    }
                })
                binding.recyclerview.visibility = View.VISIBLE
                binding.nodata.visibility = View.GONE
            } else {
                binding.recyclerview.visibility = View.GONE
                // You can also show a message to the user here
                binding.nodata.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}