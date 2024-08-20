package com.example.check.Adapter.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.check.R;
import com.example.check.model.home.recentLib.RecentLibrary;
import java.util.List;

public class RecentLibraryAdapter extends RecyclerView.Adapter<RecentLibraryAdapter.ViewHolder> {
    private List<RecentLibrary> libraries;

    public RecentLibraryAdapter(List<RecentLibrary> libraries) {
        this.libraries = libraries;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recent_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecentLibrary library = libraries.get(position);
        holder.libraryName.setText(library.getLibrary());
        holder.visitCount.setText("방문횟수: " + library.getVisitCount());
    }

    @Override
    public int getItemCount() {
        return libraries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView libraryName;
        TextView visitCount;

        public ViewHolder(View itemView) {
            super(itemView);
            libraryName = itemView.findViewById(R.id.library_name);
            visitCount = itemView.findViewById(R.id.visit_count);
        }
    }
}
