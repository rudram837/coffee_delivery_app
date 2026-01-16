package com.example.myapplication.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Model.FavoriteItem
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FavouriteAdapter(private val favouriteList: MutableList<FavoriteItem>) :
    RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_favourite, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val item = favouriteList[position]
        holder.nameTextView.text = item.title
        holder.priceTextView.text = "₹${item.price}"  // Bind the price


        if (item.picUrl.isNotEmpty()) {  // Check if picUrl list is not empty
            Glide.with(holder.itemView.context)
                .load(item.picUrl[0])  // Load the first URL from the picUrl list
                .apply(RequestOptions().centerCrop())  // Apply CenterCrop transformation
                .into(holder.imageView)  // Target ImageView
        }


        holder.removeFavBtn.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val favRef = FirebaseDatabase.getInstance().getReference("users/$userId/favouriteItems")


                favRef.orderByChild("title").equalTo(item.title).get().addOnSuccessListener { snapshot ->
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (position >= 0 && position < favouriteList.size) {
                                    favouriteList.removeAt(position)
                                    notifyItemRemoved(position)
                                } else {
                                    Log.e("Adapter", "Invalid position: $position")
                                }
                            } else {
                                Log.e("Firebase", "Failed to remove item from Firebase")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return favouriteList.size
    }

    class FavouriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.titleFav)
        val priceTextView: TextView = itemView.findViewById(R.id.priceFav)  // Add price TextView
        val imageView: ImageView = itemView.findViewById(R.id.picFav)
        val removeFavBtn: ImageView = itemView.findViewById(R.id.removeFavBtn)  // Add remove button reference
    }
}
