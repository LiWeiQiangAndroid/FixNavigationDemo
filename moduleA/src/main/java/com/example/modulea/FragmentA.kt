package com.example.modulea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.common.navi.nav

class FragmentA : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.a_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("from")?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        view.findViewById<View>(R.id.btn_nav).setOnClickListener {
            nav(R.id.fragmentB, bundleOf("from" to "From fragment A"))
        }
    }
}