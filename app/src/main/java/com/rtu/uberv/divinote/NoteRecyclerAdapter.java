package com.rtu.uberv.divinote;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rtu.uberv.divinote.models.Note;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder> {
    public static final String LOG_TAG = NoteRecyclerAdapter.class.getSimpleName();

    private List<Note> mNotes;
    private static ClickListener clickListener;

    public NoteRecyclerAdapter(List<Note> notes) {
        mNotes = notes;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        NoteViewHolder nvh = new NoteViewHolder(view);
        return nvh;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note note = mNotes.get(position);
        if (note.getRemindAt() == 0) {
            // TODO hide remind alarm
        }
        if (note.getTitle() == null) {

        }
        if (note.getContent() == null) {

        }
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public NoteViewHolder(View itemView) {
            super(itemView);

            // bind views
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

}
