package com.tkw.omamul.ui.water.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.tkw.omamul.MainApplication
import com.tkw.omamul.R
import com.tkw.omamul.databinding.FragmentWaterBinding

class WaterFragment: Fragment() {
    private lateinit var binding: FragmentWaterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("life", "WaterFragment onCreateView")
        binding = FragmentWaterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return menuItem.onNavDestinationSelected(findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

//        if(MainApplication.sharedPref?.getBoolean("test", false) == false) {
//            MainApplication.sharedPref?.edit()?.putBoolean("test", true)?.apply()
////            findNavController().navigate(R.id.FirstStartFragment, null, navOptions)
//            nextFragment(R.id.FirstStartFragment)
//        }
    }

    private fun nextFragment(action: NavDirections) {
        findNavController().navigate(action)
    }

    private fun nextFragment(fragmentId: Int) {
        findNavController().navigate(fragmentId)
    }
}