package com.app.tnbbs.view.admin.kelolatiket.adapter


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.app.tnbbs.data.model.Histori
import com.app.tnbbs.databinding.CustomListHistoriTiketBinding
import com.bumptech.glide.Glide

class KelolaTiketAdapter(
    private val context: Context,
    private val dataList: MutableList<Histori>
) : RecyclerView.Adapter<KelolaTiketAdapter.ViewHolder>() {

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
            bundle.putString("idBooking", data.idBooking)
            bundle.putString("idWisata", data.idWisata)
            bundle.putString("namaPemesan", data.namaPemesan)
            bundle.putString("noHp", data.noHp)
            bundle.putString("namaWisata", data.namaWisata)
            bundle.putString("kewarganegaraan", data.selectedNationality)
            bundle.putString("jumlahTiket", data.jumlahOrang)
            bundle.putString("tanggalPemesanan", data.timestamp)
            bundle.putString("statusPemesanan", data.statusPesanan)
            bundle.putString("lihatBukti", data.buktiPembayaran)
            bundle.putString("days", data.days.toString())
            Navigation.findNavController(it).navigate(com.app.tnbbs.R.id.action_kelolaTiketAdminFragment_to_konfirmasiTiketFragment, bundle)
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
