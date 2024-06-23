package com.app.tnbbs.view.admin.histori.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.tnbbs.data.model.Histori
import com.app.tnbbs.databinding.CustomListHistoriTiketBinding
import com.bumptech.glide.Glide

class TiketAdapterAdmin(
    private val context: Context,
    private val dataList: MutableList<Histori>
) : RecyclerView.Adapter<TiketAdapterAdmin.ViewHolder>() {

    class ViewHolder(val binding: CustomListHistoriTiketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CustomListHistoriTiketBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        Glide.with(context)
            .load(data.imageUrl)
            .into(holder.binding.ivImage)

        holder.binding.tvNamaWisata.text = data.namaWisata
        holder.binding.cardView.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("namaPemesan", data.namaPemesan)
            bundle.putString("noHp", data.noHp)
            bundle.putString("selectedNationality", data.selectedNationality)
            bundle.putString("jumlahOrang", data.jumlahOrang)
            bundle.putString("generalPrice", data.generalPrice)
            bundle.putString("days", data.days?.toString())
            bundle.putString("totalharga", data.totalharga?.toString())
            bundle.putString("totalBiayaMasuk", data.totalBiayaMasuk?.toString())

            Navigation.findNavController(it).navigate(com.app.tnbbs.R.id.action_homeTiketAdminFragment_to_detailTiketAdminFragment, bundle)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newData: List<Histori>) {
        dataList.apply {
            clear()
            addAll(newData)
        }
        notifyDataSetChanged()
    }

}
