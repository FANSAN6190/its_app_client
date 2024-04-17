import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.civiceye.R
import com.example.civiceye.ui.home.WhistleData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WhistleDataAdapter(private val whistleDataList: List<WhistleData>) : RecyclerView.Adapter<WhistleDataAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val whistleCategorySubcategory: TextView = view.findViewById(R.id.whistle_category_subcategory)
        val whistleTime: TextView = view.findViewById(R.id.whistle_time)
        val whistleUserRating: TextView = view.findViewById(R.id.whistle_user_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.whistlelist_layout, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val whistleData = whistleDataList[position]
        holder.whistleCategorySubcategory.text = "${whistleData.category} - ${whistleData.subcategory}"
        val timestamp = whistleData.timestamp.toLong()
        val date = Date(timestamp) // Assume timestamp is in milliseconds
        val format = SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.getDefault())
        holder.whistleTime.text = format.format(date)
        holder.whistleUserRating.text = "User Rating: ${whistleData.userRating}"
    }

    override fun getItemCount() = whistleDataList.size
}