package com.app.tnbbs.view.user.etiket.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.tnbbs.data.model.Wisata
import com.app.tnbbs.databinding.CustomListHistoriTiketBinding
import com.app.tnbbs.databinding.CustomListInformasiWisataBinding
import com.bumptech.glide.Glide

class EtiketAdapterUser(
    private val context: Context,
    private val dataList: MutableList<Wisata>
) : RecyclerView.Adapter<EtiketAdapterUser.ViewHolder>() {

    class ViewHolder(val binding: CustomListHistoriTiketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CustomListHistoriTiketBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        Glide.with(holder.itemView.context)
            .load(data.imageUrl)
            .into(holder.binding.ivImage)
        holder.binding.tvNamaWisata.text = data.namaWisata

        holder.binding.cardView.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("idWisata", data.idWisata)
            Navigation.findNavController(it).navigate(com.app.tnbbs.R.id.action_daftarWisataFragment_to_detailWisataFragment, bundle)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<Wisata>) {
        dataList.apply {
            clear()
            addAll(newData)
        }
        notifyDataSetChanged()
    }

}