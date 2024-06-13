package com.example.resilience;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UnifiedSessionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UnifiedSession> sessions;

    public UnifiedSessionAdapter(List<UnifiedSession> sessions) {
        this.sessions = sessions;
    }

    @Override
    public int getItemViewType(int position) {
        return sessions.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == UnifiedSession.TYPE_MEDITATION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meditation_session_item, parent, false);
            return new MeditationViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.breathing_session_item, parent, false);
            return new BreathingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UnifiedSession session = sessions.get(position);
        if (holder instanceof MeditationViewHolder) {
            ((MeditationViewHolder) holder).bind(session.getMeditationSession());
        } else if (holder instanceof BreathingViewHolder) {
            ((BreathingViewHolder) holder).bind(session.getBreathingSession());
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class MeditationViewHolder extends RecyclerView.ViewHolder {
        TextView durationTextView;
        TextView completedTextView;
        TextView selectedTimeTextView; // Nuevo TextView para el tiempo seleccionado

        MeditationViewHolder(View itemView) {
            super(itemView);
            durationTextView = itemView.findViewById(R.id.durationTextView);
            completedTextView = itemView.findViewById(R.id.completedTextView);
            selectedTimeTextView = itemView.findViewById(R.id.selectedTimeTextView); // Inicializar el nuevo TextView
        }

        void bind(MeditationSession session) {
            durationTextView.setText("Duración: " + session.getDuration() + " min");
            completedTextView.setText("Completada: " + (session.getCompleted() == 1 ? "Sí" : "No"));
            selectedTimeTextView.setText("Tiempo Seleccionado: " + session.getSelectedTime()); // Establecer el tiempo seleccionado
        }
    }


    static class BreathingViewHolder extends RecyclerView.ViewHolder {
        TextView selectedTimeTextView;
        TextView completedTimeTextView;
        TextView exhaleCountTextView;
        TextView inhaleCountTextView;
        TextView completedTextView;

        BreathingViewHolder(View itemView) {
            super(itemView);
            selectedTimeTextView = itemView.findViewById(R.id.selectedTimeTextView);
            completedTimeTextView = itemView.findViewById(R.id.completedTimeTextView);
            exhaleCountTextView = itemView.findViewById(R.id.exhaleCountTextView);
            inhaleCountTextView = itemView.findViewById(R.id.inhaleCountTextView);
            completedTextView = itemView.findViewById(R.id.completedTextView);
        }

        void bind(BreathingSession session) {
            selectedTimeTextView.setText("Tiempo Seleccionado: " + session.getSelectedTime());
            completedTimeTextView.setText("Tiempo Completado: " + session.getCompletedTime());
            exhaleCountTextView.setText("Exhalaciones: " + session.getExhaleCount());
            inhaleCountTextView.setText("Inhalaciones: " + session.getInhaleCount());
            completedTextView.setText("Completada: " + (session.getCompleted() == 1 ? "Sí" : "No"));
        }
    }
}


