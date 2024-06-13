package com.example.resilience;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ConsejosAdapter extends RecyclerView.Adapter<ConsejosAdapter.ViewHolder> {
    private String[] consejos;

    public void setConsejos(String[] consejos) {
        this.consejos = consejos;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tips_session_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String consejo = consejos[position];
        // Divide el consejo en título y descripción
        String[] partes = consejo.split(":");
        if (partes.length >= 2) {
            holder.tvTipTitle.setText(partes[0].trim());
            holder.tvTipDescription.setText(partes[1].trim());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipTitle;
        TextView tvTipDescription;

        ViewHolder(View view) {
            super(view);
            tvTipTitle = view.findViewById(R.id.tvTipTitle);
            tvTipDescription = view.findViewById(R.id.tvTipDescription);
        }
    }

    @Override
    public int getItemCount() {
        return consejos != null ? consejos.length : 0;
    }


}

