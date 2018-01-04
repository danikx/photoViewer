package photoViewer.com.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import photoViewer.com.R;
import photoViewer.com.model.Photo;

/**
 * Created by Daniyar Kalmurzin on 1/4/18.
 * email: daniyar.kalmurzin@gmail.com
 */

class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private ArrayList<Photo> data = new ArrayList<>();
    private Context ctx;


    PhotoAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(ctx).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override public void onBindViewHolder(PhotoViewHolder holder, int position) {
        final Photo photo = data.get(position);

        holder.fileName.setText(photo.fileName);
        holder.fileSize.setText(photo.fileSize);
        holder.fileHash.setText(photo.fileHash);

        Picasso.with(ctx)
                .load(photo.path)
                .resize(100, 100)
                .error(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.photo);
    }

    @Override public int getItemCount() {
        return data.size();
    }

    public void update(List<Photo> photos) {
        data.clear();
        data.addAll(photos);
        notifyDataSetChanged();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        private final TextView fileName;
        private final TextView fileSize;
        private final TextView fileHash;
        private final ImageView photo;

        PhotoViewHolder(View v) {
            super(v);

            photo = v.findViewById(R.id.imageView);
            fileName = v.findViewById(R.id.fileName);
            fileSize = v.findViewById(R.id.fileSize);
            fileHash = v.findViewById(R.id.fileHash);

        }
    }
}
