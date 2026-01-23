package com.example.temankb.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.temankb.R
import com.example.temankb.activity_detail_kb
import com.example.temankb.model.KBItem

class KBAdapter(
    private val list: List<KBItem>
) : RecyclerView.Adapter<KBAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nama: TextView = view.findViewById(R.id.kb1)
        val deskripsi: TextView = view.findViewById(R.id.tvkb1)
        val btnDetail: Button = view.findViewById(R.id.btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hasilrekomen, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.nama.text = item.nama
        holder.deskripsi.text = item.deskripsi

        holder.btnDetail.setOnClickListener {
            val context = it.context

            // === MAPPING NAMA KB BIAR COCOK KE DETAIL ===
            val jenisKB = when {
                item.nama.contains("IUD", true) || item.nama.contains("AKDR", true) -> "IUD"
                item.nama.contains("Implan", true) -> "IMPLAN"
                item.nama.contains("Pil", true) -> "PIL"
                item.nama.contains("Suntik", true) -> "SUNTIK"
                item.nama.contains("Kondom", true) -> "KONDOM"
                else -> ""
            }

            val intent = Intent(context, activity_detail_kb::class.java)
            intent.putExtra("jenis_kb", jenisKB)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}
