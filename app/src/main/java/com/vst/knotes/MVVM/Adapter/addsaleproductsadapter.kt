package com.vst.knotes.MVVM.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import com.squareup.picasso.Picasso
import com.vst.knotes.MVVM.Model.productModel
import com.vst.knotes.MVVM.View.BaseActivity.Companion.cartProducts
import com.vst.knotes.R
import com.vst.knotes.databinding.SaleProductCellBinding
import java.io.File

class addsaleproductsadapter(
    private val context: Context,
    private val data: ArrayList<productModel>?
) : BaseAdapter(), Filterable {

    private var products: ArrayList<productModel> = data ?: ArrayList()
    private var filterProducts: ArrayList<productModel> = ArrayList(products)

    private var mClickListener: ItemClickListener? = null

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val filterString = constraint.toString().lowercase().trim()
                filterProducts.clear()

                if (filterString.isEmpty()) {
                    filterProducts.addAll(products)
                } else {
                    products.forEach {
                        if (it.productname?.lowercase()?.contains(filterString) == true) {
                            filterProducts.add(it)
                        }
                    }
                }

                results.values = filterProducts
                results.count = filterProducts.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    override fun getCount(): Int {
        return filterProducts.size
    }

    override fun getItem(position: Int): Any {
        return filterProducts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: SaleProductCellBinding
        if (convertView == null) {
            binding = SaleProductCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as SaleProductCellBinding
        }

        val product = filterProducts[position]
        binding.productName.text = product.productname
        binding.uom.text = product.uom.toString()
        binding.noOfItemsText.setText(product.quantity.toString())
        binding.decreaseItems.visibility = View.GONE
        binding.noOfItemsText.visibility = View.GONE
        val existingProductIndex = cartProducts.indexOfFirst { it.productid == product.productid }
        if (existingProductIndex != -1) {
            val updatedProduct = cartProducts[existingProductIndex]
            if (updatedProduct.quantity > 0) {
                binding.decreaseItems.visibility = View.VISIBLE
                binding.noOfItemsText.visibility = View.VISIBLE
                binding.noOfItemsText.setText(updatedProduct.quantity.toString())
                binding.increaseItems.setText("+")
            }
        } else {
            binding.increaseItems.setText("Add")
        }

        binding.increaseItems.setOnClickListener {
            val existingProductIndex = cartProducts.indexOfFirst { it.productid == product.productid }
            if (existingProductIndex != -1) {
                val updatedProduct = cartProducts[existingProductIndex]
                updatedProduct.quantity++
                cartProducts[existingProductIndex] = updatedProduct
                binding.noOfItemsText.setText(updatedProduct.quantity.toString())
            } else {
                product.quantity += 1
                cartProducts.add(product)
                binding.noOfItemsText.setText(product.quantity.toString())
            }
            binding.decreaseItems.visibility = View.VISIBLE
            binding.noOfItemsText.visibility = View.VISIBLE
            binding.increaseItems.setText("+")
            notifyDataSetChanged() // Notify adapter to update the view
        }

        binding.decreaseItems.setOnClickListener {
            val existingProductIndex = cartProducts.indexOfFirst { it.productid == product.productid }
            if (existingProductIndex != -1) {
                val updatedProduct = cartProducts[existingProductIndex]
                if (updatedProduct.quantity > 1) {
                    updatedProduct.quantity--
                    cartProducts[existingProductIndex] = updatedProduct
                    binding.noOfItemsText.setText(updatedProduct.quantity.toString())
                }
                else {
                    updatedProduct.quantity =0.0
                    cartProducts.remove(updatedProduct)
                    binding.noOfItemsText.setText("0")
                    binding.decreaseItems.visibility = View.GONE
                    binding.noOfItemsText.visibility = View.GONE
                }
            }

            notifyDataSetChanged()
        }

        if (!product.productimageurl.isNullOrEmpty()) {
            Picasso.get()
                .load(product.productimageurl)
                .placeholder(R.drawable.fab)
                .error(R.drawable.fab)
                .into(binding.productImg)
        } else {
            binding.productImg.setImageResource(R.drawable.fab)
        }

        binding.root.setOnClickListener {
//            mClickListener?.onItemClick(it, position)
        }

        return binding.root
    }
}
