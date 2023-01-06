/*
 * Copyright (c) 2022.  Bambora ( https://bambora.com/ )
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.bambora.android.java.demoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.Locale

internal class AdditionalFieldAdapter(private val additionalFieldsMap: Map<String, String>) :
    RecyclerView.Adapter<AdditionalFieldAdapter.AdditionalFieldViewHolder>() {

    internal inner class AdditionalFieldViewHolder(view: View) : ViewHolder(view) {
        var fieldTitleTextView: TextView = view.findViewById(R.id.additionalFieldTextView)
        var fieldDataTextView: TextView = view.findViewById(R.id.additionalFieldDataTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdditionalFieldViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_additional_field, parent, false)
        return AdditionalFieldViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: AdditionalFieldViewHolder, position: Int) {
        val additionalFieldsList: List<Pair<String, String>> = additionalFieldsMap.toList()

        val item = additionalFieldsList[position]
        val capitalizedItemName = item.first.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }

        holder.fieldTitleTextView.text = "$capitalizedItemName:"
        holder.fieldDataTextView.text = item.second
    }
    override fun getItemCount(): Int {
        return additionalFieldsMap.size
    }
}
