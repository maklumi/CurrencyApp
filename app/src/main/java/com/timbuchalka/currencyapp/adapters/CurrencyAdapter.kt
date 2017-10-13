package com.timbuchalka.currencyapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.timbuchalka.currencyapp.Constants
import com.timbuchalka.currencyapp.R

/**
 * Created by HomePC on 13/10/2017.
 */

class CurrencyAdapter(private val ctx: Context) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewHolder = ViewHolder()
        var view : View
        if (convertView == null){
            val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.currency_item, null)
            viewHolder.textView = view.findViewById(R.id.currenty_text)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = convertView.tag as ViewHolder
            viewHolder.textView?.text = Constants.CURRENCY_NAMES[position] + "(" + Constants.CURRENCY_CODES[position] + ")"
        }
        return view
    }

    override fun getItem(position: Int): Any = Constants.CURRENCY_CODES[position]

    override fun getItemId(position: Int): Long = 0

    override fun getCount(): Int = Constants.CURRENCY_CODE_SIZE

    inner class ViewHolder {
        var textView: TextView? = null
    }

}