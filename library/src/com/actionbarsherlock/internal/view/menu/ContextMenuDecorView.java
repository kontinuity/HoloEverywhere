package com.actionbarsherlock.internal.view.menu;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.WazaBe.HoloEverywhere.app.Application;
import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;

public final class ContextMenuDecorView extends FrameLayout {
	private static final class InternalWrapper implements
			MenuPresenter.Callback, MenuBuilder.Callback {
		private final ContextMenuListener listener;
		private final String TAG = getClass().getSimpleName();

		public InternalWrapper(ContextMenuListener listener) {
			if (listener == null) {
				throw new IllegalArgumentException("Listener is null",
						new NullPointerException());
			}
			this.listener = listener;
			if (Application.isDebugMode()) {
				Log.v(TAG, "Create new InternalWrapper with listener: "
						+ listener);
			}
		}

		@Override
		public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
			if (Application.isDebugMode()) {
				Log.v(TAG, "Calling onContextMenuClosed on " + listener);
			}
			listener.onContextMenuClosed((ContextMenu) menu);
		}

		@Override
		public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
			if (Application.isDebugMode()) {
				Log.v(TAG, "Calling onContextItemSelected on " + listener);
			}
			return listener.onContextItemSelected(item);
		}

		@Override
		public void onMenuModeChange(MenuBuilder menu) {

		}

		@Override
		public boolean onOpenSubMenu(MenuBuilder subMenu) {
			return false;
		}

		public ContextMenuListener unwrap() {
			return listener;
		}
	}

	private ContextMenuBuilder contextMenu;

	private final InternalWrapper listener;
	private MenuDialogHelper menuDialogHelper;
	private final View view;

	public ContextMenuDecorView(Context context, View view,
			ContextMenuListener listener) {
		super(context);
		this.listener = new InternalWrapper(listener);
		if (view != null) {
			ViewParent parent = view.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				((ViewGroup) parent).removeView(view);
			}
			addView(view, android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		}
		this.view = view;
	}

	@Override
	public boolean showContextMenuForChild(View originalView) {
		if (contextMenu == null) {
			contextMenu = new ContextMenuBuilder(getContext(),
					listener.unwrap());
			contextMenu.setCallback(listener);
		} else {
			contextMenu.clearAll();
		}
		final MenuDialogHelper helper = contextMenu.show(originalView,
				originalView.getWindowToken());
		if (helper != null) {
			helper.setPresenterCallback(listener);
		}
		menuDialogHelper = helper;
		return menuDialogHelper != null;
	}

	public View unwrap() {
		return view;
	}
}