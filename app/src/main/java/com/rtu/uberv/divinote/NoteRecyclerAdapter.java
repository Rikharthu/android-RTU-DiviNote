package com.rtu.uberv.divinote;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rtu.uberv.divinote.models.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Locale.US;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder> {
    public static final String LOG_TAG = NoteRecyclerAdapter.class.getSimpleName();

    private List<Note> mNotes;
    private static ClickListener clickListener;
    private static OnNoteDoneClickListener noteDoneClickListener;
    private static OnNoteDeleteClickListener noteDeleteClickListener;
    private Context mContext;

    public NoteRecyclerAdapter(List<Note> notes, Context context) {
        mNotes = notes;
        mContext = context;
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
        if (note.getRemindAt() != 0) {
            // TODO format time
            Date date = new Date(note.getRemindAt());
            // extract format to string to use translations
            SimpleDateFormat dateFormat = new SimpleDateFormat(mContext.getString(R.string.note_remind_at_format), US);
            String dateString = dateFormat.format(date);
            holder.remindAtTv.setText(dateString);
        } else {
            holder.remindAtTv.setVisibility(View.GONE);
        }
        holder.titleTv.setText(note.getTitle());
        holder.contentTv.setText(note.getContent());

        Date date = new Date(note.getCreatedAt());
        // extract format to string to use translations
        SimpleDateFormat dateFormat = new SimpleDateFormat(mContext.getString(R.string.note_created_at_format), US);
        String dateString = dateFormat.format(date);
        holder.createdAtTv.setText(dateString);
        if (note.getRemindAt() > 0) {
            date = new Date(note.getRemindAt());
            dateString = dateFormat.format(date);
            holder.remindAtTv.setText(dateString);
            holder.remindAtHolder.setVisibility(View.VISIBLE);
        } else {
            holder.remindAtHolder.setVisibility(View.GONE);
        }
        holder.noteDoneChk.setChecked(note.isCompleted());

    }

    public void setData(List<Note> notes) {
        mNotes = notes;
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnNoteDoneClickListener(OnNoteDoneClickListener noteDoneClickListener) {
        this.noteDoneClickListener = noteDoneClickListener;
    }

    public void setOnNoteDeleteClickListener(OnNoteDeleteClickListener noteDeleteClickListener) {
        this.noteDeleteClickListener = noteDeleteClickListener;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public static final String TAG_NOTE_DONE = "tag_note_done";

        TextView contentTv;
        TextView titleTv;
        TextView remindAtTv;
        TextView createdAtTv;
        CheckBox noteDoneChk;
        View remindAtHolder;
        ImageButton deleteBtn;

        public NoteViewHolder(View itemView) {
            super(itemView);

            // bind views
            contentTv = (TextView) itemView.findViewById(R.id.note_item_content_tv);
            titleTv = (TextView) itemView.findViewById(R.id.note_item_title_tv);
            remindAtTv = (TextView) itemView.findViewById(R.id.note_item_remind_at_tv);
            createdAtTv = (TextView) itemView.findViewById(R.id.note_item_created_at_tv);
            noteDoneChk = (CheckBox) itemView.findViewById(R.id.note_item_completed_chk);
            remindAtHolder = itemView.findViewById(R.id.time_reminder);
            deleteBtn = (ImageButton) itemView.findViewById(R.id.note_item_delete_btn);
            deleteBtn.setOnClickListener(this);
            noteDoneChk.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // TODO refactor probably
            if (v == noteDoneChk && noteDoneClickListener != null) {
                noteDoneClickListener.onNoteDoneClick(getAdapterPosition(), v);
            } else if (v == deleteBtn && noteDeleteClickListener != null) {
                noteDeleteClickListener.onNoteDeleteClick(getAdapterPosition(),v);
            } else if (clickListener != null) {
                clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public interface OnNoteDoneClickListener {
        void onNoteDoneClick(int position, View v);
    }

    public interface OnNoteDeleteClickListener {
        void onNoteDeleteClick(int position, View v);
    }

}
