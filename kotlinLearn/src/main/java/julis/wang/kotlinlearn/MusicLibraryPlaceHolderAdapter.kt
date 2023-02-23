package julis.wang.kotlinlearn

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import julis.wang.kotlinlearn.databinding.LayoutItemMusicPlaceholderBinding
import julis.wang.kotlinlearn.databinding.LayoutItemMusicTitlePlaceholderBinding

/*********************************************************
 * Created by juliswang on 2022/9/20 15:18
 *
 * Description :
 *
 *
 *********************************************************/

class MusicLibraryPlaceHolderAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val DEFAULT_COUNT = 15
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            MusicItemHolder(
                LayoutItemMusicPlaceholderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            MusicTitleHolder(
                LayoutItemMusicTitlePlaceholderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    }

    override fun getItemCount(): Int {
        return DEFAULT_COUNT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //no-op
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position == 6) {
            6
        } else {
           0
        }
    }

    class MusicItemHolder(val binding: LayoutItemMusicPlaceholderBinding) : RecyclerView.ViewHolder(binding.root)

    class MusicTitleHolder(val binding: LayoutItemMusicTitlePlaceholderBinding) : RecyclerView.ViewHolder(binding.root)


}