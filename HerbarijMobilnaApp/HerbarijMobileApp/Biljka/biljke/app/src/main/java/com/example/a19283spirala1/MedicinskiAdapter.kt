package com.example.a19283spirala1

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicinskiAdapter(
    var biljke: List<Biljka>,
    private val biljkaRepository: BiljkaRepository,
    private val onClickListener: (Biljka) -> Unit
) : RecyclerView.Adapter<MedicinskiAdapter.BiljkaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BiljkaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medicinskipogled, parent, false)
        return BiljkaViewHolder(view)
    }

    override fun getItemCount(): Int {
        return biljke.size
    }

    override fun onBindViewHolder(holder: BiljkaViewHolder, position: Int) {
        holder.naziv.text = biljke[position].naziv
        holder.upozorenje.text = biljke[position].medicinskoUpozorenje
        holder.korist1.text = biljke[position].medicinskeKoristi[0].opis

        if (position < biljke.size && biljke[position].medicinskeKoristi.size > 1) {
            holder.korist2.text = biljke[position].medicinskeKoristi[1].opis
        } else {
            holder.korist2.text = ""
        }

        if (position < biljke.size && biljke[position].medicinskeKoristi.size > 2) {
            holder.korist3.text = biljke[position].medicinskeKoristi[2].opis
        } else {
            holder.korist3.text = ""
        }

        val context: Context = holder.slika.context

        CoroutineScope(Dispatchers.Main).launch {
            val biljkaBitmap = biljke[position].id?.let {
                biljkaRepository.getImage(it)
            }
            if (biljkaBitmap != null) {
                holder.slika.setImageBitmap(biljkaBitmap.bitmap)
            } else {
                val bitmap = TrefleDAO(context).getImage(biljke[position], biljkaRepository)
                holder.slika.setImageBitmap(bitmap)
            }
        }
        //ovo postavi sliku za biljku na poziciji position proslijedi se samo
        //ovoj getImage fun iz DAO klase


        holder.itemView.setOnClickListener {
            onClickListener(biljke[position])
            filter(biljke[position])
        }
    }

    fun filter(biljka: Biljka) {
        val filteredList = mutableListOf<Biljka>()
        for (index in dajBiljke().indices) {
            val otherBiljka = dajBiljke()[index]

            if (biljka.medicinskeKoristi.any { it in otherBiljka.medicinskeKoristi }) {
                filteredList.add(otherBiljka)
            }
        }
        updateBiljke(filteredList)
    }

    fun updateBiljke(biljke: List<Biljka>) {
        this.biljke = biljke
        notifyDataSetChanged()
    }

    inner class BiljkaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val upozorenje: TextView = itemView.findViewById(R.id.upozorenjeItem)
        val slika: ImageView = itemView.findViewById(R.id.slikaItem)
        val naziv: TextView = itemView.findViewById(R.id.nazivItem)
        val korist1: TextView = itemView.findViewById(R.id.korist1Item)
        val korist2: TextView = itemView.findViewById(R.id.korist2Item)
        val korist3: TextView = itemView.findViewById(R.id.korist3Item)
    }
}
