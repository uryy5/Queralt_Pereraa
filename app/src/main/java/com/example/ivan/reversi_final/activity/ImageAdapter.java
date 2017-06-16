package com.example.ivan.reversi_final.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.example.ivan.reversi_final.R;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private Cell[] board;
	private boolean alternativeColours = false;
	boolean temps_finalitzat;
	private int GridSize;

	public ImageAdapter(Context c, Cell[] board, int GridSize, boolean altCol) {
		context = c;
		this.board = board;
		this.GridSize = GridSize;
		this.alternativeColours = altCol;
	}

	@Override
	public int getCount() {
		return board.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setAdjustViewBounds(true);
			imageView.setPadding(1, 1, 1, 1);
		} else {
			imageView = (ImageView) convertView;
		}

		if (board[position].isHinting()) {
			imageView.setImageResource(R.drawable.cell_move);
		} else {
			switch (board[position].getState()) {
			case 1:
				imageView.setImageResource(alternativeColours ? R.drawable.cell_blue : R.drawable.cell_black);
				break;

			case 2:
				imageView.setImageResource(alternativeColours ? R.drawable.cell_red : R.drawable.cell_white);
				break;

			default:
				imageView.setImageResource(R.drawable.cell_background);
				break;
			}
		}
		return imageView;
	}
}
